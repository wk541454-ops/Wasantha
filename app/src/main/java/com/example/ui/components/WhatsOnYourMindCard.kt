package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.User

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.graphics.Brush

@Composable
fun WhatsOnYourMindCard(
    user: User,
    onProfileClick: () -> Unit = {},
    onOpenCreatePost: () -> Unit,
    onOpenPhotoPicker: () -> Unit = onOpenCreatePost,
    onOpenVideoPicker: () -> Unit = onOpenCreatePost,
    onOpenLiveClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Sleek rounded-2xl status creation card with dark slate background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E293B)) // Soft Slate Gray container
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF06B6D4).copy(alpha = 0.35f), // Neon Cyan accent
                        Color(0xFF8B5CF6).copy(alpha = 0.25f)  // Violet accent
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("whats_on_your_mind_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar Preview with Neon Cyan online ring
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clickable { onProfileClick() }
                ) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = user.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))
                                ),
                                shape = CircleShape
                            )
                            .testTag("whats_on_your_mind_avatar")
                    )
                    // Active status dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF06B6D4)) // Neon Cyan dot
                            .border(2.dp, Color(0xFF1E293B), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Rounded Card Input Area: "What's on your mind?"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A)) // Deep slate inner
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                        .clickable { onOpenCreatePost() }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("whats_on_your_mind_input"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "What's on your mind?...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Post Icon in the red marked position (at the right of "What's on your mind?")
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onOpenCreatePost() }
                        .testTag("whats_on_your_mind_post_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_3d_blue_plus_post),
                        contentDescription = "Create Post",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
