package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Story

@Composable
fun StoryBar(
    stories: List<Story>,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit,
    onLiveClick: () -> Unit = {},
    currentUserAvatar: String = "",
    isBusinessMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A)) // Modern Dark Slate
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // 1. Interactive "Add Story" Card (First card in the horizontal feed)
            item {
                val myPhoto = currentUserAvatar.ifBlank {
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80"
                }

                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(175.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1E293B))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable { onAddStoryClick() }
                        .testTag("add_story_item")
                ) {
                    // Full User Photo in the vertical card
                    AsyncImage(
                        model = myPhoto,
                        contentDescription = "My Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Bottom dark gradient overlay for text & plus button
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF0F172A).copy(alpha = 0.25f),
                                        Color(0xFF0F172A).copy(alpha = 0.9f)
                                    ),
                                    startY = 60f
                                )
                            )
                    )

                    // Bottom section with Glowing Gradient (+) button and "Add Story" text
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, start = 6.dp, end = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Iconic Gradient (+) Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))
                                    )
                                )
                                .border(2.dp, Color(0xFF0F172A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Story",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Add Story",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            // 3. Friends Stories (Square cards with full photo)
            items(stories.size) { index ->
                val story = stories[index]
                StoryBubbleItem(
                    story = story,
                    onClick = { onStoryClick(story) }
                )
            }
        }
    }
}

@Composable
fun StoryBubbleItem(
    story: Story,
    onClick: () -> Unit
) {
    val displayPhoto = story.mediaUrl?.takeIf { it.isNotBlank() } ?: story.userAvatar

    Box(
        modifier = Modifier
            .width(110.dp)
            .height(175.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E293B))
            .then(
                if (story.isLive) {
                    Modifier.border(
                        2.dp,
                        Brush.linearGradient(listOf(Color(0xFFFF3131), Color(0xFF8B5CF6))),
                        RoundedCornerShape(18.dp)
                    )
                } else if (story.hasUnseen) {
                    Modifier.border(
                        1.8.dp,
                        Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))),
                        RoundedCornerShape(18.dp)
                    )
                } else {
                    Modifier.border(1.dp, Color(0xFF334155), RoundedCornerShape(18.dp))
                }
            )
            .clickable { onClick() }
            .testTag("story_item_${story.id}")
    ) {
        // Full User's Photo / Story Media filling the square card
        AsyncImage(
            model = displayPhoto,
            contentDescription = "${story.userName}'s story",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top subtle shadow gradient for avatar readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent)
                    )
                )
        )

        // Bottom dark shadow gradient for user name readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Top-Left User Avatar badge with Story border
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    if (story.isLive) Color(0xFFFF3131)
                    else if (story.hasUnseen) Color(0xFF0084FF)
                    else Color(0xFF64748B)
                )
                .padding(2.dp)
                .clip(CircleShape)
        ) {
            AsyncImage(
                model = story.userAvatar,
                contentDescription = story.userName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top-Right LIVE badge if story is live
        if (story.isLive) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF3131))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // User Name text at bottom
        Text(
            text = story.userName,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
        )
    }
}

@Composable
fun LiveAvatar(
    imageUrl: String,
    isLive: Boolean,
    hasUnseen: Boolean = false,
    size: androidx.compose.ui.unit.Dp = 56.dp,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp
) {
    if (isLive) {
        TikTokLiveAvatar(
            avatarUrl = imageUrl,
            size = size,
            isLive = true,
            showBadge = true
        )
    } else {
        val storyGradient = Brush.sweepGradient(
            colors = listOf(Color(0xFF00F5FF), Color(0xFF9D00FF), Color(0xFF00FF94), Color(0xFF00F5FF))
        )
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (hasUnseen) storyGradient else Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF334155))))
                    .padding(if (hasUnseen) 2.5.dp else 1.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF080808))
                    .padding(2.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
