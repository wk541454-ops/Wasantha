package com.example.ui.components.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

data class AmbientParticleNode(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val alpha: Float,
    val speed: Float,
    val isCyan: Boolean
)

@Composable
fun AmbientParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_space")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    // Pre-allocated static nodes so no allocations in onDraw
    val nodes = remember {
        listOf(
            AmbientParticleNode(0.12f, 0.18f, 2.5f, 0.45f, 1.2f, true),
            AmbientParticleNode(0.85f, 0.12f, 3.5f, 0.55f, 0.9f, false),
            AmbientParticleNode(0.32f, 0.38f, 2.0f, 0.35f, 1.5f, true),
            AmbientParticleNode(0.78f, 0.45f, 4.0f, 0.40f, 0.8f, false),
            AmbientParticleNode(0.15f, 0.65f, 3.0f, 0.50f, 1.1f, true),
            AmbientParticleNode(0.90f, 0.72f, 2.2f, 0.35f, 1.4f, false),
            AmbientParticleNode(0.48f, 0.82f, 4.5f, 0.30f, 0.7f, true),
            AmbientParticleNode(0.25f, 0.92f, 2.0f, 0.40f, 1.3f, false),
            AmbientParticleNode(0.68f, 0.25f, 3.2f, 0.45f, 1.0f, true),
            AmbientParticleNode(0.55f, 0.58f, 2.8f, 0.38f, 1.15f, false)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Deep subtle radial lighting in background
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF06B6D4).copy(alpha = 0.04f),
                    Color(0xFF10B981).copy(alpha = 0.02f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.8f, size.height * 0.2f),
                radius = size.width * 0.7f
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF8B5CF6).copy(alpha = 0.03f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.2f, size.height * 0.7f),
                radius = size.width * 0.6f
            )
        )

        // Floating particles
        nodes.forEach { node ->
            val dynamicX = (node.xRatio * size.width) + (sin(phase * node.speed + node.yRatio * 10f) * 20f)
            val dynamicY = (node.yRatio * size.height) - ((phase * 15f * node.speed) % 40f)
            val pulseAlpha = (node.alpha * (0.7f + 0.3f * sin(phase * node.speed * 2f))).coerceIn(0f, 1f)

            drawCircle(
                color = if (node.isCyan) Color(0xFF06B6D4).copy(alpha = pulseAlpha)
                        else Color(0xFF10B981).copy(alpha = pulseAlpha),
                radius = node.radius,
                center = Offset(dynamicX, dynamicY)
            )

            // Soft glow aura
            drawCircle(
                color = if (node.isCyan) Color(0xFF06B6D4).copy(alpha = pulseAlpha * 0.3f)
                        else Color(0xFF10B981).copy(alpha = pulseAlpha * 0.3f),
                radius = node.radius * 2.8f,
                center = Offset(dynamicX, dynamicY)
            )
        }
    }
}
