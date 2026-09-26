package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.FriendRequest
import com.example.model.User
import com.example.ui.components.FriendOptionsBottomSheet
import com.example.viewmodel.MainViewModel

// Elegant Theme Palette for Friends Hub
private val DarkCanvas = Color(0xFF0B0D11)
private val CardBackground = Color(0xFF15181E)
private val CardBorderColor = Color(0xFF232730)
private val AccentPrimary = Color(0xFF2563EB) // Royal Blue
private val AccentGradient = Brush.horizontalGradient(
    listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
)
private val MutedText = Color(0xFF94A3B8)
private val SubtitleText = Color(0xFF64748B)

@Composable
fun FriendsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val friendRequests by viewModel.friendRequests.collectAsState()
    val allFriends by viewModel.allFriends.collectAsState()
    val knownUsers by viewModel.knownUsers.collectAsState()

    var activeFriendForOptions by remember { mutableStateOf<User?>(null) }
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: Requests, 1: Suggestions, 2: All Friends
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    if (activeFriendForOptions != null) {
        FriendOptionsBottomSheet(
            user = activeFriendForOptions!!,
            viewModel = viewModel,
            onDismiss = { activeFriendForOptions = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCanvas)
    ) {
        // ==================== TOP APP BAR ====================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.selectTab(0) },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (!isSearchActive) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Friends",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )

                    if (friendRequests.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEF4444))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${friendRequests.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Search Bar Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E232B))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(Color.White),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search users...",
                                        color = SubtitleText,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = {
                    isSearchActive = !isSearchActive
                    if (!isSearchActive) searchQuery = ""
                },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ==================== FILTER TABS ====================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tab 0: Requests
            FilterTabPill(
                title = "Requests",
                count = friendRequests.size,
                isSelected = selectedFilter == 0,
                onClick = { selectedFilter = 0 }
            )

            // Tab 1: Suggested
            FilterTabPill(
                title = "Suggestions",
                count = null,
                isSelected = selectedFilter == 1,
                onClick = { selectedFilter = 1 }
            )

            // Tab 2: Your Friends
            val onlineFriendsCount = allFriends.count { it.isOnline }
            FilterTabPill(
                title = "All Friends",
                count = allFriends.size,
                hasOnlineIndicator = onlineFriendsCount > 0,
                isSelected = selectedFilter == 2,
                onClick = { selectedFilter = 2 }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ==================== MAIN CONTENT FEED ====================
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedFilter) {
                // ==================== 0: FRIEND REQUESTS ====================
                0 -> {
                    val filteredRequests = friendRequests.filter {
                        if (searchQuery.isBlank()) true
                        else it.userName.contains(searchQuery, ignoreCase = true) ||
                                it.handle.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredRequests.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = if (searchQuery.isBlank()) "No friend requests" else "No matching requests",
                                description = if (searchQuery.isBlank()) "When someone sends you a friend request, they will appear clearly here." else "Try searching with a different name or username."
                            )
                        }
                    } else {
                        items(filteredRequests, key = { it.id }) { req ->
                            val user = knownUsers[req.userId] ?: User(
                                id = req.userId,
                                name = req.userName,
                                username = req.handle,
                                avatarUrl = req.userAvatar,
                                bio = req.bio,
                                mutualFriendsCount = req.mutualFriends,
                                isRequestReceived = true
                            )

                            FriendRequestCard(
                                request = req,
                                onOpenProfile = { viewModel.viewUserProfile(user) },
                                onConfirm = {
                                    viewModel.acceptFriendRequest(req.id)
                                    Toast.makeText(context, "${req.userName} is now your friend!", Toast.LENGTH_SHORT).show()
                                },
                                onRemove = {
                                    viewModel.deleteFriendRequest(req.id)
                                    Toast.makeText(context, "Request from ${req.userName} removed", Toast.LENGTH_SHORT).show()
                                },
                                onOptionsClick = {
                                    activeFriendForOptions = user
                                }
                            )
                        }
                    }
                }

                // ==================== 1: SUGGESTED USERS ====================
                1 -> {
                    val suggestedUsers = knownUsers.values.filter { !it.isFriend }.filter {
                        if (searchQuery.isBlank()) true
                        else it.name.contains(searchQuery, ignoreCase = true) ||
                                it.username.contains(searchQuery, ignoreCase = true)
                    }

                    if (suggestedUsers.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No suggestions right now",
                                description = "You're connected with everyone nearby! Check back later."
                            )
                        }
                    } else {
                        items(suggestedUsers.toList(), key = { it.id }) { user ->
                            SuggestedUserCard(
                                user = user,
                                onOpenProfile = { viewModel.viewUserProfile(user) },
                                onAddFriend = {
                                    if (user.isRequestSent) {
                                        viewModel.cancelFriendRequest(user.id)
                                        Toast.makeText(context, "Friend request to ${user.name} cancelled", Toast.LENGTH_SHORT).show()
                                    } else if (user.isFriend) {
                                        viewModel.openChatWithUser(user)
                                    } else {
                                        viewModel.sendFriendRequest(user.id)
                                        Toast.makeText(context, "Friend request sent to ${user.name}!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onRemove = {
                                    Toast.makeText(context, "${user.name} removed from suggestions", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                // ==================== 2: ALL FRIENDS ====================
                2 -> {
                    val filteredFriends = allFriends.filter {
                        if (searchQuery.isBlank()) true
                        else it.name.contains(searchQuery, ignoreCase = true) ||
                                it.username.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredFriends.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = if (searchQuery.isBlank()) "No friends yet" else "No friends found",
                                description = if (searchQuery.isBlank()) "Add friends from suggestions or accept pending requests." else "No friends matched your search."
                            )
                        }
                    } else {
                        items(filteredFriends, key = { it.id }) { friend ->
                            FriendItemCard(
                                friend = friend,
                                onOpenProfile = { viewModel.viewUserProfile(friend) },
                                onOptionsClick = { activeFriendForOptions = friend },
                                onChatClick = {
                                    viewModel.selectTab(2) // Jump directly to Messages
                                }
                            )
                        }
                    }
                }
            }

            // Bottom padding spacer so nothing gets blocked by the bottom bar
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

/**
 * Filter Tab Pill Component
 */
@Composable
private fun FilterTabPill(
    title: String,
    count: Int?,
    hasOnlineIndicator: Boolean = false,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF222630) else Color(0xFF14161B))
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E2229),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.White else MutedText,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )

            if (count != null && count > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFF3B82F6) else Color(0xFF272B35))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$count",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (hasOnlineIndicator) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
            }
        }
    }
}

/**
 * Clean & Beautiful Friend Request Card (NO video or photo thumbnails)
 * Displays the user cleanly with prominent display name, handle, mutual friends, and action buttons.
 */
@Composable
fun FriendRequestCard(
    request: FriendRequest,
    onOpenProfile: () -> Unit,
    onConfirm: () -> Unit,
    onRemove: () -> Unit,
    onOptionsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenProfile)
            .padding(14.dp)
            .testTag("friend_request_${request.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // User Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large, crisp circular avatar with subtle border
                AsyncImage(
                    model = request.userAvatar,
                    contentDescription = request.userName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFF2E3440), CircleShape)
                        .clickable(onClick = onOpenProfile)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Name, Handle, and Details Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = request.userName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = request.timestamp,
                            fontSize = 11.sp,
                            color = SubtitleText,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    if (request.handle.isNotBlank()) {
                        Text(
                            text = request.handle,
                            fontSize = 13.sp,
                            color = Color(0xFF60A5FA), // Light blue handle
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Mutual friends with overlapping mini avatars
                    if (request.mutualFriends > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (request.mutualFriendAvatars.isNotEmpty()) {
                                val avatars = request.mutualFriendAvatars.take(2)
                                Box(
                                    modifier = Modifier
                                        .width(if (avatars.size > 1) 30.dp else 16.dp)
                                        .height(16.dp)
                                ) {
                                    avatars.forEachIndexed { index, avatarUrl ->
                                        AsyncImage(
                                            model = avatarUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .padding(start = (index * 12).dp)
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, CardBackground, CircleShape)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${request.mutualFriends} mutual friends",
                                fontSize = 12.sp,
                                color = MutedText
                            )
                        }
                    } else if (request.subtitle.isNotBlank()) {
                        Text(
                            text = request.subtitle,
                            fontSize = 12.sp,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (request.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = request.bio,
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Options Menu Button
                IconButton(
                    onClick = onOptionsClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Options",
                        tint = SubtitleText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Confirm & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Confirm / Accept Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentGradient)
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Confirm",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Delete / Remove Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF222630))
                        .border(1.dp, Color(0xFF2E3440), RoundedCornerShape(20.dp))
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Delete",
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * Suggested User Card
 */
@Composable
fun SuggestedUserCard(
    user: User,
    onOpenProfile: () -> Unit,
    onAddFriend: () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenProfile)
            .padding(14.dp)
            .testTag("suggested_user_${user.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFF2E3440), CircleShape)
                        .clickable(onClick = onOpenProfile)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (user.username.isNotBlank()) {
                        Text(
                            text = "@${user.username}",
                            fontSize = 13.sp,
                            color = Color(0xFF60A5FA),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (user.mutualFriendsCount > 0) "${user.mutualFriendsCount} mutual friends" else "Suggested for you",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }

                // Close / Dismiss suggestion button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = SubtitleText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add Friend / Request Sent State Button
            val isRequested = user.isRequestSent
            val isFriend = user.isFriend

            val containerColor by animateColorAsState(
                targetValue = when {
                    isFriend -> Color(0xFF1E293B)
                    isRequested -> Color(0xFF064E3B)
                    else -> Color(0xFF1E293B)
                },
                label = "btnColor"
            )
            val borderColor by animateColorAsState(
                targetValue = when {
                    isFriend -> Color(0xFF38BDF8)
                    isRequested -> Color(0xFF10B981)
                    else -> Color(0xFF334155)
                },
                label = "borderColor"
            )
            val contentColor by animateColorAsState(
                targetValue = when {
                    isFriend -> Color(0xFF38BDF8)
                    isRequested -> Color(0xFF34D399)
                    else -> Color(0xFF60A5FA)
                },
                label = "contentColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(containerColor)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable { onAddFriend() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = when {
                            isFriend -> Icons.Outlined.People
                            isRequested -> Icons.Default.Check
                            else -> Icons.Default.PersonAdd
                        },
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when {
                            isFriend -> "Friends ✓"
                            isRequested -> "Request Sent ✓"
                            else -> "Add Friend"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * All Friends List Item
 */
@Composable
fun FriendItemCard(
    friend: User,
    onOpenProfile: () -> Unit,
    onOptionsClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenProfile)
            .padding(14.dp)
            .testTag("friend_item_${friend.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online status dot
            Box(modifier = Modifier.size(56.dp)) {
                AsyncImage(
                    model = friend.avatarUrl,
                    contentDescription = friend.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFF2E3440), CircleShape)
                )

                if (friend.isOnline) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(2.dp, CardBackground, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (friend.isOnline) "Active now" else if (friend.bio.isNotBlank()) friend.bio else "Friends on FriendHub",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = if (friend.isOnline) Color(0xFF10B981) else MutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Message Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(18.dp))
                    .clickable { onChatClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Message",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Message",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onOptionsClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Options",
                    tint = SubtitleText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Empty State View
 */
@Composable
private fun EmptyStateView(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF1B2028)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.People,
                contentDescription = null,
                tint = Color(0xFF60A5FA),
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            color = MutedText,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
