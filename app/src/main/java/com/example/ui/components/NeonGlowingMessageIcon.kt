package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NeonGlowingMessageIcon(
    unreadCount: Int = 2,
    size: Dp = 44.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String = "neon_message_icon"
) {
    val coroutineScope = rememberCoroutineScope()
    var isFlashing by remember { mutableStateOf(false) }

    // Ambient Breathing Neon Glow
    val infiniteTransition = rememberInfiniteTransition(label = "neonAmbient")
    val ambientGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlowAlpha"
    )

    // Flash scale and shockwave animatables
    val flashScale = remember { Animatable(1.0f) }
    val flashAlpha = remember { Animatable(0f) }
    val ringExpansion = remember { Animatable(1.0f) }

    val handleTap = {
        coroutineScope.launch {
            if (!isFlashing) {
                isFlashing = true
                // Launch parallel animations for the neon flash
                launch {
                    flashAlpha.snapTo(1f)
                    flashAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 260, easing = LinearOutSlowInEasing)
                    )
                }
                launch {
                    ringExpansion.snapTo(1.0f)
                    ringExpansion.animateTo(
                        targetValue = 1.8f,
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    flashScale.animateTo(
                        targetValue = 1.3f,
                        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
                    )
                    flashScale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
                delay(220)
                isFlashing = false
                onClick()
            }
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        // 1. Neon Shockwave Flash Wave (when tapped)
        if (flashAlpha.value > 0.01f) {
            Box(
                modifier = Modifier
                    .size(size * ringExpansion.value)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0xFF06B6D4).copy(alpha = flashAlpha.value),
                                Color(0xFF10B981).copy(alpha = flashAlpha.value * 0.7f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF06B6D4).copy(alpha = flashAlpha.value * 0.45f),
                                Color(0xFF10B981).copy(alpha = flashAlpha.value * 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // 2. Ambient Glowing Halo
        Box(
            modifier = Modifier
                .size(size)
                .scale(flashScale.value)
                .shadow(
                    elevation = if (isFlashing) 16.dp else 8.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF0055FF),
                    spotColor = Color(0xFF00C6FF)
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0055FF).copy(alpha = if (isFlashing) 0.85f else ambientGlowAlpha * 0.40f),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .border(
                    width = if (isFlashing) 2.dp else 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF0066FF).copy(alpha = if (isFlashing) 1.0f else ambientGlowAlpha),
                            Color(0xFF00C6FF).copy(alpha = if (isFlashing) 1.0f else ambientGlowAlpha * 0.8f)
                        )
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    handleTap()
                },
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(
                            containerColor = Color(0xFFE41E3F), // Messenger Red Badge
                            contentColor = Color.White,
                            modifier = Modifier.offset(x = (-2).dp, y = 2.dp)
                        ) {
                            Text(
                                text = "$unreadCount",
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_3d_chat_bubble_vector),
                    contentDescription = "Messenger",
                    modifier = Modifier.size(size * 0.85f)
                )
            }
        }
    }
}
