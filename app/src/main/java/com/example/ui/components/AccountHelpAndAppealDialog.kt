package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.User
import kotlin.random.Random

@Composable
fun AccountHelpAndAppealDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Help Center, 1: Ban Appeal, 2: Contact

    // Ban Appeal Form States
    var contactInfo by remember { mutableStateOf(currentUser.email.ifBlank { currentUser.phone }) }
    var appealReason by remember { mutableStateOf("") }
    var selectedBanType by remember { mutableStateOf("ගිණුම තාවකාලිකව අත්හිටුවීම (Suspension)") }
    var submittedCaseId by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("account_help_appeal_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E1F21),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3B3C))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1877F2).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Support",
                            tint = Color(0xFF1877F2),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FriendHub උපකාර සහ සහාය මධ්‍යස්ථානය",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Help Center & Account Ban Appeal",
                            fontSize = 11.sp,
                            color = Color(0xFFB0B3B8)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFB0B3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF242526),
                    contentColor = Color(0xFF1877F2),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF1877F2)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("උපකාර (Help)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Ban Appeal (අභියාචනා)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("සම්බන්ධ වන්න", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Contents
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Help Center FAQs
                            HelpFaqSection()
                        }
                        1 -> {
                            // Account Ban Appeal System
                            AccountBanAppealSection(
                                contactInfo = contactInfo,
                                onContactChange = { contactInfo = it },
                                appealReason = appealReason,
                                onReasonChange = { appealReason = it },
                                selectedBanType = selectedBanType,
                                onSelectBanType = { selectedBanType = it },
                                submittedCaseId = submittedCaseId,
                                onSubmit = {
                                    if (contactInfo.isBlank() || appealReason.isBlank()) {
                                        Toast.makeText(context, "කරුණාකර සියලු විස්තර සම්පූර්ණ කරන්න.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val newId = "FH-CASE-${Random.nextInt(10000, 99999)}"
                                        submittedCaseId = newId
                                        Toast.makeText(context, "අභියාචනය යොමු විය! Case ID: $newId", Toast.LENGTH_LONG).show()
                                        
                                        // Send direct email intent to admin
                                        try {
                                            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:friendhubfriendhub@gmail.com,wk541454@gmail.com")
                                                putExtra(Intent.EXTRA_SUBJECT, "🚨 [Account Appeal - $newId] $selectedBanType")
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "පරිපාලක වෙත ලැබුණු අභියාචනයයි:\n\n• Case ID: $newId\n• පරිශීලක සම්බන්ධතාව: $contactInfo\n• තත්ත්වය: $selectedBanType\n• හේතුව: $appealReason\n\nකරුණාකර පරීක්ෂා කර පද්ධතිය මගින් සුදුසු ක්‍රියාමාර්ගයක් ගන්න."
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(mailIntent, "පරිපාලක වෙත ඊමේල් යවන්න..."))
                                        } catch (e: Exception) {
                                            // Handled gracefully
                                        }
                                    }
                                },
                                onReset = {
                                    submittedCaseId = null
                                    appealReason = ""
                                }
                            )
                        }
                        2 -> {
                            // Official Contact Information
                            SupportContactSection()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpFaqSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "නිතර අසන ප්‍රශ්න සහ පිළිතුරු (FAQs)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        FaqCard(
            question = "ගිණුමක් Ban (අත්හිටුවීමට) හේතු මොනවාද?",
            answer = "1. ප්‍රජා මාර්ගෝපදේශ (Community Guidelines) කඩවීම.\n2. එකම අංකයකින් අවසරලත් සීමාව (ගිණුම් 3) ඉක්මවා ව්‍යාජ ගිණුම් සෑදීම හෝ Spam කිරීම.\n3. අනවසර පිවිසුම් (Suspicious logins) හඳුනාගැනීම නිසා ආරක්ෂාව පිණිස Lock වීම."
        )

        FaqCard(
            question = "මගේ ගිණුම Ban වුවහොත් කළ යුත්තේ කුමක්ද?",
            answer = "අපගේ 'Ban Appeal (අභියාචනා)' පටිත්ත හරහා හෝ නිල WhatsApp අංකය (0719117815) / විද්‍යුත් තැපෑල (friendhubfriendhub@gmail.com) ඔස්සේ ඔබගේ තොරතුරු යොමු කරන්න. අපගේ පරිපාලන කණ්ඩායම පැය 24ක් තුළ එය පරීක්ෂා කර Unban කරනු ලැබේ."
        )

        FaqCard(
            question = "මුරපදය හෝ OTP අමතක වූ විට?",
            answer = "පිවිසුම් තිරයේ 'Forgot Password / මුරපදය අමතකද' තෝරා දුරකථන අංකය හෝ විද්‍යුත් තැපෑල ඇතුළත් කර SMS OTP මගින් ක්ෂණිකව නව මුරපදයක් සකසා ගන්න."
        )
    }
}

@Composable
private fun FaqCard(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2C2E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = null,
                    tint = Color(0xFF1877F2),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = answer,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = Color(0xFFE4E6EB)
            )
        }
    }
}

@Composable
private fun AccountBanAppealSection(
    contactInfo: String,
    onContactChange: (String) -> Unit,
    appealReason: String,
    onReasonChange: (String) -> Unit,
    selectedBanType: String,
    onSelectBanType: (String) -> Unit,
    submittedCaseId: String?,
    onSubmit: () -> Unit,
    onReset: () -> Unit
) {
    if (submittedCaseId != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16321F)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "අභියාචනය සාර්ථකව භාරගන්නා ලදී!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "නඩත්තු අංකය (Case ID): $submittedCaseId",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color(0xFF81C784)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "අපගේ ආරක්ෂක හා පරිපාලන කණ්ඩායම (friendhubfriendhub@gmail.com / WhatsApp: 0719117815) වෙත ඔබගේ අභියාචනය යොමු කරන ලදී. පැය 24ක් තුළ ඔබගේ $contactInfo වෙත ප්‍රතිචාරයක් ලැබෙනු ඇත.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFFC8E6C9)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("නව ඉල්ලීමක් යොමු කරන්න", fontSize = 12.sp)
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF382314)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF57C00)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = "Notice",
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ගිණුම වැරදීමකින් හෝ අසාධාරණ ලෙස අත්හිටුවා ඇත්නම් මෙම පෝරමය මඟින් Administrator වෙත අභියාචනයක් ඉදිරිපත් කරන්න.",
                        fontSize = 12.sp,
                        color = Color(0xFFFFE0B2),
                        lineHeight = 16.sp
                    )
                }
            }

            Text("දුරකථන අංකය හෝ විද්‍යුත් තැපෑල:", fontSize = 12.sp, color = Color(0xFFB0B3B8))
            OutlinedTextField(
                value = contactInfo,
                onValueChange = onContactChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("07XXXXXXXX හෝ ඊමේල් ලිපිනය", fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF4E4F50),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Text("අත්හිටුවීමට ලක්වූ ගැටලුව / හේතුව:", fontSize = 12.sp, color = Color(0xFFB0B3B8))
            OutlinedTextField(
                value = appealReason,
                onValueChange = onReasonChange,
                modifier = Modifier.fillMaxWidth().height(90.dp),
                placeholder = { Text("මගේ ගිණුම කිසිදු නීතිවිරෝධී ක්‍රියාවකට සම්බන්ධ නැති අතර එය වැරදීමකින් Ban වී ඇත. කරුණාකර සමාලෝචනය කර Unban කරන්න...", fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF4E4F50),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "අභියාචනය ඉදිරිපත් කරන්න (Submit Appeal)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SupportContactSection() {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "නිල තාක්ෂණික සහාය සේවාවන් (Official Support)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        // WhatsApp Official Helpdesk
        ContactItem(
            icon = Icons.Default.Chat,
            title = "නිල WhatsApp සහාය (WhatsApp Support)",
            detail = "0719117815 (+94 71 911 7815)",
            subtitle = "ක්ෂණික පණිවිඩ සහාය - පැය 24 පුරා විවෘතයි",
            actionLabel = "WhatsApp Chat",
            actionColor = Color(0xFF25D366),
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/94719117815"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "WhatsApp අංකය: 0719117815", Toast.LENGTH_LONG).show()
                }
            }
        )

        // Email Official Support
        ContactItem(
            icon = Icons.Default.Email,
            title = "නිල සහායක විද්‍යුත් තැපෑල (Support Email)",
            detail = "friendhubfriendhub@gmail.com",
            subtitle = "ගිණුම් ගැටලු, Ban අභියාචනා සහ ආරක්ෂක ගැටලු සඳහා",
            actionLabel = "Email යවන්න",
            actionColor = Color(0xFF1877F2),
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:friendhubfriendhub@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "FriendHub Account Support Request")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "ඊමේල් ලිපිනය: friendhubfriendhub@gmail.com", Toast.LENGTH_LONG).show()
                }
            }
        )

        // Direct Call / Helpline
        ContactItem(
            icon = Icons.Default.Call,
            title = "ක්ෂණික ක්ෂේත්‍ර ඇමතුම් (Helpline)",
            detail = "0719117815",
            subtitle = "දුරකථන ඇමතුම් මගින් සම්බන්ධ වීමට",
            actionLabel = "Call Now",
            actionColor = Color(0xFF00C853),
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0719117815"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "දුරකථන අංකය: 0719117815", Toast.LENGTH_LONG).show()
                }
            }
        )

        // App Administrator Panel
        ContactItem(
            icon = Icons.Default.Security,
            title = "පද්ධති පරිපාලක (App Administrator)",
            detail = "FriendHub Community Safety Team",
            subtitle = "friendhubfriendhub@gmail.com වෙතින් සත්‍යාපිතයි",
            actionLabel = null,
            actionColor = Color(0xFFB0B3B8),
            onClick = null
        )
    }
}

@Composable
private fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    detail: String,
    subtitle: String,
    actionLabel: String? = null,
    actionColor: Color = Color(0xFF1877F2),
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2C2E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(actionColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                Text(text = detail, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF4599FF))
                Text(text = subtitle, fontSize = 11.sp, color = Color(0xFFB0B3B8))
            }
            if (actionLabel != null && onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
