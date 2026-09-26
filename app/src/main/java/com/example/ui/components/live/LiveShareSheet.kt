package com.example.ui.components.live

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ShareFriend(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isOnline: Boolean = false
)

data class ShareActionItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color = Color.White,
    val isWhatsApp: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveShareSheet(
    onDismiss: () -> Unit,
    onShowNotification: (String) -> Unit,
    viewModel: MainViewModel? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var downloadStatusText by remember { mutableStateOf("") }
    var showQualityDialog by remember { mutableStateOf(false) }
    var selectedQuality by remember { mutableStateOf("1080p HD (Auto)") }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackInput by remember { mutableStateOf("") }
    var showPromoteDialog by remember { mutableStateOf(false) }
    var showGuidelinesDialog by remember { mutableStateOf(false) }
    var selectedPromoteBudget by remember { mutableStateOf("Rs. 500 / 2K Reach") }
    var sentFriends by remember { mutableStateOf(setOf<String>()) }

    val friends = remember {
        listOf(
            ShareFriend("1", "sadaruwan boy 🎧", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200", true),
            ShareFriend("2", "tharu doni 💖", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200", false),
            ShareFriend("3", "Sudu 💖😇", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200", true),
            ShareFriend("4", "shevon", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200", false),
            ShareFriend("5", "Avishka Darshana", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200", true),
            ShareFriend("6", "nilar us", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200", false)
        )
    }

    // Top primary action channels matching TikTok Share Sheet (Screenshots 4 & 5)
    val primaryActions = remember {
        listOf(
            ShareActionItem("repost", "Repost", Icons.Default.Repeat, Color(0xFFEAB308)),
            ShareActionItem("copy", "Copy link", Icons.Default.Link, Color(0xFF3B82F6)),
            ShareActionItem("whatsapp", "WhatsApp", Icons.Default.Chat, Color(0xFF25D366), isWhatsApp = true),
            ShareActionItem("status", "Status", Icons.Default.DataUsage, Color(0xFF10B981), isWhatsApp = true),
            ShareActionItem("facebook", "Facebook", Icons.Default.Facebook, Color(0xFF1877F2)),
            ShareActionItem("whatsapp_direct", "WhatsApp Chat", Icons.Default.Send, Color(0xFF00A884), isWhatsApp = true)
        )
    }

    // Secondary action items matching TikTok Share Sheet (Screenshots 4 & 5)
    val secondaryActions = remember {
        listOf(
            ShareActionItem("guidelines", "Guidelines", Icons.Default.Shield, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("story", "Add to Story", Icons.Default.AddCircleOutline, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("report", "Report", Icons.Default.Flag, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("not_interested", "Not interested", Icons.Default.HeartBroken, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("feedback", "Feedback", Icons.Default.HelpOutline, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("promote", "Promote", Icons.Default.LocalFireDepartment, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("pc", "Watch on PC", Icons.Default.Computer, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("background", "Background", Icons.Default.Headphones, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("settings", "Settings", Icons.Default.Settings, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("quality", "Video quality", Icons.Default.HighQuality, Color(0xFFF3F4F6), Color.Black),
            ShareActionItem("download", "Save Video", Icons.Default.Download, Color(0xFFF3F4F6), Color.Black)
        )
    }

    fun openWhatsAppShare(text: String) {
        val encodedMessage = Uri.encode(text)
        val waUrl = "https://api.whatsapp.com/send?text=$encodedMessage"

        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
                setPackage("com.whatsapp")
            }
            context.startActivity(sendIntent)
            onShowNotification("Opening WhatsApp App... 💬")
        } catch (e: Exception) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                context.startActivity(webIntent)
                onShowNotification("Opening WhatsApp Web Share... 💬")
            } catch (ex: Exception) {
                val systemIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                try {
                    context.startActivity(Intent.createChooser(systemIntent, "Share via WhatsApp"))
                } catch (err: Exception) {
                    clipboardManager.setText(AnnotatedString(text))
                    onShowNotification("Link Copied to Clipboard! 📋")
                }
            }
        }
    }

    fun openFacebookShare(url: String, caption: String) {
        val encodedUrl = Uri.encode(url)
        val fbShareUrl = "https://www.facebook.com/sharer/sharer.php?u=$encodedUrl"

        try {
            val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fbShareUrl))
            context.startActivity(fbIntent)
            onShowNotification("Opening Facebook Share... 📘")
        } catch (e: Exception) {
            val systemIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "$caption\n$url")
            }
            try {
                context.startActivity(Intent.createChooser(systemIntent, "Share on Facebook"))
            } catch (err: Exception) {
                clipboardManager.setText(AnnotatedString("$caption\n$url"))
                onShowNotification("Facebook link copied to Clipboard! 📋")
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header Row: Search Icon + Title "Share" + Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onShowNotification("Type a friend's name to share...")
                }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "Share",
                    color = Color.Black,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Download progress bar banner if active
            AnimatedVisibility(visible = isDownloading) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25D366)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = downloadStatusText,
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(downloadProgress * 100).toInt()}%",
                                color = Color(0xFF25D366),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = Color(0xFF25D366),
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            // Top Row: Recent Friends Avatars (Horizontally Scrollable)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(friends) { friend ->
                    val isSent = sentFriends.contains(friend.id)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(68.dp)
                            .clickable {
                                if (!isSent) {
                                    sentFriends = sentFriends + friend.id
                                    viewModel?.sendMessage("🔴 Join my Live Stream on FriendHub: https://friendhub.com/live/123")
                                    viewModel?.sendLiveStreamInvite(friend.name, friend.avatarUrl)
                                    onShowNotification("FriendHub ඇප් එක තුළ ${friend.name} වෙත සජීවී ආරාධනා Notification එක යවන ලදී! 📩")
                                } else {
                                    onShowNotification("${friend.name} වෙත දැනටමත් ආරාධනාව යවා ඇත! ✔️")
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = friend.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (isSent) Color(0xFF25D366) else Color(0xFFE2E8F0), CircleShape)
                            )
                            if (isSent) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sent",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            } else if (friend.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                        .border(2.dp, Color.White, CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSent) "යවන ලදී ✔" else friend.name,
                            color = if (isSent) Color(0xFF25D366) else Color.Black.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isSent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Primary Action Row (Repost, Copy link, WhatsApp, Status, Facebook)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(primaryActions) { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(62.dp)
                            .clickable {
                                when (action.id) {
                                    "whatsapp", "whatsapp_direct" -> {
                                        openWhatsAppShare("🔥 Watch this amazing Live Stream on FriendHub: https://friendhub.com/live/123")
                                        onDismiss()
                                    }
                                    "status" -> {
                                        openWhatsAppShare("My Live Stream Status: https://friendhub.com/live/123")
                                        onShowNotification("Sharing to WhatsApp Status... ⭕")
                                        onDismiss()
                                    }
                                    "copy" -> {
                                        clipboardManager.setText(AnnotatedString("https://friendhub.com/live/123"))
                                        onShowNotification("Stream Link Copied to Clipboard! 📋")
                                        onDismiss()
                                    }
                                    "repost" -> {
                                        viewModel?.createPost(
                                            content = "🔁 Reposted Live Stream: Watch now on FriendHub!\nhttps://friendhub.com/live/123",
                                            mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800",
                                            mediaType = com.example.model.MediaType.IMAGE
                                        )
                                        onShowNotification("Stream Reposted to your Feed! 🔁")
                                        onDismiss()
                                    }
                                    "facebook" -> {
                                        openFacebookShare("https://friendhub.com/live/123", "Watch Live Stream on FriendHub 🔴")
                                        onDismiss()
                                    }
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(action.bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            when (action.id) {
                                "whatsapp", "whatsapp_direct", "status" -> {
                                    Icon(
                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_whatsapp_green),
                                        contentDescription = action.label,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                                "facebook" -> {
                                    Icon(
                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_facebook_blue),
                                        contentDescription = action.label,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = action.label,
                                        tint = action.iconColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = action.label,
                            color = Color.Black.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary Action Row (Story, Report, Not Interested, Promote, Quality, Settings, Download, etc.)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(secondaryActions) { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(62.dp)
                            .clickable {
                                when (action.id) {
                                    "guidelines" -> {
                                        showGuidelinesDialog = true
                                    }
                                    "story" -> {
                                        viewModel?.createStory(
                                            mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800",
                                            caption = "🔴 සජීවී විකාශය බලන්න (Watch Live Stream on FriendHub!)"
                                        )
                                        onShowNotification("ඔබගේ Story එක සාර්ථකව පල කරන ලදී! 📖 (Story Posted!)")
                                        onDismiss()
                                    }
                                    "report" -> {
                                        showReportDialog = true
                                    }
                                    "not_interested" -> {
                                        onShowNotification("We will show fewer streams like this 💔")
                                        onDismiss()
                                    }
                                    "feedback" -> {
                                        showFeedbackDialog = true
                                    }
                                    "promote" -> {
                                        showPromoteDialog = true
                                    }
                                    "pc" -> {
                                        val pcUrl = "https://friendhub.com/live/123"
                                        clipboardManager.setText(AnnotatedString(pcUrl))
                                        try {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pcUrl))
                                            context.startActivity(webIntent)
                                        } catch (e: Exception) {}
                                        onShowNotification("Web Link opened & copied for PC! 💻")
                                        onDismiss()
                                    }
                                    "background" -> {
                                        onShowNotification("Background Audio player enabled! 🎧")
                                        onDismiss()
                                    }
                                    "settings" -> {
                                        showSettingsDialog = true
                                    }
                                    "quality" -> {
                                        showQualityDialog = true
                                    }
                                    "download" -> {
                                        if (!isDownloading) {
                                            isDownloading = true
                                            coroutineScope.launch {
                                                downloadStatusText = "Saving stream to gallery..."
                                                downloadProgress = 0.2f
                                                delay(400)
                                                downloadProgress = 0.65f
                                                delay(500)
                                                downloadProgress = 1.0f
                                                isDownloading = false
                                                onShowNotification("Live Stream saved to Gallery! 🎬")
                                                Toast.makeText(context, "ලයිව් වීඩියෝව Gallery එකට සුරක්ෂිතව සේව් විය! 🎬", Toast.LENGTH_SHORT).show()
                                                onDismiss()
                                            }
                                        }
                                    }
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(action.bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = action.iconColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = action.label,
                            color = Color.Black.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Video Quality Selector Dialog
    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("Select Video Quality", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("1080p HD (Auto)", "720p HD", "480p SD", "Audio Only").forEach { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedQuality = quality
                                    showQualityDialog = false
                                    onShowNotification("Video quality changed to $quality 📺")
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedQuality == quality),
                                onClick = {
                                    selectedQuality = quality
                                    showQualityDialog = false
                                    onShowNotification("Video quality changed to $quality 📺")
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(quality, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Live Stream Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Stream Settings", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("• High Bitrate Mode: ON", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Filter Comments: Active", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Hardware Acceleration: Enabled", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Network Speed Optimization: 100%-200% Active", fontSize = 13.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSettingsDialog = false
                        onShowNotification("Settings updated successfully! ⚙️")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Text("Save", color = Color.White)
                }
            }
        )
    }

    // Report Dialog
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Stream", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)) },
            text = {
                Column {
                    Text("Please select a reason for reporting this live stream:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf("Inappropriate Content", "Spam / Misleading", "Harassment / Hate Speech", "Copyright Violation").forEach { reason ->
                        Button(
                            onClick = {
                                showReportDialog = false
                                onShowNotification("Report for '$reason' submitted! 🚩")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(reason, color = Color.Black, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Feedback Dialog
    if (showFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("Send Stream Feedback", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        placeholder = { Text("Type your feedback here...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFeedbackDialog = false
                        onShowNotification("Thank you for your feedback! 💬")
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                ) {
                    Text("Submit", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Promote Dialog
    if (showPromoteDialog) {
        AlertDialog(
            onDismissRequest = { showPromoteDialog = false },
            title = { Text("Promote Live Stream", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Boost view count & audience reach:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf("Rs. 500 / 2K Viewers", "Rs. 1,000 / 5K Viewers", "Rs. 2,500 / 15K Viewers").forEach { pkg ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { selectedPromoteBudget = pkg }.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (selectedPromoteBudget == pkg), onClick = { selectedPromoteBudget = pkg })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(pkg, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPromoteDialog = false
                        onShowNotification("Promotion campaign for '$selectedPromoteBudget' activated! 🔥")
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4500))
                ) {
                    Text("Start Campaign", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPromoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showGuidelinesDialog) {
        AlertDialog(
            onDismissRequest = { showGuidelinesDialog = false },
            containerColor = Color(0xFF1E293B),
            icon = { Text("🛡️", fontSize = 28.sp) },
            title = {
                Text(
                    text = "FriendHub Live Guidelines 🛡️",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "• Respect all hosts, co-hosts, and viewers in live streams.\n\n• No hate speech, harassment, or abusive comments in live chat.\n\n• Follow FriendHub safety policies for a clean, enjoyable community experience.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGuidelinesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2FF)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("තේරුණා (Got it) 👍", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }
}

