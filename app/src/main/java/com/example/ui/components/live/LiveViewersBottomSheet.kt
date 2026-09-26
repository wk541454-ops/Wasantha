package com.example.ui.components.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class LiveViewer(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val contributionCoins: Int,
    val rank: Int? = null,
    val badge: String? = null,
    val isFollowing: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveViewersBottomSheet(
    onDismiss: () -> Unit,
    onViewerFollowToggle: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val allViewers = remember {
        mutableStateListOf(
            LiveViewer("1", "Nethu Dilrukshi 🇱🇰", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200", 2400, rank = 1, badge = "Top Gifter 🏆"),
            LiveViewer("2", "Dulantha Senanayake", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200", 1850, rank = 2, badge = "VIP 👑"),
            LiveViewer("3", "Sahil Fernando 🖤", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200", 940, rank = 3, badge = "Super Fan ⚡"),
            LiveViewer("4", "Oshen Perera", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200", 420, badge = "Fan Club Lv.8"),
            LiveViewer("5", "Kavindu Madushan", "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=200", 310, badge = "Fan"),
            LiveViewer("6", "Anuki De Silva", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200", 180),
            LiveViewer("7", "Raveen Wickrama", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=200", 120),
            LiveViewer("8", "Tharushi Sandunika", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200", 60),
            LiveViewer("9", "Malith Bandara", "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=200", 25),
            LiveViewer("10", "Sanduni Jayawardena", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=200", 10)
        )
    }

    val filteredViewers = allViewers.filter {
        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)) &&
        (selectedFilter == "All" || (selectedFilter == "Top Gifters" && (it.rank != null || it.contributionCoins > 100)))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334155))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live Viewers (1,438)",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search viewers...", color = Color(0xFF64748B), fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(18.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B),
                    focusedBorderColor = Color(0xFF06B6D4),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filters
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Top Gifters").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF1E293B))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF10B981) else Color(0xFF334155),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color(0xFF10B981) else Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Viewers List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredViewers, key = { it.id }) { viewer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF161F2E))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Indicator
                        if (viewer.rank != null) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (viewer.rank) {
                                            1 -> Color(0xFFFFD700)
                                            2 -> Color(0xFFC0C0C0)
                                            3 -> Color(0xFFCD7F32)
                                            else -> Color(0xFF334155)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${viewer.rank}",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Avatar
                        AsyncImage(
                            model = viewer.avatarUrl,
                            contentDescription = viewer.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, if (viewer.rank == 1) Color(0xFFFFD700) else Color(0xFF06B6D4), CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = viewer.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (viewer.badge != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = viewer.badge,
                                            color = Color(0xFF06B6D4),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Text(
                                    text = "🪙 ${viewer.contributionCoins}",
                                    color = Color(0xFFFFB74D),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        var isFollowed by remember { mutableStateOf(viewer.isFollowing) }

                        // Follow / Add Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isFollowed) Color(0xFF334155) else Color(0xFF10B981))
                                .clickable {
                                    isFollowed = !isFollowed
                                    onViewerFollowToggle(viewer.id)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isFollowed) "Following" else "+ Follow",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
