package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MediaType
import com.example.model.Post
import com.example.ui.theme.NeonBlue
import com.example.util.GalleryDownloadHelper
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

enum class PostOptionsScreen {
    MAIN,
    MORE_OPTIONS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOptionsBottomSheet(
    post: Post,
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentScreen by remember { mutableStateOf(PostOptionsScreen.MAIN) }
    var isSaved by remember { mutableStateOf(post.isSaved) }
    var isNotificationsEnabled by remember { mutableStateOf(false) }
    var isInterested by remember { mutableStateOf(false) }

    // Dialog States
    var showReportDialog by remember { mutableStateOf(false) }
    var showEditHistoryDialog by remember { mutableStateOf(false) }
    var isDownloadingToGallery by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadStatusText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1F20),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF55565A))
            )
        },
        modifier = modifier.testTag("post_options_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (targetState == PostOptionsScreen.MORE_OPTIONS) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "post_options_animation"
            ) { screen ->
                when (screen) {
                    PostOptionsScreen.MAIN -> {
                        // SCREEN 1 (Exactly Screenshot 1)
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Card 1: Interested / Not Interested
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("card_interest_options"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF262729))
                            ) {
                                Column {
                                    OptionItemRow(
                                        icon = Icons.Default.AddCircle,
                                        title = "උනන්දුයි",
                                        iconTint = if (isInterested) Color(0xFF22C55E) else Color.White,
                                        badge = if (isInterested) "මනාපය ලකුණු කර ඇත ✓" else null,
                                        onClick = {
                                            isInterested = !isInterested
                                            Toast.makeText(
                                                context,
                                                if (isInterested) "ඔබගේ මනාපය සටහන් කරගන්නා ලදී 👍 (Marked as Interested)"
                                                else "මනාපය ඉවත් කරන ලදී",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )

                                    HorizontalDivider(color = Color(0xFF333538), thickness = 0.8.dp)

                                    OptionItemRow(
                                        icon = Icons.Default.RemoveCircle,
                                        title = "රුචිකත්වයක් නැත",
                                        iconTint = Color(0xFFEF4444),
                                        onClick = {
                                            viewModel.hidePost(post.id)
                                            Toast.makeText(
                                                context,
                                                "පෝස්ට් එක සඟවන ලදී. මෙවැනි පෝස්ට් අඩු කරනු ලැබේ ✕",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            onDismiss()
                                        }
                                    )
                                }
                            }

                            // Card 2: Save / Download Gallery, Share, Report
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("card_action_options"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF262729))
                            ) {
                                Column {
                                    val isVideoPost = post.mediaType == MediaType.VIDEO ||
                                            post.mediaUrl?.endsWith(".mp4", ignoreCase = true) == true ||
                                            post.mediaUrl?.contains("video", ignoreCase = true) == true

                                    if (isVideoPost) {
                                        // 1. Video posts get "Download Gallery"
                                        OptionItemRow(
                                            icon = Icons.Default.Download,
                                            title = "Download Gallery",
                                            iconTint = Color(0xFF38BDF8),
                                            onClick = {
                                                isDownloadingToGallery = true
                                                downloadProgress = 0.05f
                                                downloadStatusText = "Download Gallery... 📥"
                                                scope.launch {
                                                    val uri = GalleryDownloadHelper.downloadVideoToGallery(
                                                        context = context,
                                                        videoUrl = post.mediaUrl ?: "https://friendhub.io/sample_video.mp4",
                                                        title = post.content.ifBlank { "FriendHub Video" },
                                                        onProgress = { p -> downloadProgress = p }
                                                    )
                                                    isDownloadingToGallery = false
                                                    Toast.makeText(
                                                        context,
                                                        "Download Gallery: සාර්ථකව බාගන්නා ලදී! 🎥",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        )

                                        HorizontalDivider(color = Color(0xFF333538), thickness = 0.8.dp)

                                        // Video posts also have "Post Save"
                                        OptionItemRow(
                                            icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            title = "Post Save",
                                            iconTint = if (isSaved) Color(0xFFD946EF) else Color.White,
                                            badge = if (isSaved) "Saved 📌" else null,
                                            onClick = {
                                                val nowSaved = viewModel.togglePostSave(post.id)
                                                isSaved = nowSaved
                                                Toast.makeText(
                                                    context,
                                                    if (nowSaved) "Post Save: පෝස්ට් එක සුරකින ලදී 📌"
                                                    else "සුරැකීමෙන් ඉවත් කරන ලදී",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    } else {
                                        // Photo posts & regular posts: ONLY "Post Save" (NO "Download Gallery")
                                        OptionItemRow(
                                            icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            title = "Post Save",
                                            iconTint = if (isSaved) Color(0xFFD946EF) else Color.White,
                                            badge = if (isSaved) "Saved 📌" else null,
                                            onClick = {
                                                val nowSaved = viewModel.togglePostSave(post.id)
                                                isSaved = nowSaved
                                                Toast.makeText(
                                                    context,
                                                    if (nowSaved) "Post Save: පෝස්ට් එක සුරකින ලදී 📌"
                                                    else "සුරැකීමෙන් ඉවත් කරන ලදී",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }

                                    HorizontalDivider(color = Color(0xFF333538), thickness = 0.8.dp)

                                    // Share (බෙදා ගන්න)
                                    OptionItemRow(
                                        icon = Icons.AutoMirrored.Filled.Reply,
                                        customDrawableRes = com.example.R.drawable.ic_3d_share_vector,
                                        title = "බෙදා ගන්න",
                                        iconTint = Color.White,
                                        onClick = {
                                            onDismiss()
                                            onShareClick()
                                        }
                                    )

                                    HorizontalDivider(color = Color(0xFF333538), thickness = 0.8.dp)

                                    // Report (පෝස්ට්ව පැමිණිලි කරන්න)
                                    OptionItemRow(
                                        icon = Icons.Default.Report,
                                        title = "පෝස්ට්ව පැමිණිලි කරන්න",
                                        iconTint = Color(0xFFF59E0B),
                                        onClick = {
                                            showReportDialog = true
                                        }
                                    )
                                }
                            }

                            // Card 3: More options (Chevron Right >)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { currentScreen = PostOptionsScreen.MORE_OPTIONS }
                                    .testTag("card_more_options_trigger"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF262729))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "More options",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = "More options",
                                        tint = Color(0xFFB0B3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    PostOptionsScreen.MORE_OPTIONS -> {
                        // SCREEN 2 (Exactly Screenshot 2)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Header with Back Arrow and Title
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { currentScreen = PostOptionsScreen.MAIN },
                                    modifier = Modifier.testTag("btn_back_to_main_options")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                                Text(
                                    text = "More options",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // Card with the 3 exact items from Screenshot 2 + Gallery Downloads
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("card_more_options_content"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF262729))
                            ) {
                                Column {
                                    // 1. View edit history
                                    OptionItemRow(
                                        icon = Icons.Default.History,
                                        title = "View edit history",
                                        iconTint = Color.White,
                                        onClick = {
                                            showEditHistoryDialog = true
                                        }
                                    )

                                    HorizontalDivider(color = Color(0xFF333538), thickness = 0.8.dp)

                                    // 2. Turn on/off notifications for this post (මෙම පෝස්ට්ව සඳහා දැනුම්දීම් ක්‍රියාත්මක කරන්න)
                                    OptionItemRow(
                                        icon = if (isNotificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                        title = if (isNotificationsEnabled) "මෙම පෝස්ට්ව සඳහා දැනුම්දීම් අක්‍රීය කරන්න"
                                        else "මෙම පෝස්ට්ව සඳහා දැනුම්දීම් ක්‍රියාත්මක කරන්න",
                                        iconTint = if (isNotificationsEnabled) Color(0xFFF59E0B) else Color.White,
                                        badge = if (isNotificationsEnabled) "සක්‍රීයයි 🔔" else null,
                                        onClick = {
                                            isNotificationsEnabled = !isNotificationsEnabled
                                            Toast.makeText(
                                                context,
                                                if (isNotificationsEnabled) "මෙම පෝස්ට් එක සඳහා දැනුම්දීම් ක්‍රියාත්මක කරන ලදී 🔔"
                                                else "දැනුම්දීම් අක්‍රීය කරන ලදී 🔕",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )

                                    HorizontalDivider(color = Color(0xFF333538), thickness = 0.8.dp)

                                    // 3. Copy link (සබැඳිය පිටපත් කරන්න)
                                    OptionItemRow(
                                        icon = Icons.Default.ContentCopy,
                                        title = "සබැඳිය පිටපත් කරන්න",
                                        iconTint = Color.White,
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Post Link", "https://friendhub.io/post/${post.id}")
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "සබැඳිය පිටපත් කරන ලදී 📋", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ================= Interactive Sub-Dialogs =================

    // 1. Download Progress Dialog
    if (isDownloadingToGallery) {
        AlertDialog(
            onDismissRequest = { /* prevent dismiss while saving */ },
            containerColor = Color(0xFF242526),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier.size(24.dp),
                        color = NeonBlue,
                        trackColor = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Download Gallery", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(downloadStatusText, color = Color(0xFFCBD5E1), fontSize = 14.sp)
                    LinearProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonBlue,
                        trackColor = Color(0xFF334155)
                    )
                    Text(
                        text = "${(downloadProgress * 100).toInt()}% සම්පූර්ණයි",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            },
            confirmButton = {}
        )
    }

    // 2. Report Post Dialog
    if (showReportDialog) {
        val reportReasons = listOf(
            "අනවශ්‍ය වෙළඳ දැන්වීම් හෝ ස්පෑම් (Spam)",
            "වෛරී ප්‍රකාශ හෝ හිංසනය (Hate Speech / Harassment)",
            "අසත්‍ය හෝ මුළා සහගත පුවත් (False Information)",
            "ප්‍රචණ්ඩත්වය හෝ නීති විරෝධී ක්‍රියා (Violence)",
            "වංචා සහ මුදල් වංචා (Scam / Fraud)",
            "වෙනත් හේතුවක් (Other)"
        )
        var selectedReason by remember { mutableStateOf(reportReasons[0]) }

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Report, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("පෝස්ට්ව පැමිණිලි කරන්න", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("මෙම සටහන FriendHub ප්‍රජා ප්‍රමිතීන් උල්ලංඝනය කරන්නේ කෙසේද?", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    reportReasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedReason = reason }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonBlue,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(reason, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reportPost(post.id, selectedReason)
                        showReportDialog = false
                        Toast.makeText(context, "ස්තූතියි! ඔබගේ පැමිණිල්ල සමාලෝචනයට යොමු කරන ලදී 🛡️", Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("පැමිණිල්ල යොමු කරන්න", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // 3. Edit History Dialog
    if (showEditHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showEditHistoryDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("සංස්කරණ ඉතිහාසය (Edit History)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Author Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = post.userAvatar,
                            contentDescription = post.userName,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(post.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("පෝස්ට් හැඳුනුම් අංකය: #${post.id.take(8)}", color = Color.Gray, fontSize = 11.sp)
                        }
                    }

                    // Version 2 (Current)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F20)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("වත්මන් සංස්කරණය (Current Version)", color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(post.timestamp, color = Color.Gray, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(post.content.ifBlank { "[මාධ්‍ය සටහනකි]" }, color = Color.White, fontSize = 13.sp)
                        }
                    }

                    // Version 1 (Original)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18191A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("මුල් පළ කිරීම (Original Post)", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("පළමු කෙටුම්පත", color = Color.Gray, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = post.content.ifBlank { "Original upload version" },
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEditHistoryDialog = false }) {
                    Text("හරි (Done)", color = NeonBlue)
                }
            }
        )
    }
}

@Composable
private fun OptionItemRow(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    customDrawableRes: Int? = null,
    subtitle: String? = null,
    iconTint: Color = Color.White,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (customDrawableRes != null) {
            Custom3DShareIcon(size = 24.dp)
        } else {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = badge,
                        color = NeonBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }
    }
}
