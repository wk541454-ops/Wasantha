package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.NotificationItem
import com.example.model.NotificationType
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.viewmodel.MainViewModel

@Composable
fun NotificationsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifCount by viewModel.unreadNotificationsCount.collectAsState()
    val activePostForComments by viewModel.activePostForComments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedNotifForActions by remember { mutableStateOf<NotificationItem?>(null) }
    var pendingLiveInviteNotif by remember { mutableStateOf<NotificationItem?>(null) }
    var pendingLiveRole by remember { mutableStateOf<String>("viewer") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1D)) // FB Lite Dark Gray
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.selectTab(0) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Reverse to Home Feed",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    com.example.ui.components.AnimatedNotificationBellIcon(size = 32.dp, badgeCount = unreadNotifCount)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.setAppSettingsOpen(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        com.example.ui.components.Custom3DSettingsIcon(size = 36.dp)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (unreadNotifCount > 0) {
                    IconButton(onClick = {
                        viewModel.markAllNotificationsAsRead()
                        Toast.makeText(context, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = "Mark all as read",
                            tint = Color(0xFF00A2FF),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3B3C))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Notification කිසිවක් නැත",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB0B3B8)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationRow(
                        notification = notif,
                        onClick = {
                            if (notif.type == NotificationType.LIVE_STREAM) {
                                pendingLiveInviteNotif = notif
                                pendingLiveRole = "viewer"
                            } else {
                                viewModel.onNotificationClick(notif)
                            }
                        },
                        onJoinLive = {
                            pendingLiveInviteNotif = notif
                            pendingLiveRole = "viewer"
                        },
                        onJoinCoHost = {
                            pendingLiveInviteNotif = notif
                            pendingLiveRole = "cohost"
                        },
                        onOpenOptions = {
                            selectedNotifForActions = notif
                        }
                    )
                }
            }
        }
    }

    // Notification Options Sheet (Delete, Play, Reply, Mark as Read)
    selectedNotifForActions?.let { notif ->
        NotificationActionsBottomSheet(
            notification = notif,
            onDismiss = { selectedNotifForActions = null },
            onPlay = {
                Toast.makeText(context, "Notification එක වාදනය වේ ▶️", Toast.LENGTH_SHORT).show()
                viewModel.markNotificationAsRead(notif.id)
                selectedNotifForActions = null
            },
            onReply = {
                Toast.makeText(context, "${notif.senderName} වෙත Reply යැවුණි 💬", Toast.LENGTH_SHORT).show()
                selectedNotifForActions = null
            },
            onDelete = {
                viewModel.deleteNotification(notif.id)
                Toast.makeText(context, "Notification එක සාර්ථකව Delete කරන ලදී 🗑️", Toast.LENGTH_SHORT).show()
                selectedNotifForActions = null
            },
            onToggleRead = {
                viewModel.markNotificationAsRead(notif.id)
                Toast.makeText(context, "Marked as read 📩", Toast.LENGTH_SHORT).show()
                selectedNotifForActions = null
            }
        )
    }

    // Interactive Confirmation / Accept-Decline Dialog for Live Stream Invitations
    pendingLiveInviteNotif?.let { notif ->
        val isCoHostRole = pendingLiveRole == "cohost"
        AlertDialog(
            onDismissRequest = { pendingLiveInviteNotif = null },
            containerColor = Color(0xFF242526),
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isCoHostRole) Color(0xFF00A2FF).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCoHostRole) Icons.Default.Mic else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isCoHostRole) Color(0xFF00A2FF) else Color(0xFFEF4444),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = if (isCoHostRole) "Co-Host සජීවී ආරාධනාව 🎙️" else "ලයිව් විකාශ ආරාධනාව 🔴",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${notif.senderName} විසින් ඔබට ඔවුන්ගේ සජීවී විකාශයට ${if (isCoHostRole) "Co-Host අසුනකට (Co-Host Seat)" else "නරඹන්නෙකු ලෙස"} එකතු වන ලෙස ආරාධනා කර ඇත.",
                        color = Color(0xFFE4E6EB),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ඔබ මෙම ආරාධනාව පිළිගෙන සජීවී විකාශයට සම්බන්ධ වීමට කැමතිද?",
                        color = Color(0xFFB0B3B8),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onNotificationClick(notif)
                        viewModel.setLiveStreamingOpen(true)
                        if (isCoHostRole) {
                            Toast.makeText(context, "ලයිව් Co-Host ලෙස සාර්ථකව සම්බන්ධ විය! 🎙️", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "ලයිව් විකාශයට සාර්ථකව සම්බන්ධ විය! 🔴", Toast.LENGTH_SHORT).show()
                        }
                        pendingLiveInviteNotif = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCoHostRole) Color(0xFF00A2FF) else Color(0xFFEF4444)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("පිළිගෙන එකතු වන්න (Accept & Join)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "ආරාධනාව ප්‍රතික්ෂේප කරන ලදී (Invitation Declined)", Toast.LENGTH_SHORT).show()
                        viewModel.markNotificationAsRead(notif.id)
                        pendingLiveInviteNotif = null
                    },
                    border = BorderStroke(1.dp, Color(0xFFE4E6EB).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("ප්‍රතික්ෂේප කරන්න (Decline)", color = Color(0xFFE4E6EB))
                }
            }
        )
    }
}

@Composable
fun NotificationRow(
    notification: NotificationItem,
    onClick: () -> Unit,
    onJoinLive: (() -> Unit)? = null,
    onJoinCoHost: (() -> Unit)? = null,
    onOpenOptions: () -> Unit
) {
    val backgroundColor = if (notification.isRead) Color.Transparent else Color(0xFF242526)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("notification_item_${notification.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar + Type Badge Overlay
        Box(modifier = Modifier.size(60.dp)) {
            AsyncImage(
                model = notification.senderAvatar,
                contentDescription = notification.senderName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            val (badgeColor, badgeIcon) = when (notification.type) {
                NotificationType.LIKE -> Pair(Color(0xFF1877F2), Icons.Default.ThumbUp)
                NotificationType.REACTION -> Pair(Color(0xFFE41E3F), Icons.Default.Favorite)
                NotificationType.COMMENT -> Pair(Color(0xFF31A24C), Icons.Default.ChatBubble)
                NotificationType.FRIEND_REQUEST, NotificationType.ACCEPT_REQUEST -> Pair(Color(0xFF1877F2), Icons.Default.PersonAdd)
                NotificationType.FRIEND_NEW_POST -> Pair(Color(0xFF8B5CF6), Icons.Default.Article)
                NotificationType.FRIEND_NEW_STATUS -> Pair(Color(0xFFEC4899), Icons.Default.CameraAlt)
                NotificationType.SECURITY_LOGIN_ALERT -> Pair(Color(0xFFEF4444), Icons.Default.Security)
                NotificationType.PERMISSION_ALERT -> Pair(Color(0xFFF59E0B), Icons.Default.Lock)
                NotificationType.MENTION -> Pair(Color(0xFF10B981), Icons.Default.AlternateEmail)
                NotificationType.LIVE_STREAM -> Pair(Color(0xFFEF4444), Icons.Default.Videocam)
                NotificationType.SYSTEM_ALERT -> Pair(Color(0xFF00A2FF), Icons.Default.Notifications)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Message text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                        append(notification.senderName)
                    }
                    append(" ")
                    withStyle(style = SpanStyle(color = Color(0xFFE4E6EB))) {
                        append(notification.message)
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 18.sp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notification.timestamp,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (!notification.isRead) Color(0xFF1877F2) else Color(0xFFB0B3B8)
            )

            if (notification.type == NotificationType.LIVE_STREAM) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onJoinLive?.invoke() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join Live 🔴", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { onJoinCoHost?.invoke() },
                        border = BorderStroke(1.dp, Color(0xFF00A2FF)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF00A2FF), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Co-Host 🎙️", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00A2FF))
                    }
                }
            }
        }

        // Post Preview Image (FB style)
        notification.postPreviewUrl?.let { url ->
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = url,
                contentDescription = "Post Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // 3-dot options button
        IconButton(onClick = onOpenOptions) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Notification Options",
                tint = Color(0xFFB0B3B8),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationActionsBottomSheet(
    notification: NotificationItem,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onReply: () -> Unit,
    onDelete: () -> Unit,
    onToggleRead: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF242526),
        scrimColor = Color.Black.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle pill
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF8A8D91))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User Avatar and Message snippet preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = notification.senderAvatar,
                    contentDescription = notification.senderName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${notification.senderName} ${notification.message}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    color = Color.White,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF3A3B3C), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: Remove notification
            NotifOptionRow(
                icon = Icons.Default.Close,
                iconColor = Color(0xFFB0B3B8),
                title = "Remove this notification",
                subtitle = "Stop seeing this notification",
                onClick = {
                    onDelete()
                    onDismiss()
                }
            )

            // Option 2: Turn off group mentions
            NotifOptionRow(
                icon = Icons.Default.Block,
                iconColor = Color(0xFFB0B3B8),
                title = "Turn off mentions",
                subtitle = "Stop receiving notifications when mentioned",
                onClick = {
                    Toast.makeText(context, "Mentions turned off 🔕", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            // Option 3: Turn off notifications
            NotifOptionRow(
                icon = Icons.Default.Close,
                iconColor = Color(0xFFB0B3B8),
                title = "Turn off notifications for this",
                subtitle = "You will stop receiving updates for this post",
                onClick = {
                    Toast.makeText(context, "Notifications turned off 🔕", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            // Option 4: Report issue
            NotifOptionRow(
                icon = Icons.Default.Comment,
                iconColor = Color(0xFFB0B3B8),
                title = "Report issue",
                subtitle = "Let us know if something is wrong",
                onClick = {
                    Toast.makeText(context, "Report submitted ⚠️", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NotifOptionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.18f), RoundedCornerShape(10.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Color(0xFFB0B3B8)
            )
        }
    }
}
