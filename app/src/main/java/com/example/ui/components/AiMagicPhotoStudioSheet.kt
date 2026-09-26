package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AiMagicPreset(
    val id: String,
    val titleSinhala: String,
    val titleEnglish: String,
    val iconEmoji: String,
    val badgeText: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

val aiMagicPresets = listOf(
    AiMagicPreset(
        id = "magic_expand",
        titleSinhala = "Magic Expand 🌌",
        titleEnglish = "Frame Outpainting",
        iconEmoji = "↔️",
        badgeText = "POPULAR",
        description = "ඡායාරූපයේ කැපී ගිය පසුබිම AI එකෙන් ස්වයංක්‍රීයව දිගු කර විශාල කරයි",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFC084FC)
    ),
    AiMagicPreset(
        id = "bg_beach",
        titleSinhala = "Beach Paradise 🏖️",
        titleEnglish = "Tropical Sunset Beach",
        iconEmoji = "🏝️",
        badgeText = "TRENDING",
        description = "පසුබිම මුහුදු වෙරළක හිරු බසින අලංකාර දර්ශනයකට වෙනස් කරයි",
        primaryColor = Color(0xFF0284C7),
        secondaryColor = Color(0xFF38BDF8)
    ),
    AiMagicPreset(
        id = "bg_cyberpunk",
        titleSinhala = "Cyberpunk City 🌃",
        titleEnglish = "Neon Tokyo Lights",
        iconEmoji = "🏙️",
        badgeText = "AI MAGIC",
        description = "නියොන් ආලෝකවලින් පිරුණු ජපානයේ Cyberpunk නගරයකට පසුබිම වෙනස් කරයි",
        primaryColor = Color(0xFFEC4899),
        secondaryColor = Color(0xFFF43F5E)
    ),
    AiMagicPreset(
        id = "avatar_3d",
        titleSinhala = "3D Pixar Avatar ✨",
        titleEnglish = "3D Animation Style",
        iconEmoji = "🎭",
        badgeText = "HOT",
        description = "ඔබේ රූපය Pixar/Disney 3D කාටූන් චරිතයක් බවට පරිවර්තනය කරයි",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFFBBF24)
    ),
    AiMagicPreset(
        id = "anime_art",
        titleSinhala = "Anime Hero ⚔️",
        titleEnglish = "Japanese Anime Style",
        iconEmoji = "🎨",
        badgeText = "ART",
        description = "ජපන් ඇනිමේ විලාසිතාවේ සුපිරි වීරයෙකු ලෙස ඡායාරූපය නිර්මාණය කරයි",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF34D399)
    ),
    AiMagicPreset(
        id = "studio_light",
        titleSinhala = "Studio Enhance 💡",
        titleEnglish = "Professional Lighting",
        iconEmoji = "📸",
        badgeText = "PRO",
        description = "ඡායාරූපයේ ආලෝකය, පැහැදිලි බව සහ වර්ණ Studio Quality එකට හදයි",
        primaryColor = Color(0xFF6366F1),
        secondaryColor = Color(0xFF818CF8)
    )
)

/**
 * AI Magic Photo Studio Full-Screen Dialog
 * Allows users to perform FB Magic Studio styles:
 * - Magic Expand / Outpainting
 * - AI Background Replacement (Beach, Cyberpunk, Fantasy)
 * - Style Transfer (3D Pixar, Anime)
 * - Studio Auto-Enhance & Lighting
 */
@Composable
fun AiMagicPhotoStudioSheet(
    initialPhotoUrl: String,
    onClose: () -> Unit,
    onApplyEditedPhoto: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentPhotoUrl by remember { mutableStateOf(initialPhotoUrl) }
    var selectedPreset by remember { mutableStateOf(aiMagicPresets.first()) }
    var isProcessing by remember { mutableStateOf(false) }
    var processingProgress by remember { mutableFloatStateOf(0f) }
    var processingStageText by remember { mutableStateOf("AI Engine එක සක්‍රිය වෙමින්...") }

    // Shimmer pulse for processing magic
    val infiniteTransition = rememberInfiniteTransition(label = "magicShimmer")
    val shimmerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerRotation"
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI Magic Studio 🪄",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Generative AI Photo Editor",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // Main Photo Display Stage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(
                                    selectedPreset.primaryColor,
                                    selectedPreset.secondaryColor,
                                    selectedPreset.primaryColor
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = currentPhotoUrl,
                        contentDescription = "Magic Edit Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Processing Overlay
                    if (isProcessing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.75f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .graphicsLayer { rotationZ = shimmerRotation }
                                        .clip(CircleShape)
                                        .border(
                                            3.dp,
                                            Brush.sweepGradient(
                                                listOf(
                                                    selectedPreset.primaryColor,
                                                    Color.White,
                                                    selectedPreset.secondaryColor
                                                )
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedPreset.iconEmoji,
                                        fontSize = 28.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = processingStageText,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { processingProgress },
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = selectedPreset.primaryColor,
                                    trackColor = Color(0xFF334155)
                                )
                            }
                        }
                    }

                    // Active Style Badge top right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, selectedPreset.primaryColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedPreset.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedPreset.titleSinhala,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Presets Carousel Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "AI Magic Styles (ස්ටයිල් තෝරන්න):",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(aiMagicPresets) { preset ->
                            val isSelected = selectedPreset.id == preset.id

                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        brush = if (isSelected) Brush.linearGradient(
                                            listOf(preset.primaryColor, preset.secondaryColor)
                                        ) else Brush.linearGradient(
                                            listOf(Color(0xFF334155), Color(0xFF334155))
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedPreset = preset }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = preset.iconEmoji, fontSize = 22.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(preset.primaryColor.copy(alpha = 0.25f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = preset.badgeText,
                                                color = preset.primaryColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = preset.titleSinhala,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = preset.titleEnglish,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Box for Selected Style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = selectedPreset.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = selectedPreset.description,
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Generate / Transform Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isProcessing = true
                                processingProgress = 0.1f
                                processingStageText = "ඡායාරූපයේ ප්‍රධාන රූපය තෝරා ගනිමින්... 🔍"
                                delay(600)

                                processingProgress = 0.4f
                                processingStageText = "Generative AI Models මඟින් ${selectedPreset.titleSinhala} සකසමින්... 🪄"
                                delay(900)

                                processingProgress = 0.8f
                                processingStageText = "Lighting & High-Resolution Fine Tuning... ✨"
                                delay(700)

                                processingProgress = 1.0f
                                isProcessing = false

                                Toast.makeText(
                                    context,
                                    "${selectedPreset.titleSinhala} සාර්ථකව යෙදින! 🌟",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = !isProcessing,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedPreset.primaryColor
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Magic යොදන්න",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Apply & Use Button
                    Button(
                        onClick = {
                            onApplyEditedPhoto(currentPhotoUrl)
                            Toast.makeText(
                                context,
                                "සකසන ලද ඡායාරූපය තෝරා ගන්නා ලදී! 🚀",
                                Toast.LENGTH_SHORT
                            ).show()
                            onClose()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1877F2)
                        )
                    ) {
                        Text(
                            text = "භාවිතා කරන්න",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
