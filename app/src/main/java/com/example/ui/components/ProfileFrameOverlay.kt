package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ProfileFrameItem(
    val id: String,
    val nameSinhala: String,
    val nameEnglish: String,
    val emoji: String,
    val description: String
)

val availableProfileFrames = listOf(
    ProfileFrameItem(
        id = "none",
        nameSinhala = "නැත",
        nameEnglish = "None",
        emoji = "⚪",
        description = "මුල් පින්තූරය (No frame)"
    ),
    ProfileFrameItem(
        id = "hearts",
        nameSinhala = "ආදරය",
        nameEnglish = "Hearts",
        emoji = "❤️",
        description = "රෝස සහ රතු හදවත් (Floating Hearts)"
    ),
    ProfileFrameItem(
        id = "dove",
        nameSinhala = "සාමය",
        nameEnglish = "Peace",
        emoji = "🕊️",
        description = "සාමයේ සුදු පරවියා (Peace Dove)"
    ),
    ProfileFrameItem(
        id = "world",
        nameSinhala = "ලෝකය",
        nameEnglish = "World",
        emoji = "🌍",
        description = "පෘථිවි ගෝලය (Earth Globe)"
    ),
    ProfileFrameItem(
        id = "party",
        nameSinhala = "සාදය",
        nameEnglish = "Celebration",
        emoji = "🎉",
        description = "සාදයේ බැලූන් සහ සැරසිලි (Balloons & Confetti)"
    ),
    ProfileFrameItem(
        id = "flag",
        nameSinhala = "ශ්‍රී ලංකා",
        nameEnglish = "National",
        emoji = "🇱🇰",
        description = "ශ්‍රී ලංකා ජාතික අභිමානය (Sri Lanka Pride)"
    ),
    ProfileFrameItem(
        id = "gold",
        nameSinhala = "VIP රන්",
        nameEnglish = "VIP Gold",
        emoji = "✨",
        description = "දිදුලන රන් රාමුව (Golden Luxury Ring)"
    ),
    ProfileFrameItem(
        id = "neon",
        nameSinhala = "නියෝන්",
        nameEnglish = "Cyber Neon",
        emoji = "⚡",
        description = "Cyberpunk Neon Glow (නියෝන් ආලෝකය)"
    )
)

@Composable
fun ProfileFrameOverlay(
    frameId: String?,
    modifier: Modifier = Modifier
) {
    if (frameId.isNullOrBlank() || frameId == "none") return

    Box(modifier = modifier.fillMaxSize()) {
        when (frameId) {
            "hearts" -> {
                // Heart Ring with floating hearts
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFFF4081),
                                Color(0xFFFF1744),
                                Color(0xFFFF80AB),
                                Color(0xFFFF4081)
                            )
                        ),
                        style = Stroke(width = size.width * 0.045f)
                    )
                }
                // Hearts placed around perimeter
                Box(modifier = Modifier.align(Alignment.TopStart).padding(top = 4.dp, start = 8.dp)) {
                    Text(text = "💖", fontSize = 18.sp)
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 6.dp)) {
                    Text(text = "💕", fontSize = 16.sp)
                }
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 6.dp, start = 6.dp)) {
                    Text(text = "❤️", fontSize = 18.sp)
                }
                Box(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 8.dp, end = 6.dp)) {
                    Text(text = "✨", fontSize = 14.sp)
                }
            }
            "dove" -> {
                // Peace Dove overlay at bottom-left corner with olive branch
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF81D4FA),
                                Color(0xFFE1F5FE),
                                Color(0xFF4FC3F7),
                                Color(0xFF81D4FA)
                            )
                        ),
                        style = Stroke(width = size.width * 0.04f)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 2.dp, start = 2.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(4.dp)
                ) {
                    Text(text = "🕊️🌿", fontSize = 18.sp)
                }
            }
            "world" -> {
                // Earth globe badge
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF29B6F6),
                                Color(0xFF00E676),
                                Color(0xFF29B6F6)
                            )
                        ),
                        style = Stroke(width = size.width * 0.04f)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 4.dp, end = 4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1877F2).copy(alpha = 0.9f))
                        .padding(4.dp)
                ) {
                    Text(text = "🌍", fontSize = 18.sp)
                }
            }
            "party" -> {
                // Celebration balloons and confetti
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFFFD600),
                                Color(0xFFFF4081),
                                Color(0xFF00E5FF),
                                Color(0xFFFFD600)
                            )
                        ),
                        style = Stroke(width = size.width * 0.045f)
                    )
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 2.dp, end = 2.dp)) {
                    Text(text = "🎈", fontSize = 18.sp)
                }
                Box(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 2.dp, end = 2.dp)) {
                    Text(text = "🎉", fontSize = 18.sp)
                }
                Box(modifier = Modifier.align(Alignment.TopStart).padding(top = 4.dp, start = 2.dp)) {
                    Text(text = "🥳", fontSize = 14.sp)
                }
            }
            "flag" -> {
                // Sri Lanka National Pride badge
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFFF8F00),
                                Color(0xFF880E4F),
                                Color(0xFF00C853),
                                Color(0xFFFF8F00)
                            )
                        ),
                        style = Stroke(width = size.width * 0.045f)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "🇱🇰 SRI LANKA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            "gold" -> {
                // Golden luxury border
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFF8E1),
                                Color(0xFFFFA000),
                                Color(0xFFFFD700)
                            )
                        ),
                        style = Stroke(width = size.width * 0.05f)
                    )
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    Text(text = "✨", fontSize = 16.sp)
                }
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)) {
                    Text(text = "⭐", fontSize = 14.sp)
                }
            }
            "neon" -> {
                // Cyan and Purple Cyber Neon Glow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF00F5FF),
                                Color(0xFFB000FF),
                                Color(0xFFFF0055),
                                Color(0xFF00F5FF)
                            )
                        ),
                        style = Stroke(width = size.width * 0.05f)
                    )
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    Text(text = "⚡", fontSize = 15.sp)
                }
            }
        }
    }
}
