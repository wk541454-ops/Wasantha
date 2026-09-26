package com.example.model

import java.util.UUID

data class LiveChatMessage(
    val userName: String,
    val message: String,
    val isSystem: Boolean = false,
    val isMe: Boolean = false,
    val id: String = UUID.randomUUID().toString()
)

data class LiveGift(
    val name: String,
    val icon: String,
    val timestamp: Long,
    val id: String = UUID.randomUUID().toString()
)

data class LiveSession(
    val id: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val hostAvatar: String = "",
    val title: String = "",
    val category: String = "",
    val viewerCount: Int = 0,
    val startedAt: Long = 0,
    val status: String = "PRE_LIVE", // PRE_LIVE, LIVE, ENDED
    val seats: List<LiveSeat> = emptyList()
)

data class LiveSeat(
    val id: Int = 0,
    val name: String = "",
    val isHost: Boolean = false,
    val isOccupied: Boolean = false,
    val coins: Int = 0,
    val isMuted: Boolean = false,
    val isSpeaking: Boolean = false,
    val avatarUrl: String = ""
)
