package com.example.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.auth.FirebaseAuthManager
import com.example.auth.PhoneAuthHelper
import kotlinx.coroutines.delay

enum class SecuritySubScreen {
    OVERVIEW,
    LINK_PHONE,
    LINK_EMAIL,
    CHANGE_PASSWORD
}

@Composable
fun AccountSecurityLinkingModal(
    onDismiss: () -> Unit,
    onLogoutRequested: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    val linkedProviders by FirebaseAuthManager.linkedProviders.collectAsState()
    var currentSubScreen by remember { mutableStateOf(SecuritySubScreen.OVERVIEW) }

    // Dialog state
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf<String?>(null) }
    var actionSuccess by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Subscreen States - Phone Linking
    var phoneInput by remember { mutableStateOf("+94 ") }
    var phoneOtpCode by remember { mutableStateOf("") }
    var phoneVerificationId by remember { mutableStateOf<String?>(null) }
    var isPhoneOtpSent by remember { mutableStateOf(false) }
    var cooldownTimer by remember { mutableIntStateOf(0) }

    // Subscreen States - Email Linking
    var emailInput by remember { mutableStateOf("") }
    var emailPasswordInput by remember { mutableStateOf("") }

    // Subscreen States - Change Password
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var isPassVisible by remember { mutableStateOf(false) }

    // Cooldown countdown effect
    LaunchedEffect(cooldownTimer) {
        if (cooldownTimer > 0) {
            delay(1000)
            cooldownTimer -= 1
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentSubScreen != SecuritySubScreen.OVERVIEW) {
                                currentSubScreen = SecuritySubScreen.OVERVIEW
                                actionError = null
                                actionSuccess = null
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (currentSubScreen) {
                                SecuritySubScreen.OVERVIEW -> "Account Security & Credentials"
                                SecuritySubScreen.LINK_PHONE -> "Link Phone Number"
                                SecuritySubScreen.LINK_EMAIL -> "Link Email Address"
                                SecuritySubScreen.CHANGE_PASSWORD -> "Change Account Password"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Multi-factor authentication & session control",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Error / Success Banners
                AnimatedVisibility(visible = actionError != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFCA5A5))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(actionError ?: "", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                AnimatedVisibility(visible = actionSuccess != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF6EE7B7))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(actionSuccess ?: "", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    when (currentSubScreen) {
                        SecuritySubScreen.OVERVIEW -> {
                            // Section: Linked Credentials
                            Text(
                                text = "LINKED AUTHENTICATION METHODS",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            // Phone Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Mobile Phone", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(
                                                text = if (linkedProviders.hasPhone) linkedProviders.phone ?: "Linked" else "Not Linked",
                                                color = if (linkedProviders.hasPhone) Color(0xFF10B981) else Color(0xFF94A3B8),
                                                fontSize = 13.sp
                                            )
                                        }

                                        if (linkedProviders.hasPhone) {
                                            if (linkedProviders.canUnlinkPhone) {
                                                TextButton(
                                                    onClick = {
                                                        isProcessing = true
                                                        actionError = null
                                                        FirebaseAuthManager.unlinkPhone(
                                                            onSuccess = {
                                                                isProcessing = false
                                                                actionSuccess = "Phone number removed successfully."
                                                            },
                                                            onError = { err ->
                                                                isProcessing = false
                                                                actionError = err
                                                            }
                                                        )
                                                    },
                                                    enabled = !isProcessing
                                                ) {
                                                    Text("Unlink", color = Color(0xFFEF4444), fontSize = 12.sp)
                                                }
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    currentSubScreen = SecuritySubScreen.LINK_PHONE
                                                    actionError = null
                                                    actionSuccess = null
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("+ Link Phone", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Email Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Email Address", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (linkedProviders.hasEmail) linkedProviders.email ?: "Linked" else "Not Linked",
                                                    color = if (linkedProviders.hasEmail) Color(0xFF10B981) else Color(0xFF94A3B8),
                                                    fontSize = 13.sp
                                                )
                                                if (linkedProviders.hasEmail) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (linkedProviders.isEmailVerified) "✓ Verified" else "⚠ Unverified",
                                                        color = if (linkedProviders.isEmailVerified) Color(0xFF10B981) else Color(0xFFF59E0B),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        if (linkedProviders.hasEmail) {
                                            if (linkedProviders.canUnlinkEmail) {
                                                TextButton(
                                                    onClick = {
                                                        isProcessing = true
                                                        actionError = null
                                                        FirebaseAuthManager.unlinkEmail(
                                                            onSuccess = {
                                                                isProcessing = false
                                                                actionSuccess = "Email removed successfully."
                                                            },
                                                            onError = { err ->
                                                                isProcessing = false
                                                                actionError = err
                                                            }
                                                        )
                                                    },
                                                    enabled = !isProcessing
                                                ) {
                                                    Text("Unlink", color = Color(0xFFEF4444), fontSize = 12.sp)
                                                }
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    currentSubScreen = SecuritySubScreen.LINK_EMAIL
                                                    actionError = null
                                                    actionSuccess = null
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("+ Link Email", fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    if (linkedProviders.hasEmail && !linkedProviders.isEmailVerified) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(
                                            onClick = {
                                                FirebaseAuthManager.sendEmailVerification(
                                                    onSuccess = {
                                                        actionSuccess = "Verification email sent to ${linkedProviders.email}."
                                                    },
                                                    onError = { err -> actionError = err }
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Send Email Verification Link", fontSize = 12.sp, color = Color(0xFFF59E0B))
                                        }
                                    }
                                }
                            }

                            // Actions
                            Text(
                                text = "SECURITY CONTROLS",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )

                            // Change Password Button
                            OutlinedButton(
                                onClick = {
                                    currentSubScreen = SecuritySubScreen.CHANGE_PASSWORD
                                    actionError = null
                                    actionSuccess = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Account Password")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Logout Button
                            Button(
                                onClick = { showLogoutConfirmDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Log Out Completely")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Delete Account Button
                            OutlinedButton(
                                onClick = { showDeleteConfirmDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7F1D1D))
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Permanently Delete Account")
                            }
                        }

                        SecuritySubScreen.LINK_PHONE -> {
                            Text(
                                text = "Link a phone number to your account to sign in with SMS verification from any authorized device.",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Phone Number (+94 ...)") },
                                placeholder = { Text("+94 77 123 4567") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (activity != null) {
                                        isProcessing = true
                                        actionError = null
                                        FirebaseAuthManager.sendPhoneOtp(
                                            activity = activity,
                                            rawPhoneNumber = phoneInput,
                                            onCodeSent = { vid ->
                                                isProcessing = false
                                                phoneVerificationId = vid
                                                isPhoneOtpSent = true
                                                cooldownTimer = 60
                                                actionSuccess = "SMS verification code dispatched!"
                                            },
                                            onError = { err ->
                                                isProcessing = false
                                                actionError = err
                                            }
                                        )
                                    } else {
                                        actionError = "Unable to access Activity for SMS verification."
                                    }
                                },
                                enabled = !isProcessing && cooldownTimer == 0,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text(if (cooldownTimer > 0) "Resend in ${cooldownTimer}s" else "Send SMS Verification Code")
                            }

                            if (isPhoneOtpSent) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Text("Enter the 6-digit code received via SMS:", color = Color.White, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = phoneOtpCode,
                                    onValueChange = { if (it.length <= 6) phoneOtpCode = it },
                                    label = { Text("6-Digit OTP Code") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF10B981),
                                        unfocusedBorderColor = Color(0xFF475569),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val vid = phoneVerificationId
                                        if (vid != null) {
                                            isProcessing = true
                                            actionError = null
                                            FirebaseAuthManager.linkPhoneCredential(
                                                verificationId = vid,
                                                otpCode = phoneOtpCode,
                                                onSuccess = {
                                                    isProcessing = false
                                                    actionSuccess = "Phone number linked successfully!"
                                                    currentSubScreen = SecuritySubScreen.OVERVIEW
                                                },
                                                onError = { err ->
                                                    isProcessing = false
                                                    actionError = err
                                                }
                                            )
                                        }
                                    },
                                    enabled = !isProcessing && phoneOtpCode.length == 6,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("Verify & Link Phone Number")
                                }
                            }
                        }

                        SecuritySubScreen.LINK_EMAIL -> {
                            Text(
                                text = "Link an email address and password to your account for multi-device access.",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFA855F7),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = emailPasswordInput,
                                onValueChange = { emailPasswordInput = it },
                                label = { Text("Create Password (min 6 chars)") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFA855F7),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    isProcessing = true
                                    actionError = null
                                    FirebaseAuthManager.linkEmailCredential(
                                        email = emailInput,
                                        pass = emailPasswordInput,
                                        onSuccess = {
                                            isProcessing = false
                                            actionSuccess = "Email linked successfully! A verification email has been dispatched."
                                            currentSubScreen = SecuritySubScreen.OVERVIEW
                                        },
                                        onError = { err ->
                                            isProcessing = false
                                            actionError = err
                                        }
                                    )
                                },
                                enabled = !isProcessing && emailInput.isNotBlank() && emailPasswordInput.length >= 6,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) {
                                Text("Link Email to Account")
                            }
                        }

                        SecuritySubScreen.CHANGE_PASSWORD -> {
                            OutlinedTextField(
                                value = newPasswordInput,
                                onValueChange = { newPasswordInput = it },
                                label = { Text("New Password") },
                                singleLine = true,
                                visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { isPassVisible = !isPassVisible }) {
                                        Icon(if (isPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.White)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = confirmPasswordInput,
                                onValueChange = { confirmPasswordInput = it },
                                label = { Text("Confirm New Password") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (newPasswordInput != confirmPasswordInput) {
                                        actionError = "Passwords do not match."
                                        return@Button
                                    }
                                    isProcessing = true
                                    actionError = null
                                    FirebaseAuthManager.changePassword(
                                        newPassword = newPasswordInput,
                                        onSuccess = {
                                            isProcessing = false
                                            actionSuccess = "Password updated successfully!"
                                            currentSubScreen = SecuritySubScreen.OVERVIEW
                                        },
                                        onError = { err ->
                                            isProcessing = false
                                            actionError = err
                                        }
                                    )
                                },
                                enabled = !isProcessing && newPasswordInput.length >= 6,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text("Update Password")
                            }
                        }
                    }
                }
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("Log Out Completely?", fontWeight = FontWeight.Bold) },
            text = { Text("You will be signed out from this device. All encrypted sessions will be securely cleared.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogoutRequested()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Permanently Delete Account?", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)) },
            text = { Text("This will permanently erase your profile, friends list, messages, and linked credentials. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        FirebaseAuthManager.deleteAccount(
                            onSuccess = {
                                Toast.makeText(context, "Account permanently deleted.", Toast.LENGTH_LONG).show()
                                onLogoutRequested()
                            },
                            onError = { err -> actionError = err }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
