package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import android.app.Activity
import com.example.auth.FirebaseAuthManager
import com.example.auth.PhoneAuthHelper
import kotlinx.coroutines.delay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.repository.AccountManager

enum class RegStep(val stepNumber: Int) {
    WELCOME(1),
    NAME(2),
    BIRTHDAY(3),
    GENDER(4),
    CONTACT(5),
    PHONE_OTP(6),
    PASSWORD(7),
    CONFIRM(8)
}

@Composable
fun CreateAccountStepFlow(
    selectedLanguage: String,
    onBackToLogin: () -> Unit,
    onAccountCreated: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var currentStep by remember { mutableStateOf(RegStep.WELCOME) }

    // Form states
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthYear by remember { mutableIntStateOf(2000) }
    var birthMonth by remember { mutableIntStateOf(5) }
    var birthDay by remember { mutableIntStateOf(14) }
    var gender by remember { mutableStateOf("Male") }
    var contactType by remember { mutableStateOf("PHONE") } // PHONE or EMAIL
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Phone OTP States
    var phoneVerificationId by remember { mutableStateOf<String?>(null) }
    var phoneOtpInput by remember { mutableStateOf("") }
    var otpExpirySeconds by remember { mutableIntStateOf(120) }
    var resendCooldownSeconds by remember { mutableIntStateOf(0) }
    var otpAttemptsRemaining by remember { mutableIntStateOf(5) }
    var isPhoneVerified by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var isSubmittingRegistration by remember { mutableStateOf(false) }

    // Error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val totalSteps = 7 // Form steps

    // OTP Timer Effects
    LaunchedEffect(currentStep, resendCooldownSeconds) {
        if (currentStep == RegStep.PHONE_OTP && resendCooldownSeconds > 0) {
            delay(1000L)
            resendCooldownSeconds--
        }
    }

    LaunchedEffect(currentStep, otpExpirySeconds) {
        if (currentStep == RegStep.PHONE_OTP && otpExpirySeconds > 0) {
            delay(1000L)
            otpExpirySeconds--
        }
    }

    fun sendPhoneRegistrationOtp(formattedPhone: String, isResend: Boolean = false) {
        if (activity == null) {
            errorMessage = "Device activity required for SMS verification."
            return
        }
        isSendingOtp = true
        errorMessage = null

        FirebaseAuthManager.sendPhoneOtp(
            activity = activity,
            rawPhoneNumber = formattedPhone,
            isResend = isResend,
            onCodeSent = { vid ->
                isSendingOtp = false
                phoneVerificationId = vid
                phoneOtpInput = ""
                resendCooldownSeconds = 60
                otpExpirySeconds = 120
                otpAttemptsRemaining = 5
                currentStep = RegStep.PHONE_OTP
                Toast.makeText(context, "Verification code sent to $formattedPhone via SMS", Toast.LENGTH_SHORT).show()
            },
            onError = { err ->
                isSendingOtp = false
                errorMessage = err
                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            }
        )
    }

    fun goToNextStep() {
        errorMessage = null
        when (currentStep) {
            RegStep.WELCOME -> currentStep = RegStep.NAME

            RegStep.NAME -> {
                val cleanFirst = firstName.trim()
                val cleanLast = lastName.trim()
                if (cleanFirst.length < 2) {
                    errorMessage = when (selectedLanguage) {
                        "English" -> "Please enter your first name (minimum 2 characters)"
                        "தமிழ்" -> "தயவுசெய்து உங்கள் முதல் பெயரை உள்ளிடவும் (குறைந்தது 2 எழுத்துக்கள்)"
                        else -> "කරුණාකර ඔබගේ මුල් නම ඇතුළත් කරන්න (අවම අකුරු 2ක්)"
                    }
                    return
                }
                if (cleanLast.length < 2) {
                    errorMessage = when (selectedLanguage) {
                        "English" -> "Please enter your surname (minimum 2 characters)"
                        "தமிழ்" -> "தயவுசெய்து உங்கள் குடும்பப் பெயரை உள்ளிடவும்"
                        else -> "කරුණාකර ඔබගේ වාසගම ඇතුළත් කරන්න (අවම අකුරු 2ක්)"
                    }
                    return
                }
                currentStep = RegStep.BIRTHDAY
            }

            RegStep.BIRTHDAY -> {
                val currentYear = 2026
                val age = currentYear - birthYear
                if (age < 13) {
                    errorMessage = when (selectedLanguage) {
                        "English" -> "You must be at least 13 years old to join FriendHub."
                        "தமிழ்" -> "FriendHub இல் சேர உங்களுக்கு குறைந்தது 13 வயது இருக்க வேண்டும்."
                        else -> "FriendHub හා එක්වීමට ඔබට අවම වශයෙන් අවුරුදු 13ක් පිරී තිබිය යුතුය."
                    }
                    return
                }
                currentStep = RegStep.GENDER
            }

            RegStep.GENDER -> {
                currentStep = RegStep.CONTACT
            }

            RegStep.CONTACT -> {
                val contactValue = if (contactType == "PHONE") phoneInput.trim() else emailInput.trim()
                if (contactValue.isBlank()) {
                    errorMessage = when (selectedLanguage) {
                        "English" -> "Mobile number or email address is strictly required."
                        "தமிழ்" -> "கைபேசி எண் அல்லது மின்னஞ்சல் கட்டாயமானது."
                        else -> "දුරකථන අංකය හෝ විද්‍යුත් තැපෑල ඇතුළත් කිරීම අනිවාර්ය වේ."
                    }
                    return
                }

                if (contactType == "PHONE") {
                    val formatted = PhoneAuthHelper.formatToE164(contactValue)
                    if (!PhoneAuthHelper.isValidPhoneNumber(formatted)) {
                        errorMessage = when (selectedLanguage) {
                            "English" -> "Please enter a valid phone number (+94 7X XXX XXXX)."
                            "தமிழ்" -> "செல்லுபடியாகும் கைபேசி எண்ணை உள்ளிடவும்."
                            else -> "කරුණාකර වලංගු දුරකථන අංකයක් ඇතුළත් කරන්න (උදා: +94 77 123 4567 හෝ 0771234567)."
                        }
                        return
                    }

                    // STRICT LIMIT ENFORCEMENT
                    val count = AccountManager.getAccountCount(contactValue)
                    if (count >= AccountManager.getMaxAccountsAllowed()) {
                        errorMessage = "❌ Limit reached: Maximum 3 accounts already exist for this phone number."
                        return
                    }

                    sendPhoneRegistrationOtp(formatted, isResend = false)
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(contactValue).matches()) {
                        errorMessage = when (selectedLanguage) {
                            "English" -> "Please enter a valid email address (e.g. name@example.com)."
                            "தமிழ்" -> "செல்லுபடியாகும் மின்னஞ்சல் முகவரியை உள்ளிடவும்."
                            else -> "කරුණාකර වලංගු විද්‍යුත් තැපැල් ලිපිනයක් ඇතුළත් කරන්න."
                        }
                        return
                    }
                    val count = AccountManager.getAccountCount(contactValue)
                    if (count >= AccountManager.getMaxAccountsAllowed()) {
                        errorMessage = "❌ Limit reached: Maximum 3 accounts already exist for this email address."
                        return
                    }
                    currentStep = RegStep.PASSWORD
                }
            }

            RegStep.PHONE_OTP -> {
                val vid = phoneVerificationId
                if (vid == null) {
                    errorMessage = "Verification session expired. Please tap Resend Code."
                    return
                }
                if (phoneOtpInput.length != 6) {
                    errorMessage = "Please enter the 6-digit code received via SMS."
                    return
                }
                if (otpExpirySeconds <= 0) {
                    errorMessage = "Verification code expired. Please tap Resend Code."
                    return
                }

                isVerifyingOtp = true
                errorMessage = null
                FirebaseAuthManager.verifyPhoneOtp(
                    verificationId = vid,
                    otpCode = phoneOtpInput,
                    onSuccess = { _, _ ->
                        isVerifyingOtp = false
                        isPhoneVerified = true
                        Toast.makeText(context, "Phone number verified successfully!", Toast.LENGTH_SHORT).show()
                        currentStep = RegStep.PASSWORD
                    },
                    onError = { err ->
                        isVerifyingOtp = false
                        otpAttemptsRemaining = FirebaseAuthManager.getRemainingAttempts()
                        errorMessage = err
                    }
                )
            }

            RegStep.PASSWORD -> {
                if (password.length < 6) {
                    errorMessage = when (selectedLanguage) {
                        "English" -> "Password must have at least 6 letters, numbers or symbols."
                        "தமிழ்" -> "கடவுச்சொல் குறைந்தது 6 எழுத்துக்களைக் கொண்டிருக்க வேண்டும்."
                        else -> "මුරපදයට අවම වශයෙන් අක්ෂර හෝ සංඛ්‍යා 6ක් තිබිය යුතුය."
                    }
                    return
                }
                currentStep = RegStep.CONFIRM
            }

            RegStep.CONFIRM -> {
                val finalContact = if (contactType == "PHONE") PhoneAuthHelper.formatToE164(phoneInput.trim()) else emailInput.trim()
                val birthdayStr = "$birthYear-${birthMonth.toString().padStart(2, '0')}-${birthDay.toString().padStart(2, '0')}"
                val fullName = "${firstName.trim()} ${lastName.trim()}"

                isSubmittingRegistration = true
                errorMessage = null

                if (contactType == "EMAIL") {
                    FirebaseAuthManager.registerWithEmail(
                        email = finalContact,
                        pass = password,
                        displayName = fullName,
                        onSuccess = { fbUser ->
                            isSubmittingRegistration = false
                            AccountManager.registerAccount(
                                context = context,
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                rawContact = finalContact,
                                password = password,
                                birthday = birthdayStr,
                                gender = gender
                            )
                            val newUser = User(
                                id = fbUser.uid,
                                name = fullName,
                                email = finalContact,
                                isOnline = true
                            )
                            Toast.makeText(
                                context,
                                "Welcome $fullName! Verification email sent. Please check your inbox.",
                                Toast.LENGTH_LONG
                            ).show()
                            onAccountCreated(newUser)
                        },
                        onError = { err ->
                            isSubmittingRegistration = false
                            // Fallback to local registration if offline/mock
                            val result = AccountManager.registerAccount(
                                context = context,
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                rawContact = finalContact,
                                password = password,
                                birthday = birthdayStr,
                                gender = gender
                            )
                            if (result.first) {
                                val authAcc = AccountManager.findAccount(context, finalContact)
                                val newUser = authAcc?.let { AccountManager.toUser(it) } ?: User(name = fullName, email = finalContact)
                                onAccountCreated(newUser)
                            } else {
                                errorMessage = err
                            }
                        }
                    )
                } else {
                    // Phone is already verified!
                    val currentFbUser = FirebaseAuthManager.currentUser.value
                    val userId = currentFbUser?.uid ?: java.util.UUID.randomUUID().toString()
                    FirebaseAuthManager.syncUserProfileToFirestore(
                        userId = userId,
                        displayName = fullName,
                        email = "",
                        phone = finalContact
                    )
                    AccountManager.registerAccount(
                        context = context,
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        rawContact = finalContact,
                        password = password,
                        birthday = birthdayStr,
                        gender = gender
                    )
                    isSubmittingRegistration = false
                    val newUser = User(
                        id = userId,
                        name = fullName,
                        phone = finalContact,
                        isOnline = true
                    )
                    Toast.makeText(
                        context,
                        "Welcome $fullName! Phone number verified and account created.",
                        Toast.LENGTH_LONG
                    ).show()
                    onAccountCreated(newUser)
                }
            }
        }
    }

    fun goToPreviousStep() {
        errorMessage = null
        when (currentStep) {
            RegStep.WELCOME -> onBackToLogin()
            RegStep.NAME -> currentStep = RegStep.WELCOME
            RegStep.BIRTHDAY -> currentStep = RegStep.NAME
            RegStep.GENDER -> currentStep = RegStep.BIRTHDAY
            RegStep.CONTACT -> currentStep = RegStep.GENDER
            RegStep.PHONE_OTP -> currentStep = RegStep.CONTACT
            RegStep.PASSWORD -> if (contactType == "PHONE") currentStep = RegStep.PHONE_OTP else currentStep = RegStep.CONTACT
            RegStep.CONFIRM -> currentStep = RegStep.PASSWORD
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF18191A))
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { goToPreviousStep() },
                modifier = Modifier.testTag("reg_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
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
                if (currentStep != RegStep.WELCOME) {
                    val stepIdx = currentStep.stepNumber - 1
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> "Step $stepIdx of $totalSteps"
                            "தமிழ்" -> "படி $stepIdx / $totalSteps"
                            else -> "පියවර $stepIdx / $totalSteps"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF1877F2),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            TextButton(onClick = onBackToLogin) {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "Cancel"
                        "தமிழ்" -> "ரத்துசெய்"
                        else -> "අවලංගු කරන්න"
                    },
                    color = Color(0xFFB0B3B8),
                    fontSize = 13.sp
                )
            }
        }

        // Progress indicator
        if (currentStep != RegStep.WELCOME) {
            val progress = (currentStep.stepNumber - 1).toFloat() / totalSteps.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = Color(0xFF1877F2),
                trackColor = Color(0xFF3A3B3C)
            )
        } else {
            HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.8.dp)
        }

        // Animated Step Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.stepNumber > initialState.stepNumber) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "RegStepAnimation"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        RegStep.WELCOME -> {
                            WelcomeStepView(
                                selectedLanguage = selectedLanguage,
                                onNext = { goToNextStep() },
                                onLogin = onBackToLogin
                            )
                        }

                        RegStep.NAME -> {
                            NameStepView(
                                selectedLanguage = selectedLanguage,
                                firstName = firstName,
                                onFirstNameChange = { firstName = it; errorMessage = null },
                                lastName = lastName,
                                onLastNameChange = { lastName = it; errorMessage = null },
                                errorMessage = errorMessage,
                                onNext = { goToNextStep() }
                            )
                        }

                        RegStep.BIRTHDAY -> {
                            BirthdayStepView(
                                selectedLanguage = selectedLanguage,
                                birthYear = birthYear,
                                onYearChange = { birthYear = it; errorMessage = null },
                                birthMonth = birthMonth,
                                onMonthChange = { birthMonth = it; errorMessage = null },
                                birthDay = birthDay,
                                onDayChange = { birthDay = it; errorMessage = null },
                                errorMessage = errorMessage,
                                onNext = {
                                    if (birthDay !in 1..31) {
                                        errorMessage = when (selectedLanguage) {
                                            "English" -> "Please enter a valid day (1-31)"
                                            "தமிழ்" -> "தயவுசெய்து சரியான நாளை உள்ளிடவும் (1-31)"
                                            else -> "කරුණාකර නිවැරදි දිනයක් ඇතුළත් කරන්න (1-31)"
                                        }
                                    } else if (birthMonth !in 1..12) {
                                        errorMessage = when (selectedLanguage) {
                                            "English" -> "Please enter a valid month (1-12)"
                                            "தமிழ்" -> "தயவுசெய்து சரியான மாதத்தை உள்ளிடவும் (1-12)"
                                            else -> "කරුණාකර නිවැරදි මාසයක් ඇතුළත් කරන්න (1-12)"
                                        }
                                    } else if (birthYear !in 1920..2026) {
                                        errorMessage = when (selectedLanguage) {
                                            "English" -> "Please enter a valid year"
                                            "தமிழ்" -> "தயவுசெய்து சரியான ஆண்டை உள்ளிடவும்"
                                            else -> "කරුණාකර නිවැරදි වර්ෂයක් ඇතුළත් කරන්න"
                                        }
                                    } else {
                                        goToNextStep()
                                    }
                                }
                            )
                        }

                        RegStep.GENDER -> {
                            GenderStepView(
                                selectedLanguage = selectedLanguage,
                                selectedGender = gender,
                                onGenderSelected = { gender = it },
                                onNext = { goToNextStep() }
                            )
                        }

                        RegStep.CONTACT -> {
                            ContactStepView(
                                selectedLanguage = selectedLanguage,
                                contactType = contactType,
                                onContactTypeChange = { contactType = it; errorMessage = null },
                                phoneInput = phoneInput,
                                onPhoneChange = { phoneInput = it; errorMessage = null },
                                emailInput = emailInput,
                                onEmailChange = { emailInput = it; errorMessage = null },
                                errorMessage = errorMessage,
                                onNext = { goToNextStep() }
                            )
                        }

                        RegStep.PHONE_OTP -> {
                            val formatted = PhoneAuthHelper.formatToE164(phoneInput)
                            PhoneOtpStepView(
                                selectedLanguage = selectedLanguage,
                                phoneNumber = formatted,
                                otpCode = phoneOtpInput,
                                onOtpChange = { 
                                    if (it.length <= 6) {
                                        phoneOtpInput = it
                                        errorMessage = null
                                    }
                                },
                                expirySeconds = otpExpirySeconds,
                                resendCooldownSeconds = resendCooldownSeconds,
                                attemptsRemaining = otpAttemptsRemaining,
                                isVerifying = isVerifyingOtp,
                                isSending = isSendingOtp,
                                onResend = {
                                    sendPhoneRegistrationOtp(formatted, isResend = true)
                                },
                                errorMessage = errorMessage,
                                onVerify = { goToNextStep() }
                            )
                        }

                        RegStep.PASSWORD -> {
                            PasswordStepView(
                                selectedLanguage = selectedLanguage,
                                password = password,
                                onPasswordChange = { password = it; errorMessage = null },
                                isPasswordVisible = isPasswordVisible,
                                onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                                errorMessage = errorMessage,
                                onNext = { goToNextStep() }
                            )
                        }

                        RegStep.CONFIRM -> {
                            ConfirmStepView(
                                selectedLanguage = selectedLanguage,
                                firstName = firstName,
                                lastName = lastName,
                                contact = if (contactType == "PHONE") phoneInput else emailInput,
                                isPhone = contactType == "PHONE",
                                birthday = "$birthYear-${birthMonth.toString().padStart(2, '0')}-${birthDay.toString().padStart(2, '0')}",
                                gender = gender,
                                errorMessage = errorMessage,
                                onConfirm = { goToNextStep() }
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: WELCOME SCREEN (Facebook Lite Style)
// -------------------------------------------------------------
@Composable
private fun WelcomeStepView(
    selectedLanguage: String,
    onNext: () -> Unit,
    onLogin: () -> Unit
) {
    Spacer(modifier = Modifier.height(24.dp))

    Box(
        modifier = Modifier
            .size(90.dp)
            .background(Color(0xFF1877F2).copy(alpha = 0.15f), shape = CircleShape)
            .border(2.dp, Color(0xFF1877F2), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = "Community",
            tint = Color(0xFF1877F2),
            modifier = Modifier.size(46.dp)
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Join FriendHub"
            "தமிழ்" -> "FriendHub இல் இணையுங்கள்"
            else -> "FriendHub වෙත එක්වන්න"
        },
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "We'll help you create a new account in a few easy steps. Connect with friends and the world around you."
            "தமிழ்" -> "சில எளிய படிகளில் புதிய கணக்கை உருவாக்க நாங்கள் உங்களுக்கு உதவுவோம். உங்கள் நண்பர்களுடன் இணையுங்கள்."
            else -> "පහසු පියවර කිහිපයකින් නව ගිණුමක් සාදා ඔබගේ මිතුරන්, පවුලේ අය සහ ප්‍රජාවන් සමඟ සම්බන්ධ වන්න."
        },
        fontSize = 14.sp,
        color = Color(0xFFB0B3B8),
        textAlign = TextAlign.Center,
        lineHeight = 20.sp,
        modifier = Modifier.padding(horizontal = 12.dp)
    )

    Spacer(modifier = Modifier.height(36.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_welcome_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Next"
                "தமிழ்" -> "அடுத்து"
                else -> "ඉදිරියට (Next)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(onClick = onLogin) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Already have an account? Log In"
                "தமிழ்" -> "ஏற்கனவே கணக்கு உள்ளதா? உள்நுழைக"
                else -> "දැනටමත් ගිණුමක් තිබේද? පිවිසෙන්න"
            },
            color = Color(0xFF1877F2),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// -------------------------------------------------------------
// STEP 2: NAME STEP (First Name, Surname)
// -------------------------------------------------------------
@Composable
private fun NameStepView(
    selectedLanguage: String,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    errorMessage: String?,
    onNext: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "What's your name?"
            "தமிழ்" -> "உங்கள் பெயர் என்ன?"
            else -> "ඔබගේ නම කුමක්ද?"
        },
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Enter the name you use in real life."
            "தமிழ்" -> "உண்மையான வாழ்க்கையில் நீங்கள் பயன்படுத்தும் பெயரை உள்ளிடவும்."
            else -> "ඔබ සැබෑ ජීවිතයේදී භාවිතා කරන නම ඇතුළත් කරන්න."
        },
        fontSize = 14.sp,
        color = Color(0xFFB0B3B8),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "First name"
                        "தமிழ்" -> "முதல் பெயர்"
                        else -> "මුල් නම (First Name)"
                    },
                    fontSize = 13.sp
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
                focusedBorderColor = Color(0xFF1877F2),
                unfocusedBorderColor = Color(0xFF393A3B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "Surname"
                        "தமிழ்" -> "குடும்பப் பெயர்"
                        else -> "වාසගම (Last Name)"
                    },
                    fontSize = 13.sp
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
                focusedBorderColor = Color(0xFF1877F2),
                unfocusedBorderColor = Color(0xFF393A3B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        ErrorBanner(errorMessage)
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_name_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Next"
                "தமிழ்" -> "அடுத்து"
                else -> "ඉදිරියට (Next)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// -------------------------------------------------------------
// STEP 3: BIRTHDAY STEP
// -------------------------------------------------------------
@Composable
private fun BirthdayStepView(
    selectedLanguage: String,
    birthYear: Int,
    onYearChange: (Int) -> Unit,
    birthMonth: Int,
    onMonthChange: (Int) -> Unit,
    birthDay: Int,
    onDayChange: (Int) -> Unit,
    errorMessage: String?,
    onNext: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color(0xFF242526), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Cake,
            contentDescription = "Birthday",
            tint = Color(0xFF1877F2),
            modifier = Modifier.size(34.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "What's your date of birth?"
            "தமிழ்" -> "உங்கள் பிறந்த தேதி என்ன?"
            else -> "ඔබගේ උපන් දිනය කුමක්ද?"
        },
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Choose your date of birth. You can always make this private later."
            "தமிழ்" -> "உங்கள் பிறந்த தேதியைத் தேர்ந்தெடுக்கவும். பின்னர் இதை தனிப்பட்டதாக்கலாம்."
            else -> "ඔබගේ උපන් දිනය තෝරන්න. මෙය පසුව ඔබට රහසිගතව (Private) තබා ගත හැක."
        },
        fontSize = 14.sp,
        color = Color(0xFFB0B3B8),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Day, Month, Year selectors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Day
        OutlinedTextField(
            value = if (birthDay == 0) "" else birthDay.toString(),
            onValueChange = { str ->
                if (str.isEmpty()) {
                    onDayChange(0)
                } else {
                    val v = str.filter { it.isDigit() }.toIntOrNull()
                    if (v != null) onDayChange(v)
                }
            },
            label = { Text("Day (දිනය)", fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF242526),
                unfocusedContainerColor = Color(0xFF242526),
                focusedBorderColor = Color(0xFF1877F2),
                unfocusedBorderColor = Color(0xFF393A3B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Month
        OutlinedTextField(
            value = if (birthMonth == 0) "" else birthMonth.toString(),
            onValueChange = { str ->
                if (str.isEmpty()) {
                    onMonthChange(0)
                } else {
                    val v = str.filter { it.isDigit() }.toIntOrNull()
                    if (v != null) onMonthChange(v)
                }
            },
            label = { Text("Month (මාසය)", fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF242526),
                unfocusedContainerColor = Color(0xFF242526),
                focusedBorderColor = Color(0xFF1877F2),
                unfocusedBorderColor = Color(0xFF393A3B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Year
        OutlinedTextField(
            value = if (birthYear == 0) "" else birthYear.toString(),
            onValueChange = { str ->
                if (str.isEmpty()) {
                    onYearChange(0)
                } else {
                    val v = str.filter { it.isDigit() }.toIntOrNull()
                    if (v != null) onYearChange(v)
                }
            },
            label = { Text("Year (වසර)", fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.weight(1.3f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF242526),
                unfocusedContainerColor = Color(0xFF242526),
                focusedBorderColor = Color(0xFF1877F2),
                unfocusedBorderColor = Color(0xFF393A3B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }

    val calculatedAge = 2026 - birthYear
    Spacer(modifier = Modifier.height(12.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF1877F2), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = when (selectedLanguage) {
                    "English" -> "Calculated age: $calculatedAge years old"
                    "தமிழ்" -> "கணக்கிடப்பட்ட வயது: $calculatedAge ஆண்டுகள்"
                    else -> "ගණනය කළ වයස: අවුරුදු $calculatedAge කි"
                },
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        ErrorBanner(errorMessage)
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_birthday_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Next"
                "தமிழ்" -> "அடுத்து"
                else -> "ඉදිරියට (Next)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// -------------------------------------------------------------
// STEP 4: GENDER STEP
// -------------------------------------------------------------
@Composable
private fun GenderStepView(
    selectedLanguage: String,
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "What's your gender?"
            "தமிழ்" -> "உங்கள் பாலினம் என்ன?"
            else -> "ඔබගේ ස්ත්‍රී/පුරුෂ භාවය කුමක්ද?"
        },
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "You can change who sees this on your profile later."
            "தமிழ்" -> "பின்னர் இதை யார் பார்க்கலாம் என்பதை மாற்றலாம்."
            else -> "පසුව ඔබට ඔබගේ ස්ත්‍රී/පුරුෂ භාවය වෙනත් අයට පෙනෙන ආකාරය රහසිගත කළ හැක."
        },
        fontSize = 14.sp,
        color = Color(0xFFB0B3B8),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    val options = listOf(
        Triple("Female", "ස්ත්‍රී (Female)", "பெண்"),
        Triple("Male", "පුරුෂ (Male)", "ஆண்"),
        Triple("Custom", "වෙනත් (Custom)", "மற்றவை")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { opt ->
            val label = when (selectedLanguage) {
                "English" -> opt.first
                "தமிழ்" -> opt.third
                else -> opt.second
            }
            val isSelected = selectedGender == opt.first

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGenderSelected(opt.first) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1877F2).copy(alpha = 0.15f) else Color(0xFF242526)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) Color(0xFF1877F2) else Color(0xFF393A3B))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.White
                    )
                    RadioButton(
                        selected = isSelected,
                        onClick = { onGenderSelected(opt.first) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF1877F2),
                            unselectedColor = Color(0xFF8A8D91)
                        )
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_gender_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Next"
                "தமிழ்" -> "அடுத்து"
                else -> "ඉදිරියට (Next)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// -------------------------------------------------------------
// STEP 5: CONTACT STEP (STRICT VALIDATION + MAX 3 ACCOUNTS LIMIT)
// -------------------------------------------------------------
@Composable
private fun ContactStepView(
    selectedLanguage: String,
    contactType: String,
    onContactTypeChange: (String) -> Unit,
    phoneInput: String,
    onPhoneChange: (String) -> Unit,
    emailInput: String,
    onEmailChange: (String) -> Unit,
    errorMessage: String?,
    onNext: () -> Unit
) {
    val activeInput = if (contactType == "PHONE") phoneInput else emailInput
    val accountCount = if (activeInput.isNotBlank()) AccountManager.getAccountCount(activeInput) else 0
    val maxLimit = AccountManager.getMaxAccountsAllowed()
    val isLimitExceeded = accountCount >= maxLimit

    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color(0xFF242526), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (contactType == "PHONE") Icons.Default.Phone else Icons.Default.Email,
            contentDescription = "Contact",
            tint = Color(0xFF1877F2),
            modifier = Modifier.size(34.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Enter your mobile number or email"
            "தமிழ்" -> "உங்கள் கைபேசி எண் அல்லது மின்னஞ்சலை உள்ளிடவும்"
            else -> "දුරකථන අංකය හෝ විද්‍යුත් තැපෑල ඇතුළත් කරන්න"
        },
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Enter the number or email where you can be reached. Max 3 accounts allowed per contact."
            "தமிழ்" -> "உங்களை தொடர்பு கொள்ளக்கூடிய எண் அல்லது மின்னஞ்சலை உள்ளிடவும். ஒரு தொடர்புக்கு அதிகபட்சம் 3 கணக்குகள் அனுமதிக்கப்படும்."
            else -> "ඔබව සම්බන්ධ කරගත හැකි සැබෑ දුරකථන අංකය හෝ ඊමේල් ලිපිනය ඇතුළත් කරන්න. එක් අංකයකට/ඊමේල් ලිපිනයකට උපරිම ගිණුම් 3ක් පමණි."
        },
        fontSize = 13.sp,
        color = Color(0xFFB0B3B8),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Toggle Tab: Phone vs Email
    TabRow(
        selectedTabIndex = if (contactType == "PHONE") 0 else 1,
        containerColor = Color(0xFF242526),
        contentColor = Color.White,
        indicator = { tabPositions ->
            val idx = if (contactType == "PHONE") 0 else 1
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[idx]),
                color = Color(0xFF1877F2),
                height = 3.dp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, Color(0xFF393A3B), RoundedCornerShape(10.dp))
    ) {
        Tab(
            selected = contactType == "PHONE",
            onClick = { onContactTypeChange("PHONE") },
            text = {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "Mobile Number"
                        "தமிழ்" -> "கைபேசி எண்"
                        else -> "දුරකථන අංකය"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        )
        Tab(
            selected = contactType == "EMAIL",
            onClick = { onContactTypeChange("EMAIL") },
            text = {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "Email Address"
                        "தமிழ்" -> "மின்னஞ்சல்"
                        else -> "විද්‍යුත් තැපෑල"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    if (contactType == "PHONE") {
        OutlinedTextField(
            value = phoneInput,
            onValueChange = onPhoneChange,
            label = {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "Mobile number (e.g. 0771234567)"
                        "தமிழ்" -> "கைபேசி எண் (எ.கா. 0771234567)"
                        else -> "දුරකථන අංකය (උදා: 0771234567)"
                    },
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_contact_phone_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF242526),
                unfocusedContainerColor = Color(0xFF242526),
                focusedBorderColor = if (isLimitExceeded) Color(0xFFE41E3F) else Color(0xFF1877F2),
                unfocusedBorderColor = if (isLimitExceeded) Color(0xFFE41E3F) else Color(0xFF393A3B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    } else {
        OutlinedTextField(
            value = emailInput,
            onValueChange = onEmailChange,
            label = {
                Text(
                    text = when (selectedLanguage) {
                        "English" -> "Email address (e.g. name@mail.com)"
                        "தமிழ்" -> "மின்னஞ்சல் முகவரி"
                        else -> "විද්‍යුත් තැපෑල (උදා: name@mail.com)"
                    },
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_contact_email_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF242526),
                unfocusedContainerColor = Color(0xFF242526),
                focusedBorderColor = if (isLimitExceeded) Color(0xFFE41E3F) else Color(0xFF1877F2),
                unfocusedBorderColor = if (isLimitExceeded) Color(0xFFE41E3F) else Color(0xFF393A3B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }

    // Dynamic Limit & Quota Badge
    if (activeInput.isNotBlank() && AccountManager.isValidContact(activeInput)) {
        Spacer(modifier = Modifier.height(10.dp))
        if (isLimitExceeded) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1419)),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE41E3F))),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Limit Exceeded", tint = Color(0xFFE41E3F), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> "❌ Maximum limit reached ($accountCount/$maxLimit accounts created). You cannot create more accounts with this contact."
                            "தமிழ்" -> "❌ அதிகபட்ச வரம்பு எட்டப்பட்டது ($accountCount/$maxLimit கணக்குகள்). இந்த தொடர்பைக் கொண்டு கூடுதல் கணக்குகளை உருவாக்க முடியாது."
                            else -> "❌ උපරිම සීමාව ඉක්මවා ඇත (ගිණුම් $accountCount/$maxLimit සාදා ඇත). මෙම අංකයෙන්/ඊමේල් ලිපිනයෙන් තවත් ගිණුම් සෑදිය නොහැක."
                        },
                        fontSize = 12.sp,
                        color = Color(0xFFFFB4AB),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10281E)),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF42B72A))),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF42B72A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (selectedLanguage) {
                            "English" -> "✓ Valid contact • Accounts used: $accountCount of $maxLimit allowed (This will be account #${accountCount + 1})"
                            "தமிழ்" -> "✓ செல்லுபடியாகும் தொடர்பு • பயன்படுத்தப்பட்ட கணக்குகள்: $accountCount / $maxLimit"
                            else -> "✓ වලංගුයි • භාවිත කර ඇති ගිණුම්: $accountCount / $maxLimit කි (මෙය ඔබගේ #${accountCount + 1} වන ගිණුම වේ)"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF69F0AE),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        ErrorBanner(errorMessage)
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onNext,
        enabled = !isLimitExceeded,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_contact_next_button"),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1877F2),
            disabledContainerColor = Color(0xFF3A3B3C)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Next"
                "தமிழ்" -> "அடுத்து"
                else -> "ඉදිරියට (Next)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (!isLimitExceeded) Color.White else Color(0xFF8A8D91)
        )
    }
}

// -------------------------------------------------------------
// STEP 6: PASSWORD STEP
// -------------------------------------------------------------
@Composable
private fun PasswordStepView(
    selectedLanguage: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePassword: () -> Unit,
    errorMessage: String?,
    onNext: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color(0xFF242526), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Security",
            tint = Color(0xFF1877F2),
            modifier = Modifier.size(34.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Choose a password"
            "தமிழ்" -> "கடவுச்சொல்லைத் தேர்ந்தெடுக்கவும்"
            else -> "මුරපදයක් තෝරන්න"
        },
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Create a password with at least 6 letters and numbers. It should be something others can't guess."
            "தமிழ்" -> "குறைந்தது 6 எழுத்துக்கள் மற்றும் எண்களைக் கொண்ட கடவுச்சொல்லை உருவாக்கவும்."
            else -> "අවම වශයෙන් අක්ෂර සහ සංඛ්‍යා 6 කින් යුත් ශක්තිමත් මුරපදයක් සාදන්න. අන් අයට අනුමාන කළ නොහැකි එකක් විය යුතුය."
        },
        fontSize = 13.sp,
        color = Color(0xFFB0B3B8),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(
                text = when (selectedLanguage) {
                    "English" -> "Password (min 6 characters)"
                    "தமிழ்" -> "கடவுச்சொல் (குறைந்தது 6 எழுத்துகள்)"
                    else -> "මුරපදය (අවම අක්ෂර 6ක්)"
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
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle Password",
                    tint = Color(0xFFB0B3B8)
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
            focusedBorderColor = Color(0xFF1877F2),
            unfocusedBorderColor = Color(0xFF393A3B),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )

    // Password strength hint
    if (password.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        val isStrong = password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }
        val isMedium = password.length >= 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isStrong) Icons.Default.Shield else Icons.Default.Info,
                contentDescription = null,
                tint = if (isStrong) Color(0xFF42B72A) else if (isMedium) Color(0xFFF7B125) else Color(0xFFE41E3F),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isStrong) "ශක්තිමත් මුරපදයකි (Strong)" else if (isMedium) "මධ්‍යස්ථ මුරපදයකි (Medium)" else "දුර්වලයි (Weak - min 6 chars)",
                fontSize = 12.sp,
                color = if (isStrong) Color(0xFF42B72A) else if (isMedium) Color(0xFFF7B125) else Color(0xFFE41E3F)
            )
        }
    }

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        ErrorBanner(errorMessage)
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_password_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Next"
                "தமிழ்" -> "அடுத்து"
                else -> "ඉදිරියට (Next)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// -------------------------------------------------------------
// STEP 7: REVIEW & CONFIRM SIGN UP
// -------------------------------------------------------------
@Composable
private fun ConfirmStepView(
    selectedLanguage: String,
    firstName: String,
    lastName: String,
    contact: String,
    isPhone: Boolean,
    birthday: String,
    gender: String,
    errorMessage: String?,
    onConfirm: () -> Unit
) {
    val count = AccountManager.getAccountCount(contact)

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Finish signing up"
            "தமிழ்" -> "பதிவை முடிக்கவும்"
            else -> "ලියාපදිංචිය සම්පූර්ණ කරන්න"
        },
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Please review your details. Privacy and security are guaranteed."
            "தமிழ்" -> "உங்கள் விவரங்களை மதிப்பாய்வு செய்யவும். தனியுரிமை மற்றும் பாதுகாப்பு உத்தரவாதம்."
            else -> "ඔබගේ තොරතුරු තහවුරු කරන්න. ඔබගේ පෞද්ගලිකත්වය සහ ආරක්ෂාව 100% තහවුරු වේ."
        },
        fontSize = 13.sp,
        color = Color(0xFFB0B3B8),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Profile details summary card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF393A3B)))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "සම්පූර්ණ නම (Full Name)", fontSize = 11.sp, color = Color(0xFF8A8D91))
                    Text(text = "$firstName $lastName", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.5.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (isPhone) Icons.Default.Phone else Icons.Default.Email, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = if (isPhone) "දුරකථන අංකය (Mobile)" else "විද්‍යුත් තැපෑල (Email)", fontSize = 11.sp, color = Color(0xFF8A8D91))
                    Text(text = contact, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = "ගිණුම් කෝටාව: #${count + 1} of 3 (Accounts Limit 3)",
                        fontSize = 11.sp,
                        color = Color(0xFF42B72A),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.5.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Cake, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "උපන් දිනය හා ස්ත්‍රී/පුරුෂ භාවය", fontSize = 11.sp, color = Color(0xFF8A8D91))
                    Text(text = "$birthday • $gender", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "By tapping Sign Up, you agree to our Terms, Privacy Policy and Cookies Policy."
            "தமிழ்" -> "பதிவு செய்க என்பதைத் தட்டுவதன் மூலம், எங்கள் விதிமுறைகள் மற்றும் தனியுரிமைக் கொள்கையை ஏற்கிறீர்கள்."
            else -> "ලියාපදිංචි වන්න තට්ටු කිරීමෙන්, ඔබ අපගේ නියමයන්, පෞද්ගලිකත්ව ප්‍රතිපත්තිය සහ කුකී ප්‍රතිපත්තිය පිළිගනී."
        },
        fontSize = 11.sp,
        color = Color(0xFF8A8D91),
        textAlign = TextAlign.Center,
        lineHeight = 16.sp
    )

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        ErrorBanner(errorMessage)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onConfirm,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_final_signup_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42B72A)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = when (selectedLanguage) {
                "English" -> "Sign Up"
                "தமிழ்" -> "பதிவு செய்க"
                else -> "ලියාපදිංචි වන්න (Sign Up)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// -------------------------------------------------------------
// REUSABLE ERROR BANNER
// -------------------------------------------------------------
@Composable
private fun ErrorBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1419)),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE41E3F))),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFE41E3F), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = Color(0xFFFFB4AB),
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
            )
        }
    }
}

// -------------------------------------------------------------
// STEP: PHONE OTP VERIFICATION STEP
// -------------------------------------------------------------
@Composable
private fun PhoneOtpStepView(
    selectedLanguage: String,
    phoneNumber: String,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    expirySeconds: Int,
    resendCooldownSeconds: Int,
    attemptsRemaining: Int,
    isVerifying: Boolean,
    isSending: Boolean,
    onResend: () -> Unit,
    errorMessage: String?,
    onVerify: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color(0xFF242526), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Sms,
            contentDescription = "SMS Verification",
            tint = Color(0xFF1877F2),
            modifier = Modifier.size(34.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "Enter 6-digit Code"
            "தமிழ்" -> "6 இலக்க குறியீட்டை உள்ளிடவும்"
            else -> "SMS තහවුරු කිරීමේ කේතය"
        },
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (selectedLanguage) {
            "English" -> "We sent a 6-digit code via SMS to ${PhoneAuthHelper.maskPhoneNumber(phoneNumber)}"
            "தமிழ்" -> "${PhoneAuthHelper.maskPhoneNumber(phoneNumber)} என்ற எண்ணுக்கு SMS மூலம் குறியீடு அனுப்பப்பட்டது"
            else -> "${PhoneAuthHelper.maskPhoneNumber(phoneNumber)} අංකයට SMS මඟින් ලැබුණු ඉලක්කම් 6 කේතය ඇතුළත් කරන්න"
        },
        fontSize = 13.sp,
        color = Color(0xFFB0B3B8),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 6-digit OTP Display Boxes
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        for (i in 0 until 6) {
            val char = otpCode.getOrNull(i)?.toString() ?: ""
            val isFocused = otpCode.length == i
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFF242526), RoundedCornerShape(8.dp))
                    .border(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = if (isFocused) Color(0xFF1877F2) else if (char.isNotEmpty()) Color(0xFF42B72A) else Color(0xFF393A3B),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Real text field for OTP entry
    OutlinedTextField(
        value = otpCode,
        onValueChange = { input ->
            if (input.all { it.isDigit() } && input.length <= 6) {
                onOtpChange(input)
            }
        },
        label = {
            Text(
                text = "Enter 6-digit SMS OTP",
                fontSize = 12.sp
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_phone_otp_input"),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF242526),
            unfocusedContainerColor = Color(0xFF242526),
            focusedBorderColor = Color(0xFF1877F2),
            unfocusedBorderColor = Color(0xFF393A3B),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Live Expiry Timer and Attempt Counter
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Timer,
                contentDescription = null,
                tint = if (expirySeconds > 30) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            val minutes = expirySeconds / 60
            val seconds = expirySeconds % 60
            Text(
                text = if (expirySeconds > 0) {
                    "Expires in: %02d:%02d".format(minutes, seconds)
                } else {
                    "Code expired"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (expirySeconds > 30) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }

        Text(
            text = "Attempts left: $attemptsRemaining",
            fontSize = 12.sp,
            color = if (attemptsRemaining > 2) Color(0xFF94A3B8) else Color(0xFFEF4444),
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Resend OTP Button with Cooldown
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onResend,
            enabled = resendCooldownSeconds == 0 && !isSending,
            modifier = Modifier.testTag("reg_phone_otp_resend_button")
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (resendCooldownSeconds == 0) Color(0xFF1877F2) else Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (resendCooldownSeconds > 0) {
                    "Resend code in ${resendCooldownSeconds}s"
                } else {
                    "Resend SMS Code"
                },
                fontSize = 13.sp,
                color = if (resendCooldownSeconds == 0) Color(0xFF1877F2) else Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        ErrorBanner(errorMessage)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onVerify,
        enabled = otpCode.length == 6 && expirySeconds > 0 && !isVerifying,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("reg_phone_otp_verify_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = if (isVerifying) "Verifying Code..." else "Verify & Continue",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
