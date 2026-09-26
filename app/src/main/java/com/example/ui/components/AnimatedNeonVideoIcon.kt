package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Red Neon Pulsing Video Icon based on YouTube neon play button design.
 * Features an authentic glowing red neon tube squircle with centered play triangle,
 * smoothly breathing/pulsing ("නෙවෙනවා පත්තු වෙනවා වගේ, එළිය අඩු වෙනවා ආය වැඩි වෙනවා වගේ").
 */
@Composable
fun AnimatedNeonVideoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
    animateGlow: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "red_neon_video_pulse")

    // Smooth breathing glow alpha (dim down -> brighten up continuously)
    val glowAlpha by if (animateGlow) {
        infiniteTransition.animateFloat(
            initialValue = 0.20f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_alpha"
        )
    } else {
        remember { mutableStateOf(0.85f) }
    }

    // Scale pulse for the ambient radial aura
    val auraScale by if (animateGlow) {
        infiniteTransition.animateFloat(
            initialValue = 0.90f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "aura_scale"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    // Colors matching user photo
    val neonRed = Color(0xFFFF0033)
    val brightNeonRed = Color(0xFFFF2A55)
    val neonWhiteCore = Color(0xFFFFFFFF)
    val darkBg = Color(0xFF0C0103)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 1. Outer Radial Red Glow Aura (Pulsing / Breathing light: "eliya adu wenawa ayeth wadi wenawa")
        Box(
            modifier = Modifier
                .size(size * auraScale)
                .clip(RoundedCornerShape(35))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            brightNeonRed.copy(alpha = glowAlpha * 0.75f),
                            neonRed.copy(alpha = glowAlpha * 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Dark Neon Container Base (Squircle shape matching YouTube red neon photo)
        Box(
            modifier = Modifier
                .size(size * 0.88f)
                .clip(RoundedCornerShape(28))
                .background(darkBg)
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            neonWhiteCore.copy(alpha = 0.4f + (glowAlpha * 0.5f)),
                            brightNeonRed.copy(alpha = 0.3f + (glowAlpha * 0.7f)),
                            neonRed.copy(alpha = 0.2f + (glowAlpha * 0.6f))
                        )
                    ),
                    shape = RoundedCornerShape(28)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 3. Precision Neon Tube & Centered Play Triangle
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
            ) {
                val w = this.size.width
                val h = this.size.height

                // Play Triangle Coordinates (centered properly in squircle)
                val playPath = Path().apply {
                    val startX = w * 0.35f
                    val endX = w * 0.70f
                    val topY = h * 0.26f
                    val bottomY = h * 0.74f
                    val centerY = h * 0.50f

                    moveTo(startX, topY)
                    lineTo(endX, centerY)
                    lineTo(startX, bottomY)
                    close()
                }

                // Play Triangle Outer Red Glow
                drawPath(
                    path = playPath,
                    color = neonRed.copy(alpha = glowAlpha * 0.6f),
                    style = Stroke(width = 4f * glowAlpha)
                )

                // Play Triangle Neon Tube Outline
                drawPath(
                    path = playPath,
                    color = brightNeonRed.copy(alpha = 0.4f + (glowAlpha * 0.6f)),
                    style = Stroke(width = 2.2f)
                )

                // Play Triangle White Core Flare
                drawPath(
                    path = playPath,
                    color = neonWhiteCore.copy(alpha = 0.5f + (glowAlpha * 0.5f)),
                    style = Stroke(width = 1.0f)
                )
            }
        }
    }
}
