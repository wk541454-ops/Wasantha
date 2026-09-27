package com.example.model

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80",
    val bio: String = "",
    val work: String = "Software Engineer",
    val education: String = "University Graduate",
    val location: String = "Colombo, Sri Lanka",
    val hometown: String = "Colombo, Sri Lanka",
    val birthday: String = "",
    val relationshipStatus: String = "Single",
    val email: String = "user@friendhub.io",
    val phone: String = "+94 77 123 4567",
    val website: String = "https://friendhub.io",
    val isOnline: Boolean = true,
    val lastActiveAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false,
    val followersCount: Int = 1240,
    val followingCount: Int = 380,
    val postsCount: Int = 10,
    val profileFrame: String? = null,
    val profileNote: String? = null,
    val isProfileLocked: Boolean = true,
    val isFriend: Boolean = false,
    val isFollowedByMe: Boolean = true,
    val isRequestReceived: Boolean = false,
    val isRequestSent: Boolean = false,
    val mutualFriendsCount: Int = 0,
    val mutualFriendsPreview: List<String> = emptyList(),
    val mutualThingsText: String? = null,
    val workDetails: String? = null,
    val categoryBadge: String? = null,
    val workplace2: String? = null,
    val isSnoozed: Boolean = false,
    val isBreakTaken: Boolean = false,
    val hideReactionCounts: Boolean = false,
    val pushNotificationsEnabled: Boolean = true,
    val commentsNotificationsEnabled: Boolean = true,
    val tagsNotificationsEnabled: Boolean = true,
    val friendRequestsNotificationsEnabled: Boolean = true,
    val doNotDisturb: Boolean = false,
    val language: String = "SI",
    val autoUpdateOnWifi: Boolean = true,
    val hdVideoUpload: Boolean = true,
    val hdPhotoUpload: Boolean = true,
    val defaultPostAudience: String = "Public",
    val friendRequestAudience: String = "Everyone",
    val friendsListAudience: String = "Friends",
    val storyPrivacy: String = "Friends",
    val storyArchiveEnabled: Boolean = true,
    val whoCanPostOnProfile: String = "Friends",
    val reviewTagsEnabled: Boolean = true,
    val friendsCount: Int = 248,
    val languages: String = "English, Sinhala",
    val interests: String = "Photography, Technology, Travel, Music",
    val hobbies: String = "Hiking, Coding, Reading, Gaming",
    val occupation: String = "Software Engineer",
    val fieldPrivacy: Map<String, String> = emptyMap(),
    val lastActivePrivacy: String = "FRIENDS",
    // --- Creator Unlock Program & Monetization ---
    val isVerifiedCreator: Boolean = false, // VIP Gold Verification Checkmark (Unlocked >= 5000 followers)
    val creatorMonetizationEnabled: Boolean = false,
    val is4kUploadUnlocked: Boolean = false,
    val isBioLinkUnlocked: Boolean = false,
    val isLiveMonetizationUnlocked: Boolean = false,
    val isBusinessSubscribed: Boolean = false,
    val coinBalance: Int = 500,
    val followsTodayCount: Int = 12,
    val lastFollowActionTimestamp: Long = System.currentTimeMillis(),
    val isBlockedByMe: Boolean = false,
    val blockedUserIds: List<String> = emptyList(),
    val isBanned: Boolean = false,
    val violationCount: Int = 0
)

enum class MediaType {
    NONE, IMAGE, VIDEO
}

enum class ReactionType(val emoji: String, val label: String) {
    NONE("👍", "Like"),
    LIKE("👍", "Like"),
    LOVE("❤️", "Love"),
    HAHA("😆", "Haha"),
    WOW("😮", "Wow"),
    SAD("😢", "Sad"),
    ANGRY("😡", "Angry"),
    FIRE("🔥", "Fire"),
    PARTY("🎉", "Party"),
    PRAY("🙏", "Pray"),
    HUNDRED("💯", "100"),
    STAR("⭐", "Star"),
    COOL("😎", "Cool"),
    THINKING("🤔", "Thinking"),
    SMILE("😊", "Smile"),
    DISLIKE("👎", "Dislike"),
    CLAP("👏", "Clap"),
    ROCKET("🚀", "Rocket"),
    EYES("👀", "Eyes"),
    SHUSH("🤫", "Shush"),
    MIND_BLOWN("🤯", "Mind Blown"),
    SLEEPY("😴", "Sleepy"),
    NERD("🤓", "Nerd"),
    DEVIL("😈", "Devil"),
    ALIEN("👽", "Alien"),
    GHOST("👻", "Ghost")
}

data class Reel(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val userName: String = "",
    val creatorName: String = "",
    val creatorAvatar: String = "",
    val videoUrl: String = "",
    val caption: String = "",
    val likesCount: String = "12.5K",
    val viewsCount: String = "45K"
)

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val timestamp: String = "Just now",
    val likes: Int = 0,
    val repliesCount: Int = 0,
    val reactionSummary: String? = null,
    val isFlagged: Boolean = false,
    val flagReason: String? = null
)

enum class FactCheckStatus {
    NONE, VERIFIED, FAKE_NEWS
}

data class Post(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val userVerified: Boolean = false,
    val timestamp: String = "Just now",
    val content: String = "",
    val mediaUrl: String? = null,
    val mediaType: MediaType = MediaType.NONE,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val selectedReaction: ReactionType = ReactionType.NONE,
    val isSaved: Boolean = false,
    val isEncrypted: Boolean = true,
    val comments: List<Comment> = emptyList(),
    val reactionSummary: String? = null,
    val targetUserName: String? = null,
    val feeling: String? = null,
    val isOnline: Boolean = false,
    val aiSummary: String? = null,
    val factCheckStatus: FactCheckStatus = FactCheckStatus.NONE,
    val audience: String = "Public",
    val isPinned: Boolean = false,
    val albumName: String? = null,
    val isFlagged: Boolean = false,
    val flagReason: String? = null,
    val sharedWithUserIds: List<String> = emptyList()
)

data class StoryViewer(
    val userId: String = UUID.randomUUID().toString(),
    val userName: String = "",
    val userAvatar: String = "",
    val reactionEmoji: String? = null,
    val timestamp: String = "Just now"
)

data class Story(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val mediaUrl: String? = null,
    val backgroundColor: String? = null,
    val caption: String = "",
    val timestamp: String = "2h ago",
    val hasUnseen: Boolean = true,
    val isMe: Boolean = false,
    val isLive: Boolean = false,
    val likeCount: Int = 0,
    val reactionSummary: String? = null,
    val viewersCount: Int = 0,
    val viewers: List<StoryViewer> = emptyList()
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val content: String = "",
    val mediaUrl: String? = null,
    val timestamp: String = "10:42 AM",
    val isMe: Boolean = false,
    val isEncrypted: Boolean = true,
    val reaction: String? = null,
    val isSeen: Boolean = true,
    val seenTimestamp: String? = "10:43 AM",
    val isMediaDownloaded: Boolean = true,
    val isTikTokCard: Boolean = false,
    val cardMessage: String? = null,
    val isFlagged: Boolean = false,
    val flagReason: String? = null
)

data class ChatSummary(
    val id: String = UUID.randomUUID().toString(),
    val peerUserId: String? = null,
    val peerName: String = "",
    val peerAvatar: String = "",
    val lastMessage: String = "",
    val lastTimestamp: String = "10:42 AM",
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isLive: Boolean = false,
    val lastSeen: String = "Active now",
    val isGroup: Boolean = false,
    val memberAvatars: List<String> = emptyList(),
    val isConnected: Boolean = false,
    val participantIds: List<String> = emptyList()
)

data class MarketplaceItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val price: Double = 0.0,
    val category: String = "General",
    val imageUrl: String = "",
    val sellerName: String = "",
    val sellerAvatar: String = "",
    val location: String = "San Francisco, CA",
    val description: String = "",
    val timestamp: String = "Today",
    val isSaved: Boolean = false
)

data class CallLogItem(
    val id: String = UUID.randomUUID().toString(),
    val peerName: String = "",
    val peerAvatar: String = "",
    val type: String = "VIDEO", // VIDEO or AUDIO
    val direction: String = "INCOMING", // INCOMING, OUTGOING, MISSED
    val timestamp: String = "10 mins ago",
    val duration: String = "04:12",
    val isGroup: Boolean = false,
    val memberAvatars: List<String> = emptyList()
)

data class FriendRequest(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val userName: String = "",
    val handle: String = "",
    val userAvatar: String = "",
    val subtitle: String = "",
    val bio: String = "",
    val tagBadge: String = "Suggested for you",
    val mutualFriends: Int = 3,
    val mutualFriendAvatars: List<String> = emptyList(),
    val videoThumbnails: List<String> = emptyList(),
    val timestamp: String = "2w",
    val isFollowing: Boolean = false
)

enum class NotificationType {
    LIKE, COMMENT, FRIEND_REQUEST, ACCEPT_REQUEST, REACTION,
    FRIEND_NEW_POST, FRIEND_NEW_STATUS, SECURITY_LOGIN_ALERT,
    PERMISSION_ALERT, MENTION, LIVE_STREAM, SYSTEM_ALERT
}

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val type: NotificationType = NotificationType.LIKE,
    val senderName: String = "",
    val senderAvatar: String = "",
    val message: String = "",
    val timestamp: String = "2 hours ago",
    val isRead: Boolean = false,
    val postPreviewUrl: String? = null,
    val targetId: String? = null
)

data class ReportRecord(
    val id: String = UUID.randomUUID().toString(),
    val targetType: String = "POST", // POST, USER, COMMENT, MARKETPLACE_ITEM, LIVE_STREAM, CHAT
    val targetId: String = "",
    val reason: String = "",
    val additionalDetails: String = "",
    val reportedAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING_REVIEW" // PENDING_REVIEW, RESOLVED, DISMISSED
)

data class ProfileVisitor(
    val id: String = UUID.randomUUID().toString(),
    val visitorId: String = "",
    val visitorName: String = "",
    val visitorAvatar: String = "",
    val visitorBio: String = "Photography & Tech enthusiast 📸",
    val timestamp: String = "5m ago",
    val viewedAtMillis: Long = System.currentTimeMillis()
)

data class HighlightCategory(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val coverUrl: String = "",
    val storiesCount: Int = 3
)
