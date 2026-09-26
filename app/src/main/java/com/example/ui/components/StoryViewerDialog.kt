package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.animation.FloatingHeartsOverlay
import com.example.animation.rememberFloatingHeartsState
import com.example.model.Story
import com.example.model.StoryViewer
import com.example.model.User
import com.example.ui.theme.HeartRed
import com.example.ui.theme.NeonBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * TikTok-style Story Viewer Modal matching user screenshot.
 * Features:
 * - Top Header: Profile avatar with (+), active friends story bubbles, "Friends" title, Search icon.
 * - View Counter: "kidenek baluwada kiyana eka add karanna" (prominently displays view count).
 * - Comment Notification: "command karama msg ekak noya notificasan ekak yannai emoji animesoni pawennai".
 * - Dual Animation on Like: "like icon eka obapuwama dennatama animesoni yannai story eka dapu kena saha balana kenata penna".
 */
@Composable
fun StoryViewerDialog(
    story: Story,
    currentUser: User? = null,
    friendStories: List<Story> = emptyList(),
    onDismiss: () -> Unit,
    onReact: (String) -> Unit = {},
    onToggleLike: () -> Unit = {},
    onComment: (String) -> Unit = {},
    onSelectOtherStory: (Story) -> Unit = {}
) {
    val progress = remember { Animatable(0f) }
    var isPaused by remember { mutableStateOf(false) }
    var isHolding by remember { mutableStateOf(false) }
    var isTyping by remember { mutableStateOf(false) }
    var showViewersSheet by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }
    var isFriendRequested by remember { mutableStateOf(false) }
    var currentLikeCount by remember { mutableIntStateOf(story.likeCount) }
    var currentViewCount by remember { mutableIntStateOf(maxOf(story.viewersCount, 1)) }
    var likeScaleTarget by remember { mutableStateOf(1f) }
    val floatingHeartsState = rememberFloatingHeartsState()
    val coroutineScope = rememberCoroutineScope()

    // Interactive in-app notification banner for Story likes and comments
    var activeNotificationBanner by remember { mutableStateOf<Pair<String, String>?>(null) }

    val likeScale by animateFloatAsState(
        targetValue = likeScaleTarget,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "likeScale"
    )

    val viewerName = currentUser?.name ?: "You"

    // Timer pauses when typing comment ("command ekak danna tiyp karanakota status eka ply eka nathara wenna oni"),
    // holding touch on status ("status eka obagena iddi ply eka nathara wenna oni"), or viewing sheet.
    val shouldPause = isPaused || showViewersSheet || isHolding || isTyping || commentText.isNotBlank()
    LaunchedEffect(story.id, shouldPause) {
        if (!shouldPause) {
            val remainingMillis = ((1f - progress.value) * 7000f).toLong().coerceAtLeast(100L)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = remainingMillis.toInt(), easing = LinearEasing)
            )
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // ==================== 1. FULL BACKGROUND STORY MEDIA ====================
            // Pressing and holding anywhere on the story pauses playback
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHolding = true
                                try {
                                    tryAwaitRelease()
                                } finally {
                                    isHolding = false
                                }
                            }
                        )
                    }
            ) {
                if (!story.mediaUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = story.mediaUrl,
                        contentDescription = "Story Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val bgColor = try {
                        Color(android.graphics.Color.parseColor(story.backgroundColor ?: "#1C1C1E"))
                    } catch (e: Exception) {
                        Color(0xFF1C1C1E)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = story.caption,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }

            // Dark gradient overlay on top and bottom for contrast readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                        )
                    )
            )

            // ==================== 2. FLOATING HEARTS & EMOJIS OVERLAY ====================
            FloatingHeartsOverlay(
                state = floatingHeartsState,
                modifier = Modifier.fillMaxSize()
            )

            // ==================== 3. TOP NOTIFICATION BANNER ====================
            AnimatedVisibility(
                visible = activeNotificationBanner != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            ) {
                activeNotificationBanner?.let { (title, message) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E1E1E).copy(alpha = 0.95f))
                            .border(1.dp, Color(0xFFFE2C55).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFE2C55)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = message,
                                    color = Color(0xFFE0E0E0),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // ==================== 4. TIKTOK TOP BAR (Friends Title, Story Bubbles, Search) ====================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp, start = 12.dp, end = 12.dp)
            ) {
                // Segmented Progress Bar Line
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Profile Avatar with blue (+) button (Your Story)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                    ) {
                        AsyncImage(
                            model = currentUser?.avatarUrl ?: story.userAvatar,
                            contentDescription = "My Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF00D2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Story",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Friends Story Avatar Bubbles (Active stories list)
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(friendStories.take(4)) { fStory ->
                            val isCurrentStory = fStory.id == story.id
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (isCurrentStory) 2.dp else 1.5.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(Color(0xFFFE2C55), Color(0xFF25F4EE), Color(0xFFFE2C55))
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { onSelectOtherStory(fStory) },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = fStory.userAvatar,
                                    contentDescription = fStory.userName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }

                    // Centered TikTok Friends Header Label
                    Text(
                        text = "Friends",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Right Actions: Search & Close
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { /* Search Action */ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // ==================== 5. BOTTOM OVERLAY SECTION (Matching Screenshot) ====================
            // Elevated from bottom so it's not cut off ("Command bar eka tikak udata ganna penne nha")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(start = 14.dp, end = 14.dp, bottom = 28.dp)
            ) {
                // Author, Audience, View Count, and Floating Heart Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column: User avatar & details & Audience badge & Views Count
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // User Avatar & Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = story.userAvatar,
                                contentDescription = story.userName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color(0xFFFE2C55), CircleShape)
                            )

                            Column {
                                Text(
                                    text = "${story.userName} 🎧🎤... • ${story.timestamp}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Followers Only Badge (Matching Screenshot)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.22f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.People,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Followers only",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    // View Count Badge ("kidenek baluwada kiyana eka add karanna")
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF242526).copy(alpha = 0.85f))
                                            .border(1.dp, Color(0xFF3E4042), RoundedCornerShape(12.dp))
                                            .clickable { showViewersSheet = true }
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = "Views",
                                                tint = NeonBlue,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "$currentViewCount views",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right Side: Floating Heart / Like Button
                    // "like icon eka obapuwama dennatama animesoni yannai story eka dapu kena saha balana kenata penna"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                isLiked = !isLiked
                                currentLikeCount += if (isLiked) 1 else -1
                                likeScaleTarget = 1.35f
                                coroutineScope.launch {
                                    delay(180)
                                    likeScaleTarget = 1f
                                }

                                // Burst animation for both viewer and creator: 100 hearts traveling through screen!
                                floatingHeartsState.emitReaction("❤️", 100)
                                onToggleLike()
                                onReact("❤️")

                                // Trigger visible Notification Banner for both
                                activeNotificationBanner = "❤️ Story Like" to "$viewerName liked ${story.userName}'s story!"
                                coroutineScope.launch {
                                    delay(3500)
                                    activeNotificationBanner = null
                                }
                            },
                            modifier = Modifier.scale(likeScale)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like Story",
                                tint = if (isLiked) HeartRed else Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        Text(
                            text = "$currentLikeCount",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==================== 6. BOTTOM MESSAGE & EMBEDDED DRAGGABLE 15-EMOJIS BAR ====================
                // "wenamama emoji bar ekak danna epa command bar eka athulatama danna mehata adala ganna puluwan wenna"
                val quickEmojis15 = remember {
                    listOf(
                        "❤️", "🔥", "😂", "😍", "😳",
                        "🥺", "👏", "😮", "💯", "✨",
                        "🎉", "🥰", "🤣", "💖", "🙏"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rounded Message Pill Input Field
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF242526).copy(alpha = 0.95f))
                            .border(1.dp, Color(0xFF3E4042), RoundedCornerShape(24.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Send/Airplane icon
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (commentText.isNotBlank()) Color(0xFF00D2FF) else Color(0xFF8E8E93),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(enabled = commentText.isNotBlank()) {
                                        val sentMsg = commentText
                                        commentText = ""
                                        isTyping = false
                                        isPaused = false

                                        // Float ~100 animated emojis across the entire screen
                                        floatingHeartsState.emitReaction("🎉", 50)
                                        floatingHeartsState.emitReaction("💬", 50)

                                        // Trigger Notification banner
                                        onComment(sentMsg)
                                        activeNotificationBanner = "🔔 Story Comment" to "$viewerName commented on ${story.userName}'s story: \"$sentMsg\""
                                        coroutineScope.launch {
                                            delay(3500)
                                            activeNotificationBanner = null
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Text Field ("command ekak danna tiyp karanakota status eka ply eka nathara wenna oni")
                            BasicTextField(
                                value = commentText,
                                onValueChange = {
                                    commentText = it
                                    isTyping = it.isNotBlank()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .onFocusChanged { focusState ->
                                        isTyping = focusState.isFocused || commentText.isNotBlank()
                                    },
                                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                cursorBrush = SolidColor(Color.White),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (commentText.isEmpty()) {
                                        Text(
                                            text = "Message...",
                                            color = Color(0xFFAAAAAA),
                                            fontSize = 13.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // 15 Draggable / Scrollable Emojis INSIDE the Command Bar!
                            // "command bar eka athulatama danna mehata adala ganna puluwan wenna"
                            LazyRow(
                                modifier = Modifier.widthIn(max = 140.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(quickEmojis15) { emoji ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF3A3B3C).copy(alpha = 0.85f))
                                            .clickable {
                                                // Emit ~100 animated emojis flowing through entire screen!
                                                floatingHeartsState.emitReaction(emoji, 100)
                                                onReact(emoji)
                                                activeNotificationBanner = "$emoji Reaction" to "$viewerName reacted with $emoji to ${story.userName}'s story!"
                                                coroutineScope.launch {
                                                    delay(3500)
                                                    activeNotificationBanner = null
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = emoji,
                                            fontSize = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Share Button (outside pill)
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF242526).copy(alpha = 0.95f))
                            .border(1.dp, Color(0xFF3E4042), CircleShape)
                            .clickable {
                                floatingHeartsState.emitReaction("✨", 100)
                                activeNotificationBanner = "↗️ Story Shared" to "Story link copied to clipboard!"
                                coroutineScope.launch {
                                    delay(3000)
                                    activeNotificationBanner = null
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Quick Add Friend Icon ("mithuru add karaganna icon eka athule")
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isFriendRequested) Color(0xFF10B981) else Color(0xFF242526).copy(alpha = 0.95f))
                            .border(1.dp, if (isFriendRequested) Color(0xFF10B981) else Color(0xFF3E4042), CircleShape)
                            .clickable {
                                isFriendRequested = !isFriendRequested
                                if (isFriendRequested) {
                                    floatingHeartsState.emitReaction("🤝", 60)
                                    activeNotificationBanner = "👥 Friend Request Sent" to "Friend request sent to ${story.userName}!"
                                } else {
                                    activeNotificationBanner = "👥 Request Cancelled" to "Request to ${story.userName} cancelled."
                                }
                                coroutineScope.launch {
                                    delay(3000)
                                    activeNotificationBanner = null
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFriendRequested) Icons.Default.Check else Icons.Default.PersonAdd,
                            contentDescription = if (isFriendRequested) "Request Sent" else "Add Friend",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ==================== 7. STATUS VIEWERS & LIKES BOTTOM SHEET ====================
            AnimatedVisibility(
                visible = showViewersSheet,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                StatusViewersAndLikesSheet(
                    story = story,
                    currentLikeCount = currentLikeCount,
                    currentViewCount = currentViewCount,
                    onClose = { showViewersSheet = false }
                )
            }
        }
    }
}

/**
 * Status Viewers and Likes list sheet for inspecting viewers and reactions.
 */
@Composable
private fun StatusViewersAndLikesSheet(
    story: Story,
    currentLikeCount: Int,
    currentViewCount: Int,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color(0xFF1E1E1E))
            .border(1.dp, Color(0xFF3E4042), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 24.dp)
        ) {
            // Drag handle pill
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF65676B))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sheet Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Story Views & Reactions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "කථාන්දරය බැලූ සහ ප්‍රතිචාර දැක්වූ පරිශීලකයින්",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }

                // Stats badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2C2C2E))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "👁️ $currentViewCount",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2C2C2E))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "❤️ $currentLikeCount",
                            color = HeartRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFF3E4042), thickness = 0.5.dp)

            // Viewers and Reactions list
            if (story.viewers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "තවම කිසිවෙකු ඔබගේ Story එක බලා නැත",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    items(story.viewers) { viewer ->
                        StoryViewerItem(viewer = viewer)
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryViewerItem(viewer: StoryViewer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Profile Avatar
        AsyncImage(
            model = viewer.userAvatar,
            contentDescription = viewer.userName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFF3E4042), CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // User Name & Timestamp
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = viewer.userName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )
            Text(
                text = viewer.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray.copy(alpha = 0.7f)
            )
        }

        // Reaction Badge
        if (!viewer.reactionEmoji.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3A3B3C))
                    .border(1.dp, Color(0xFF505256), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewer.reactionEmoji,
                    fontSize = 18.sp
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "Viewed",
                tint = Color(0xFF65676B),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
