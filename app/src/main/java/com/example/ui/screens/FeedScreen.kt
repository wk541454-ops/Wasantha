package com.example.ui.screens

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.net.Uri
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.ui.components.NativeSponsoredFeedCard
import com.example.ui.components.sampleSponsoredAds
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import com.example.animation.FloatingHeartsOverlay
import com.example.animation.rememberFloatingHeartsState
import com.example.model.MediaType
import com.example.model.Post
import com.example.model.ReactionType
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.PostCard
import com.example.ui.components.StoryBar
import com.example.ui.components.PostShareBottomSheet
import com.example.ui.components.PostOptionsBottomSheet
import com.example.ui.components.StoryViewerDialog
import com.example.ui.components.CreateStoryFlowModal
import com.example.ui.components.TikTokLiveStudioModal
import com.example.ui.components.MediaSourcePickerBottomSheet
import com.example.ui.components.PostVideoCreateBottomSheet
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurfaceVariant
import com.example.viewmodel.MainViewModel
import com.example.ui.components.home.AmbientParticles
import com.example.ui.components.home.GamificationTopBar
import com.example.ui.components.home.LivePreviewFloatingWindow

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import kotlinx.coroutines.launch
import com.example.ui.components.WhatsOnYourMindCard
import com.example.ui.components.ReelsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val reels by viewModel.reels.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val feedFilter by viewModel.feedFilter.collectAsState()
    val activeStory by viewModel.activeStory.collectAsState()
    val activePostForComments by viewModel.activePostForComments.collectAsState()
    val isCreatePostOpen by viewModel.isCreatePostOpen.collectAsState()
    val isCreateStoryOpen by viewModel.isCreateStoryOpen.collectAsState()
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val visitedPostIds by viewModel.visitedPostIds.collectAsState()

    val floatingHeartsState = rememberFloatingHeartsState()

    val filteredPosts = when (feedFilter) {
        "TRENDING" -> posts.sortedByDescending { it.likeCount }
        "MEDIA" -> posts.filter { it.mediaType != MediaType.NONE }
        "ENCRYPTED" -> posts.filter { it.isEncrypted }
        else -> posts
    }

    // When data is finished / offline: show ONLY what the user previously visited/browsed, or fallback sample posts
    val baseDisplayPosts = if (!isNetworkAvailable) {
        val cached = filteredPosts.filter { it.id in visitedPostIds }
        if (cached.isNotEmpty()) cached else filteredPosts.take(2)
    } else {
        filteredPosts
    }

    val displayPosts = if (baseDisplayPosts.isEmpty()) {
        listOf(
            Post(
                id = "fallback_1",
                userId = "user_sample_1",
                userName = "Kasun Perera",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&auto=format&fit=crop&q=80",
                userVerified = true,
                timestamp = "2 hours ago",
                content = "Welcome to FriendHub! Enjoy connecting with your friends, sharing stories, and exploring the modern community feed. ✨",
                mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop&q=80",
                mediaType = MediaType.IMAGE,
                likeCount = 42,
                commentCount = 5,
                shareCount = 2,
                isLikedByMe = false
            ),
            Post(
                id = "fallback_2",
                userId = "user_sample_2",
                userName = "Nirosha Silva",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
                userVerified = false,
                timestamp = "5 hours ago",
                content = "Beautiful evening walk at Galle Face! 🌅🇱🇰",
                mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
                mediaType = MediaType.IMAGE,
                likeCount = 128,
                commentCount = 22,
                shareCount = 11,
                isLikedByMe = true
            )
        )
    } else {
        baseDisplayPosts
    }

    val context = LocalContext.current
    var initialPostMediaType by remember { mutableStateOf(MediaType.NONE) }
    var initialMediaUrl by remember { mutableStateOf<String?>(null) }
    var showMediaSourcePickerSheet by remember { mutableStateOf(false) }
    var selectedPostForShare by remember { mutableStateOf<Post?>(null) }
    var selectedPostForOptions by remember { mutableStateOf<Post?>(null) }
    var isLiveStudioOpen by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uriStr = uri.toString()
            initialMediaUrl = uriStr
            initialPostMediaType = MediaType.IMAGE
            viewModel.setCreatePostOpen(true)
            Toast.makeText(context, "ඡායාරූපය සාර්ථකව තෝරා ගන්නා ලදී! 📷", Toast.LENGTH_SHORT).show()
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uriStr = uri.toString()
            initialMediaUrl = uriStr
            initialPostMediaType = MediaType.VIDEO
            viewModel.setCreatePostOpen(true)
            Toast.makeText(context, "වීඩියෝව සාර්ථකව තෝරා ගන්නා ලදී! 🎥", Toast.LENGTH_SHORT).show()
        }
    }

    val density = androidx.compose.ui.platform.LocalDensity.current.density
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Modern Dark Slate Canvas
    ) {
        AmbientParticles()
        Column(modifier = Modifier.fillMaxSize()) {
            GamificationTopBar()

            // Feed Content
            // Main Feed Area with Pull-to-Refresh
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        viewModel.refreshFeed()
                        kotlinx.coroutines.delay(800)
                        isRefreshing = false
                        Toast.makeText(context, "පුවත් යාවත්කාලීන කරන ලදී! ✨", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A)), // Modern Dark Slate
                    contentPadding = PaddingValues(bottom = 100.dp) // Space for floating bottom bar
                ) {
                // Notice when viewing offline cached posts only
                if (!isNetworkAvailable) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF242526))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📌 කලින් ඔබ බැලූ පුවත් පමණක් පෙන්වයි (${displayPosts.size})",
                                fontSize = 12.sp,
                                color = Color(0xFFB0B3B8),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // What's on your mind Card (Top input bar)
                item {
                    WhatsOnYourMindCard(
                        user = currentUser,
                        onProfileClick = { viewModel.selectTab(7) }, // Navigates directly to Profile Screen
                        onOpenCreatePost = {
                            initialMediaUrl = null
                            initialPostMediaType = MediaType.NONE
                            viewModel.setCreatePostOpen(true)
                        },
                        onOpenPhotoPicker = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onOpenVideoPicker = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        onOpenLiveClick = {
                            isLiveStudioOpen = true
                        }
                    )
                }

                // Top Status / Stories Bar (Horizontal rectangular story cards)
                item {
                    StoryBar(
                        stories = stories,
                        onStoryClick = { story ->
                            if (story.isLive) {
                                viewModel.setLiveStreamingOpen(true)
                                viewModel.startLiveStream()
                            } else {
                                viewModel.openStory(story)
                            }
                        },
                        onAddStoryClick = { viewModel.setCreateStoryOpen(true) },
                        onLiveClick = {
                            viewModel.setLiveStreamingOpen(true)
                        },
                        currentUserAvatar = currentUser.avatarUrl
                    )
                }

                // Posts List
                if (displayPosts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(text = "කිසිදු පුවතක් හමුවී නැත (No posts found)", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    itemsIndexed(displayPosts, key = { _, post -> post.id }) { index, post ->
                        // Calculate item index offset for 3D layout info (item index in LazyColumn is index + 4 because of the 4 items before it: notice, mindCard, storyBar, emptyState)
                        // Actually, just find the item by key or by layoutInfo directly.
                        val itemIndexInList = index + if (!isNetworkAvailable) 3 else 2 
                        
                        PostCard(
                            post = post,
                            floatingHeartsState = floatingHeartsState,
                            onLikeClick = { viewModel.togglePostLike(post.id) },
                            onCommentClick = { viewModel.openCommentsForPost(post) },
                            onShareClick = {
                                viewModel.incrementPostLikes(post.id, 0)
                                selectedPostForShare = post
                            },
                            onSelectReaction = { rx -> viewModel.setPostReaction(post.id, rx) },
                            onAuthorClick = {
                                val user = viewModel.knownUsers.value[post.userId] ?: com.example.model.User(
                                    id = post.userId,
                                    name = post.userName,
                                    avatarUrl = post.userAvatar
                                )
                                viewModel.viewUserProfile(user)
                            },
                            onOptionsClick = { selectedPostForOptions = post },
                            isLightMode = false,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Insert a Facebook-style Native Sponsored Feed Card every 2 posts
                        if ((index + 1) % 2 == 0) {
                            val adIndex = ((index + 1) / 2 - 1) % sampleSponsoredAds.size
                            Spacer(modifier = Modifier.height(10.dp))
                            NativeSponsoredFeedCard(ad = sampleSponsoredAds[adIndex])
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }

    // Floating Hearts Overlay across screen
    FloatingHeartsOverlay(
        state = floatingHeartsState,
        modifier = Modifier.fillMaxSize()
    )

    // Story Viewer Modal
    activeStory?.let { story ->
        StoryViewerDialog(
            story = story,
            currentUser = currentUser,
            friendStories = stories,
            onDismiss = { viewModel.closeStory() },
            onReact = { emoji -> viewModel.reactToStory(story.id, emoji) },
            onToggleLike = { viewModel.toggleStoryLike(story) },
            onComment = { comment -> viewModel.sendStoryCommentNotification(story, comment) },
            onSelectOtherStory = { otherStory -> viewModel.openStory(otherStory) }
        )
    }

    // Comments Bottom Sheet
    activePostForComments?.let { post ->
        CommentsBottomSheet(
            post = post,
            currentUser = currentUser,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { viewModel.addCommentToActivePost(it) }
        )
    }

    // Create Post Dialog
    if (isCreatePostOpen) {
        CreatePostDialog(
            currentUser = currentUser,
            initialMediaType = initialPostMediaType,
            initialMediaUrl = initialMediaUrl,
            onDismiss = {
                viewModel.setCreatePostOpen(false)
                initialMediaUrl = null
            },
            onSubmitPost = { content, mediaUrl, mediaType ->
                viewModel.createPost(content, mediaUrl, mediaType)
                initialMediaUrl = null
            }
        )
    }

    // Media Source Picker Bottom Sheet (Camera vs Gallery vs Video)
    if (showMediaSourcePickerSheet) {
        MediaSourcePickerBottomSheet(
            onDismiss = { showMediaSourcePickerSheet = false },
            onPhotoCaptured = { uri ->
                initialMediaUrl = uri
                initialPostMediaType = MediaType.IMAGE
                viewModel.setCreatePostOpen(true)
            },
            onGalleryPhotoPicked = { uri ->
                initialMediaUrl = uri
                initialPostMediaType = MediaType.IMAGE
                viewModel.setCreatePostOpen(true)
            },
            onGalleryVideoPicked = { uri ->
                initialMediaUrl = uri
                initialPostMediaType = MediaType.VIDEO
                viewModel.setCreatePostOpen(true)
            }
        )
    }

    // Post Share Sheet
    selectedPostForShare?.let { post ->
        PostShareBottomSheet(
            postTitle = post.content,
            postUrl = post.mediaUrl,
            viewModel = viewModel,
            onShareToFriend = { name, avatar ->
                viewModel.sharePostToFriend(name, avatar, post.content, post.mediaUrl)
            },
            onDismiss = { selectedPostForShare = null }
        )
    }

    // Post Options Bottom Sheet (3-Dots Menu)
    selectedPostForOptions?.let { post ->
        PostOptionsBottomSheet(
            post = post,
            viewModel = viewModel,
            onDismiss = { selectedPostForOptions = null },
            onShareClick = {
                selectedPostForOptions = null
                selectedPostForShare = post
            }
        )
    }

    // Facebook Lite Full Story Flow (Picker + Editor with all tools)
    if (isCreateStoryOpen) {
        CreateStoryFlowModal(
            onDismiss = { viewModel.setCreateStoryOpen(false) },
            onSubmitStory = { url, caption, bgColor ->
                viewModel.createStory(url, caption, bgColor)
            }
        )
    }

    // Live Broadcast & Recording Studio
    if (isLiveStudioOpen) {
        viewModel.setLiveStreamingOpen(true)
        isLiveStudioOpen = false
    }
}

@Composable
fun FilterChipsRow(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf(
        "ALL" to "All Feed",
        "TRENDING" to "Trending 🔥",
        "MEDIA" to "Photos & Videos 📷",
        "ENCRYPTED" to "Encrypted 🔒"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (key, label) ->
            val isSelected = selectedFilter == key
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) NeonBlue.copy(alpha = 0.2f) else OledSurfaceVariant)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) NeonBlue else OledCardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelectFilter(key) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = if (isSelected) NeonBlue else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(filter: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.RssFeed,
            contentDescription = "Empty",
            tint = NeonPurple,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No posts found for $filter filter",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Be the first to share something exciting with your friends!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CreateStoryDialog(
    onDismiss: () -> Unit,
    onSubmitStory: (mediaUrl: String?, caption: String, backgroundColor: String?) -> Unit
) {
    val context = LocalContext.current
    var captionText by remember { mutableStateOf("") }
    var selectedMediaUrl by remember { mutableStateOf<String?>(null) }
    var selectedBgColor by remember { mutableStateOf(Color(0xFF1877F2)) } // Default FB Blue

    val backgroundColors = listOf(
        Color(0xFF1877F2), // FB Blue
        Color(0xFFE41E3F), // Red
        Color(0xFF10B981), // Green
        Color(0xFFA855F7), // Purple
        Color(0xFFFF9800), // Orange
        Color(0xFF242526)  // Dark
    )

    val quickEmojis = listOf("😊", "❤️", "🔥", "👍", "🌟", "🚀", "🎉", "😎", "💯", "🙏")

    val storyPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaUrl = uri.toString()
            Toast.makeText(context, "කතාන්දරයට ඡායාරූපය එකතු කරන ලදී! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
            border = androidx.compose.foundation.BorderStroke(1.dp, OledCardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Story (කතාවක් එක් කරන්න)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Preview box (Story card preview with selected bg color or image)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selectedMediaUrl == null) selectedBgColor else Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedMediaUrl != null) {
                        AsyncImage(
                            model = selectedMediaUrl,
                            contentDescription = "Story Media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = if (captionText.isBlank()) "ඔබේ කතාව මෙතැන ලියන්න... ✍️" else captionText,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Text Input
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    placeholder = { Text("What's on your story? (කතාවට සටහනක් ලියන්න)", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = Color(0xFF3A3B3C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF3A3B3C),
                        unfocusedContainerColor = Color(0xFF3A3B3C)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Emoji Quick bar
                Text(text = "Add Emojis (ඉමොජි එකතු කරන්න):", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickEmojis) { emoji ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3A3B3C))
                                .clickable { captionText += emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Background Color Picker (when no image selected)
                if (selectedMediaUrl == null) {
                    Text(text = "Background Color (පසුබිම් වර්ණය):", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        backgroundColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(2.dp, if (selectedBgColor == color) Color.White else Color.Transparent, CircleShape)
                                    .clickable { selectedBgColor = color }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Gallery Button
                Button(
                    onClick = {
                        storyPhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF31A24C))
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ගැලරියෙන් පින්තූරයක් තෝරන්න (Gallery)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Publish Button
                Button(
                    onClick = {
                        val colorHex = if (selectedMediaUrl == null) {
                            String.format("#%06X", (0xFFFFFF and selectedBgColor.toArgb()))
                        } else null
                        onSubmitStory(selectedMediaUrl, if (captionText.isBlank()) "New Story 🌟" else captionText, colorHex)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                ) {
                    Text("Publish Story (කතාව ප්‍රකාශ කරන්න 🚀)", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                }
            }
        }
    }
}
}

