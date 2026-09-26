package com.example.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.auth.FirebaseAuthManager
import com.example.auth.PhoneAuthHelper
import com.example.model.User
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun AccountLinkingDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val currentUser by viewModel.currentUser.collectAsState()
    val linkedProviders by FirebaseAuthManager.linkedProviders.collectAsState()

    // Form mode states
    var isAddingPhone by remember { mutableStateOf(false) }
    var isAddingEmail by remember { mutableStateOf(false) }
    var isChangingEmail by remember { mutableStateOf(false) }

    // Phone states
    var newPhoneInput by remember { mutableStateOf("") }
    var phoneVerificationId by remember { mutableStateOf<String?>(null) }
    var otpCodeInput by remember { mutableStateOf("") }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpCooldownSeconds by remember { mutableIntStateOf(0) }
    var otpExpirySeconds by remember { mutableIntStateOf(120) }
    var phoneErrorMessage by remember { mutableStateOf<String?>(null) }

    // Email states
    var newEmailInput by remember { mutableStateOf("") }
    var emailPasswordInput by remember { mutableStateOf("") }
    var isLinkingEmail by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf<String?>(null) }

    // Confirmation dialog states
    var showUnlinkPhoneConfirm by remember { mutableStateOf(false) }
    var showUnlinkEmailConfirm by remember { mutableStateOf(false) }

    // Cooldown countdown
    LaunchedEffect(otpCooldownSeconds) {
        if (otpCooldownSeconds > 0) {
            delay(1000L)
            otpCooldownSeconds--
        }
    }

    // Expiry countdown
    LaunchedEffect(otpExpirySeconds, phoneVerificationId) {
        if (phoneVerificationId != null && otpExpirySeconds > 0) {
            delay(1000L)
            otpExpirySeconds--
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ගිණුම් සම්බන්ධ කිරීම",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Account Linking & Credentials",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Text("✕", color = Color.Gray, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Explanatory Info Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "දුරකථන අංකය සහ විද්‍යුත් තැපෑල දෙකම එකම ගිණුමට සම්බන්ධ කිරීමෙන් ඕනෑම ක්‍රමයකින් ආරක්ෂිතව ප්‍රවේශ විය හැක. වෙනම ගිණුම් සෑදීමක් සිදු නොවේ.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // SECTION 1: PHONE NUMBER CREDENTIAL
                // ==========================================
                Text(
                    text = "දුරකථන අංකය (Phone Number)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val hasLinkedPhone = linkedProviders.hasPhone || currentUser.phone.isNotBlank()
                val currentPhone = linkedProviders.phone ?: currentUser.phone

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (hasLinkedPhone) Color(0xFF10B981) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (hasLinkedPhone) currentPhone else "දුරකථන අංකයක් සම්බන්ධ කර නැත",
                                        color = if (hasLinkedPhone) Color.White else Color(0xFF94A3B8),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (hasLinkedPhone) {
                                        Text(
                                            text = "තහවුරු කරන ලද අංකයකි • Verified",
                                            color = Color(0xFF10B981),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            if (hasLinkedPhone) {
                                Row {
                                    IconButton(
                                        onClick = {
                                            isAddingPhone = !isAddingPhone
                                            phoneErrorMessage = null
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Change Phone",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { showUnlinkPhoneConfirm = true },
                                        enabled = linkedProviders.canUnlinkPhone,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Unlink Phone",
                                            tint = if (linkedProviders.canUnlinkPhone) Color(0xFFEF4444) else Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { isAddingPhone = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("එක් කරන්න", fontSize = 12.sp)
                                }
                            }
                        }

                        // Form to add or change phone number
                        AnimatedVisibility(visible = isAddingPhone) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (hasLinkedPhone) "නව දුරකථන අංකය ඇතුළත් කරන්න:" else "සම්බන්ධ කිරීමට දුරකථන අංකය:",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newPhoneInput,
                                    onValueChange = {
                                        newPhoneInput = it
                                        phoneErrorMessage = null
                                    },
                                    placeholder = { Text("+94 77 123 4567", color = Color(0xFF64748B), fontSize = 13.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("link_phone_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF475569),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { isAddingPhone = false; phoneVerificationId = null }) {
                                        Text("අවලංගු කරන්න", color = Color.Gray, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (activity == null) {
                                                phoneErrorMessage = "Activity context required for SMS verification"
                                                return@Button
                                            }
                                            val formatted = PhoneAuthHelper.formatToE164(newPhoneInput)
                                            if (!PhoneAuthHelper.isValidPhoneNumber(formatted)) {
                                                phoneErrorMessage = "වලංගු දුරකථන අංකයක් ඇතුළත් කරන්න (e.g. +94 77 123 4567)"
                                                return@Button
                                            }
                                            isSendingOtp = true
                                            phoneErrorMessage = null
                                            FirebaseAuthManager.sendPhoneOtp(
                                                activity = activity,
                                                rawPhoneNumber = formatted,
                                                onCodeSent = { vid ->
                                                    isSendingOtp = false
                                                    phoneVerificationId = vid
                                                    otpCooldownSeconds = 60
                                                    otpExpirySeconds = 120
                                                    Toast.makeText(context, "OTP කේතය ඔබගේ දුරකථනයට එවන ලදී.", Toast.LENGTH_SHORT).show()
                                                },
                                                onError = { err ->
                                                    isSendingOtp = false
                                                    phoneErrorMessage = err
                                                }
                                            )
                                        },
                                        enabled = !isSendingOtp && otpCooldownSeconds == 0 && newPhoneInput.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("send_phone_link_otp_button")
                                    ) {
                                        if (isSendingOtp) {
                                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("යවමින්...", fontSize = 13.sp)
                                        } else {
                                            Text(
                                                if (otpCooldownSeconds > 0) "නැවත එවන්න (${otpCooldownSeconds}s)" else "OTP කේතය එවන්න",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // OTP verification section if code was sent
                                AnimatedVisibility(visible = phoneVerificationId != null) {
                                    Column(modifier = Modifier.padding(top = 10.dp)) {
                                        Text(
                                            text = "ලැබුණු ඉලක්කම් 6ක OTP කේතය ඇතුළත් කරන්න (${otpExpirySeconds}s වලංගුයි):",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                            value = otpCodeInput,
                                            onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) otpCodeInput = it },
                                            placeholder = { Text("123456", color = Color(0xFF64748B)) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("link_otp_input"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF10B981),
                                                unfocusedBorderColor = Color(0xFF475569),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = {
                                                val vid = phoneVerificationId ?: return@Button
                                                if (otpCodeInput.length != 6) {
                                                    phoneErrorMessage = "කරුණාකර ඉලක්කම් 6ම ඇතුළත් කරන්න."
                                                    return@Button
                                                }
                                                isVerifyingOtp = true
                                                phoneErrorMessage = null
                                                FirebaseAuthManager.linkPhoneCredential(
                                                    verificationId = vid,
                                                    otpCode = otpCodeInput,
                                                    onSuccess = {
                                                        isVerifyingOtp = false
                                                        isAddingPhone = false
                                                        phoneVerificationId = null
                                                        val formatted = PhoneAuthHelper.formatToE164(newPhoneInput)
                                                        viewModel.updateUserProfile(currentUser.copy(phone = formatted))
                                                        Toast.makeText(context, "දුරකථන අංකය සාර්ථකව සම්බන්ධ කරන ලදී! ✅", Toast.LENGTH_LONG).show()
                                                    },
                                                    onError = { err ->
                                                        isVerifyingOtp = false
                                                        phoneErrorMessage = err
                                                    }
                                                )
                                            },
                                            enabled = !isVerifyingOtp && otpCodeInput.length == 6,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("confirm_link_phone_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            if (isVerifyingOtp) {
                                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("තහවුරු කරමින්...", fontSize = 13.sp)
                                            } else {
                                                Text("තහවුරු කර සම්බන්ධ කරන්න", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }

                                if (phoneErrorMessage != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = phoneErrorMessage!!,
                                        color = Color(0xFFEF4444),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // SECTION 2: EMAIL ADDRESS CREDENTIAL
                // ==========================================
                Text(
                    text = "විද්‍යුත් තැපෑල (Email Address)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val hasLinkedEmail = linkedProviders.hasEmail || currentUser.email.isNotBlank()
                val currentEmail = linkedProviders.email ?: currentUser.email

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (hasLinkedEmail) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (hasLinkedEmail) currentEmail else "ඊමේල් ලිපිනයක් සම්බන්ධ කර නැත",
                                        color = if (hasLinkedEmail) Color.White else Color(0xFF94A3B8),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (hasLinkedEmail) {
                                        Text(
                                            text = if (linkedProviders.isEmailVerified) "තහවුරු කර ඇත • Verified" else "තහවුරු කර නොමැත • Unverified",
                                            color = if (linkedProviders.isEmailVerified) Color(0xFF10B981) else Color(0xFFF59E0B),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            if (hasLinkedEmail) {
                                Row {
                                    IconButton(
                                        onClick = {
                                            isChangingEmail = !isChangingEmail
                                            emailErrorMessage = null
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Change Email",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { showUnlinkEmailConfirm = true },
                                        enabled = linkedProviders.canUnlinkEmail,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Unlink Email",
                                            tint = if (linkedProviders.canUnlinkEmail) Color(0xFFEF4444) else Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { isAddingEmail = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("එක් කරන්න", fontSize = 12.sp)
                                }
                            }
                        }

                        // Resend verification email if unverified
                        if (hasLinkedEmail && !linkedProviders.isEmailVerified) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "තහවුරු කිරීමේ සබැඳිය නැවත එවන්නද?",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                                OutlinedButton(
                                    onClick = {
                                        FirebaseAuthManager.sendEmailVerification(
                                            onSuccess = {
                                                Toast.makeText(context, "තහවුරු කිරීමේ ඊමේලය යවන ලදී. ඔබගේ Inbox බලන්න.", Toast.LENGTH_LONG).show()
                                            },
                                            onError = { err ->
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("නැවත එවන්න", fontSize = 11.sp, color = Color(0xFF38BDF8))
                                }
                            }
                        }

                        // Form to link new email + password
                        AnimatedVisibility(visible = isAddingEmail) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "විද්‍යුත් තැපෑල සහ මුරපදය ඇතුළත් කරන්න:",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newEmailInput,
                                    onValueChange = {
                                        newEmailInput = it
                                        emailErrorMessage = null
                                    },
                                    placeholder = { Text("you@example.com", color = Color(0xFF64748B), fontSize = 13.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("link_email_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF475569),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = emailPasswordInput,
                                    onValueChange = {
                                        emailPasswordInput = it
                                        emailErrorMessage = null
                                    },
                                    placeholder = { Text("මුරපදය (අවම වශයෙන් අකුරු 6ක්)", color = Color(0xFF64748B), fontSize = 13.sp) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("link_email_password_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF475569),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { isAddingEmail = false }) {
                                        Text("අවලංගු කරන්න", color = Color.Gray, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val cleanEmail = newEmailInput.trim()
                                            val cleanPass = emailPasswordInput.trim()
                                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                                                emailErrorMessage = "වලංගු ඊමේල් ලිපිනයක් ඇතුළත් කරන්න."
                                                return@Button
                                            }
                                            if (cleanPass.length < 6) {
                                                emailErrorMessage = "මුරපදය අවම වශයෙන් අක්ෂර 6ක් විය යුතුය."
                                                return@Button
                                            }
                                            isLinkingEmail = true
                                            emailErrorMessage = null
                                            FirebaseAuthManager.linkEmailCredential(
                                                email = cleanEmail,
                                                pass = cleanPass,
                                                onSuccess = {
                                                    isLinkingEmail = false
                                                    isAddingEmail = false
                                                    viewModel.updateUserProfile(currentUser.copy(email = cleanEmail))
                                                    Toast.makeText(context, "ඊමේල් ලිපිනය සාර්ථකව සම්බන්ධ කරන ලදී! තහවුරු කිරීමේ පණිවිඩයක් යවා ඇත.", Toast.LENGTH_LONG).show()
                                                },
                                                onError = { err ->
                                                    isLinkingEmail = false
                                                    emailErrorMessage = err
                                                }
                                            )
                                        },
                                        enabled = !isLinkingEmail && newEmailInput.isNotBlank() && emailPasswordInput.length >= 6,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("submit_link_email_button")
                                    ) {
                                        if (isLinkingEmail) {
                                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("සම්බන්ධ කරමින්...", fontSize = 13.sp)
                                        } else {
                                            Text("ඊමේලය සම්බන්ධ කරන්න", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                }

                                if (emailErrorMessage != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = emailErrorMessage!!,
                                        color = Color(0xFFEF4444),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Form to change email address
                        AnimatedVisibility(visible = isChangingEmail) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "නව ඊමේල් ලිපිනය ඇතුළත් කරන්න:",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newEmailInput,
                                    onValueChange = {
                                        newEmailInput = it
                                        emailErrorMessage = null
                                    },
                                    placeholder = { Text("new.email@example.com", color = Color(0xFF64748B), fontSize = 13.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF475569),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { isChangingEmail = false }) {
                                        Text("අවලංගු කරන්න", color = Color.Gray, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val clean = newEmailInput.trim()
                                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(clean).matches()) {
                                                emailErrorMessage = "වලංගු ඊමේල් ලිපිනයක් ඇතුළත් කරන්න."
                                                return@Button
                                            }
                                            isLinkingEmail = true
                                            emailErrorMessage = null
                                            FirebaseAuthManager.changeEmail(
                                                newEmail = clean,
                                                onSuccess = {
                                                    isLinkingEmail = false
                                                    isChangingEmail = false
                                                    Toast.makeText(context, "නව ඊමේල් ලිපිනය තහවුරු කිරීමට සබැඳියක් එවා ඇත.", Toast.LENGTH_LONG).show()
                                                },
                                                onError = { err ->
                                                    isLinkingEmail = false
                                                    emailErrorMessage = err
                                                }
                                            )
                                        },
                                        enabled = !isLinkingEmail && newEmailInput.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("ඊමේලය වෙනස් කරන්න", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (emailErrorMessage != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = emailErrorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Security Note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ගිණුමට ප්‍රවේශය අහිමිවීම වැළැක්වීමට අවම වශයෙන් එක් ක්‍රමයක් සම්බන්ධ කර තබාගත යුතුය.",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("සම්පූර්ණයි (Close)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Unlink Phone Confirmation Dialog
    if (showUnlinkPhoneConfirm) {
        AlertDialog(
            onDismissRequest = { showUnlinkPhoneConfirm = false },
            containerColor = Color(0xFF1E293B),
            title = { Text("දුරකථන අංකය ඉවත් කරන්නද?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("ඔබගේ ගිණුමෙන් මෙම දුරකථන අංකය ඉවත් කිරීමට අවශ්‍ය බව විශ්වාසද? ඔබට තවදුරටත් SMS OTP මඟින් ප්‍රවේශ විය නොහැක.", color = Color(0xFFCBD5E1)) },
            confirmButton = {
                Button(
                    onClick = {
                        showUnlinkPhoneConfirm = false
                        FirebaseAuthManager.unlinkPhone(
                            onSuccess = {
                                viewModel.updateUserProfile(currentUser.copy(phone = ""))
                                Toast.makeText(context, "දුරකථන අංකය ඉවත් කරන ලදී.", Toast.LENGTH_SHORT).show()
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("ඉවත් කරන්න")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlinkPhoneConfirm = false }) {
                    Text("අවලංගු කරන්න", color = Color.Gray)
                }
            }
        )
    }

    // Unlink Email Confirmation Dialog
    if (showUnlinkEmailConfirm) {
        AlertDialog(
            onDismissRequest = { showUnlinkEmailConfirm = false },
            containerColor = Color(0xFF1E293B),
            title = { Text("ඊමේල් ලිපිනය ඉවත් කරන්නද?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("ඔබගේ ගිණුමෙන් මෙම ඊමේල් ලිපිනය ඉවත් කිරීමට අවශ්‍ය බව විශ්වාසද? ඔබට තවදුරටත් ඊමේලය සහ මුරපදය මඟින් ප්‍රවේශ විය නොහැක.", color = Color(0xFFCBD5E1)) },
            confirmButton = {
                Button(
                    onClick = {
                        showUnlinkEmailConfirm = false
                        FirebaseAuthManager.unlinkEmail(
                            onSuccess = {
                                viewModel.updateUserProfile(currentUser.copy(email = ""))
                                Toast.makeText(context, "ඊමේල් ලිපිනය ඉවත් කරන ලදී.", Toast.LENGTH_SHORT).show()
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("ඉවත් කරන්න")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlinkEmailConfirm = false }) {
                    Text("අවලංගු කරන්න", color = Color.Gray)
                }
            }
        )
    }
}
