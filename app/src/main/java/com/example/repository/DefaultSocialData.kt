package com.example.repository

import com.example.model.*

object DefaultSocialData {

    val currentUser = User(
        id = "user_me",
        name = "Wasantha Kumara",
        username = "wasanthakumara",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80",
        bio = "App Admin & System Owner 👑 | Kuttapitiya, Pelmadulla",
        work = "App Admin & System Owner",
        education = "Software Development & IT",
        location = "Kuttapitiya, Pelmadulla",
        hometown = "Kuttapitiya, Pelmadulla",
        relationshipStatus = "Single",
        email = "wk541454@gmail.com",
        phone = "0719117815",
        website = "https://friendhub.io",
        isOnline = true,
        isVerified = true,
        followersCount = 5420,
        followingCount = 412
    )

    val friends = listOf(
        User(
            id = "user_kasun",
            name = "Kasun Perera",
            username = "kasun_perera",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            bio = "Photography & Tech enthusiast 📸",
            location = "Colombo, Sri Lanka",
            isOnline = true,
            isFriend = true,
            isFollowedByMe = true,
            followersCount = 1420
        ),
        User(
            id = "user_nimali",
            name = "Nimali Silva",
            username = "nimali_s",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            bio = "Designer • Coffee Lover ☕",
            location = "Kandy, Sri Lanka",
            isOnline = true,
            isFriend = true,
            isFollowedByMe = true,
            followersCount = 2890
        ),
        User(
            id = "user_dineth",
            name = "Dineth Fernando",
            username = "dineth_f",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
            bio = "Traveler & Mobile Dev ✈️📱",
            location = "Galle, Sri Lanka",
            isOnline = false,
            isFriend = true,
            isFollowedByMe = true,
            followersCount = 980
        )
    )

    val posts = listOf(
        Post(
            id = "post_1",
            userId = "user_me",
            userName = "Wasantha Kumara",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
            userVerified = true,
            timestamp = "Just now",
            content = "Welcome to FriendHub! 🚀 Connect with friends, share stories, make HD video calls, and explore Marketplace. Real-time Cloud sync is active!",
            mediaUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=1000&auto=format&fit=crop&q=80",
            mediaType = MediaType.IMAGE,
            likeCount = 24,
            commentCount = 3,
            shareCount = 5,
            isLikedByMe = true,
            selectedReaction = ReactionType.LIKE,
            comments = listOf(
                Comment(
                    id = "c1",
                    postId = "post_1",
                    userId = "user_kasun",
                    userName = "Kasun Perera",
                    userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
                    content = "Awesome application! UI is super clean and fast. 🔥",
                    timestamp = "10m ago",
                    likes = 4
                ),
                Comment(
                    id = "c2",
                    postId = "post_1",
                    userId = "user_nimali",
                    userName = "Nimali Silva",
                    userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
                    content = "Love the design and real-time features! 💯",
                    timestamp = "5m ago",
                    likes = 2
                )
            )
        ),
        Post(
            id = "post_2",
            userId = "user_kasun",
            userName = "Kasun Perera",
            userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            userVerified = false,
            timestamp = "2 hours ago",
            content = "Golden hour vibes from the southern coast 🌅 Nothing beats a refreshing sunset after a busy day.",
            mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000&auto=format&fit=crop&q=80",
            mediaType = MediaType.IMAGE,
            likeCount = 89,
            commentCount = 7,
            shareCount = 2
        ),
        Post(
            id = "post_3",
            userId = "user_nimali",
            userName = "Nimali Silva",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            userVerified = false,
            timestamp = "5 hours ago",
            content = "Exploring modern mobile UI architecture patterns with Jetpack Compose & Firebase. Clean, fast, and scalable! 💻✨",
            mediaUrl = null,
            mediaType = MediaType.NONE,
            likeCount = 42,
            commentCount = 4,
            shareCount = 1
        )
    )

    val stories = listOf(
        Story(
            id = "story_me",
            userId = "user_me",
            userName = "Your Story",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
            mediaUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            caption = "Live on FriendHub! ✨",
            hasUnseen = false,
            isMe = true
        ),
        Story(
            id = "story_kasun",
            userId = "user_kasun",
            userName = "Kasun Perera",
            userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
            caption = "Nature trip 🌲",
            hasUnseen = true,
            isMe = false
        ),
        Story(
            id = "story_nimali",
            userId = "user_nimali",
            userName = "Nimali Silva",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop&q=80",
            caption = "Studio workspace ☕",
            hasUnseen = true,
            isMe = false
        )
    )

    val reels = listOf(
        Reel(
            id = "reel_1",
            creatorName = "Kasun Perera",
            creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            videoUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
            caption = "Sunset waves in Mirissa 🌊 #SriLanka #Travel",
            likesCount = "18.2K",
            viewsCount = "62K"
        ),
        Reel(
            id = "reel_2",
            creatorName = "Nimali Silva",
            creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            videoUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800&auto=format&fit=crop&q=80",
            caption = "Designing seamless mobile experiences 💻🚀",
            likesCount = "9.4K",
            viewsCount = "38K"
        )
    )

    val chats = listOf(
        ChatSummary(
            id = "chat_kasun",
            peerUserId = "user_kasun",
            peerName = "Kasun Perera",
            peerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            lastMessage = "Hey! Did you check out the new update?",
            lastTimestamp = "10:42 AM",
            unreadCount = 1,
            isOnline = true
        ),
        ChatSummary(
            id = "chat_nimali",
            peerUserId = "user_nimali",
            peerName = "Nimali Silva",
            peerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            lastMessage = "Thanks for the feedback on the project!",
            lastTimestamp = "Yesterday",
            unreadCount = 0,
            isOnline = false
        )
    )

    val messages = mapOf(
        "chat_kasun" to listOf(
            Message(
                id = "m1",
                chatId = "chat_kasun",
                senderId = "user_kasun",
                senderName = "Kasun Perera",
                senderAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
                content = "Hey Wasantha! How is the new FriendHub build going?",
                timestamp = "10:38 AM",
                isMe = false
            ),
            Message(
                id = "m2",
                chatId = "chat_kasun",
                senderId = "user_me",
                senderName = "Wasantha Kumara",
                senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                content = "Working smoothly! Full real-time cloud sync is online.",
                timestamp = "10:40 AM",
                isMe = true
            ),
            Message(
                id = "m3",
                chatId = "chat_kasun",
                senderId = "user_kasun",
                senderName = "Kasun Perera",
                senderAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
                content = "Hey! Did you check out the new update?",
                timestamp = "10:42 AM",
                isMe = false
            )
        )
    )

    val marketplaceItems = listOf(
        MarketplaceItem(
            id = "item_1",
            title = "Sony Alpha A7 III Camera Body",
            price = 1250.0,
            category = "Electronics",
            imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&auto=format&fit=crop&q=80",
            sellerName = "Kasun Perera",
            sellerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            location = "Colombo, Sri Lanka",
            description = "Excellent condition, shutter count under 12k. Includes original battery and charger.",
            timestamp = "Today"
        ),
        MarketplaceItem(
            id = "item_2",
            title = "Apple MacBook Pro 14 M2 Pro",
            price = 1850.0,
            category = "Computers",
            imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80",
            sellerName = "Nimali Silva",
            sellerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            location = "Kandy, Sri Lanka",
            description = "16GB RAM, 512GB SSD. Flawless condition with box and accessories.",
            timestamp = "Yesterday"
        )
    )

    val callLogs = listOf(
        CallLogItem(
            id = "call_1",
            peerName = "Kasun Perera",
            peerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            type = "VIDEO",
            direction = "INCOMING",
            timestamp = "15 mins ago",
            duration = "05:22"
        ),
        CallLogItem(
            id = "call_2",
            peerName = "Nimali Silva",
            peerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            type = "AUDIO",
            direction = "OUTGOING",
            timestamp = "2 hours ago",
            duration = "02:45"
        )
    )

    val friendRequests = listOf(
        FriendRequest(
            id = "freq_1",
            userId = "user_chathura",
            userName = "Chathura Jayasinghe",
            userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&auto=format&fit=crop&q=80",
            subtitle = "5 mutual friends",
            bio = "Software Engineer | Open Source contributor"
        )
    )

    val notifications = listOf(
        NotificationItem(
            id = "notif_1",
            type = NotificationType.LIKE,
            senderName = "Kasun Perera",
            senderAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            message = "liked your post: 'Welcome to FriendHub!'",
            timestamp = "10m ago",
            isRead = false
        ),
        NotificationItem(
            id = "notif_2",
            type = NotificationType.COMMENT,
            senderName = "Nimali Silva",
            senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            message = "commented on your photo: 'Love the design and real-time features! 💯'",
            timestamp = "25m ago",
            isRead = false
        ),
        NotificationItem(
            id = "notif_3",
            type = NotificationType.FRIEND_REQUEST,
            senderName = "Chathura Jayasinghe",
            senderAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&auto=format&fit=crop&q=80",
            message = "sent you a friend request.",
            timestamp = "1 hour ago",
            isRead = true
        )
    )

    val profileVisitors = listOf(
        ProfileVisitor(
            id = "pv_1",
            visitorId = "user_kasun",
            visitorName = "Kasun Perera",
            visitorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            visitorBio = "Photography & Tech enthusiast 📸",
            timestamp = "Just now"
        ),
        ProfileVisitor(
            id = "pv_2",
            visitorId = "user_nimali",
            visitorName = "Nimali Silva",
            visitorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            visitorBio = "Designer • Coffee Lover ☕",
            timestamp = "12 mins ago"
        ),
        ProfileVisitor(
            id = "pv_3",
            visitorId = "user_dineth",
            visitorName = "Dineth Fernando",
            visitorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
            visitorBio = "Traveler & Mobile Dev ✈️📱",
            timestamp = "45 mins ago"
        ),
        ProfileVisitor(
            id = "pv_4",
            visitorId = "user_amaya",
            visitorName = "Amaya Jayawardena",
            visitorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
            visitorBio = "Digital Creator & Stylist ✨",
            timestamp = "2 hours ago"
        )
    )

    val storyHighlights = listOf(
        HighlightCategory(id = "hl_1", title = "Travel", coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=500&auto=format&fit=crop&q=80"),
        HighlightCategory(id = "hl_2", title = "Food", coverUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500&auto=format&fit=crop&q=80"),
        HighlightCategory(id = "hl_3", title = "Friends", coverUrl = "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=500&auto=format&fit=crop&q=80"),
        HighlightCategory(id = "hl_4", title = "Hangout", coverUrl = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=500&auto=format&fit=crop&q=80"),
        HighlightCategory(id = "hl_5", title = "Outing", coverUrl = "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=500&auto=format&fit=crop&q=80")
    )
}
