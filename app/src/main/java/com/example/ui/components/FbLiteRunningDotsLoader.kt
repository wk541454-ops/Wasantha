package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Authentic Facebook Lite 4-Dots Running Loading Animation.
 * Displays 4 animated dots that pulse, scale, and travel in a fluid left-to-right wave
 * exactly like the iconic FB Lite app loading indicator ("තිත් 4ක් දුවන FB Lite loading").
 */
@Composable
fun FbLiteRunningDotsLoader(
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
    spacing: Dp = 8.dp,
    dotColor: Color = Color(0xFF1877F2), // FB Lite Signature Blue
    travelHeight: Dp = 6.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fbLiteDotsTransition")

    // Staggered wave cycle across 1200ms
    val duration = 1200

    // Animation progress for each of the 4 dots
    val dot0Anim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot0"
    )

    Row(
        modifier = modifier.testTag("fb_lite_running_dots_loader"),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 4) {
            // Calculate wave offset per dot (0.0, 0.2, 0.4, 0.6)
            val offsetFraction = (i * 0.18f)
            val adjustedProgress = (dot0Anim + offsetFraction) % 1f

            // Sinusoidal bell curve peak around progress = 0.5
            val sinFactor = kotlin.math.sin(adjustedProgress * Math.PI).toFloat().coerceAtLeast(0f)

            val scale = 0.75f + (sinFactor * 0.55f) // Scale from 0.75 to 1.30
            val alpha = 0.35f + (sinFactor * 0.65f) // Alpha from 0.35 to 1.00
            val yOffset = -travelHeight * sinFactor  // Slight upward lift

            Box(
                modifier = Modifier
                    .offset(y = yOffset)
                    .size(dotSize)
                    .scale(scale)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}
