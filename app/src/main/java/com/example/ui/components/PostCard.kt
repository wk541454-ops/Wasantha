package com.example.ui.components
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.withStyle

import androidx.compose.foundation.Image
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animation.FloatingHeartsState
import com.example.model.MediaType
import com.example.model.Post
import com.example.ui.theme.HeartRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import com.example.model.ReactionType

import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share

import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Add
import com.example.model.FactCheckStatus
import com.example.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.widthIn

import androidx.compose.ui.input.pointer.*
import androidx.compose.foundation.gestures.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.offset

@Composable
fun PostCard(
    post: Post,
    viewModel: com.example.viewmodel.MainViewModel,
    modifier: Modifier = Modifier,
    isLightMode: Boolean = true,
    onAuthorClick: (() -> Unit)? = null,
    onOptionsClick: (() -> Unit)? = null
) {
    val floatingHeartsState = remember { FloatingHeartsState() }
    val context = LocalContext.current
    PostCard(
        post = post,
        floatingHeartsState = floatingHeartsState,
        onLikeClick = { viewModel.togglePostLike(post.id) },
        onCommentClick = { viewModel.openCommentsForPost(post) },
        onShareClick = {
            viewModel.sharePost(post.id)
            Toast.makeText(context, "Post shared successfully! 🚀", Toast.LENGTH_SHORT).show()
        },
        onSelectReaction = { rx -> viewModel.setPostReaction(post.id, rx) },
        onAuthorClick = onAuthorClick ?: {
            val user = viewModel.knownUsers.value[post.userId] ?: com.example.model.User(
                id = post.userId,
                name = post.userName,
                avatarUrl = post.userAvatar
            )
            viewModel.viewUserProfile(user)
        },
        onOptionsClick = onOptionsClick,
        isLightMode = isLightMode,
        modifier = modifier
    )
}

@Composable
fun PostCard(
    post: Post,
    floatingHeartsState: FloatingHeartsState,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSelectReaction: ((ReactionType) -> Unit)? = null,
    onAuthorClick: (() -> Unit)? = null,
    onOptionsClick: (() -> Unit)? = null,
    isLightMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSaved by remember { mutableStateOf(post.isSaved) }
    var isPlayingVideo by remember { mutableStateOf(false) }
    var showReactionsPopup by remember { mutableStateOf(false) }
    var showMoreEmojis by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }
    var currentReactionEmoji by remember(post.id, post.isLikedByMe) {
        mutableStateOf<String?>(if (post.selectedReaction != com.example.model.ReactionType.NONE) post.selectedReaction.emoji else if (post.isLikedByMe) "👍" else null)
    }
    val likeScale = remember { androidx.compose.animation.core.Animatable(1f) }
    
    // Theme Colors
    val cardBackground = Color(0xFF1E293B) // Modern Dark Slate Card #1E293B
    val primaryTextColor = Color(0xFFF8FAFC)
    val secondaryTextColor = Color(0xFF94A3B8)
    val actionButtonTint = Color(0xFF94A3B8)
    val dividerColor = Color(0xFF334155)
    val popupBgColor = Color(0xFF1E293B)
    val popupBorderColor = Color(0xFF334155)

    // TikTok Heart Animation State
    var showTikTokHeart by remember { mutableStateOf(false) }
    val heartAlpha by animateFloatAsState(
        targetValue = if (showTikTokHeart) 1f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )
    val heartScale by animateFloatAsState(
        targetValue = if (showTikTokHeart) 1.5f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = ""
    )

    androidx.compose.runtime.LaunchedEffect(showTikTokHeart) {
        if (showTikTokHeart) {
            delay(800)
            showTikTokHeart = false
        }
    }

    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFF334155), Color(0xFF1E293B))
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Avatar, Name, Verified Badge, Time, Privacy Icon, More (...) and Close (X)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.userAvatar,
                    contentDescription = post.userName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(enabled = onAuthorClick != null) { onAuthorClick?.invoke() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = onAuthorClick != null) { onAuthorClick?.invoke() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.userName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = primaryTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (post.userVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified User",
                                tint = Color(0xFF00FF94), // Emerald Green
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${post.timestamp} • 🌐",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = secondaryTextColor
                        )
                    }
                }

                IconButton(onClick = { onOptionsClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = secondaryTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content Text
            if (post.content.isNotBlank()) {
                val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
                    val words = post.content.split(Regex("(?<=\\s)|(?=\\s)"))
                    for (word in words) {
                        if (word.startsWith("#") && word.length > 1) {
                            withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color(0xFF00F5FF))) { // Neon Cyan
                                append(word)
                            }
                        } else {
                            append(word)
                        }
                    }
                }
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = primaryTextColor
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Media Attachment (Image / Video) - TikTok Style Interactions
            if (post.mediaUrl != null && post.mediaType != MediaType.NONE) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF080808))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (!post.isLikedByMe) {
                                        onLikeClick()
                                    }
                                    showTikTokHeart = true
                                    floatingHeartsState.emitReaction("❤️", 30)
                                }
                            )
                        }
                ) {
                    if (post.mediaType == MediaType.IMAGE) {
                        AsyncImage(
                            model = post.mediaUrl,
                            contentDescription = "Post Media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f) // Square for consistency in luxury feeds
                        )
                    } else if (post.mediaType == MediaType.VIDEO) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(9f / 16f) // Reels style
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80",
                                contentDescription = "Video Thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )

                            // Video Play/Pause Overlay Button
                            IconButton(
                                onClick = { isPlayingVideo = !isPlayingVideo },
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00F5FF).copy(alpha = 0.8f))
                            ) {
                                Icon(
                                    imageVector = if (isPlayingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Video",
                                    tint = Color.Black,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // TikTok Double-Tap Heart Animation
                    if (heartAlpha > 0f) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFFF3131),
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.Center)
                                .graphicsLayer {
                                    scaleX = heartScale
                                    scaleY = heartScale
                                    alpha = heartAlpha
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interaction Bar: Premium Facebook Style Space-Between Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val isReacted = currentReactionEmoji != null || post.isLikedByMe
                val activeEmoji = currentReactionEmoji ?: if (post.isLikedByMe) "👍" else null
                
                val reactionColor = when (activeEmoji) {
                    "❤️" -> Color(0xFFFF3131)
                    "👍" -> Color(0xFF00F5FF)
                    "😂", "😆", "😮", "😢" -> Color(0xFFF7B125)
                    else -> if (isReacted) Color(0xFF00F5FF) else actionButtonTint
                }

                // 1. FriendHub Animated Like Button with 2s Long Press
                Box {
                    FriendHubLikeButton(
                        isLiked = isReacted,
                        onLikeToggle = {
                            if (isReacted) {
                                currentReactionEmoji = null
                                if (post.isLikedByMe) onLikeClick()
                            } else {
                                currentReactionEmoji = "👍"
                                onLikeClick()
                                floatingHeartsState.emitReaction("👍", 30) // Floating emojis on tap
                            }
                        },
                        onLongPress = {
                            showReactionsPopup = true
                        },
                        activeColor = Color(0xFF00F5FF), // Blue when liked
                        inactiveColor = Color(0xFFFF3131), // Red initially
                        size = 28.dp,
                        scope = scope
                    )

                    if (showReactionsPopup) {
                        Box(
                            modifier = Modifier
                                .offset(y = (-60).dp)
                                .align(Alignment.TopStart)
                        ) {
                            ReactionsPopup(
                                onSelectReaction = { reaction ->
                                    onSelectReaction?.invoke(reaction)
                                    currentReactionEmoji = reaction.emoji
                                    floatingHeartsState.emitReaction(reaction.emoji, 35) // Floating reaction emojis
                                    showReactionsPopup = false
                                }
                            )
                        }
                    }
                }
                
                // 2. Comment (Text only without icon in matching vibrant color)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00F5FF).copy(alpha = 0.12f),
                                    Color(0xFF3B82F6).copy(alpha = 0.12f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00F5FF).copy(alpha = 0.4f),
                                    Color(0xFF3B82F6).copy(alpha = 0.4f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onCommentClick() }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Comment",
                        color = Color(0xFF00F5FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.3.sp
                    )
                }
                
                // 3. Share
                IconButton(onClick = { onShareClick() }) {
                    Custom3DShareIcon(size = 28.dp)
                }

                // 4. Bookmark
                IconButton(onClick = { isSaved = !isSaved }) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) Color(0xFF00FF94) else actionButtonTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            if (post.likeCount > 0 || post.commentCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${post.likeCount + if (post.isLikedByMe) 1 else 0} likes • ${post.commentCount} comments",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor
                )
            }
            
            // Floating Likes Overlay
            com.example.animation.FloatingHeartsOverlay(
                state = floatingHeartsState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
}

@Composable
fun FriendHubLikeButton(
    isLiked: Boolean,
    onLikeToggle: () -> Unit,
    onLongPress: () -> Unit,
    scope: kotlinx.coroutines.CoroutineScope,
    activeColor: Color = Color(0xFF00F5FF), 
    inactiveColor: Color = Color(0xFFFF3131),
    size: androidx.compose.ui.unit.Dp = 24.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.4f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val glowRadius by animateDpAsState(
        targetValue = if (isLiked) 12.dp else 0.dp,
        animationSpec = tween(300),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .size(size * 2f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onLikeToggle() },
                    onPress = {
                        val job = scope.launch {
                            delay(2000L) // 2 Seconds
                            onLongPress()
                        }
                        try {
                            awaitRelease()
                        } finally {
                            job.cancel()
                        }
                    }
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLiked) {
            // High Quality 3D Bubble Image from generated asset
            Image(
                painter = painterResource(id = R.drawable.custom_3d_like_button_v2_1789529658273),
                contentDescription = "Liked",
                modifier = Modifier
                    .size(size * 1.8f)
                    .clip(CircleShape)
            )
            
            // Glow effect
            Box(
                modifier = Modifier
                    .size(size * 1.8f)
                    .shadow(elevation = glowRadius, shape = CircleShape, ambientColor = activeColor, spotColor = activeColor)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = "Like",
                tint = inactiveColor,
                modifier = Modifier.size(size)
            )
        }
    }
}


