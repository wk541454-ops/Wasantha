package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Authentic Facebook Messenger icon matching the user's provided official Messenger design:
 * Classic electric royal blue speech bubble with the sharp white lightning bolt.
 */
@Composable
fun FacebookMessengerIcon(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    isSelected: Boolean = false,
    badgeText: String = "",
    useGradient: Boolean = true,
    colorOverride: Color? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        label = "messengerIconScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .testTag("messenger_icon"),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_3d_chat_bubble_vector),
            contentDescription = "Messenger",
            modifier = Modifier.size(size),
            colorFilter = colorOverride?.let { ColorFilter.tint(it) }
        )

        // Unread Badge if present
        if (badgeText.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE41E3F))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}

