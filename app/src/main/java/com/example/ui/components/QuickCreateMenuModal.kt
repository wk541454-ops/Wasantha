package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import com.example.viewmodel.MainViewModel

@Composable
fun QuickCreateMenuModal(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OledSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, OledCardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Create Menu (තනන්න)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                CreateOptionRow(
                    icon = Icons.Default.EditNote,
                    iconColor = NeonBlue,
                    title = "Post (තෝරන්න / ලියන්න)",
                    subtitle = "Share a photo, video or text post to News Feed",
                    tag = "create_post_option",
                    onClick = {
                        onDismiss()
                        viewModel.setCreatePostOpen(true)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                CreateOptionRow(
                    icon = Icons.Default.Videocam,
                    iconColor = Color(0xFFF43F5E),
                    title = "Video Post (වීඩියෝ පෝස්ට්)",
                    subtitle = "Post a video with description and tags to Feed",
                    tag = "create_video_post_option",
                    onClick = {
                        onDismiss()
                        viewModel.setCreatePostOpen(true)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                CreateOptionRow(
                    icon = Icons.Default.AddPhotoAlternate,
                    iconColor = Color(0xFFE41E3F),
                    title = "Story (කතාවක්)",
                    subtitle = "Share a 24-hour photo or story to friends",
                    tag = "create_story_option",
                    onClick = {
                        onDismiss()
                        viewModel.setCreateStoryOpen(true)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                CreateOptionRow(
                    icon = Icons.Default.Movie,
                    iconColor = Color(0xFFA855F7),
                    title = "Reel (කෙටි වීඩියෝවක්)",
                    subtitle = "Upload short video reel to trending feed",
                    tag = "create_reel_option",
                    onClick = {
                        onDismiss()
                        viewModel.setCreatePostOpen(true)
                        Toast.makeText(context, "Reel Studio Ready! Select a video to publish.", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                CreateOptionRow(
                    icon = Icons.Default.LiveTv,
                    iconColor = Color(0xFFEF4444),
                    title = "Go Live (සජීවී විකාශය)",
                    subtitle = "Start a real-time live video stream to your friends",
                    tag = "create_live_option",
                    onClick = {
                        onDismiss()
                        viewModel.setLiveStreamingOpen(true)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                CreateOptionRow(
                    icon = Icons.Default.Storefront,
                    iconColor = Color(0xFF10B981),
                    title = "Marketplace Listing (භාණ්ඩ අලෙවිය)",
                    subtitle = "List an item for sale in FriendHub Marketplace",
                    tag = "create_marketplace_option",
                    onClick = {
                        onDismiss()
                        viewModel.setCreateMarketplaceOpen(true)
                    }
                )
            }
        }
    }
}

@Composable
fun CreateOptionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OledSurfaceVariant)
            .clickable { onClick() }
            .padding(12.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (tag == "create_live_option") {
            Animated3DLiveIcon(size = 42.dp, showLiveBadge = false)
        } else {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Text(subtitle, color = Color.Gray, fontSize = 11.sp)
        }
    }
}
