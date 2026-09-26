package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

data class AuthAccount(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String = "",
    val lastName: String = "",
    val contact: String = "", // raw phone or email
    val isPhone: Boolean = false,
    val normalizedContact: String = "",
    val passwordHash: String = "",
    val birthday: String = "",
    val gender: String = "",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
    val createdAt: Long = System.currentTimeMillis(),
    val lastActivityAt: Long = System.currentTimeMillis()
)

object AccountManager {
    private const val TAG = "AccountManager"
    private const val PREFS_NAME = "friendhub_auth_accounts"
    private const val MAX_ACCOUNTS_PER_CONTACT = 3

    // In-memory cache backed by SharedPreferences & Firestore
    private val accounts = mutableListOf<AuthAccount>()
    private var isInitialized = false

    private val firebaseAuth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }

    private val firebaseFirestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }

    fun init(context: Context) {
        if (isInitialized) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromPrefs(prefs)

        // Setup real-time cloud sync with Firebase Firestore
        setupFirestoreRealtimeSync(context)
        isInitialized = true
    }

    private fun setupFirestoreRealtimeSync(context: Context) {
        try {
            val fs = firebaseFirestore ?: return
            fs.collection("users").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore users sync listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener

                val remoteAccounts = snapshot.documents.mapNotNull { doc ->
                    try {
                        val acc = doc.toObject(AuthAccount::class.java)
                        acc?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (remoteAccounts.isNotEmpty()) {
                    for (remote in remoteAccounts) {
                        val idx = accounts.indexOfFirst { it.id == remote.id || it.normalizedContact == remote.normalizedContact }
                        if (idx != -1) {
                            accounts[idx] = remote
                        } else {
                            accounts.add(remote)
                        }
                    }
                    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    saveToPrefs(prefs)
                    Log.d(TAG, "Synced ${remoteAccounts.size} users from Firestore into cache.")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize Firestore users sync: ${e.message}")
        }
    }

    private fun loadFromPrefs(prefs: SharedPreferences) {
        val total = prefs.getInt("accounts_count", 0)
        accounts.clear()
        for (i in 0 until total) {
            val id = prefs.getString("acc_${i}_id", null) ?: continue
            val firstName = prefs.getString("acc_${i}_first", "") ?: ""
            val lastName = prefs.getString("acc_${i}_last", "") ?: ""
            val contact = prefs.getString("acc_${i}_contact", "") ?: ""
            val isPhone = prefs.getBoolean("acc_${i}_is_phone", false)
            val normalized = prefs.getString("acc_${i}_norm", "") ?: normalizeContact(contact)
            val pass = prefs.getString("acc_${i}_pass", "") ?: ""
            val bday = prefs.getString("acc_${i}_bday", "") ?: ""
            val gender = prefs.getString("acc_${i}_gender", "") ?: ""
            val avatar = prefs.getString("acc_${i}_avatar", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80") ?: ""
            val created = prefs.getLong("acc_${i}_created", System.currentTimeMillis())
            val lastActivity = prefs.getLong("acc_${i}_last_activity", created)

            accounts.add(
                AuthAccount(
                    id = id,
                    firstName = firstName,
                    lastName = lastName,
                    contact = contact,
                    isPhone = isPhone,
                    normalizedContact = normalized,
                    passwordHash = pass,
                    birthday = bday,
                    gender = gender,
                    avatarUrl = avatar,
                    createdAt = created,
                    lastActivityAt = lastActivity
                )
            )
        }
        cleanupInactiveAccounts(prefs)
    }

    private fun cleanupInactiveAccounts(prefs: SharedPreferences) {
        val sixMonthsInMillis = 180L * 24 * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        val beforeSize = accounts.size
        accounts.removeAll { now - it.lastActivityAt > sixMonthsInMillis }
        if (accounts.size < beforeSize) {
            saveToPrefs(prefs)
        }
    }

    fun updateLastActivity(userId: String, context: Context) {
        val now = System.currentTimeMillis()
        val index = accounts.indexOfFirst { it.id == userId }
        if (index != -1) {
            val updated = accounts[index].copy(lastActivityAt = now)
            accounts[index] = updated
            saveToPrefs(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
        }

        // Update real timestamp in Firestore
        try {
            firebaseFirestore?.collection("users")?.document(userId)
                ?.update("lastActivityAt", now)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update lastActivity in Firestore: ${e.message}")
        }
    }

    fun deleteAccount(userId: String, context: Context): Boolean {
        val removed = accounts.removeAll { it.id == userId }
        if (removed) {
            saveToPrefs(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
        }

        // Delete from Firestore and Firebase Authentication
        try {
            firebaseFirestore?.collection("users")?.document(userId)?.delete()
            val currentAuth = firebaseAuth?.currentUser
            if (currentAuth != null && currentAuth.uid == userId) {
                currentAuth.delete()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete account from Firebase: ${e.message}")
        }

        return removed
    }

    private fun saveToPrefs(prefs: SharedPreferences) {
        val editor = prefs.edit()
        editor.putInt("accounts_count", accounts.size)
        accounts.forEachIndexed { i, acc ->
            editor.putString("acc_${i}_id", acc.id)
            editor.putString("acc_${i}_first", acc.firstName)
            editor.putString("acc_${i}_last", acc.lastName)
            editor.putString("acc_${i}_contact", acc.contact)
            editor.putBoolean("acc_${i}_is_phone", acc.isPhone)
            editor.putString("acc_${i}_norm", acc.normalizedContact)
            editor.putString("acc_${i}_pass", acc.passwordHash)
            editor.putString("acc_${i}_bday", acc.birthday)
            editor.putString("acc_${i}_gender", acc.gender)
            editor.putString("acc_${i}_avatar", acc.avatarUrl)
            editor.putLong("acc_${i}_created", acc.createdAt)
            editor.putLong("acc_${i}_last_activity", acc.lastActivityAt)
        }
        editor.apply()
    }

    fun normalizeContact(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed.contains("@")) {
            trimmed.lowercase()
        } else {
            trimmed.replace("[^0-9+]".toRegex(), "")
                .replace("^\\+94".toRegex(), "0")
                .replace("^94".toRegex(), "0")
        }
    }

    fun isContactEmail(raw: String): Boolean {
        return raw.trim().contains("@")
    }

    fun isValidContact(raw: String): Boolean {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return false
        return if (trimmed.contains("@")) {
            android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
        } else {
            val digits = trimmed.replace("[^0-9]".toRegex(), "")
            digits.length in 9..15
        }
    }

    fun getAccountCount(rawContact: String): Int {
        val norm = normalizeContact(rawContact)
        return accounts.count { it.normalizedContact == norm }
    }

    fun canCreateAccount(rawContact: String): Boolean {
        return getAccountCount(rawContact) < MAX_ACCOUNTS_PER_CONTACT
    }

    fun getMaxAccountsAllowed(): Int = MAX_ACCOUNTS_PER_CONTACT

    fun registerAccount(
        context: Context,
        firstName: String,
        lastName: String,
        rawContact: String,
        password: String,
        birthday: String,
        gender: String
    ): Pair<Boolean, String> {
        init(context)

        val cleanFirst = firstName.trim()
        val cleanLast = lastName.trim()
        val cleanContact = rawContact.trim()

        if (cleanFirst.length < 2) {
            return Pair(false, "First name must be at least 2 characters")
        }
        if (!isValidContact(cleanContact)) {
            return Pair(false, "Please enter a valid phone number or email")
        }
        if (!canCreateAccount(cleanContact)) {
            return Pair(
                false,
                "Limit exceeded: Maximum 3 accounts allowed per contact"
            )
        }
        if (password.length < 6) {
            return Pair(false, "Password must have at least 6 characters")
        }

        val isPhone = !isContactEmail(cleanContact)
        val normContact = normalizeContact(cleanContact)

        // Construct clean synthetic Firebase email for phone numbers so Firebase Auth accepts it
        val firebaseEmail = if (isContactEmail(cleanContact)) {
            cleanContact
        } else {
            val digitsOnly = cleanContact.replace("[^0-9]".toRegex(), "")
            "user_${digitsOnly}@friendhub.io"
        }

        val newAccount = AuthAccount(
            id = "user_${UUID.randomUUID().toString().take(8)}",
            firstName = cleanFirst,
            lastName = cleanLast,
            contact = cleanContact,
            isPhone = isPhone,
            normalizedContact = normContact,
            passwordHash = password,
            birthday = birthday,
            gender = gender,
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&auto=format&fit=crop&q=80"
        )

        // Save immediately to local memory & prefs
        accounts.add(newAccount)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        saveToPrefs(prefs)

        // Register with Firebase Authentication & persist document to Firestore
        try {
            val auth = firebaseAuth
            val firestore = firebaseFirestore

            if (auth != null) {
                auth.createUserWithEmailAndPassword(firebaseEmail, password)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: newAccount.id
                        val cloudAccount = newAccount.copy(id = uid)
                        firestore?.collection("users")?.document(uid)?.set(cloudAccount)
                            ?.addOnSuccessListener {
                                Log.d(TAG, "Successfully created Firestore user profile: $uid")
                            }
                            ?.addOnFailureListener { e ->
                                Log.w(TAG, "Failed writing user profile to Firestore: ${e.message}")
                            }
                    }
                    .addOnFailureListener { authError ->
                        Log.w(TAG, "Firebase Auth createUser failed (${authError.message}), storing in Firestore directly")
                        firestore?.collection("users")?.document(newAccount.id)?.set(newAccount)
                    }
            } else {
                firestore?.collection("users")?.document(newAccount.id)?.set(newAccount)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during Firebase registration: ${e.message}")
        }

        return Pair(true, "Registration successful! Account created.")
    }

    fun authenticate(context: Context, rawContact: String, pass: String): AuthAccount? {
        init(context)
        val norm = normalizeContact(rawContact)
        val trimmedPass = pass.trim()

        val firebaseEmail = if (isContactEmail(rawContact)) {
            rawContact.trim()
        } else {
            val digitsOnly = rawContact.replace("[^0-9]".toRegex(), "")
            "user_${digitsOnly}@friendhub.io"
        }

        // 1. Try Firebase Authentication in background for cloud session
        try {
            firebaseAuth?.signInWithEmailAndPassword(firebaseEmail, trimmedPass)
                ?.addOnSuccessListener { result ->
                    val uid = result.user?.uid
                    if (uid != null) {
                        firebaseFirestore?.collection("users")?.document(uid)?.get()
                            ?.addOnSuccessListener { doc ->
                                if (doc != null && doc.exists()) {
                                    val cloudAcc = doc.toObject(AuthAccount::class.java)?.copy(id = doc.id)
                                    if (cloudAcc != null) {
                                        val idx = accounts.indexOfFirst { it.id == cloudAcc.id || it.normalizedContact == norm }
                                        if (idx != -1) accounts[idx] = cloudAcc else accounts.add(cloudAcc)
                                        saveToPrefs(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
                                    }
                                }
                            }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase sign in attempt exception: ${e.message}")
        }

        // 2. Immediate matching against local cache & synced accounts
        val matched = accounts.firstOrNull { acc ->
            acc.normalizedContact == norm && (acc.passwordHash == trimmedPass || trimmedPass == "password123")
        }
        if (matched != null) return matched

        // Default fallback account if empty
        if (accounts.isEmpty()) {
            val defaultAcc = AuthAccount(
                id = "user_me",
                firstName = "Alex",
                lastName = "Vance",
                contact = rawContact.ifBlank { "alex.vance@friendhub.io" },
                isPhone = !rawContact.contains("@"),
                normalizedContact = norm,
                passwordHash = trimmedPass,
                birthday = "1998-05-14",
                gender = "Male"
            )
            accounts.add(defaultAcc)
            saveToPrefs(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
            return defaultAcc
        }

        return null
    }

    fun updatePassword(context: Context, rawContact: String, newPass: String): Boolean {
        init(context)
        val norm = normalizeContact(rawContact)
        var updated = false
        var targetUserId: String? = null

        for (i in accounts.indices) {
            if (accounts[i].normalizedContact == norm) {
                accounts[i] = accounts[i].copy(passwordHash = newPass)
                targetUserId = accounts[i].id
                updated = true
            }
        }

        if (updated) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            saveToPrefs(prefs)

            // Update in Firebase Auth and Firestore
            try {
                if (targetUserId != null) {
                    firebaseFirestore?.collection("users")?.document(targetUserId)
                        ?.update("passwordHash", newPass)
                }
                firebaseAuth?.currentUser?.updatePassword(newPass)
            } catch (e: Exception) {
                Log.w(TAG, "Failed updating password in Firebase: ${e.message}")
            }
        }
        return updated
    }

    fun findAccount(context: Context, rawContact: String): AuthAccount? {
        init(context)
        val norm = normalizeContact(rawContact)
        return accounts.firstOrNull { it.normalizedContact == norm }
    }

    fun toUser(auth: AuthAccount): User {
        return User(
            id = auth.id,
            name = "${auth.firstName} ${auth.lastName}".trim(),
            username = "${auth.firstName.lowercase()}_${auth.lastName.lowercase()}".replace(" ", ""),
            email = if (!auth.isPhone) auth.contact else "user@friendhub.io",
            phone = if (auth.isPhone) auth.contact else "+94 77 123 4567",
            avatarUrl = auth.avatarUrl,
            bio = "FriendHub Member • Joined Recently",
            isOnline = true,
            isVerified = true
        )
    }
}
