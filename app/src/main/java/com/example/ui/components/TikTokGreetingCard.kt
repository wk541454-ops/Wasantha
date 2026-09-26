package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TikTokGreetingCard(
    messageContent: String,
    onTap: () -> Unit
) {
    Text(
        text = "💌 $messageContent",
        modifier = Modifier
            .padding(8.dp)
            .clickable { onTap() },
        color = Color(0xFFE91E63) // TikTok-like pink
    )
}
