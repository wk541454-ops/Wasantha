package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R

/**
 * Custom 3D Animated Live Streaming Icon based on Glassmorphism design.
 * Features a dark/black blend background, glowing glass rings, red play button,
 * red glossy "LIVE" pill badge, and continuous smooth floating up-and-down animation.
 */
@Composable
fun Animated3DLiveIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showLiveBadge: Boolean = true,
    animateFloating: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "3d_live_float")
    
    // Smooth up-and-down floating animation (y-axis movement)
    val offsetY by if (animateFloating) {
        infiniteTransition.animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float_offset"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    // Gentle pulse for red glow background
    val glowAlpha by if (animateFloating) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_pulse"
        )
    } else {
        remember { mutableStateOf(0.6f) }
    }

    Box(
        modifier = modifier
            .size(size)
            .offset(y = offsetY.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Pure dark black background radial glow effect (Removes white box, seamless black blend)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF0033).copy(alpha = glowAlpha * 0.5f),
                            Color(0xFF0F0508).copy(alpha = 0.95f),
                            Color.Black
                        )
                    )
                )
        )

        // 2. Outer 3D Glass Image / Ring with Circular Clip (Black background cutoff)
        Box(
            modifier = Modifier
                .size(size * 0.88f)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(Color.Black)
                .border(
                    width = (size.value * 0.04f).dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF4D6D),
                            Color(0xFFFF0033),
                            Color(0xFF80001A)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_3d_video_play),
                contentDescription = "3D Live Icon",
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Black)
            )
        }

        // 3. Floating "LIVE" Glossy Red Pill Badge at the bottom
        if (showLiveBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (size.value * 0.10f).dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF1A40),
                                Color(0xFFFF0033),
                                Color(0xFFB30024)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = (size.value * 0.14f).dp, vertical = (size.value * 0.03f).dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = (size.value * 0.20f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
