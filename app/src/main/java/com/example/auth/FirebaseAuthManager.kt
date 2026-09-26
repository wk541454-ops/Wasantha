package com.example.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class LinkedProvidersInfo(
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val phone: String? = null,
    val hasEmail: Boolean = false,
    val hasPhone: Boolean = false
) {
    val canUnlinkEmail: Boolean get() = hasEmail && hasPhone
    val canUnlinkPhone: Boolean get() = hasPhone && hasEmail
}

object FirebaseAuthManager {

    private const val TAG = "FirebaseAuthManager"

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (t: Throwable) {
            Log.w(TAG, "FirebaseAuth instance unavailable: ${t.message}")
            null
        }

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (t: Throwable) {
            Log.w(TAG, "FirebaseFirestore instance unavailable: ${t.message}")
            null
        }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _linkedProviders = MutableStateFlow(LinkedProvidersInfo())
    val linkedProviders: StateFlow<LinkedProvidersInfo> = _linkedProviders.asStateFlow()

    // --- Phone OTP Rate Limiting & Expiry Controls ---
    private var lastOtpSentTimestamp: Long = 0L
    private var resendCount: Int = 0
    private var failedVerificationAttempts: Int = 0
    private var activeVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var activePhoneNumber: String? = null

    const val OTP_COOLDOWN_SECONDS = 60
    const val OTP_EXPIRY_SECONDS = 120
    const val MAX_RESEND_ATTEMPTS = 3
    const val MAX_VERIFICATION_ATTEMPTS = 5

    init {
        try {
            val user = auth?.currentUser
            _currentUser.value = user
            _isUserLoggedIn.value = user != null
            updateLinkedProviders(user)

            auth?.addAuthStateListener { firebaseAuth ->
                try {
                    val u = firebaseAuth.currentUser
                    _currentUser.value = u
                    _isUserLoggedIn.value = u != null
                    updateLinkedProviders(u)
                    Log.d(TAG, "AuthState changed: uid=${u?.uid}, loggedIn=${u != null}")
                } catch (t: Throwable) {
                    Log.w(TAG, "AuthState listener error: ${t.message}")
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Initialization failed: ${t.message}")
        }
    }

    fun init(context: Context) {
        try {
            val user = auth?.currentUser
            _currentUser.value = user
            _isUserLoggedIn.value = user != null
            updateLinkedProviders(user)
        } catch (t: Throwable) {
            Log.e(TAG, "init(context) error: ${t.message}")
        }
    }

    fun isLoggedIn(): Boolean {
        return auth?.currentUser != null
    }

    fun updateLinkedProviders(user: FirebaseUser?) {
        if (user == null) {
            _linkedProviders.value = LinkedProvidersInfo()
            return
        }

        var foundEmail: String? = user.email
        var foundPhone: String? = user.phoneNumber
        var hasEmail = false
        var hasPhone = false

        for (profile in user.providerData) {
            when (profile.providerId) {
                EmailAuthProvider.PROVIDER_ID -> {
                    hasEmail = true
                    if (foundEmail == null) foundEmail = profile.email
                }
                PhoneAuthProvider.PROVIDER_ID -> {
                    hasPhone = true
                    if (foundPhone == null) foundPhone = profile.phoneNumber
                }
            }
        }

        _linkedProviders.value = LinkedProvidersInfo(
            email = foundEmail,
            isEmailVerified = user.isEmailVerified,
            phone = foundPhone,
            hasEmail = hasEmail,
            hasPhone = hasPhone
        )
    }

    // -------------------------------------------------------------
    // 1. PHONE AUTHENTICATION (SEND OTP / VERIFY OTP)
    // -------------------------------------------------------------

    fun getRemainingCooldownSeconds(): Int {
        val elapsed = (System.currentTimeMillis() - lastOtpSentTimestamp) / 1000
        val remaining = (OTP_COOLDOWN_SECONDS - elapsed).toInt()
        return if (remaining > 0) remaining else 0
    }

    fun getRemainingExpirySeconds(): Int {
        val elapsed = (System.currentTimeMillis() - lastOtpSentTimestamp) / 1000
        val remaining = (OTP_EXPIRY_SECONDS - elapsed).toInt()
        return if (remaining > 0) remaining else 0
    }

    fun isOtpExpired(): Boolean {
        if (lastOtpSentTimestamp == 0L) return true
        val elapsed = (System.currentTimeMillis() - lastOtpSentTimestamp) / 1000
        return elapsed > OTP_EXPIRY_SECONDS
    }

    fun getRemainingAttempts(): Int {
        return (MAX_VERIFICATION_ATTEMPTS - failedVerificationAttempts).coerceAtLeast(0)
    }

    /**
     * Send Phone OTP with international format, rate limiting, and cooldown validation.
     */
    fun sendPhoneOtp(
        activity: Activity,
        rawPhoneNumber: String,
        isResend: Boolean = false,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val formattedNumber = PhoneAuthHelper.formatToE164(rawPhoneNumber)

        if (!PhoneAuthHelper.isValidPhoneNumber(formattedNumber)) {
            onError("Please enter a valid phone number (e.g. +94 77 123 4567).")
            return
        }

        val cooldown = getRemainingCooldownSeconds()
        if (cooldown > 0 && isResend) {
            onError("Please wait $cooldown seconds before requesting another verification code.")
            return
        }

        if (isResend && resendCount >= MAX_RESEND_ATTEMPTS) {
            onError("Maximum OTP resend limit reached. For security, please wait 10 minutes or use email.")
            return
        }

        activePhoneNumber = formattedNumber
        failedVerificationAttempts = 0

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "Instant auto-verification completed for $formattedNumber")
                // Automatic instant SMS verification on supported devices
                val smsCode = credential.smsCode
                if (!smsCode.isNullOrEmpty()) {
                    // Auto-retrieved SMS
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "Phone verification failed: ${e.message}", e)
                onError(mapFirebaseError(e))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "OTP code dispatched to $formattedNumber, verificationId=$verificationId")
                activeVerificationId = verificationId
                resendToken = token
                lastOtpSentTimestamp = System.currentTimeMillis()
                if (isResend) resendCount++
                onCodeSent(verificationId)
            }
        }

        try {
            val firebaseAuthInstance = auth ?: run { onError("Firebase is not initialized"); return }
            val builder = PhoneAuthOptions.newBuilder(firebaseAuthInstance)
                .setPhoneNumber(formattedNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)

            if (isResend && resendToken != null) {
                builder.setForceResendingToken(resendToken!!)
            }

            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Exception in verifyPhoneNumber: ${e.message}")
            onError("Failed to initiate SMS verification: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    /**
     * Verifies the 6-digit OTP code against Firebase PhoneAuthProvider.
     */
    fun verifyPhoneOtp(
        verificationId: String,
        otpCode: String,
        onSuccess: (user: FirebaseUser, isNewUser: Boolean) -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val trimmedCode = otpCode.trim()

        if (trimmedCode.length != 6 || !trimmedCode.all { it.isDigit() }) {
            onError("Please enter a valid 6-digit verification code.")
            return
        }

        if (isOtpExpired()) {
            onError("This verification code has expired. Please tap 'Resend Code' to receive a new one.")
            return
        }

        if (failedVerificationAttempts >= MAX_VERIFICATION_ATTEMPTS) {
            onError("Too many incorrect attempts. This code is now invalid. Please request a new code.")
            return
        }

        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, trimmedCode)
            val firebaseAuth = auth
            if (firebaseAuth == null) {
                onError("Firebase Auth is not available.")
                return
            }
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    val isNewUser = authResult.additionalUserInfo?.isNewUser == true
                    failedVerificationAttempts = 0
                    if (user != null) {
                        syncUserToFirestore(user, activePhoneNumber)
                        onSuccess(user, isNewUser)
                    } else {
                        onError("Authentication succeeded but failed to retrieve user profile.")
                    }
                }
                .addOnFailureListener { e ->
                    failedVerificationAttempts++
                    val remaining = getRemainingAttempts()
                    Log.e(TAG, "OTP verification failed (attempts remaining: $remaining): ${e.message}")
                    if (remaining > 0) {
                        onError("Incorrect verification code. $remaining attempt(s) remaining.")
                    } else {
                        onError("Incorrect verification code. Maximum attempts reached. Please request a new code.")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during OTP verification: ${e.message}")
            onError(mapFirebaseError(e))
        }
    }

    // -------------------------------------------------------------
    // 2. EMAIL AUTHENTICATION (REGISTER / LOGIN / FORGOT PASSWORD)
    // -------------------------------------------------------------

    fun registerWithEmail(
        email: String,
        pass: String,
        displayName: String,
        onSuccess: (user: FirebaseUser) -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()
        val cleanName = displayName.trim()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onError("Please enter a valid email address.")
            return
        }
        if (cleanPass.length < 6) {
            onError("Password must be at least 6 characters long.")
            return
        }

        val a = auth ?: run { onError("Firebase is not initialized"); return }
        a.createUserWithEmailAndPassword(cleanEmail, cleanPass)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    // Send Email Verification Link
                    user.sendEmailVerification()
                        .addOnSuccessListener {
                            Log.d(TAG, "Verification email sent to $cleanEmail")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Failed sending verification email: ${e.message}")
                        }

                    syncUserToFirestore(user, null, cleanName, cleanEmail)
                    onSuccess(user)
                } else {
                    onError("Failed to create account profile.")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Email registration failed: ${e.message}")
                onError(mapFirebaseError(e))
            }
    }

    fun loginWithEmail(
        email: String,
        pass: String,
        onSuccess: (user: FirebaseUser, isEmailVerified: Boolean) -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        if (cleanEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onError("Please enter a valid email address.")
            return
        }
        if (cleanPass.isEmpty()) {
            onError("Please enter your password.")
            return
        }

        val a = auth ?: run { onError("Firebase is not initialized"); return }
        a.signInWithEmailAndPassword(cleanEmail, cleanPass)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    onSuccess(user, user.isEmailVerified)
                } else {
                    onError("Failed to retrieve user profile.")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Email login failed: ${e.message}")
                onError(mapFirebaseError(e))
            }
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onError("Please enter a valid email address.")
            return
        }

        val a = auth ?: run { onError("Firebase is not initialized"); return }
        a.sendPasswordResetEmail(cleanEmail)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Password reset failed: ${e.message}")
                // For privacy, do not reveal if an account exists unless network error
                if (e is FirebaseNetworkException) {
                    onError(mapFirebaseError(e))
                } else {
                    // Generic safe response
                    onSuccess()
                }
            }
    }

    fun sendEmailVerification(
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("No active user session found.")
            return
        }

        user.sendEmailVerification()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(mapFirebaseError(e))
            }
    }

    // -------------------------------------------------------------
    // 3. ACCOUNT LINKING & CREDENTIAL MANAGEMENT
    // -------------------------------------------------------------

    /**
     * Link Phone number to an existing Email or Phone account.
     */
    fun linkPhoneCredential(
        verificationId: String,
        otpCode: String,
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("Please log in before linking credentials.")
            return
        }

        val trimmedCode = otpCode.trim()
        if (trimmedCode.length != 6) {
            onError("Please enter a valid 6-digit verification code.")
            return
        }

        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, trimmedCode)
            user.linkWithCredential(credential)
                .addOnSuccessListener { linkResult ->
                    val updatedUser = linkResult.user
                    updateLinkedProviders(updatedUser)
                    if (updatedUser != null) {
                        firestore?.collection("users")?.document(updatedUser.uid)
                            ?.update(
                                mapOf(
                                    "phone" to (updatedUser.phoneNumber ?: activePhoneNumber),
                                    "linkedPhone" to (updatedUser.phoneNumber ?: activePhoneNumber)
                                )
                            )
                    }
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Link phone failed: ${e.message}")
                    onError(mapFirebaseError(e))
                }
        } catch (e: Exception) {
            onError(mapFirebaseError(e))
        }
    }

    /**
     * Link Email and Password to an existing Phone account.
     */
    fun linkEmailCredential(
        email: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("Please log in before linking credentials.")
            return
        }

        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onError("Please enter a valid email address.")
            return
        }
        if (cleanPass.length < 6) {
            onError("Password must be at least 6 characters.")
            return
        }

        val credential = EmailAuthProvider.getCredential(cleanEmail, cleanPass)
        user.linkWithCredential(credential)
            .addOnSuccessListener { linkResult ->
                val updatedUser = linkResult.user
                updateLinkedProviders(updatedUser)
                updatedUser?.sendEmailVerification()
                if (updatedUser != null) {
                    firestore?.collection("users")?.document(updatedUser.uid)
                        ?.update(
                            mapOf(
                                "email" to cleanEmail,
                                "linkedEmail" to cleanEmail
                            )
                        )
                }
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Link email failed: ${e.message}")
                onError(mapFirebaseError(e))
            }
    }

    /**
     * Unlink Phone number from account (only allowed if Email is also linked).
     */
    fun unlinkPhone(
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("No active user session.")
            return
        }

        if (!_linkedProviders.value.canUnlinkPhone) {
            onError("Cannot remove phone number. You must keep at least one authentication method linked to prevent losing account access.")
            return
        }

        user.unlink(PhoneAuthProvider.PROVIDER_ID)
            .addOnSuccessListener { authResult ->
                val updatedUser = authResult.user
                updateLinkedProviders(updatedUser)
                if (updatedUser != null) {
                    firestore?.collection("users")?.document(updatedUser.uid)
                        ?.update(mapOf("linkedPhone" to null))
                }
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(mapFirebaseError(e))
            }
    }

    /**
     * Unlink Email from account (only allowed if Phone is also linked).
     */
    fun unlinkEmail(
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("No active user session.")
            return
        }

        if (!_linkedProviders.value.canUnlinkEmail) {
            onError("Cannot remove email address. You must keep at least one authentication method linked to prevent losing account access.")
            return
        }

        user.unlink(EmailAuthProvider.PROVIDER_ID)
            .addOnSuccessListener { authResult ->
                val updatedUser = authResult.user
                updateLinkedProviders(updatedUser)
                if (updatedUser != null) {
                    firestore?.collection("users")?.document(updatedUser.uid)
                        ?.update(mapOf("linkedEmail" to null))
                }
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(mapFirebaseError(e))
            }
    }

    /**
     * Change Email with verification requirement.
     */
    fun changeEmail(
        newEmail: String,
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("No active user session.")
            return
        }
        val cleanEmail = newEmail.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onError("Please enter a valid email address.")
            return
        }

        user.verifyBeforeUpdateEmail(cleanEmail)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(mapFirebaseError(e))
            }
    }

    /**
     * Change Password.
     */
    fun changePassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("No active user session.")
            return
        }
        if (newPassword.length < 6) {
            onError("Password must be at least 6 characters.")
            return
        }

        user.updatePassword(newPassword)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(mapFirebaseError(e))
            }
    }

    // -------------------------------------------------------------
    // 4. SESSION & ACCOUNT DELETION
    // -------------------------------------------------------------

    fun logout(onComplete: () -> Unit = {}) {
        try {
            auth?.signOut()
            _currentUser.value = null
            _isUserLoggedIn.value = false
            _linkedProviders.value = LinkedProvidersInfo()
            lastOtpSentTimestamp = 0L
            resendCount = 0
            failedVerificationAttempts = 0
            activeVerificationId = null
            resendToken = null
            activePhoneNumber = null
            Log.d(TAG, "Logged out completely from Firebase Auth.")
            onComplete()
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout: ${e.message}")
            onComplete()
        }
    }

    fun deleteAccount(
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) {
            onError("No active user session found.")
            return
        }
        val uid = user.uid

        // 1. Delete Firestore user document
        firestore?.collection("users")?.document(uid)?.delete()
            ?.addOnCompleteListener {
                // 2. Delete Firebase Auth account
                user.delete()
                    .addOnSuccessListener {
                        logout { onSuccess() }
                    }
                    .addOnFailureListener { e ->
                        onError(mapFirebaseError(e))
                    }
            }
    }

    // -------------------------------------------------------------
    // 5. FIRESTORE USER SYNCHRONIZATION
    // -------------------------------------------------------------

    fun syncUserProfileToFirestore(
        firebaseUser: FirebaseUser,
        phone: String? = null,
        name: String? = null,
        email: String? = null
    ) {
        val fs = firestore ?: return
        val uid = firebaseUser.uid
        val docRef = fs.collection("users").document(uid)

        docRef.get().addOnSuccessListener { snapshot ->
            val existing = if (snapshot != null && snapshot.exists()) snapshot.toObject(User::class.java) else null
            val assignedName = when {
                !name.isNullOrBlank() -> name
                !firebaseUser.displayName.isNullOrBlank() -> firebaseUser.displayName!!
                existing != null && existing.name.isNotBlank() -> existing.name
                !firebaseUser.email.isNullOrBlank() -> firebaseUser.email!!.substringBefore("@").replace(".", " ").capitalize()
                !firebaseUser.phoneNumber.isNullOrBlank() -> "User ${firebaseUser.phoneNumber!!.takeLast(4)}"
                else -> "FriendHub Member"
            }

            val finalUser = (existing ?: User(id = uid)).copy(
                id = uid,
                name = assignedName,
                email = email ?: firebaseUser.email ?: existing?.email ?: "",
                phone = phone ?: firebaseUser.phoneNumber ?: existing?.phone ?: "",
                isOnline = true
            )

            docRef.set(finalUser).addOnSuccessListener {
                Log.d(TAG, "Firestore user profile synced successfully: $uid")
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Failed reading existing user document during sync: ${e.message}")
        }
    }

    fun syncUserToFirestore(
        firebaseUser: FirebaseUser,
        phone: String? = null,
        name: String? = null,
        email: String? = null
    ) {
        syncUserProfileToFirestore(firebaseUser, phone, name, email)
    }

    fun syncUserProfileToFirestore(
        userId: String,
        displayName: String? = null,
        email: String? = null,
        phone: String? = null
    ) {
        val fs = firestore ?: return
        val docRef = fs.collection("users").document(userId)
        val data = mutableMapOf<String, Any>(
            "id" to userId,
            "name" to (displayName ?: "FriendHub Member"),
            "isOnline" to true
        )
        if (!email.isNullOrBlank()) data["email"] = email
        if (!phone.isNullOrBlank()) data["phone"] = phone
        docRef.set(data, com.google.firebase.firestore.SetOptions.merge())
    }

    // -------------------------------------------------------------
    // 6. ERROR MAPPING UTILITY
    // -------------------------------------------------------------

    fun mapFirebaseError(e: Throwable): String {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                when {
                    e.errorCode == "ERROR_INVALID_VERIFICATION_CODE" -> "Incorrect verification code. Please check and try again."
                    e.errorCode == "ERROR_SESSION_EXPIRED" -> "Verification code expired. Please request a new code."
                    e.errorCode == "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again or tap Forgot Password."
                    e.errorCode == "ERROR_INVALID_EMAIL" -> "The email address is improperly formatted."
                    else -> "Invalid credentials. Please verify your input and try again."
                }
            }
            is FirebaseAuthInvalidUserException -> {
                "Account not found or has been disabled. Please check your credentials or create a new account."
            }
            is FirebaseAuthUserCollisionException -> {
                "This phone number or email is already linked to another FriendHub account."
            }
            is FirebaseAuthWeakPasswordException -> {
                "The password is too weak. Please choose a password with at least 6 characters."
            }
            is FirebaseTooManyRequestsException -> {
                "Too many attempts. You have been temporarily blocked for security reasons. Please try again later."
            }
            is FirebaseNetworkException -> {
                "Network connection error. Please check your internet connection and try again."
            }
            else -> {
                val msg = e.localizedMessage ?: "An unexpected authentication error occurred."
                if (msg.contains("TOO_LONG", ignoreCase = true) || msg.contains("TOO_SHORT", ignoreCase = true)) {
                    "Invalid phone number length."
                } else if (msg.contains("reCAPTCHA", ignoreCase = true) || msg.contains("SafetyNet", ignoreCase = true) || msg.contains("Play Integrity", ignoreCase = true)) {
                    "SMS verification requires Play Integrity or reCAPTCHA verification on physical device."
                } else {
                    msg
                }
            }
        }
    }
}
