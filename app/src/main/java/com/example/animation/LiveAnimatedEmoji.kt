package com.example.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LiveAnimatedEmoji(
    emoji: String,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emojiAnimation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiScale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiRotation"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiOffset"
    )

    val isHeart = emoji.contains("❤️") || emoji.contains("💖") || emoji.contains("💗") || emoji.contains("💓")
    val isLaugh = emoji.contains("😂") || emoji.contains("😆") || emoji.contains("🤣")
    val isCry = emoji.contains("😢") || emoji.contains("😭")
    val isFire = emoji.contains("🔥")
    val isCrown = emoji.contains("👑") || emoji.contains("🪙") || emoji.contains("🎁")

    val appliedScale = if (isHeart || isFire) scale else 1f
    val appliedRotation = if (isLaugh) rotation else 0f
    val appliedOffsetY = if (isCry || isCrown || isLaugh) offsetY else 0f

    Box(
        modifier = modifier
            .offset(y = appliedOffsetY.dp)
            .scale(appliedScale)
            .rotate(appliedRotation),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = fontSize
        )
    }
}
