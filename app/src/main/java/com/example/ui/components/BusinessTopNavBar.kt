package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.OndemandVideo
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

data class BusinessTabItem(
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val badgeText: String? = null
)

@Composable
fun BusinessTopNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    businessAvatar: String,
    onCreateClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSwitchToPersonalClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color(0xFF0F172A), // Slate 900
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Brand Logo + Switch to Personal Button + Action Icons (+, Search, Menu)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Business Suite",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            letterSpacing = (-0.5).sp,
                            fontFamily = com.example.ui.theme.PlusJakartaSansFamily
                        ),
                        color = Color(0xFF10B981) // Emerald Green
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B), // Slate 800
                        modifier = Modifier.clickable { onSwitchToPersonalClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Personal 👤",
                                fontSize = 11.sp,
                                color = Color(0xFFF8FAFC),
                                fontWeight = FontWeight.Bold,
                                fontFamily = com.example.ui.theme.PlusJakartaSansFamily
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Create [+] Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .clickable { onCreateClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create",
                            tint = Color(0xFFF8FAFC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Search Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .clickable { onSearchClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFF8FAFC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Menu Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .clickable { onMenuClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFFF8FAFC),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Second Row: Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Home Feed
                BusinessNavTabItem(
                    icon = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                    badgeText = "15+",
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f),
                    isLightMode = false
                )

                // Tab 1: Messages / Chat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clickable { onTabSelected(1) },
                    contentAlignment = Alignment.Center
                ) {
                    FacebookMessengerIcon(
                        size = 30.dp,
                        isSelected = selectedTab == 1,
                        useGradient = selectedTab == 1,
                        badgeText = "15+",
                        colorOverride = if (selectedTab == 1) Color(0xFF10B981) else Color(0xFF94A3B8)
                    )
                    if (selectedTab == 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.BottomCenter)
                                .background(Color(0xFF10B981))
                        )
                    }
                }

                // Tab 2: Videos (with 3D Red Play Icon)
                BusinessNavTabItem(
                    icon = if (selectedTab == 2) Icons.Filled.OndemandVideo else Icons.Outlined.OndemandVideo,
                    customDrawableRes = R.drawable.ic_3d_video_play,
                    badgeText = "15+",
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    modifier = Modifier.weight(1f),
                    isLightMode = false
                )

                // Tab 3: Notifications
                BusinessNavTabItem(
                    icon = if (selectedTab == 3) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                    badgeText = "15+",
                    isSelected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    modifier = Modifier.weight(1f),
                    isLightMode = false
                )

                // Tab 4: Storefront / Business Page Products & Details
                BusinessNavTabItem(
                    icon = if (selectedTab == 4) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                    badgeText = null,
                    isSelected = selectedTab == 4,
                    onClick = { onTabSelected(4) },
                    modifier = Modifier.weight(1f),
                    isLightMode = false
                )
            }
            HorizontalDivider(color = Color(0xFF1E293B), thickness = 0.5.dp)
        }
    }
}

@Composable
private fun BusinessNavTabItem(
    icon: ImageVector,
    badgeText: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLightMode: Boolean = false,
    customDrawableRes: Int? = null
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (customDrawableRes == R.drawable.ic_3d_video_play) {
                AnimatedNeonVideoIcon(size = 32.dp)
            } else if (customDrawableRes != null) {
                Image(
                    painter = painterResource(id = customDrawableRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF10B981) else Color(0xFF94A3B8),
                    modifier = Modifier.size(27.dp)
                )
            }
            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-8).dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected && icon == Icons.Filled.Home) Color(0xFF10B981) else Color(0xFFEF4444))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFF10B981))
            )
        }
    }
}

