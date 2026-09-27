package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Comment
import com.example.model.Post
import com.example.model.User
import com.example.ui.theme.HeartRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: Post,
    currentUser: User,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
    isLightMode: Boolean = true
) {
    var commentText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    val sheetBg = if (isLightMode) Color.White else OledSurface
    val primaryText = if (isLightMode) Color(0xFF050505) else Color.White
    val secondaryText = if (isLightMode) Color(0xFF65676B) else Color(0xFFB0B3B8)
    val inputBg = if (isLightMode) Color(0xFFF0F2F5) else OledSurfaceVariant
    val inputBorder = if (isLightMode) Color(0xFFE4E6EB) else OledCardBorder

    androidx.compose.runtime.LaunchedEffect(post.comments.size) {
        if (post.comments.isNotEmpty()) {
            listState.animateScrollToItem(post.comments.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Reverse Back",
                            tint = primaryText
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Comments (${post.comments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = primaryText
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = secondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comments List
            if (post.comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No comments yet. Be the first to start the conversation! 💬",
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryText
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(post.comments, key = { it.id }) { comment ->
                        CommentItemRow(
                            comment = comment,
                            isLightMode = isLightMode,
                            onReplyClick = { userName ->
                                commentText = "@$userName "
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Comment Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentUser.avatarUrl,
                    contentDescription = currentUser.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text(
                            text = "Write a comment...",
                            fontSize = 14.sp,
                            color = secondaryText
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_comment_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = inputBorder,
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedTextColor = primaryText,
                        unfocusedTextColor = primaryText
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText)
                            commentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1877F2))
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Comment",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItemRow(
    comment: Comment,
    isLightMode: Boolean = true,
    onReplyClick: (String) -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(comment.likes) }

    val bubbleBg = if (isLightMode) Color(0xFFF0F2F5) else Color(0xFF3A3B3C)
    val authorColor = if (isLightMode) Color(0xFF050505) else Color.White
    val contentColor = if (isLightMode) Color(0xFF050505) else Color.White
    val subTextColor = if (isLightMode) Color(0xFF65676B) else Color(0xFFB0B3B8)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = comment.userAvatar,
            contentDescription = comment.userName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Comment Bubble
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(bubbleBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                        color = authorColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comment.content,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = contentColor
                    )
                }
            }

            // Sub row: Timestamp + Like + Reply
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = comment.timestamp,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = subTextColor
                )
                Text(
                    text = if (isLiked) "Liked" else "Like",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isLiked) Color(0xFF1877F2) else subTextColor,
                    modifier = Modifier.clickable {
                        isLiked = !isLiked
                        likesCount += if (isLiked) 1 else -1
                    }
                )
                Text(
                    text = "Reply",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = subTextColor,
                    modifier = Modifier.clickable { onReplyClick(comment.userName) }
                )
                if (likesCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1877F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$likesCount",
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }
                }
            }
        }
    }
}
