package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.R
import com.example.model.MediaType
import com.example.model.Post
import com.example.model.Reel
import com.example.ui.components.AnimatedNeonVideoIcon
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.PostShareBottomSheet
import com.example.ui.components.PostOptionsBottomSheet
import com.example.animation.FloatingHeartsOverlay
import com.example.animation.rememberFloatingHeartsState
import com.example.animation.FloatingHeartsState
import com.example.viewmodel.MainViewModel

@Composable
fun VideosScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val reels by viewModel.reels.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    // Bottom Sheets states
    var selectedReelForShare by remember { mutableStateOf<Reel?>(null) }
    var selectedReelForComments by remember { mutableStateOf<Reel?>(null) }
    var selectedReelForOptions by remember { mutableStateOf<Reel?>(null) }

    val floatingHeartsState = rememberFloatingHeartsState()
    
    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (reels.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OndemandVideo,
                    contentDescription = null,
                    tint = Color(0xFFA855F7),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No videos yet. Be the first to post a reel!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "තවමත් වීඩියෝ නොමැත. ප්‍රථම රීල් වීඩියෝව එකතු කරන්න!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.setCreatePostOpen(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA855F7),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Video / Reel")
                }
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = reels[page]
                ReelPlayerItem(
                    reel = reel,
                    isCurrentPage = pagerState.currentPage == page,
                    floatingHeartsState = floatingHeartsState,
                    onCommentClick = { selectedReelForComments = reel },
                    onShareClick = { selectedReelForShare = reel },
                    onOptionsClick = { selectedReelForOptions = reel }
                )
            }
        }
        
        // Top overlay (back, watch, live)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimatedNeonVideoIcon(
                    size = 36.dp,
                    animateGlow = true
                )
                Text(
                    text = "Watch",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color.White
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Upload Reel / Video Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { viewModel.setCreatePostOpen(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Reel / Post Video",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Search Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { viewModel.setSearchOpen(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    FloatingHeartsOverlay(state = floatingHeartsState, modifier = Modifier.fillMaxSize())

    // Modals
    selectedReelForShare?.let { reel ->
        PostShareBottomSheet(
            postTitle = reel.caption,
            postUrl = reel.videoUrl,
            viewModel = viewModel,
            onShareToFriend = { name, avatar ->
                viewModel.sharePostToFriend(name, avatar, reel.caption, reel.videoUrl)
            },
            onDismiss = { selectedReelForShare = null }
        )
    }

    selectedReelForComments?.let { reel ->
        val syntheticPost = Post(
            id = reel.id,
            userName = reel.creatorName,
            userAvatar = reel.creatorAvatar,
            content = reel.caption,
            mediaUrl = reel.videoUrl,
            mediaType = MediaType.VIDEO
        )
        CommentsBottomSheet(
            post = syntheticPost,
            currentUser = currentUser,
            onDismiss = { selectedReelForComments = null },
            onAddComment = { /* Add logic */ }
        )
    }

    selectedReelForOptions?.let { reel ->
        val syntheticPost = Post(
            id = reel.id,
            userName = reel.creatorName,
            userAvatar = reel.creatorAvatar,
            content = reel.caption,
            mediaUrl = reel.videoUrl,
            mediaType = MediaType.VIDEO
        )
        PostOptionsBottomSheet(
            post = syntheticPost,
            viewModel = viewModel,
            onDismiss = { selectedReelForOptions = null },
            onShareClick = {
                selectedReelForOptions = null
                selectedReelForShare = reel
            }
        )
    }
}

@Composable
fun ReelPlayerItem(
    reel: Reel,
    isCurrentPage: Boolean,
    floatingHeartsState: FloatingHeartsState,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Native Android Video Player Component
        var isPrepared by remember { mutableStateOf(false) }
        var hasVideoError by remember { mutableStateOf(false) }

        if (hasVideoError || reel.videoUrl.isBlank()) {
            AsyncImage(
                model = if (reel.videoUrl.isNotBlank()) reel.videoUrl else "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
                contentDescription = reel.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        setOnErrorListener { _, _, _ ->
                            hasVideoError = true
                            true // Handled error
                        }
                        setOnPreparedListener { mediaPlayer ->
                            isPrepared = true
                            mediaPlayer.isLooping = true
                            try {
                                if (isCurrentPage) {
                                    start()
                                }
                            } catch (e: Exception) {
                                hasVideoError = true
                            }
                        }
                        if (reel.videoUrl.isNotBlank()) {
                            try {
                                setVideoURI(Uri.parse(reel.videoUrl))
                            } catch (e: Exception) {
                                hasVideoError = true
                            }
                        }
                    }
                },
                update = { videoView ->
                    if (isPrepared && !hasVideoError) {
                        try {
                            if (isCurrentPage) {
                                if (!videoView.isPlaying) videoView.start()
                            } else {
                                if (videoView.isPlaying) videoView.pause()
                            }
                        } catch (e: Exception) {
                            hasVideoError = true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay gradient at bottom for text visibility
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )

        // Bottom Left Info (Creator, Caption, Music)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.8f) // leave space for right actions
                .padding(start = 16.dp, bottom = 100.dp) // space for bottom bar
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = reel.creatorAvatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "@${reel.creatorName}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                if (!isFollowing) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                            .clickable { isFollowing = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Follow", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = reel.caption,
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Original Audio - ${reel.creatorName}", color = Color.White, fontSize = 14.sp)
            }
        }

        // Right Edge Actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { 
                    isLiked = !isLiked 
                    if(isLiked) floatingHeartsState.emitHearts(3)
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFE41E3F) else Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(text = reel.likesCount, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            
            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onCommentClick) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Comment",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(text = "342", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onShareClick) {
                    com.example.ui.components.Custom3DShareIcon(size = 36.dp)
                }
                Text(text = "Share", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Options
            IconButton(onClick = onOptionsClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
