package com.example.ui.components.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class DayRewardItem(
    val dayNumber: Int,
    val coinAmount: Int,
    val isPastClaimed: Boolean,
    val isToday: Boolean,
    val isLocked: Boolean
)

data class DailyTask(
    val id: String,
    val title: String,
    val subtitle: String,
    val rewardCoins: Int,
    val isCompleted: Boolean
)

@Composable
fun DailyRewardsDialog(
    streakDays: Int,
    isClaimed: Boolean,
    onClaim: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var justClaimedEffect by remember { mutableStateOf(false) }

    // Pulsing animation for today's active card
    val infiniteTransition = rememberInfiniteTransition(label = "streakPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val rewardsSchedule = remember(streakDays, isClaimed) {
        listOf(
            DayRewardItem(1, 50, isPastClaimed = true, isToday = false, isLocked = false),
            DayRewardItem(2, 75, isPastClaimed = true, isToday = false, isLocked = false),
            DayRewardItem(3, 100, isPastClaimed = true, isToday = false, isLocked = false),
            DayRewardItem(4, 150, isPastClaimed = true, isToday = false, isLocked = false),
            DayRewardItem(5, 200, isPastClaimed = isClaimed, isToday = true, isLocked = false),
            DayRewardItem(6, 250, isPastClaimed = false, isToday = false, isLocked = true),
            DayRewardItem(7, 500, isPastClaimed = false, isToday = false, isLocked = true)
        )
    }

    val dailyTasks = remember {
        listOf(
            DailyTask("task_1", "පෝස්ට් එකක් මිතුරන් සමඟ බෙදාගන්න", "Share 1 post to News Feed", 50, false),
            DailyTask("task_2", "මිතුරෙකුට පණිවිඩයක් යවන්න", "Send 2 direct messages", 30, true),
            DailyTask("task_3", "කෙටි වීඩියෝ (Reels) මිනිත්තු 3ක් නරඹන්න", "Watch Reels for 3 minutes", 40, false)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF0F172A))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF10B981), Color(0xFF06B6D4))
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clickable(enabled = false) {}
                    .testTag("daily_rewards_dialog"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFFF97316), Color(0xFFEA580C).copy(alpha = 0.2f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFFDBA74),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "දෛනික ත්‍යාග සහ Streak",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "🔥 දින $streakDays ක Streak එකක්! නොකඩවා පැමිණෙන්න",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 7-Day Rewards Schedule Carousel / Row
                    Text(
                        text = "දින 7 ක ප්‍රතිලාභ දින දර්ශනය (7-Day Rewards)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rewardsSchedule.forEach { item ->
                            val isClaimedItem = item.isPastClaimed
                            val isTodayActive = item.isToday && !item.isPastClaimed
                            
                            val cardBorderColor = when {
                                isTodayActive -> Color(0xFF10B981).copy(alpha = pulseGlow)
                                isClaimedItem -> Color(0xFF10B981).copy(alpha = 0.5f)
                                else -> Color(0xFF334155)
                            }
                            val cardBgColor = when {
                                isTodayActive -> Color(0xFF1E293B)
                                isClaimedItem -> Color(0xFF13221E)
                                else -> Color(0xFF1E293B).copy(alpha = 0.5f)
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardBgColor)
                                    .border(
                                        width = if (isTodayActive) 1.5.dp else 1.dp,
                                        color = cardBorderColor,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "D${item.dayNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTodayActive) Color(0xFF06B6D4) else Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (item.dayNumber == 7) "🏆" else "🪙",
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+${item.coinAmount}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isClaimedItem) Color(0xFF10B981) else Color(0xFFFBBF24)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (isClaimedItem) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Claimed",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else if (item.isLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF06B6D4))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Big Claim Card / Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isClaimed) "අද දින ත්‍යාගය ලබා ගෙන ඇත!" else "අද දින ත්‍යාගය: +200 කාසි",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isClaimed) "හෙට දින නැවත පැමිණ D6 ත්‍යාගය ලබා ගන්න" else "Streak එක වැඩි කරගැනීමට Claim කරන්න",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Button(
                                onClick = {
                                    if (!isClaimed) {
                                        onClaim(200)
                                        justClaimedEffect = true
                                        Toast.makeText(context, "සුබ පැතුම්! කාසි 200ක් සහ Streak එක සාර්ථකව ලැබුණි! 🎉🔥", Toast.LENGTH_LONG).show()
                                    }
                                },
                                enabled = !isClaimed,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981),
                                    disabledContainerColor = Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("claim_daily_reward_button")
                            ) {
                                Text(
                                    text = if (isClaimed) "ලබා ගත්තා ✓" else "Claim 🪙",
                                    color = if (isClaimed) Color(0xFF94A3B8) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Tasks Section
                    Text(
                        text = "අමතර කාසි උපයන්න (Daily Tasks)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    dailyTasks.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = task.subtitle,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "+${task.rewardCoins} 🪙",
                                    color = Color(0xFFFBBF24),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (task.isCompleted) Color(0xFF10B981) else Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (task.isCompleted) Color.White else Color(0xFF64748B),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Close Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF94A3B8)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF334155)))
                        )
                    ) {
                        Text("වසන්න (Close)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
