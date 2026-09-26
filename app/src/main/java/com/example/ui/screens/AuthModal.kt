package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.auth.FirebaseAuthManager
import com.example.auth.PhoneAuthHelper
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import kotlinx.coroutines.delay

@Composable
fun AuthModal(
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedAuthTab by remember { mutableIntStateOf(1) } // 0: Email/Pass, 1: Phone OTP

    // Phone Auth States
    var phoneNumber by remember { mutableStateOf("+94 77 123 4567") }
    var otpCode by remember { mutableStateOf("") }
    var phoneVerificationId by remember { mutableStateOf<String?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var resendCooldownSeconds by remember { mutableIntStateOf(0) }
    var otpExpirySeconds by remember { mutableIntStateOf(120) }

    // Email Auth States
    var email by remember { mutableStateOf("user@friendhub.app") }
    var password by remember { mutableStateOf("") }
    var isSigningInEmail by remember { mutableStateOf(false) }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isErrorMessage by remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(false) }

    // Resend Cooldown Timer
    LaunchedEffect(resendCooldownSeconds) {
        if (resendCooldownSeconds > 0) {
            delay(1000L)
            resendCooldownSeconds--
        }
    }

    // OTP Expiration Timer
    LaunchedEffect(otpExpirySeconds, phoneVerificationId) {
        if (phoneVerificationId != null && otpExpirySeconds > 0) {
            delay(1000L)
            otpExpirySeconds--
        }
    }

    fun sendPhoneOtp() {
        if (activity == null) {
            statusMessage = "Activity context is required for SMS verification."
            isErrorMessage = true
            return
        }
        val formatted = PhoneAuthHelper.formatToE164(phoneNumber)
        if (!PhoneAuthHelper.isValidPhoneNumber(formatted)) {
            statusMessage = "Please enter a valid phone number (e.g. +94 77 123 4567)."
            isErrorMessage = true
            return
        }

        isSendingOtp = true
        statusMessage = "Sending real-time SMS OTP via Firebase..."
        isErrorMessage = false

        FirebaseAuthManager.sendPhoneOtp(
            activity = activity,
            rawPhoneNumber = formatted,
            onCodeSent = { vid ->
                isSendingOtp = false
                phoneVerificationId = vid
                resendCooldownSeconds = 60
                otpExpirySeconds = 120
                statusMessage = "SMS verification code sent to $formatted."
                isErrorMessage = false
                Toast.makeText(context, "OTP sent to $formatted", Toast.LENGTH_SHORT).show()
            },
            onError = { err ->
                isSendingOtp = false
                statusMessage = err
                isErrorMessage = true
            }
        )
    }

    fun verifyOtp() {
        val vid = phoneVerificationId
        if (vid == null) {
            statusMessage = "Please request an OTP code first."
            isErrorMessage = true
            return
        }
        if (otpCode.length != 6) {
            statusMessage = "Please enter all 6 digits of the OTP code."
            isErrorMessage = true
            return
        }

        isVerifyingOtp = true
        statusMessage = "Verifying code with Firebase Authentication..."
        isErrorMessage = false

        FirebaseAuthManager.verifyPhoneOtp(
            verificationId = vid,
            otpCode = otpCode,
            onSuccess = { fbUser, isNewUser ->
                isVerifyingOtp = false
                isAuthenticated = true
                statusMessage = "Phone verified successfully! User: ${fbUser.phoneNumber ?: fbUser.uid}"
                isErrorMessage = false
                onLoginSuccess()
            },
            onError = { err ->
                isVerifyingOtp = false
                statusMessage = err
                isErrorMessage = true
            }
        )
    }

    fun signInWithEmail() {
        val cleanEmail = email.trim()
        val cleanPass = password.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            statusMessage = "Please enter a valid email address."
            isErrorMessage = true
            return
        }
        if (cleanPass.length < 6) {
            statusMessage = "Password must be at least 6 characters."
            isErrorMessage = true
            return
        }

        isSigningInEmail = true
        statusMessage = "Authenticating with Firebase..."
        isErrorMessage = false

        FirebaseAuthManager.loginWithEmail(
            email = cleanEmail,
            pass = cleanPass,
            onSuccess = { fbUser, isEmailVerified ->
                isSigningInEmail = false
                isAuthenticated = true
                statusMessage = "Signed in successfully as ${fbUser.email}!"
                isErrorMessage = false
                onLoginSuccess()
            },
            onError = { err ->
                isSigningInEmail = false
                statusMessage = err
                isErrorMessage = true
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OledSurface),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.horizontalGradient(listOf(OledCardBorder, OledCardBorder)))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Reverse Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Auth", tint = NeonBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Firebase Authentication",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode selector
                TabRow(
                    selectedTabIndex = selectedAuthTab,
                    containerColor = OledSurfaceVariant,
                    contentColor = NeonBlue,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                            color = NeonBlue
                        )
                    }
                ) {
                    Tab(
                        selected = selectedAuthTab == 0,
                        onClick = {
                            selectedAuthTab = 0
                            statusMessage = null
                            isErrorMessage = false
                        },
                        text = { Text("Email Sign-In", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedAuthTab == 0) NeonBlue else Color.Gray) }
                    )
                    Tab(
                        selected = selectedAuthTab == 1,
                        onClick = {
                            selectedAuthTab = 1
                            statusMessage = null
                            isErrorMessage = false
                        },
                        text = { Text("Phone OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedAuthTab == 1) NeonBlue else Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isAuthenticated) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Authentication Successful! 🎉",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = statusMessage ?: "Signed in securely.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonPurple,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue, contentColor = Color.Black)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (selectedAuthTab == 1) {
                    // OTP Verification Screen
                    if (phoneVerificationId == null) {
                        // Step 1: Input Phone Number
                        Text(
                            text = "Phone Verification Code (OTP)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Enter your mobile phone number (Sri Lanka +94 or international) to receive an OTP code.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it; statusMessage = null },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+94 77 123 4567") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeonBlue) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_phone_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = OledCardBorder,
                                focusedContainerColor = OledSurfaceVariant,
                                unfocusedContainerColor = OledSurfaceVariant,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        if (statusMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = statusMessage!!,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (isErrorMessage) Color(0xFFEF4444) else NeonPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { sendPhoneOtp() },
                            enabled = !isSendingOtp && phoneNumber.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("send_otp_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue, contentColor = Color.Black)
                        ) {
                            if (isSendingOtp) {
                                CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sending SMS...", fontWeight = FontWeight.Bold)
                            } else {
                                Text("Send OTP Verification Code 📲", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Step 2: Input 6-Digit OTP Code
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Enter 6-Digit Verification Code",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            TextButton(
                                onClick = { sendPhoneOtp() },
                                enabled = resendCooldownSeconds == 0 && !isSendingOtp
                            ) {
                                Text(
                                    text = if (resendCooldownSeconds > 0) "Resend (${resendCooldownSeconds}s)" else "Resend OTP",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (resendCooldownSeconds == 0) NeonBlue else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Text(
                            text = "OTP sent to $phoneNumber (Expires in ${otpExpirySeconds}s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) { otpCode = it; statusMessage = null } },
                            label = { Text("OTP Verification Code") },
                            placeholder = { Text("123456") },
                            leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = NeonBlue) },
                            modifier = Modifier.fillMaxWidth().testTag("otp_code_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = OledCardBorder,
                                focusedContainerColor = OledSurfaceVariant,
                                unfocusedContainerColor = OledSurfaceVariant,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        if (statusMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = statusMessage!!,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (isErrorMessage) Color(0xFFEF4444) else NeonPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { verifyOtp() },
                            enabled = !isVerifyingOtp && otpCode.length == 6,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_otp_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue, contentColor = Color.Black)
                        ) {
                            if (isVerifyingOtp) {
                                CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verifying...", fontWeight = FontWeight.Bold)
                            } else {
                                Text("Verify & Sign In 🔐", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "← Back to Phone Number",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            modifier = Modifier.clickable { phoneVerificationId = null; otpCode = "" }
                        )
                    }
                } else {
                    // Email & Password Auth
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; statusMessage = null },
                        placeholder = { Text("Email address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonBlue) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = OledCardBorder,
                            focusedContainerColor = OledSurfaceVariant,
                            unfocusedContainerColor = OledSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; statusMessage = null },
                        placeholder = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonBlue) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = OledCardBorder,
                            focusedContainerColor = OledSurfaceVariant,
                            unfocusedContainerColor = OledSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = statusMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isErrorMessage) Color(0xFFEF4444) else NeonPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { signInWithEmail() },
                        enabled = !isSigningInEmail && email.isNotBlank() && password.length >= 6,
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sign_in_button"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue, contentColor = Color.Black)
                    ) {
                        if (isSigningInEmail) {
                            CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Signing In...", fontWeight = FontWeight.Bold)
                        } else {
                            Text("Sign In with Email 📧", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
