package com.example.ui.components.live

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.random.Random

enum class GiftAnimationType {
    NONE,
    EMOJI_BURST,
    ROCKET,
    DIAMOND_RAIN,
    CROWN
}

data class EmojiParticle(
    val id: Int,
    val xRatio: Float,
    val startYRatio: Float,
    val riseDistance: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val fontSizeSp: Float,
    val rotationDeg: Float,
    val delayOffset: Float
)

data class DiamondParticle(
    val id: Int,
    val xRatio: Float,
    val speed: Float,
    val size: Float,
    val delayOffset: Float
)

@Composable
fun GiftAnimationOverlay(
    gift: SendableGift?,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (gift == null) return

    val animationType = gift.animationType
    val animProgress = remember(gift) { Animatable(0f) }

    LaunchedEffect(gift) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = when (animationType) {
                    GiftAnimationType.EMOJI_BURST -> 3200
                    GiftAnimationType.ROCKET -> 3200
                    GiftAnimationType.DIAMOND_RAIN -> 3500
                    GiftAnimationType.CROWN -> 3000
                    GiftAnimationType.NONE -> 0
                },
                easing = FastOutSlowInEasing
            )
        )
        onAnimationEnd()
    }

    val progress = animProgress.value

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = if (progress > 0.85f) (1f - progress) / 0.15f else 1f },
        contentAlignment = Alignment.Center
    ) {
        when (animationType) {
            GiftAnimationType.EMOJI_BURST -> {
                EmojiBurstAnimation(progress = progress, giftEmoji = gift.emoji, giftName = gift.name)
            }
            GiftAnimationType.ROCKET -> {
                RocketLaunchAnimation(progress = progress)
            }
            GiftAnimationType.DIAMOND_RAIN -> {
                DiamondRainAnimation(progress = progress)
            }
            GiftAnimationType.CROWN -> {
                CrownGiftAnimation(progress = progress)
            }
            GiftAnimationType.NONE -> {}
        }
    }
}

@Composable
private fun EmojiBurstAnimation(progress: Float, giftEmoji: String, giftName: String) {
    val particles = remember(giftEmoji) {
        List(55) { i ->
            EmojiParticle(
                id = i,
                xRatio = Random.nextFloat() * 0.88f + 0.06f,
                startYRatio = Random.nextFloat() * 0.2f + 0.8f,
                riseDistance = Random.nextFloat() * 1100f + 800f,
                swayAmplitude = Random.nextFloat() * 45f + 20f,
                swayFrequency = Random.nextFloat() * 1.8f + 1f,
                fontSizeSp = Random.nextFloat() * 26f + 22f,
                rotationDeg = Random.nextFloat() * 360f - 180f,
                delayOffset = Random.nextFloat() * 0.35f
            )
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Floating high-up emoji particles across entire screen
        particles.forEach { p ->
            val adjProgress = ((progress - p.delayOffset) / (1f - p.delayOffset)).coerceIn(0f, 1f)
            if (adjProgress > 0f) {
                val startX = p.xRatio * widthPx
                val startY = p.startYRatio * heightPx
                val currentY = startY - (adjProgress * p.riseDistance)
                val currentX = startX + sin(adjProgress * Math.PI.toFloat() * p.swayFrequency) * p.swayAmplitude
                val alpha = if (adjProgress < 0.12f) adjProgress / 0.12f else (1f - adjProgress * 0.85f).coerceIn(0f, 1f)
                val scale = if (adjProgress < 0.18f) (adjProgress / 0.18f) * 1.3f else (1.3f - adjProgress * 0.45f)

                Text(
                    text = giftEmoji,
                    fontSize = p.fontSizeSp.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = currentX
                            translationY = currentY
                            this.alpha = alpha
                            this.scaleX = scale
                            this.scaleY = scale
                            this.rotationZ = p.rotationDeg * adjProgress
                        }
                )
            }
        }

        // Center Hero Banner for the Gift
        val centerScale = if (progress < 0.3f) {
            (progress / 0.3f) * 2.4f
        } else {
            2.4f + (progress - 0.3f) * 0.3f
        }
        val centerAlpha = if (progress < 0.12f) progress / 0.12f else if (progress > 0.8f) (1f - progress) / 0.2f else 1f

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = centerScale.coerceIn(0.1f, 3f)
                    scaleY = centerScale.coerceIn(0.1f, 3f)
                    this.alpha = centerAlpha.coerceIn(0f, 1f)
                }
        ) {
            Text(text = giftEmoji, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFE2C55), Color(0xFFFF9900))))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${giftName.uppercase()}! $giftEmoji✨",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun RocketLaunchAnimation(progress: Float) {
    val rocketY = 1.2f - (progress * 1.8f) // From bottom to off-screen top
    val rocketScale = 1f + (progress * 1.2f)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Particle exhaust trails
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * rocketY

            if (rocketY in -0.2f..1.1f) {
                // Smoke and fire glow
                for (i in 0 until 18) {
                    val spreadX = (Random.nextFloat() - 0.5f) * 90f
                    val offsetY = Random.nextFloat() * 120f + 30f
                    val alpha = (1f - (offsetY / 150f)).coerceIn(0f, 0.8f)

                    drawCircle(
                        color = if (i % 2 == 0) Color(0xFFFF5722).copy(alpha = alpha) else Color(0xFFFFEB3B).copy(alpha = alpha),
                        radius = Random.nextFloat() * 14f + 6f,
                        center = Offset(cx + spreadX, cy + offsetY)
                    )
                }

                // Cyan neon warp streaks
                for (j in 0 until 10) {
                    val sx = (Random.nextFloat() - 0.5f) * size.width * 0.8f
                    val sy = Random.nextFloat() * size.height
                    drawLine(
                        color = Color(0xFF00F5FF).copy(alpha = 0.5f),
                        start = Offset(cx + sx, sy),
                        end = Offset(cx + sx, sy + 60f),
                        strokeWidth = 3f
                    )
                }
            }
        }

        // Rocket body
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .graphicsLayer {
                    translationY = (rocketY - 0.5f) * 1200f
                    scaleX = rocketScale
                    scaleY = rocketScale
                }
        ) {
            Text(text = "🚀", fontSize = 84.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "NEON ROCKET BLAST! 🚀⚡",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DiamondRainAnimation(progress: Float) {
    val diamonds = remember {
        List(40) { i ->
            DiamondParticle(
                id = i,
                xRatio = (i % 10) * 0.1f + Random.nextFloat() * 0.08f,
                speed = Random.nextFloat() * 0.6f + 0.8f,
                size = Random.nextFloat() * 20f + 16f,
                delayOffset = Random.nextFloat() * 0.35f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            diamonds.forEach { d ->
                val adjustedProgress = ((progress - d.delayOffset) / (1f - d.delayOffset)).coerceIn(0f, 1f)
                if (adjustedProgress > 0f) {
                    val x = d.xRatio * size.width
                    val y = adjustedProgress * size.height * 1.3f * d.speed - 50f
                    val alpha = if (adjustedProgress < 0.15f) adjustedProgress / 0.15f else (1f - adjustedProgress * 0.7f).coerceIn(0f, 1f)

                    // Draw glowing diamond shape
                    drawCircle(
                        color = Color(0xFF00F5FF).copy(alpha = alpha * 0.85f),
                        radius = d.size * 0.65f,
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color(0xFF10B981).copy(alpha = alpha * 0.95f),
                        radius = d.size * 0.35f,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // Center Big Diamond
        val centerScale = (sin(progress * Math.PI.toFloat()) * 1.8f).coerceAtLeast(0.1f)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = centerScale
                    scaleY = centerScale
                }
        ) {
            Text(text = "💎", fontSize = 72.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF10B981))))
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "DIAMOND SHOWER! 💎✨",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CrownGiftAnimation(progress: Float) {
    val crownScale = (sin(progress * Math.PI.toFloat()) * 2.2f).coerceAtLeast(0.1f)
    val floatY = (1f - progress) * -120f

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                translationY = floatY
                scaleX = crownScale
                scaleY = crownScale
            }
        ) {
            Text(text = "👑", fontSize = 84.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF8C00))))
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "VIP CROWN DONATION! 👑🌟",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
