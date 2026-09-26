package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * TikTok-style Live Profile Avatar Component.
 * When isLive = true, displays an animated pulsing gradient neon ring
 * and a floating "LIVE" pill badge at the bottom of the profile picture.
 */
@Composable
fun TikTokLiveAvatar(
    avatarUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    isLive: Boolean = true,
    showBadge: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    if (!isLive) {
        // Normal profile avatar when not live
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile Avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "tiktok_live_ring")

    // Pulsing ring stroke scale animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_ring_pulse"
    )

    // Rotating gradient angle for dynamic TikTok live halo effect
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "live_ring_rotate"
    )

    Box(
        modifier = modifier
            .size(size + 8.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // 1. TikTok Animated Gradient Live Ring
        Box(
            modifier = Modifier
                .size(size * pulseScale + 4.dp)
                .clip(CircleShape)
                .rotate(rotationAngle)
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFFF0055), // Neon TikTok Pink
                            Color(0xFFFF0033), // Live Red
                            Color(0xFFFF5500), // Glowing Orange
                            Color(0xFFE11D48), // Crimson
                            Color(0xFFFF0055)  // Loop back
                        )
                    )
                )
        )

        // 2. Black gap spacer for ring contrast
        Box(
            modifier = Modifier
                .size(size + 2.dp)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // 3. User Profile Image
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Live Profile Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        }

        // 4. TikTok "LIVE" Pill Badge overlay at bottom center
        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF0055),
                                Color(0xFFFF0033),
                                Color(0xFFD60029)
                            )
                        )
                    )
                    .border(1.dp, Color.White, RoundedCornerShape(50))
                    .padding(horizontal = (size.value * 0.12f).coerceAtLeast(6f).dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = (size.value * 0.18f).coerceAtLeast(8f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
