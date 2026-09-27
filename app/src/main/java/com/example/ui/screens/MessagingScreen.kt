package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import com.example.ui.components.NativeSponsoredChatCard
import com.example.ui.components.sampleSponsoredAds
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.ChatSummary
import com.example.model.Message
import com.example.ui.components.StoryBar
import com.example.ui.components.LiveAvatar
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import com.example.ui.theme.OledBackground
import com.example.ui.theme.OnlineGreen
import com.example.viewmodel.MainViewModel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.HeartRed

// Top 10 Frequently Used Reactions (👍 ❤️ 😆 😮 😢 😡 🔥 🙏 💯)
val TOP_10_EMOJIS = listOf("👍", "❤️", "😆", "😮", "😢", "😡", "🔥", "🎉", "🙏", "💯")

val LOVE_STICKERS = listOf("💖", "😍", "😘", "🌹", "💍", "💌", "🧸", "🍫", "✨", "🥰", "💋", "💕")

data class TikTokGiftItem(
    val id: String,
    val name: String,
    val icon: String,
    val coins: Int,
    val color: Color
)

val TIKTOK_EMOJIS_LIST = listOf(
    Pair("😊", "[smile]"),
    Pair("😃", "[happy]"),
    Pair("😂", "[laugh]"),
    Pair("🥰", "[loveface]"),
    Pair("😭", "[cry]"),
    Pair("😱", "[shock]"),
    Pair("😎", "[cool]"),
    Pair("😌", "[proud]"),
    Pair("😡", "[angry]"),
    Pair("🤤", "[drool]"),
    Pair("🤔", "[thinking]"),
    Pair("🖐️", "[slap]"),
    Pair("🤪", "[wicked]"),
    Pair("😳", "[flushed]"),
    Pair("😈", "[devil]"),
    Pair("😇", "[angel]"),
    Pair("😵", "[stun]"),
    Pair("🥱", "[yawn]"),
    Pair("🥺", "[cute]"),
    Pair("🔥", "[fire]"),
    Pair("🌹", "[rose]"),
    Pair("❤️", "[heart]"),
    Pair("💯", "[100]"),
    Pair("✨", "[sparkles]"),
    Pair("👏", "[clap]"),
    Pair("🥳", "[party]")
)

val TIKTOK_STICKERS_LIST = listOf(
    Pair("💃", "TikTok Dance"),
    Pair("🐱", "Cat Vibes"),
    Pair("🎧", "Viral Beats"),
    Pair("🔥", "Lit & Fire"),
    Pair("✨", "Glow Up"),
    Pair("💀", "Bro Dead"),
    Pair("🚀", "To The Moon"),
    Pair("👑", "Boss Vibes"),
    Pair("💅", "Slay Queen"),
    Pair("🍕", "Foodie Life"),
    Pair("🐶", "Puppy Eyes"),
    Pair("💖", "Hearts Stream"),
    Pair("🤩", "Superstar"),
    Pair("🕺", "Groovy"),
    Pair("💥", "Viral Hit"),
    Pair("☕", "Coffee Chill")
)

val TIKTOK_GIFTS_LIST = listOf(
    TikTokGiftItem("rose", "Rose", "🌹", 1, Color(0xFFFF2C55)),
    TikTokGiftItem("icecream", "Ice Cream", "🍦", 1, Color(0xFF38BDF8)),
    TikTokGiftItem("heart_me", "Heart Me", "💖", 10, Color(0xFFF43F5E)),
    TikTokGiftItem("doughnut", "Doughnut", "🍩", 30, Color(0xFFF59E0B)),
    TikTokGiftItem("crown", "Galaxy Crown", "👑", 1000, Color(0xFFFFD700)),
    TikTokGiftItem("fireworks", "Fireworks", "🎆", 1088, Color(0xFFA855F7)),
    TikTokGiftItem("supercar", "Supercar", "🏎️", 7000, Color(0xFFEF4444)),
    TikTokGiftItem("diamond", "Diamond Ring", "💎", 10000, Color(0xFF06B6D4)),
    TikTokGiftItem("falcon", "Flying Falcon", "🦅", 10999, Color(0xFFD97706)),
    TikTokGiftItem("rocket", "TikTok Rocket", "🚀", 20000, Color(0xFFEC4899)),
    TikTokGiftItem("lion", "Golden Lion", "🦁", 29999, Color(0xFFEAB308)),
    TikTokGiftItem("universe", "TikTok Universe", "🪐", 34999, Color(0xFF8B5CF6))
)

private data class ChatReactionParticle(
    val id: Int,
    val startXFraction: Float,
    val speedMultiplier: Float,
    val delayMs: Int,
    val scale: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val chats by viewModel.chats.collectAsState()
    val mediaAutoSave by viewModel.mediaAutoSave.collectAsState()
    val activeChat by viewModel.activeChat.collectAsState()
    val activeChatMessages by viewModel.activeChatMessages.collectAsState()
    val isPeerTyping by viewModel.isPeerTyping.collectAsState()
    val aiChatAssistantEnabled by viewModel.aiChatAssistantEnabled.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showMessengerSettings by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    androidx.activity.compose.BackHandler(enabled = activeChat != null) {
        viewModel.closeChat()
    }

    if (activeChat != null) {
        ChatDetailView(
            chat = activeChat!!,
            messages = activeChatMessages,
            mediaAutoSave = mediaAutoSave,
            aiChatAssistantEnabled = aiChatAssistantEnabled,
            isPeerTyping = isPeerTyping,
            onBackClick = { viewModel.closeChat() },
            onSendMessage = { viewModel.sendMessage(it) },
            onReactToMessage = { messageId, reaction ->
                viewModel.addMessageReaction(messageId, reaction)
            },
            onStartCall = { type ->
                viewModel.startCall(
                    peerName = activeChat!!.peerName,
                    peerAvatar = activeChat!!.peerAvatar,
                    type = type,
                    isGroup = activeChat!!.isGroup,
                    memberAvatars = activeChat!!.memberAvatars
                )
            },
            onDownloadMedia = { viewModel.downloadMedia(it) },
            onToggleAutoSave = { viewModel.toggleMediaAutoSave() },
            onSimulateIncomingCall = { viewModel.simulateIncomingCall() },
            onConnectOrFollow = {
                viewModel.connectOrFollowInChat(activeChat!!.id, activeChat!!.peerUserId)
            },
            onProfileClick = { user ->
                viewModel.viewUserProfile(user)
            }
        )
        } else {
            val onlineFriends = viewModel.allFriends.collectAsState().value.filter { it.isOnline }
            
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A), // Slate 900
                                Color(0xFF1E293B)  // Slate 800
                            )
                        )
                    )
            ) {
                // Header with Encrypted Badge & Settings Gear
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_3d_chat_bubble_vector),
                            contentDescription = "Messenger",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Chats",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color(0xFFF8FAFC) // Slate 50
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = Color(0xFF10B981), // Emerald 500
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Settings Gear button opening "පණිවිඩ යැවීමේ සැකසුම්"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showCreateGroupDialog = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF334155)) // Slate 700
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = "Create Group",
                                tint = Color(0xFFF8FAFC),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showMessengerSettings = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF334155)) // Slate 700
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Messaging Settings",
                                tint = Color(0xFFF8FAFC),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Search input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search conversations...", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("chat_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981), // Emerald 500
                        unfocusedBorderColor = Color(0xFF334155), // Slate 700
                        focusedTextColor = Color(0xFFF8FAFC),
                        unfocusedTextColor = Color(0xFFF8FAFC),
                        focusedContainerColor = Color(0xFF1E293B), // Slate 800
                        unfocusedContainerColor = Color(0xFF1E293B)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active Now Story Reel
                Text(
                    text = "Active Now",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(onlineFriends) { friend ->
                        val isLive = friend.name == "David Chen" || friend.name == "Akash Ruwan"
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { 
                                if (isLive) {
                                    viewModel.setLiveStreamingOpen(true)
                                    viewModel.startLiveStream()
                                } else {
                                    viewModel.openChatWithUser(friend)
                                }
                            }
                        ) {
                            Box {
                                LiveAvatar(
                                    imageUrl = friend.avatarUrl,
                                    isLive = isLive,
                                    size = 56.dp
                                )
                                
                                if (!isLive) {
                                    // Emerald Green online status dot
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                            .border(2.dp, Color(0xFF0F172A), CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = friend.name.split(" ").first(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = Color(0xFFF8FAFC)
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat items list
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp), // Extra bottom padding for floating nav
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                itemsIndexed(chats, key = { _, chat -> chat.id }) { index, chat ->
                    ChatItemCard(
                        chat = chat,
                        onClick = { viewModel.openChat(chat) },
                        onAvatarClick = {
                            val userId = chat.peerUserId ?: "user_${chat.id}"
                            val user = viewModel.knownUsers.value[userId] ?: com.example.model.User(
                                id = userId,
                                name = chat.peerName,
                                avatarUrl = chat.peerAvatar,
                                isOnline = chat.isOnline
                            )
                            viewModel.viewUserProfile(user)
                        }
                    )

                    // Inject a Native Sponsored Card every 2 chats (subtle & non-intrusive, like Facebook Messenger)
                    if ((index + 1) % 2 == 0) {
                        val adIndex = ((index + 1) / 2 - 1) % sampleSponsoredAds.size
                        Spacer(modifier = Modifier.height(2.dp))
                        NativeSponsoredChatCard(ad = sampleSponsoredAds[adIndex])
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }

        if (showMessengerSettings) {
            MessengerSettingsBottomSheet(
                viewModel = viewModel,
                onDismiss = { showMessengerSettings = false }
            )
        }

        if (showCreateGroupDialog) {
            CreateGroupDialog(
                friends = viewModel.allFriends.collectAsState().value,
                onDismiss = { showCreateGroupDialog = false },
                onCreateGroup = { name, members ->
                    viewModel.createGroup(name, members)
                    showCreateGroupDialog = false
                }
            )
        }
    }
}

@Composable
fun CreateGroupDialog(
    friends: List<com.example.model.User>,
    onDismiss: () -> Unit,
    onCreateGroup: (String, List<com.example.model.User>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    val selectedMembers = remember { mutableStateOf(setOf<com.example.model.User>()) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OledBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, OledCardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Create New Group",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = OledCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Select Members", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(friends) { friend ->
                        val isSelected = selectedMembers.value.contains(friend)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) selectedMembers.value -= friend
                                    else selectedMembers.value += friend
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = friend.name, color = Color.White, modifier = Modifier.weight(1f))
                            androidx.compose.material3.Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (isSelected) selectedMembers.value -= friend
                                    else selectedMembers.value += friend
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (groupName.isNotBlank() && selectedMembers.value.isNotEmpty()) {
                            onCreateGroup(groupName, selectedMembers.value.toList())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Group", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerSettingsBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var activeStatusEnabled by remember { mutableStateOf(currentUser.isOnline) }
    var notificationsEnabled by remember { mutableStateOf(currentUser.pushNotificationsEnabled) }
    var messageRequestsEnabled by remember { mutableStateOf(false) }
    val e2eEncryptionEnabled by viewModel.e2eEncryptionEnabled.collectAsState()

    fun updateProfile() {
        viewModel.updateUserProfile(currentUser.copy(
            isOnline = activeStatusEnabled,
            pushNotificationsEnabled = notificationsEnabled
        ))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = OledSurface,
        scrimColor = Color.Black.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header matching Sinhala user screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = NeonBlue,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "පණිවිඩ යැවීමේ සැකසුම්",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. සක්‍රීය තත්වය (Active Status)
            SettingToggleRow(
                icon = Icons.Default.People,
                title = "සක්‍රීය තත්වය (Active Status)",
                subtitle = "ඔබ ඔන්ලයින් සිටින විට මිතුරන්ට පෙන්වන්න",
                checked = activeStatusEnabled,
                onCheckedChange = {
                    activeStatusEnabled = it
                    updateProfile()
                    val msg = if (it) "සක්‍රීය තත්වය ක්‍රියාත්මකයි ✅" else "සක්‍රීය තත්වය අක්‍රීයයි"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. පණිවිඩ යැවීමේ දැනුම්දීම් (Message Notifications)
            SettingToggleRow(
                icon = Icons.Default.Notifications,
                title = "පණිවිඩ යැවීමේ දැනුම්දීම් (Notifications)",
                subtitle = "නව පණිවිඩ ලැබුණු විට Sound & Push notifications",
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    updateProfile()
                    val msg = if (it) "දැනුම්දීම් ක්‍රියාත්මකයි 🔔" else "දැනුම්දීම් අක්‍රීයයි"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. පණිවිඩ ඉල්ලීම් (Message Requests)
            SettingToggleRow(
                icon = Icons.Default.Message,
                title = "පණිවිඩ ඉල්ලීම් (Message Requests)",
                subtitle = "නොදන්නා අයගෙන් එන පණිවිඩ පෙරීම",
                checked = messageRequestsEnabled,
                onCheckedChange = {
                    messageRequestsEnabled = it
                    val msg = if (it) "පණිවිඩ ඉල්ලීම් ක්‍රියාත්මකයි" else "පණිවිඩ ඉල්ලීම් පෙරීම ක්‍රියාත්මකයි"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. සංරක්ෂණාගාරගත කරන්න (Archive Chats)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        Toast.makeText(context, "සංරක්ෂිත චැට් ලැයිස්තුව පරීක්ෂා කරන ලදී 📁", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3B3C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Archive", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "සංරක්ෂණාගාරගත කරන්න", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "සංරක්ෂිත සංවාද කළමනාකරණය", color = Color.Gray, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. පෞද්ගලිකත්වය සහ සුරක්ෂිතතාව (Privacy and Security)
            SettingToggleRow(
                icon = Icons.Default.Security,
                title = "පෞද්ගලිකත්වය සහ සුරක්ෂිතතාව",
                subtitle = "End-to-End Encryption & Biometric Lock",
                checked = e2eEncryptionEnabled,
                onCheckedChange = {
                    viewModel.toggleE2EEncryption()
                    Toast.makeText(context, "සුරක්ෂිතතා සැකසුම් යාවත්කාලීන විය 🔒", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A3B3C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NeonBlue
            )
        )
    }
}

@Composable
fun ChatItemCard(
    chat: ChatSummary,
    onClick: () -> Unit,
    onAvatarClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("chat_item_${chat.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onAvatarClick?.invoke() }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(chat.peerAvatar)
                        .crossfade(true)
                        .placeholder(com.example.R.drawable.ic_friend_profile_white)
                        .error(com.example.R.drawable.ic_friend_profile_white)
                        .build(),
                    contentDescription = chat.peerName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                )
                if (chat.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(2.dp, Color(0xFF1E293B), CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.peerName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold, 
                            fontSize = 15.sp,
                            color = Color(0xFFF8FAFC)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = chat.lastTimestamp,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chat.lastMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            color = if (chat.unreadCount > 0) Color(0xFFF8FAFC) else Color(0xFFCBD5E1),
                            fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${chat.unreadCount}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailView(
    chat: ChatSummary,
    messages: List<Message>,
    mediaAutoSave: Boolean,
    aiChatAssistantEnabled: Boolean,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onReactToMessage: (messageId: String, reaction: String) -> Unit,
    onStartCall: (type: String) -> Unit,
    onDownloadMedia: (String) -> Unit,
    onToggleAutoSave: () -> Unit,
    onSimulateIncomingCall: () -> Unit,
    onProfileClick: (com.example.model.User) -> Unit,
    isPeerTyping: Boolean = false,
    onConnectOrFollow: () -> Unit = {}
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }
    var translatedMessageId by remember { mutableStateOf<String?>(null) }
    var activeFloatingEmoji by remember { mutableStateOf("👍") }
    var triggerFloatingCount by remember { mutableIntStateOf(0) }
    var showQuickReactionPicker by remember { mutableStateOf(false) }
    var selectedMessageForShare by remember { mutableStateOf<Message?>(null) }
    var showChatSettings by remember { mutableStateOf(false) }
    var showStickerStore by remember { mutableStateOf(false) }
    var showTikTokStickerSheet by remember { mutableStateOf(false) }
    var tikTokStickerInitialTab by remember { mutableIntStateOf(0) }
    var selectedWallpaper by remember { mutableStateOf<String?>(null) }
    var isChatBlocked by remember { mutableStateOf(false) }
    var coupleNickname by remember { mutableStateOf(chat.peerName) }

    // Messenger Themes & Fun Action States
    var currentTheme by remember { mutableStateOf(com.example.ui.components.MessengerChatTheme.MESSENGER_BLUE) }
    var customQuickEmoji by remember { mutableStateOf("👍") }
    var showMessengerMediaSheet by remember { mutableStateOf(false) }
    var messengerMediaInitialTab by remember { mutableIntStateOf(0) }
    var showMessengerFunHub by remember { mutableStateOf(false) }
    var showSurpriseGiftDialog by remember { mutableStateOf(false) }
    var showQuickPollDialog by remember { mutableStateOf(false) }
    var showQuickEmojiPicker by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var isVanishMode by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onSendMessage("📷 [Snap] Photo captured")
            Toast.makeText(context, "ඡායාරූපය යවන ලදී! 📸", Toast.LENGTH_SHORT).show()
        } else {
            onSendMessage("📷 [Snap] https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop&q=80")
            Toast.makeText(context, "Snap ඡායාරූපය යවන ලදී! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        cameraLauncher.launch(null)
    }

    val openCameraSafely = {
        if (com.example.util.CameraPermissionManager.shouldRequestPermission(context)) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            cameraLauncher.launch(null)
        }
    }

    val checkCelebrationEffect = { text: String ->
        val lower = text.lowercase()
        when {
            lower.contains("birthday") || lower.contains("උපන්දිනය") || lower.contains("🎉") -> {
                activeFloatingEmoji = "🎉"
                triggerFloatingCount += 5
            }
            lower.contains("love") || lower.contains("ආදර") || lower.contains("❤️") || lower.contains("💖") -> {
                activeFloatingEmoji = "❤️"
                triggerFloatingCount += 5
            }
            lower.contains("congrat") || lower.contains("සුභ පැතුම්") || lower.contains("bravo") -> {
                activeFloatingEmoji = "✨"
                triggerFloatingCount += 5
            }
            lower.contains("fire") || lower.contains("ගින්දර") || lower.contains("🔥") -> {
                activeFloatingEmoji = "🔥"
                triggerFloatingCount += 5
            }
            lower.contains("gift") || lower.contains("තෑග්ග") || lower.contains("🎁") -> {
                activeFloatingEmoji = "🎁"
                triggerFloatingCount += 4
            }
        }
    }

    // Voice Recording & WhatsApp-Style Audio Preview States
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var isVoiceRecorded by remember { mutableStateOf(false) }
    var recordedVoiceDuration by remember { mutableIntStateOf(0) }
    var isPreviewPlaying by remember { mutableStateOf(false) }
    var previewCurrentSeconds by remember { mutableIntStateOf(0) }

    // Growing Like Button States (Smooth Direct Location Scaling Engine)
    var isHoldingLike by remember { mutableStateOf(false) }
    val likeScale by animateFloatAsState(
        targetValue = if (isHoldingLike) 2.8f else 1.0f,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "likeScaleAnim"
    )

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            recordingSeconds = 0
            while (isRecordingVoice) {
                kotlinx.coroutines.delay(1000L)
                recordingSeconds++
            }
        }
    }

    LaunchedEffect(isPreviewPlaying) {
        if (isPreviewPlaying) {
            while (isPreviewPlaying && previewCurrentSeconds < recordedVoiceDuration) {
                kotlinx.coroutines.delay(1000L)
                previewCurrentSeconds++
            }
            if (previewCurrentSeconds >= recordedVoiceDuration) {
                isPreviewPlaying = false
                previewCurrentSeconds = 0
            }
        }
    }

    val mySentCount = remember(messages) { messages.count { it.isMe } }
    val peerReplied = remember(messages) { messages.any { !it.isMe } }
    val isConnectedChat = chat.isConnected || peerReplied
    val isBlockedFromSendingMore = !isConnectedChat && mySentCount >= 1

    // Photo Gallery Launcher using PickVisualMedia
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (isBlockedFromSendingMore) {
            Toast.makeText(context, "පළමු පණිවිඩය දැනටමත් යවා ඇත. ඔවුන් පිළිතුරු දුන් පසු හෝ Follow කළ පසු දිගටම පණිවිඩ යැවිය හැක.", Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }
        if (uri != null) {
            onSendMessage("📷 Image Attachment: $uri")
            Toast.makeText(context, "ඡායාරූපය ගැලරියෙන් සාර්ථකව යවන ලදී! 🖼️", Toast.LENGTH_SHORT).show()
        } else {
            onSendMessage("📷 [Photo] https://images.unsplash.com/photo-1516483638261-f4dbaf036963?w=800&auto=format&fit=crop&q=80")
            Toast.makeText(context, "ඡායාරූපය යවන ලදී! 🖼️", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(currentTheme.bgColors))
    ) {
        // Optional Custom Chat Wallpaper Background
        if (selectedWallpaper != null) {
            AsyncImage(
                model = selectedWallpaper,
                contentDescription = "Chat Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Chat Top Bar with Last Seen / Active Status & More Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OledSurface)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val userId = chat.peerUserId ?: "user_${chat.id}"
                            val user = com.example.model.User(
                                id = userId,
                                name = chat.peerName,
                                avatarUrl = chat.peerAvatar,
                                isOnline = chat.isOnline
                            )
                            onProfileClick(user)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        AsyncImage(
                            model = chat.peerAvatar,
                            contentDescription = coupleNickname,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        if (chat.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(OnlineGreen)
                                    .border(1.5.dp, OledSurface, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = coupleNickname,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Love", tint = Color(0xFFE41E3F), modifier = Modifier.size(12.dp))
                        }
                        Text(
                            text = if (chat.isOnline) "Active now • E2E Encrypted ❤️" else "${chat.lastSeen} • Encrypted",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = if (chat.isOnline) OnlineGreen else Color.Gray
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onStartCall("VIDEO") }
                        .padding(horizontal = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facetime_video_call),
                        contentDescription = "Video Call",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "Video Call",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        ),
                        color = NeonBlue
                    )
                }

                IconButton(onClick = onSimulateIncomingCall) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Simulate Incoming Call", tint = NeonBlue)
                }

                IconButton(onClick = { showChatSettings = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Chat Options", tint = Color.White)
                }
            }

            if (isVanishMode) {
                Surface(
                    color = Color(0xFF2E081F),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "👻", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Vanish Mode Active • Messages disappear after being seen",
                            color = Color(0xFFFF416C),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Messages List State & Auto-Scroll
            val listState = rememberLazyListState()

            LaunchedEffect(messages.size, isPeerTyping, textInput) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.Bottom)
            ) {
                // Friendly non-friend intro header at the top of chat
                if (!isConnectedChat && mySentCount == 0 && messages.isEmpty()) {
                    item(key = "non_friend_intro_header") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 28.dp, bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = chat.peerAvatar,
                                contentDescription = chat.peerName,
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = chat.peerName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "මිතුරන් නොවේ • ඔබට යැවිය හැක්කේ 1 පණිවිඩයක් පමණි",
                                color = Color(0xFF8A8D91),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // If connected and empty, show subtle FB-style centered status note
                if (isConnectedChat && messages.isEmpty()) {
                    item(key = "connected_subtle_note_empty") {
                        SubtleChatConnectedNote()
                    }
                }

                itemsIndexed(messages, key = { _, msg -> msg.id }) { index, msg ->
                    MessageBubbleRow(
                        message = msg,
                        peerAvatar = chat.peerAvatar,
                        mediaAutoSave = mediaAutoSave,
                        chatThemeGradient = currentTheme.bubbleGradient,
                        onReactionSelected = { reactionEmoji ->
                            onReactToMessage(msg.id, reactionEmoji)
                            activeFloatingEmoji = reactionEmoji
                            triggerFloatingCount++
                        },
                        onOpenOptions = {
                            selectedMessageForShare = msg
                        },
                        onDownloadMedia = { onDownloadMedia(msg.id) },
                        aiChatEnabled = aiChatAssistantEnabled,
                        isTranslated = translatedMessageId == msg.id,
                        onTranslate = {
                            translatedMessageId = if (translatedMessageId == msg.id) null else msg.id
                        }
                    )

                    // In the middle of the chat, like a message, show subtle status note right after the 1st message
                    if (isConnectedChat && index == 0) {
                        SubtleChatConnectedNote()
                    }
                }

                if (isPeerTyping) {
                    item(key = "peer_typing_indicator") {
                        TypingIndicatorFBStyle(peerAvatar = chat.peerAvatar)
                    }
                }
            }

            // Love Stickers Bar / Quick Emoji Bar
            if (showQuickReactionPicker && !isBlockedFromSendingMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OledSurface)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(OledSurfaceVariant)
                            .padding(vertical = 8.dp)
                    ) {
                        items(LOVE_STICKERS) { sticker ->
                            Text(
                                text = sticker,
                                fontSize = 28.sp,
                                modifier = Modifier
                                    .clickable {
                                        activeFloatingEmoji = sticker
                                        triggerFloatingCount++
                                        onSendMessage("Sticker: $sticker")
                                        showQuickReactionPicker = false
                                        Toast.makeText(context, "ආදරණීය ස්ටිකරය යවන ලදී! $sticker", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }

            // Input Bar vs Blocked Single-Message Limit Bar
            if (isBlockedFromSendingMore) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OledSurface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3B3C))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔒 පණිවිඩ ඉල්ලීම යවා ඇත",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ඔවුන් පිළිතුරු දෙන තෙක් හෝ Follow කරන තෙක් තවත් පණිවිඩ යැවිය නොහැක.",
                                color = Color(0xFFB0B3B8),
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onConnectOrFollow()
                                Toast.makeText(context, "දැන් ඔබ දෙදෙනාට එකිනෙකා සමඟ කතාබස් කළ හැකිය", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Follow", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                if (isRecordingVoice) {
                    // Voice Recorder Bar (Live Recording)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OledSurface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pulsing Red Recording Indicator
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE41E3F))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Live Recording Timer Counter
                        Text(
                            text = String.format("%02d:%02d", recordingSeconds / 60, recordingSeconds % 60),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Animated Sound Waveform Bars
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val waveHeights = listOf(14.dp, 24.dp, 10.dp, 28.dp, 18.dp, 32.dp, 20.dp, 12.dp, 26.dp, 16.dp)
                            waveHeights.forEachIndexed { index, defaultHeight ->
                                val animatedHeight by animateDpAsState(
                                    targetValue = if ((recordingSeconds + index) % 2 == 0) defaultHeight else (defaultHeight * 0.5f),
                                    animationSpec = tween(durationMillis = 300),
                                    label = "wave"
                                )
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(animatedHeight)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFF0084FF))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Pause / Stop Recording Button (Listen before sending)
                        IconButton(
                            onClick = {
                                isRecordingVoice = false
                                isVoiceRecorded = true
                                recordedVoiceDuration = if (recordingSeconds > 0) recordingSeconds else 1
                                previewCurrentSeconds = 0
                                isPreviewPlaying = false
                                Toast.makeText(context, "පටිගත කිරීම අවසන්. දැන් සවන් දී (Listen) යැවිය හැක! 🎧", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9800).copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Pause Recording to Preview",
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Cancel / Trash Recording Button
                        IconButton(
                            onClick = {
                                isRecordingVoice = false
                                isVoiceRecorded = false
                                recordingSeconds = 0
                                isPreviewPlaying = false
                                Toast.makeText(context, "හඬ පණිවිඩය අවලංගු කරන ලදී (Voice message discarded) 🗑️", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE41E3F).copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Cancel Recording",
                                tint = Color(0xFFE41E3F),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Direct Send Voice Note Button
                        IconButton(
                            onClick = {
                                val durationFormatted = String.format("%02d:%02d", recordingSeconds / 60, recordingSeconds % 60)
                                onSendMessage("🎤 Voice Message ($durationFormatted)")
                                isRecordingVoice = false
                                isVoiceRecorded = false
                                recordingSeconds = 0
                                isPreviewPlaying = false
                                Toast.makeText(context, "හඬ පණිවිඩය යවන ලදී 🎙️", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0084FF))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Voice Note",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else if (isVoiceRecorded) {
                    // WhatsApp-Style Voice Note Preview Bar (Listen before sending)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OledSurface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete Recording Button
                        IconButton(
                            onClick = {
                                isVoiceRecorded = false
                                isRecordingVoice = false
                                isPreviewPlaying = false
                                recordingSeconds = 0
                                previewCurrentSeconds = 0
                                Toast.makeText(context, "හඬ පණිවිඩය මකා දමන ලදී 🗑️", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE41E3F).copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Voice Recording",
                                tint = Color(0xFFE41E3F),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Play / Pause Audio Preview Button (WhatsApp Style)
                        IconButton(
                            onClick = { isPreviewPlaying = !isPreviewPlaying },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0084FF))
                        ) {
                            Icon(
                                imageVector = if (isPreviewPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPreviewPlaying) "Pause Preview" else "Play Preview",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Audio Waveform & Timer Display
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = String.format("%02d:%02d", previewCurrentSeconds / 60, previewCurrentSeconds % 60) + " / " + String.format("%02d:%02d", recordedVoiceDuration / 60, recordedVoiceDuration % 60),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val previewBars = listOf(16.dp, 28.dp, 12.dp, 24.dp, 18.dp, 30.dp, 22.dp, 14.dp, 26.dp, 20.dp, 16.dp, 24.dp)
                                previewBars.forEachIndexed { idx, barHeight ->
                                    val isPlayed = (idx.toFloat() / previewBars.size.toFloat()) <= (previewCurrentSeconds.toFloat() / recordedVoiceDuration.coerceAtLeast(1).toFloat())
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(if (isPreviewPlaying && isPlayed) barHeight else (barHeight * 0.7f))
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (isPlayed) Color(0xFF0084FF) else Color.Gray.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Re-record Button
                        IconButton(
                            onClick = {
                                isVoiceRecorded = false
                                isPreviewPlaying = false
                                isRecordingVoice = true
                                recordingSeconds = 0
                                previewCurrentSeconds = 0
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3A3B3C))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Re-record Voice Note",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Send Voice Note Button
                        IconButton(
                            onClick = {
                                val durationFormatted = String.format("%02d:%02d", recordedVoiceDuration / 60, recordedVoiceDuration % 60)
                                onSendMessage("🎤 Voice Message ($durationFormatted)")
                                isVoiceRecorded = false
                                isPreviewPlaying = false
                                isRecordingVoice = false
                                recordingSeconds = 0
                                previewCurrentSeconds = 0
                                Toast.makeText(context, "හඬ පණිවිඩය යවන ලදී 🎙️", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0084FF))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Voice Note",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    // TikTok Style Chat Box & Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A)) // Match Chat Detail background
                            .border(width = 0.5.dp, color = Color(0xFF334155))
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Camera Button
                        IconButton(
                            onClick = {
                                if (isBlockedFromSendingMore) {
                                    Toast.makeText(context, "පළමු පණිවිඩය දැනටමත් යවා ඇත. ඔවුන් පිළිතුරු දුන් පසු දිගටම පණිවිඩ යැවිය හැක.", Toast.LENGTH_LONG).show()
                                } else {
                                    openCameraSafely()
                                }
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Camera",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // 2. Photo Gallery Button
                        IconButton(
                            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Photo Gallery",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // 3. Voice Record Mic Button
                        IconButton(
                            onClick = {
                                if (isBlockedFromSendingMore) {
                                    Toast.makeText(context, "පළමු පණිවිඩය දැනටමත් යවා ඇත. ඔවුන් පිළිතුරු දුන් පසු දිගටම පණිවිඩ යැවිය හැක.", Toast.LENGTH_LONG).show()
                                } else {
                                    isRecordingVoice = true
                                    Toast.makeText(context, "හඬ පණිවිඩය පටිගත කිරීම අරඹන ලදී... 🎙️", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Record",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Messenger Pill-shaped Text Input Field
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { 
                                Text(
                                    text = if (isVanishMode) "Vanish message... 👻" else "Aa Send a message...",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp
                                ) 
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        showMessengerMediaSheet = true
                                        messengerMediaInitialTab = 0
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SentimentSatisfiedAlt,
                                        contentDescription = "Emojis, Stickers & GIFs",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color(0xFFF8FAFC),
                                unfocusedTextColor = Color(0xFFF8FAFC),
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B),
                                cursorColor = Color(0xFF10B981)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        if (textInput.isNotBlank()) {
                            // Send Button
                            IconButton(
                                onClick = {
                                    val toSend = textInput
                                    textInput = ""
                                    onSendMessage(toSend)
                                    checkCelebrationEffect(toSend)
                                    if (!isConnectedChat && mySentCount == 0) {
                                        Toast.makeText(context, "පළමු පණිවිඩය යවන ලදී (1/1). මිතුරු වූ පසු දිගටම චැට් කළ හැක.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            // Quick Heart Icon (Instead of Like Thumb)
                            IconButton(
                                onClick = {
                                    if (isBlockedFromSendingMore) {
                                        Toast.makeText(context, "පළමු පණිවිඩය දැනටමත් යවා ඇත.", Toast.LENGTH_LONG).show()
                                    } else {
                                        onSendMessage("❤️")
                                        activeFloatingEmoji = "❤️"
                                        triggerFloatingCount += 5
                                    }
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Send Heart",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Floating Heart / Reaction Particles Stream Overlay
        ChatFloatingReactionsOverlay(
            activeEmoji = activeFloatingEmoji,
            triggerCount = triggerFloatingCount
        )

        // Message Actions Sheet (Download, Copy, Share)
        if (selectedMessageForShare != null) {
            MessageActionsSheet(
                message = selectedMessageForShare!!,
                onDismiss = { selectedMessageForShare = null }
            )
        }

        // Chat Customization & Settings Bottom Sheet (Wallpaper, Block, Nickname, Gallery Save)
        if (showChatSettings) {
            ChatCustomizationBottomSheet(
                peerName = coupleNickname,
                peerAvatar = chat.peerAvatar,
                isOnline = chat.isOnline,
                lastSeen = if (chat.isOnline) "දැන් සක්‍රීයයි" else "${chat.lastSeen} පෙර සක්‍රීය විය",
                mediaAutoSave = mediaAutoSave,
                onToggleAutoSave = onToggleAutoSave,
                onSetWallpaper = { url ->
                    selectedWallpaper = url
                    showChatSettings = false
                },
                onBlockChat = {
                    isChatBlocked = true
                    showChatSettings = false
                    Toast.makeText(context, "${chat.peerName} සාර්ථකව Block කරන ලදී 🚫", Toast.LENGTH_SHORT).show()
                },
                onUpdateNickname = { newNick ->
                    coupleNickname = newNick
                    showChatSettings = false
                    Toast.makeText(context, "ආදරණීය නම '$newNick' ලෙස වෙනස් විය! ✨", Toast.LENGTH_SHORT).show()
                },
                onStartCall = { callType ->
                    showChatSettings = false
                    onStartCall(callType)
                },
                onOpenProfile = {
                    showChatSettings = false
                    val userId = chat.peerUserId ?: "user_${chat.peerName.lowercase().replace(" ", "")}"
                    val user = com.example.model.User(
                        id = userId,
                        name = chat.peerName,
                        username = chat.peerName.lowercase().replace(" ", ""),
                        avatarUrl = chat.peerAvatar,
                        isOnline = chat.isOnline
                    )
                    onProfileClick(user)
                },
                onDismiss = { showChatSettings = false }
            )
        }

        // Sticker Store Bottom Sheet (PlayStore / Download Sticker packs)
        if (showStickerStore) {
            StickerStoreBottomSheet(
                onSelectSticker = { sticker ->
                    onSendMessage("Sticker: $sticker")
                    showStickerStore = false
                    Toast.makeText(context, "ස්ටිකරය සාර්ථකව යවන ලදී! $sticker 💖", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showStickerStore = false }
            )
        }

        // Messenger Full Media Sheet (Emojis, Stickers, GIFs, Soundmojis, Gifts)
        if (showMessengerMediaSheet || showTikTokStickerSheet) {
            val startTab = if (showMessengerMediaSheet) messengerMediaInitialTab else tikTokStickerInitialTab
            com.example.ui.components.MessengerChatMediaSheet(
                initialTab = startTab,
                accentColor = currentTheme.accentColor,
                onSendEmoji = { emojiChar ->
                    activeFloatingEmoji = emojiChar
                    triggerFloatingCount += 2
                    checkCelebrationEffect(emojiChar)
                    onSendMessage(emojiChar)
                    showMessengerMediaSheet = false
                    showTikTokStickerSheet = false
                    Toast.makeText(context, "Emoji යවන ලදී! $emojiChar", Toast.LENGTH_SHORT).show()
                },
                onSendSticker = { stickerMsg ->
                    activeFloatingEmoji = "✨"
                    triggerFloatingCount += 2
                    onSendMessage(stickerMsg)
                    showMessengerMediaSheet = false
                    showTikTokStickerSheet = false
                    Toast.makeText(context, "ස්ටිකරය යවන ලදී! 🎭", Toast.LENGTH_SHORT).show()
                },
                onSendGif = { gif ->
                    activeFloatingEmoji = "🎬"
                    triggerFloatingCount += 3
                    onSendMessage("GIF: ${gif.gifUrl} | ${gif.title}")
                    showMessengerMediaSheet = false
                    showTikTokStickerSheet = false
                    Toast.makeText(context, "GIF යවන ලදී! ${gif.title} 🎬", Toast.LENGTH_SHORT).show()
                },
                onSendSoundmoji = { soundmoji ->
                    activeFloatingEmoji = soundmoji.emoji
                    triggerFloatingCount += 4
                    onSendMessage("🔊 [Soundmoji]: ${soundmoji.emoji} - ${soundmoji.title}")
                    showMessengerMediaSheet = false
                    showTikTokStickerSheet = false
                    Toast.makeText(context, "${soundmoji.emoji} ${soundmoji.title} Soundmoji යවන ලදී! 🔊", Toast.LENGTH_SHORT).show()
                },
                onSendGift = { gift ->
                    activeFloatingEmoji = gift.icon
                    triggerFloatingCount += 5
                    val giftMsg = "🎁 Gift: ${gift.name} (${gift.icon}) • ${gift.coins} Coins"
                    onSendMessage(giftMsg)
                    showMessengerMediaSheet = false
                    showTikTokStickerSheet = false
                    Toast.makeText(context, "${gift.icon} ${gift.name} තෑග්ග යවන ලදී! (Coins: ${gift.coins}) 🪙", Toast.LENGTH_LONG).show()
                },
                onOpenSurpriseGiftComposer = {
                    showMessengerMediaSheet = false
                    showTikTokStickerSheet = false
                    showSurpriseGiftDialog = true
                },
                onDismiss = {
                    showMessengerMediaSheet = false
                    showTikTokStickerSheet = false
                }
            )
        }

        // Messenger Fun Action Hub (Themes, Dice, Coin Flip, Polls, 8-Ball, Nicknames, Vanish Mode)
        if (showMessengerFunHub) {
            com.example.ui.components.MessengerFunHubSheet(
                currentTheme = currentTheme,
                onSelectTheme = { theme ->
                    currentTheme = theme
                    Toast.makeText(context, "${theme.icon} ${theme.title} තේමාව සක්‍රීය කරන ලදී!", Toast.LENGTH_SHORT).show()
                },
                onRollDice = {
                    showMessengerFunHub = false
                    val roll = (1..6).random()
                    activeFloatingEmoji = "🎲"
                    triggerFloatingCount += 4
                    onSendMessage("🎲 [Dice Roll]: $roll")
                    Toast.makeText(context, "දාදු කැටය පෙරළන ලදී! අංකය: $roll 🎲", Toast.LENGTH_SHORT).show()
                },
                onFlipCoin = {
                    showMessengerFunHub = false
                    val result = if ((0..1).random() == 0) "Heads (සිරස) 👑" else "Tails (අගය) 🦅"
                    activeFloatingEmoji = "🪙"
                    triggerFloatingCount += 4
                    onSendMessage("🪙 [Coin Flip]: $result")
                    Toast.makeText(context, "කාසිය උඩ දමන ලදී! ප්‍රතිඵලය: $result 🪙", Toast.LENGTH_SHORT).show()
                },
                onOpenPollComposer = {
                    showMessengerFunHub = false
                    showQuickPollDialog = true
                },
                onOpenMagic8Ball = {
                    showMessengerFunHub = false
                    val answers = listOf("අනිවාර්යයෙන්ම ඔව්! 🌟", "සැකයක් නෑ! 👍", "බලාපොරොත්තු තබාගන්න 🍀", "නැවත අසන්න 🤔", "දැනට කියන්න බෑ ⏳", "නැහැ, එය එපා ❌", "ඉතා හොඳ ප්‍රතිඵලයක්! ✨")
                    val ans = answers.random()
                    activeFloatingEmoji = "🎱"
                    triggerFloatingCount += 3
                    onSendMessage("🎱 [Magic 8-Ball]: $ans")
                    Toast.makeText(context, "Magic 8-Ball පිළිතුර යවන ලදී! 🎱", Toast.LENGTH_SHORT).show()
                },
                onOpenNicknameEditor = {
                    showMessengerFunHub = false
                    showNicknameDialog = true
                },
                onOpenQuickEmojiPicker = {
                    showMessengerFunHub = false
                    showQuickEmojiPicker = true
                },
                isVanishMode = isVanishMode,
                onToggleVanishMode = {
                    isVanishMode = !isVanishMode
                    Toast.makeText(context, if (isVanishMode) "Vanish Mode සක්‍රීයයි! 👻" else "Vanish Mode අක්‍රීයයි! 👁️", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showMessengerFunHub = false }
            )
        }

        // Surprise Gift Composer Dialog
        if (showSurpriseGiftDialog) {
            com.example.ui.components.SurpriseGiftDialog(
                onSendSurprise = { secretText ->
                    activeFloatingEmoji = "🎁"
                    triggerFloatingCount += 5
                    onSendMessage("🎁 [Surprise Gift]: $secretText")
                    showSurpriseGiftDialog = false
                    Toast.makeText(context, "රහස් තෑග්ග ඔතා යවන ලදී! 🎁", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showSurpriseGiftDialog = false }
            )
        }

        // Quick Poll Composer Dialog
        if (showQuickPollDialog) {
            com.example.ui.components.QuickPollDialog(
                onSendPoll = { question, opt1, opt2 ->
                    activeFloatingEmoji = "📊"
                    triggerFloatingCount += 4
                    onSendMessage("📊 [Poll]: $question | $opt1 | $opt2")
                    showQuickPollDialog = false
                    Toast.makeText(context, "Quick Poll එක සාර්ථකව යවන ලදී! 📊", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showQuickPollDialog = false }
            )
        }

        // Quick Reaction Emoji Customizer
        if (showQuickEmojiPicker) {
            com.example.ui.components.QuickEmojiPickerSheet(
                currentEmoji = customQuickEmoji,
                onSelectEmoji = { emoji ->
                    customQuickEmoji = emoji
                    showQuickEmojiPicker = false
                    Toast.makeText(context, "Quick Reaction Emoji එක '$emoji' ලෙස වෙනස් විය! ✨", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showQuickEmojiPicker = false }
            )
        }

        // Nickname Editor Dialog
        if (showNicknameDialog) {
            var tempNick by remember { mutableStateOf(coupleNickname) }
            AlertDialog(
                onDismissRequest = { showNicknameDialog = false },
                title = { Text("ආදරණීය නම සකසන්න ✏️", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = tempNick,
                        onValueChange = { tempNick = it },
                        placeholder = { Text("නව නම මෙහි ලියන්න...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempNick.isNotBlank()) {
                                coupleNickname = tempNick.trim()
                                showNicknameDialog = false
                                Toast.makeText(context, "නම '$tempNick' ලෙස වෙනස් විය! ✨", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("සුරකින්න")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNicknameDialog = false }) {
                        Text("අවලංගු කරන්න", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF1E1E24)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatCustomizationBottomSheet(
    peerName: String,
    peerAvatar: String = "",
    isOnline: Boolean = false,
    lastSeen: String = "3පැකට පෙර සක්‍රීය විය",
    mediaAutoSave: Boolean,
    onToggleAutoSave: () -> Unit,
    onSetWallpaper: (String) -> Unit,
    onBlockChat: () -> Unit,
    onUpdateNickname: (String) -> Unit,
    onStartCall: (String) -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Interactive states
    var customNickname by remember { mutableStateOf(peerName) }
    var isMuted by remember { mutableStateOf(false) }
    var showMuteDialog by remember { mutableStateOf(false) }
    var muteDuration by remember { mutableStateOf("පැය 8කට") }
    
    var showShareContactDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var groupNameInput by remember { mutableStateOf("Group with $peerName") }
    
    var showQuickReactionDialog by remember { mutableStateOf(false) }
    var quickEmoji by remember { mutableStateOf("👍") }
    
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showEncryptionDialog by remember { mutableStateOf(false) }
    
    var showDisappearingDialog by remember { mutableStateOf(false) }
    var disappearingTime by remember { mutableStateOf("අක්‍රීයයි") }
    
    var readReceiptsEnabled by remember { mutableStateOf(true) }
    
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("අනවශ්‍ය පණිවිඩ (Spam)") }
    
    var showBlockConfirmDialog by remember { mutableStateOf(false) }
    var showWallpaperSection by remember { mutableStateOf(false) }

    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            onSetWallpaper(uri.toString())
            Toast.makeText(context, "ගැලරියෙන් බිතුපත්‍රය සාර්ථකව තෝරා ගන්නා ලදී! 🖼️", Toast.LENGTH_SHORT).show()
        }
    }

    val wallpapers = listOf(
        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=800&auto=format&fit=crop&q=80"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF242526),
        scrimColor = Color.Black.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Profile Header matching Screenshots
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onDismiss()
                        onOpenProfile()
                    }
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (peerAvatar.isNotBlank()) {
                        AsyncImage(
                            model = peerAvatar,
                            contentDescription = peerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0084FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = peerName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }
                    }
                    if (isOnline) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF31A24C))
                                .border(2.5.dp, Color(0xFF242526), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = customNickname,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isOnline) "දැන් සක්‍රීයයි" else lastSeen,
                    color = Color(0xFFB0B3B8),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF3A3B3C).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB0B3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "මුල සිට අග දක්වා ම සංකේතිතයි",
                        color = Color(0xFFB0B3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Top 4 Action Circle Buttons Row (අමතන්න, වීඩියෝ කතාබහ, ප්‍රොෆයිලය, නිහඬ කරන්න)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 1. අමතන්න (Call)
                QuickActionCircleItem(
                    icon = Icons.Default.Call,
                    label = "අමතන්න",
                    onClick = {
                        onStartCall("AUDIO")
                        Toast.makeText(context, "$peerName වෙත අමතනු ලැබේ... 📞", Toast.LENGTH_SHORT).show()
                    }
                )

                // 2. වීඩියෝ කතාබහ (Video Call)
                QuickActionCircleItem(
                    icon = Icons.Default.Videocam,
                    customDrawableRes = R.drawable.ic_facetime_video_call,
                    label = "වීඩියෝ කතාබහ",
                    onClick = {
                        onStartCall("VIDEO")
                        Toast.makeText(context, "$peerName සමඟ වීඩියෝ කතාබහ ආරම්භ වේ... 🎥", Toast.LENGTH_SHORT).show()
                    }
                )

                // 3. ප්‍රොෆයිලය (Profile)
                QuickActionCircleItem(
                    icon = Icons.Default.AccountCircle,
                    label = "ප්‍රොෆයිලය",
                    onClick = {
                        onDismiss()
                        onOpenProfile()
                    }
                )

                // 4. නිහඬ කරන්න (Mute)
                QuickActionCircleItem(
                    icon = if (isMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                    label = if (isMuted) "නිහඬයි" else "නිහඬ කරන්න",
                    isActive = isMuted,
                    onClick = { showMuteDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= SECTION 1: ක්‍රියා (Actions) =================
            OptionSectionTitle("ක්‍රියා")

            // 0. ප්‍රොෆයිලය බලන්න (View Profile)
            OptionItemRow(
                icon = Icons.Default.Person,
                title = "ප්‍රොෆයිලය බලන්න",
                subtitle = "$peerName ගේ සවිස්තරාත්මක Profile එක විවෘත කරන්න",
                onClick = {
                    onDismiss()
                    onOpenProfile()
                }
            )

            // 1. නොකිය වූ ලෙස ලකුණු කරන්න
            OptionItemRow(
                icon = Icons.Default.Mail,
                title = "නොකිය වූ ලෙස ලකුණු කරන්න",
                onClick = {
                    Toast.makeText(context, "සංවාදය නොකියවූ ලෙස ලකුණු කරන ලදී ✉️", Toast.LENGTH_SHORT).show()
                }
            )

            // 2. සබඳතාව බෙදා ගන්න
            OptionItemRow(
                icon = Icons.Default.Share,
                customDrawableRes = com.example.R.drawable.ic_3d_share_vector,
                title = "සබඳතාව බෙදා ගන්න",
                onClick = { showShareContactDialog = true }
            )

            // 3. peerName සමඟ සමූහයක් තනන්න
            OptionItemRow(
                icon = Icons.Default.GroupAdd,
                title = "$peerName සමඟ සමූහයක් තනන්න",
                onClick = { showCreateGroupDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ================= SECTION 2: අභිරුචිකරණය (Customization) =================
            OptionSectionTitle("අභිරුචිකරණය")

            // 1. කඩිනම් ප්‍රතිචාරය (Quick Reaction Emoji)
            OptionItemRow(
                icon = Icons.Default.ThumbUp,
                title = "කඩිනම් ප්‍රතිචාරය",
                subtitle = "වත්මන් Emoji: $quickEmoji",
                onClick = { showQuickReactionDialog = true }
            )

            // 2. සුරතල් නමි (Nicknames)
            OptionItemRow(
                icon = Icons.Default.Edit,
                title = "සුරතල් නමි",
                subtitle = customNickname,
                onClick = { showNicknameDialog = true }
            )

            // 3. චැට් බිතුපත්‍ර (Chat Wallpapers)
            OptionItemRow(
                icon = Icons.Default.Palette,
                title = "චැට් බිතුපත්‍ර (Chat Wallpapers)",
                subtitle = "ගැලරියෙන් හෝ පැලට් එකෙන් තෝරන්න",
                onClick = { showWallpaperSection = !showWallpaperSection }
            )

            if (showWallpaperSection) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 50.dp, bottom = 10.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF3A3B3C))
                                .clickable { wallpaperPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = "Gallery", tint = Color(0xFF0084FF))
                            Text(text = "ගැලරිය", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    items(wallpapers) { wpUrl ->
                        AsyncImage(
                            model = wpUrl,
                            contentDescription = "Wallpaper",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.dp, Color(0xFF0084FF), RoundedCornerShape(12.dp))
                                .clickable {
                                    onSetWallpaper(wpUrl)
                                    Toast.makeText(context, "බිතුපත්‍රය සාර්ථකව යෙදින! 🖼️", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }
                }
            }

            // 4. ඡායාරූප සහ වීඩියෝ ගැලරියට සුරකින්න (Auto Save)
            OptionItemRow(
                icon = Icons.Default.Download,
                title = "ඡායාරූප සහ වීඩියෝ ගැලරියට සුරකින්න",
                subtitle = if (mediaAutoSave) "ලැබෙන මාධ්‍ය සියල්ල ගැලරියට සුරැකේ" else "Manual Mode (අනුමත කළ යුතුය)",
                trailing = {
                    Switch(
                        checked = mediaAutoSave,
                        onCheckedChange = { onToggleAutoSave() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0084FF)
                        )
                    )
                },
                onClick = { onToggleAutoSave() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ================= SECTION 3: පෞද්ගලිකත්වය සහ සභාය (Privacy & Support) =================
            OptionSectionTitle("පෞද්ගලිකත්වය සහ සභාය")

            // 1. මුල සිට අග දක්වා ම සංකේතනය සත්‍යාපනය කරන්න
            OptionItemRow(
                icon = Icons.Default.Lock,
                title = "මුල සිට අග දක්වා ම සංකේතනය සත්‍යාපනය කරන්න",
                onClick = { showEncryptionDialog = true }
            )

            // 2. අතුරුදහන් වන පණිවිඩ (Disappearing Messages)
            OptionItemRow(
                icon = Icons.Default.Timer,
                title = "අතුරුදහන් වන පණිවිඩ",
                subtitle = disappearingTime,
                onClick = { showDisappearingDialog = true }
            )

            // 3. කියවූ බවට ලදුපත් (Read Receipts)
            OptionItemRow(
                icon = Icons.Default.Visibility,
                title = "කියවූ බවට ලදුපත්",
                subtitle = if (readReceiptsEnabled) "සක්‍රීයයි" else "ක්‍රියාවිරහිතයි",
                trailing = {
                    Switch(
                        checked = readReceiptsEnabled,
                        onCheckedChange = {
                            readReceiptsEnabled = it
                            val msg = if (it) "කියවූ බවට ලදුපත් සක්‍රීයයි 👁️" else "කියවූ බවට ලදුපත් ක්‍රියාවිරහිතයි"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0084FF)
                        )
                    )
                },
                onClick = {
                    readReceiptsEnabled = !readReceiptsEnabled
                    val msg = if (readReceiptsEnabled) "කියවූ බවට ලදුපත් සක්‍රීයයි 👁️" else "කියවූ බවට ලදුපත් ක්‍රියාවිරහිතයි"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )

            // 4. peerName ව අවහිර කරන්න (Block)
            OptionItemRow(
                icon = Icons.Default.Block,
                iconTint = Color(0xFFE41E3F),
                title = "$peerName ව අවහිර කරන්න",
                titleColor = Color(0xFFE41E3F),
                onClick = { showBlockConfirmDialog = true }
            )

            // 5. පැමිණිලි කරන්න (Report)
            OptionItemRow(
                icon = Icons.Default.ReportProblem,
                iconTint = Color(0xFFE41E3F),
                title = "පැමිණිලි කරන්න",
                titleColor = Color(0xFFE41E3F),
                subtitle = "ප්‍රතිපෝෂණය ලබා දී සංවාදය පැමිණිලි කරන්න",
                onClick = { showReportDialog = true }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Dialog 1: Mute Dialog
    if (showMuteDialog) {
        val muteOptions = listOf("පැය 1කට", "පැය 8කට", "පැය 24කට", "ඔබ පණගන්වන තෙක්")
        AlertDialog(
            onDismissRequest = { showMuteDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("දැනුම්දීම් නිහඬ කරන්න (Mute)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    muteOptions.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    muteDuration = opt
                                    isMuted = true
                                    Toast.makeText(context, "$opt සඳහා දැනුම්දීම් නිහඬ කරන ලදී 🔕", Toast.LENGTH_SHORT).show()
                                    showMuteDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (muteDuration == opt && isMuted),
                                onClick = {
                                    muteDuration = opt
                                    isMuted = true
                                    Toast.makeText(context, "$opt සඳහා දැනුම්දීම් නිහඬ කරන ලදී 🔕", Toast.LENGTH_SHORT).show()
                                    showMuteDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0084FF))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMuteDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog 2: Share Contact Dialog
    if (showShareContactDialog) {
        AlertDialog(
            onDismissRequest = { showShareContactDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("$peerName ගේ සබඳතාව බෙදා ගන්න", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("මෙම සබඳතාවය වෙනත් සේවාවකට යවන්න:", color = Color(0xFFB0B3B8), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "FriendHub Contact: $peerName (https://friendhub.lk/user/$peerName)")
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Contact Share"))
                            showShareContactDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF))
                    ) {
                        Text("බාහිර ඇප් මඟින් බෙදා ගන්න (Share Link)", color = Color.White)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareContactDialog = false }) {
                    Text("වසා දමන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog 3: Create Group Dialog
    if (showCreateGroupDialog) {
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("$peerName සමඟ සමූහයක් තනන්න", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = groupNameInput,
                        onValueChange = { groupNameInput = it },
                        label = { Text("සමූහයේ නම (Group Name)", color = Color(0xFFB0B3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0084FF),
                            unfocusedBorderColor = Color(0xFF383838)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "'$groupNameInput' සමූහය සාර්ථකව තනන ලදී! 👥", Toast.LENGTH_LONG).show()
                        showCreateGroupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF))
                ) {
                    Text("සමූහය සාදන්න", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog 4: Quick Emoji Selector Dialog
    if (showQuickReactionDialog) {
        val emojis = listOf("👍", "❤️", "😂", "🔥", "😮", "👏", "💩", "🥳", "💯", "🙏")
        AlertDialog(
            onDismissRequest = { showQuickReactionDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("කඩිනම් ප්‍රතිචාර Emoji ය තෝරන්න", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    emojis.take(5).forEach { em ->
                        Text(
                            text = em,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    quickEmoji = em
                                    Toast.makeText(context, "කඩිනම් ප්‍රතිචාරය '$em' ලෙස වෙනස් විය! $em", Toast.LENGTH_SHORT).show()
                                    showQuickReactionDialog = false
                                }
                                .padding(6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQuickReactionDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog 5: Nickname Edit Dialog
    if (showNicknameDialog) {
        AlertDialog(
            onDismissRequest = { showNicknameDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("අන්වර්ථ නාමය වෙනස් කරන්න", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = customNickname,
                        onValueChange = { customNickname = it },
                        label = { Text("සුරතල් නම (Nickname)", color = Color(0xFFB0B3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0084FF),
                            unfocusedBorderColor = Color(0xFF383838)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateNickname(customNickname)
                        showNicknameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF))
                ) {
                    Text("සුරකින්න", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNicknameDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog 6: E2E Encryption Verification Dialog
    if (showEncryptionDialog) {
        AlertDialog(
            onDismissRequest = { showEncryptionDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF31A24C))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("සංකේතනය සත්‍යාපනය", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("මුල සිට අග දක්වා ම සංකේතනය සක්‍රීයයි (100% End-to-End Encrypted)", color = Color(0xFF31A24C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("ආරක්‍ෂිත යතුර (Safety Numbers):", color = Color(0xFFB0B3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "4819 - 0392 - 7712 - 9041\n3328 - 1092 - 5683 - 2941",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "සංකේතනය සාර්ථකව සත්‍යාපනය කරන ලදී! 🔒", Toast.LENGTH_SHORT).show()
                        showEncryptionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF31A24C))
                ) {
                    Text("සත්‍යාපනය වී ඇත", color = Color.White)
                }
            }
        )
    }

    // Dialog 7: Disappearing Messages Dialog
    if (showDisappearingDialog) {
        val options = listOf("අක්‍රීයයි", "පැය 24 (24 Hours)", "දින 7 (7 Days)", "දින 90 (90 Days)")
        AlertDialog(
            onDismissRequest = { showDisappearingDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("අතුරුදහන් වන පණිවිඩ (Disappearing Messages)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    options.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    disappearingTime = opt
                                    Toast.makeText(context, "අතුරුදහන් වන පණිවිඩ කාලය '$opt' ලෙස සැකසිනි ⏱️", Toast.LENGTH_SHORT).show()
                                    showDisappearingDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (disappearingTime == opt),
                                onClick = {
                                    disappearingTime = opt
                                    Toast.makeText(context, "අතුරුදහන් වන පණිවිඩ කාලය '$opt' ලෙස සැකසිනි ⏱️", Toast.LENGTH_SHORT).show()
                                    showDisappearingDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0084FF))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDisappearingDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog 8: Report Dialog
    if (showReportDialog) {
        val reportReasons = listOf("අනවශ්‍ය පණිවිඩ (Spam)", "හිංසනය හෝ තර්ජන (Harassment)", "ව්‍යාජ ගිණුමක් (Fake Account)", "අසභ්‍ය අන්තර්ගත (Offensive Content)")
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("සංවාදය පැමිණිලි කරන්න (Report)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("පැමිණිලි කිරීමට හේතුව තෝරන්න:", color = Color(0xFFB0B3B8), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    reportReasons.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = r }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (reportReason == r),
                                onClick = { reportReason = r },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE41E3F))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(r, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "පැමිණිල්ල සාර්ථකව යොමු කරන ලදී! පරීක්ෂා කර පියවර ගන්නෙමු ⚠️", Toast.LENGTH_LONG).show()
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE41E3F))
                ) {
                    Text("යවන්න", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog 9: Block Confirmation Dialog
    if (showBlockConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBlockConfirmDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("$peerName ව අවහිර කරන්නද?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "අවහිර කළ පසු ඔබට මෙම පරිශීලකයාගෙන් පණිවිඩ හෝ ඇමතුම් නොලැබෙනු ඇත.",
                    color = Color(0xFFB0B3B8),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBlockConfirmDialog = false
                        onBlockChat()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE41E3F))
                ) {
                    Text("අවහිර කරන්න (Block)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockConfirmDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }
}

@Composable
fun QuickActionCircleItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    customDrawableRes: Int? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isActive) Color(0xFF0084FF) else Color(0xFF3A3B3C)),
            contentAlignment = Alignment.Center
        ) {
            if (customDrawableRes != null) {
                Image(
                    painter = painterResource(id = customDrawableRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color(0xFFE4E6EB),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OptionSectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFB0B3B8),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
    )
}

@Composable
fun OptionItemRow(
    icon: ImageVector,
    customDrawableRes: Int? = null,
    iconTint: Color = Color.White,
    title: String,
    titleColor: Color = Color.White,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A3B3C)),
            contentAlignment = Alignment.Center
        ) {
            if (customDrawableRes != null) {
                com.example.ui.components.Custom3DShareIcon(size = 24.dp)
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = titleColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = Color(0xFFB0B3B8),
                    fontSize = 12.sp
                )
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerStoreBottomSheet(
    onSelectSticker: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val stickerPacks = listOf(
        "💖 Romantic Love Pack",
        "🧸 Cute Teddy Bears",
        "🌹 Red Roses & Hearts",
        "✨ Sparkling Magic",
        "💋 Kisses & Hugs"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = OledSurface,
        scrimColor = Color.Black.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Store, contentDescription = "Store", tint = Color(0xFFE41E3F), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "ස්ටිකර් ගබඩාව (Sticker Store)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "ආදරණීය ස්ටිකර් පැකේජ බාගත කර භාවිත කරන්න",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            stickerPacks.forEach { pack ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            Toast.makeText(context, "$pack PlayStore එකෙන් බාගත කර එකතු කරන ලදී! 🎉", Toast.LENGTH_LONG).show()
                            onSelectSticker("💖")
                        },
                    colors = CardDefaults.cardColors(containerColor = OledSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = pack, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Button(
                            onClick = {
                                Toast.makeText(context, "$pack Downloaded! 🚀", Toast.LENGTH_SHORT).show()
                                onSelectSticker("🥰")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Download", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TikTokChatStickerSheet(
    initialTab: Int = 0,
    onSendEmoji: (String) -> Unit,
    onSendSticker: (String) -> Unit,
    onSendGift: (TikTokGiftItem) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF161616),
        scrimColor = Color.Black.copy(alpha = 0.75f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF3A3A3A))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp)
        ) {
            // TikTok Tabs Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("😊 TikTok Emojis", "🎭 Stickers", "🎁 Gifts").forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedTab = index }
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color(0xFF888888),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(2.5.dp)
                                .background(if (isSelected) Color(0xFFFE2C55) else Color.Transparent, RoundedCornerShape(1.dp))
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF282828), thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // 1. TikTok Emojis Grid
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Official TikTok Emoji Reactions",
                                color = Color(0xFFAAAAAA),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        items(TIKTOK_EMOJIS_LIST.chunked(5)) { rowEmojis ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowEmojis.forEach { (emoji, label) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                onSendEmoji("$emoji $label")
                                            }
                                            .padding(6.dp)
                                    ) {
                                        Text(text = emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = label.replace("[", "").replace("]", ""),
                                            fontSize = 9.sp,
                                            color = Color(0xFF999999),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // 2. TikTok Stickers
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Trending TikTok Meme & Dance Stickers",
                                color = Color(0xFFAAAAAA),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        items(TIKTOK_STICKERS_LIST.chunked(4)) { rowStickers ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowStickers.forEach { (stickerIcon, stickerName) ->
                                    Card(
                                        modifier = Modifier
                                            .width(76.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                onSendSticker("Sticker: $stickerIcon $stickerName")
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                                        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp, brush = Brush.horizontalGradient(listOf(Color(0xFF333333), Color(0xFF333333))))
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text(text = stickerIcon, fontSize = 32.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = stickerName,
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // 3. TikTok Gifts (Coins + Icons)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Send TikTok Live & Chat Gifts",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 12.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🪙 50,000 Coins Available",
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        items(TIKTOK_GIFTS_LIST.chunked(4)) { rowGifts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowGifts.forEach { gift ->
                                    Card(
                                        modifier = Modifier
                                            .width(78.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                onSendGift(gift)
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                                        border = CardDefaults.outlinedCardBorder().copy(
                                            width = 1.dp,
                                            brush = Brush.linearGradient(listOf(gift.color.copy(alpha = 0.6f), Color(0xFF333333)))
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                        ) {
                                            Text(text = gift.icon, fontSize = 28.sp)
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = gift.name,
                                                fontSize = 10.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "🪙 ${gift.coins}",
                                                fontSize = 9.sp,
                                                color = Color(0xFFFFD700),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubbleRow(
    message: Message,
    peerAvatar: String,
    mediaAutoSave: Boolean,
    chatThemeGradient: List<Color> = listOf(Color(0xFF0084FF), Color(0xFF00C6FF)),
    onReactionSelected: (String) -> Unit,
    onOpenOptions: () -> Unit,
    onDownloadMedia: () -> Unit,
    aiChatEnabled: Boolean = true,
    isTranslated: Boolean = false,
    onTranslate: () -> Unit = {}
) {
    var show3DPopup by remember { mutableStateOf(false) }
    val tikTokTopReactions = remember { listOf("👍", "❤️", "🔥", "😂", "🥰", "👏", "✨", "💯", "🥺", "🎉") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        // TikTok Reaction Selector Bar
        if (show3DPopup) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(1.dp, Color(0xFFFE2C55), RoundedCornerShape(24.dp))
            ) {
                items(tikTokTopReactions) { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .clickable {
                                onReactionSelected(emoji)
                                show3DPopup = false
                            }
                            .padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!message.isMe) {
                AsyncImage(
                    model = message.senderAvatar,
                    contentDescription = message.senderName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            val isLikeMsg = message.content == "👍" || message.content.contains("BIG_LIKE")
            val isHeartMsg = message.content == "❤️" || message.content.contains("BIG_TIKTOK_HEART") || message.content.contains("TikTok Heart")
            val isCustomQuickEmojiMsg = message.content.contains("[BIG_MAX]") || message.content.contains("[BIG_MED]")
            val isGiftMsg = message.content.startsWith("🎁 Gift:")
            val isStickerMsg = message.content.startsWith("Sticker:")
            val isPureIconMsg = isLikeMsg || isHeartMsg || isCustomQuickEmojiMsg

            Box {
                Box(
                    modifier = Modifier
                        .clip(
                            if (isPureIconMsg) CircleShape
                            else RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (message.isMe) 16.dp else 4.dp,
                                bottomEnd = if (message.isMe) 4.dp else 16.dp
                            )
                        )
                        .then(
                            if (isPureIconMsg) {
                                Modifier.background(Color.Transparent)
                            } else if (isGiftMsg) {
                                Modifier
                                    .background(Color(0xFF1A1A1A))
                                    .border(1.dp, Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFE2C55))), RoundedCornerShape(16.dp))
                            } else if (message.isMe) {
                                Modifier.background(Color(0xFF10B981))
                            } else {
                                Modifier.background(Color(0xFF1E293B))
                            }
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { show3DPopup = !show3DPopup },
                                onLongPress = { onOpenOptions() }
                            )
                        }
                        .padding(if (isPureIconMsg) PaddingValues(0.dp) else PaddingValues(horizontal = 14.dp, vertical = 10.dp))
                ) {
                    Column {
                        if (message.content.startsWith("🎤") || message.content.contains("Voice Message") || message.content.contains("හඬ පණිවිඩය")) {
                            VoiceMessageBubbleContent(
                                messageContent = message.content,
                                isMe = message.isMe
                            )
                        } else if (message.content.contains("BIG_TIKTOK_HEART_MAX")) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Max TikTok Heart",
                                tint = Color(0xFFFE2C55),
                                modifier = Modifier.size(92.dp)
                            )
                        } else if (message.content.contains("BIG_TIKTOK_HEART_MED")) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Medium TikTok Heart",
                                tint = Color(0xFFFE2C55),
                                modifier = Modifier.size(68.dp)
                            )
                        } else if (message.content == "❤️" || message.content.contains("TikTok Heart")) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "TikTok Heart",
                                tint = Color(0xFFFE2C55),
                                modifier = Modifier.size(46.dp)
                            )
                        } else if (message.content.contains("BIG_LIKE_MAX")) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Max Like",
                                tint = Color(0xFF0084FF),
                                modifier = Modifier.size(92.dp)
                            )
                        } else if (message.content.contains("BIG_LIKE_MED")) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Medium Like",
                                tint = Color(0xFF0084FF),
                                modifier = Modifier.size(68.dp)
                            )
                        } else if (message.content == "👍") {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Like",
                                tint = Color(0xFF0084FF),
                                modifier = Modifier.size(46.dp)
                            )
                        } else if (isGiftMsg) {
                            // TikTok Live Gift Card in Chat
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                val giftParts = message.content.replace("🎁 Gift: ", "").split(" • ")
                                val giftTitle = giftParts.firstOrNull() ?: "Gift"
                                val giftCost = giftParts.getOrNull(1) ?: ""

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Brush.radialGradient(listOf(Color(0xFFFE2C55).copy(alpha = 0.4f), Color(0xFFFFD700).copy(alpha = 0.2f)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = giftTitle.substringAfter("(").substringBefore(")").ifEmpty { "🎁" },
                                        fontSize = 26.sp
                                    )
                                }

                                Column {
                                    Text(
                                        text = "TIKTOK LIVE GIFT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFE2C55)
                                    )
                                    Text(
                                        text = giftTitle.substringBefore(" ("),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (giftCost.isNotBlank()) {
                                        Text(
                                            text = "🪙 $giftCost",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                        } else if (isStickerMsg) {
                            // TikTok Sticker message
                            val stickerText = message.content.removePrefix("Sticker: ").trim()
                            val parts = stickerText.split(" ", limit = 2)
                            val icon = parts.firstOrNull() ?: "✨"
                            val label = parts.getOrNull(1) ?: ""

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Text(text = icon, fontSize = 42.sp)
                                if (label.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        } else if (message.content.contains("[BIG_MAX]") || message.content.contains("[BIG_MED]")) {
                            val emojiChar = message.content.substringBefore(" [").trim().ifEmpty { "👍" }
                            val isMax = message.content.contains("[BIG_MAX]")
                            com.example.animation.LiveAnimatedEmoji(
                                emoji = emojiChar,
                                fontSize = if (isMax) 62.sp else 46.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        } else if (message.content.startsWith("🎁 [Surprise Gift]: ")) {
                            com.example.ui.components.InteractiveSurpriseGiftCard(
                                messageContent = message.content,
                                onTriggerCelebration = {
                                    onReactionSelected("🎉")
                                }
                            )
                        } else if (message.content.startsWith("📊 [Poll]: ")) {
                            com.example.ui.components.InteractivePollCard(
                                messageContent = message.content
                            )
                        } else if (message.content.startsWith("GIF: ")) {
                            val clean = message.content.removePrefix("GIF: ")
                            val gifUrl = clean.substringBefore(" | ")
                            val gifTitle = clean.substringAfter(" | ").ifEmpty { "Messenger GIF" }
                            Column(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = gifUrl,
                                    contentDescription = gifTitle,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = gifTitle, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "GIF 🎬", color = Color(0xFF00C6FF), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        } else if (message.content.startsWith("🔊 [Soundmoji]: ")) {
                            val parts = message.content.removePrefix("🔊 [Soundmoji]: ").split(" - ")
                            val emoji = parts.firstOrNull()?.trim() ?: "🔊"
                            val desc = parts.getOrNull(1)?.trim() ?: "Sound Reaction"
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        com.example.ui.components.playSoundTone(emoji)
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = emoji, fontSize = 32.sp)
                                Column {
                                    Text(text = desc, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "▶ Tap to play sound", color = Color(0xFF00C6FF), fontSize = 10.sp)
                                }
                            }
                        } else if (message.content.startsWith("🎲 [Dice Roll]: ")) {
                            val roll = message.content.removePrefix("🎲 [Dice Roll]: ").trim()
                            val diceIcon = when (roll) {
                                "1" -> "⚀"
                                "2" -> "⚁"
                                "3" -> "⚂"
                                "4" -> "⚃"
                                "5" -> "⚄"
                                else -> "⚅"
                            }
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = diceIcon, fontSize = 38.sp)
                                Column {
                                    Text(text = "Dice Rolled: $roll 🎲", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = if (roll == "6") "Lucky Six! 🌟" else "Nice roll!", color = Color(0xFFFFD700), fontSize = 11.sp)
                                }
                            }
                        } else if (message.content.startsWith("🪙 [Coin Flip]: ")) {
                            val result = message.content.removePrefix("🪙 [Coin Flip]: ").trim()
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "🪙", fontSize = 34.sp)
                                Column {
                                    Text(text = "Coin Flip: $result", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Instant Fair Decision ✨", color = Color(0xFFFFD700), fontSize = 11.sp)
                                }
                            }
                        } else if (message.content.startsWith("🎱 [Magic 8-Ball]: ")) {
                            val answer = message.content.removePrefix("🎱 [Magic 8-Ball]: ").trim()
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "🎱", fontSize = 34.sp)
                                Column {
                                    Text(text = "Magic 8-Ball Answer", color = Color(0xFFA1A1AA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = answer, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        } else {
                            val urlRegex = Regex("""(https?://[^\s]+|content://[^\s]+|file://[^\s]+)""")
                            val match = urlRegex.find(message.content)
                            val extractedUrl = match?.value
                            val isImageUrl = extractedUrl != null && (
                                extractedUrl.contains(".jpg", ignoreCase = true) || extractedUrl.contains(".jpeg", ignoreCase = true) ||
                                extractedUrl.contains(".png", ignoreCase = true) || extractedUrl.contains(".gif", ignoreCase = true) ||
                                extractedUrl.contains(".webp", ignoreCase = true) || extractedUrl.contains("firebasestorage", ignoreCase = true) ||
                                extractedUrl.contains("unsplash", ignoreCase = true) || extractedUrl.contains("pexels", ignoreCase = true) ||
                                extractedUrl.contains("imgur", ignoreCase = true) || message.content.contains("📷")
                            )

                            val displayContent = if (isImageUrl && extractedUrl != null) {
                                message.content.replace(extractedUrl, "").replace("📷 [Snap]", "").replace("📷 [Photo]", "").replace("📷 Image Attachment:", "").trim()
                            } else {
                                message.content
                            }

                            if (displayContent.isNotBlank()) {
                                Text(
                                    text = if (isTranslated) "[AI Translated]: $displayContent" else displayContent,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = Color.White
                                )
                            }

                            val activeMediaUrl = message.mediaUrl?.ifBlank { null } ?: if (isImageUrl) extractedUrl else null
                            if (!activeMediaUrl.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                AsyncImage(
                                    model = activeMediaUrl,
                                    contentDescription = "Attached Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }

                        if (aiChatEnabled && !message.isMe && !message.content.contains("http://") && !message.content.contains("https://")) {
                            Text(
                                text = if (isTranslated) "Show Original" else "AI Translate",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF25F4EE),
                                modifier = Modifier
                                    .clickable { onTranslate() }
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Reaction Badge on message bubble
                if (!message.reaction.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(if (message.isMe) Alignment.BottomStart else Alignment.BottomEnd)
                            .graphicsLayer {
                                translationY = 12f
                                translationX = if (message.isMe) -12f else 12f
                            }
                            .clip(CircleShape)
                            .background(Color(0xFF242526))
                            .border(1.dp, Color(0xFF3A3B3C), CircleShape)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(text = message.reaction, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Message Timestamp & Options Trigger
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Encrypted",
                tint = Color(0xFFFE2C55).copy(alpha = 0.7f),
                modifier = Modifier.size(10.dp)
            )

            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color(0xFFAAAAAA)
            )

            if (message.isMe) {
                Text(
                    text = if (message.isSeen) "• Seen" else "• Delivered",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                    color = if (message.isSeen) Color(0xFF25F4EE) else Color(0xFF888888)
                )

                if (message.isSeen) {
                    AsyncImage(
                        model = peerAvatar,
                        contentDescription = "Seen by peer",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .border(0.5.dp, Color.White, CircleShape)
                    )
                }
            }

            Text(
                text = "• Options",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Color(0xFFFE2C55)),
                modifier = Modifier
                    .clickable { onOpenOptions() }
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageActionsSheet(
    message: Message,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF242526)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Share & Options 🚀",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // WhatsApp Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast.makeText(context, "WhatsApp එකට සාර්ථකව යවන ලදී! 💬", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                Spacer(modifier = Modifier.width(14.dp))
                Text(text = "WhatsApp එකට යවන්න (Share to WhatsApp)", color = Color.White, fontWeight = FontWeight.Medium)
            }

            // Save to Gallery
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast.makeText(context, "ඡායාරූපය/වීඩියෝව දුරකථන ගැලරියට සාර්ථකව බාගත විය! 📥", Toast.LENGTH_LONG).show()
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = Color(0xFF31A24C))
                Spacer(modifier = Modifier.width(14.dp))
                Text(text = "ගැලරියට සුරකින්න (Save to Gallery)", color = Color.White, fontWeight = FontWeight.Medium)
            }

            // Copy Text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Message", message.content)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied! 📋", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFFF7B928))
                Spacer(modifier = Modifier.width(14.dp))
                Text(text = "Copy Text", color = Color.White, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SubtleChatConnectedNote() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "දැන් ඔබ දෙදෙනාට එකිනෙකා සමඟ කතාබස් හුවමාරු කළ හැකිය",
            color = Color(0xFFF8FAFC),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    Color(0xFF1E293B).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun TypingIndicatorFBStyle(peerAvatar: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = peerAvatar,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(OledSurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
fun IncomingCallMessengerOverlay(
    call: com.example.model.CallLogItem,
    onAnswer: () -> Unit,
    onDecline: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = call.peerAvatar,
                contentDescription = call.peerName,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(3.dp, com.example.ui.theme.NeonBlue, CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = call.peerName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "Messenger Video Call Ringing...",
                style = MaterialTheme.typography.bodyMedium,
                color = com.example.ui.theme.NeonBlue
            )
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Decline
                IconButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.HeartRed)
                ) {
                    Icon(imageVector = Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White)
                }
                // Answer
                IconButton(
                    onClick = onAnswer,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.OnlineGreen)
                ) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.Call, contentDescription = "Answer", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ActiveCallDialog(
    call: com.example.model.CallLogItem,
    onEndCall: () -> Unit,
    messages: List<Message> = emptyList(),
    onSendMessage: (String) -> Unit = {}
) {
    com.example.ui.components.call.EnhancedActiveCallDialog(
        call = call,
        onEndCall = onEndCall,
        messages = messages,
        onSendMessage = onSendMessage
    )
    if (false) {
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isRecording by remember { mutableStateOf(false) }
    var isSharingScreen by remember { mutableStateOf(false) }
    var showChatOverlay by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onEndCall,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Main Peer Camera View Simulation
            if (!isVideoOff) {
                AsyncImage(
                    model = call.peerAvatar,
                    contentDescription = call.peerName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OledSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = call.peerAvatar,
                            contentDescription = call.peerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = call.peerName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            // Screen Share Overlay Indicator
            if (isSharingScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NeonBlue.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PresentToAll, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                        Text("You are sharing your screen", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Top Status Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Encrypted", tint = NeonPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("End-to-End Encrypted HD Call", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${call.peerName} • 00:42",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonBlue
                )
            }

            // Self Camera Inset (Top Right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 20.dp)
                    .size(110.dp, 160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, NeonBlue, RoundedCornerShape(16.dp))
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                    contentDescription = "Self Camera",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Chat Overlay
            AnimatedVisibility(
                visible = showChatOverlay,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, top = 80.dp, bottom = 120.dp)
                    .width(280.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("In-Call Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            IconButton(onClick = { showChatOverlay = false }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                        
                        LazyColumn(
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth(),
                            reverseLayout = true
                        ) {
                            items(messages.reversed()) { msg ->
                                Text(
                                    text = "${if(msg.isMe) "Me" else call.peerName}: ${msg.content}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Message...", fontSize = 11.sp, color = Color.Gray) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonBlue,
                                    unfocusedBorderColor = Color.Gray
                                )
                            )
                            IconButton(onClick = {
                                if (chatInput.isNotBlank()) {
                                    onSendMessage(chatInput)
                                    chatInput = ""
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NeonBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Bottom Call Control Buttons Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(bottom = 24.dp, top = 12.dp)
            ) {
                // Feature Row (Speaker, Record, Share, Chat)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ControlSmallButton(
                        icon = if (isSpeakerOn) Icons.Default.Speaker else Icons.Default.Cast,
                        label = "Speaker",
                        active = isSpeakerOn,
                        onClick = { isSpeakerOn = !isSpeakerOn }
                    )
                    ControlSmallButton(
                        icon = Icons.Default.RecordVoiceOver,
                        label = "Record",
                        active = isRecording,
                        onClick = { isRecording = !isRecording }
                    )
                    ControlSmallButton(
                        icon = Icons.Default.PresentToAll,
                        label = "Share",
                        active = isSharingScreen,
                        onClick = { isSharingScreen = !isSharingScreen }
                    )
                    ControlSmallButton(
                        icon = Icons.Default.ChatBubble,
                        label = "Chat",
                        active = showChatOverlay,
                        onClick = { showChatOverlay = !showChatOverlay }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) HeartRed else OledSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }

                    // End Call
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
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Video Toggle
                    IconButton(
                        onClick = { isVideoOff = !isVideoOff },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isVideoOff) HeartRed else OledSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Video",
                            tint = Color.White
                        )
                    }

                    // Switch Camera
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(OledSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwitchCamera,
                            contentDescription = "Flip Camera",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ChatFloatingReactionsOverlay(
    activeEmoji: String,
    triggerCount: Int
) {
    if (triggerCount <= 0 || activeEmoji.isBlank()) return

    val isBlueLike = activeEmoji.contains("👍")

    val particles = remember(triggerCount) {
        List(8) { index ->
            ChatReactionParticle(
                id = index,
                startXFraction = (0.15f + (index * 0.1f)) % 0.85f,
                speedMultiplier = 0.85f + (index % 4) * 0.15f,
                delayMs = (index * 35) % 200,
                scale = 0.85f + (index % 3) * 0.2f
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        particles.forEach { particle ->
            var startAnim by remember(triggerCount) { mutableStateOf(false) }

            LaunchedEffect(triggerCount) {
                kotlinx.coroutines.delay(particle.delayMs.toLong())
                startAnim = true
            }

            val offsetY by animateFloatAsState(
                targetValue = if (startAnim) -1100f else 0f,
                animationSpec = tween(
                    durationMillis = (1600 * particle.speedMultiplier).toInt(),
                    easing = LinearEasing
                ),
                label = "chatParticleY"
            )

            val alpha by animateFloatAsState(
                targetValue = if (startAnim) 0f else 1f,
                animationSpec = tween(
                    durationMillis = (1600 * particle.speedMultiplier).toInt(),
                    easing = FastOutSlowInEasing
                ),
                label = "chatParticleAlpha"
            )

            if (startAnim && alpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .graphicsLayer {
                            translationX = particle.startXFraction * 900f
                            translationY = offsetY
                            this.alpha = alpha
                        }
                ) {
                    if (isBlueLike) {
                        Box(
                            modifier = Modifier
                                .size((30 * particle.scale).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0084FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size((18 * particle.scale).dp)
                            )
                        }
                    } else {
                        Text(
                            text = activeEmoji,
                            fontSize = (28 * particle.scale).sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ControlSmallButton(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (active) NeonBlue else OledSurfaceVariant.copy(alpha = 0.5f))
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = if (active) Color.Black else Color.White, modifier = Modifier.size(20.dp))
        }
        Text(text = label, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun VoiceMessageBubbleContent(
    messageContent: String,
    isMe: Boolean
) {
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            playbackProgress = 0f
            while (isPlaying && playbackProgress < 1f) {
                kotlinx.coroutines.delay(100L)
                playbackProgress += 0.04f
            }
            isPlaying = false
            playbackProgress = 0f
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isMe) Color.Black.copy(alpha = 0.25f) else Color(0xFF0084FF))
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause Voice",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(22.dp)
            ) {
                val waveformBars = listOf(12, 22, 14, 28, 18, 30, 20, 10, 24, 16, 26, 14)
                waveformBars.forEachIndexed { idx, heightDp ->
                    val activeBar = idx < (waveformBars.size * playbackProgress)
                    val barColor = if (isMe) {
                        if (activeBar) Color.Black else Color.Black.copy(alpha = 0.35f)
                    } else {
                        if (activeBar) Color(0xFF0084FF) else Color.Gray.copy(alpha = 0.5f)
                    }
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(heightDp.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(barColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = messageContent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isMe) Color.Black.copy(alpha = 0.8f) else Color.LightGray
            )
        }
    }
}
