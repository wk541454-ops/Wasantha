package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.BusinessPage
import com.example.model.ChatSummary
import com.example.viewmodel.MainViewModel

@Composable
fun BusinessMessagingScreen(viewModel: MainViewModel, businessContext: BusinessPage) {
    val activeChat by viewModel.activeChat.collectAsState()
    val activeChatMessages by viewModel.activeChatMessages.collectAsState()
    val isPeerTyping by viewModel.isPeerTyping.collectAsState()
    val aiChatAssistantEnabled by viewModel.aiChatAssistantEnabled.collectAsState()
    val mediaAutoSave by viewModel.mediaAutoSave.collectAsState()

    if (activeChat != null) {
        com.example.ui.screens.ChatDetailView(
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
        return
    }

    val allChats by viewModel.chats.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredChats = allChats.filter {
        it.peerName.contains(searchQuery, ignoreCase = true) ||
        it.lastMessage.contains(searchQuery, ignoreCase = true)
    }

    // Inbox view
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Slate 900
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp)) {
                    AsyncImage(
                        model = businessContext.imageUrl,
                        contentDescription = "Business Avatar",
                        modifier = Modifier.fillMaxSize().clip(CircleShape).border(1.dp, Color(0xFF10B981), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(1.5.dp, Color(0xFF0F172A), CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Chats 💬",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC),
                    fontFamily = com.example.ui.theme.PlusJakartaSansFamily
                )
            }
            Text(
                text = "${filteredChats.count { it.unreadCount > 0 }} Unread",
                fontSize = 13.sp,
                color = Color(0xFF10B981),
                fontWeight = FontWeight.SemiBold,
                fontFamily = com.example.ui.theme.PlusJakartaSansFamily
            )
        }
        
        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                textStyle = TextStyle(color = Color(0xFFF8FAFC), fontSize = 14.sp, fontFamily = com.example.ui.theme.PlusJakartaSansFamily),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text("Search conversations...", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )
        }

        // Active Now horizontal avatars row removed for cleaner Business Messaging
        
        HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredChats) { chat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (chat.unreadCount > 0) Color(0xFF1E293B).copy(alpha = 0.5f) else Color.Transparent)
                        .clickable { viewModel.openChat(chat) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(52.dp)) {
                        AsyncImage(
                            model = chat.peerAvatar,
                            contentDescription = chat.peerName,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        if (chat.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(13.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                                    .border(2.dp, Color(0xFF0F172A), CircleShape)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(14.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            chat.peerName,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color(0xFFF8FAFC),
                            fontFamily = com.example.ui.theme.PlusJakartaSansFamily
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            chat.lastMessage,
                            color = if (chat.unreadCount > 0) Color(0xFFF8FAFC) else Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            maxLines = 1,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            chat.lastTimestamp,
                            color = if (chat.unreadCount > 0) Color(0xFF10B981) else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                        if (chat.unreadCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${chat.unreadCount}",
                                    color = Color(0xFF0F172A),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFF1E293B).copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}
