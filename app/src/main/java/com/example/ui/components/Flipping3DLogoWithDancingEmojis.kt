package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Model representing a tiny steam-like dancing emoji particle floating upwards gracefully from the circular icon.
 */
data class TinyDancingEmojiParticle(
    val id: Long,
    val emoji: String,
    val originX: Float,
    val originY: Float,
    val targetSpreadX: Float,
    val travelDistanceY: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val swayPhase: Float,
    val wobbleFrequency: Float,
    val maxTilt: Float,
    val fontSizeSp: Float,
    val delayMs: Long,
    val durationMs: Long,
    val startTimeMs: Long
)

/**
 * 3D Flipping Logo Icon - Strictly CIRCULAR (රවුමට):
 * Side 1: Circular glossy 3D "fn" photo logo.
 * Side 2: Circular dark metallic steam vent portal (no static emoji).
 * Cycle:
 * - Shows photo for 4.5s
 * - Flips to vent side
 * - Stays on vent side for a full 3 seconds (තත්පර 3ක් රැඳී තිබේ)
 * - Releases a continuous stream of tiny emojis floating slowly and gracefully upwards ("වේගය අඩු කර නට නටා උඩට යන්න")
 * - Flips back to photo side
 */
@Composable
fun Flipping3DFriendHubLogo(
    onTriggerEmojiBurst: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val flipAngle = remember { Animatable(0f) }
    var iconCenterOffset by remember { mutableStateOf(Offset(75f, 100f)) }

    // Steaming portal energy ring animation
    val steamRingTransition = rememberInfiniteTransition(label = "steamVent")
    val steamVentRotation by steamRingTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ventRotation"
    )
    val steamGlowPulse by steamRingTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Periodic automatic flip and steam-out cycle
    LaunchedEffect(Unit) {
        while (true) {
            // Stay showing the 3D "fn" photo logo for 4.5 seconds
            delay(4500L)

            // Flip 0° -> 180° smoothly over 700ms
            flipAngle.animateTo(
                targetValue = 180f,
                animationSpec = tween(
                    durationMillis = 700,
                    easing = FastOutSlowInEasing
                )
            )

            // Trigger the slow, graceful steam emoji emission as the vent is fully open
            onTriggerEmojiBurst(iconCenterOffset)

            // Stay on the venting side for 3 FULL SECONDS (තත්පර 3ක් තියෙන්න හරින්න)
            delay(3000L)

            // Flip 180° -> 360° back to the 3D "fn" photo logo
            flipAngle.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 700,
                    easing = FastOutSlowInEasing
                )
            )
            flipAngle.snapTo(0f)
        }
    }

    // Measure exact global position in root so steam originates right from the upper rim
    Box(
        modifier = modifier
            .size(36.dp)
            .onGloballyPositioned { coordinates ->
                val rootPos = coordinates.positionInRoot()
                val size = coordinates.size
                iconCenterOffset = Offset(
                    x = rootPos.x + size.width / 2f,
                    y = rootPos.y + (size.height * 0.2f)
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Interactive manual tap: smoothly flip to vent, wait 3 seconds, and return
                coroutineScope.launch {
                    val currentVal = flipAngle.value
                    if (currentVal < 90f || currentVal >= 270f) {
                        flipAngle.animateTo(
                            targetValue = 180f,
                            animationSpec = tween(700, easing = FastOutSlowInEasing)
                        )
                        onTriggerEmojiBurst(iconCenterOffset)
                        delay(3000L)
                        flipAngle.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(700, easing = FastOutSlowInEasing)
                        )
                        flipAngle.snapTo(0f)
                    } else {
                        flipAngle.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(700, easing = FastOutSlowInEasing)
                        )
                        flipAngle.snapTo(0f)
                    }
                }
            }
            .testTag("flipping_3d_friendhub_logo"),
        contentAlignment = Alignment.Center
    ) {
        val currentAngle = flipAngle.value
        val normalizedAngle = ((currentAngle % 360f) + 360f) % 360f
        val showingFront = normalizedAngle <= 90f || normalizedAngle >= 270f

        if (showingFront) {
            // SIDE 1: Circular (රවුම) 3D "fn" Logo Photo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = currentAngle
                        cameraDistance = 16f * density.density
                    }
                    .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color(0xFF1E3A8A), spotColor = Color(0xFF38BDF8))
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(
                        1.5.dp,
                        Brush.sweepGradient(
                            listOf(
                                Color(0xFF38BDF8),
                                Color(0xFF2563EB),
                                Color(0xFF60A5FA),
                                Color(0xFF38BDF8)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo_fn),
                    contentDescription = "FriendHub 3D Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Sleek glossy dome light curvature reflection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        } else {
            // SIDE 2: Circular Steam Vent / Portal (NO static emoji inside!)
            // Stays visible for 3 seconds while tiny emojis steam upwards
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = currentAngle - 180f
                        cameraDistance = 16f * density.density
                    }
                    .shadow(elevation = 8.dp, shape = CircleShape, ambientColor = Color(0xFF0284C7), spotColor = Color(0xFF38BDF8))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF090D16)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.sweepGradient(
                            listOf(
                                Color(0xFF38BDF8),
                                Color(0xFF818CF8),
                                Color(0xFFC084FC),
                                Color(0xFF38BDF8)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Spinning energy ring representing the steam vent opening
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = steamVentRotation
                            scaleX = steamGlowPulse
                            scaleY = steamGlowPulse
                        }
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.8f),
                                    Color.Transparent,
                                    Color(0xFFC084FC).copy(alpha = 0.8f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                // Soft inner glowing core of the steam vent (no static emoji)
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.7f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

/**
 * Fullscreen overlay that renders tiny steam-like emojis bubbling and steaming upwards
 * smoothly and gently at a slower, relaxed pace ("වේගය අඩු කර නට නටා සෙමින් උඩට යන්න").
 * Features:
 * - Slower, gentle upward floating speed (3.5 to 4.2 seconds duration)
 * - Tiny compact size suited for top bar space (9sp - 13sp)
 * - Continuous chimney/steam plume upward flow (one every 95ms across the 3 seconds)
 * - Soft, graceful horizontal dancing sway ("nata nata yanna")
 * - Gentle rotational wobbles like delicate steam bubbles
 * - Zero touch blocking
 */
@Composable
fun FloatingDancingEmojisOverlay(
    burstTrigger: Long,
    burstOrigin: Offset,
    modifier: Modifier = Modifier
) {
    var activeParticles by remember { mutableStateOf(listOf<TinyDancingEmojiParticle>()) }
    val emojiList = remember {
        listOf(
            "❤️", "👍", "💖", "🔥", "✨",
            "🥰", "💕", "💓", "🌟", "💫",
            "❤️", "😍", "🎉", "💙", "💗",
            "👍", "🔥", "✨", "❤️", "🥰",
            "💕", "🌟", "💓", "👍", "💖"
        )
    }

    // Trigger tiny steam burst whenever burstTrigger updates
    LaunchedEffect(burstTrigger) {
        if (burstTrigger <= 0L) return@LaunchedEffect

        val now = System.currentTimeMillis()
        val newParticles = mutableListOf<TinyDancingEmojiParticle>()

        // 20 tiny emojis released steadily over ~2.4 seconds while vent is open
        val count = 20
        for (i in 0 until count) {
            val emoji = emojiList[i % emojiList.size]

            // Steam chimney column: origin strictly centered at top rim of the 3D circular logo
            val horizontalDrift = (Random.nextFloat() - 0.5f) * 12f // tight +/- 6px chimney column
            val driftBias = (Random.nextFloat() * 8f) - 4f

            // Gentle upward floating distance (120px to 190px upwards)
            val travelDistanceY = -(Random.nextFloat() * 70f + 120f)

            // Very smooth, delicate dancing sway ("chuti chuti emoji nata nata yanna")
            val swayAmp = Random.nextFloat() * 5f + 3f // 3px to 8px compact gentle sway
            val swayFreq = Random.nextFloat() * 1.0f + 1.2f // relaxed gentle tempo
            val swayPhase = Random.nextFloat() * (2f * PI.toFloat())
            val wobbleFreq = Random.nextFloat() * 1.2f + 1.2f
            val maxTilt = Random.nextFloat() * 12f + 6f

            // Extremely compact "chuti" size matching top bar proportions
            val fontSizeSp = Random.nextFloat() * 3f + 8f // 8sp to 11sp (chuti size)

            // Continuous steam stream released while the vent is flipped open for 3 seconds
            val delayMs = i * 110L
            // Very slow, peaceful floating travel speed (3.2s to 3.8s)
            val durationMs = Random.nextLong(3200L, 3800L)

            newParticles.add(
                TinyDancingEmojiParticle(
                    id = now + i,
                    emoji = emoji,
                    originX = burstOrigin.x,
                    originY = burstOrigin.y,
                    targetSpreadX = horizontalDrift + driftBias,
                    travelDistanceY = travelDistanceY,
                    swayAmplitude = swayAmp,
                    swayFrequency = swayFreq,
                    swayPhase = swayPhase,
                    wobbleFrequency = wobbleFreq,
                    maxTilt = maxTilt,
                    fontSizeSp = fontSizeSp,
                    delayMs = delayMs,
                    durationMs = durationMs,
                    startTimeMs = now
                )
            )
        }

        // Keep active list fresh
        activeParticles = (activeParticles + newParticles).takeLast(60)
    }

    // Frame animation loop
    var currentTimeMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(activeParticles.isNotEmpty()) {
        if (activeParticles.isNotEmpty()) {
            while (activeParticles.isNotEmpty()) {
                withFrameMillis {
                    currentTimeMs = System.currentTimeMillis()
                }
                activeParticles = activeParticles.filter { particle ->
                    (currentTimeMs - particle.startTimeMs) < (particle.delayMs + particle.durationMs)
                }
            }
        }
    }

    if (activeParticles.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("floating_dancing_emojis_overlay")
    ) {
        activeParticles.forEach { particle ->
            val elapsed = currentTimeMs - particle.startTimeMs - particle.delayMs
            if (elapsed > 0) {
                val progress = (elapsed.toFloat() / particle.durationMs.toFloat()).coerceIn(0f, 1f)

                // 1. Slow, buoyant upward rising motion (linear smooth float like gentle smoke/steam)
                val smoothProgress = progress * (2f - progress) // Ease-out gentle deceleration
                val currentY = particle.originY + (particle.travelDistanceY * smoothProgress)

                // 2. Gentle Horizontal Dancing Sway ("nata nata yanna")
                val baseSpread = particle.targetSpreadX * smoothProgress
                val sway = sin((progress * particle.swayFrequency * 2f * PI.toFloat()) + particle.swayPhase) * particle.swayAmplitude
                val currentX = particle.originX + baseSpread + sway

                // 3. Delicate rotational tilt
                val tilt = sin((progress * particle.wobbleFrequency * 2f * PI.toFloat()) + particle.swayPhase) * particle.maxTilt

                // 4. Smooth scale and gentle vapor fade-out
                val scale = when {
                    progress < 0.10f -> (progress / 0.10f) * 1.10f
                    progress < 0.20f -> 1.10f - ((progress - 0.10f) / 0.10f) * 0.10f
                    else -> 1.0f + 0.05f * sin(progress * 3f * PI.toFloat())
                }

                val alpha = when {
                    progress < 0.06f -> progress / 0.06f
                    progress > 0.60f -> ((1f - progress) / 0.40f).coerceIn(0f, 1f)
                    else -> 0.92f
                }

                Text(
                    text = particle.emoji,
                    fontSize = particle.fontSizeSp.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = currentX - 7.dp.toPx()
                            translationY = currentY - 7.dp.toPx()
                            rotationZ = tilt
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                )
            }
        }
    }
}
