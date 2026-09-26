package com.example.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.MediaType
import com.example.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

// Live Guest Model
data class LiveGuest(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isMuted: Boolean = false,
    val isVideoOn: Boolean = true,
    val isSpeaking: Boolean = false,
    val joinedCity: String = "Sri Lanka",
    val giftsSentCount: Int = 0
)

// Live Comment Model
data class LiveChatComment(
    val id: String,
    val userName: String,
    val userAvatar: String,
    val message: String,
    val isHost: Boolean = false,
    val isGiftNotice: Boolean = false,
    val giftIcon: String = ""
)

// Live Gift Model
data class TikTokGift(
    val id: String,
    val name: String,
    val icon: String,
    val coins: Int,
    val bannerBg: Color,
    val animationType: String
)

val TIKTOK_GIFTS = listOf(
    TikTokGift("1", "Rose", "🌹", 1, Color(0xFFE91E63), "ROSE_SHOWER"),
    TikTokGift("2", "Heart Me", "💖", 5, Color(0xFFFF4081), "HEART_BURST"),
    TikTokGift("3", "Perfume", "🧴", 20, Color(0xFF9C27B0), "SPARKLE"),
    TikTokGift("4", "Diamond", "💎", 99, Color(0xFF00E5FF), "DIAMOND_SHINE"),
    TikTokGift("5", "Golden Lion", "🦁", 1000, Color(0xFFFFD700), "LION_ROAR"),
    TikTokGift("6", "Legendary Dragon", "🐉", 2999, Color(0xFFFF3D00), "DRAGON_FLAME"),
    TikTokGift("7", "Galaxy Space", "🌌", 5000, Color(0xFF7C4DFF), "GALAXY_ORBIT")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TikTokLiveStudioModal(
    currentUser: User,
    onDismiss: () -> Unit,
    onPostToFeed: (title: String, videoUrl: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Camera Permissions & Hardware State
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "📹 සජීවී කැමරාව සාර්ථකව සම්බන්ධ විය!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "කැමරා අවසරය ලබා දෙන්න (Camera permission is needed for Live)", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Recording State
    var isRecording by remember { mutableStateOf(true) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var isHostMicMuted by remember { mutableStateOf(false) }
    var isBeautyFilterOn by remember { mutableStateOf(true) }
    var isFlashOn by remember { mutableStateOf(false) }

    // Live Metrics
    var viewerCount by remember { mutableIntStateOf(1840) }
    var totalLikes by remember { mutableIntStateOf(14200) }
    var totalCoins by remember { mutableIntStateOf(850) }

    // End Live & Save Dialog State
    var showEndLiveSummary by remember { mutableStateOf(false) }
    var isDownloadingToGallery by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var isDownloadCompleted by remember { mutableStateOf(false) }
    var postCaptionInput by remember { mutableStateOf("🔴 My TikTok Live Stream Session with Friends! #Live #FriendHub") }

    // Live Guests (Multi-Guest Screen)
    val liveGuests = remember {
        mutableStateListOf(
            LiveGuest(
                id = "guest_1",
                name = "Kasun Perera",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
                isMuted = false,
                isVideoOn = true,
                isSpeaking = true,
                joinedCity = "Colombo"
            ),
            LiveGuest(
                id = "guest_2",
                name = "Dinithi Silva",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                isMuted = false,
                isVideoOn = true,
                isSpeaking = false,
                joinedCity = "Kandy"
            ),
            LiveGuest(
                id = "guest_3",
                name = "Lahiru Madushan",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                isMuted = true,
                isVideoOn = true,
                isSpeaking = false,
                joinedCity = "Galle"
            )
        )
    }

    // Live Comments Stream
    val chatComments = remember {
        mutableStateListOf(
            LiveChatComment("1", "Amila Bandara", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200", "සුපිරි Live එකක් සහෝදරයා! 🔥🔥"),
            LiveChatComment("2", "Sandeepa", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=200", "Hello from Pelmadulla! 👋❤️"),
            LiveChatComment("3", "Nadeesha", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200", "Voice clarity is awesome! 🎤✨"),
            LiveChatComment("4", "Kavindu", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200", "Gift එකක් දැම්මා මචං! 🌹🌹", isGiftNotice = true, giftIcon = "🌹")
        )
    }
    var commentInputText by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()

    // Gift Tray & Active Gift Animations
    var showGiftTray by remember { mutableStateOf(false) }
    var activeGiftAnimation by remember { mutableStateOf<TikTokGift?>(null) }
    var activeGiftSender by remember { mutableStateOf("") }
    var activeGiftCount by remember { mutableIntStateOf(1) }

    // Manage Guests Sheet
    var showGuestsManagementSheet by remember { mutableStateOf(false) }

    // Floating Hearts Animation State
    val floatingHearts = remember { mutableStateListOf<Offset>() }

    // Timer coroutine for recording seconds
    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            recordingSeconds++
            // Randomly increase likes and viewers to feel alive
            if (recordingSeconds % 3 == 0) {
                viewerCount += Random.nextInt(1, 5)
                totalLikes += Random.nextInt(5, 20)
            }
        }
    }

    // Auto-scroll chat to latest
    LaunchedEffect(chatComments.size) {
        if (chatComments.isNotEmpty()) {
            chatListState.animateScrollToItem(chatComments.size - 1)
        }
    }

    // Simulated Voice Activity for guests (Audio waves)
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            val randomIndex = Random.nextInt(liveGuests.size)
            val current = liveGuests[randomIndex]
            if (!current.isMuted) {
                liveGuests[randomIndex] = current.copy(isSpeaking = !current.isSpeaking)
            }
        }
    }

    // Automated incoming comments & gifts simulation
    LaunchedEffect(Unit) {
        val sampleIncoming = listOf(
            "Chamara: ආයුබෝවන් හැමෝටම! 🇱🇰",
            "Nalinda: අද Topic එක ගොඩක් වටිනවා 👍",
            "Tharushi: මාවත් Guest විදිහට Add කරගන්නකෝ 🥰",
            "Sahan: Video Quality එක 1080p තියෙනවා 🔥",
            "Ruwan: Super brother, keep it up!"
        )
        var msgIdx = 0
        while (true) {
            delay(4000)
            if (msgIdx < sampleIncoming.size) {
                val parts = sampleIncoming[msgIdx].split(": ")
                chatComments.add(
                    LiveChatComment(
                        id = System.currentTimeMillis().toString(),
                        userName = parts[0],
                        userAvatar = "https://images.unsplash.com/photo-${1500000000000 + Random.nextInt(1000000, 9999999)}?w=200",
                        message = parts[1]
                    )
                )
                msgIdx++
            } else {
                msgIdx = 0
            }
        }
    }

    // Pulse animation for recording red dot
    val infiniteTransition = rememberInfiniteTransition(label = "RecPulse")
    val recPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recPulse"
    )

    // Voice Wave Animation for speaking guests
    val voiceWaveScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voiceWave"
    )

    fun triggerGiftAnimation(gift: TikTokGift, senderName: String) {
        activeGiftAnimation = gift
        activeGiftSender = senderName
        activeGiftCount = Random.nextInt(1, 10)
        totalCoins += gift.coins * activeGiftCount
        totalLikes += gift.coins * 50

        chatComments.add(
            LiveChatComment(
                id = System.currentTimeMillis().toString(),
                userName = senderName,
                userAvatar = currentUser.avatarUrl,
                message = "Sent ${gift.name} ${gift.icon} x$activeGiftCount",
                isGiftNotice = true,
                giftIcon = gift.icon
            )
        )

        coroutineScope.launch {
            delay(3500)
            activeGiftAnimation = null
        }
    }

    fun downloadVideoToGallery() {
        isDownloadingToGallery = true
        downloadProgress = 0f
        coroutineScope.launch {
            for (i in 1..100) {
                delay(20)
                downloadProgress = i / 100f
            }
            isDownloadingToGallery = false
            isDownloadCompleted = true

            // Real MediaStore registration / mock saving
            try {
                val fileName = "FriendHub_Live_${System.currentTimeMillis()}.mp4"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/FriendHub")
                    }
                    context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                }
            } catch (e: Exception) {
                // graceful catch
            }

            Toast.makeText(context, "✅ වීඩියෝව ගැලරියට සාර්ථකව Download කරන ලදී! (Gallery Saved)", Toast.LENGTH_LONG).show()
        }
    }

    Dialog(
        onDismissRequest = {
            showEndLiveSummary = true
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            // Spawn floating hearts
                            floatingHearts.add(offset)
                            totalLikes += 2
                            coroutineScope.launch {
                                delay(1800)
                                if (floatingHearts.isNotEmpty()) floatingHearts.removeAt(0)
                            }
                        }
                    )
                }
        ) {
            // ==================== 1. LIVE VIDEO CAMERA FEED BACKGROUND ====================
            // Real CameraX Live Camera Preview (Front / Back Selfie Camera)
            Box(modifier = Modifier.fillMaxSize()) {
                TikTokLiveCameraPreview(
                    isFrontCamera = isFrontCamera,
                    isFlashOn = isFlashOn,
                    isBeautyFilterOn = isBeautyFilterOn,
                    hasCameraPermission = hasCameraPermission,
                    onRequestCameraPermission = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // High contrast aesthetic gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
            }

            // ==================== 2. MULTI-GUEST VIDEO GRID (LIVE INNA AYA) ====================
            // Live Guest Video boxes on the Right Side (TikTok Multi-Guest Layout)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp, top = 110.dp)
                    .width(110.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                liveGuests.forEachIndexed { index, guest ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .border(
                                width = if (guest.isSpeaking && !guest.isMuted) 2.dp else 1.dp,
                                color = if (guest.isSpeaking && !guest.isMuted) Color(0xFF00E676) else Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                // Tap guest to toggle individual Mute / Options
                                val updatedMute = !guest.isMuted
                                liveGuests[index] = guest.copy(isMuted = updatedMute, isSpeaking = if (updatedMute) false else guest.isSpeaking)
                                Toast.makeText(
                                    context,
                                    if (updatedMute) "${guest.name} Muted 🔇" else "${guest.name} Unmuted 🔊",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (guest.isVideoOn) {
                                AsyncImage(
                                    model = guest.avatarUrl,
                                    contentDescription = guest.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF262626)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VideocamOff, contentDescription = "Video Off", tint = Color.Gray)
                                }
                            }

                            // Dark overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                        )
                                    )
                            )

                            // Speaking Voice Wave Indicator
                            if (guest.isSpeaking && !guest.isMuted) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(4.dp)
                                        .background(Color(0xFF00E676), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = "Talking",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .scale(voiceWaveScale)
                                    )
                                }
                            }

                            // Muted Indicator or Audio Status (Individually Mute/Unmute)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (guest.isMuted) Color(0xFFE53935) else Color.Black.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (guest.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mic Status",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }

                            // Guest Name & City tag
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = guest.name,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "📍 ${guest.joinedCity}",
                                    color = Color(0xFFB0BEC5),
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Add / Invite Guest Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .clickable { showGuestsManagementSheet = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Guest", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add (${liveGuests.size})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ==================== 3. TOP TIKTOK LIVE HEADER ====================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 12.dp, end = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Host Profile Pill & Coins badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = "Host Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color(0xFFFF0050), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = currentUser.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙 $totalCoins", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "• ❤️ $totalLikes", color = Color(0xFFFF4081), fontSize = 10.sp)
                            }
                        }
                    }

                    // Middle: RECORDING STATUS (● REC 02:45)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE53935).copy(alpha = 0.85f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .alpha(recPulseAlpha)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val mins = recordingSeconds / 60
                        val secs = recordingSeconds % 60
                        Text(
                            text = "REC ${String.format("%02d:%02d", mins, secs)}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Right: Top Viewers count & Close Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👁️ $viewerCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Close / End Live Button
                        IconButton(
                            onClick = { showEndLiveSummary = true },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "End Live", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

            }

            // ==================== 4. ACTIVE GIFT FULL-SCREEN ANIMATION (TIKTOK STYLE) ====================
            AnimatedVisibility(
                visible = activeGiftAnimation != null,
                enter = scaleIn(tween(400)) + fadeIn(),
                exit = scaleOut(tween(400)) + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                activeGiftAnimation?.let { gift ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(gift.bannerBg.copy(alpha = 0.85f))
                            .padding(horizontal = 24.dp, vertical = 18.dp)
                    ) {
                        Text(
                            text = gift.icon,
                            fontSize = 64.sp,
                            modifier = Modifier.scale(voiceWaveScale)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${gift.name} x$activeGiftCount",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "From: $activeGiftSender 🎁",
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ==================== 5. FLOATING HEARTS PARTICLES ====================
            floatingHearts.forEach { offset ->
                Box(
                    modifier = Modifier
                        .offset(x = offset.x.dp, y = (offset.y - 100).dp)
                        .scale(1.2f)
                ) {
                    Text(text = "❤️", fontSize = 24.sp)
                }
            }

            // ==================== 6. BOTTOM LIVE CONTROLS & COMMENTS STREAM ====================
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                // Live Chat Comments Box
                LazyColumn(
                    state = chatListState,
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(chatComments, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (comment.isGiftNotice) Color(0xFFFF0050).copy(alpha = 0.8f)
                                    else Color.Black.copy(alpha = 0.45f)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${comment.userName}: ",
                                color = if (comment.isGiftNotice) Color.Yellow else Color(0xFF00E5FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = comment.message,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Tools Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chat Input Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(22.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commentInputText,
                                onValueChange = { commentInputText = it },
                                placeholder = { Text("Say something in Live...", color = Color.Gray, fontSize = 12.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (commentInputText.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        chatComments.add(
                                            LiveChatComment(
                                                id = System.currentTimeMillis().toString(),
                                                userName = "You (Host)",
                                                userAvatar = currentUser.avatarUrl,
                                                message = commentInputText,
                                                isHost = true
                                            )
                                        )
                                        commentInputText = ""
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF00E5FF))
                                }
                            }
                        }
                    }

                    // 1. Host Microphone Mute / Unmute Button
                    IconButton(
                        onClick = {
                            isHostMicMuted = !isHostMicMuted
                            Toast.makeText(
                                context,
                                if (isHostMicMuted) "ඔබේ මයික්‍රෆෝනය Mute කරන ලදී 🔇" else "මයික්‍රෆෝනය සක්‍රීයයි 🎙️",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(if (isHostMicMuted) Color(0xFFE53935) else Color.Black.copy(alpha = 0.55f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isHostMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Host Mic",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 2. Flip Camera (Front / Selfie <-> Back Camera)
                    IconButton(
                        onClick = {
                            isFrontCamera = !isFrontCamera
                            Toast.makeText(
                                context,
                                if (isFrontCamera) "Selfie Camera සක්‍රීයයි 🤳" else "Back Camera සක්‍රීයයි 📷",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Flip Camera",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Flash / Torch Toggle Button
                    IconButton(
                        onClick = {
                            isFlashOn = !isFlashOn
                            Toast.makeText(
                                context,
                                if (isFlashOn) "Flash / Torch සක්‍රීයයි ⚡" else "Flash අක්‍රීයයි 🌑",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(if (isFlashOn) Color(0xFFFFD700).copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.55f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Toggle Flash",
                            tint = if (isFlashOn) Color.Black else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 3. TikTok Gift Box Button
                    IconButton(
                        onClick = { showGiftTray = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFF0050), CircleShape)
                            .shadow(6.dp, CircleShape)
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Send Gift", tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    // 4. Double-Tap Like Heart Button
                    IconButton(
                        onClick = {
                            totalLikes += 5
                            floatingHearts.add(Offset(Random.nextInt(100, 300).toFloat(), 600f))
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFF4081), CircleShape)
                    ) {
                        Text(text = "💖", fontSize = 20.sp)
                    }
                }
            }

            // ==================== 7. TIKTOK GIFT TRAY BOTTOM SHEET ====================
            if (showGiftTray) {
                ModalBottomSheet(
                    onDismissRequest = { showGiftTray = false },
                    containerColor = Color(0xFF1E1E1E),
                    scrimColor = Color.Black.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎁 TikTok Live Gifts & Animations",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Coin Balance: 🪙 $totalCoins", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Gift Items Grid
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(TIKTOK_GIFTS) { gift ->
                                Card(
                                    modifier = Modifier
                                        .width(95.dp)
                                        .clickable {
                                            triggerGiftAnimation(gift, currentUser.name)
                                            showGiftTray = false
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = gift.icon, fontSize = 36.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = gift.name,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "🪙 ${gift.coins}",
                                            color = Color(0xFFFFD700),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // ==================== 8. LIVE GUESTS MANAGEMENT MODAL ====================
            if (showGuestsManagementSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showGuestsManagementSheet = false },
                    containerColor = Color(0xFF18191A),
                    scrimColor = Color.Black.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "👥 Live Video Guests (${liveGuests.size} on stage)",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Manage participant audio, mute/unmute individually, or invite new viewers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        liveGuests.forEachIndexed { index, guest ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF242526))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = guest.avatarUrl,
                                    contentDescription = guest.name,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(guest.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = if (guest.isMuted) "🔇 Muted by Host" else if (guest.isSpeaking) "🟢 Speaking..." else "Connected",
                                        color = if (guest.isMuted) Color(0xFFE53935) else Color(0xFF00E676),
                                        fontSize = 11.sp
                                    )
                                }

                                // Toggle Mute
                                OutlinedButton(
                                    onClick = {
                                        val toggled = !guest.isMuted
                                        liveGuests[index] = guest.copy(isMuted = toggled, isSpeaking = if (toggled) false else guest.isSpeaking)
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (guest.isMuted) Color(0xFF00E676) else Color(0xFFE53935)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(if (guest.isMuted) "Unmute 🔊" else "Mute 🔇", fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Remove Guest
                                IconButton(
                                    onClick = {
                                        liveGuests.removeAt(index)
                                        Toast.makeText(context, "${guest.name} removed from live", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.PersonRemove, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add new guest button
                        Button(
                            onClick = {
                                liveGuests.add(
                                    LiveGuest(
                                        id = "guest_${System.currentTimeMillis()}",
                                        name = "Ruvini Fernando",
                                        avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400",
                                        isMuted = false,
                                        isVideoOn = true,
                                        isSpeaking = true,
                                        joinedCity = "Negombo"
                                    )
                                )
                                Toast.makeText(context, "New Guest Added to Live Stage! 🎉", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Invite Another Viewer to Stage (+)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ==================== 9. END LIVE RECORDING SUMMARY & POST / DOWNLOAD DIALOG ====================
            if (showEndLiveSummary) {
                Dialog(
                    onDismissRequest = { showEndLiveSummary = false }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(16.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉 Live Video Record Completed!",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "ඔබගේ සජීවී විකාශය සාර්ථකව පටිගත විය.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB0B3B8)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Statistics Summary Grid
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF18191A))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "⏱️ කාලය", color = Color.Gray, fontSize = 11.sp)
                                    Text(
                                        text = "${recordingSeconds / 60}m ${recordingSeconds % 60}s",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "👁️ Viewers", color = Color.Gray, fontSize = 11.sp)
                                    Text(
                                        text = "$viewerCount",
                                        color = Color(0xFF00E5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "❤️ Likes", color = Color.Gray, fontSize = 11.sp)
                                    Text(
                                        text = "$totalLikes",
                                        color = Color(0xFFFF4081),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Caption input for post
                            OutlinedTextField(
                                value = postCaptionInput,
                                onValueChange = { postCaptionInput = it },
                                label = { Text("Post Caption (පෝස්ට් එකේ සටහන)", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1877F2),
                                    unfocusedBorderColor = Color(0xFF3A3B3C),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action 1: Download to Gallery Button
                            if (isDownloadingToGallery) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    LinearProgressIndicator(
                                        progress = { downloadProgress },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFF00E676),
                                        trackColor = Color(0xFF3A3B3C)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("ගැලරියට බාගත වෙමින් පවතී... (${(downloadProgress * 100).toInt()}%)", color = Color(0xFF00E676), fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = { downloadVideoToGallery() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDownloadCompleted) Color(0xFF2E7D32) else Color(0xFF31A24C)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDownloadCompleted) Icons.Default.Check else Icons.Default.Download,
                                        contentDescription = "Download"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isDownloadCompleted) "Gallery එකට Download කරන ලදී ✅" else "Download to Gallery (ගැලරියට බාගන්න)",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action 2: Post to Feed Button
                            Button(
                                onClick = {
                                    val videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                                    onPostToFeed(postCaptionInput, videoUrl)
                                    Toast.makeText(context, "🚀 Live Video එක Feed එකේ සාර්ථකව පල කරන ලදී!", Toast.LENGTH_LONG).show()
                                    showEndLiveSummary = false
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Publish, contentDescription = "Post")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Post to Feed (පෝස්ට් එකක් ලෙස පල කරන්න 🚀)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Dismiss without posting
                            OutlinedButton(
                                onClick = {
                                    showEndLiveSummary = false
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                            ) {
                                Text("අවසන් කරන්න (Close)")
                            }
                        }
                    }
                }
            }

        }
    }
}

/**
 * Real CameraX Live Camera Preview for TikTok Live Studio:
 * - Dynamically binds to Front (Selfie) or Back camera based on `isFrontCamera`.
 * - Controls Flash/Torch when `isFlashOn` changes on the back camera.
 * - Handles camera permissions and fallback gracefully.
 */
@Composable
fun TikTokLiveCameraPreview(
    isFrontCamera: Boolean,
    isFlashOn: Boolean,
    isBeautyFilterOn: Boolean,
    hasCameraPermission: Boolean,
    onRequestCameraPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var hasCameraBindingError by remember { mutableStateOf(false) }

    // Toggle torch/flash on the bound camera
    LaunchedEffect(isFlashOn, boundCamera, isFrontCamera) {
        if (!isFrontCamera && boundCamera?.cameraInfo?.hasFlashUnit() == true) {
            try {
                boundCamera?.cameraControl?.enableTorch(isFlashOn)
            } catch (e: Exception) {
                // Torch unsupported
            }
        }
    }

    if (hasCameraPermission && !hasCameraBindingError) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider

                        val cameraSelector = if (isFrontCamera) {
                            if (provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                        } else {
                            if (provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                        }

                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        provider.unbindAll()
                        boundCamera = provider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (e: Exception) {
                        hasCameraBindingError = true
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            update = { previewView ->
                cameraProvider?.let { provider ->
                    try {
                        val cameraSelector = if (isFrontCamera) {
                            if (provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                        } else {
                            if (provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                        }

                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        provider.unbindAll()
                        boundCamera = provider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (e: Exception) {
                        hasCameraBindingError = true
                    }
                }
            },
            // මෙහිදී Android සංස්කරණය 31 (Android 12) හෝ ඊට වැඩි නම් පමණක් Blur දෙන ලෙස ආරක්ෂිතව සකසා ඇත
            modifier = modifier.then(
                if (isBeautyFilterOn && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    Modifier.blur(0.5.dp)
                } else {
                    Modifier
                }
            )
        )
    } else {
        // Fallback / Permission Request Box with live visuals
        Box(
            modifier = modifier.background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = if (isFrontCamera)
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=1200&auto=format&fit=crop&q=80"
                else
                    "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1200&auto=format&fit=crop&q=80",
                contentDescription = "Host Live Camera Fallback",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isBeautyFilterOn && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            Modifier.blur(0.5.dp)
                        } else {
                            Modifier
                        }
                    )
            )

            if (!hasCameraPermission) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFFFF0050), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Permission",
                        tint = Color(0xFFFF0050),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "සජීවී කැමරාව ක්‍රියාත්මක කරන්න\n(Enable Live Camera Access 📸)",
                        color = Color.White,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRequestCameraPermission,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0050)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Allow Camera Access 📹", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
