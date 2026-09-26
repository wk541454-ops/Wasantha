package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.model.Comment
import com.example.model.Post
import com.example.model.User
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import com.example.viewmodel.MainViewModel

@Composable
fun PostDetailScreen(
    viewModel: MainViewModel,
    post: Post
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var commentText by remember { mutableStateOf("") }
    var showShareSheet by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.closePostDetail()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1C1C1D)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.closePostDetail() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Post",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Main Post Content
                item {
                    PostDetailHeader(
                        post = post,
                        onLikeClick = { viewModel.togglePostLike(post.id) },
                        onShareClick = { showShareSheet = true }
                    )
                }

                // Comments Section
                items(post.comments) { comment ->
                    CommentItemFBStyle(comment)
                }
            }

            // Bottom Input Bar
            BottomCommentInput(currentUser = currentUser, value = commentText, onValueChange = { commentText = it }) {
                if (commentText.isNotBlank()) {
                    viewModel.addCommentToPostInDetail(post.id, commentText)
                    commentText = ""
                }
            }
        }
    }

    if (showShareSheet) {
        com.example.ui.components.PostShareBottomSheet(
            postTitle = post.content,
            postUrl = post.mediaUrl,
            viewModel = viewModel,
            onShareToFriend = { name, avatar ->
                viewModel.sharePostToFriend(name, avatar, post.content, post.mediaUrl)
            },
            onDismiss = { showShareSheet = false }
        )
    }
}

@Composable
fun PostDetailHeader(
    post: Post,
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                AsyncImage(
                    model = post.userAvatar,
                    contentDescription = post.userName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                if (post.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF31A24C))
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.userName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (post.targetUserName != null) {
                        Text(
                            text = " ▸ ",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = post.targetUserName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = Color.Gray)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "මි ${post.timestamp} • ", color = Color.Gray, fontSize = 12.sp)
                    Icon(Icons.Default.People, contentDescription = "Privacy", tint = Color.Gray, modifier = Modifier.size(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (post.feeling != null) {
            Text(
                text = post.feeling,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(
            text = post.content,
            color = Color.White,
            fontSize = 17.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Reactions Summary
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    tint = Color.White,
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFF1877F2), CircleShape)
                        .padding(3.dp)
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Love",
                    tint = Color.White,
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFFE41E3F), CircleShape)
                        .padding(3.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = post.reactionSummary ?: "${post.likeCount} reactions",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            FBButton(
                modifier = Modifier.weight(1f).clickable { onLikeClick() },
                icon = Icons.Default.ThumbUpOffAlt,
                label = "${post.likeCount}",
                isSelected = post.isLikedByMe
            )
            Spacer(modifier = Modifier.width(12.dp))
            FBButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ChatBubbleOutline,
                label = "${post.commentCount}"
            )
            Spacer(modifier = Modifier.width(12.dp))
            FBButton(
                modifier = Modifier.weight(1f).clickable { onShareClick() },
                icon = Icons.Default.Share,
                customDrawableRes = com.example.R.drawable.ic_3d_share_vector,
                label = "Share"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FBButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    customDrawableRes: Int? = null,
    label: String,
    isSelected: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF3A3B3C))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (customDrawableRes != null) {
            com.example.ui.components.Custom3DShareIcon(size = 26.dp)
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFF1877F2) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (isSelected) Color(0xFF1877F2) else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CommentItemFBStyle(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = comment.userAvatar,
            contentDescription = comment.userName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3A3B3C))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = comment.userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = comment.content,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            ) {
                Text(text = "${comment.timestamp} • ", color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = "Reply",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { }
                )
                if (comment.likes > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${comment.likes}", color = Color.Gray, fontSize = 12.sp)
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Love",
                        tint = Color(0xFFE41E3F),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomCommentInput(
    currentUser: User,
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C1D))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF3A3B3C))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = "Comment as ${currentUser.name}",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 4
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEmotions, contentDescription = "Emoji", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Gif, contentDescription = "GIF", tint = Color.Gray)
            }
        }
    }
}
