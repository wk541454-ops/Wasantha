package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReactionType
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurfaceVariant

@Composable
fun ReactionsPopup(
    onSelectReaction: (ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMore by remember { mutableStateOf(false) }
    
    val primaryReactions = listOf(
        ReactionType.LIKE,
        ReactionType.LOVE,
        ReactionType.HAHA,
        ReactionType.WOW,
        ReactionType.SAD
    )

    val extendedReactions = listOf(
        ReactionType.ANGRY,
        ReactionType.FIRE,
        ReactionType.PARTY,
        ReactionType.PRAY,
        ReactionType.HUNDRED,
        ReactionType.STAR,
        ReactionType.COOL,
        ReactionType.THINKING,
        ReactionType.SMILE,
        ReactionType.DISLIKE,
        ReactionType.CLAP,
        ReactionType.ROCKET,
        ReactionType.EYES,
        ReactionType.SHUSH,
        ReactionType.MIND_BLOWN
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(OledSurfaceVariant.copy(alpha = 0.95f))
            .border(1.dp, NeonBlue.copy(alpha = 0.6f), RoundedCornerShape(30.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("reactions_popup_bar"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            primaryReactions.forEach { reaction ->
                ReactionItem(reaction, onSelectReaction)
            }
            
            // Plus Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NeonBlue.copy(alpha = 0.2f))
                    .clickable { showMore = !showMore }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = "More",
                    tint = NeonBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (showMore) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(5),
                modifier = Modifier.height(120.dp).width(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(extendedReactions.size) { index ->
                    ReactionItem(extendedReactions[index], onSelectReaction)
                }
            }
        }
    }
}

@Composable
private fun ReactionItem(
    reaction: ReactionType,
    onSelectReaction: (ReactionType) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.5f else 1.0f,
        label = "reactionScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clickable { onSelectReaction(reaction) }
            .padding(4.dp)
            .testTag("reaction_item_${reaction.name}")
    ) {
        Text(
            text = reaction.emoji,
            fontSize = 22.sp
        )
    }
}
