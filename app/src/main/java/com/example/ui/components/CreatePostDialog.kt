package com.example.ui.components

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Style
import kotlinx.coroutines.delay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.MediaType
import com.example.model.User

/**
 * Represents an item in the gallery picker grid.
 */
data class PostGalleryItem(
    val id: String,
    val url: String,
    val mediaType: MediaType = MediaType.IMAGE,
    val durationText: String? = null // For videos like "9:06", "0:18"
)

/**
 * 2-Screen Facebook Lite Post Creation Experience:
 * - Screen 0: "නව පෝස්ටුව" (Screenshot 2: Gallery Picker Grid, camera, multiple select)
 * - Screen 1: "පෝස්ටුව තනන්න" (Screenshot 1: Post Editor with user profile, privacy pills,
 *   dark text input, selected photo with ✏️ Edit and 🗑️ Delete buttons, action items list,
 *   and blue "පළ කරන්න" buttons).
 */
@Composable
fun CreatePostDialog(
    currentUser: User,
    initialMediaType: MediaType = MediaType.NONE,
    initialMediaUrl: String? = null,
    onDismiss: () -> Unit,
    onSubmitPost: (content: String, mediaUrl: String?, mediaType: MediaType) -> Unit
) {
    val context = LocalContext.current

    // Navigation state: 0 = "නව පෝස්ටුව" Gallery Screen, 1 = "පෝස්ටුව තනන්න" Post Composer Screen
    var currentScreen by remember {
        mutableStateOf(if (initialMediaUrl != null) 1 else 0)
    }

    // Post content state
    var postText by remember { mutableStateOf("") }
    var selectedMediaUrl by remember { mutableStateOf<String?>(initialMediaUrl) }
    var selectedMediaType by remember {
        mutableStateOf(if (initialMediaUrl != null && initialMediaType == MediaType.NONE) MediaType.IMAGE else initialMediaType)
    }

    // Privacy & Extras
    var privacySetting by remember { mutableStateOf("මිතුරන්") } // ප්‍රසිද්ධ, මිතුරන්, මම පමණයි
    var selectedMusic by remember { mutableStateOf<String?>(null) }
    val taggedFriends = remember { mutableStateListOf<String>() }
    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var selectedFeeling by remember { mutableStateOf<String?>(null) }

    // Dialog sheets for actions
    var showMusicDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showFeelingDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showAiMagicStudioSheet by remember { mutableStateOf(false) }

    // Multi-select mode
    var isMultipleSelectMode by remember { mutableStateOf(false) }
    val selectedMultipleUrls = remember { mutableStateListOf<String>() }

    // Gallery media list state (starts empty - no extra sample photos)
    val galleryMediaItems = remember {
        mutableStateListOf<PostGalleryItem>()
    }

    // Device camera capture launcher (Takes direct photo from device camera)
    val cameraCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uriStr = saveBitmapToCache(context, bitmap)
            if (uriStr.isNotEmpty()) {
                selectedMediaUrl = uriStr
                selectedMediaType = MediaType.IMAGE
                galleryMediaItems.add(
                    0,
                    PostGalleryItem(
                        id = "camera_${System.currentTimeMillis()}",
                        url = uriStr,
                        mediaType = MediaType.IMAGE
                    )
                )
                currentScreen = 1 // Navigate to Post Composer
                Toast.makeText(context, "ඡායාරූපය සාර්ථකව ගන්නා ලදී! 📸", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraCaptureLauncher.launch(null)
        } else {
            Toast.makeText(context, "කැමරා අවසරය ලබා දෙන්න (Camera permission required)", Toast.LENGTH_SHORT).show()
        }
    }

    // Device system photo picker launcher
    val systemPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uriStr = uri.toString()
            selectedMediaUrl = uriStr
            selectedMediaType = MediaType.IMAGE
            galleryMediaItems.add(
                0,
                PostGalleryItem(
                    id = "picked_${System.currentTimeMillis()}",
                    url = uriStr,
                    mediaType = MediaType.IMAGE
                )
            )
            currentScreen = 1 // Navigate to Post Composer
            Toast.makeText(context, "ඡායාරූපය සාර්ථකව තෝරා ගන්නා ලදී! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    // Device system video picker launcher
    val systemVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uriStr = uri.toString()
            selectedMediaUrl = uriStr
            selectedMediaType = MediaType.VIDEO
            galleryMediaItems.add(
                0,
                PostGalleryItem(
                    id = "video_${System.currentTimeMillis()}",
                    url = uriStr,
                    mediaType = MediaType.VIDEO,
                    durationText = "0:30"
                )
            )
            currentScreen = 1 // Navigate to Post Composer
            Toast.makeText(context, "වීඩියෝව සාර්ථකව තෝරා ගන්නා ලදී! 🎥", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF18191A)
        ) {
            if (currentScreen == 0) {
                // SCREEN 0: "නව පෝස්ටුව" Gallery Picker (Screenshot 2)
                NewPostGalleryPickerScreen(
                    galleryItems = galleryMediaItems,
                    isMultipleSelectMode = isMultipleSelectMode,
                    onToggleMultipleSelect = {
                        isMultipleSelectMode = !isMultipleSelectMode
                        if (!isMultipleSelectMode) selectedMultipleUrls.clear()
                    },
                    selectedUrls = selectedMultipleUrls,
                    onToggleUrlSelect = { url ->
                        if (selectedMultipleUrls.contains(url)) {
                            selectedMultipleUrls.remove(url)
                        } else {
                            selectedMultipleUrls.add(url)
                        }
                    },
                    onSelectSingleItem = { item ->
                        selectedMediaUrl = item.url
                        selectedMediaType = item.mediaType
                        currentScreen = 1 // Go to Post Composer
                    },
                    onProceedWithMultiple = {
                        if (selectedMultipleUrls.isNotEmpty()) {
                            selectedMediaUrl = selectedMultipleUrls.first()
                            selectedMediaType = MediaType.IMAGE
                            currentScreen = 1
                        }
                    },
                    onOpenDeviceCamera = {
                        if (com.example.util.CameraPermissionManager.shouldRequestPermission(context)) {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            cameraCaptureLauncher.launch(null)
                        }
                    },
                    onOpenDevicePhotoPicker = {
                        systemPhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onOpenDeviceVideoPicker = {
                        systemVideoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    onSkipToTextOnly = {
                        selectedMediaUrl = null
                        selectedMediaType = MediaType.NONE
                        currentScreen = 1
                    },
                    onClose = onDismiss
                )
            } else {
                // SCREEN 1: "පෝස්ටුව තනන්න" Post Composer (Screenshot 1)
                PostComposerScreen(
                    currentUser = currentUser,
                    postText = postText,
                    onPostTextChanged = { postText = it },
                    selectedMediaUrl = selectedMediaUrl,
                    selectedMediaType = selectedMediaType,
                    onRemoveMedia = {
                        selectedMediaUrl = null
                        selectedMediaType = MediaType.NONE
                    },
                    onChangeMedia = {
                        currentScreen = 0
                    },
                    privacySetting = privacySetting,
                    onChangePrivacy = { privacySetting = it },
                    selectedMusic = selectedMusic,
                    onOpenMusicSelector = { showMusicDialog = true },
                    onRemoveMusic = { selectedMusic = null },
                    taggedFriends = taggedFriends,
                    onOpenTagSelector = { showTagDialog = true },
                    onRemoveTag = { taggedFriends.remove(it) },
                    selectedLocation = selectedLocation,
                    onOpenLocationSelector = { showLocationDialog = true },
                    onRemoveLocation = { selectedLocation = null },
                    selectedFeeling = selectedFeeling,
                    onOpenFeelingSelector = { showFeelingDialog = true },
                    onRemoveFeeling = { selectedFeeling = null },
                    onOpenAiHelper = { showAiDialog = true },
                    onOpenAiMagicStudio = { showAiMagicStudioSheet = true },
                    onBack = { currentScreen = 0 },
                    onPublish = {
                        val networkMonitor = com.example.util.NetworkMonitor(context)
                        if (!networkMonitor.isConnected()) {
                            Toast.makeText(context, "අන්තර්ජාල සබඳතාව නොමැත. කරුණාකර Network සක්‍රිය කර නැවත උත්සාහ කරන්න. 📡", Toast.LENGTH_LONG).show()
                        } else {
                            var finalContent = postText.trim()
                            if (selectedFeeling != null) {
                                finalContent = if (finalContent.isEmpty()) "Feeling $selectedFeeling" else "$finalContent — feeling $selectedFeeling"
                            }
                            if (taggedFriends.isNotEmpty()) {
                                finalContent = if (finalContent.isEmpty()) "With ${taggedFriends.joinToString(", ")}" else "$finalContent with ${taggedFriends.joinToString(", ")}"
                            }
                            if (selectedLocation != null) {
                                finalContent = if (finalContent.isEmpty()) "At $selectedLocation" else "$finalContent at $selectedLocation"
                            }
                            if (selectedMusic != null) {
                                finalContent = if (finalContent.isEmpty()) "🎵 $selectedMusic" else "$finalContent \n🎵 $selectedMusic"
                            }

                            onSubmitPost(finalContent, selectedMediaUrl, selectedMediaType)
                            Toast.makeText(context, "පෝස්ටුව සාර්ථකව පළ කරන ලදී! 🚀", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    // Secondary Action Dialogs
    if (showMusicDialog) {
        MusicSelectDialog(
            onClose = { showMusicDialog = false },
            onSelect = { song ->
                selectedMusic = song
                showMusicDialog = false
            }
        )
    }

    if (showTagDialog) {
        TagFriendsDialog(
            currentlyTagged = taggedFriends,
            onClose = { showTagDialog = false },
            onToggleFriend = { name ->
                if (taggedFriends.contains(name)) taggedFriends.remove(name)
                else taggedFriends.add(name)
            }
        )
    }

    if (showLocationDialog) {
        LocationSelectDialog(
            onClose = { showLocationDialog = false },
            onSelect = { loc ->
                selectedLocation = loc
                showLocationDialog = false
            }
        )
    }

    if (showFeelingDialog) {
        FeelingSelectDialog(
            onClose = { showFeelingDialog = false },
            onSelect = { feel ->
                selectedFeeling = feel
                showFeelingDialog = false
            }
        )
    }

    if (showAiDialog) {
        AiPostAssistDialog(
            currentText = postText,
            onClose = { showAiDialog = false },
            onApplyText = { generated ->
                postText = generated
                showAiDialog = false
            }
        )
    }

    if (showAiMagicStudioSheet) {
        AiMagicPhotoStudioSheet(
            initialPhotoUrl = selectedMediaUrl ?: "",
            onClose = { showAiMagicStudioSheet = false },
            onApplyEditedPhoto = { editedUrl ->
                selectedMediaUrl = editedUrl
                selectedMediaType = MediaType.IMAGE
                showAiMagicStudioSheet = false
            }
        )
    }
}

/**
 * Screen 0: "නව පෝස්ටුව" Gallery Picker Screen (Matching Screenshot 2)
 */
@Composable
private fun NewPostGalleryPickerScreen(
    galleryItems: List<PostGalleryItem>,
    isMultipleSelectMode: Boolean,
    onToggleMultipleSelect: () -> Unit,
    selectedUrls: List<String>,
    onToggleUrlSelect: (String) -> Unit,
    onSelectSingleItem: (PostGalleryItem) -> Unit,
    onProceedWithMultiple: () -> Unit,
    onOpenDeviceCamera: () -> Unit,
    onOpenDevicePhotoPicker: () -> Unit,
    onOpenDeviceVideoPicker: () -> Unit,
    onSkipToTextOnly: () -> Unit,
    onClose: () -> Unit
) {
    var selectedAlbumName by remember { mutableStateOf("ගැලරිය") }
    var isAlbumDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF18191A))
    ) {
        // Top Bar: [✕]  නව පෝස්ටුව  [📷]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = "නව පෝස්ටුව",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onOpenDeviceCamera, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera / Picker",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Sub-bar: [ගැලරිය ⌵] on left, [✓ එකකට වැඩි ගණනක්...] on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Album dropdown: ගැලරිය ⌵
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
                        contentDescription = "Album Dropdown",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = isAlbumDropdownOpen,
                    onDismissRequest = { isAlbumDropdownOpen = false },
                    modifier = Modifier.background(Color(0xFF242526))
                ) {
                    listOf("ගැලරිය", "කැමරාව", "Screenshots", "WhatsApp Images", "FriendHub", "බාගැනීම්").forEach { album ->
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

            // Checkbox pill: ✓ එකකට වැඩි ගණනක්...
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isMultipleSelectMode) Color(0xFF1877F2) else Color(0xFF3A3B3C))
                    .clickable { onToggleMultipleSelect() }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "එකකට වැඩි ගණනක්...",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Fast Text-only shortcut banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF242526))
                .clickable { onSkipToTextOnly() }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✍️", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ඡායාරූප නැතුව පෙළ පමණක් ලියන්න (Text only)",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }
            Text(
                text = "ලියන්න >",
                color = Color(0xFF1877F2),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2-Column Media Grid (Camera and Phone Photos only)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Open Camera
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF242526))
                        .border(1.dp, Color(0xFF3A3B3C), RoundedCornerShape(14.dp))
                        .clickable { onOpenDeviceCamera() }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1877F2).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Camera",
                                tint = Color(0xFF1877F2),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "කැමරාව",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 2. Open Device Photos (Native Android Picker)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF242526))
                        .border(1.dp, Color(0xFF3A3B3C), RoundedCornerShape(14.dp))
                        .clickable { onOpenDevicePhotoPicker() }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF31A24C).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Device Photos",
                                tint = Color(0xFF31A24C),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "දුරකථන ඡායාරූප",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 3. Open Device Videos (Native Android Picker)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF242526))
                        .border(1.dp, Color(0xFF3A3B3C), RoundedCornerShape(14.dp))
                        .clickable { onOpenDeviceVideoPicker() }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF43F5E).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Device Videos",
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "දුරකථන වීඩියෝ",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Gallery photos and videos
            items(galleryItems) { item ->
                val isSelected = selectedUrls.contains(item.url)

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(Color(0xFF242526))
                        .clickable {
                            if (isMultipleSelectMode) {
                                onToggleUrlSelect(item.url)
                            } else {
                                onSelectSingleItem(item)
                            }
                        }
                ) {
                    AsyncImage(
                        model = item.url,
                        contentDescription = "Gallery Item",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Video duration pill (e.g. 📹 9:06, 0:18 as in user's screenshot)
                    if (item.mediaType == MediaType.VIDEO && item.durationText != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_3d_video_play),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(13.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.durationText,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Selection indicator check circle in multi-select mode
                    if (isMultipleSelectMode) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF1877F2) else Color.Black.copy(alpha = 0.5f))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Bottom Proceed Bar if multiple items selected
        if (isMultipleSelectMode && selectedUrls.isNotEmpty()) {
            Surface(
                color = Color(0xFF242526),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedUrls.size} ක් තෝරා ගන්නා ලදී",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = onProceedWithMultiple,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("ඊළඟට >", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Screen 1: "පෝස්ටුව තනන්න" Post Composer Screen (Matching Screenshot 1)
 */
@Composable
private fun PostComposerScreen(
    currentUser: User,
    postText: String,
    onPostTextChanged: (String) -> Unit,
    selectedMediaUrl: String?,
    selectedMediaType: MediaType,
    onRemoveMedia: () -> Unit,
    onChangeMedia: () -> Unit,
    privacySetting: String,
    onChangePrivacy: (String) -> Unit,
    selectedMusic: String?,
    onOpenMusicSelector: () -> Unit,
    onRemoveMusic: () -> Unit,
    taggedFriends: List<String>,
    onOpenTagSelector: () -> Unit,
    onRemoveTag: (String) -> Unit,
    selectedLocation: String?,
    onOpenLocationSelector: () -> Unit,
    onRemoveLocation: () -> Unit,
    selectedFeeling: String?,
    onOpenFeelingSelector: () -> Unit,
    onRemoveFeeling: () -> Unit,
    onOpenAiHelper: () -> Unit,
    onOpenAiMagicStudio: () -> Unit,
    onBack: () -> Unit,
    onPublish: () -> Unit
) {
    val context = LocalContext.current
    var isPrivacyDropdownOpen by remember { mutableStateOf(false) }
    
    // New Feature States
    var isPollMode by remember { mutableStateOf(false) }
    val pollOptions = remember { mutableStateListOf<String>("", "") }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var voiceNoteRecorded by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }
    var showBackgroundStyles by remember { mutableStateOf(false) }
    var selectedBackgroundBrush by remember { mutableStateOf<Brush?>(null) }
    var is3DApplied by remember { mutableStateOf(false) }

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            while(true) {
                delay(1000L)
                recordingTime++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF18191A))
    ) {
        // Top App Bar: [←]  පෝස්ටුව තනන්න  [පළ කරන්න (Blue Text)]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "පෝස්ටුව තනන්න",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onPublish,
                enabled = postText.isNotBlank() || selectedMediaUrl != null
            ) {
                Text(
                    text = "පළ කරන්න",
                    color = if (postText.isNotBlank() || selectedMediaUrl != null) Color(0xFF1877F2) else Color.Gray,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider(color = Color(0xFF2E3033), thickness = 0.8.dp)

        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User Header Row (Avatar, Name, Privacy chips)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // User Avatar
                AsyncImage(
                    model = currentUser.avatarUrl,
                    contentDescription = currentUser.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = currentUser.name,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Privacy Pill [👥 මිතුරන් ▾] and [🔲 AI ▾]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Privacy Pill
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2E3033))
                                    .clickable { isPrivacyDropdownOpen = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = when (privacySetting) {
                                        "ප්‍රසිද්ධ" -> Icons.Default.Public
                                        "මම පමණයි" -> Icons.Default.Lock
                                        else -> Icons.Default.Groups
                                    },
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = privacySetting,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = isPrivacyDropdownOpen,
                                onDismissRequest = { isPrivacyDropdownOpen = false },
                                modifier = Modifier.background(Color(0xFF242526))
                            ) {
                                listOf("ප්‍රසිද්ධ", "මිතුරන්", "මම පමණයි").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = Color.White) },
                                        onClick = {
                                            onChangePrivacy(option)
                                            isPrivacyDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        // AI Helper Pill: [🔲 AI ▾]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF2E3033))
                                .clickable { onOpenAiHelper() }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF4285F4),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post Text Input Container (Dark rounded box as in Screenshot 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(selectedBackgroundBrush ?: SolidColor(Color(0xFF242526)))
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = postText,
                    onValueChange = onPostTextChanged,
                    placeholder = {
                        Text(
                            text = if (selectedMediaUrl != null) "මෙම ඡායාරූපය ගැන යමක් පවසන්න..." else "ඔබ සිතන්නේ කුමක්ද?...",
                            color = Color(0xFFAAAAAA),
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_post_text_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Media Card (Exact Replica: photo thumbnail with ✏️ and 🗑️ icons overlayed on top right!)
            if (!selectedMediaUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF242526))
                        .border(1.dp, Color(0xFF3A3B3C), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = selectedMediaUrl,
                        contentDescription = "Selected Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (selectedMediaType == MediaType.VIDEO) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Video",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    if (is3DApplied) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("3D", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                    }

                    // Overlay Action Pills on Top-Right: 🪄 AI Magic, ✏️ Edit and 🗑️ Delete
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 🪄 AI Magic Photo Editor Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))))
                                .clickable { onOpenAiMagicStudio() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Magic Studio",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // ✏️ Edit / Change Photo Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                                .clickable { onChangeMedia() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // 🗑️ Delete Photo Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                                .clickable { onRemoveMedia() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Attached Badges Chips (Music, Tag, Location, Feeling)
            if (selectedMusic != null || taggedFriends.isNotEmpty() || selectedLocation != null || selectedFeeling != null) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    if (selectedMusic != null) {
                        item {
                            PostAttachedBadge(
                                icon = Icons.Default.MusicNote,
                                tint = Color(0xFFE91E63),
                                text = selectedMusic,
                                onRemove = onRemoveMusic
                            )
                        }
                    }
                    if (selectedLocation != null) {
                        item {
                            PostAttachedBadge(
                                icon = Icons.Default.LocationOn,
                                tint = Color(0xFFF02849),
                                text = selectedLocation,
                                onRemove = onRemoveLocation
                            )
                        }
                    }
                    if (selectedFeeling != null) {
                        item {
                            PostAttachedBadge(
                                icon = Icons.Default.Mood,
                                tint = Color(0xFFF7B125),
                                text = "Feeling $selectedFeeling",
                                onRemove = onRemoveFeeling
                            )
                        }
                    }
                    items(taggedFriends) { friend ->
                        PostAttachedBadge(
                            icon = Icons.Default.PersonAdd,
                            tint = Color(0xFF1877F2),
                            text = "@$friend",
                            onRemove = { onRemoveTag(friend) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🎨 Background Styles UI
            if (showBackgroundStyles) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    item {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF242526)).clickable { selectedBackgroundBrush = null }.border(1.dp, Color.Gray, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, contentDescription = "None", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    val styles = listOf(
                        Brush.linearGradient(listOf(Color(0xFFFF9A9E), Color(0xFFFECFEF))),
                        Brush.linearGradient(listOf(Color(0xFFa18cd1), Color(0xFFfbc2eb))),
                        Brush.linearGradient(listOf(Color(0xFF84fab0), Color(0xFF8fd3f4))),
                        Brush.linearGradient(listOf(Color(0xFFfccb90), Color(0xFFd57eeb))),
                        Brush.linearGradient(listOf(Color(0xFFff0844), Color(0xFFffb199))),
                        Brush.linearGradient(listOf(Color(0xFF4facfe), Color(0xFF00f2fe)))
                    )
                    items(styles) { brush ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(brush)
                                .clickable { selectedBackgroundBrush = brush }
                                .border(2.dp, if (selectedBackgroundBrush == brush) Color.White else Color.Transparent, CircleShape)
                        )
                    }
                }
            }

            // 📊 Poll UI
            if (isPollMode) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF242526)).padding(12.dp)) {
                    Text("ඡන්ද විමසීමක් (Poll Options)", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    pollOptions.forEachIndexed { index, text ->
                        OutlinedTextField(
                            value = text,
                            onValueChange = { pollOptions[index] = it },
                            placeholder = { Text("විකල්පය ${index + 1}", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3A3B3C), unfocusedBorderColor = Color(0xFF3A3B3C),
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            )
                        )
                    }
                    TextButton(onClick = { pollOptions.add("") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF1877F2))
                        Spacer(Modifier.width(4.dp))
                        Text("විකල්පයක් එක් කරන්න", color = Color(0xFF1877F2))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 🎤 Voice Note UI
            if (isRecordingVoice || voiceNoteRecorded) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFF242526)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isRecordingVoice) { isRecordingVoice = false; voiceNoteRecorded = true }
                            else { isRecordingVoice = true; voiceNoteRecorded = false; recordingTime = 0 }
                        },
                        modifier = Modifier.size(40.dp).background(if (isRecordingVoice) Color(0xFFFF5722) else Color(0xFF1877F2), CircleShape)
                    ) {
                        Icon(if (isRecordingVoice) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    if (isRecordingVoice) {
                        Text("පටිගත වෙමින්... (00:${recordingTime.toString().padStart(2, '0')})", color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
                    } else if (voiceNoteRecorded) {
                        Text("හඬ පටය (00:${recordingTime.toString().padStart(2, '0')})", color = Color.White)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { voiceNoteRecorded = false }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Voice Note", tint = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action Items List (Screenshot 1 Exact Layout)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 1. 🖼️ ඡායාරූප/වීඩියෝ (Green)
                PostActionListItem(
                    icon = Icons.Default.Image,
                    iconTint = Color(0xFF31A24C),
                    title = "ඡායාරූප/වීඩියෝ",
                    onClick = onChangeMedia
                )

                // 2. 🎵 සංගීතය (Pink)
                PostActionListItem(
                    icon = Icons.Default.MusicNote,
                    iconTint = Color(0xFFE91E63),
                    title = "සංගීතය",
                    onClick = onOpenMusicSelector
                )

                // 3. 👤+ පුද්ගලයන් ටැග් කරන්න (Blue)
                PostActionListItem(
                    icon = Icons.Default.PersonAdd,
                    iconTint = Color(0xFF1877F2),
                    title = "පුද්ගලයන් ටැග් කරන්න",
                    onClick = onOpenTagSelector
                )

                // 4. 📍 පිහිටීම එක් කරන්න (Red)
                PostActionListItem(
                    icon = Icons.Default.LocationOn,
                    iconTint = Color(0xFFF02849),
                    title = "පිහිටීම එක් කරන්න",
                    onClick = onOpenLocationSelector
                )

                // 5. 😊 හැඟීම/ක්‍රියාකාරකම (Yellow)
                PostActionListItem(
                    icon = Icons.Default.Mood,
                    iconTint = Color(0xFFF7B125),
                    title = "හැඟීම/ක්‍රියාකාරකම",
                    onClick = onOpenFeelingSelector
                )

                // 6. 🎤 හඬ පටයක් (Reddish Orange)
                PostActionListItem(
                    icon = Icons.Default.Mic,
                    iconTint = Color(0xFFFF5722),
                    title = "හඬ පටයක් එක් කරන්න (Voice Note)",
                    onClick = { 
                        isRecordingVoice = true
                        voiceNoteRecorded = false
                        recordingTime = 0
                    }
                )

                // 7. 📊 ඡන්ද විමසීමක් (Teal)
                PostActionListItem(
                    icon = Icons.Default.BarChart,
                    iconTint = Color(0xFF009688),
                    title = "ඡන්ද විමසීමක් (Poll)",
                    onClick = { isPollMode = !isPollMode }
                )

                // 8. 🪄 AI Magic Photo Editor (Purple-Pink Gradient)
                PostActionListItem(
                    icon = Icons.Default.AutoAwesome,
                    iconTint = Color(0xFFC084FC),
                    title = "AI Magic Photo Studio (Magic Expand / BG / 3D Avatar)",
                    onClick = {
                        if (selectedMediaUrl != null) {
                            onOpenAiMagicStudio()
                        } else {
                            android.widget.Toast.makeText(context, "කරුණාකර පළමුව ඡායාරූපයක් තෝරන්න 📸", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // 9. ✨ AI Caption Assist (Blue)
                PostActionListItem(
                    icon = Icons.Default.AutoAwesome,
                    iconTint = Color(0xFF1877F2),
                    title = "AI Caption & Text Assist (ලියන්න AI)",
                    onClick = onOpenAiHelper
                )

                // 9. 📸 3D ඡායාරූපය (Blue Grey)
                PostActionListItem(
                    icon = Icons.Default.Camera,
                    iconTint = Color(0xFF607D8B),
                    title = "3D ඡායාරූපයක් සාදන්න",
                    onClick = { 
                        if (selectedMediaUrl != null) {
                            is3DApplied = !is3DApplied
                        } else {
                            android.widget.Toast.makeText(context, "කරුණාකර පළමුව ඡායාරූපයක් තෝරන්න", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // 10. 🎨 පසුබිම් වර්ණ (Gradient)
                PostActionListItem(
                    icon = Icons.Default.Style,
                    iconTint = Color(0xFFE91E63),
                    title = "පසුබිම් වර්ණ (Background Styles)",
                    onClick = { showBackgroundStyles = !showBackgroundStyles }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Big Bottom Facebook Lite Blue Button: [පළ කරන්න]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onPublish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("publish_post_submit_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2),
                    disabledContainerColor = Color(0xFF2E3033)
                ),
                enabled = postText.isNotBlank() || selectedMediaUrl != null
            ) {
                Text(
                    text = "පළ කරන්න",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Individual action item row in Screenshot 1 list:
 * e.g., 🖼️ ඡායාරූප/වීඩියෝ, 🎵 සංගීතය, 👤+ පුද්ගලයන් ටැග් කරන්න, 📍 පිහිටීම එක් කරන්න, 😊 හැඟීම/ක්‍රියාකාරකම
 */
@Composable
private fun PostActionListItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Attached tag/music/location badge chip.
 */
@Composable
private fun PostAttachedBadge(
    icon: ImageVector,
    tint: Color,
    text: String,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2E3033))
            .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = Color.LightGray,
            modifier = Modifier
                .size(14.dp)
                .clickable { onRemove() }
        )
    }
}

// -------------------------------------------------------------
// Action Sheets & Dialogs for 100% 500% functionality
// -------------------------------------------------------------

@Composable
private fun MusicSelectDialog(
    onClose: () -> Unit,
    onSelect: (String) -> Unit
) {
    val songs = listOf(
        "Manike Mage Hithe - Yohani & Chamath",
        "Naadha Gamana - Bathiya & Santhush",
        "Lofi Rain Vibes - Chill Hop",
        "Sanda Tharu Mal - Kasun Kalhara",
        "Hitha Wawannema Na - Rookantha",
        "Dineka Mage Mathaka - Sanuka"
    )

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎵 සංගීත ඛණ්ඩයක් තෝරන්න", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                songs.forEach { song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(song) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(song, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TagFriendsDialog(
    currentlyTagged: List<String>,
    onClose: () -> Unit,
    onToggleFriend: (String) -> Unit
) {
    val friends = listOf(
        "නිමල් පෙරේරා",
        "කසුන් බණ්ඩාර",
        "දසුන් මධුශංක",
        "සචිනි ප්‍රනාන්දු",
        "චමෝද් වික්‍රමසිංහ",
        "නෙත්මි තත්සරණි"
    )

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👤+ පුද්ගලයන් ටැග් කරන්න", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                friends.forEach { friend ->
                    val isTagged = currentlyTagged.contains(friend)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleFriend(friend) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(friend, color = Color.White, fontSize = 14.sp)
                        }
                        if (isTagged) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationSelectDialog(
    onClose: () -> Unit,
    onSelect: (String) -> Unit
) {
    val locations = listOf(
        "Colombo, Sri Lanka",
        "Kandy, Sri Lanka",
        "Galle Fort, Sri Lanka",
        "Ella, Sri Lanka",
        "Negombo Beach, Sri Lanka",
        "Sigiriya, Sri Lanka"
    )

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📍 පිහිටීමක් එක් කරන්න", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                locations.forEach { loc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(loc) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFF02849), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(loc, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeelingSelectDialog(
    onClose: () -> Unit,
    onSelect: (String) -> Unit
) {
    val feelings = listOf(
        "සතුටුයි 😊",
        "ආශිර්වාදමත් 🙏",
        "ආදරණීය ❤️",
        "විවේකීව 🏖️",
        "විනෝදයෙන් 🎉",
        "කෘතඥයි ✨"
    )

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("😊 හැඟීම / ක්‍රියාකාරකම", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                feelings.forEach { feel ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(feel) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(feel, color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AiPostAssistDialog(
    currentText: String,
    onClose: () -> Unit,
    onApplyText: (String) -> Unit
) {
    val suggestions = listOf(
        "ජීවිතයේ සුන්දරම අවස්ථා මිතුරන් සමඟ විඳිමින්... ✨📸 #SriLanka #Memories",
        "අලුත් දවසක්, අලුත් බලාපොරොත්තු සමගින්! සුබ දවසක් වේවා සැමට! 🌸🙏",
        "Traveling around Sri Lanka, collecting wonderful moments 🍃🌿 #NatureLover",
        "සතුට කියන්නේ අපිට ලැබෙන පුංචි දේවල් අගය කිරීමයි ❤️"
    )

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF4285F4))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Post Assist 💡", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("කැමති Caption එකක් තෝරන්න:", color = Color.LightGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                suggestions.forEach { sugg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E3033))
                            .clickable { onApplyText(sugg) }
                            .padding(10.dp)
                    ) {
                        Text(sugg, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
