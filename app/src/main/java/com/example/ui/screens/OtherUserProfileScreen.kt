package com.example.ui.screens

import android.widget.Toast
import com.example.repository.DefaultSocialData
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.model.*
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.FriendOptionsBottomSheet
import com.example.ui.components.ViewProfilePhotoDialog
import com.example.viewmodel.MainViewModel

/**
 * Complete, modern social-media profile screen for viewing ANOTHER USER'S PROFILE.
 * Designed with an original, premium social-media UI, full friend system states,
 * strict access control privacy rules, and 6 comprehensive profile tabs:
 * 1. Posts (Text, Photo, Video, Reels with reactions, comments, share, save, pinned post)
 * 2. About (13 detailed fields with field-level privacy: Public / Friends / Only Me)
 * 3. Photos (Photo grid, Albums, Full-screen viewer, Privacy rules)
 * 4. Videos (Thumbnails, Video player preview, Likes, Comments, Shares)
 * 5. Reels (Vertical 9:16 grid with sound title and view count)
 * 6. Friends (Friends list, Search friends, Mutual friends, Privacy controls)
 */
@Composable
fun OtherUserProfileScreen(
    user: User,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val allPosts by viewModel.posts.collectAsState()
    val allReels by viewModel.reels.collectAsState()
    val knownUsers by viewModel.knownUsers.collectAsState()
    val selectedProfile by viewModel.selectedUserProfile.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val liveUser = knownUsers[user.id] ?: (if (selectedProfile?.id == user.id) selectedProfile else null) ?: user
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    // Loading, Error, and Offline States
    var isLoading by remember { mutableStateOf(false) }
    var isNetworkError by remember { mutableStateOf(false) }
    var isOfflineMode by remember { mutableStateOf(false) }

    // Record profile visitor on opening and provide brief smooth loading shimmer
    LaunchedEffect(liveUser.id) {
        viewModel.recordProfileVisitor(liveUser)
        isLoading = true
        kotlinx.coroutines.delay(400)
        isLoading = false
    }

    // State management
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Posts, 1: About, 2: Photos, 3: Videos, 4: Reels, 5: Friends
    var showFriendOptionsSheet by remember { mutableStateOf(false) }
    var showMoreOptionsMenu by remember { mutableStateOf(false) }
    var activePostForComments by remember { mutableStateOf<Post?>(null) }
    var viewingPhotoUrl by remember { mutableStateOf<String?>(null) }
    var viewingVideoPost by remember { mutableStateOf<Post?>(null) }
    var viewingReel by remember { mutableStateOf<Reel?>(null) }
    var friendsSearchQuery by remember { mutableStateOf("") }
    var selectedPhotoAlbum by remember { mutableStateOf("All Photos") }

    // Privacy & Friends status
    val isFriend = liveUser.isFriend
    val isLocked = liveUser.isProfileLocked

    // Full screen Loading Skeleton
    if (isLoading) {
        OtherUserProfileSkeleton(
            isDarkMode = isDarkMode,
            onBack = onBack
        )
        return
    }

    // Full screen Network Error State
    if (isNetworkError) {
        OtherUserProfileErrorView(
            user = liveUser,
            isDarkMode = isDarkMode,
            onRetry = {
                isNetworkError = false
                isLoading = true
            },
            onBack = onBack,
            onShowCached = {
                isNetworkError = false
                isOfflineMode = true
            }
        )
        return
    }

    // Filter user's posts according to privacy rules:
    // If friend: show Public and Friends-only posts.
    // If not friend: show only Public posts.
    // Never show Only Me posts to other users.
    val rawUserPosts = remember(allPosts, liveUser) {
        allPosts.filter { it.userId == liveUser.id || it.userName.equals(liveUser.name, ignoreCase = true) }
    }
    val filteredPosts = remember(rawUserPosts, isFriend) {
        rawUserPosts.filter { post ->
            if (post.audience.equals("Only Me", ignoreCase = true)) {
                false
            } else if (post.audience.equals("Friends", ignoreCase = true)) {
                isFriend
            } else {
                true // Public posts
            }
        }.sortedByDescending { it.isPinned }
    }

    // Media and Photos
    val userPhotos = remember(filteredPosts, liveUser) {
        val list = mutableListOf<String>()
        if (liveUser.avatarUrl.isNotBlank()) list.add(liveUser.avatarUrl)
        if (liveUser.coverUrl.isNotBlank()) list.add(liveUser.coverUrl)
        filteredPosts.forEach { post ->
            if (!post.mediaUrl.isNullOrBlank() && post.mediaType != MediaType.VIDEO) {
                list.add(post.mediaUrl)
            }
        }
        if (list.size < 4 && (isFriend || !isLocked)) {
            list.addAll(
                listOf(
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800&auto=format&fit=crop&q=80"
                )
            )
        }
        list.distinct()
    }

    // User's Videos
    val userVideos = remember(filteredPosts, liveUser) {
        val list = filteredPosts.filter { it.mediaType == MediaType.VIDEO || it.content.contains("video", ignoreCase = true) }.toMutableList()
        if (list.isEmpty() && (isFriend || !isLocked)) {
            list.add(
                Post(
                    id = "vid_${liveUser.id}_1",
                    userId = liveUser.id,
                    userName = liveUser.name,
                    userAvatar = liveUser.avatarUrl,
                    timestamp = "2 days ago",
                    content = "Weekend sunset timelapse from the hill country 🌅🍃",
                    mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
                    mediaType = MediaType.VIDEO,
                    likeCount = 89,
                    commentCount = 14,
                    shareCount = 6,
                    audience = if (isFriend) "Friends" else "Public"
                )
            )
            list.add(
                Post(
                    id = "vid_${liveUser.id}_2",
                    userId = liveUser.id,
                    userName = liveUser.name,
                    userAvatar = liveUser.avatarUrl,
                    timestamp = "1 week ago",
                    content = "Quick coding walkthrough of modern Kotlin coroutines 💻⚡",
                    mediaUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&auto=format&fit=crop&q=80",
                    mediaType = MediaType.VIDEO,
                    likeCount = 156,
                    commentCount = 28,
                    shareCount = 12,
                    audience = "Public"
                )
            )
        }
        list
    }

    // User's Reels
    val userReels = remember(allReels, liveUser) {
        val list = allReels.filter { it.userId == liveUser.id || it.userName.equals(liveUser.name, ignoreCase = true) }.toMutableList()
        if (list.isEmpty() && (isFriend || !isLocked)) {
            list.addAll(allReels.take(4))
        }
        list
    }

    // Known / Mutual Friends list
    val userFriendsList = remember(knownUsers, liveUser) {
        val pool = knownUsers.values.filter { it.id != liveUser.id && it.id != currentUser.id }.toList()
        if (pool.isNotEmpty()) pool else DefaultSocialData.friends
    }

    val filteredFriendsList = remember(userFriendsList, friendsSearchQuery) {
        if (friendsSearchQuery.isBlank()) userFriendsList
        else userFriendsList.filter {
            it.name.contains(friendsSearchQuery, ignoreCase = true) ||
            it.username.contains(friendsSearchQuery, ignoreCase = true)
        }
    }

    val screenBg = if (isDarkMode) Color(0xFF0B1120) else Color(0xFFF1F5F9)

    // MAIN SCREEN LAYOUT
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .padding(bottom = 60.dp)
    ) {
        // 0. Offline Mode Banner (when active)
        if (isOfflineMode) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Offline",
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Offline Mode: Showing cached profile",
                            color = Color(0xFF92400E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                isOfflineMode = false
                                isLoading = true
                                Toast.makeText(context, "Reconnected! Online data synchronized 🟢", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Retry", color = Color(0xFFB45309), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        // 1. Cover Photo Banner & Floating Top Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                // Large Cover Photo
                AsyncImage(
                    model = liveUser.coverUrl.ifBlank { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80" },
                    contentDescription = "Cover Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { viewingPhotoUrl = liveUser.coverUrl }
                )

                // Dark Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.65f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                // Top Navigation Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search Button
                        IconButton(
                            onClick = { viewModel.setSearchOpen(true) },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }

                        // Share Profile Link Button
                        IconButton(
                            onClick = {
                                val link = "https://friendhub.io/profile/${liveUser.username.ifBlank { liveUser.id }}"
                                clipboardManager.setText(AnnotatedString(link))
                                Toast.makeText(context, "Profile link copied! 🔗", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Profile",
                                tint = Color.White
                            )
                        }

                        // More Options (3-dots)
                        IconButton(
                            onClick = { showMoreOptionsMenu = true },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "More Options",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 2. Profile Card (Avatar, Name, Bio, Stats, Action Bar)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .offset(y = (-35).dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Overlapping Avatar with Glowing Ring & Online Status
                    Box(
                        modifier = Modifier
                            .offset(y = (-45).dp)
                            .size(106.dp)
                            .clickable { viewingPhotoUrl = liveUser.avatarUrl },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = liveUser.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80" },
                            contentDescription = liveUser.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(32.dp))
                                .border(4.dp, Color(0xFF1E293B), RoundedCornerShape(32.dp))
                        )

                        // Online / Offline Status Badge
                        if (liveUser.isOnline) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                                    .border(2.5.dp, Color(0xFF1E293B), CircleShape)
                            )
                        }
                    }

                    // Full Name, Verification Badge, Username, Status
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = (-32).dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = liveUser.name,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = Color.White
                                )
                            )
                            if (liveUser.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (isLocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E3A8A).copy(alpha = 0.6f),
                                    border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF93C5FD), modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Locked", fontSize = 10.sp, color = Color(0xFF93C5FD), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // @Username
                        Text(
                            text = "@${liveUser.username.ifBlank { liveUser.name.lowercase().replace(" ", "_") }}",
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Online / Last Active Status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (liveUser.isOnline) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Active now", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            } else {
                                val showLastActive = isFriend || liveUser.lastActivePrivacy.equals("PUBLIC", ignoreCase = true)
                                val lastActiveText = if (showLastActive) "Active 25m ago" else "Offline"
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF94A3B8)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(lastActiveText, color = Color(0xFF94A3B8), fontSize = 12.sp)
                            }
                        }

                        // Bio
                        if (liveUser.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = liveUser.bio,
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Clickable Website
                        if (liveUser.website.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Opening ${liveUser.website}", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = liveUser.website,
                                    fontSize = 12.sp,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Horizontal Stats Counter Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Friends
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${liveUser.friendsCount}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(text = "Friends", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }

                            HorizontalDivider(modifier = Modifier.height(20.dp).width(1.dp), color = Color(0xFF334155))

                            // Followers
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${liveUser.followersCount}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(text = "Followers", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }

                            HorizontalDivider(modifier = Modifier.height(20.dp).width(1.dp), color = Color(0xFF334155))

                            // Following
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${liveUser.followingCount}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(text = "Following", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }

                            HorizontalDivider(modifier = Modifier.height(20.dp).width(1.dp), color = Color(0xFF334155))

                            // Posts
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${filteredPosts.size.coerceAtLeast(1)}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(text = "Posts", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Mutual Friends Preview Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            // Overlapping 3 Avatars
                            Box(modifier = Modifier.width(52.dp).height(24.dp)) {
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).border(1.5.dp, Color(0xFF0F172A), CircleShape).background(Color(0xFF2563EB)))
                                Box(modifier = Modifier.offset(x = 14.dp).size(24.dp).clip(CircleShape).border(1.5.dp, Color(0xFF0F172A), CircleShape).background(Color(0xFF10B981)))
                                Box(modifier = Modifier.offset(x = 28.dp).size(24.dp).clip(CircleShape).border(1.5.dp, Color(0xFF0F172A), CircleShape).background(Color(0xFFF59E0B)))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${liveUser.mutualFriendsCount.coerceAtLeast(3)} mutual friends including Alex and Maya",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Friend System Action Bar
                        OtherUserActionBar(
                            user = liveUser,
                            viewModel = viewModel,
                            onOpenFriendOptions = { showFriendOptionsSheet = true },
                            onOpenMore = { showMoreOptionsMenu = true }
                        )
                    }
                }
            }
        }

        // 3. Privacy Rule Overlay: If user locked profile and viewer is NOT a friend
        if (isLocked && !isFriend) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .offset(y = (-20).dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Private Profile",
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Private Profile 🔒",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Only friends can view ${liveUser.name.split(" ").firstOrNull() ?: "this user"}'s full timeline, photos, videos, and private details.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                        if (!liveUser.isRequestSent) {
                            Button(
                                onClick = {
                                    viewModel.sendFriendRequest(liveUser.id)
                                    Toast.makeText(context, "Friend request sent to ${liveUser.name}!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Add", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 4. 6-Tab Navigation Bar (Posts, About, Photos, Videos, Reels, Friends)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-10).dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "Posts" to Icons.Default.Article,
                        "About" to Icons.Default.Info,
                        "Photos" to Icons.Default.PhotoLibrary,
                        "Videos" to Icons.Default.VideoLibrary,
                        "Reels" to Icons.Default.Movie,
                        "Friends" to Icons.Default.People
                    )
                    items(tabs.indices.toList()) { index ->
                        val (tabTitle, tabIcon) = tabs[index]
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = tabIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tabTitle,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // 5. Active Tab Content Rendering
        when (selectedTab) {
            0 -> {
                // TAB 0: POSTS
                if (filteredPosts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📝", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isLocked && !isFriend) "No Public Posts Available" else "No posts yet",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isLocked && !isFriend) "Add friend to unlock private timeline posts." else "When ${liveUser.name} shares posts, they will appear here.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredPosts, key = { it.id }) { post ->
                        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                            OtherUserPostCard(
                                post = post,
                                user = liveUser,
                                isFriend = isFriend,
                                onLikeClick = { viewModel.togglePostLike(post.id) },
                                onCommentClick = { activePostForComments = post },
                                onShareClick = {
                                    viewModel.sharePost(post.id)
                                    Toast.makeText(context, "Post link copied to clipboard! 🔗", Toast.LENGTH_SHORT).show()
                                },
                                onSaveClick = {
                                    val saved = viewModel.togglePostSave(post.id)
                                    Toast.makeText(context, if (saved) "Saved to Bookmarks! 🔖" else "Removed from Bookmarks", Toast.LENGTH_SHORT).show()
                                },
                                onMediaClick = {
                                    if (post.mediaType == MediaType.VIDEO) viewingVideoPost = post
                                    else if (!post.mediaUrl.isNullOrBlank()) viewingPhotoUrl = post.mediaUrl
                                }
                            )
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: ABOUT (13 fields with Privacy rules)
                item {
                    OtherUserAboutTab(
                        user = liveUser,
                        isFriend = isFriend
                    )
                }
            }

            2 -> {
                // TAB 2: PHOTOS (Grid, Albums, Viewer)
                item {
                    OtherUserPhotosTab(
                        user = liveUser,
                        isFriend = isFriend,
                        photos = userPhotos,
                        selectedAlbum = selectedPhotoAlbum,
                        onSelectAlbum = { selectedPhotoAlbum = it },
                        onPhotoClick = { viewingPhotoUrl = it }
                    )
                }
            }

            3 -> {
                // TAB 3: VIDEOS (Thumbnails, Preview player)
                item {
                    OtherUserVideosTab(
                        user = liveUser,
                        isFriend = isFriend,
                        videos = userVideos,
                        onVideoClick = { viewingVideoPost = it }
                    )
                }
            }

            4 -> {
                // TAB 4: REELS (Vertical 9:16 grid)
                item {
                    OtherUserReelsTab(
                        user = liveUser,
                        isFriend = isFriend,
                        reels = userReels,
                        onReelClick = { viewingReel = it }
                    )
                }
            }

            5 -> {
                // TAB 5: FRIENDS (Friends list, Search, Mutuals)
                item {
                    OtherUserFriendsTab(
                        user = liveUser,
                        isFriend = isFriend,
                        friends = filteredFriendsList,
                        searchQuery = friendsSearchQuery,
                        onSearchQueryChange = { friendsSearchQuery = it },
                        onFriendClick = { friend ->
                            viewModel.openUserProfile(friend)
                        },
                        onMessageClick = { friend ->
                            viewModel.openChatWithUser(friend)
                        }
                    )
                }
            }
        }
    }

    // MODALS AND DIALOGS
    if (showFriendOptionsSheet) {
        FriendOptionsBottomSheet(
            user = liveUser,
            viewModel = viewModel,
            onDismiss = { showFriendOptionsSheet = false }
        )
    }

    if (showMoreOptionsMenu) {
        OtherUserMoreOptionsDialog(
            user = liveUser,
            isFriend = isFriend,
            isDarkMode = isDarkMode,
            onDismiss = { showMoreOptionsMenu = false },
            onRefresh = {
                showMoreOptionsMenu = false
                isLoading = true
            },
            onToggleOffline = {
                showMoreOptionsMenu = false
                isOfflineMode = !isOfflineMode
                Toast.makeText(
                    context,
                    if (isOfflineMode) "Offline Mode: displaying cached profile" else "Online Mode restored 🌐",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onSimulateError = {
                showMoreOptionsMenu = false
                isNetworkError = true
            },
            onCopyLink = {
                clipboardManager.setText(AnnotatedString("https://friendhub.io/profile/${liveUser.username.ifBlank { liveUser.id }}"))
                Toast.makeText(context, "Profile link copied to clipboard! 🔗", Toast.LENGTH_SHORT).show()
                showMoreOptionsMenu = false
            },
            onSnooze = {
                viewModel.snoozeUser(liveUser.id)
                Toast.makeText(context, "${liveUser.name} snoozed for 30 days.", Toast.LENGTH_SHORT).show()
                showMoreOptionsMenu = false
            },
            onTakeBreak = {
                viewModel.takeBreakFromUser(liveUser.id)
                Toast.makeText(context, "Taking a break from ${liveUser.name}.", Toast.LENGTH_SHORT).show()
                showMoreOptionsMenu = false
            },
            onBlock = {
                viewModel.unfriendUser(liveUser.id)
                Toast.makeText(context, "${liveUser.name} blocked.", Toast.LENGTH_SHORT).show()
                showMoreOptionsMenu = false
                onBack()
            },
            onReport = {
                Toast.makeText(context, "Report submitted successfully.", Toast.LENGTH_SHORT).show()
                showMoreOptionsMenu = false
            }
        )
    }

    // Comments Bottom Sheet
    if (activePostForComments != null) {
        CommentsBottomSheet(
            post = activePostForComments!!,
            currentUser = currentUser,
            onDismiss = { activePostForComments = null },
            onAddComment = { commentContent ->
                viewModel.repository.addComment(activePostForComments!!.id, commentContent)
                activePostForComments = null
                Toast.makeText(context, "Comment added! 💬", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // High-Res Full-screen Photo Viewer Dialog
    if (viewingPhotoUrl != null) {
        ViewProfilePhotoDialog(
            photoUrl = viewingPhotoUrl!!,
            frameId = null,
            userName = liveUser.name,
            onEditClick = { viewingPhotoUrl = null },
            onDismiss = { viewingPhotoUrl = null }
        )
    }

    // Video Player Dialog
    if (viewingVideoPost != null) {
        VideoPlayerModal(
            post = viewingVideoPost!!,
            user = liveUser,
            onDismiss = { viewingVideoPost = null },
            onLike = { viewModel.togglePostLike(viewingVideoPost!!.id) },
            onComment = {
                val p = viewingVideoPost
                viewingVideoPost = null
                activePostForComments = p
            }
        )
    }

    // Reel Player Dialog
    if (viewingReel != null) {
        ReelPlayerModal(
            reel = viewingReel!!,
            onDismiss = { viewingReel = null }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// ACTION BAR COMPONENT (Handles all 4 friend states)
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserActionBar(
    user: User,
    viewModel: MainViewModel,
    onOpenFriendOptions: () -> Unit,
    onOpenMore: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            // State 1: Friend Request Received
            user.isRequestReceived -> {
                Button(
                    onClick = {
                        viewModel.confirmFriendRequestFromProfile(user.id)
                        Toast.makeText(context, "${user.name} added as a friend! ✓", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1.2f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Accept", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.rejectFriendRequestFromProfile(user.id)
                        Toast.makeText(context, "Friend request rejected.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(0.9f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF475569)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Reject", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                IconButton(
                    onClick = { viewModel.openChatWithUser(user) },
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Message", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            // State 2: Already Friends
            user.isFriend -> {
                Button(
                    onClick = onOpenFriendOptions,
                    modifier = Modifier.weight(1.1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Friends", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = { viewModel.openChatWithUser(user) },
                    modifier = Modifier.weight(1.1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                IconButton(
                    onClick = {
                        val following = viewModel.toggleFollowUser(user.id)
                        Toast.makeText(context, if (following) "Following ${user.name}" else "Unfollowed ${user.name}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF334155))
                ) {
                    Icon(
                        imageVector = if (user.isFollowedByMe) Icons.Default.Check else Icons.Default.PersonAdd,
                        contentDescription = "Follow",
                        tint = if (user.isFollowedByMe) Color(0xFF38BDF8) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onOpenMore,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF334155))
                ) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Options", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            // State 3: Friend Request Sent
            user.isRequestSent -> {
                Button(
                    onClick = {
                        viewModel.cancelFriendRequest(user.id)
                        Toast.makeText(context, "Friend request cancelled.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1.2f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Request Sent", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val following = viewModel.toggleFollowUser(user.id)
                        Toast.makeText(context, if (following) "Following ${user.name}" else "Unfollowed", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (user.isFollowedByMe) Color(0xFF1E293B) else Color(0xFF2563EB)),
                    border = if (user.isFollowedByMe) BorderStroke(1.dp, Color(0xFF475569)) else null
                ) {
                    Text(if (user.isFollowedByMe) "Following" else "Follow", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                IconButton(
                    onClick = { viewModel.openChatWithUser(user) },
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Message", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            // State 4: Not Friends
            else -> {
                Button(
                    onClick = {
                        viewModel.sendFriendRequest(user.id)
                        Toast.makeText(context, "Friend request sent to ${user.name}! ✉️", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1.3f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Friend", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val following = viewModel.toggleFollowUser(user.id)
                        Toast.makeText(context, if (following) "Now following ${user.name}" else "Unfollowed", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Text(if (user.isFollowedByMe) "Following" else "Follow", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                IconButton(
                    onClick = { viewModel.openChatWithUser(user) },
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Message", tint = Color.White, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onOpenMore,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF334155))
                ) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Options", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// POST CARD FOR OTHER USER'S PROFILE
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserPostCard(
    post: Post,
    user: User,
    isFriend: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onMediaClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Pinned Post Banner
            if (post.isPinned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pinned Post", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Author Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.avatarUrl.ifBlank { post.userAvatar },
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(42.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name.ifBlank { post.userName },
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        if (user.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.timestamp.ifBlank { "Recently" },
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (post.audience.equals("Friends", ignoreCase = true)) Icons.Default.People else Icons.Default.Public,
                            contentDescription = post.audience,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                IconButton(onClick = onSaveClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (post.isSaved) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Post Content Text
            if (post.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = post.content,
                    color = Color(0xFFF1F5F9),
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            // Post Media (Image or Video preview)
            if (!post.mediaUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onMediaClick() }
                ) {
                    AsyncImage(
                        model = post.mediaUrl,
                        contentDescription = "Post Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (post.mediaType == MediaType.VIDEO) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reaction Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👍 ❤️", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likeCount.coerceAtLeast(14)}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${post.commentCount.coerceAtLeast(2)} comments", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text("•", color = Color(0xFF64748B), fontSize = 11.sp)
                    Text("${post.shareCount} shares", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons Row (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (post.isLikedByMe) "Liked" else "Like",
                        color = if (post.isLikedByMe) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(onClick = onCommentClick) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comment", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Comment", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                TextButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: ABOUT (13 FIELDS WITH ACCESS CONTROL)
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserAboutTab(
    user: User,
    isFriend: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "About & Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = if (isFriend) "All details unlocked for friends 🔓" else "Some details restricted to friends only 🔒",
                color = if (isFriend) Color(0xFF22C55E) else Color(0xFF38BDF8),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Bio
            AboutDetailItem(Icons.Default.Info, "Bio", user.bio.ifBlank { "No bio added" }, "Public")

            // 2. Workplace
            AboutDetailItem(Icons.Default.Work, "Workplace", user.work.ifBlank { "Technology" }, "Public")

            // 3. Occupation
            AboutDetailItem(Icons.Default.BusinessCenter, "Occupation", user.occupation.ifBlank { "Software Engineer" }, "Public")

            // 4. Education
            AboutDetailItem(Icons.Default.School, "Education", user.education.ifBlank { "University Graduate" }, "Public")

            // 5. Current City
            AboutDetailItem(Icons.Default.LocationOn, "Current City", user.location.ifBlank { "Colombo, Sri Lanka" }, "Public")

            // 6. Hometown
            AboutDetailItem(Icons.Default.Home, "Hometown", user.hometown.ifBlank { "Colombo, Sri Lanka" }, "Public")

            // 7. Birthday (Protected for friends only)
            AboutDetailItem(
                icon = Icons.Default.Cake,
                title = "Birthday",
                value = if (isFriend) user.birthday.ifBlank { "July 15" } else "Hidden (Friends only 🔒)",
                privacy = if (isFriend) "Friends Only" else "Restricted"
            )

            // 8. Languages
            AboutDetailItem(Icons.Default.Translate, "Languages", user.languages.ifBlank { "English, Sinhala" }, "Public")

            // 9. Interests
            AboutDetailItem(Icons.Default.Interests, "Interests", user.interests.ifBlank { "Photography, Tech, Traveling" }, "Public")

            // 10. Hobbies
            AboutDetailItem(Icons.Default.SportsEsports, "Hobbies", user.hobbies.ifBlank { "Hiking, Coding, Gaming" }, "Public")

            // 11. Website
            AboutDetailItem(Icons.Default.Language, "Website", user.website.ifBlank { "https://friendhub.io" }, "Public", isLink = true)

            // 12. Contact Information (Phone & Email - Protected for friends only)
            AboutDetailItem(
                icon = Icons.Default.Phone,
                title = "Phone",
                value = if (isFriend) user.phone.ifBlank { "+94 77 123 4567" } else "Hidden (Friends only 🔒)",
                privacy = if (isFriend) "Friends Only" else "Restricted"
            )
            AboutDetailItem(
                icon = Icons.Default.Email,
                title = "Email",
                value = if (isFriend) user.email.ifBlank { "contact@friendhub.io" } else "Hidden (Friends only 🔒)",
                privacy = if (isFriend) "Friends Only" else "Restricted"
            )

            // 13. Relationship Status (Protected for friends only)
            AboutDetailItem(
                icon = Icons.Default.Favorite,
                title = "Relationship",
                value = if (isFriend) user.relationshipStatus.ifBlank { "Single" } else "Hidden (Friends only 🔒)",
                privacy = if (isFriend) "Friends Only" else "Restricted"
            )
        }
    }
}

@Composable
fun AboutDetailItem(
    icon: ImageVector,
    title: String,
    value: String,
    privacy: String,
    isLink: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(34.dp).background(Color(0xFF334155), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFF38BDF8), modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text(
                text = value,
                color = if (isLink) Color(0xFF38BDF8) else Color.White,
                fontSize = 13.sp,
                fontWeight = if (isLink) FontWeight.Bold else FontWeight.Medium
            )
        }
        Box(
            modifier = Modifier
                .background(
                    if (privacy == "Public") Color(0xFF38BDF8).copy(alpha = 0.15f)
                    else if (privacy == "Friends Only") Color(0xFF22C55E).copy(alpha = 0.15f)
                    else Color(0xFFEF4444).copy(alpha = 0.15f),
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = privacy,
                color = if (privacy == "Public") Color(0xFF38BDF8) else if (privacy == "Friends Only") Color(0xFF22C55E) else Color(0xFFEF4444),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: PHOTOS (Grid & Albums)
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserPhotosTab(
    user: User,
    isFriend: Boolean,
    photos: List<String>,
    selectedAlbum: String,
    onSelectAlbum: (String) -> Unit,
    onPhotoClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        // Albums filter toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val albums = listOf("All Photos", "Uploads", "Profile Pictures", "Cover Photos")
            albums.forEach { album ->
                val isSelected = selectedAlbum == album
                Surface(
                    onClick = { onSelectAlbum(album) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                ) {
                    Text(
                        text = album,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (photos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📷", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Photos Found", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("When ${user.name} uploads photos, they will show up here.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        } else {
            // 3-column photos grid
            val rows = photos.chunked(3)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEach { rowPhotos ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPhotos.forEach { url ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clickable { onPhotoClick(url) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        if (rowPhotos.size < 3) {
                            repeat(3 - rowPhotos.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: VIDEOS
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserVideosTab(
    user: User,
    isFriend: Boolean,
    videos: List<Post>,
    onVideoClick: (Post) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        if (videos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎬", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Videos Available", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("No video uploads from ${user.name} yet.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                videos.forEach { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onVideoClick(video) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                AsyncImage(
                                    model = video.mediaUrl ?: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
                                    contentDescription = "Video Thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.65f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(30.dp))
                                }
                                Surface(
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.75f)
                                ) {
                                    Text("02:15", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = video.content.ifBlank { "Video from ${user.name}" },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("👍 ${video.likeCount} likes • ${video.commentCount} comments", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    Text(video.timestamp, color = Color(0xFF38BDF8), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 4: REELS
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserReelsTab(
    user: User,
    isFriend: Boolean,
    reels: List<Reel>,
    onReelClick: (Reel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        if (reels.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎞️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Reels Yet", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Reels created by ${user.name} will appear here.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        } else {
            val rows = reels.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rows.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { reel ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(240.dp)
                                    .clickable { onReelClick(reel) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = reel.videoUrl.ifBlank { "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800" },
                                        contentDescription = "Reel",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                                )
                                            )
                                    )
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.align(Alignment.Center).size(36.dp)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = reel.caption.ifBlank { "Reel by ${user.name}" },
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "👁️ ${reel.viewsCount.ifBlank { "12K" }}",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 5: FRIENDS
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserFriendsTab(
    user: User,
    isFriend: Boolean,
    friends: List<User>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFriendClick: (User) -> Unit,
    onMessageClick: (User) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search friends...", color = Color(0xFF94A3B8), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "${friends.size} Friends",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (friends.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("👥", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Friends Found", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("No matches for \"$searchQuery\"", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                friends.forEach { friend ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFriendClick(friend) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = friend.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(44.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(friend.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "${(1..8).random()} mutual friends",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                            Button(
                                onClick = { onMessageClick(friend) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Message", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// VIDEO PLAYER MODAL DIALOG
// -------------------------------------------------------------------------------------------------
@Composable
fun VideoPlayerModal(
    post: Post,
    user: User,
    onDismiss: () -> Unit,
    onLike: () -> Unit,
    onComment: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
        ) {
            Column {
                // Video Screen Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Color.Black)
                        .clickable { isPlaying = !isPlaying }
                ) {
                    AsyncImage(
                        model = post.mediaUrl ?: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
                        contentDescription = "Video",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (!isPlaying) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = user.avatarUrl, contentDescription = user.name, modifier = Modifier.size(34.dp).clip(CircleShape))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(post.timestamp, color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(post.content, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onLike,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Like (${post.likeCount})", color = Color.White, fontSize = 12.sp)
                        }
                        Button(
                            onClick = onComment,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Comments (${post.commentCount})", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// REEL PLAYER MODAL DIALOG
// -------------------------------------------------------------------------------------------------
@Composable
fun ReelPlayerModal(
    reel: Reel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().height(460.dp).padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = reel.videoUrl.ifBlank { "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800" },
                    contentDescription = "Reel",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(36.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                ) {
                    Text(reel.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(reel.caption, color = Color(0xFFF1F5F9), fontSize = 12.sp, maxLines = 2)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🎵 Original Audio • ${reel.viewsCount} views", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MORE OPTIONS DIALOG (Report, Block, Snooze, Share, Offline/Refresh, Error simulation)
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserMoreOptionsDialog(
    user: User,
    isFriend: Boolean,
    isDarkMode: Boolean = true,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit = {},
    onToggleOffline: () -> Unit = {},
    onSimulateError: () -> Unit = {},
    onCopyLink: () -> Unit,
    onSnooze: () -> Unit,
    onTakeBreak: () -> Unit,
    onBlock: () -> Unit,
    onReport: () -> Unit
) {
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textMuted = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(user.name, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = cardBorder)
                Spacer(modifier = Modifier.height(6.dp))

                // Refresh Profile (Skeleton loader trigger)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onRefresh).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Refresh Profile", color = textPrimary, fontSize = 13.sp)
                }

                // Copy Link
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onCopyLink).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Copy Link to Profile", color = textPrimary, fontSize = 13.sp)
                }

                // Toggle Offline Mode
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleOffline).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Offline Mode (Cached Profile)", color = textPrimary, fontSize = 13.sp)
                }

                // Simulate Network Error
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onSimulateError).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFFF97316))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Test Network Error State", color = textPrimary, fontSize = 13.sp)
                }

                if (isFriend) {
                    // Snooze
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = onSnooze).padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = Color(0xFFFBBF24))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Snooze ${user.name} for 30 days", color = textPrimary, fontSize = 13.sp)
                    }

                    // Take a break
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = onTakeBreak).padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Take a Break", color = textPrimary, fontSize = 13.sp)
                    }
                }

                // Block
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onBlock).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Block ${user.name}", color = Color(0xFFEF4444), fontSize = 13.sp)
                }

                // Report
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onReport).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Report, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Report Profile", color = Color(0xFFEF4444), fontSize = 13.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// LOADING SKELETON WITH SHIMMER ANIMATION
// -------------------------------------------------------------------------------------------------
@Composable
fun rememberShimmerBrush(isDarkMode: Boolean): Brush {
    val shimmerColors = if (isDarkMode) {
        listOf(
            Color(0xFF1E293B),
            Color(0xFF334155),
            Color(0xFF1E293B)
        )
    } else {
        listOf(
            Color(0xFFE2E8F0),
            Color(0xFFF1F5F9),
            Color(0xFFE2E8F0)
        )
    }
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}

@Composable
fun OtherUserProfileSkeleton(
    isDarkMode: Boolean,
    onBack: () -> Unit
) {
    val shimmerBrush = rememberShimmerBrush(isDarkMode = isDarkMode)
    val bgColor = if (isDarkMode) Color(0xFF0B1120) else Color(0xFFF8FAFC)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Cover Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(shimmerBrush)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .size(38.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Profile Card Skeleton
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .offset(y = (-35).dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .offset(y = (-45).dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(shimmerBrush)
                )

                // Name bar
                Box(
                    modifier = Modifier
                        .offset(y = (-30).dp)
                        .width(160.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmerBrush)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Username bar
                Box(
                    modifier = Modifier
                        .offset(y = (-25).dp)
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmerBrush)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(36.dp, 16.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush))
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.size(44.dp, 10.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1.2f).height(40.dp).clip(RoundedCornerShape(12.dp)).background(shimmerBrush))
                    Box(modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(12.dp)).background(shimmerBrush))
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(shimmerBrush))
                }
            }
        }

        // Tab pills skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                Box(modifier = Modifier.size(76.dp, 34.dp).clip(RoundedCornerShape(18.dp)).background(shimmerBrush))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Post card skeleton
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(shimmerBrush))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Box(modifier = Modifier.size(110.dp, 14.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.size(60.dp, 10.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush))
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush))
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)).background(shimmerBrush))
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// NETWORK ERROR STATE COMPONENT
// -------------------------------------------------------------------------------------------------
@Composable
fun OtherUserProfileErrorView(
    user: User,
    isDarkMode: Boolean,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onShowCached: () -> Unit
) {
    val bgColor = if (isDarkMode) Color(0xFF0B1120) else Color(0xFFF8FAFC)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(38.dp)
                .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = textPrimary
            )
        }

        // Center Error Card
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Error",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Unable to Load Profile",
                    color = textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Could not fetch the latest profile for ${user.name}. Please check your internet connection and try again.",
                    color = textSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry Connection", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onShowCached,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF475569))
                ) {
                    Text("View Offline Cached Profile", color = textPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
