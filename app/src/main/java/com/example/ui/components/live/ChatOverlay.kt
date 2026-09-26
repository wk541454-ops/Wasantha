package com.example.ui.components.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LiveChatMessage

@Composable
fun ChatOverlay(comments: List<LiveChatMessage>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(8.dp),
        reverseLayout = true
    ) {
        items(comments) { comment ->
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "${comment.userName}: ${comment.message}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}
