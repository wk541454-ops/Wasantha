package com.example.repository

import android.util.Log
import com.example.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FriendHubRepository {

    private val TAG = "FriendHubRepo"

    private val _currentUser = MutableStateFlow(DefaultSocialData.currentUser)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _posts = MutableStateFlow(DefaultSocialData.posts)
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _businessPosts = MutableStateFlow<List<Post>>(emptyList())
    val businessPosts: StateFlow<List<Post>> = _businessPosts.asStateFlow()

    private val _stories = MutableStateFlow(DefaultSocialData.stories)
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _reels = MutableStateFlow(DefaultSocialData.reels)
    val reels: StateFlow<List<Reel>> = _reels.asStateFlow()

    private val _chats = MutableStateFlow(DefaultSocialData.chats)
    val chats: StateFlow<List<ChatSummary>> = _chats.asStateFlow()

    private val _messages = MutableStateFlow(DefaultSocialData.messages)

    private val _marketplaceItems = MutableStateFlow(DefaultSocialData.marketplaceItems)
    val marketplaceItems: StateFlow<List<MarketplaceItem>> = _marketplaceItems.asStateFlow()

    private val _callLogs = MutableStateFlow(DefaultSocialData.callLogs)
    val callLogs: StateFlow<List<CallLogItem>> = _callLogs.asStateFlow()

    private val _friendRequests = MutableStateFlow(DefaultSocialData.friendRequests)
    val friendRequests: StateFlow<List<FriendRequest>> = _friendRequests.asStateFlow()

    private val _knownUsers = MutableStateFlow<Map<String, User>>(
        DefaultSocialData.friends.associateBy { it.id }
    )
    val knownUsers: StateFlow<Map<String, User>> = _knownUsers.asStateFlow()

    private val _allFriends = MutableStateFlow(DefaultSocialData.friends)
    val allFriends: StateFlow<List<User>> = _allFriends.asStateFlow()

    private val _notifications = MutableStateFlow(DefaultSocialData.notifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseFirestore: FirebaseFirestore? = null

    private val _isPeerTyping = MutableStateFlow(false)
    val isPeerTyping: StateFlow<Boolean> = _isPeerTyping.asStateFlow()

    private val _incomingCall = MutableStateFlow<CallLogItem?>(null)
    val incomingCall: StateFlow<CallLogItem?> = _incomingCall.asStateFlow()

    private val _userReports = MutableStateFlow<List<ReportRecord>>(emptyList())
    val userReports: StateFlow<List<ReportRecord>> = _userReports.asStateFlow()

    private val _profileVisitors = MutableStateFlow(DefaultSocialData.profileVisitors)
    val profileVisitors: StateFlow<List<ProfileVisitor>> = _profileVisitors.asStateFlow()

    fun recordProfileVisitor(user: User) {
        if (user.id == _currentUser.value.id) return
        val existing = _profileVisitors.value.filter { it.visitorId != user.id }
        val newVisitor = ProfileVisitor(
            id = UUID.randomUUID().toString(),
            visitorId = user.id,
            visitorName = user.name,
            visitorAvatar = user.avatarUrl,
            visitorBio = user.bio.ifBlank { "Viewed your profile recently" },
            timestamp = "Just now"
        )
        _profileVisitors.value = listOf(newVisitor) + existing
        
        addNotification(
            NotificationItem(
                id = "notif_pv_${System.currentTimeMillis()}",
                type = NotificationType.SYSTEM_ALERT,
                senderName = user.name,
                senderAvatar = user.avatarUrl,
                message = "👀 ${user.name} viewed your profile!",
                timestamp = "Just now"
            )
        )
    }

    fun simulateProfileVisitor() {
        val pool = _allFriends.value.ifEmpty { _knownUsers.value.values.toList() }
        val candidate = pool.randomOrNull() ?: User(
            id = "sim_user_${System.currentTimeMillis() % 1000}",
            name = "Sarah Jenkins",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            bio = "Digital Creator & Traveler ✨",
            isFriend = true
        )
        recordProfileVisitor(candidate)
    }

    // AI Configuration Flags
    var isAiModerationEnabled: Boolean = true
    var isAiVerificationEnabled: Boolean = true

    init {
        initFirebase()
    }

    private fun initFirebase() {
        try {
            firebaseAuth = try { FirebaseAuth.getInstance() } catch (t: Throwable) { null }
            firebaseFirestore = try { FirebaseFirestore.getInstance() } catch (t: Throwable) { null }

            if (firebaseAuth != null && firebaseFirestore != null) {
                try {
                    com.google.firebase.messaging.FirebaseMessaging.getInstance().isAutoInitEnabled = false
                } catch (t: Throwable) {
                    Log.w(TAG, "FCM auto-init skip: ${t.message}")
                }

                Log.d(TAG, "Firebase successfully connected!")
                setupFirebaseAuthListener()
                setupFirestoreRealtimeListeners()
            } else {
                Log.w(TAG, "Firebase services not available - running in offline/mock mode.")
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Firebase initialization fallback: ${t.message}")
        }
    }

    private fun setupFirebaseAuthListener() {
        val auth = firebaseAuth ?: return
        val fs = firebaseFirestore ?: return

        auth.addAuthStateListener { firebaseAuthInstance ->
            val user = firebaseAuthInstance.currentUser
            if (user != null) {
                fs.collection("users").document(user.uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            try {
                                val loadedUser = doc.toObject(User::class.java)
                                if (loadedUser != null) {
                                    _currentUser.value = loadedUser.copy(id = user.uid)
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "Failed to parse current user: ${e.message}")
                            }
                        }
                    }
            }
        }
    }

    private fun setupFirestoreRealtimeListeners() {
        val fs = firebaseFirestore ?: return
        try {
            // 1. Real-time Posts Collection
            fs.collection("posts")
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Posts listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot == null) return@addSnapshotListener

                    val remotePosts = snapshot.documents.mapNotNull { doc ->
                        try {
                            val post = doc.toObject(Post::class.java)
                            post?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (remotePosts.isNotEmpty()) {
                        val friendIds = _allFriends.value.map { it.id }
                        _posts.value = com.example.util.FriendHubSecurityController.filterVisiblePosts(
                            remotePosts,
                            _currentUser.value.id,
                            friendIds
                        )
                    } else if (snapshot.isEmpty) {
                        seedStarterDataToCloud(fs)
                    }
                }

            // 2. Real-time Stories Collection
            fs.collection("stories")
                .limit(30)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val remoteStories = snapshot.documents.mapNotNull { doc ->
                        try {
                            val story = doc.toObject(Story::class.java)
                            story?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteStories.isNotEmpty()) {
                        _stories.value = remoteStories
                    }
                }

            // 3. Real-time Reels Collection
            fs.collection("reels")
                .limit(30)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val remoteReels = snapshot.documents.mapNotNull { doc ->
                        try {
                            val reel = doc.toObject(Reel::class.java)
                            reel?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteReels.isNotEmpty()) {
                        _reels.value = remoteReels
                    }
                }

            // 4. Real-time Chats Collection
            fs.collection("chats")
                .limit(40)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val remoteChats = snapshot.documents.mapNotNull { doc ->
                        try {
                            val chat = doc.toObject(ChatSummary::class.java)
                            chat?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteChats.isNotEmpty()) {
                        _chats.value = remoteChats
                    }
                }

            // 5. Real-time Marketplace Collection
            fs.collection("marketplace")
                .limit(30)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val remoteItems = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(MarketplaceItem::class.java)
                            item?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteItems.isNotEmpty()) {
                        _marketplaceItems.value = remoteItems
                    }
                }

            // 6. Real-time Friend Requests
            fs.collection("friend_requests")
                .limit(20)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val remoteReqs = snapshot.documents.mapNotNull { doc ->
                        try {
                            val req = doc.toObject(FriendRequest::class.java)
                            req?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteReqs.isNotEmpty()) {
                        _friendRequests.value = remoteReqs
                    }
                }

            // 7. Real-time Friends
            fs.collection("friends")
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val remoteFriends = snapshot.documents.mapNotNull { doc ->
                        try {
                            val friend = doc.toObject(User::class.java)
                            friend?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteFriends.isNotEmpty()) {
                        _allFriends.value = remoteFriends
                    }
                }

            // 8. Real-time Notifications
            fs.collection("notifications")
                .limit(30)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val remoteNotifications = snapshot.documents.mapNotNull { doc ->
                        try {
                            val notif = doc.toObject(NotificationItem::class.java)
                            notif?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteNotifications.isNotEmpty()) {
                        _notifications.value = remoteNotifications
                    }
                }

        } catch (e: Exception) {
            Log.e(TAG, "Error setting up Firestore real-time listeners: ${e.message}")
        }
    }

    private fun seedStarterDataToCloud(fs: FirebaseFirestore) {
        val me = _currentUser.value
        val starterPost = Post(
            id = "welcome_post_1",
            userId = me.id,
            userName = me.name,
            userAvatar = me.avatarUrl,
            userVerified = true,
            timestamp = "Just now",
            content = "Welcome to FriendHub! 🚀 Connect with friends, share stories, make HD video calls, and explore Marketplace. Real-time Cloud Database is now active!",
            mediaUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=1000&auto=format&fit=crop&q=80",
            mediaType = MediaType.IMAGE,
            likeCount = 12,
            commentCount = 2,
            shareCount = 4,
            isLikedByMe = false,
            comments = listOf(
                Comment(
                    id = "c_welcome_1",
                    postId = "welcome_post_1",
                    userId = "system_bot",
                    userName = "FriendHub Team",
                    userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&auto=format&fit=crop&q=80",
                    content = "Glad to have you here! All updates synchronize live across all devices.",
                    timestamp = "Just now"
                )
            )
        )

        val starterStory = Story(
            id = "welcome_story_1",
            userId = me.id,
            userName = me.name,
            userAvatar = me.avatarUrl,
            mediaUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            caption = "Live on FriendHub! ✨",
            hasUnseen = true,
            isMe = true
        )

        try {
            fs.collection("posts").document(starterPost.id).set(starterPost)
            fs.collection("stories").document(starterStory.id).set(starterStory)
        } catch (e: Exception) {
            Log.w(TAG, "Failed seeding starter cloud data: ${e.message}")
        }
    }

    fun refreshPosts() {
        val fs = firebaseFirestore ?: return
        fs.collection("posts").limit(50).get()
            .addOnSuccessListener { snapshot ->
                val fetched = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(Post::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                }
                if (fetched.isNotEmpty()) {
                    _posts.value = fetched
                }
            }
    }

    fun getUserById(userId: String): User? {
        val me = _currentUser.value
        if (userId == me.id) return me
        val rawUser = _allFriends.value.find { it.id == userId } ?: _knownUsers.value[userId] ?: return null
        val isFriend = _allFriends.value.any { it.id == userId } || rawUser.isFriend
        return com.example.util.FriendHubSecurityController.protectUserDataPrivacy(rawUser, me.id, isFriend)
    }

    fun getUserByName(name: String): User? {
        if (name.equals(_currentUser.value.name, ignoreCase = true)) return _currentUser.value
        return _allFriends.value.find { it.name.equals(name, ignoreCase = true) }
    }

    fun acceptFriendRequest(requestId: String) {
        val req = _friendRequests.value.find { it.id == requestId }
        _friendRequests.value = _friendRequests.value.filter { it.id != requestId }

        if (req != null) {
            val newFriend = User(
                id = req.userId,
                name = req.userName,
                avatarUrl = req.userAvatar,
                bio = req.bio,
                isFriend = true,
                isOnline = true
            )
            _allFriends.value = listOf(newFriend) + _allFriends.value

            try {
                firebaseFirestore?.collection("friend_requests")?.document(requestId)?.delete()
                firebaseFirestore?.collection("friends")?.document(newFriend.id)?.set(newFriend)
            } catch (e: Exception) {
                Log.w(TAG, "Firestore friend accept failed: ${e.message}")
            }
        }
    }

    fun deleteFriendRequest(requestId: String) {
        _friendRequests.value = _friendRequests.value.filter { it.id != requestId }
        try {
            firebaseFirestore?.collection("friend_requests")?.document(requestId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore delete request failed: ${e.message}")
        }
    }

    fun rejectFriendRequest(requestId: String) {
        deleteFriendRequest(requestId)
    }

    fun toggleFollowFriendRequest(requestId: String) {
        _friendRequests.value = _friendRequests.value.map {
            if (it.id == requestId) it.copy(isFollowing = !it.isFollowing) else it
        }
    }

    fun unfriendUser(userId: String) {
        _allFriends.value = _allFriends.value.filter { it.id != userId }
        try {
            firebaseFirestore?.collection("friends")?.document(userId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore unfriend failed: ${e.message}")
        }
    }

    fun sendFriendRequest(userId: String) {
        val me = _currentUser.value
        val newReq = FriendRequest(
            id = "req_${System.currentTimeMillis()}",
            userId = me.id,
            userName = me.name,
            userAvatar = me.avatarUrl,
            subtitle = "Sent you a friend request"
        )
        try {
            firebaseFirestore?.collection("friend_requests")?.document(newReq.id)?.set(newReq)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore send friend request failed: ${e.message}")
        }
    }

    fun cancelFriendRequest(userId: String) {
        _friendRequests.value = _friendRequests.value.filter { it.userId != userId }
    }

    fun toggleFollowUser(userId: String): Boolean {
        var isFollowing = false
        _allFriends.value = _allFriends.value.map { user ->
            if (user.id == userId) {
                val newStatus = !user.isFollowedByMe
                isFollowing = newStatus
                user.copy(isFollowedByMe = newStatus)
            } else user
        }
        return isFollowing
    }

    fun snoozeUser(userId: String, days: Int = 30) {
        _allFriends.value = _allFriends.value.map {
            if (it.id == userId) it.copy(isSnoozed = true) else it
        }
    }

    fun takeBreakFromUser(userId: String) {
        _allFriends.value = _allFriends.value.map {
            if (it.id == userId) it.copy(isBreakTaken = true) else it
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
        try {
            firebaseFirestore?.collection("notifications")?.document(notificationId)?.update("isRead", true)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore mark notification read error: ${e.message}")
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun addNotification(notification: NotificationItem) {
        _notifications.value = listOf(notification) + _notifications.value
        try {
            firebaseFirestore?.collection("notifications")?.document(notification.id)?.set(notification)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore add notification error: ${e.message}")
        }
    }

    fun addBusinessPost(pageName: String, pageAvatar: String, content: String, mediaUrl: String? = null, mediaType: MediaType = MediaType.NONE) {
        val newPost = Post(
            id = "biz_${System.currentTimeMillis()}",
            userName = pageName,
            userAvatar = pageAvatar,
            userVerified = true,
            timestamp = "Just now",
            content = content,
            mediaUrl = mediaUrl,
            mediaType = mediaType
        )
        _businessPosts.value = listOf(newPost) + _businessPosts.value
        try {
            firebaseFirestore?.collection("posts")?.document(newPost.id)?.set(newPost)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore add business post error: ${e.message}")
        }
    }

    fun addPost(content: String, mediaUrl: String? = null, mediaType: MediaType = MediaType.NONE, audience: String = "Public") {
        val me = _currentUser.value

        // Moderate for hate speech, abusive text, and privacy violations
        val moderation = com.example.util.FriendHubSecurityController.inspectAndModerateContent(content)
        val isFlagged = moderation.isFlagged
        val flagReason = moderation.flagReason
        val finalContent = if (isFlagged) {
            moderation.sanitizedContent
        } else {
            com.example.util.FriendHubSecurityController.sanitizeTokensAndCredentials(content)
        }

        val factCheck = if (isAiVerificationEnabled) {
            when {
                content.contains("http", ignoreCase = true) || content.contains(".com", ignoreCase = true) -> {
                    if (content.contains("research", ignoreCase = true) || content.contains("source", ignoreCase = true)) {
                        FactCheckStatus.VERIFIED
                    } else {
                        FactCheckStatus.FAKE_NEWS
                    }
                }
                else -> FactCheckStatus.NONE
            }
        } else {
            FactCheckStatus.NONE
        }

        val aiSummary = if (isAiVerificationEnabled && content.length > 20) {
            "AI Summary: " + content.take(30) + "..."
        } else null

        val newPost = Post(
            id = "post_${System.currentTimeMillis()}",
            userId = me.id,
            userName = me.name,
            userAvatar = me.avatarUrl,
            userVerified = me.isVerified,
            timestamp = "Just now",
            content = finalContent,
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            likeCount = 0,
            commentCount = 0,
            shareCount = 0,
            isLikedByMe = false,
            isEncrypted = true,
            audience = audience,
            isFlagged = isFlagged,
            flagReason = flagReason,
            aiSummary = aiSummary,
            factCheckStatus = factCheck
        )
        
        _posts.value = listOf(newPost) + _posts.value

        if (isFlagged && flagReason != null) {
            submitReport("POST", newPost.id, flagReason, "Automatically flagged by FriendHub Security Controller")
            addNotification(
                NotificationItem(
                    id = "notif_sec_${System.currentTimeMillis()}",
                    type = NotificationType.SYSTEM_ALERT,
                    senderName = "FriendHub Security",
                    senderAvatar = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=500&auto=format&fit=crop&q=80",
                    message = "⚠️ Your post was flagged by Safety Controller: $flagReason",
                    timestamp = "Just now"
                )
            )
        }

        if (mediaType == MediaType.VIDEO && !mediaUrl.isNullOrBlank()) {
            addReel(mediaUrl, content.ifBlank { "Reel by ${me.name}" })
        }

        try {
            firebaseFirestore?.collection("posts")?.document(newPost.id)?.set(newPost)
                ?.addOnSuccessListener {
                    Log.d(TAG, "Post stored successfully in Firestore: ${newPost.id}")
                }
                ?.addOnFailureListener { e ->
                    Log.e(TAG, "Firestore write post failed: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Firestore addPost error: ${e.message}")
        }
    }

    fun addReel(videoUrl: String, caption: String) {
        val me = _currentUser.value
        val newReel = Reel(
            id = "reel_${System.currentTimeMillis()}",
            creatorName = me.name,
            creatorAvatar = me.avatarUrl,
            videoUrl = videoUrl,
            caption = caption,
            likesCount = "1",
            viewsCount = "1"
        )
        _reels.value = listOf(newReel) + _reels.value
        try {
            firebaseFirestore?.collection("reels")?.document(newReel.id)?.set(newReel)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore add reel error: ${e.message}")
        }
    }

    fun setReaction(postId: String, reaction: ReactionType) {
        val updater: (Post) -> Post = { p ->
            if (p.id == postId) {
                val wasLiked = p.isLikedByMe
                val isNowLiked = reaction != ReactionType.NONE
                val diff = when {
                    !wasLiked && isNowLiked -> 1
                    wasLiked && !isNowLiked -> -1
                    else -> 0
                }
                p.copy(
                    isLikedByMe = isNowLiked,
                    selectedReaction = reaction,
                    likeCount = (p.likeCount + diff).coerceAtLeast(0)
                )
            } else p
        }
        _posts.value = _posts.value.map(updater)
        syncPostToFirestore(postId)
    }

    fun toggleLike(postId: String) {
        val updater: (Post) -> Post = { p ->
            if (p.id == postId) {
                val newLiked = !p.isLikedByMe
                val newReaction = if (newLiked) ReactionType.LIKE else ReactionType.NONE
                val newCount = if (newLiked) p.likeCount + 1 else (p.likeCount - 1).coerceAtLeast(0)
                p.copy(isLikedByMe = newLiked, selectedReaction = newReaction, likeCount = newCount)
            } else p
        }
        _posts.value = _posts.value.map(updater)
        _businessPosts.value = _businessPosts.value.map(updater)
        syncPostToFirestore(postId)
    }

    fun incrementLikeCount(postId: String, count: Int = 1) {
        val updater: (Post) -> Post = { p ->
            if (p.id == postId) {
                p.copy(isLikedByMe = true, likeCount = p.likeCount + count)
            } else p
        }
        _posts.value = _posts.value.map(updater)
        _businessPosts.value = _businessPosts.value.map(updater)
        syncPostToFirestore(postId)
    }

    fun sharePost(postId: String) {
        val updater: (Post) -> Post = { p ->
            if (p.id == postId) {
                p.copy(shareCount = p.shareCount + 1)
            } else p
        }
        _posts.value = _posts.value.map(updater)
        _businessPosts.value = _businessPosts.value.map(updater)
        syncPostToFirestore(postId)
    }

    fun addComment(postId: String, content: String) {
        val me = _currentUser.value
        val moderation = com.example.util.FriendHubSecurityController.inspectAndModerateContent(content)
        val isFlagged = moderation.isFlagged
        val flagReason = moderation.flagReason
        val finalContent = if (isFlagged) {
            moderation.sanitizedContent
        } else {
            com.example.util.FriendHubSecurityController.sanitizeTokensAndCredentials(content)
        }

        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            postId = postId,
            userId = me.id,
            userName = me.name,
            userAvatar = me.avatarUrl,
            content = finalContent,
            timestamp = "Just now",
            isFlagged = isFlagged,
            flagReason = flagReason
        )

        if (isFlagged && flagReason != null) {
            submitReport("COMMENT", newComment.id, flagReason, "Comment flagged on post $postId")
        }

        val updater: (Post) -> Post = { p ->
            if (p.id == postId) {
                p.copy(
                    commentCount = p.commentCount + 1,
                    comments = p.comments + newComment
                )
            } else p
        }
        _posts.value = _posts.value.map(updater)
        _businessPosts.value = _businessPosts.value.map(updater)
        syncPostToFirestore(postId)
    }

    fun replyToComment(postId: String, commentId: String, replyText: String) {
        val me = _currentUser.value
        val replyComment = Comment(
            id = "reply_${System.currentTimeMillis()}",
            postId = postId,
            userId = me.id,
            userName = me.name,
            userAvatar = me.avatarUrl,
            content = "@Reply: $replyText",
            timestamp = "Just now"
        )
        val updater: (Post) -> Post = { p ->
            if (p.id == postId) {
                p.copy(
                    commentCount = p.commentCount + 1,
                    comments = p.comments + replyComment
                )
            } else p
        }
        _posts.value = _posts.value.map(updater)
        _businessPosts.value = _businessPosts.value.map(updater)
        syncPostToFirestore(postId)
    }

    private fun syncPostToFirestore(postId: String) {
        val target = _posts.value.find { it.id == postId } ?: _businessPosts.value.find { it.id == postId } ?: return
        try {
            firebaseFirestore?.collection("posts")?.document(postId)?.set(target)
        } catch (e: Exception) {
            Log.w(TAG, "Failed syncing post $postId to Firestore: ${e.message}")
        }
    }

    fun toggleSavePost(postId: String): Boolean {
        var isNowSaved = false
        val updater: (Post) -> Post = { p ->
            if (p.id == postId) {
                isNowSaved = !p.isSaved
                p.copy(isSaved = isNowSaved)
            } else p
        }
        _posts.value = _posts.value.map(updater)
        _businessPosts.value = _businessPosts.value.map(updater)
        syncPostToFirestore(postId)
        return isNowSaved
    }

    fun hidePost(postId: String) {
        _posts.value = _posts.value.filter { it.id != postId }
        _businessPosts.value = _businessPosts.value.filter { it.id != postId }
    }

    fun reportPost(postId: String, reason: String) {
        submitReport(targetType = "POST", targetId = postId, reason = reason)
    }

    fun submitReport(targetType: String, targetId: String, reason: String, details: String = "") {
        val record = ReportRecord(
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            additionalDetails = details,
            reportedAt = System.currentTimeMillis(),
            status = "PENDING_REVIEW"
        )
        _userReports.value = _userReports.value + record
        Log.d(TAG, "Report submitted: $targetType ($targetId) - Reason: $reason")
        
        // Save to Firestore if connected
        try {
            firebaseFirestore?.collection("reports")?.add(
                mapOf(
                    "targetType" to targetType,
                    "targetId" to targetId,
                    "reason" to reason,
                    "details" to details,
                    "reportedAt" to record.reportedAt,
                    "status" to record.status
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to send report to Firestore: ${e.message}")
        }
    }

    private fun containsHateSpeech(text: String): Boolean {
        val badWords = listOf("hate", "kill", "stupid", "idiot", "ugly", "spam", "scam")
        return badWords.any { text.lowercase().contains(it) }
    }

    fun addStory(mediaUrl: String?, caption: String, backgroundColor: String? = null) {
        val me = _currentUser.value
        val newStory = Story(
            id = "story_${System.currentTimeMillis()}",
            userId = me.id,
            userName = me.name,
            userAvatar = me.avatarUrl,
            mediaUrl = mediaUrl,
            backgroundColor = backgroundColor,
            caption = caption,
            timestamp = "Just now",
            isMe = true
        )
        _stories.value = listOf(newStory) + _stories.value.filter { !it.isMe }

        try {
            firebaseFirestore?.collection("stories")?.document(newStory.id)?.set(newStory)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore add story error: ${e.message}")
        }
    }

    fun getMessagesForChat(chatId: String): List<Message> {
        val fs = firebaseFirestore
        if (fs != null && !_messages.value.containsKey(chatId)) {
            fs.collection("chats").document(chatId).collection("messages")
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val msgs = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(Message::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                    }
                    if (msgs.isNotEmpty()) {
                        val currentMap = _messages.value.toMutableMap()
                        currentMap[chatId] = msgs
                        _messages.value = currentMap
                    }
                }
        }
        return _messages.value[chatId] ?: emptyList()
    }

    fun ensureChatSummary(chat: ChatSummary) {
        val existing = _chats.value.find { it.id == chat.id || (it.peerUserId != null && it.peerUserId == chat.peerUserId) }
        if (existing == null) {
            _chats.value = listOf(chat) + _chats.value
            try {
                firebaseFirestore?.collection("chats")?.document(chat.id)?.set(chat)
            } catch (e: Exception) {
                Log.w(TAG, "Firestore ensure chat error: ${e.message}")
            }
        } else {
            _chats.value = _chats.value.map {
                if (it.id == existing.id) it.copy(isConnected = chat.isConnected) else it
            }
        }
    }

    fun setChatConnected(chatId: String, connected: Boolean) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(isConnected = connected) else it
        }
    }

    fun simulateIncomingCall() {
        val mockPeer = CallLogItem(
            id = "call_${System.currentTimeMillis()}",
            peerName = "Kasun Perera",
            peerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            type = "VIDEO",
            direction = "INCOMING",
            timestamp = "Just now",
            duration = "00:00"
        )
        _incomingCall.value = mockPeer
    }

    fun dismissIncomingCall() {
        _incomingCall.value = null
    }

    fun sharePostToFriend(peerName: String, peerAvatar: String, postTitle: String, postUrl: String?) {
        val chatId = "chat_${peerName.replace(" ", "_").lowercase()}"
        val content = if (postUrl.isNullOrBlank()) "Shared a post: $postTitle" else "Shared a post: $postTitle - $postUrl"
        ensureChatSummary(
            ChatSummary(
                id = chatId,
                peerName = peerName,
                peerAvatar = peerAvatar,
                lastMessage = content,
                lastTimestamp = "Just now"
            )
        )
        sendMessage(chatId, content)
    }

    fun addMessageReaction(chatId: String, messageId: String, reactionEmoji: String) {
        val current = _messages.value[chatId] ?: return
        val updated = current.map {
            if (it.id == messageId) it.copy(reaction = reactionEmoji) else it
        }
        val map = _messages.value.toMutableMap()
        map[chatId] = updated
        _messages.value = map

        try {
            firebaseFirestore?.collection("chats")?.document(chatId)
                ?.collection("messages")?.document(messageId)?.update("reaction", reactionEmoji)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore message reaction error: ${e.message}")
        }
    }

    fun addMarketplaceItem(title: String, price: Double, category: String, description: String, imageUrl: String) {
        val me = _currentUser.value
        val newItem = MarketplaceItem(
            id = "item_${System.currentTimeMillis()}",
            title = title,
            price = price,
            category = category,
            imageUrl = imageUrl,
            sellerName = me.name,
            sellerAvatar = me.avatarUrl,
            location = me.location,
            description = description,
            timestamp = "Just now"
        )
        _marketplaceItems.value = listOf(newItem) + _marketplaceItems.value
        try {
            firebaseFirestore?.collection("marketplace")?.document(newItem.id)?.set(newItem)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore add marketplace error: ${e.message}")
        }
    }

    fun deleteNotification(notificationId: String) {
        _notifications.value = _notifications.value.filter { it.id != notificationId }
        try {
            firebaseFirestore?.collection("notifications")?.document(notificationId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore delete notification error: ${e.message}")
        }
    }

    fun createGroup(groupName: String, members: List<User>) {
        val newChat = ChatSummary(
            id = "group_${System.currentTimeMillis()}",
            peerName = groupName,
            isGroup = true,
            memberAvatars = members.map { it.avatarUrl },
            lastMessage = "Group created",
            lastTimestamp = "Just now"
        )
        ensureChatSummary(newChat)
    }

    fun reactToStory(storyId: String, emoji: String) {
        val story = _stories.value.find { it.id == storyId }
        if (story != null) {
            val notification = NotificationItem(
                id = "notif_${System.currentTimeMillis()}",
                type = NotificationType.REACTION,
                senderName = _currentUser.value.name,
                senderAvatar = _currentUser.value.avatarUrl,
                message = "reacted to your story with $emoji",
                timestamp = "Just now",
                targetId = storyId
            )
            addNotification(notification)
        }
    }

    fun updateUserProfile(updatedUser: User) {
        _currentUser.value = updatedUser
        try {
            firebaseFirestore?.collection("users")?.document(updatedUser.id)?.set(updatedUser)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore updateUserProfile error: ${e.message}")
        }
    }

    fun editPost(postId: String, newContent: String) {
        val updater: (Post) -> Post = { p ->
            if (p.id == postId) p.copy(content = newContent) else p
        }
        _posts.value = _posts.value.map(updater)
        _businessPosts.value = _businessPosts.value.map(updater)
        try {
            firebaseFirestore?.collection("posts")?.document(postId)?.update("content", newContent)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore edit post error: ${e.message}")
        }
    }

    fun deletePost(postId: String) {
        _posts.value = _posts.value.filter { it.id != postId }
        _businessPosts.value = _businessPosts.value.filter { it.id != postId }
        try {
            firebaseFirestore?.collection("posts")?.document(postId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore delete post error: ${e.message}")
        }
    }

    fun deleteStory(storyId: String) {
        _stories.value = _stories.value.filter { it.id != storyId }
        try {
            firebaseFirestore?.collection("stories")?.document(storyId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore delete story error: ${e.message}")
        }
    }

    fun deleteMessage(chatId: String, messageId: String) {
        val current = _messages.value[chatId] ?: return
        val updated = current.filter { it.id != messageId }
        val map = _messages.value.toMutableMap()
        map[chatId] = updated
        _messages.value = map

        try {
            firebaseFirestore?.collection("chats")?.document(chatId)
                ?.collection("messages")?.document(messageId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore delete message error: ${e.message}")
        }
    }

    fun deleteChat(chatId: String) {
        _chats.value = _chats.value.filter { it.id != chatId }
        val map = _messages.value.toMutableMap()
        map.remove(chatId)
        _messages.value = map
        try {
            firebaseFirestore?.collection("chats")?.document(chatId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore delete chat error: ${e.message}")
        }
    }

    fun blockUser(userId: String) {
        _allFriends.value = _allFriends.value.filter { it.id != userId }
        try {
            val currentUid = firebaseAuth?.currentUser?.uid ?: return
            firebaseFirestore?.collection("users")?.document(currentUid)
                ?.collection("blockedUsers")?.document(userId)?.set(mapOf("blockedAt" to System.currentTimeMillis()))
        } catch (e: Exception) {
            Log.w(TAG, "Block user error: ${e.message}")
        }
    }

    fun unblockUser(userId: String) {
        try {
            val currentUid = firebaseAuth?.currentUser?.uid ?: return
            firebaseFirestore?.collection("users")?.document(currentUid)
                ?.collection("blockedUsers")?.document(userId)?.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Unblock user error: ${e.message}")
        }
    }

    fun updatePresence(isOnline: Boolean) {
        val uid = firebaseAuth?.currentUser?.uid ?: return
        val updates = mapOf(
            "isOnline" to isOnline,
            "lastActiveAt" to System.currentTimeMillis()
        )
        try {
            firebaseFirestore?.collection("users")?.document(uid)?.update(updates)
            _currentUser.value = _currentUser.value.copy(isOnline = isOnline, lastActiveAt = System.currentTimeMillis())
        } catch (e: Exception) {
            Log.w(TAG, "Update presence error: ${e.message}")
        }
    }

    fun updateActivity() {
        val uid = firebaseAuth?.currentUser?.uid ?: return
        try {
            firebaseFirestore?.collection("users")?.document(uid)?.update("lastActiveAt", System.currentTimeMillis())
            _currentUser.value = _currentUser.value.copy(lastActiveAt = System.currentTimeMillis())
        } catch (e: Exception) {
            Log.w(TAG, "Update activity error: ${e.message}")
        }
    }

    fun observeUserPresence(userId: String, onUpdate: (User) -> Unit) {
        try {
            firebaseFirestore?.collection("users")?.document(userId)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                    val user = snapshot.toObject(User::class.java)?.copy(id = snapshot.id)
                    if (user != null) {
                        onUpdate(user)
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Observe presence error: ${e.message}")
        }
    }

    private val _activeLiveSession = MutableStateFlow<LiveSession?>(null)
    val activeLiveSession: StateFlow<LiveSession?> = _activeLiveSession.asStateFlow()

    private val _liveMessagesList = MutableStateFlow<List<LiveChatMessage>>(emptyList())
    val liveMessagesList: StateFlow<List<LiveChatMessage>> = _liveMessagesList.asStateFlow()

    fun startLiveSession(title: String, category: String) {
        val fs = firebaseFirestore ?: return
        val user = _currentUser.value
        val sessionId = "live_${user.id}_${System.currentTimeMillis()}"
        
        val initialSeats = listOf(
            LiveSeat(1, user.name, isHost = true, isOccupied = true, avatarUrl = user.avatarUrl),
            LiveSeat(2, isOccupied = false),
            LiveSeat(3, isOccupied = false),
            LiveSeat(4, isOccupied = false)
        )

        val session = LiveSession(
            id = sessionId,
            hostId = user.id,
            hostName = user.name,
            hostAvatar = user.avatarUrl,
            title = title,
            category = category,
            viewerCount = 1,
            startedAt = System.currentTimeMillis(),
            status = "LIVE",
            seats = initialSeats
        )

        try {
            fs.collection("live_sessions").document(sessionId).set(session)
            _activeLiveSession.value = session
            observeLiveSession(sessionId)
        } catch (e: Exception) {
            Log.e(TAG, "Start live session error: ${e.message}")
        }
    }

    private fun observeLiveSession(sessionId: String) {
        val fs = firebaseFirestore ?: return
        fs.collection("live_sessions").document(sessionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val session = snapshot.toObject(LiveSession::class.java)
                _activeLiveSession.value = session
            }

        fs.collection("live_sessions").document(sessionId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val msgs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(LiveChatMessage::class.java)
                }
                _liveMessagesList.value = msgs
            }
    }

    fun sendLiveMessage(sessionId: String, text: String) {
        val fs = firebaseFirestore ?: return
        val user = _currentUser.value
        val msg = LiveChatMessage(
            userName = user.name,
            message = text,
            isMe = true
        )
        try {
            fs.collection("live_sessions").document(sessionId).collection("messages")
                .add(msg.copy(id = UUID.randomUUID().toString()))
        } catch (e: Exception) {
            Log.w(TAG, "Send live message error: ${e.message}")
        }
    }

    fun endLiveSession(sessionId: String) {
        val fs = firebaseFirestore ?: return
        try {
            fs.collection("live_sessions").document(sessionId).update("status", "ENDED")
            _activeLiveSession.value = null
            _liveMessagesList.value = emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "End live session error: ${e.message}")
        }
    }

    fun updateLiveViewerCount(sessionId: String, delta: Int) {
        val fs = firebaseFirestore ?: return
        try {
            fs.collection("live_sessions").document(sessionId).get().addOnSuccessListener { doc ->
                val current = doc.getLong("viewerCount") ?: 0
                doc.reference.update("viewerCount", (current + delta).coerceAtLeast(0))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Update viewer count error: ${e.message}")
        }
    }

    fun joinLiveSeat(sessionId: String, seatId: Int, userName: String, avatarUrl: String, isMicOn: Boolean) {
        val fs = firebaseFirestore ?: return
        try {
            fs.collection("live_sessions").document(sessionId).get().addOnSuccessListener { doc ->
                val session = doc.toObject(LiveSession::class.java) ?: return@addOnSuccessListener
                val updatedSeats = session.seats.map { seat ->
                    if (seat.id == seatId) {
                        seat.copy(
                            name = userName,
                            avatarUrl = avatarUrl,
                            isOccupied = true,
                            isMuted = !isMicOn,
                            isSpeaking = isMicOn
                        )
                    } else seat
                }
                doc.reference.update("seats", updatedSeats)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Join live seat error: ${e.message}")
        }
    }

    private val _conversations = MutableStateFlow<List<ChatSummary>>(emptyList())
    val conversations: StateFlow<List<ChatSummary>> = _conversations.asStateFlow()

    fun sendMessage(conversationId: String, content: String, mediaUrl: String? = null) {
        val fs = firebaseFirestore ?: return
        val user = _currentUser.value
        val chat = _chats.value.find { it.id == conversationId } ?: _conversations.value.find { it.id == conversationId }

        val canAccess = if (chat != null) {
            com.example.util.FriendHubSecurityController.canUserAccessChat(conversationId, chat.participantIds, chat.peerUserId, user.id)
        } else {
            conversationId.contains(user.id) || conversationId.contains(user.name.lowercase())
        }

        if (!canAccess) {
            Log.e(TAG, "Security Controller: Blocked unauthorized message attempt to conversation $conversationId")
            return
        }

        // 1. Moderate for hate speech, abusive text, and privacy violations
        val moderation = com.example.util.FriendHubSecurityController.inspectAndModerateContent(content)
        val isFlagged = moderation.isFlagged
        val flagReason = moderation.flagReason
        val finalContent = if (isFlagged) {
            moderation.sanitizedContent
        } else {
            com.example.util.FriendHubSecurityController.sanitizeTokensAndCredentials(content)
        }

        // 2. Encrypt message payload using AES-256-GCM End-to-End Encryption before sending to Cloud Firestore
        val encryptedContent = com.example.util.E2EECryptoEngine.encryptMessage(finalContent, conversationId)

        val msgId = UUID.randomUUID().toString()
        val message = Message(
            id = msgId,
            chatId = conversationId,
            senderId = user.id,
            senderName = user.name,
            senderAvatar = user.avatarUrl,
            content = encryptedContent,
            mediaUrl = mediaUrl,
            timestamp = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
            isMe = true,
            isSeen = false,
            isFlagged = isFlagged,
            flagReason = flagReason
        )

        try {
            fs.collection("conversations").document(conversationId)
                .collection("messages").document(msgId).set(message)
            
            // Update last message in conversation doc with encrypted snippet
            fs.collection("conversations").document(conversationId).update(
                mapOf(
                    "lastMessage" to encryptedContent,
                    "lastTimestamp" to message.timestamp
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Send message error: ${e.message}")
        }
    }

    fun observeConversations() {
        val fs = firebaseFirestore ?: return
        val user = _currentUser.value
        if (user.id.isBlank()) return

        fs.collection("conversations")
            .whereArrayContains("participantIds", user.id)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val chats = snapshot.documents.mapNotNull { it.toObject(ChatSummary::class.java) }
                _conversations.value = chats
                // Also update legacy _chats for UI compatibility if needed
                _chats.value = chats
            }
    }

    fun observeMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val fs = firebaseFirestore ?: return@callbackFlow
        val user = _currentUser.value
        val chat = _chats.value.find { it.id == conversationId } ?: _conversations.value.find { it.id == conversationId }

        val canAccess = if (chat != null) {
            com.example.util.FriendHubSecurityController.canUserAccessChat(conversationId, chat.participantIds, chat.peerUserId, user.id)
        } else {
            conversationId.contains(user.id) || conversationId.contains(user.name.lowercase()) || user.id.isBlank()
        }

        if (!canAccess) {
            Log.e(TAG, "Security Controller: Blocked unauthorized message read for conversation $conversationId")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = fs.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val msgs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.let {
                        com.example.util.FriendHubSecurityController.sanitizeMessage(it, conversationId)
                    }
                } ?: emptyList()
                trySend(msgs)
            }
        awaitClose { listener.remove() }
    }

    fun markMessageAsSeen(conversationId: String, messageId: String) {
        val fs = firebaseFirestore ?: return
        try {
            fs.collection("conversations").document(conversationId)
                .collection("messages").document(messageId).update("isSeen", true)
        } catch (e: Exception) {
            Log.w(TAG, "Mark seen error: ${e.message}")
        }
    }

    fun loadNextPageOfPosts(pageSize: Int = 15) {
        val fs = firebaseFirestore ?: return
        try {
            val lastPostId = _posts.value.lastOrNull()?.id
            var query = fs.collection("posts").limit(pageSize.toLong())
            if (lastPostId != null) {
                query = query.startAfter(lastPostId)
            }
            query.get().addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val pagePosts = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(Post::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                    }
                    if (pagePosts.isNotEmpty()) {
                        _posts.value = (_posts.value + pagePosts).distinctBy { it.id }
                    }
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Paginated posts fetch error: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching next page of posts: ${e.message}")
        }
    }

    // --- Creator Unlock Program & Follow Limits ---
    fun followUserWithGuardrail(targetUserId: String): Boolean {
        val current = _currentUser.value
        val now = System.currentTimeMillis()
        val isSameDay = (now - current.lastFollowActionTimestamp) < 24 * 60 * 60 * 1000

        val followsCount = if (isSameDay) current.followsTodayCount else 0
        if (followsCount >= 100) {
            // Anti-Spam Guardrail: Max 100 follows/day
            return false
        }

        // Increment follow count and execute follow
        val updatedUser = current.copy(
            followsTodayCount = followsCount + 1,
            lastFollowActionTimestamp = now,
            followingCount = current.followingCount + 1
        )
        _currentUser.value = updatedUser

        // Also check if target or current user reached 5,000 threshold
        checkAndAuditCreatorThreshold(targetUserId)
        return true
    }

    fun checkAndAuditCreatorThreshold(userId: String) {
        val current = _currentUser.value
        if (current.id == userId || current.followersCount >= 5000) {
            val isEligible = current.followersCount >= 5000
            if (isEligible && !current.isVerifiedCreator) {
                _currentUser.value = current.copy(
                    isVerifiedCreator = true,
                    creatorMonetizationEnabled = true,
                    is4kUploadUnlocked = true,
                    isBioLinkUnlocked = true,
                    isLiveMonetizationUnlocked = true
                )
            }
        }
    }

    fun auditAccountForMonetization() {
        val current = _currentUser.value
        if (current.followersCount >= 5000) {
            _currentUser.value = current.copy(
                isVerifiedCreator = true,
                creatorMonetizationEnabled = true,
                is4kUploadUnlocked = true,
                isBioLinkUnlocked = true,
                isLiveMonetizationUnlocked = true
            )
        }
    }

    fun addVirtualCoins(coins: Int) {
        val current = _currentUser.value
        _currentUser.value = current.copy(coinBalance = current.coinBalance + coins)
    }

    fun subscribeBusinessAccount() {
        val current = _currentUser.value
        _currentUser.value = current.copy(
            isBusinessSubscribed = true,
            isVerified = true
        )
    }
}

