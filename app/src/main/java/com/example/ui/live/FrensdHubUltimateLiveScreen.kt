package com.example.ui.live

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val DeepSlate = Color(0xFF0F172A)
val SlateCard = Color(0xFF1E293B)
val EmeraldGreen = Color(0xFF10B981)
val ElectricCyan = Color(0xFF06B6D4)
val LiveRed = Color(0xFFEE2B47)

data class LiveSeat(
    val id: Int,
    val name: String = "",
    val isHost: Boolean = false,
    val isOccupied: Boolean = false,
    val coins: Int = 0
)

data class ChatMessage(val id: String, val sender: String, val text: String, val isSystem: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendHubUltimateLiveScreen(
    onCloseLive: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isFollowing by remember { mutableStateOf(false) }
    var isCameraOn by remember { mutableStateOf(true) }
    var commentText by remember { mutableStateOf("") }
    
    // Bottom Sheets & Dialogs
    var showGiftSheet by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showViewersSheet by remember { mutableStateOf(false) }
    var showGuestModal by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    
    // Rose Explosion Animation States
    var triggerRoseAnim by remember { mutableStateOf(false) }
    val roseScale by animateFloatAsState(
        targetValue = if (triggerRoseAnim) 3.5f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "RoseScale"
    )

    val seats = remember {
        mutableStateListOf(
            LiveSeat(1, "Nethu", isHost = true, isOccupied = true, coins = 1400),
            LiveSeat(2, "Jani", isOccupied = true, coins = 0),
            LiveSeat(3, "Dulaa 🇱🇰", isOccupied = true, coins = 2),
            LiveSeat(4, "Sahil 🖤", isOccupied = true, coins = 0),
            LiveSeat(5, "Oshen 👽", isOccupied = true, coins = 0),
            LiveSeat(6, isOccupied = false),
            LiveSeat(7, isOccupied = false),
            LiveSeat(8, isOccupied = false),
            LiveSeat(9, isOccupied = false)
        )
    }

    val comments = remember {
        mutableStateListOf(
            ChatMessage("1", "", "Viewers must be 18 or older to send gifts.", isSystem = true),
            ChatMessage("2", "Sam", "Gifts coming! 🎁"),
            ChatMessage("3", "Casey", "FriendHub is best! 🔥"),
            ChatMessage("4", "Kamal", "joined the LIVE 🖐️", isSystem = true)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ================= 1. TOP HEADER (WITH LIVE EYE VIEWER COUNT) =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Host Card
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCard.copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, LiveRed, CircleShape)
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("❤️Nethu❤️", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("❤️ 1.4K", color = Color.LightGray, fontSize = 9.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isFollowing) SlateCard else LiveRed,
                        modifier = Modifier.clickable { isFollowing = !isFollowing }
                    ) {
                        Text(
                            text = if (isFollowing) "Following" else "+ Follow",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Eye Viewer Count Badge & Close Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // LIVE EYE VIEWER ICON
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SlateCard.copy(alpha = 0.85f),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clickable { showViewersSheet = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.RemoveRedEye, contentDescription = "Viewers", tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("1.4K", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onCloseLive,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SlateCard.copy(alpha = 0.85f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // ================= 2. 3x3 MULTI-GUEST SEATS GRID =================
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                items(seats) { seat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        if (seat.isOccupied) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (seat.isHost) EmeraldGreen else ElectricCyan, CircleShape)
                                    .background(SlateCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(34.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(seat.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(if (seat.isHost) "Host" else "🪙 ${seat.coins}", color = if (seat.isHost) EmeraldGreen else ElectricCyan, fontSize = 9.sp)
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(SlateCard.copy(alpha = 0.6f))
                                    .clickable { showGuestModal = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Join", tint = Color.Gray, modifier = Modifier.size(26.dp))
                            }
                        }
                    }
                }
            }

            // ================= 3. COMMENTS & ACTION BAR =================
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(110.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    items(comments) { item ->
                        Text(
                            text = if (item.isSystem) item.text else "${item.sender}: ${item.text}",
                            color = if (item.isSystem) EmeraldGreen else Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Type...", color = Color.Gray, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(21.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SlateCard,
                            unfocusedContainerColor = SlateCard,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White
                        ),
                        trailingIcon = {
                            IconButton(onClick = { showEmojiPicker = true }) {
                                Icon(Icons.Default.SentimentSatisfied, contentDescription = "Emoji", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = { isCameraOn = !isCameraOn }, modifier = Modifier.size(38.dp).clip(CircleShape).background(if (isCameraOn) EmeraldGreen else LiveRed)) {
                        Icon(if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = { showGuestModal = true }, modifier = Modifier.size(38.dp).clip(CircleShape).background(SlateCard)) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = { showGiftSheet = true }, modifier = Modifier.size(38.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(LiveRed, Color(0xFFFF4081))))) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // WORKING SHARE BUTTON
                    IconButton(onClick = { showShareSheet = true }, modifier = Modifier.size(38.dp).clip(CircleShape).background(SlateCard)) {
                        com.example.ui.components.Custom3DShareIcon(size = 28.dp)
                    }
                }
            }
        }

        // ================= 4. EXPLODING ROSE ANIMATION OVERLAY =================
        if (roseScale > 0.1f) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("🌹", fontSize = 48.sp, modifier = Modifier.scale(roseScale))
                if (triggerRoseAnim) {
                    Text("🌸", fontSize = 24.sp, modifier = Modifier.offset(x = (-80).dp, y = (-80).dp))
                    Text("🌸", fontSize = 24.sp, modifier = Modifier.offset(x = 80.dp, y = (-80).dp))
                    Text("🌸", fontSize = 24.sp, modifier = Modifier.offset(x = (-80).dp, y = 80.dp))
                    Text("🌸", fontSize = 24.sp, modifier = Modifier.offset(x = 80.dp, y = 80.dp))
                }
            }
        }

        // ================= 5. SHARE, POST & DOWNLOAD BOTTOM SHEET =================
        val context = androidx.compose.ui.platform.LocalContext.current
        if (showShareSheet) {
            com.example.ui.components.live.LiveShareSheet(
                onDismiss = { showShareSheet = false },
                onShowNotification = { msg ->
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }

        // ================= 6. GIFT SHEET WITH WORKING ANIMATION =================
        if (showGiftSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGiftSheet = false },
                containerColor = SlateCard
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Send Gift & Trigger Animation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showGiftSheet = false
                            coroutineScope.launch {
                                triggerRoseAnim = true
                                delay(1800)
                                triggerRoseAnim = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🌹 Send Rose (Explode Petals)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
