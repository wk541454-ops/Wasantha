package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.animation.FloatingHeartsOverlay
import com.example.animation.rememberFloatingHeartsState
import com.example.ui.components.live.*
import com.example.model.*
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Emerald Dark Luxury Theme Palette
private val SlateDark = Color(0xFF0F172A)
private val SlateDarkest = Color(0xFF090D16)
private val SlateCard = Color(0xFF1E293B)
private val SlateBorder = Color(0xFF334155)
private val EmeraldGreen = Color(0xFF10B981)
private val ElectricCyan = Color(0xFF06B6D4)
private val LiveRed = Color(0xFFEF4444)
private val GoldYellow = Color(0xFFFFB74D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamingScreen(
    viewModel: MainViewModel,
    onCloseLive: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val floatingHeartsState = rememberFloatingHeartsState()

    val activeSession by viewModel.activeLiveSession.collectAsState()
    val liveMessages by viewModel.liveMessagesList.collectAsState()
    
    val isLiveStarted = activeSession != null && activeSession?.status == "LIVE"

    // Pre-Live Setup States
    var streamTitle by remember { mutableStateOf("Friday Night Vibes & Chill 🎵") }
    var selectedLiveMode by remember { mutableStateOf("Host + Multi-Guest") }
    var streamGoal by remember { mutableStateOf("500 Roses Goal 🌹") }
    var selectedPrivacy by remember { mutableStateOf("Public") }
    var showBeautyFilters by remember { mutableStateOf(false) }
    var isMicOn by remember { mutableStateOf(true) }
    var isCameraOn by remember { mutableStateOf(true) }

    // Active Live Stream States
    var isFollowing by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var selectedSeatForJoin by remember { mutableStateOf<Int?>(null) }
    var showViewersSheet by remember { mutableStateOf(false) }
    var isCoHostDockExpanded by remember { mutableStateOf(true) }

    val userCoins by viewModel.userCoins.collectAsState()
    val showGiftSheet by viewModel.showGiftSheet.collectAsState()
    val showCoinRechargeModal by viewModel.showCoinRechargeModal.collectAsState()
    val showShareSheet by viewModel.showShareSheet.collectAsState()

    // Gift Animation Engine States
    var activeGift by remember { mutableStateOf<SendableGift?>(null) }
    var showGuestInviteDialog by remember { mutableStateOf(false) }
    var showGuidelinesDialog by remember { mutableStateOf(false) }
    var showMusicKaraokeSheet by remember { mutableStateOf(false) }

    // Dynamic Co-Host & Guest Seats Data
    val seats = activeSession?.seats ?: emptyList()

    // Dynamic Comments List
    val comments = liveMessages

    val chatListState = rememberLazyListState()

    // Auto-scroll chat on new comments safely
    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            try {
                chatListState.scrollToItem((comments.size - 1).coerceAtLeast(0))
            } catch (e: Exception) {
                // Prevent layout lockup
            }
        }
    }

    // Continuous floating hearts rising smoothly from the right side above Share button
    LaunchedEffect(isLiveStarted) {
        if (isLiveStarted) {
            while (true) {
                kotlinx.coroutines.delay(800L + kotlin.random.Random.nextLong(600L))
                floatingHeartsState.emitRightSideHearts(count = kotlin.random.Random.nextInt(1, 3))
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SlateDarkest,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateDarkest)
        ) {
            if (!isLiveStarted) {
                PreLiveSetupContent(
                    streamTitle = streamTitle,
                    onTitleChange = { streamTitle = it },
                    selectedLiveMode = selectedLiveMode,
                    onModeChange = { selectedLiveMode = it },
                    streamGoal = streamGoal,
                    selectedPrivacy = selectedPrivacy,
                    onStartLive = { 
                        viewModel.setLiveStreamTitle(streamTitle)
                        viewModel.startLiveStream()
                    },
                    onCloseLive = {
                        viewModel.endLiveStream()
                        viewModel.setLiveStreamingOpen(false)
                        onCloseLive()
                    },
                    onCameraFlip = { isCameraOn = !isCameraOn },
                    onMicToggle = { isMicOn = !isMicOn },
                    onBeautyFilters = { showBeautyFilters = true },
                    isMicOn = isMicOn
                )
            } else {
                // ==================== HYBRID LIVE STREAM INTERFACE ====================
                Box(modifier = Modifier.fillMaxSize()) {

                    // 1. FULL-SCREEN BACKGROUND / HOST VIDEO CAMERA STREAM
                    HostStreamBackgroundLayer(
                        hostAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800"
                    )

                    // 2. TOP HEADER OVERLAY (Fixed at top, status bar padded)
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .zIndex(10f)
                    ) {
                        TopLiveHeaderBar(
                            isFollowing = isFollowing,
                            onFollowToggle = { isFollowing = !isFollowing },
                            onViewersClick = { showViewersSheet = true },
                            onShowGuidelines = { showGuidelinesDialog = true },
                            onPipClick = {
                                viewModel.setLivePipActive(true)
                                viewModel.setLiveStreamingOpen(false)
                            },
                            onCloseClick = {
                                viewModel.endLiveStream()
                                viewModel.setLiveStreamingOpen(false)
                                onCloseLive()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )

                        // Live Backing Track / MP3 Banner (Disabled)
                    }

                    // 3. PICTURE-IN-PICTURE (PiP) MULTI-GUEST GRID DOCK (Neatly aligned on right side)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp, top = 60.dp, bottom = 220.dp)
                            .zIndex(8f)
                    ) {
                        CoHostPiPGuestDock(
                            seats = seats,
                            isExpanded = isCoHostDockExpanded,
                            onToggleExpand = { isCoHostDockExpanded = !isCoHostDockExpanded },
                            onSeatClick = { seatId ->
                                selectedSeatForJoin = seatId
                            }
                        )
                    }

                    // 4. BOTTOM ANCHORED SECTION (Chat Box + Action Bar - Completely Unobstructed)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(start = 12.dp, end = 12.dp, bottom = 16.dp, top = 8.dp)
                            .zIndex(9f)
                    ) {
                        // Live Chat Stream Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.78f)
                                .height(170.dp)
                        ) {
                            LazyColumn(
                                state = chatListState,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(comments, key = { it.id }) { item ->
                                    ChatBubbleItem(item)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom Action Controls Bar (TikTok Style)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // "Add a comment..." Comment Input Field
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(21.dp))
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                        if (commentText.isEmpty()) {
                                            Text(
                                                "Add a comment...",
                                                color = Color.White.copy(alpha = 0.75f),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                        BasicTextField(
                                            value = commentText,
                                            onValueChange = { commentText = it },
                                            singleLine = true,
                                            maxLines = 1,
                                            textStyle = TextStyle(
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            cursorBrush = SolidColor(Color(0xFFFE2C55)),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    if (commentText.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFE2C55))
                                                .clickable {
                                                    viewModel.sendLiveChatMessage(commentText)
                                                    floatingHeartsState.emitRightSideHearts(count = 5)
                                                    commentText = ""
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Send,
                                                contentDescription = "Send",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Multi-guest seats button (👥)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .clickable { showGuestInviteDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👥", fontSize = 18.sp)
                            }

                            // Coin / Discount icon button (🪙 %)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .clickable { viewModel.setShowCoinRechargeModal(true) },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙", fontSize = 13.sp)
                                    Text("%", color = Color(0xFFFFD54F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Gift Button (🎁 Pink Gift Box with pulse)
                            val infiniteTransition = rememberInfiniteTransition(label = "giftGlow")
                            val giftPulse by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.08f,
                                animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                                label = "pulse"
                            )

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .graphicsLayer {
                                        scaleX = giftPulse
                                        scaleY = giftPulse
                                    }
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFFFE2C55), Color(0xFFFF5252)))
                                    )
                                    .clickable { viewModel.setShowGiftSheet(true) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎁", fontSize = 20.sp)
                            }

                            // Share Button with Count (↗️ 60)
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .clickable {
                                        floatingHeartsState.emitHearts(6)
                                        viewModel.setShowShareSheet(true)
                                    }
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("↗️", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("60", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Floating Hearts Layer (Rises smoothly from right side above Share icon)
                    FloatingHeartsOverlay(
                        state = floatingHeartsState,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 65.dp, end = 16.dp)
                            .zIndex(15f)
                    )
                }
            }

            // ==================== 5. FULL-SCREEN GIFT OVERLAY ENGINE ====================
            if (isLiveStarted && activeGift != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(25f)
                ) {
                    GiftAnimationOverlay(
                        gift = activeGift,
                        onAnimationEnd = { activeGift = null }
                    )
                }
            }

            // ==================== 6. MODALS & BOTTOM SHEETS ====================
            // Viewers Bottom Sheet
            if (showViewersSheet) {
                LiveViewersBottomSheet(
                    onDismiss = { showViewersSheet = false },
                    onViewerFollowToggle = { viewerId ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Follow status updated for viewer!")
                        }
                    }
                )
            }

            // Share Sheet with Download Stream Progress
            if (showShareSheet) {
                LiveShareSheet(
                    onDismiss = { viewModel.setShowShareSheet(false) },
                    onShowNotification = { msg ->
                        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                    },
                    viewModel = viewModel
                )
            }

            // Gift Selection Sheet
            if (showGiftSheet) {
                LiveGiftSheet(
                    userCoins = userCoins,
                    onDismiss = { viewModel.setShowGiftSheet(false) },
                    onOpenRecharge = {
                        viewModel.setShowGiftSheet(false)
                        viewModel.setShowCoinRechargeModal(true)
                    },
                    onSendGift = { gift ->
                        if (viewModel.spendCoins(gift.cost)) {
                            activeGift = gift
                            viewModel.sendLiveChatMessage("Sent ${gift.name} ${gift.emoji}! 🎁")
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Sent ${gift.name} ${gift.emoji}!")
                            }
                        }
                    }
                )
            }

            // Coin Wallet Recharge Sheet (LKR)
            if (showCoinRechargeModal) {
                CoinWalletRechargeSheet(
                    userCoins = userCoins,
                    onDismiss = { viewModel.setShowCoinRechargeModal(false) },
                    onPurchaseSuccess = { addedCoins ->
                        viewModel.addCoins(addedCoins)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Successfully added $addedCoins coins to wallet! 🪙")
                        }
                    }
                )
            }

            if (showGuestInviteDialog) {
                TikTokGuestInviteDialog(
                    hostName = "😈නොටි පැංචා😈",
                    hostAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                    onAccept = {
                        showGuestInviteDialog = false
                        val emptySeat = seats.find { !it.isOccupied }
                        if (emptySeat != null) {
                            viewModel.joinLiveSeat(emptySeat.id, micOn = true)
                        }
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("You joined LIVE as a guest! 🎙️")
                        }
                    },
                    onDecline = {
                        showGuestInviteDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Guest invitation declined.")
                        }
                    }
                )
            }

            // Guest Join Seat Sheet
            selectedSeatForJoin?.let { seatNumber ->
                GuestJoinSheet(
                    seatNumber = seatNumber,
                    onDismiss = { selectedSeatForJoin = null },
                    onJoinConfirmed = { micOn, camOn ->
                        viewModel.joinLiveSeat(seatNumber, micOn = micOn, camOn = camOn)
                        selectedSeatForJoin = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("You took Seat #$seatNumber! 🎙️")
                        }
                    }
                )
            }

            // Guidelines Modal Dialog
            if (showGuidelinesDialog) {
                AlertDialog(
                    onDismissRequest = { showGuidelinesDialog = false },
                    containerColor = Color(0xFF1E293B),
                    icon = { Text("🛡️", fontSize = 28.sp) },
                    title = {
                        Text(
                            text = "FriendHub Live Guidelines 🛡️",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "• Respect all hosts, co-hosts, and viewers in live streams.\n\n• No hate speech, harassment, or abusive comments in live chat.\n\n• Follow FriendHub safety policies for a clean, enjoyable community experience.",
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showGuidelinesDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2FF)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("තේරුණා (Got it) 👍", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                )
            }

            // Live Music & Online MP3 Karaoke Bottom Sheet
            if (showMusicKaraokeSheet) {
                LiveMusicKaraokeSheet(
                    onDismiss = { showMusicKaraokeSheet = false },
                    onTrackSelectedMessage = { trackMsg ->
                        viewModel.sendLiveChatMessage(trackMsg)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(trackMsg)
                        }
                    }
                )
            }
        }
    }
}

/**
 * High-Fidelity Host Camera Stream Background Layer with ambient studio lighting
 */
@Composable
private fun HostStreamBackgroundLayer(
    hostAvatarUrl: String
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // High fidelity host stream imagery
        AsyncImage(
            model = hostAvatarUrl,
            contentDescription = "Host Camera Stream",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle Vignette Overlay for TikTok Live readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )
    }
}

/**
 * Top Navigation & Host Pill Bar
 */
@Composable
private fun TopLiveHeaderBar(
    isFollowing: Boolean,
    onFollowToggle: () -> Unit,
    onViewersClick: () -> Unit,
    onShowGuidelines: () -> Unit,
    onPipClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TikTok Host Info Pill (Left)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .padding(start = 3.dp, end = 8.dp, top = 3.dp, bottom = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                    contentDescription = "Host Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFFFE2C55), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        "😈නොටි පැංචා😈",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💖 3.5K", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

                // + Join Orange/Red Pill Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFFF5252), Color(0xFFFF7A00)))
                        )
                        .clickable { onFollowToggle() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🧡", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (isFollowing) "Joined" else "+ Join",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Viewers Seat Indicator & Close Button (Right)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Guidelines 🛡️ Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { onShowGuidelines() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛡️", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Real Live Viewers Indicator Pill (👥 1.4K Viewers)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable { onViewersClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("👥", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("1.4K", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Close X Button
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Live",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Secondary Tag Bar: "Popular LIVE" tag & Goal indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "🧡 Popular LIVE" Pill Tag
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🧡", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Popular LIVE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // "🎵 0/1" Goal Tag
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🎵", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "0/1",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Docked Picture-in-Picture (PiP) Guest Grid on the right side
 */
@Composable
private fun CoHostPiPGuestDock(
    seats: List<LiveSeat>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSeatClick: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Toggle pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { onToggleExpand() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Co-Hosts (${seats.count { it.isOccupied }})",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }

        if (isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Display non-host co-host guest seats
                seats.filter { !it.isHost }.take(4).forEach { seat ->
                    GuestSeatPiPItem(
                        seat = seat,
                        onClick = { onSeatClick(seat.id) }
                    )
                }
            }
        }
    }
}

/**
 * Single PiP Guest Video / Thumbnail Card
 */
@Composable
private fun GuestSeatPiPItem(
    seat: LiveSeat,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        if (seat.isOccupied) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = seat.avatarUrl,
                    contentDescription = seat.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (seat.isSpeaking) 2.dp else 1.dp,
                            color = if (seat.isSpeaking) Color(0xFF10B981) else Color(0xFFFE2C55).copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                )

                // Speaking indicator dot
                if (seat.isSpeaking) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(9.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // User Name
            Text(
                text = seat.name,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Coin Badge Pill (e.g. 🪙 0)
            Row(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 6.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🪙", fontSize = 9.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${seat.coins}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Empty TikTok Seat (+)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Join Seat",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Animated Chat Bubble Item with badges
 */
@Composable
private fun ChatBubbleItem(item: com.example.model.LiveChatMessage) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (item.isSystem) Color(0xFF10B981).copy(alpha = 0.2f)
                else Color.Black.copy(alpha = 0.5f)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!item.isSystem) {
                Text(
                    text = "${item.userName}: ",
                    color = if (item.isMe) Color(0xFF10B981) else Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = item.message,
                color = if (item.isSystem) Color(0xFF10B981) else Color.White,
                fontSize = 12.sp,
                fontWeight = if (item.isSystem) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * Animated Audio Equalizer Bar
 */
@Composable
private fun HostAudioLiveMeter() {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 22f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 26f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h4"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(h1, h2, h3, h4, h2, h3).forEach { h ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 1.5.dp)
                        .width(3.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.verticalGradient(listOf(ElectricCyan, EmeraldGreen))
                        )
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Spatial Mic",
                color = Color(0xFFCBD5E1),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Pre-Live Stream Configuration Screen
 */
@Composable
fun PreLiveSetupContent(
    streamTitle: String,
    onTitleChange: (String) -> Unit,
    selectedLiveMode: String,
    onModeChange: (String) -> Unit,
    streamGoal: String,
    selectedPrivacy: String,
    onStartLive: () -> Unit,
    onCloseLive: () -> Unit,
    onCameraFlip: () -> Unit,
    onMicToggle: () -> Unit,
    onBeautyFilters: () -> Unit,
    isMicOn: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCloseLive) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text("Live Studio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onCameraFlip) {
                Icon(Icons.Default.FlipCameraIos, contentDescription = "Flip", tint = Color.White)
            }
        }

        // Camera Preview Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SlateCard)
                .border(2.dp, LiveRed.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                com.example.ui.components.Animated3DLiveIcon(size = 76.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Camera Preview Ready", color = Color.Gray, fontSize = 12.sp)
            }

            // Floating Buttons on Preview
            Row(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onMicToggle,
                    modifier = Modifier.background(SlateCard.copy(alpha = 0.6f), CircleShape).size(36.dp)
                ) {
                    Icon(
                        if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onBeautyFilters,
                    modifier = Modifier.background(SlateCard.copy(alpha = 0.6f), CircleShape).size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Title Input
        OutlinedTextField(
            value = streamTitle,
            onValueChange = onTitleChange,
            placeholder = { Text("Enter live stream title...", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = Color.White
            )
        )

        // Modes
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Host + Multi-Guest", "Solo Stream", "Gaming").forEach { mode ->
                FilterChip(
                    selected = selectedLiveMode == mode,
                    onClick = { onModeChange(mode) },
                    label = { Text(mode, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = SlateCard,
                        labelColor = Color.White
                    )
                )
            }
        }

        // Goal & Privacy
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Text(streamGoal, fontSize = 12.sp, color = Color.White)
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Text("Privacy: $selectedPrivacy", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Go Live Button
        Button(
            onClick = onStartLive,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Go LIVE Now 🔴", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

/**
 * TikTok Guest Invitation Bottom Sheet Modal (Screenshot 1)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TikTokGuestInviteDialog(
    hostName: String,
    hostAvatarUrl: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDecline,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Right Settings Gear Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Overlapping Avatars (Host + User)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = hostAvatarUrl,
                    contentDescription = "Host Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(-16.dp))
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Headline (Screenshot 1)
            Text(
                text = "$hostName ... invites you to join LIVE as a guest",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle (Screenshot 1)
            Text(
                text = "TikTok rewards both you and the creator for your popularity during this LIVE.",
                color = Color.Gray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Buttons: Decline (113s) & Accept (Pink)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Decline Button
                Button(
                    onClick = onDecline,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Text(
                        text = "Decline (113s)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Accept Button
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
                ) {
                    Text(
                        text = "Accept",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
