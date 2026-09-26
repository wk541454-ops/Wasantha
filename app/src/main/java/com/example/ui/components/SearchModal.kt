package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import com.example.viewmodel.MainViewModel

@Composable
fun SearchModal(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val posts by viewModel.posts.collectAsState()
    val friends by viewModel.allFriends.collectAsState()
    val marketplaceItems by viewModel.marketplaceItems.collectAsState()

    val filteredPosts = if (searchQuery.isBlank()) emptyList() else posts.filter {
        it.content.contains(searchQuery, ignoreCase = true) || it.userName.contains(searchQuery, ignoreCase = true)
    }

    val filteredFriends = if (searchQuery.isBlank()) emptyList() else friends.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.bio.contains(searchQuery, ignoreCase = true)
    }

    val filteredItems = if (searchQuery.isBlank()) emptyList() else marketplaceItems.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    val stories by viewModel.stories.collectAsState()
    val filteredStreams = if (searchQuery.isBlank()) emptyList() else stories.filter {
        it.isLive && (it.userName.contains(searchQuery, ignoreCase = true) || it.caption.contains(searchQuery, ignoreCase = true))
    }

    val aiResponse by viewModel.aiAssistantResponse.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF18191A))
                .padding(16.dp)
        ) {
            // Search Bar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.clearAiSearch()
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("search_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it 
                        if (it.length >= 3) viewModel.performAiSearch(it) else viewModel.clearAiSearch()
                    },
                    placeholder = { Text("Ask anything or search FriendHub...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = "" 
                                viewModel.clearAiSearch()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .testTag("search_input_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = OledCardBorder,
                        focusedContainerColor = OledSurfaceVariant,
                        unfocusedContainerColor = OledSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isBlank()) {
                // Recent Searches & Suggestions
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val recentKeywords = listOf("Photos & Reels", "Friend Requests", "iPhone 15 Pro", "Tech Groups", "Live Streams")
                recentKeywords.forEach { keyword ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { searchQuery = keyword }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = keyword, color = Color.LightGray, fontSize = 15.sp, modifier = Modifier.weight(1f))
                    }
                }
            } else {
                // Search Results
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // AI Assistant Section
                    aiResponse?.let { response ->
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = NeonBlue.copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = NeonBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Smart AI Assistant",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = NeonBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = response,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }

                    // Friends Results
                    if (filteredFriends.isNotEmpty()) {
                        item {
                            Text("Friends & People", fontWeight = FontWeight.Bold, color = NeonBlue, fontSize = 14.sp)
                        }
                        items(filteredFriends) { friend ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        Toast.makeText(context, "Opened profile of ${friend.name}", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    },
                                colors = CardDefaults.cardColors(containerColor = OledSurfaceVariant)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = friend.avatarUrl,
                                        contentDescription = friend.name,
                                        modifier = Modifier.size(44.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(friend.name, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(friend.bio, fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // Posts Results
                    if (filteredPosts.isNotEmpty()) {
                        item {
                            Text("Posts & Feed", fontWeight = FontWeight.Bold, color = NeonBlue, fontSize = 14.sp)
                        }
                        items(filteredPosts) { post ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        Toast.makeText(context, "Navigated to post by ${post.userName}", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    },
                                colors = CardDefaults.cardColors(containerColor = OledSurfaceVariant)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.RssFeed, contentDescription = null, tint = NeonBlue)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(post.userName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text(post.content, color = Color.LightGray, fontSize = 14.sp, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }

                    // Marketplace Results
                    if (filteredItems.isNotEmpty()) {
                        item {
                            Text("Marketplace Listings", fontWeight = FontWeight.Bold, color = NeonBlue, fontSize = 14.sp)
                        }
                        items(filteredItems) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        Toast.makeText(context, "Opened listing: ${item.title}", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    },
                                colors = CardDefaults.cardColors(containerColor = OledSurfaceVariant)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF4CAF50))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(item.title, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("LKR ${item.price}", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Live Streams Results
                    if (filteredStreams.isNotEmpty()) {
                        item {
                            Text("Live Streams (සජීවී විකාශන)", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), fontSize = 14.sp)
                        }
                        items(filteredStreams) { stream ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setLiveStreamingOpen(true)
                                        viewModel.startLiveStream()
                                        onDismiss()
                                    },
                                colors = CardDefaults.cardColors(containerColor = OledSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Animated3DLiveIcon(size = 42.dp, showLiveBadge = false)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(stream.userName, fontWeight = FontWeight.Bold, color = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFFEF4444))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text("Tap to join live stream & send gifts", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    if (filteredFriends.isEmpty() && filteredPosts.isEmpty() && filteredItems.isEmpty() && filteredStreams.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("No matching results found for '$searchQuery'", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
