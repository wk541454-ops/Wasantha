package com.example.ui.components.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SendableGift(
    val id: String,
    val name: String,
    val emoji: String,
    val cost: Int,
    val isUnlockable: Boolean = false,
    val animationType: GiftAnimationType = GiftAnimationType.EMOJI_BURST
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveGiftSheet(
    userCoins: Int,
    onDismiss: () -> Unit,
    onOpenRecharge: () -> Unit,
    onSendGift: (SendableGift) -> Unit
) {
    val gifts = remember {
        listOf(
            SendableGift("gift_pack", "Gift Pack", "🎁", 0, isUnlockable = true, GiftAnimationType.EMOJI_BURST),
            SendableGift("rose", "Rose", "🌹", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("love_you", "Love you so much", "🥰", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("youre_awesome", "You're awesome", "🐱", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("mate_melody", "Mate Melody", "🧉", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("good_job", "Good Job", "👍", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("heart_me", "Heart Me", "💖", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("welcome_dallah", "Welcome Dallah", "🫖", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("ice_cream", "Ice Cream Cone", "🍦", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("tiktok_logo", "TikTok", "🎵", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("clap_clap", "Clap Clap", "👏", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("shard_of_hope", "A Shard of Hope", "🛡️", 1, animationType = GiftAnimationType.EMOJI_BURST),
            SendableGift("rocket", "Rocket", "🚀", 500, animationType = GiftAnimationType.ROCKET),
            SendableGift("diamond", "Diamond", "💎", 1000, animationType = GiftAnimationType.DIAMOND_RAIN),
            SendableGift("crown", "VIP Crown", "👑", 2500, animationType = GiftAnimationType.CROWN)
        )
    }

    var selectedGiftId by remember { mutableStateOf("rose") }
    val selectedGift = gifts.firstOrNull { it.id == selectedGiftId } ?: gifts[1]
    var selectedCategory by remember { mutableStateOf("Gifts") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E24),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            // TikTok Style Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎁", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Unlock Gifts and Content",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Balance & Recharge Pill Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2D2D35))
                        .clickable { onOpenRecharge() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("🪙", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$userCoins",
                        color = Color(0xFFFFD54F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedCategory == "Exclusive") {
                // Screenshot 3: Personas & Guardian / Fan level View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Guardian Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF132219))
                            .border(1.dp, Color(0xFF2E5B3E), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🦌", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Become this creator's one and only Guardian",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Personas", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Personas Card (Golden Stag Guardian)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF25262D))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFF1E1E24)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🦌", fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙 4999", color = Color(0xFFFFD54F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFE2C55))
                                    .clickable { onOpenRecharge() }
                                    .padding(horizontal = 12.dp, vertical = 3.dp)
                            ) {
                                Text("Check", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Fan level", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            Triple("Heart Me", "💖", 1),
                            Triple("Go Popular", " Popular", 1),
                            Triple("Super Popular", " Super", 9),
                            Triple("Community", "🎁", 1)
                        ).forEach { (name, emoji, cost) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2A2A36)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text("🪙 $cost", color = Color(0xFFFFD54F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // TikTok Gift Grid (4 Columns)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    items(gifts, key = { it.id }) { gift ->
                        val isSelected = gift.id == selectedGiftId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(0xFF2A2A36) else Color.Transparent
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) Color(0xFFFE2C55) else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedGiftId = gift.id
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = gift.emoji, fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = gift.name,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))

                                if (gift.isUnlockable) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFE2C55))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Unlock",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🪙", fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${gift.cost}",
                                            color = Color(0xFFFFD54F),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Bar: Category Tabs & TikTok Style Send / Recharge Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Categories (Gifts, Exclusive)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Gifts ⬍",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { selectedCategory = "Gifts" }
                    )
                    Text(
                        text = "Exclusive",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.clickable { selectedCategory = "Exclusive" }
                    )
                }

                // Send / Recharge Action Button
                val canAfford = userCoins >= selectedGift.cost
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Recharge Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2D2D35))
                            .clickable { onOpenRecharge() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Recharge",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Send Button
                    Button(
                        onClick = {
                            if (canAfford) {
                                onSendGift(selectedGift)
                            } else {
                                onOpenRecharge()
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFE2C55)
                        ),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (canAfford) "Send" else "Recharge",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
