package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Animated Swinging Blue Notification Bell Icon ("Selavi Selavi Thiyenna").
 * Features the bright electric blue notification bell with red badge "1"
 * swinging smoothly side-to-side from top pivot point.
 */
@Composable
fun AnimatedNotificationBellIcon(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    badgeCount: Int = 1,
    animateSwinging: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bell_ringing")

    // Swinging rotation angle (-16 degrees to +16 degrees)
    val swingAnimation by infiniteTransition.animateFloat(
        initialValue = -16f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                -16f at 0 with FastOutSlowInEasing
                16f at 300 with FastOutSlowInEasing
                -12f at 600 with FastOutSlowInEasing
                12f at 800 with FastOutSlowInEasing
                0f at 1000 with LinearEasing
                0f at 1200 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bell_swing"
    )
    val swingAngle = if (animateSwinging) swingAnimation else 0f

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = swingAngle
                transformOrigin = TransformOrigin(0.5f, 0.1f) // Swing pivot from top loop of bell
            },
        contentAlignment = Alignment.Center
    ) {
        // Bell Vector Image (Transparent background)
        Image(
            painter = painterResource(id = R.drawable.ic_notification_bell_vector),
            contentDescription = "Notification Bell",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
