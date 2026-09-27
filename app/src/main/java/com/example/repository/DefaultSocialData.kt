package com.example.repository

import com.example.model.*

object DefaultSocialData {

    val currentUser = User(
        id = "",
        name = "",
        username = "",
        avatarUrl = "",
        coverUrl = "",
        bio = "",
        work = "",
        education = "",
        location = "",
        hometown = "",
        relationshipStatus = "",
        email = "",
        phone = "",
        website = "",
        isOnline = false,
        isVerified = false,
        followersCount = 0,
        followingCount = 0
    )

    val friends = emptyList<User>()
    val posts = emptyList<Post>()
    val stories = emptyList<Story>()
    val reels = emptyList<Reel>()
    val chats = emptyList<ChatSummary>()
    val messages = emptyMap<String, List<Message>>()
    val marketplaceItems = emptyList<MarketplaceItem>()
    val callLogs = emptyList<CallLogItem>()
    val friendRequests = emptyList<FriendRequest>()
    val notifications = emptyList<NotificationItem>()
    val profileVisitors = emptyList<ProfileVisitor>()
    val storyHighlights = emptyList<HighlightCategory>()
}
