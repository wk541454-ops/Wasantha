package com.example.ui.components.call

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.model.CallLogItem
import com.example.model.Message
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.random.Random

// Floating reaction particle representation
data class CallReactionParticle(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val initialXOffset: Float, // 0.0 to 1.0 fraction
    val scaleFactor: Float = 1.0f
)

@Composable
fun EnhancedActiveCallDialog(
    call: CallLogItem,
    onEndCall: () -> Unit,
    messages: List<Message> = emptyList(),
    onSendMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // Call state variables
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var isSharingScreen by remember { mutableStateOf(false) }
    var sharedScreenTab by remember { mutableIntStateOf(0) } // 0: Home Apps, 1: Photos, 2: Presentation
    var isCameraFront by remember { mutableStateOf(true) }
    var isBeautyModeOn by remember { mutableStateOf(false) }
    var showChatOverlay by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    var showReactionsTray by remember { mutableStateOf(false) }
    var heartComboCount by remember { mutableIntStateOf(0) }
    var isShutterFlashing by remember { mutableStateOf(false) }
    var latestIncomingToast by remember { mutableStateOf<String?>(null) }

    // Floating reaction particles
    val floatingParticles = remember { mutableStateListOf<CallReactionParticle>() }

    // Local in-call messages list
    val localCallMessages = remember {
        mutableStateListOf<Message>().apply {
            addAll(messages)
            if (isEmpty()) {
                add(
                    Message(
                        senderName = call.peerName,
                        content = "Hey! Video Call connects smoothly! 📹✨",
                        isMe = false,
                        timestamp = "Just now"
                    )
                )
            }
        }
    }

    // Call duration timer effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    // Recording timer effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingSeconds++
            }
        }
    }

    // Heart combo auto-reset effect
    LaunchedEffect(heartComboCount) {
        if (heartComboCount > 0) {
            delay(2200)
            heartComboCount = 0
        }
    }

    // Simulated peer messages when call is active
    LaunchedEffect(Unit) {
        delay(12000)
        val incomingMsg = "HD Audio & Video is super clear! 👍"
        localCallMessages.add(
            Message(
                senderName = call.peerName,
                content = incomingMsg,
                isMe = false,
                timestamp = "Now"
            )
        )
        if (!showChatOverlay) {
            latestIncomingToast = "${call.peerName}: $incomingMsg"
            delay(4000)
            latestIncomingToast = null
        }
    }

    // Draggable self-camera PiP offset
    var pipOffsetX by remember { mutableFloatStateOf(0f) }
    var pipOffsetY by remember { mutableFloatStateOf(0f) }

    val formattedCallDuration = remember(callSeconds) {
        val mins = (callSeconds / 60).toString().padStart(2, '0')
        val secs = (callSeconds % 60).toString().padStart(2, '0')
        "$mins:$secs"
    }

    val formattedRecordDuration = remember(recordingSeconds) {
        val mins = (recordingSeconds / 60).toString().padStart(2, '0')
        val secs = (recordingSeconds % 60).toString().padStart(2, '0')
        "$mins:$secs"
    }

    // Pulsing recording animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val recordingPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recAlpha"
    )

    Dialog(
        onDismissRequest = onEndCall,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // ================= 1. MAIN BACKGROUND: PEER VIDEO OR SCREEN SHARE =================
            if (isSharingScreen) {
                // Interactive Screen Share Simulation Canvas
                ScreenShareCanvas(
                    selectedTab = sharedScreenTab,
                    onSelectTab = { sharedScreenTab = it },
                    peerName = call.peerName,
                    onStopShare = {
                        isSharingScreen = false
                        Toast.makeText(context, "Screen Sharing ended 📱", Toast.LENGTH_SHORT).show()
                    }
                )
            } else if (!isVideoOff) {
                // Peer Video Feed
                AsyncImage(
                    model = call.peerAvatar,
                    contentDescription = call.peerName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle dark vignette gradient overlays for high text contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.65f),
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
            } else {
                // Video Off Mode: High-End Avatar with Glowing Audio Rings
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(OledSurfaceVariant, Color.Black)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.Center) {
                            // Pulsing Audio Ring
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(NeonBlue.copy(alpha = 0.2f))
                            )
                            AsyncImage(
                                model = call.peerAvatar,
                                contentDescription = call.peerName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, NeonBlue, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = call.peerName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Camera is turned off • Audio Connected 🎙️",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // ================= 2. RECORDING RED BORDER VIGNETTE =================
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 3.dp,
                            color = HeartRed.copy(alpha = recordingPulseAlpha)
                        )
                )
            }

            // ================= 3. FLOATING HEARTS & REACTION PARTICLES =================
            CallFloatingParticlesOverlay(particles = floatingParticles)

            // ================= 4. SELF CAMERA PIP WINDOW =================
            if (!isSharingScreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 90.dp, end = 16.dp)
                        .offset { IntOffset(pipOffsetX.roundToInt(), pipOffsetY.roundToInt()) }
                        .size(110.dp, 160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(listOf(NeonBlue, NeonPurple)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                pipOffsetX = (pipOffsetX + dragAmount.x).coerceIn(-600f, 0f)
                                pipOffsetY = (pipOffsetY + dragAmount.y).coerceIn(-100f, 600f)
                            }
                        }
                ) {
                    // Front / Back camera simulation
                    val selfImg = if (isCameraFront) {
                        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80"
                    } else {
                        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=500&auto=format&fit=crop&q=80"
                    }

                    AsyncImage(
                        model = selfImg,
                        contentDescription = "My Camera",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Camera Label Pill
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCameraFront) "Front" else "Back 0.5x",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (isBeautyModeOn) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("✨", fontSize = 8.sp)
                        }
                    }

                    // Flip mini button
                    IconButton(
                        onClick = {
                            isCameraFront = !isCameraFront
                            Toast.makeText(
                                context,
                                if (isCameraFront) "Front Camera 🤳" else "Back Ultra-Wide Camera 📷",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwitchCamera,
                            contentDescription = "Flip",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            } else {
                // When sharing screen, Peer's camera appears in a floating round bubble
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 90.dp, end = 16.dp)
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, OnlineGreen, CircleShape)
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = call.peerAvatar,
                        contentDescription = call.peerName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = call.peerName.split(" ").firstOrNull() ?: "Peer",
                        color = Color.White,
                        fontSize = 9.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 4.dp)
                    )
                }
            }

            // ================= 5. TOP STATUS & CONTROLS HEADER =================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // FaceTime Video Call Icon + Title + Timer
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // User's uploaded custom FaceTime Video Call Icon!
                        Image(
                            painter = painterResource(id = R.drawable.ic_facetime_video_call),
                            contentDescription = "FaceTime Video Call",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = call.peerName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = OnlineGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formattedCallDuration,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = OnlineGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("•", color = Color.Gray, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "1080p 60fps HD",
                                    color = NeonBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Top Action Icons: Shutter Photo Capture + Minimize
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Photo Snapshot Shutter Button
                        IconButton(
                            onClick = {
                                isShutterFlashing = true
                                Toast.makeText(context, "📸 Call Photo saved to Gallery!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Capture Snapshot",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Minimize to PiP
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Minimized to Floating Call Window 🪟", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloseFullscreen,
                                contentDescription = "Minimize",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Recording Active Badge Pill
                if (isRecording) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(HeartRed.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "REC  $formattedRecordDuration  •  HD Audio & Video",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ================= 6. FLOATING INCOMING CHAT NOTIFICATION TOAST =================
            latestIncomingToast?.let { toastText ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 90.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { showChatOverlay = true }
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = toastText, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // ================= 7. HEART BURST COMBO BADGE =================
            if (heartComboCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp)
                        .background(
                            Brush.horizontalGradient(listOf(HeartRed, Color(0xFFFF2A6D))),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "❤️ x$heartComboCount",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }

            // ================= 8. IN-CALL CHAT OVERLAY DRAWER =================
            AnimatedVisibility(
                visible = showChatOverlay,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 180.dp)
                    .width(310.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.88f)),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ChatBubble, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("In-Call Live Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            IconButton(onClick = { showChatOverlay = false }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Message History List
                        LazyColumn(
                            modifier = Modifier
                                .height(160.dp)
                                .fillMaxWidth(),
                            reverseLayout = true
                        ) {
                            items(localCallMessages.reversed()) { msg ->
                                val isMe = msg.isMe
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isMe) NeonBlue else Color.White.copy(alpha = 0.15f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${if (isMe) "You" else msg.senderName}: ${msg.content}",
                                            color = if (isMe) Color.Black else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Quick Presets
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("I hear you! 👍", "Wait ✋", "Awesome 🔥").forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable {
                                            localCallMessages.add(
                                                Message(senderName = "You", content = preset, isMe = true, timestamp = "Now")
                                            )
                                            onSendMessage(preset)
                                        }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(preset, color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }

                        // Message Input Field
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Type message...", fontSize = 11.sp, color = Color.Gray) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonBlue,
                                    unfocusedBorderColor = Color.DarkGray
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        localCallMessages.add(
                                            Message(
                                                senderName = "You",
                                                content = chatInput,
                                                isMe = true,
                                                timestamp = "Now"
                                            )
                                        )
                                        onSendMessage(chatInput)
                                        chatInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NeonBlue)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // ================= 9. EXPANDABLE EMOJI REACTIONS TRAY =================
            AnimatedVisibility(
                visible = showReactionsTray,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 170.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("❤️", "🔥", "😂", "👏", "🎉", "😮", "💯", "👍").forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        // Spawn floating particles
                                        repeat(5) {
                                            floatingParticles.add(
                                                CallReactionParticle(
                                                    emoji = emoji,
                                                    initialXOffset = Random.nextFloat(),
                                                    scaleFactor = 0.8f + Random.nextFloat() * 0.6f
                                                )
                                            )
                                        }
                                        if (emoji == "❤️") heartComboCount++
                                        showReactionsTray = false
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            // ================= 10. BOTTOM CONTROL PANELS =================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                        )
                    )
                    .padding(bottom = 28.dp, top = 8.dp)
            ) {
                // Feature Row: Speaker, Record, Share Screen, Chat, Hearts / Reactions, Beauty
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Speaker Toggle (Speakerphone vs Earpiece)
                    CallFeatureItem(
                        icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.Hearing,
                        label = if (isSpeakerOn) "Speaker" else "Earpiece",
                        isActive = isSpeakerOn,
                        activeColor = OnlineGreen,
                        onClick = {
                            isSpeakerOn = !isSpeakerOn
                            Toast.makeText(
                                context,
                                if (isSpeakerOn) "🔊 Speakerphone On (HD Audio)" else "🔈 Earpiece Mode Active",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    // 2. Video Record Toggle
                    CallFeatureItem(
                        icon = if (isRecording) Icons.Default.RadioButtonChecked else Icons.Default.FiberManualRecord,
                        label = if (isRecording) "Recording" else "Record",
                        isActive = isRecording,
                        activeColor = HeartRed,
                        onClick = {
                            isRecording = !isRecording
                            if (isRecording) {
                                Toast.makeText(context, "🔴 Call recording started (1080p HD)", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "📹 Recording saved to Gallery! (38 MB)", Toast.LENGTH_LONG).show()
                            }
                        }
                    )

                    // 3. Share Screen Toggle
                    CallFeatureItem(
                        icon = if (isSharingScreen) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                        label = if (isSharingScreen) "Sharing" else "Share Screen",
                        isActive = isSharingScreen,
                        activeColor = NeonBlue,
                        onClick = {
                            isSharingScreen = !isSharingScreen
                            Toast.makeText(
                                context,
                                if (isSharingScreen) "📱 Screen Sharing Started" else "Screen Sharing Stopped",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    // 4. In-Call Chat Overlay
                    CallFeatureItem(
                        icon = Icons.Default.ChatBubble,
                        label = "Chat",
                        isActive = showChatOverlay,
                        activeColor = NeonPurple,
                        onClick = { showChatOverlay = !showChatOverlay }
                    )

                    // 5. Studio Beauty / Background Blur
                    CallFeatureItem(
                        icon = Icons.Default.AutoAwesome,
                        label = "Filter",
                        isActive = isBeautyModeOn,
                        activeColor = Color(0xFFFFD700),
                        onClick = {
                            isBeautyModeOn = !isBeautyModeOn
                            Toast.makeText(
                                context,
                                if (isBeautyModeOn) "✨ Studio Portrait Blur Activated" else "Normal Camera Mode",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    // 6. Direct Heart ❤️ / Reactions Tray Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                heartComboCount++
                                repeat(6) {
                                    floatingParticles.add(
                                        CallReactionParticle(
                                            emoji = "❤️",
                                            initialXOffset = 0.6f + (Random.nextFloat() * 0.35f),
                                            scaleFactor = 0.9f + Random.nextFloat() * 0.7f
                                        )
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(HeartRed, Color(0xFFFF2A6D)))
                                )
                        ) {
                            Text("❤️", fontSize = 20.sp)
                        }
                        Text(
                            text = "React",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { showReactionsTray = !showReactionsTray }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Call Actions: Mic, End Call, Video Camera, Flip Camera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute / Unmute Mic
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            Toast.makeText(
                                context,
                                if (isMuted) "Microphone Muted 🔇" else "Microphone Unmuted 🎙️",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) HeartRed else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // End Call (Large Red Capsule)
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(HeartRed)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Video Camera On / Off
                    IconButton(
                        onClick = {
                            isVideoOff = !isVideoOff
                            Toast.makeText(
                                context,
                                if (isVideoOff) "Camera Stopped 📷❌" else "Camera Started 📹",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isVideoOff) HeartRed else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Flip Camera (Front / Back)
                    IconButton(
                        onClick = {
                            isCameraFront = !isCameraFront
                            Toast.makeText(
                                context,
                                if (isCameraFront) "Front Camera 🤳" else "Back Ultra-Wide Camera 📷",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwitchCamera,
                            contentDescription = "Flip Camera",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // ================= 11. CAMERA SHUTTER FLASH EFFECT =================
            if (isShutterFlashing) {
                LaunchedEffect(Unit) {
                    delay(150)
                    isShutterFlashing = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }
        }
    }
}

// Small Feature Button in Call Toolbar
@Composable
private fun CallFeatureItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else Color.White.copy(alpha = 0.15f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            color = if (isActive) activeColor else Color.White,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// Simulated Interactive Screen Sharing View
@Composable
private fun ScreenShareCanvas(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    peerName: String,
    onStopShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(top = 70.dp, bottom = 140.dp, start = 16.dp, end = 16.dp)
    ) {
        // Top Screen Sharing Indicator Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OnlineGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .border(1.dp, OnlineGreen, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ScreenShare, contentDescription = null, tint = OnlineGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sharing Screen with $peerName",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onStopShare,
                colors = ButtonDefaults.buttonColors(containerColor = HeartRed),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text("Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Screen Share Content Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("📱 App Feed", "🖼️ Photo Gallery", "📊 Presentation").forEachIndexed { idx, title ->
                val isSelected = selectedTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonBlue else Color.White.copy(alpha = 0.1f))
                        .clickable { onSelectTab(idx) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simulated Shared Screen Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    Column {
                        Text("FriendHub Social Feed Live Preview", color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("David Chen: Check out this new project design! 🚀", color = Color.White, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("❤️ 42 likes • 8 comments", color = Color.LightGray, fontSize = 10.sp)
                            }
                        }
                    }
                }
                1 -> {
                    Column {
                        Text("Shared Photo Collection", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=500&auto=format&fit=crop&q=80",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1519741497674-611481863552?w=500&auto=format&fit=crop&q=80",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
                    }
                }
                else -> {
                    Column {
                        Text("📊 Q3 Growth Presentation Slides", color = OnlineGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A5F)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("• Active Video Calls: +142% 📈", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Screen Share Sessions: 98.4% uptime", color = Color.LightGray, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Low latency HD stream pipeline", color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Particle Floating Engine for Hearts & Emojis
@Composable
private fun CallFloatingParticlesOverlay(particles: List<CallReactionParticle>) {
    Box(modifier = Modifier.fillMaxSize()) {
        particles.takeLast(16).forEach { particle ->
            key(particle.id) {
                var animProgress by remember { mutableFloatStateOf(0f) }

                LaunchedEffect(Unit) {
                    val anim = Animatable(0f)
                    anim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2400, easing = LinearOutSlowInEasing)
                    ) {
                        animProgress = this.value
                    }
                }

                if (animProgress < 1f) {
                    val yOffset = (1f - animProgress) * 700f // Floats upwards
                    val xWobble = kotlin.math.sin(animProgress * 8f) * 30f
                    val alpha = (1f - animProgress).coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = (particle.initialXOffset * 280).dp)
                            .offset(x = xWobble.dp, y = (-yOffset).dp)
                            .scale(particle.scaleFactor * (0.8f + (animProgress * 0.4f)))
                    ) {
                        Text(
                            text = particle.emoji,
                            fontSize = 28.sp,
                            modifier = Modifier.background(Color.Transparent),
                            color = Color.White.copy(alpha = alpha)
                        )
                    }
                }
            }
        }
    }
}
