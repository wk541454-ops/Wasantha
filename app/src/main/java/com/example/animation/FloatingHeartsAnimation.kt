package com.example.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.HeartRed
import java.util.UUID
import kotlin.math.sin
import kotlin.random.Random

data class HeartParticle(
    val id: String = UUID.randomUUID().toString(),
    val startOffsetX: Float, // in dp
    val startOffsetY: Float = -20f, // in dp
    val totalRiseDistance: Float = 950f, // in dp - rises all the way to the top and off-screen
    val swayAmplitude: Float = 22f, // in dp
    val swayFrequency: Float = 1.0f,
    val iconSize: Dp = 24.dp,
    val color: Color = Color.White,
    val icon: ImageVector? = null,
    val emoji: String? = null,
    val durationMillis: Int = 3600,
    val progress: Animatable<Float, *> = Animatable(0f)
)

class FloatingHeartsState {
    val particles = mutableStateListOf<HeartParticle>()

    fun emitHearts(count: Int = 100) {
        emitRightSideHearts(count)
    }

    fun emitRightSideHearts(count: Int = 100, reaction: String = "LIKE") {
        val defaultEmojis = listOf("👍", "❤️", "💖", "🔥", "✨", "🥰", "😍", "🎉")
        val targetCount = count.coerceIn(10, 120)
        for (i in 0 until targetCount) {
            val emoji = when (reaction) {
                "LIKE", "DEFAULT" -> defaultEmojis.random()
                "👍" -> if (Random.nextFloat() > 0.3f) "👍" else listOf("❤️", "🔥", "✨", "🎉").random()
                "❤️" -> if (Random.nextFloat() > 0.3f) "❤️" else listOf("💖", "🥰", "✨", "🌹").random()
                "😂" -> if (Random.nextFloat() > 0.3f) "😂" else listOf("😆", "🤣", "✨", "🔥").random()
                "😮" -> if (Random.nextFloat() > 0.3f) "😮" else listOf("😲", "✨", "⚡").random()
                "😢" -> if (Random.nextFloat() > 0.3f) "😢" else listOf("🥺", "💧", "💔").random()
                "😡" -> if (Random.nextFloat() > 0.3f) "😡" else listOf("🤬", "🔥", "⚡").random()
                else -> if (reaction.isNotBlank() && reaction.length <= 4 && !reaction.equals("LIKE", ignoreCase = true)) reaction else defaultEmojis.random()
            }
            val particle = HeartParticle(
                // Spread across the full width of the screen so emojis fill the view
                startOffsetX = -(Random.nextFloat() * 340f - 20f),
                startOffsetY = -10f - Random.nextFloat() * 80f,
                // Rises all the way past the top edge of any mobile screen (900dp - 1250dp)
                totalRiseDistance = 900f + Random.nextFloat() * 350f,
                swayAmplitude = 16f + Random.nextFloat() * 32f,
                swayFrequency = 0.8f + Random.nextFloat() * 1.4f,
                iconSize = (20 + Random.nextInt(16)).dp,
                color = when (emoji) {
                    "❤️", "💖", "💕", "💘" -> HeartRed
                    "✨", "🥰", "🌹" -> Color(0xFFFE2C55)
                    "🔥" -> Color(0xFFFFB300)
                    else -> Color.White
                },
                emoji = emoji,
                durationMillis = 2800 + Random.nextInt(2200)
            )
            particles.add(particle)
        }
    }

    fun emitHeartsAt(xDp: Float, yDp: Float, count: Int = 100, reaction: String = "❤️") {
        val emojis = listOf("❤️", "✨", "🔥", "💖", "⚡", "😍", "🎉")
        val targetCount = count.coerceIn(10, 120)
        for (i in 0 until targetCount) {
            val emoji = if (reaction == "❤️") emojis.random() else reaction
            val particle = HeartParticle(
                startOffsetX = xDp + (Random.nextFloat() * 240f - 120f),
                startOffsetY = yDp,
                totalRiseDistance = 900f + Random.nextFloat() * 350f,
                swayAmplitude = 20f + Random.nextFloat() * 30f,
                swayFrequency = 0.9f + Random.nextFloat() * 1.5f,
                iconSize = (20 + Random.nextInt(16)).dp,
                color = when (emoji) {
                    "❤️", "💖" -> HeartRed
                    "⚡", "✨" -> Color(0xFF00F5FF)
                    "🔥" -> Color(0xFFFFB300)
                    else -> Color.White
                },
                emoji = emoji,
                durationMillis = 3000 + Random.nextInt(2000)
            )
            particles.add(particle)
        }
    }

    fun emitReaction(reaction: String, count: Int = 100) {
        emitRightSideHearts(count, reaction)
    }
}

@Composable
fun rememberFloatingHeartsState(): FloatingHeartsState {
    return remember { FloatingHeartsState() }
}

@Composable
fun FloatingHeartsOverlay(
    state: FloatingHeartsState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomEnd
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier,
        contentAlignment = alignment
    ) {
        state.particles.toList().forEach { particle ->
            LaunchedEffect(particle.id) {
                particle.progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = particle.durationMillis,
                        easing = LinearEasing
                    )
                )
                state.particles.remove(particle)
            }

            val p = particle.progress.value
            val currentY = particle.startOffsetY - (p * particle.totalRiseDistance)
            val currentX = particle.startOffsetX + sin(p * Math.PI.toFloat() * particle.swayFrequency) * particle.swayAmplitude
            
            // Full opacity until the very end of the screen (top 12%), then fades out off-screen
            val alpha = if (p < 0.08f) (p / 0.08f) else if (p > 0.88f) ((1f - p) / 0.12f) else 1f
            val scale = if (p < 0.1f) 0.5f + (p / 0.1f) * 0.5f else 1f

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = with(density) { currentX.dp.roundToPx() },
                            y = with(density) { currentY.dp.roundToPx() }
                        )
                    }
                    .alpha(alpha.coerceIn(0f, 1f))
                    .scale(scale.coerceIn(0.3f, 1.4f)),
                contentAlignment = Alignment.Center
            ) {
                if (particle.icon != null) {
                    Icon(
                        imageVector = particle.icon,
                        contentDescription = null,
                        tint = particle.color,
                        modifier = Modifier.size(particle.iconSize)
                    )
                } else if (particle.emoji != null) {
                    Text(
                        text = particle.emoji,
                        fontSize = with(density) { particle.iconSize.toSp() }
                    )
                }
            }
        }
    }
}
