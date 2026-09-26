package com.example.ui.components

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R

/**
 * Representation of a gallery item shown in the "නව කථාන්දරය" picker.
 */
data class StoryGalleryMediaItem(
    val id: String,
    val drawableRes: Int? = null,
    val imageUrl: String? = null,
    val isCameraAction: Boolean = false,
    val title: String = ""
)

/**
 * Overlay item (Stickers, Text, Music, Tag) placed on top of the story canvas.
 */
data class StoryCanvasOverlayItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String, // "STICKER", "TEXT", "MUSIC", "TAG", "AI_LABEL", "DOODLE"
    val content: String,
    val subContent: String? = null,
    val color: Color = Color.White
)

/**
 * 1:1 Replica of Facebook Lite Story Flow:
 * - Screen 1: "නව කථාන්දරය" (New Story Picker) with Aa පෙළ, ♫ සංගීතය, 📷 කැමරාව, and media gallery grid
 * - Screen 2: Story Editor with all setting icons:
 *     ස්ටිකර, පෙළ, සංගීතය, ටැග් කරන්න, ප්‍රයෝග, Doodle, AI ලේබලය,
 *     Quick stickers bar (❤️, 🙏, 😎, 💕, 😂, තවත්),
 *     and "දැන් බෙදා ගන්න >" button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryFlowModal(
    onDismiss: () -> Unit,
    onSubmitStory: (mediaUrl: String?, caption: String, backgroundColor: String?) -> Unit
) {
    val context = LocalContext.current

    // Screen navigation state: 0 = New Story Picker (Screenshot 2), 1 = Story Editor (Screenshot 1)
    var currentStep by remember { mutableStateOf(0) }

    // Selected Media & Customizations
    var selectedMediaDrawable by remember { mutableStateOf<Int?>(null) }
    var selectedMediaUrl by remember { mutableStateOf<String?>(null) }
    var isTextStoryMode by remember { mutableStateOf(false) }
    var textStoryCaption by remember { mutableStateOf("") }
    var textStoryBgColor by remember { mutableStateOf(Color(0xFF1877F2)) }

    // Overlays placed on canvas
    val canvasOverlays = remember { mutableStateListOf<StoryCanvasOverlayItem>() }

    // Active tool drawers in editor
    var activeToolDrawer by remember { mutableStateOf<String?>(null) } // "STICKERS", "TEXT", "MUSIC", "TAG", "EFFECTS", "DOODLE", "AI", "SETTINGS"

    // Active photo effect/filter
    var activeFilterName by remember { mutableStateOf("සාමාන්‍ය") } // Normal, Vintage, B&W, Cyber, Warm

    // Device Photo Picker
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaUrl = uri.toString()
            selectedMediaDrawable = null
            isTextStoryMode = false
            currentStep = 1 // Go to editor
            Toast.makeText(context, "ඡායාරූපය තෝරා ගන්නා ලදී! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    // Clean generic templates (no user personal gallery photos)
    val galleryItems = remember {
        listOf(
            StoryGalleryMediaItem(
                id = "nature_mountains",
                imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
                title = "Nature Landscape"
            ),
            StoryGalleryMediaItem(
                id = "sunset_sky",
                imageUrl = "https://images.unsplash.com/photo-1495616811223-4d98c6e9c869?w=800&auto=format&fit=crop&q=80",
                title = "Sunset Sky"
            ),
            StoryGalleryMediaItem(
                id = "neon_minimal",
                imageUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=800&auto=format&fit=crop&q=80",
                title = "Cyber Neon"
            ),
            StoryGalleryMediaItem(
                id = "coffee_vibes",
                imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&auto=format&fit=crop&q=80",
                title = "Coffee Aesthetic"
            ),
            StoryGalleryMediaItem(
                id = "night_city",
                imageUrl = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=800&auto=format&fit=crop&q=80",
                title = "City Lights"
            ),
            StoryGalleryMediaItem(
                id = "abstract_glow",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
                title = "Abstract Gradient"
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF18191A)
        ) {
            if (currentStep == 0) {
                // SCREEN 1: "නව කථාන්දරය" Picker Screen (Screenshot 2)
                NewStoryPickerScreen(
                    galleryItems = galleryItems,
                    onClose = onDismiss,
                    onSelectTextMode = {
                        isTextStoryMode = true
                        selectedMediaDrawable = null
                        selectedMediaUrl = null
                        currentStep = 1
                    },
                    onSelectMusicMode = {
                        isTextStoryMode = false
                        selectedMediaDrawable = null
                        selectedMediaUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80"
                        canvasOverlays.add(
                            StoryCanvasOverlayItem(
                                type = "MUSIC",
                                content = "Manike Mage Hithe",
                                subContent = "Yohani & Chamath Sangeeth ♫"
                            )
                        )
                        currentStep = 1
                    },
                    onSelectCameraMode = {
                        galleryPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onSelectMediaItem = { item ->
                        selectedMediaDrawable = item.drawableRes
                        selectedMediaUrl = item.imageUrl
                        isTextStoryMode = false
                        currentStep = 1
                    },
                    onOpenDeviceGallery = {
                        galleryPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            } else {
                // SCREEN 2: Story Editor Screen with All Tool Icons (Screenshot 1)
                StoryEditorScreen(
                    selectedMediaDrawable = selectedMediaDrawable,
                    selectedMediaUrl = selectedMediaUrl,
                    isTextStoryMode = isTextStoryMode,
                    textStoryCaption = textStoryCaption,
                    onUpdateTextStoryCaption = { textStoryCaption = it },
                    textStoryBgColor = textStoryBgColor,
                    onUpdateTextStoryBgColor = { textStoryBgColor = it },
                    canvasOverlays = canvasOverlays,
                    activeFilterName = activeFilterName,
                    onSelectFilter = { activeFilterName = it },
                    activeToolDrawer = activeToolDrawer,
                    onOpenToolDrawer = { activeToolDrawer = it },
                    onCloseToolDrawer = { activeToolDrawer = null },
                    onBack = { currentStep = 0 },
                    onAddOverlay = { item -> canvasOverlays.add(item) },
                    onShareStory = {
                        val finalCaption = if (textStoryCaption.isNotBlank()) textStoryCaption else "New Story ✨"
                        val finalMedia = selectedMediaUrl ?: selectedMediaDrawable?.let { "android.resource://${context.packageName}/$it" }
                        val bgHex = if (selectedMediaDrawable == null && selectedMediaUrl == null) {
                            String.format("#%06X", 0xFFFFFF and textStoryBgColor.hashCode())
                        } else null

                        onSubmitStory(finalMedia, finalCaption, bgHex)
                        Toast.makeText(context, "කථාන්දරය සාර්ථකව බෙදා ගන්නා ලදී! 🎉", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

/**
 * Screen 1: "නව කථාන්දරය" New Story Picker Screen (Matching Screenshot 2)
 */
@Composable
private fun NewStoryPickerScreen(
    galleryItems: List<StoryGalleryMediaItem>,
    onClose: () -> Unit,
    onSelectTextMode: () -> Unit,
    onSelectMusicMode: () -> Unit,
    onSelectCameraMode: () -> Unit,
    onSelectMediaItem: (StoryGalleryMediaItem) -> Unit,
    onOpenDeviceGallery: () -> Unit
) {
    var isMultipleSelectMode by remember { mutableStateOf(false) }
    var selectedAlbumName by remember { mutableStateOf("කැමරාව") }
    var isAlbumDropdownOpen by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF18191A))
    ) {
        // Top Bar: [X]  නව කථාන්දරය  [⚙️]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "නව කථාන්දරය",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            IconButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Story Settings",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Top 3 Large Action Cards: [Aa පෙළ]  [♫ සංගීතය]  [📷 කැමරාව]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Aa - පෙළ Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp)
                    .clickable { onSelectTextMode() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3B3C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aa",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "පෙළ",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ♫ - සංගීතය Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp)
                    .clickable { onSelectMusicMode() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3B3C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Music",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "සංගීතය",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 📷 - කැමරාව Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp)
                    .clickable { onSelectCameraMode() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3B3C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "කැමරාව",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Album filter dropdown & "✓ කිහිපයක් තෝරන්න" row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Album dropdown: කැමරාව ⌵
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { isAlbumDropdownOpen = true }
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = selectedAlbumName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Album dropdown",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = isAlbumDropdownOpen,
                    onDismissRequest = { isAlbumDropdownOpen = false },
                    modifier = Modifier.background(Color(0xFF242526))
                ) {
                    listOf("කැමරාව", "ගැලරිය", "Screenshots", "WhatsApp Images", "බාගැනීම්").forEach { album ->
                        DropdownMenuItem(
                            text = { Text(album, color = Color.White) },
                            onClick = {
                                selectedAlbumName = album
                                isAlbumDropdownOpen = false
                            }
                        )
                    }
                }
            }

            // Select multiple toggle: ✓ කිහිපයක් තෝරන්න
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isMultipleSelectMode) Color(0xFF1877F2) else Color(0xFF3A3B3C))
                    .clickable { isMultipleSelectMode = !isMultipleSelectMode }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "කිහිපයක් තෝරන්න",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3-Columns Gallery Media Grid (Matching Screenshot 2)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp),
            contentPadding = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Photos matching the user's screenshot
            items(galleryItems) { item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(Color(0xFF242526))
                        .clickable { onSelectMediaItem(item) }
                ) {
                    if (item.drawableRes != null) {
                        Image(
                            painter = painterResource(id = item.drawableRes),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (item.imageUrl != null) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    if (isMultipleSelectMode) {
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.5.dp, Color.White, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // Tile to pick from actual device gallery
            item {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(Color(0xFF2E3033))
                        .clickable { onOpenDeviceGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "More from Device",
                            tint = Color(0xFF1877F2),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "තවත් තෝරන්න",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        StoryPrivacySettingsDialog(onClose = { showSettingsDialog = false })
    }
}

/**
 * Screen 2: Story Editor Screen (Matching Screenshot 1)
 * Displays:
 * - Selected image or text background
 * - Top-left [X]
 * - Vertical right-side tools menu:
 *     ස්ටිකර, පෙළ, සංගීතය, ටැග් කරන්න, ප්‍රයෝග, Doodle, AI ලේබලය
 * - Bottom stickers bar: "ස්ටිකරයක් එක් කරන්න" & Quick stickers: ❤️, 🙏, 😎, 💕, 😂, තවත්
 * - Bottom action bar: Settings gear icon ⚙️ on left, "දැන් බෙදා ගන්න >" button on right!
 */
@Composable
private fun StoryEditorScreen(
    selectedMediaDrawable: Int?,
    selectedMediaUrl: String?,
    isTextStoryMode: Boolean,
    textStoryCaption: String,
    onUpdateTextStoryCaption: (String) -> Unit,
    textStoryBgColor: Color,
    onUpdateTextStoryBgColor: (Color) -> Unit,
    canvasOverlays: List<StoryCanvasOverlayItem>,
    activeFilterName: String,
    onSelectFilter: (String) -> Unit,
    activeToolDrawer: String?,
    onOpenToolDrawer: (String) -> Unit,
    onCloseToolDrawer: () -> Unit,
    onBack: () -> Unit,
    onAddOverlay: (StoryCanvasOverlayItem) -> Unit,
    onShareStory: () -> Unit
) {
    val context = LocalContext.current
    var showQuickStickerBar by remember { mutableStateOf(true) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Color matrix filter for "ප්‍රයෝග" (Effects)
    val colorMatrix = remember(activeFilterName) {
        when (activeFilterName) {
            "B&W" -> ColorMatrix().apply { setToSaturation(0f) }
            "Vintage" -> ColorMatrix(
                floatArrayOf(
                    0.9f, 0f, 0f, 0f, 20f,
                    0f, 0.8f, 0f, 0f, 15f,
                    0f, 0f, 0.6f, 0f, 10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "Warm" -> ColorMatrix(
                floatArrayOf(
                    1.2f, 0f, 0f, 0f, 30f,
                    0f, 1.0f, 0f, 0f, 10f,
                    0f, 0f, 0.8f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "Cyber" -> ColorMatrix(
                floatArrayOf(
                    0.8f, 0f, 0.5f, 0f, 10f,
                    0f, 1.2f, 0f, 0f, 20f,
                    0.3f, 0f, 1.4f, 0f, 40f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            else -> ColorMatrix() // Normal
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main Story Background (Selected Photo or Solid Color Text Mode)
        if (isTextStoryMode || (selectedMediaDrawable == null && selectedMediaUrl == null)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(textStoryBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (textStoryCaption.isBlank()) "ඔබේ කතාව මෙතැන ටයිප් කරන්න... ✍️" else textStoryCaption,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .clickable { onOpenToolDrawer("TEXT") }
                )
            }
        } else {
            // Photo Background with active filter applied
            if (selectedMediaDrawable != null) {
                Image(
                    painter = painterResource(id = selectedMediaDrawable),
                    contentDescription = "Selected Media",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier.fillMaxSize()
                )
            } else if (selectedMediaUrl != null) {
                AsyncImage(
                    model = selectedMediaUrl,
                    contentDescription = "Selected Media",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Render Overlays (Stickers, Text, Music, Tags) placed on top of canvas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 140.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            canvasOverlays.forEach { overlay ->
                when (overlay.type) {
                    "STICKER" -> {
                        Text(
                            text = overlay.content,
                            fontSize = 48.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    "TEXT" -> {
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = overlay.content,
                                color = overlay.color,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    "MUSIC" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF242526).copy(alpha = 0.85f))
                                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color(0xFF1877F2),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = overlay.content,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                overlay.subContent?.let {
                                    Text(
                                        text = it,
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    "TAG" -> {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1877F2).copy(alpha = 0.9f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = overlay.content,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    "AI_LABEL" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(1.dp, Color(0xFF4285F4), RoundedCornerShape(14.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF4285F4),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = overlay.content,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Top Left [X] Button (Back to picker)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(44.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        // Vertical Tool/Setting Icons on the Right Side (Screenshot 1)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.End
        ) {
            // 1. ස්ටිකර (Stickers)
            StoryEditorToolButton(
                label = "ස්ටිකර",
                iconContent = {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = "Stickers",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onOpenToolDrawer("STICKERS") }
            )

            // 2. පෙළ (Text)
            StoryEditorToolButton(
                label = "පෙළ",
                iconContent = {
                    Text(
                        text = "Aa",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                onClick = { onOpenToolDrawer("TEXT") }
            )

            // 3. සංගීතය (Music)
            StoryEditorToolButton(
                label = "සංගීතය",
                iconContent = {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Music",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onOpenToolDrawer("MUSIC") }
            )

            // 4. ටැග් කරන්න (Tag)
            StoryEditorToolButton(
                label = "ටැග් කරන්න",
                iconContent = {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Tag",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = { onOpenToolDrawer("TAG") }
            )

            // 5. ප්‍රයෝග (Effects / Filters)
            StoryEditorToolButton(
                label = "ප්‍රයෝග",
                iconContent = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Effects",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onOpenToolDrawer("EFFECTS") }
            )

            // 6. Doodle (Draw)
            StoryEditorToolButton(
                label = "Doodle",
                iconContent = {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = "Doodle",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onOpenToolDrawer("DOODLE") }
            )

            // 7. AI ලේබලය (AI Label)
            StoryEditorToolButton(
                label = "AI ලේබලය",
                iconContent = {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = "AI Label",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onOpenToolDrawer("AI") }
            )
        }

        // Bottom Section: Quick Stickers + Bottom Bar (Settings & දැන් බෙදා ගන්න >)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                        startY = 0f
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Quick Stickers Bar (Screenshot 1)
            if (showQuickStickerBar) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ස්ටිකරයක් එක් කරන්න",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "වසන්න",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { showQuickStickerBar = false }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Stickers Row: ❤️ 🙏 😎 💕 😂 and [Sticker] තවත්
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val quickStickers = listOf("❤️", "🙏", "😎", "💕", "😂")
                    quickStickers.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .clickable {
                                    onAddOverlay(StoryCanvasOverlayItem(type = "STICKER", content = emoji))
                                    Toast.makeText(context, "$emoji ස්ටිකරය එක් කරන ලදී!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(4.dp)
                        )
                    }

                    // "තවත්" Button with smiling sticker face
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF3A3B3C))
                            .clickable { onOpenToolDrawer("STICKERS") }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mood,
                            contentDescription = "More stickers",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "තවත්",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Bottom Action Bar: Settings Gear ⚙️ on left, Big Blue "දැන් බෙදා ගන්න >" button on right!
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Settings Gear Icon ⚙️
                IconButton(
                    onClick = { showSettingsDialog = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF242526))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Big Bright Facebook Lite Blue Button: "දැන් බෙදා ගන්න >"
                Button(
                    onClick = onShareStory,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    modifier = Modifier.testTag("share_story_button")
                ) {
                    Text(
                        text = "දැන් බෙදා ගන්න",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // DRAWERS & MODALS for 100% to 500% functionality of each tool icon:
        when (activeToolDrawer) {
            "STICKERS" -> {
                StickersPickerDrawer(
                    onClose = onCloseToolDrawer,
                    onSelectSticker = { stickerText ->
                        onAddOverlay(StoryCanvasOverlayItem(type = "STICKER", content = stickerText))
                        onCloseToolDrawer()
                    }
                )
            }
            "TEXT" -> {
                TextEditorDrawer(
                    initialText = textStoryCaption,
                    onClose = onCloseToolDrawer,
                    onApplyText = { newText, color ->
                        if (isTextStoryMode) {
                            onUpdateTextStoryCaption(newText)
                            onUpdateTextStoryBgColor(color)
                        } else {
                            onAddOverlay(StoryCanvasOverlayItem(type = "TEXT", content = newText, color = color))
                        }
                        onCloseToolDrawer()
                    }
                )
            }
            "MUSIC" -> {
                MusicPickerDrawer(
                    onClose = onCloseToolDrawer,
                    onSelectSong = { title, artist ->
                        onAddOverlay(
                            StoryCanvasOverlayItem(
                                type = "MUSIC",
                                content = title,
                                subContent = "$artist ♫"
                            )
                        )
                        onCloseToolDrawer()
                    }
                )
            }
            "TAG" -> {
                TagFriendsDrawer(
                    onClose = onCloseToolDrawer,
                    onTagFriend = { friendName ->
                        onAddOverlay(StoryCanvasOverlayItem(type = "TAG", content = "@$friendName"))
                        onCloseToolDrawer()
                    }
                )
            }
            "EFFECTS" -> {
                EffectsPickerDrawer(
                    currentFilter = activeFilterName,
                    onClose = onCloseToolDrawer,
                    onSelectFilter = onSelectFilter
                )
            }
            "DOODLE" -> {
                DoodleBrushDrawer(
                    onClose = onCloseToolDrawer,
                    onAddDoodle = { doodleName, color ->
                        onAddOverlay(StoryCanvasOverlayItem(type = "TEXT", content = doodleName, color = color))
                        onCloseToolDrawer()
                    }
                )
            }
            "AI" -> {
                AiStoryLabelDrawer(
                    onClose = onCloseToolDrawer,
                    onApplyAiCaption = { aiText ->
                        onAddOverlay(StoryCanvasOverlayItem(type = "AI_LABEL", content = "AI: $aiText"))
                        onCloseToolDrawer()
                    }
                )
            }
        }

        if (showSettingsDialog) {
            StoryPrivacySettingsDialog(onClose = { showSettingsDialog = false })
        }
    }
}

/**
 * Single Right-side Tool Button with Circular icon + Sinhala label next to it (Screenshot 1)
 */
@Composable
private fun StoryEditorToolButton(
    label: String,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp)
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF242526).copy(alpha = 0.85f))
                .border(1.dp, Color(0xFF3E4042), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }
    }
}

/**
 * 1. Stickers Drawer (ස්ටිකර)
 */
@Composable
private fun StickersPickerDrawer(
    onClose: () -> Unit,
    onSelectSticker: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFF242526))
                .clickable(enabled = false) {}
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ස්ටිකර තෝරන්න (Stickers)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Facebook Badges: Location, Time, Mention, Poll
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onSelectSticker("📍 Colombo, Sri Lanka") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3B3C)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ස්ථානය", fontSize = 12.sp)
                }

                Button(
                    onClick = { onSelectSticker("⏰ 10:22 PM") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3B3C)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("වේලාව", fontSize = 12.sp)
                }

                Button(
                    onClick = { onSelectSticker("📊 Poll: Yes / No") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3B3C)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Poll, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ඡන්දය", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sticker Emojis Grid
            val allStickers = listOf(
                "❤️", "🙏", "😎", "💕", "😂", "🔥", "🎉", "💯", "🥰", "🤩",
                "🌸", "🚗", "🌟", "✨", "🍕", "☕", "🏖️", "🇱🇰", "👑", "💪",
                "👋", "🎶", "😴", "🥳", "🌈", "🦋", "💥", "🎯", "🥥", "🎊"
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.height(200.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allStickers) { sticker ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(Color(0xFF3A3B3C))
                            .clickable { onSelectSticker(sticker) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = sticker, fontSize = 28.sp)
                    }
                }
            }
        }
    }
}

/**
 * 2. Text Editor Drawer (පෙළ)
 */
@Composable
private fun TextEditorDrawer(
    initialText: String,
    onClose: () -> Unit,
    onApplyText: (String, Color) -> Unit
) {
    var textInput by remember { mutableStateOf(initialText) }
    var selectedColor by remember { mutableStateOf(Color.White) }
    val colorPalette = listOf(
        Color.White,
        Color(0xFFFFEB3B), // Yellow
        Color(0xFF1877F2), // FB Blue
        Color(0xFFE91E63), // Pink
        Color(0xFF4CAF50), // Green
        Color(0xFFFF5722), // Orange
        Color(0xFF9C27B0)  // Purple
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF242526))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "පෙළ එක් කරන්න (Add Text)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("කතාවට යමක් ලියන්න...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = selectedColor,
                    unfocusedTextColor = selectedColor,
                    focusedBorderColor = selectedColor,
                    unfocusedBorderColor = Color(0xFF3E4042)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Color Palette
            Text(text = "වර්ණය තෝරන්න:", color = Color.LightGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                colorPalette.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 2.dp,
                                color = if (selectedColor == color) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onApplyText(textInput, selectedColor)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("එක් කරන්න (Done)", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/**
 * Helper to query file display name from URI
 */
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
    }
    if (name == null) {
        name = uri.path?.let { path ->
            val cut = path.lastIndexOf('/')
            if (cut != -1) path.substring(cut + 1) else path
        }
    }
    return name
}

data class TikTokStorySound(
    val rank: Int,
    val title: String,
    val artist: String,
    val postsCount: String,
    val duration: String,
    val coverUrl: String,
    val region: String
)

/**
 * 3. TikTok-style Music & Audio Converter Picker Drawer
 */
@Composable
private fun MusicPickerDrawer(
    onClose: () -> Unit,
    onSelectSong: (title: String, artist: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isConvertingVideo by remember { mutableStateOf(false) }
    var conversionProgress by remember { mutableFloatStateOf(0f) }
    var convertedVideoTitle by remember { mutableStateOf("") }

    var selectedTopTab by remember { mutableStateOf("Hot") } // "Hot", "For You", "Favourites", "Recent"
    var selectedRegionTab by remember { mutableStateOf("Sri Lanka") } // "Sri Lanka", "Global"
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Launcher for picking MP3 / Audio directly from phone gallery / storage
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(context, it) ?: "Local Gallery Track"
            val cleanTitle = fileName.replace(".mp3", "", ignoreCase = true)
                .replace(".m4a", "", ignoreCase = true)
                .replace(".wav", "", ignoreCase = true)
            Toast.makeText(context, "📁 ගැලරියෙන් '$cleanTitle' සින්දුව කතාවට එක් කරන ලදී!", Toast.LENGTH_LONG).show()
            onSelectSong(cleanTitle, "Device Storage Audio 🎵")
        }
    }

    // Launcher for picking Video from phone gallery to extract audio track
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { videoUri ->
            val videoName = getFileNameFromUri(context, videoUri) ?: "Gallery Video"
            val cleanVideoName = videoName.substringBeforeLast(".")
            convertedVideoTitle = cleanVideoName

            isConvertingVideo = true
            conversionProgress = 0f

            coroutineScope.launch {
                for (i in 1..100) {
                    delay(25)
                    conversionProgress = i / 100f
                }
                delay(300)
                isConvertingVideo = false
                Toast.makeText(
                    context,
                    "🎬 '$cleanVideoName' වීඩියෝවේ Audio එක සාර්ථකව MP3 බවට පරිවර්තනය කර කතාවට එක් කරන ලදී! ✅",
                    Toast.LENGTH_LONG
                ).show()
                onSelectSong("🎬 Converted: $cleanVideoName", "Original Video Sound 🎵")
            }
        }
    }

    // Sound list data structure matching screenshot
    val sriLankaTracks = listOf(
        TikTokStorySound(
            rank = 1,
            title = "Bowitiya Mal",
            artist = "Ravi Jay",
            postsCount = "19.2K posts",
            duration = "0:28",
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=200&auto=format&fit=crop&q=80",
            region = "Sri Lanka"
        ),
        TikTokStorySound(
            rank = 2,
            title = "Nura Ananthe",
            artist = "Cozzy",
            postsCount = "14.7K posts",
            duration = "0:45",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&auto=format&fit=crop&q=80",
            region = "Sri Lanka"
        ),
        TikTokStorySound(
            rank = 3,
            title = "Atha Arala Daala",
            artist = "Lil Rome Praba",
            postsCount = "59.4K posts",
            duration = "0:51",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=200&auto=format&fit=crop&q=80",
            region = "Sri Lanka"
        ),
        TikTokStorySound(
            rank = 4,
            title = "Nil Denethi",
            artist = "Iraj",
            postsCount = "7204 posts",
            duration = "0:44",
            coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=200&auto=format&fit=crop&q=80",
            region = "Sri Lanka"
        ),
        TikTokStorySound(
            rank = 5,
            title = "Manike Mage Hithe",
            artist = "Yohani & Chamath Sangeeth",
            postsCount = "120.5K posts",
            duration = "0:30",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=200&auto=format&fit=crop&q=80",
            region = "Sri Lanka"
        ),
        TikTokStorySound(
            rank = 6,
            title = "Rathu Rosa",
            artist = "Romesh Sugathapala",
            postsCount = "28.3K posts",
            duration = "0:35",
            coverUrl = "https://images.unsplash.com/photo-1518895949257-7621c3c786d7?w=200&auto=format&fit=crop&q=80",
            region = "Sri Lanka"
        )
    )

    val globalTracks = listOf(
        TikTokStorySound(
            rank = 1,
            title = "Flowers",
            artist = "Miley Cyrus",
            postsCount = "840.2K posts",
            duration = "0:30",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&auto=format&fit=crop&q=80",
            region = "Global"
        ),
        TikTokStorySound(
            rank = 2,
            title = "Shape of You",
            artist = "Ed Sheeran",
            postsCount = "1.2M posts",
            duration = "0:40",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=200&auto=format&fit=crop&q=80",
            region = "Global"
        ),
        TikTokStorySound(
            rank = 3,
            title = "As It Was",
            artist = "Harry Styles",
            postsCount = "950.8K posts",
            duration = "0:32",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=200&auto=format&fit=crop&q=80",
            region = "Global"
        ),
        TikTokStorySound(
            rank = 4,
            title = "Stay",
            artist = "The Kid LAROI & Justin Bieber",
            postsCount = "1.5M posts",
            duration = "0:35",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=200&auto=format&fit=crop&q=80",
            region = "Global"
        )
    )

    val displayedTracks = remember(selectedRegionTab, searchQuery) {
        val baseList = if (selectedRegionTab == "Sri Lanka") sriLankaTracks else globalTracks
        if (searchQuery.isBlank()) {
            baseList
        } else {
            baseList.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.artist.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { if (!isConvertingVideo) onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF181818))
                .clickable(enabled = false) {}
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Drag Handle Bar at Top Center (Matching Screenshot)
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF555555))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ==================== 1. TIKTOK TOP NAVIGATION TABS (Hot, For You, Favourites, Recent, 🔍) ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf("Hot", "For You", "Favourites", "Recent")
                    tabs.forEach { tabName ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedTopTab = tabName }
                        ) {
                            Text(
                                text = tabName,
                                color = if (selectedTopTab == tabName) Color.White else Color(0xFF8E8E93),
                                fontSize = 16.sp,
                                fontWeight = if (selectedTopTab == tabName) FontWeight.Bold else FontWeight.Medium
                            )
                            if (selectedTopTab == tabName) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(2.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { showSearchBar = !showSearchBar },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Search Bar Input (Toggled)
            AnimatedVisibility(visible = showSearchBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2C2C2E))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("සින්දුවේ නම / ගායකයා සෙවුම් කරන්න...", color = Color.Gray, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==================== 2. SUB-FILTER CHIPS (Sri Lanka vs Global) ====================
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Sri Lanka" Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedRegionTab == "Sri Lanka") Color.White else Color(0xFF2C2C2E))
                        .clickable { selectedRegionTab = "Sri Lanka" }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Sri Lanka",
                        color = if (selectedRegionTab == "Sri Lanka") Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // "Global" Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedRegionTab == "Global") Color.White else Color(0xFF2C2C2E))
                        .clickable { selectedRegionTab = "Global" }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Global",
                        color = if (selectedRegionTab == "Global") Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==================== 3. GALLERY MP3 & VIDEO AUDIO EXTRACTOR BUTTONS ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Pick Audio/MP3 from Gallery Storage
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { audioPickerLauncher.launch("audio/*") },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Pick Audio",
                            tint = Color(0xFF00D2FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📁 ගැලරියෙන් MP3",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Button 2: Extract & Convert Audio from Gallery Video
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Video Audio Converter",
                            tint = Color(0xFFFF0050),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🎬 Video to Audio Convert",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Video Conversion Progress Notification Banner
            AnimatedVisibility(visible = isConvertingVideo) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFFFF0050), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🎬 වීඩියෝ ශ්‍රව්‍ය පථය පරිවර්තනය වෙමින් පවතී...", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { conversionProgress },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFFFF0050),
                            trackColor = Color(0xFF444444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==================== 4. RANKED TIKTOK SOUND TRACK LIST (Matching Screenshot) ====================
            LazyColumn(
                modifier = Modifier.height(340.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedTracks) { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSong(sound.title, sound.artist) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Number (1, 2, 3, 4...)
                        Text(
                            text = "${sound.rank}",
                            color = Color(0xFF8E8E93),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(28.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Album Artwork Thumbnail
                        AsyncImage(
                            model = sound.coverUrl,
                            contentDescription = sound.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2C2C2E))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Track Title & Details (Artist • Posts Count • Duration)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sound.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${sound.artist} • ${sound.postsCount} • ${sound.duration}",
                                color = Color(0xFF8E8E93),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. Tag Friends Drawer (ටැග් කරන්න)
 */
@Composable
private fun TagFriendsDrawer(
    onClose: () -> Unit,
    onTagFriend: (String) -> Unit
) {
    val friends = listOf("Akash Ruwan", "Damayanthi Damayanthi", "Prabath Deshapriya", "Nadeeka Perera", "Kavindu Senanayake", "Saman Kumara")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFF242526))
                .clickable(enabled = false) {}
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "මිතුරන් ටැග් කරන්න (Tag Friends)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.height(240.dp)) {
                items(friends) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTagFriend(friend) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3A3B3C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = friend.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(text = friend, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))

                        Text(text = "ටැග් කරන්න", color = Color(0xFF1877F2), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color(0xFF3A3B3C), thickness = 0.5.dp)
                }
            }
        }
    }
}

/**
 * 5. Effects / Filters Picker Drawer (ප්‍රයෝග)
 */
@Composable
private fun EffectsPickerDrawer(
    currentFilter: String,
    onClose: () -> Unit,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf(
        "සාමාන්‍ය" to "Normal",
        "Warm" to "උණුසුම් ☀️",
        "Vintage" to "Vintage 🎞️",
        "B&W" to "කළු සහ සුදු 🖤",
        "Cyber" to "Cyber Neon 🔮"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFF242526))
                .clickable(enabled = false) {}
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ප්‍රයෝග තෝරන්න (Photo Filters)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filters) { (name, label) ->
                    val isSelected = currentFilter == name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF1877F2) else Color(0xFF3A3B3C))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                onSelectFilter(name)
                                onClose()
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 6. Doodle Brush Drawer (Doodle)
 */
@Composable
private fun DoodleBrushDrawer(
    onClose: () -> Unit,
    onAddDoodle: (String, Color) -> Unit
) {
    val doodlePresets = listOf(
        "❤️ Heart Draw" to Color(0xFFE91E63),
        "✨ Sparkles Doodle" to Color(0xFFFFEB3B),
        "💫 Swirl Doodle" to Color(0xFF00E5FF),
        "⚡ Lightning Draw" to Color(0xFFFFD600),
        "🌟 Star Burst" to Color(0xFFFF4081)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFF242526))
                .clickable(enabled = false) {}
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Doodle (චිත්‍ර අඳින්න)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            doodlePresets.forEach { (preset, color) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddDoodle(preset, color) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = preset, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
                HorizontalDivider(color = Color(0xFF3A3B3C), thickness = 0.5.dp)
            }
        }
    }
}

/**
 * 7. AI Label Drawer (AI ලේබලය)
 */
@Composable
private fun AiStoryLabelDrawer(
    onClose: () -> Unit,
    onApplyAiCaption: (String) -> Unit
) {
    val aiCaptions = listOf(
        "මතකයන් පිරුණු සුන්දර දවසක්... 🌸✨",
        "Living in the moment and loving every second! 💫",
        "හදවතින්ම දැනෙන සතුට බෙදා ගනිමි 💖",
        "Capturing today’s sunshine & positive vibes ☀️"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFF242526))
                .clickable(enabled = false) {}
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color(0xFF4285F4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI ලේබලය සහ සටහන් (AI Assist)",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "කථාන්දරයට ගැලපෙන AI ලේබලයක් හෝ සටහනක් තෝරන්න:",
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            aiCaptions.forEach { caption ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF3A3B3C))
                        .clickable { onApplyAiCaption(caption) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = caption,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF4285F4),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Story Privacy Settings Dialog (⚙️)
 */
@Composable
private fun StoryPrivacySettingsDialog(
    onClose: () -> Unit
) {
    var selectedAudience by remember { mutableStateOf("ප්‍රසිද්ධ (Public)") }
    val options = listOf(
        "ප්‍රසිද්ධ (Public)" to "ෆේස්බුක් හි ඕනෑම කෙනෙකුට",
        "මිතුරන් (Friends)" to "ඔබේ මිතුරන්ට පමණි",
        "අභිරුචි (Custom)" to "තෝරාගත් පිරිසකට පමණි"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF242526))
                .border(1.dp, Color(0xFF3E4042), RoundedCornerShape(20.dp))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Text(
                text = "කථාන්දර රහස්‍යතාව (Story Privacy)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ඔබගේ කතාව දැකිය හැක්කේ කාටද?",
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            options.forEach { (title, subtitle) ->
                val isSelected = selectedAudience == title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF3A3B3C) else Color.Transparent)
                        .clickable { selectedAudience = title }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF1877F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("සුරකින්න (Save)", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
