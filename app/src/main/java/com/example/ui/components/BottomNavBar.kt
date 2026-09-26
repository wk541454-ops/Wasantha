package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    unreadMessagesCount: Int = 2,
    unreadNotificationsCount: Int = 0,
    currentUserAvatar: String = "",
    coinBalance: Int = 1250,
    streakDays: Int = 5,
    onCoinWalletClick: () -> Unit = {},
    onDailyStreakClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onLiveClick: () -> Unit = {},
    onAppSettingsClick: () -> Unit = {},
    onTriggerEmojiBurst: (Offset) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Top Bar Background: Modern Dark Slate #0F172A container
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFF0F172A).copy(alpha = 0.98f))
            .border(
                width = 0.5.dp,
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: 3D Flipping Logo ("fn" photo & reaction side with 20 dancing emojis) + Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 3D Flipping Logo Icon
            // Side 1: 3D "fn" glossy photo
            // Side 2: 3D Reaction Hub with hearts, like & sparkles. When flipped or tapped, 20 dancing emojis erupt!
            Flipping3DFriendHubLogo(
                onTriggerEmojiBurst = onTriggerEmojiBurst
            )

            // "FriendHub" logo with custom Cyan-Violet Gradient text
            Text(
                text = "FriendHub",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = (-0.6).sp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))
                    )
                ),
                modifier = Modifier.clickable { onTabSelected(0) }
            )
        }

        // Right Action Icons: Live Stream, Search and Notifications
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Stream 3D Animated Icon with glossy small LIVE badge overlay
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable { onLiveClick() }
                    .testTag("top_live_button"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F0508))
                        .border(1.dp, Color(0xFFFF0033), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Animated3DLiveIcon(
                        size = 38.dp,
                        showLiveBadge = false,
                        animateFloating = false
                    )
                }

                // Tiny glossy LIVE pill badge fitting neatly at the bottom of the circle
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF0055), Color(0xFFFF0033))
                            )
                        )
                        .border(1.dp, Color.White, RoundedCornerShape(50))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.4.sp
                    )
                }
            }

            // Search Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), CircleShape)
                    .clickable { onSearchClick() }
                    .testTag("top_search_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFF8FAFC),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Notifications Animated Blue Bell Icon ("Selavi Selavi Thiyenna")
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { onTabSelected(4) /* Notifications Tab */ }
                    .testTag("top_notifications_button"),
                contentAlignment = Alignment.Center
            ) {
                AnimatedNotificationBellIcon(
                    size = 32.dp,
                    badgeCount = unreadNotificationsCount,
                    animateSwinging = false
                )
            }

            // 3D Settings Icon directly to the right of Notification icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { onAppSettingsClick() }
                    .testTag("top_settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Custom3DSettingsIcon(size = 36.dp)
            }
        }
    }
}

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    unreadMessagesCount: Int = 2,
    friendRequestsCount: Int = 0,
    onCreateClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Top Navigation Bar with rounded-2xl & glow
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.96f)) // Slate Background
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF06B6D4).copy(alpha = 0.4f), Color(0xFF8B5CF6).copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home (0)
            BottomNavItem(
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )

            // 2. Friends / Add Friends (1) - In place of Video icon, with friend requests count badge
            BottomNavItem(
                icon = Icons.Outlined.PersonAdd,
                selectedIcon = Icons.Filled.PersonAdd,
                label = "Friends",
                badgeCount = friendRequestsCount,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )

            // 3. Messages (2) - Next tab after Friends, with unread messages count
            BottomNavItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                selectedIcon = Icons.AutoMirrored.Filled.Chat,
                customDrawableRes = R.drawable.ic_3d_chat_bubble_vector,
                label = "Messages",
                badgeCount = if (selectedTab == 2) 0 else unreadMessagesCount,
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )

            // 4. Video / Watch (3) - Next tab after Messages, with 3D Red Play Button icon
            BottomNavItem(
                icon = Icons.Outlined.VideoLibrary,
                selectedIcon = Icons.Filled.VideoLibrary,
                customDrawableRes = R.drawable.ic_3d_video_play,
                label = "Video",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )

            // 5. Marketplace (5) - Remains at the end
            BottomNavItem(
                icon = Icons.Outlined.Storefront,
                selectedIcon = Icons.Filled.Storefront,
                customDrawableRes = R.drawable.ic_3d_marketplace_vector,
                label = "Marketplace",
                isSelected = selectedTab == 5,
                onClick = { onTabSelected(5) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    customDrawableRes: Int? = null,
    label: String = "",
    badgeCount: Int = 0,
    badgeColor: Color = Color(0xFFEF4444),
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF06B6D4) else Color(0xFF94A3B8), // Neon Cyan active
        label = "navColor"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                if (customDrawableRes == R.drawable.ic_3d_video_play) {
                    AnimatedNeonVideoIcon(size = 32.dp)
                } else if (customDrawableRes != null) {
                    Image(
                        painter = painterResource(id = customDrawableRes),
                        contentDescription = label,
                        modifier = Modifier.size(if (customDrawableRes == R.drawable.ic_3d_chat_bubble_vector || customDrawableRes == R.drawable.ic_3d_marketplace_vector) 32.dp else 26.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (isSelected) selectedIcon else icon,
                        contentDescription = label,
                        tint = activeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-4).dp)
                            .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                            .border(1.dp, Color(0xFF0F172A), CircleShape)
                            .padding(horizontal = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                // Glowing active tab indicator
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))
                            )
                        )
                )
            }
        }
    }
}
