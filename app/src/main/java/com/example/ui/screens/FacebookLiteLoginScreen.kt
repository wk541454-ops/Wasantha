package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.auth.FirebaseAuthManager
import com.example.auth.PhoneAuthHelper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.User
import com.example.repository.AccountManager
import com.example.repository.AuthAccount
import kotlinx.coroutines.delay
import kotlin.random.Random

// Enum to manage screens within Facebook Lite Login
enum class FbLoginStep {
    MAIN_LOGIN,
    LOGIN_OTP_VERIFICATION,
    FORGOT_PASSWORD_FIND,
    FORGOT_PASSWORD_CHOOSE_METHOD,
    FORGOT_PASSWORD_OTP,
    FORGOT_PASSWORD_NEW_PASS,
    CREATE_ACCOUNT_NO_OTP
}

@Composable
fun FacebookLiteLoginScreen(
    currentUser: User,
    onLoginSuccessWithUser: (User) -> Unit = {},
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var currentStep by remember { mutableStateOf(FbLoginStep.MAIN_LOGIN) }

    LaunchedEffect(Unit) {
        AccountManager.init(context)
        FirebaseAuthManager.init(context)
    }

    // Main Login States - Dual tabs: 0 = Phone Number, 1 = Email Address
    var selectedLoginTab by remember { mutableIntStateOf(0) }
    var phoneInput by remember { mutableStateOf(currentUser.phone.ifBlank { "+94 77 123 4567" }) }
    var emailInput by remember { mutableStateOf(currentUser.email.ifBlank { "alex.vance@friendhub.io" }) }
    var password by remember { mutableStateOf("password123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("සිංහල") }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }
    var unverifiedEmailWarning by remember { mutableStateOf(false) }
    var authenticatedAccount by remember { mutableStateOf<AuthAccount?>(null) }
    var activeContactForOtp by remember { mutableStateOf(currentUser.phone) }

    // Phone Auth & 2FA OTP States
    var phoneVerificationId by remember { mutableStateOf<String?>(null) }
    var loginOtpCode by remember { mutableStateOf("") }
    var otpExpirySeconds by remember { mutableIntStateOf(120) }
    var resendCooldownSeconds by remember { mutableIntStateOf(0) }
    var otpAttemptsRemaining by remember { mutableIntStateOf(5) }
    var isOtpSending by remember { mutableStateOf(false) }
    var isOtpVerifying by remember { mutableStateOf(false) }
    var trustDevice by remember { mutableStateOf(true) }

    // Forgot Password States
    var forgotSearchQuery by remember { mutableStateOf(currentUser.email) }
    var forgotSelectedMethod by remember { mutableStateOf("EMAIL") }
    var forgotOtpCode by remember { mutableStateOf("") }
    var isSendingResetEmail by remember { mutableStateOf(false) }
    var forgotResetNotice by remember { mutableStateOf<String?>(null) }
    var forgotNewPassword by remember { mutableStateOf("") }
    var forgotConfirmPassword by remember { mutableStateOf("") }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var logoutOtherDevices by remember { mutableStateOf(true) }

    // Timers for OTP Expiry and Resend Cooldown
    LaunchedEffect(currentStep, resendCooldownSeconds) {
        if (currentStep == FbLoginStep.LOGIN_OTP_VERIFICATION && resendCooldownSeconds > 0) {
            delay(1000L)
            resendCooldownSeconds--
        }
    }

    LaunchedEffect(currentStep, otpExpirySeconds) {
        if (currentStep == FbLoginStep.LOGIN_OTP_VERIFICATION && otpExpirySeconds > 0) {
            delay(1000L)
            otpExpirySeconds--
        }
    }

    // Function to trigger real Phone OTP via FirebaseAuthManager
    fun startPhoneLoginOtp(phone: String, isResend: Boolean = false) {
        val formatted = PhoneAuthHelper.formatToE164(phone)
        if (!PhoneAuthHelper.isValidPhoneNumber(formatted)) {
            loginErrorMessage = when (selectedLanguage) {
                "English" -> "Please enter a valid phone number (e.g. +94771234567 or 0771234567)"
                "தமிழ்" -> "சரியான தொலைபேசி எண்ணை உள்ளிடவும் (+94...)"
                else -> "කරුණාකර නිවැරදි දුරකථන අංකයක් ඇතුළත් කරන්න (උදා: +94771234567 හෝ 0771234567)"
            }
            return
        }

        if (activity == null) {
            loginErrorMessage = "Device activity context required for SMS verification."
            return
        }

        isOtpSending = true
        loginErrorMessage = null

        FirebaseAuthManager.sendPhoneOtp(
            activity = activity,
            rawPhoneNumber = formatted,
            isResend = isResend,
            onCodeSent = { vid ->
                isOtpSending = false
                phoneVerificationId = vid
                activeContactForOtp = formatted
                loginOtpCode = ""
                resendCooldownSeconds = 60
                otpExpirySeconds = 120
                otpAttemptsRemaining = 5
                currentStep = FbLoginStep.LOGIN_OTP_VERIFICATION
                Toast.makeText(
                    context,
                    when (selectedLanguage) {
                        "English" -> "SMS verification code sent to $formatted"
                        "தமிழ்" -> "$formatted என்ற எண்ணுக்கு SMS குறியீடு அனுப்பப்பட்டது"
                        else -> "$formatted අංකයට SMS කේතය සාර්ථකව යවන ලදී"
                    },
                    Toast.LENGTH_SHORT
                ).show()
            },
            onError = { err ->
                isOtpSending = false
                loginErrorMessage = err
                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            }
        )
    }

    // Function to verify Phone OTP
    fun verifyPhoneLoginOtp() {
        val vid = phoneVerificationId
        if (vid == null) {
            loginErrorMessage = "Verification session expired. Please request a new SMS code."
            return
        }
        if (loginOtpCode.length != 6) {
            loginErrorMessage = "Please enter all 6 digits of the SMS code."
            return
        }
        if (otpExpirySeconds <= 0) {
            loginErrorMessage = "SMS verification code has expired. Please tap Resend Code."
            return
        }

        isOtpVerifying = true
        loginErrorMessage = null

        FirebaseAuthManager.verifyPhoneOtp(
            verificationId = vid,
            otpCode = loginOtpCode,
            onSuccess = { fbUser, _ ->
                isOtpVerifying = false
                Toast.makeText(
                    context,
                    when (selectedLanguage) {
                        "English" -> "Phone verified! Logged in."
                        "தமிழ்" -> "தொலைபேசி எண் சரிபார்க்கப்பட்டது! உள்நுழைந்தது."
                        else -> "දුරකථන අංකය තහවුරු විය! පිවිසුණා."
                    },
                    Toast.LENGTH_SHORT
                ).show()

                val loggedUser = User(
                    id = fbUser.uid,
                    name = fbUser.displayName?.ifBlank { null } ?: "FriendHub User",
                    phone = fbUser.phoneNumber ?: activeContactForOtp,
                    isOnline = true
                )
                onLoginSuccessWithUser(loggedUser)
                onLoginSuccess()
            },
            onError = { err ->
                isOtpVerifying = false
                otpAttemptsRemaining = FirebaseAuthManager.getRemainingAttempts()
                loginErrorMessage = err
                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            }
        )
    }

    // Function to Login with Email & Password
    fun loginWithEmail() {
        if (emailInput.isBlank() || password.isBlank()) {
            loginErrorMessage = when (selectedLanguage) {
                "English" -> "Please enter your email address and password."
                "தமிழ்" -> "மின்னஞ்சல் மற்றும் கடவுச்சொல்லை உள்ளிடவும்."
                else -> "කරුණාකර ඊමේල් ලිපිනය සහ මුරපදය ඇතුළත් කරන්න."
            }
            Toast.makeText(context, loginErrorMessage, Toast.LENGTH_SHORT).show()
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.trim()).matches()) {
            loginErrorMessage = when (selectedLanguage) {
                "English" -> "Please enter a valid email address."
                "தமிழ்" -> "சரியான மின்னஞ்சலை உள்ளிடவும்."
                else -> "කරුණාකර නිවැරදි ඊමේල් ලිපිනයක් ඇතුළත් කරන්න."
            }
            Toast.makeText(context, loginErrorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        loginErrorMessage = null
        FirebaseAuthManager.loginWithEmail(
            email = emailInput.trim(),
            pass = password,
            onSuccess = { fbUser, isEmailVerified ->
                unverifiedEmailWarning = !isEmailVerified
                val loggedUser = User(
                    id = fbUser.uid,
                    name = fbUser.displayName?.ifBlank { null }
                        ?: fbUser.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
                        ?: "FriendHub User",
                    email = fbUser.email ?: emailInput.trim(),
                    isOnline = true
                )
                Toast.makeText(
                    context,
                    when (selectedLanguage) {
                        "English" -> "Welcome back, ${loggedUser.name}!"
                        "தமிழ்" -> "நல்வரவு, ${loggedUser.name}!"
                        else -> "සාදරයෙන් පිළිගනිමු, ${loggedUser.name}!"
                    },
                    Toast.LENGTH_SHORT
                ).show()
                onLoginSuccessWithUser(loggedUser)
                onLoginSuccess()
            },
            onError = { err ->
                // Check if account exists locally in AccountManager as fallback
                val localAcc = AccountManager.authenticate(context, emailInput.trim(), password)
                if (localAcc != null) {
                    val userToLogin = AccountManager.toUser(localAcc)
                    onLoginSuccessWithUser(userToLogin)
                    onLoginSuccess()
                } else {
                    loginErrorMessage = err
                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF18191A))
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "FbLoginTransition"
        ) { step ->
            when (step) {
                FbLoginStep.MAIN_LOGIN -> {
                    MainLoginView(
                        currentUser = currentUser,
                        selectedLoginTab = selectedLoginTab,
                        onTabChange = { selectedLoginTab = it; loginErrorMessage = null },
                        phoneInput = phoneInput,
                        onPhoneChange = { phoneInput = it; loginErrorMessage = null },
                        emailInput = emailInput,
                        onEmailChange = { emailInput = it; loginErrorMessage = null },
                        password = password,
                        onPasswordChange = { password = it; loginErrorMessage = null },
                        isPasswordVisible = isPasswordVisible,
                        onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                        selectedLanguage = selectedLanguage,
                        onLanguageSelected = { selectedLanguage = it },
                        loginErrorMessage = loginErrorMessage,
                        unverifiedEmailWarning = unverifiedEmailWarning,
                        onResendEmailVerification = {
                            FirebaseAuthManager.sendEmailVerification(
                                onSuccess = {
                                    Toast.makeText(context, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show()
                                },
                                onError = { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        isOtpSending = isOtpSending,
                        onOneTapLogin = {
                            loginErrorMessage = null
                            if (selectedLoginTab == 0) {
                                startPhoneLoginOtp(currentUser.phone)
                            } else {
                                loginWithEmail()
                            }
                        },
                        onSendPhoneOtp = {
                            startPhoneLoginOtp(phoneInput)
                        },
                        onLoginWithEmail = {
                            loginWithEmail()
                        },
                        onForgotPassword = { currentStep = FbLoginStep.FORGOT_PASSWORD_FIND },
                        onCreateAccount = { currentStep = FbLoginStep.CREATE_ACCOUNT_NO_OTP }
                    )
                }

                FbLoginStep.LOGIN_OTP_VERIFICATION -> {
                    LoginOtpVerificationView(
                        phone = activeContactForOtp,
                        otpCode = loginOtpCode,
                        onOtpChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) loginOtpCode = it },
                        expirySeconds = otpExpirySeconds,
                        resendCooldownSeconds = resendCooldownSeconds,
                        attemptsRemaining = otpAttemptsRemaining,
                        isVerifying = isOtpVerifying,
                        isSending = isOtpSending,
                        onResend = { startPhoneLoginOtp(activeContactForOtp, isResend = true) },
                        errorMessage = loginErrorMessage,
                        trustDevice = trustDevice,
                        onToggleTrustDevice = { trustDevice = !trustDevice },
                        onBack = { currentStep = FbLoginStep.MAIN_LOGIN },
                        onConfirm = { verifyPhoneLoginOtp() }
                    )
                }

                FbLoginStep.FORGOT_PASSWORD_FIND -> {
                    ForgotPasswordFindView(
                        searchQuery = forgotSearchQuery,
                        onSearchChange = { forgotSearchQuery = it; forgotResetNotice = null },
                        resetNotice = forgotResetNotice,
                        isSending = isSendingResetEmail,
                        onBack = { currentStep = FbLoginStep.MAIN_LOGIN },
                        onSendReset = {
                            val query = forgotSearchQuery.trim()
                            if (query.isBlank()) {
                                Toast.makeText(context, "Please enter your email address", Toast.LENGTH_SHORT).show()
                                return@ForgotPasswordFindView
                            }
                            isSendingResetEmail = true
                            FirebaseAuthManager.sendPasswordResetEmail(
                                email = query,
                                onSuccess = {
                                    isSendingResetEmail = false
                                    forgotResetNotice = "If an account exists for $query, password reset instructions have been sent to your email."
                                    Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_LONG).show()
                                },
                                onError = { err ->
                                    isSendingResetEmail = false
                                    forgotResetNotice = err
                                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    )
                }

                FbLoginStep.FORGOT_PASSWORD_CHOOSE_METHOD -> {
                    ForgotPasswordChooseMethodView(
                        currentUser = authenticatedAccount?.let { AccountManager.toUser(it) } ?: currentUser,
                        selectedMethod = forgotSelectedMethod,
                        onMethodSelected = { forgotSelectedMethod = it },
                        onBack = { currentStep = FbLoginStep.FORGOT_PASSWORD_FIND },
                        onContinue = { currentStep = FbLoginStep.FORGOT_PASSWORD_OTP }
                    )
                }

                FbLoginStep.FORGOT_PASSWORD_OTP -> {
                    val contactTarget = authenticatedAccount?.contact ?: (if (forgotSelectedMethod == "SMS") currentUser.phone else currentUser.email)
                    ForgotPasswordOtpView(
                        phoneOrEmail = contactTarget,
                        generatedOtp = "123456",
                        otpCode = forgotOtpCode,
                        onOtpChange = { if (it.length <= 6) forgotOtpCode = it },
                        showSmsBanner = false,
                        onDismissBanner = {},
                        onAutoFill = { forgotOtpCode = "123456" },
                        onBack = { currentStep = FbLoginStep.FORGOT_PASSWORD_CHOOSE_METHOD },
                        onResend = {},
                        onContinue = {
                            currentStep = FbLoginStep.FORGOT_PASSWORD_NEW_PASS
                        }
                    )
                }

                FbLoginStep.FORGOT_PASSWORD_NEW_PASS -> {
                    ForgotPasswordNewPassView(
                        newPassword = forgotNewPassword,
                        onNewPasswordChange = { forgotNewPassword = it },
                        confirmPassword = forgotConfirmPassword,
                        onConfirmPasswordChange = { forgotConfirmPassword = it },
                        isPasswordVisible = isNewPasswordVisible,
                        onTogglePassword = { isNewPasswordVisible = !isNewPasswordVisible },
                        logoutOtherDevices = logoutOtherDevices,
                        onToggleLogoutDevices = { logoutOtherDevices = !logoutOtherDevices },
                        onBack = { currentStep = FbLoginStep.FORGOT_PASSWORD_OTP },
                        onSubmit = {
                            if (forgotNewPassword.length < 6) {
                                Toast.makeText(context, "මුරපදයට අවම වශයෙන් අක්ෂර 6ක් තිබිය යුතුය", Toast.LENGTH_SHORT).show()
                            } else if (forgotNewPassword != forgotConfirmPassword) {
                                Toast.makeText(context, "මුරපද දෙක නොගැළපේ", Toast.LENGTH_SHORT).show()
                            } else {
                                val targetContact = authenticatedAccount?.contact ?: forgotSearchQuery
                                AccountManager.updatePassword(context, targetContact, forgotNewPassword)
                                Toast.makeText(context, "මුරපදය සාර්ථකව වෙනස් විය! ගිණුමට පිවිසුණා.", Toast.LENGTH_LONG).show()
                                val userToLogin = authenticatedAccount?.let { AccountManager.toUser(it) } ?: currentUser
                                onLoginSuccessWithUser(userToLogin)
                                onLoginSuccess()
                            }
                        }
                    )
                }

                FbLoginStep.CREATE_ACCOUNT_NO_OTP -> {
                    CreateAccountStepFlow(
                        selectedLanguage = selectedLanguage,
                        onBackToLogin = { currentStep = FbLoginStep.MAIN_LOGIN },
                        onAccountCreated = { newUser ->
                            onLoginSuccessWithUser(newUser)
                            onLoginSuccess()
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. MAIN LOGIN VIEW (Facebook Lite style with Phone & Email tabs)
// -------------------------------------------------------------
@Composable
private fun MainLoginView(
    currentUser: User,
    selectedLoginTab: Int,
    onTabChange: (Int) -> Unit,
    phoneInput: String,
    onPhoneChange: (String) -> Unit,
    emailInput: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePassword: () -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    loginErrorMessage: String? = null,
    unverifiedEmailWarning: Boolean = false,
    onResendEmailVerification: () -> Unit = {},
    isOtpSending: Boolean = false,
    onOneTapLogin: () -> Unit,
    onSendPhoneOtp: () -> Unit,
    onLoginWithEmail: () -> Unit,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit
) {
    val languages = listOf("සිංහල", "English", "தமிழ்")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Languages Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            languages.forEachIndexed { index, lang ->
                Text(
                    text = lang,
                    fontSize = 13.sp,
                    color = if (selectedLanguage == lang) Color(0xFF1877F2) else Color(0xFFB0B3B8),
                    fontWeight = if (selectedLanguage == lang) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clickable { onLanguageSelected(lang) }
                        .padding(horizontal = 8.dp)
                )
                if (index < languages.size - 1) {
                    Text("•", color = Color(0xFF4E4F50), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // FriendHub Logo
        Text(
            text = "FriendHub",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                letterSpacing = (-1.5).sp
            ),
            color = Color(0xFF1877F2)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 1-Tap Login Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onOneTapLogin() }
                .testTag("one_tap_login_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF393A3B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFF1877F2), CircleShape)
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = currentUser.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> if (selectedLoginTab == 0) "Log in with phone SMS code" else "Quick log in"
                            "தமிழ்" -> if (selectedLoginTab == 0) "SMS குறியீட்டுடன் உள்நுழைக" else "விரைவான உள்நுழைவு"
                            else -> if (selectedLoginTab == 0) "දුරකථන SMS කේතයෙන් පිවිසෙන්න" else "ක්ෂණිකව පිවිසෙන්න"
                        },
                        color = Color(0xFF1877F2),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "➔",
                    color = Color(0xFF1877F2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Divider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF393A3B), thickness = 0.8.dp)
            Text(
                text = when (selectedLanguage) {
                    "English" -> " OR CHOOSE METHOD "
                    "தமிழ்" -> " அல்லது முறையைத் தேர்ந்தெடுக்கவும் "
                    else -> " හෝ ක්‍රමය තෝරන්න "
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF8A8D91),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF393A3B), thickness = 0.8.dp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dual Auth Selector Tabs: [ 📱 Phone Number ] [ ✉️ Email ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color(0xFF242526), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF393A3B), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            // Tab 0: Phone Number
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedLoginTab == 0) Color(0xFF1877F2) else Color.Transparent)
                    .clickable { onTabChange(0) }
                    .padding(vertical = 10.dp)
                    .testTag("login_tab_phone"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = if (selectedLoginTab == 0) Color.White else Color(0xFFB0B3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> "Phone"
                            "தமிழ்" -> "தொலைபேசி"
                            else -> "දුරකථන අංකය"
                        },
                        fontSize = 13.sp,
                        fontWeight = if (selectedLoginTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedLoginTab == 0) Color.White else Color(0xFFB0B3B8)
                    )
                }
            }

            // Tab 1: Email
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedLoginTab == 1) Color(0xFF1877F2) else Color.Transparent)
                    .clickable { onTabChange(1) }
                    .padding(vertical = 10.dp)
                    .testTag("login_tab_email"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = if (selectedLoginTab == 1) Color.White else Color(0xFFB0B3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> "Email"
                            "தமிழ்" -> "மின்னஞ்சல்"
                            else -> "ඊමේල් (Email)"
                        },
                        fontSize = 13.sp,
                        fontWeight = if (selectedLoginTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedLoginTab == 1) Color.White else Color(0xFFB0B3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form fields based on selectedLoginTab
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedLoginTab == 0) {
                // PHONE LOGIN TAB
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = onPhoneChange,
                    label = {
                        Text(
                            text = when (selectedLanguage) {
                                "English" -> "Phone Number (+94XXXXXXXXX)"
                                "தமிழ்" -> "தொலைபேசி எண் (+94...)"
                                else -> "දුරකථන අංකය (+94...)"
                            },
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = Color(0xFF1877F2)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "+94 77 123 4567",
                            color = Color(0xFF65676B),
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFF393A3B),
                        focusedLabelColor = Color(0xFF1877F2),
                        unfocusedLabelColor = Color(0xFFB0B3B8)
                    )
                )

                Text(
                    text = when (selectedLanguage) {
                        "English" -> "We will send an SMS code to verify your phone number. Carrier rates may apply."
                        "தமிழ்" -> "உங்கள் தொலைபேசி எண்ணை சரிபார்க்க SMS குறியீடு அனுப்பப்படும்."
                        else -> "ඔබගේ දුරකථන අංකය සත්‍යාපනය සඳහා ඉලක්කම් 6 ක SMS කේතයක් එවනු ලැබේ."
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF8A8D91),
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            } else {
                // EMAIL LOGIN TAB
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = onEmailChange,
                    label = {
                        Text(
                            text = when (selectedLanguage) {
                                "English" -> "Email address"
                                "தமிழ்" -> "மின்னஞ்சல் முகவரி"
                                else -> "විද්‍යුත් තැපෑල (Email)"
                            },
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color(0xFF1877F2)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "user@example.com",
                            color = Color(0xFF65676B),
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFF393A3B),
                        focusedLabelColor = Color(0xFF1877F2),
                        unfocusedLabelColor = Color(0xFFB0B3B8)
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = {
                        Text(
                            text = when (selectedLanguage) {
                                "English" -> "Password"
                                "தமிழ்" -> "கடவுச்சொல்"
                                else -> "මුරපදය (Password)"
                            },
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = Color(0xFF1877F2)
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = onTogglePassword) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF8A8D91)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFF393A3B),
                        focusedLabelColor = Color(0xFF1877F2),
                        unfocusedLabelColor = Color(0xFFB0B3B8)
                    )
                )
            }

            // Error Message Banner
            if (loginErrorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_error_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1219)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE41E3F)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Error",
                            tint = Color(0xFFE41E3F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = loginErrorMessage,
                            color = Color(0xFFFFB4AB),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Unverified Email Warning Banner
            if (unverifiedEmailWarning && selectedLoginTab == 1) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF332A00)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB800)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Unverified",
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ඊමේල් ලිපිනය තහවුරු කර නොමැත",
                                color = Color(0xFFFFE082),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ඔබගේ ඊමේල් ගිණුමට තහවුරු කිරීමේ සබැඳියක් එවන ලදී. කරුණාකර Inbox පරීක්ෂා කරන්න.",
                            color = Color(0xFFFFE082),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "සබැඳිය නැවත එවන්න (Resend Verification Email)",
                            color = Color(0xFF42B72A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onResendEmailVerification() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Button
            if (selectedLoginTab == 0) {
                // Phone OTP Button
                Button(
                    onClick = onSendPhoneOtp,
                    enabled = !isOtpSending,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_phone_otp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isOtpSending) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SMS කේතය යවමින්...", color = Color.White, fontSize = 15.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (selectedLanguage) {
                                "English" -> "Send Login Code (OTP)"
                                "தமிழ்" -> "SMS குறியீட்டை அனுப்பு"
                                else -> "SMS කේතය එවන්න (Send OTP)"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Email Login Button
                Button(
                    onClick = onLoginWithEmail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> "Log In"
                            "தமிழ்" -> "உள்நுழைக"
                            else -> "පිවිසෙන්න (Log In)"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forgot_password_button")
                ) {
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> "Forgotten password?"
                            "தமிழ்" -> "கடவுச்சொல் மறந்துவிட்டதா?"
                            else -> "මුරපදය අමතකද? (Forgot Password)"
                        },
                        color = Color(0xFF1877F2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Create Account Button
        Button(
            onClick = onCreateAccount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(46.dp)
                .testTag("create_new_account_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42B72A)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = when (selectedLanguage) {
                    "English" -> "New FriendHub Account"
                    "தமிழ்" -> "புதிய FriendHub கணக்கு"
                    else -> "නව FriendHub ගිණුම සාදන්න"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = when (selectedLanguage) {
                "English" -> "FriendHub © 2026 • Security & Privacy • Multi-Device"
                "தமிழ்" -> "FriendHub © 2026 • தனியுரிமை • விதிமுறைகள்"
                else -> "FriendHub © 2026 • පෞද්ගලිකත්වය • ආරක්ෂාව"
            },
            fontSize = 11.sp,
            color = Color(0xFF65676B),
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}

// -------------------------------------------------------------
// 2. LOGIN OTP VERIFICATION VIEW (Secure Phone OTP Verification)
// -------------------------------------------------------------
@Composable
private fun LoginOtpVerificationView(
    phone: String,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    expirySeconds: Int,
    resendCooldownSeconds: Int,
    attemptsRemaining: Int,
    isVerifying: Boolean,
    isSending: Boolean,
    onResend: () -> Unit,
    errorMessage: String? = null,
    trustDevice: Boolean,
    onToggleTrustDevice: () -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val maskedPhone = PhoneAuthHelper.maskPhoneNumber(phone)
    val minutes = expirySeconds / 60
    val seconds = expirySeconds % 60
    val formattedExpiry = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "SMS සත්‍යාපනය (Phone Verification)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.8.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shield Lock Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1877F2).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = Color(0xFF1877F2),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ආරක්ෂණ SMS කේතය ඇතුළත් කරන්න",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ඔබගේ අනන්‍යතාව තහවුරු කිරීමට $maskedPhone වෙත ඉලක්කම් 6 කින් යුත් SMS කේතයක් එවන ලදී.",
                fontSize = 14.sp,
                color = Color(0xFFB0B3B8),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Expiry countdown timer & Attempts pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF242526))
                    .border(1.dp, if (expirySeconds < 30) Color(0xFFE41E3F) else Color(0xFF393A3B), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint = if (expirySeconds < 30) Color(0xFFE41E3F) else Color(0xFF1877F2),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (expirySeconds > 0) "කල් ඉකුත් වීමට: $formattedExpiry" else "කේතය කල් ඉකුත් විය",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (expirySeconds < 30) Color(0xFFFF897D) else Color(0xFFE4E6EB)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "ඉතිරි උත්සාහයන්: $attemptsRemaining/5",
                    fontSize = 12.sp,
                    color = Color(0xFF8A8D91)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6-digit PIN Boxes View
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0 until 6) {
                    val char = otpCode.getOrNull(i)?.toString() ?: ""
                    val isFocused = otpCode.length == i
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF242526))
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) Color(0xFF1877F2) else Color(0xFF393A3B),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actual input to receive keystrokes
            OutlinedTextField(
                value = otpCode,
                onValueChange = onOtpChange,
                label = { Text("ඉලක්කම් 6 කේතය ටයිප් කරන්න") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_input_field"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF393A3B),
                    focusedLabelColor = Color(0xFF1877F2),
                    unfocusedLabelColor = Color(0xFFB0B3B8)
                )
            )

            // Error display
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1219)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE41E3F)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFFB4AB),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trust this device checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleTrustDevice() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = trustDevice,
                    onCheckedChange = { onToggleTrustDevice() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF1877F2),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "මෙම උපාංගය සුරකින්න (Remember device)",
                    fontSize = 13.sp,
                    color = Color(0xFFE4E6EB)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Submit Button
            Button(
                onClick = onConfirm,
                enabled = otpCode.length == 6 && !isVerifying && expirySeconds > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_otp_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("සත්‍යාපනය කරමින්...", color = Color.White, fontSize = 15.sp)
                } else {
                    Text(
                        text = "තහවුරු කර පිවිසෙන්න (Verify & Log In)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resend Code Button with Countdown
            if (resendCooldownSeconds > 0) {
                Text(
                    text = "කේතය නැවත එවීමට තත්පර ${resendCooldownSeconds}ක් රැඳී සිටින්න...",
                    fontSize = 13.sp,
                    color = Color(0xFF8A8D91),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            } else {
                TextButton(
                    onClick = onResend,
                    enabled = !isSending,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFF1877F2),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "කේතය නැවත එවන්න (Resend SMS OTP)",
                        color = Color(0xFF1877F2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. FORGOT PASSWORD - EMAIL PASSWORD RESET
// -------------------------------------------------------------
@Composable
private fun ForgotPasswordFindView(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    resetNotice: String? = null,
    isSending: Boolean = false,
    onBack: () -> Unit,
    onSendReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "මුරපදය යළි සකසන්න (Reset Password)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.8.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "ඔබගේ ඊමේල් ලිපිනය ඇතුළත් කරන්න",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ඔබගේ ගිණුමේ මුරපදය යළි සැකසීමට ආරක්ෂිත සබැඳියක් ඔබගේ විද්‍යුත් තැපෑලට එවනු ලැබේ. ආරක්ෂක හේතූන් මත, ගිණුමේ පැවැත්ම මෙහි හෙළි නොකෙරේ.",
                fontSize = 13.sp,
                color = Color(0xFFB0B3B8),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("විද්‍යුත් තැපෑල (Email Address)", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF1877F2)
                    )
                },
                placeholder = { Text("user@example.com", color = Color(0xFF65676B)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_search_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF393A3B),
                    focusedLabelColor = Color(0xFF1877F2),
                    unfocusedLabelColor = Color(0xFFB0B3B8)
                )
            )

            if (resetNotice != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E1A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF42B72A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = resetNotice,
                        color = Color(0xFF81C784),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSendReset,
                enabled = !isSending && searchQuery.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("find_account_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ඊමේලය යවමින්...", color = Color.White, fontSize = 15.sp)
                } else {
                    Text(
                        text = "මුරපදය යළි සැකසීමේ සබැඳිය එවන්න",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. FORGOT PASSWORD - STEP 2: CHOOSE METHOD
// -------------------------------------------------------------
@Composable
private fun ForgotPasswordChooseMethodView(
    currentUser: User,
    selectedMethod: String,
    onMethodSelected: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "කේතය ලබාගන්නා ක්‍රමය තෝරන්න",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.8.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Account Found Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF393A3B))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = currentUser.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = currentUser.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "FriendHub ගිණුම හමුවිය ✅",
                            fontSize = 13.sp,
                            color = Color(0xFF42B72A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "මුරපදය නැවත සකසන කේතය ඔබ වෙත එවන්නේ කෙසේද?",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Option 1: SMS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedMethod == "SMS") Color(0xFF1877F2).copy(alpha = 0.12f) else Color(0xFF242526))
                    .clickable { onMethodSelected("SMS") }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == "SMS",
                    onClick = { onMethodSelected("SMS") },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1877F2))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "SMS මඟින් කේතය එවන්න", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = currentUser.phone, fontSize = 12.sp, color = Color(0xFFB0B3B8))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedMethod == "EMAIL") Color(0xFF1877F2).copy(alpha = 0.12f) else Color(0xFF242526))
                    .clickable { onMethodSelected("EMAIL") }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == "EMAIL",
                    onClick = { onMethodSelected("EMAIL") },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1877F2))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "විද්‍යුත් තැපෑලෙන් (Email) කේතය එවන්න", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = currentUser.email, fontSize = 12.sp, color = Color(0xFFB0B3B8))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("choose_method_continue_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "ඉදිරියට යන්න (Continue)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 5. FORGOT PASSWORD - STEP 3: ENTER OTP
// -------------------------------------------------------------
@Composable
private fun ForgotPasswordOtpView(
    phoneOrEmail: String,
    generatedOtp: String,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    showSmsBanner: Boolean,
    onDismissBanner: () -> Unit,
    onAutoFill: () -> Unit,
    onBack: () -> Unit,
    onResend: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "ආරක්ෂක කේතය ඇතුළත් කරන්න",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.8.dp)

        // Notification Banner
        AnimatedVisibility(
            visible = showSmsBanner && generatedOtp.isNotEmpty(),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onAutoFill() }
                    .testTag("forgot_sms_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1877F2))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = "SMS",
                        tint = Color(0xFF1877F2),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "💬 FriendHub Security",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1877F2)
                        )
                        Text(
                            text = "$generatedOtp යනු ඔබගේ FriendHub මුරපදය නැවත සකසන කේතයයි. මෙහි තට්ටු කර ස්වයංක්‍රීයව පුරවන්න.",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "අපි $phoneOrEmail වෙත ඉලක්කම් 6ක කේතයක් එව්වෙමු.",
                fontSize = 14.sp,
                color = Color(0xFFB0B3B8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = onOtpChange,
                label = { Text("ඉලක්කම් 6 කේතය (Security Code)", fontSize = 13.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_otp_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF393A3B),
                    focusedLabelColor = Color(0xFF1877F2),
                    unfocusedLabelColor = Color(0xFFB0B3B8)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("forgot_otp_continue_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "ඉදිරියට යන්න (Continue)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onResend,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "කේතය නැවත එවන්න (Resend Code)",
                    color = Color(0xFF1877F2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 6. FORGOT PASSWORD - STEP 4: CREATE NEW PASSWORD
// -------------------------------------------------------------
@Composable
private fun ForgotPasswordNewPassView(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePassword: () -> Unit,
    logoutOtherDevices: Boolean,
    onToggleLogoutDevices: () -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "නව මුරපදයක් සාදන්න",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.8.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "අවම වශයෙන් අක්ෂර 6කින් යුත් ශක්තිමත් නව මුරපදයක් ඇතුළත් කරන්න.",
                fontSize = 13.sp,
                color = Color(0xFFB0B3B8)
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                label = { Text("නව මුරපදය (New Password)", fontSize = 13.sp) },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle visibility",
                            tint = Color(0xFF8A8D91)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_password_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF393A3B),
                    focusedLabelColor = Color(0xFF1877F2),
                    unfocusedLabelColor = Color(0xFFB0B3B8)
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("මුරපදය තහවුරු කරන්න (Confirm)", fontSize = 13.sp) },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_password_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF393A3B),
                    focusedLabelColor = Color(0xFF1877F2),
                    unfocusedLabelColor = Color(0xFFB0B3B8)
                )
            )

            // Password strength indicator
            if (newPassword.isNotEmpty()) {
                val strengthText = when {
                    newPassword.length < 6 -> "දුර්වලයි (අවම අක්ෂර 6ක් අවශ්‍යයි)"
                    newPassword.length < 8 -> "මධ්‍යස්ථයි"
                    else -> "ශක්තිමත් මුරපදයකි ✅"
                }
                val strengthColor = when {
                    newPassword.length < 6 -> Color(0xFFE41E3F)
                    newPassword.length < 8 -> Color(0xFFF59E0B)
                    else -> Color(0xFF42B72A)
                }
                Text(
                    text = "මුරපදයේ ශක්තිය: $strengthText",
                    fontSize = 12.sp,
                    color = strengthColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Checkbox: Log out other devices
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleLogoutDevices() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = logoutOtherDevices,
                    onCheckedChange = { onToggleLogoutDevices() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF1877F2),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "අනෙක් සියලුම උපාංගවලින් මාව Log out කරන්න",
                    fontSize = 13.sp,
                    color = Color(0xFFE4E6EB)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_new_password_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "මුරපදය වෙනස් කර පිවිසෙන්න (Save & Log In)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 7. CREATE ACCOUNT VIEW (STRICTLY NO OTP - DIRECT SIGN UP)
// -------------------------------------------------------------
@Composable
private fun CreateAccountNoOtpView(
    selectedLanguage: String = "සිංහල",
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phoneOrEmail: String,
    onPhoneOrEmailChange: (String) -> Unit,
    birthday: String,
    onBirthdayChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePassword: () -> Unit,
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = when (selectedLanguage) {
                    "English" -> "New FriendHub Account"
                    "தமிழ்" -> "புதிய FriendHub கணக்கு"
                    else -> "නව FriendHub ගිණුම"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.8.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = when (selectedLanguage) {
                    "English" -> "No OTP required. Enter your details to create an account instantly!"
                    "தமிழ்" -> "OTP தேவையில்லை. உங்கள் விவரங்களை உள்ளிட்டு உடனடியாக சேரவும்!"
                    else -> "මෙහි OTP කේත අවශ්‍ය නොවේ. ඔබගේ තොරතුරු ඇතුළත් කර ක්ෂණිකව එකතු වන්න!"
                },
                fontSize = 13.sp,
                color = Color(0xFF42B72A),
                fontWeight = FontWeight.SemiBold
            )

            // Name Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    label = {
                        Text(
                            when (selectedLanguage) {
                                "English" -> "First name"
                                "தமிழ்" -> "முதல் பெயர்"
                                else -> "මුල් නම"
                            },
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reg_first_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFF393A3B),
                        focusedLabelColor = Color(0xFF1877F2),
                        unfocusedLabelColor = Color(0xFFB0B3B8)
                    )
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    label = {
                        Text(
                            when (selectedLanguage) {
                                "English" -> "Surname"
                                "தமிழ்" -> "குடும்பப் பெயர்"
                                else -> "වාසගම"
                            },
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reg_last_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFF393A3B),
                        focusedLabelColor = Color(0xFF1877F2),
                        unfocusedLabelColor = Color(0xFFB0B3B8)
                    )
                )
            }

            // Mobile or Email
            OutlinedTextField(
                value = phoneOrEmail,
                onValueChange = onPhoneOrEmailChange,
                label = {
                    Text(
                        when (selectedLanguage) {
                            "English" -> "Mobile number or email address"
                            "தமிழ்" -> "கைபேசி எண் அல்லது மின்னஞ்சல்"
                            else -> "දුරකථන අංකය හෝ විද්‍යුත් තැපෑල"
                        },
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_phone_email_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF393A3B),
                    focusedLabelColor = Color(0xFF1877F2),
                    unfocusedLabelColor = Color(0xFFB0B3B8)
                )
            )

            // Birthday & Gender
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = birthday,
                    onValueChange = onBirthdayChange,
                    label = {
                        Text(
                            when (selectedLanguage) {
                                "English" -> "Date of birth (YYYY-MM-DD)"
                                "தமிழ்" -> "பிறந்த தேதி (YYYY-MM-DD)"
                                else -> "උපන් දිනය (YYYY-MM-DD)"
                            },
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFF393A3B),
                        focusedLabelColor = Color(0xFF1877F2),
                        unfocusedLabelColor = Color(0xFFB0B3B8)
                    )
                )

                OutlinedTextField(
                    value = gender,
                    onValueChange = onGenderChange,
                    label = {
                        Text(
                            when (selectedLanguage) {
                                "English" -> "Gender"
                                "தமிழ்" -> "பாலினம்"
                                else -> "ස්ත්‍රී/පුරුෂ"
                            },
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFF393A3B),
                        focusedLabelColor = Color(0xFF1877F2),
                        unfocusedLabelColor = Color(0xFFB0B3B8)
                    )
                )
            }

            // New Password
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = {
                    Text(
                        when (selectedLanguage) {
                            "English" -> "New password"
                            "தமிழ்" -> "புதிய கடவுச்சொல்"
                            else -> "නව මුරපදය"
                        },
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = Color(0xFF8A8D91)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_password_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF393A3B),
                    focusedLabelColor = Color(0xFF1877F2),
                    unfocusedLabelColor = Color(0xFFB0B3B8)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Direct Sign Up Green Button
            Button(
                onClick = onRegisterSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("register_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42B72A)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "Sign Up"
                        "தமிழ்" -> "பதிவு செய்க"
                        else -> "ලියාපදිංචි වන්න"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (selectedLanguage) {
                    "English" -> "By clicking Sign Up, you agree to our Terms and Privacy Policy."
                    "தமிழ்" -> "பதிவு செய்க என்பதை கிளிக் செய்வதன் மூலம், நீங்கள் எங்கள் விதிமுறைகள் மற்றும் தனியுரிமைக் கொள்கையை ஒப்புக்கொள்கிறீர்கள்."
                    else -> "ලියාපදිංචි වීම ක්ලික් කිරීමෙන්, ඔබ අපගේ නියමයන් සහ දත්ත ප්‍රතිපත්තියට එකඟ වේ."
                },
                fontSize = 11.sp,
                color = Color(0xFF65676B),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
