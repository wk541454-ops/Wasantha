package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostVideoCreateBottomSheet(
    onDismiss: () -> Unit,
    onCreatePost: () -> Unit,
    onPickVideo: () -> Unit,
    onPickPhoto: () -> Unit,
    onCreateReel: () -> Unit,
    onGoLive: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F172A), // Dark slate
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF475569))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Create / පළ කරන්න",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Share photos, videos or thoughts with friends",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF94A3B8)
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Post (Standard text/media post)
            CreateOptionItem(
                icon = Icons.Default.EditNote,
                iconTint = Color(0xFF38BDF8),
                badgeText = "POPULAR",
                badgeColor = Color(0xFF0284C7),
                title = "Create Post (පෝස්ට් එකක් ලියන්න)",
                subtitle = "Share status updates, feelings, location or tag friends",
                tag = "sheet_create_post_option",
                onClick = {
                    onDismiss()
                    onCreatePost()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Video Post (Dedicated Video flow)
            CreateOptionItem(
                icon = Icons.Default.Videocam,
                iconTint = Color(0xFFF43F5E),
                badgeText = "VIDEO",
                badgeColor = Color(0xFFE11D48),
                title = "Video Post (වීඩියෝ පෝස්ට් එකක්)",
                subtitle = "Select & upload high quality videos to your feed",
                tag = "sheet_create_video_option",
                isHighlighted = true,
                onClick = {
                    onDismiss()
                    onPickVideo()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Photo Post
            CreateOptionItem(
                icon = Icons.Default.PhotoLibrary,
                iconTint = Color(0xFF22C55E),
                badgeText = null,
                badgeColor = Color.Transparent,
                title = "Photo (ඡායාරූපයක් එක්කරන්න)",
                subtitle = "Upload gallery photos with filters and captions",
                tag = "sheet_create_photo_option",
                onClick = {
                    onDismiss()
                    onPickPhoto()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Short Reel
            CreateOptionItem(
                icon = Icons.Default.Movie,
                iconTint = Color(0xFFA855F7),
                badgeText = "REELS",
                badgeColor = Color(0xFF7E22CE),
                title = "Reel (කෙටි වීඩියෝවක්)",
                subtitle = "Publish creative short video reels with trending music",
                tag = "sheet_create_reel_option",
                onClick = {
                    onDismiss()
                    onCreateReel()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Go Live
            CreateOptionItem(
                icon = Icons.Default.LiveTv,
                iconTint = Color(0xFFEF4444),
                badgeText = "LIVE",
                badgeColor = Color(0xFFDC2626),
                title = "Go Live (සජීවී විකාශය)",
                subtitle = "Start a real-time live video stream with interactive gifts",
                tag = "sheet_create_live_option",
                onClick = {
                    onDismiss()
                    onGoLive()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CreateOptionItem(
    icon: ImageVector,
    iconTint: Color,
    badgeText: String?,
    badgeColor: Color,
    title: String,
    subtitle: String,
    tag: String,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isHighlighted) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            iconTint.copy(alpha = 0.16f),
                            Color(0xFF1E293B)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF1E293B))
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (isHighlighted) iconTint.copy(alpha = 0.5f) else Color(0xFF334155),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f))
                .border(1.dp, iconTint.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color.White
                )

                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = Color(0xFF94A3B8)
            )
        }
    }
}
