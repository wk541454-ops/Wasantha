package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CoinPackage
import com.example.model.CreatorAnalytics
import com.example.model.CreatorEarningWallet
import com.example.model.User
import com.example.model.VirtualGiftItem

val sampleCoinPackages = listOf(
    CoinPackage("coin_1", 100, 0, 0.99, "Rs. 320", null),
    CoinPackage("coin_2", 550, 50, 4.99, "Rs. 1,600", "POPULAR 🔥"),
    CoinPackage("coin_3", 1200, 200, 9.99, "Rs. 3,200", "BEST VALUE ✨"),
    CoinPackage("coin_4", 3000, 600, 24.99, "Rs. 8,000", "VIP CREATOR 👑")
)

val sampleVirtualGifts = listOf(
    VirtualGiftItem("gift_rose", "Rose", "🌹", 10, "SPARKLE", 0.075),
    VirtualGiftItem("gift_star", "Super Star", "⭐", 50, "STAR_BURST", 0.375),
    VirtualGiftItem("gift_fire", "Fire Flare", "🔥", 100, "FIRE_BLAZE", 0.75),
    VirtualGiftItem("gift_diamond", "Blue Diamond", "💎", 250, "DIAMOND_SHINE", 1.875),
    VirtualGiftItem("gift_crown", "Royal Gold Crown", "👑", 500, "ROYAL_ANIMATION", 3.75),
    VirtualGiftItem("gift_rocket", "Rocket Boost", "🚀", 1000, "ROCKET_LAUNCH", 7.50)
)

/**
 * Full Creator Dashboard & Monetization Center Dialog
 */
@Composable
fun CreatorDashboardModal(
    user: User,
    wallet: CreatorEarningWallet = CreatorEarningWallet(),
    analytics: CreatorAnalytics = CreatorAnalytics(),
    onDismiss: () -> Unit,
    onCashoutRequested: (amountUsd: Double) -> Unit,
    onBuyCoinsClick: () -> Unit,
    onAuditAccountClick: () -> Unit,
    onSubscribeBusinessClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Analytics", "Earnings & Payout", "Coin Store")

    val isUnlocked = user.followersCount >= 5000 || user.isVerifiedCreator

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1120))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Surface(
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Creator Studio & Monetization",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (isUnlocked) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VipGoldCheckmark(size = 16.dp)
                                    }
                                }
                                Text(
                                    text = if (isUnlocked) "VIP Creator Active (5,000+ Followers)" else "Threshold: ${user.followersCount} / 5,000 Followers",
                                    color = if (isUnlocked) Color(0xFF34D399) else Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF38BDF8),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF38BDF8)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }

                // Content Views
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    when (selectedTab) {
                        0 -> CreatorOverviewTab(
                            user = user,
                            isUnlocked = isUnlocked,
                            wallet = wallet,
                            onAuditClick = onAuditAccountClick,
                            onSubscribeBusinessClick = onSubscribeBusinessClick
                        )
                        1 -> CreatorAnalyticsTab(analytics = analytics, isUnlocked = isUnlocked)
                        2 -> CreatorEarningsTab(wallet = wallet, isUnlocked = isUnlocked, onCashout = onCashoutRequested)
                        3 -> CoinStoreTab(wallet = wallet, onBuy = onBuyCoinsClick)
                    }
                }
            }
        }
    }
}

/**
 * TAB 0: Creator Overview & Unlocked Benefits
 */
@Composable
fun CreatorOverviewTab(
    user: User,
    isUnlocked: Boolean,
    wallet: CreatorEarningWallet,
    onAuditClick: () -> Unit,
    onSubscribeBusinessClick: () -> Unit
) {
    val context = LocalContext.current
    val progress = (user.followersCount / 5000f).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Milestone Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(
                1.dp,
                if (isUnlocked) Color(0xFFF59E0B).copy(alpha = 0.6f) else Color(0xFF334155)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isUnlocked) "🏆 VIP Creator Unlocked!" else "Creator Unlock Program",
                            color = if (isUnlocked) Color(0xFFFBBF24) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Target: 5,000 Verified Followers",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    if (isUnlocked) {
                        VipGoldCheckmark(size = 28.dp)
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF334155)
                        ) {
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (isUnlocked) Color(0xFFF59E0B) else Color(0xFF38BDF8),
                    trackColor = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${user.followersCount} Followers",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (isUnlocked) "Eligible for AdMob & Gifts Revenue" else "${5000 - user.followersCount} more needed",
                        color = if (isUnlocked) Color(0xFF34D399) else Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                if (isUnlocked) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onAuditClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF22C55E))
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Automated Creator Audit: PASSED ✓", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Feature Matrix Checklist
        Text(
            text = "Feature Matrix & Unlock Benefits",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        FeatureUnlockRow(
            icon = Icons.Default.Verified,
            iconTint = Color(0xFFFBBF24),
            title = "VIP Gold Verification Badge",
            desc = "Special animated gold checkmark badge next to your profile display name.",
            isUnlocked = isUnlocked
        )

        FeatureUnlockRow(
            icon = Icons.Default.Analytics,
            iconTint = Color(0xFF38BDF8),
            title = "Creator Analytics Dashboard",
            desc = "Post impressions, reach, demographic breakdown, and peak engagement times.",
            isUnlocked = isUnlocked
        )

        FeatureUnlockRow(
            icon = Icons.Default.Hd,
            iconTint = Color(0xFF34D399),
            title = "15-Min 4K/HD Video & Live Streaming",
            desc = "Upload ultra-high-definition 4K video clips and extended live broadcasts.",
            isUnlocked = isUnlocked
        )

        FeatureUnlockRow(
            icon = Icons.Default.Link,
            iconTint = Color(0xFFA78BFA),
            title = "Bio & Story Hyperlinks",
            desc = "Attach clickable website, store, and social links to your bio and stories.",
            isUnlocked = isUnlocked
        )

        FeatureUnlockRow(
            icon = Icons.Default.MonetizationOn,
            iconTint = Color(0xFFF59E0B),
            title = "Ad Revenue Sharing (40%) & Gift Cashouts",
            desc = "Earn revenue from feed ads displayed on your posts and cash out virtual gifts.",
            isUnlocked = isUnlocked
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Business Account Subscriptions Promotion
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
            border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💼", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Business Account Verification",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Subscribe for $7.99/month to get Marketplace top ranking, WhatsApp direct button, and 2x monthly post promotions.",
                    color = Color(0xFFC7D2FE),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSubscribeBusinessClick,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Text("Upgrade to Business Pro ($7.99/mo)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun FeatureUnlockRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    desc: String,
    isUnlocked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(iconTint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, color = Color(0xFF94A3B8), fontSize = 11.sp, lineHeight = 15.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isUnlocked) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF22C55E).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("UNLOCKED", color = Color(0xFF22C55E), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .background(Color(0xFF64748B).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("5K REQUIRED", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * TAB 1: Creator Analytics Dashboard
 */
@Composable
fun CreatorAnalyticsTab(
    analytics: CreatorAnalytics,
    isUnlocked: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Stats grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "Total Reach",
                value = "48.5K",
                subtext = "+14.2% this week",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Impressions",
                value = "124.0K",
                subtext = "Feed & Reels views",
                icon = Icons.Default.Visibility,
                tint = Color(0xFF34D399),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "Engagement Rate",
                value = "${analytics.engagementRate}%",
                subtext = "Likes, Comments, Shares",
                icon = Icons.Default.Star,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Video Plays",
                value = "89.2K",
                subtext = "Average 42s watch time",
                icon = Icons.Default.VideoLibrary,
                tint = Color(0xFFA78BFA),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Demographics breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Audience Demographics & Insights", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                AnalyticsDetailRow("Peak Engagement Times", analytics.peakEngagementHour, Icons.Default.PieChart)
                AnalyticsDetailRow("Top Age Groups", analytics.topAudienceAge, Icons.Default.Analytics)
                AnalyticsDetailRow("Top Geographies", analytics.topAudienceCountry, Icons.Default.Public)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Security / Anti-Spam Guardrail Notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Anti-Spam & Bot Guardrails Active", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Daily follow limit: 100 follows/day. Automated audits continuously check for fake engagement.", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp)
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtext, color = tint, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AnalyticsDetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text(text = value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

/**
 * TAB 2: Creator Earnings & Revenue Sharing
 */
@Composable
fun CreatorEarningsTab(
    wallet: CreatorEarningWallet,
    isUnlocked: Boolean,
    onCashout: (Double) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Wallet Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Available for Payout", color = Color(0xFF94A3B8), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${String.format("%.2f", wallet.availableCashoutUsd)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF22C55E).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Verified Balance", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("AdMob Feed Revenue (40% Share)", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("$${String.format("%.2f", wallet.adRevenueShareUsd)}", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Virtual Gifts Revenue (75% Share)", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("$${String.format("%.2f", wallet.giftRevenueUsd)}", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isUnlocked && wallet.availableCashoutUsd >= 50.0) {
                            onCashout(wallet.availableCashoutUsd)
                        } else if (!isUnlocked) {
                            Toast.makeText(context, "Reach 5,000 followers to unlock monetization cashouts.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Minimum cashout threshold is $50.00.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUnlocked && wallet.availableCashoutUsd >= 50.0) Color(0xFF22C55E) else Color(0xFF475569)
                    )
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUnlocked) "Request Payout to Bank ($${String.format("%.2f", wallet.availableCashoutUsd)})" else "Monetization Locked (Need 5K Followers)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Platform Earning & Revenue Sharing Policy
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Self-Sustaining Revenue Sharing Model", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Ad Revenue Share: Creators with 5,000+ followers receive 40% of all AdMob revenue generated on their feed and video impressions.\n" +
                            "• In-App Gifts: Viewers purchase Coins; platform retains 25% for infrastructure & Google Play fees; creators receive 75%.\n" +
                            "• Payouts processed monthly via Direct Bank Transfer or PayPal.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * TAB 3: Virtual Coins Store (In-App Purchases)
 */
@Composable
fun CoinStoreTab(
    wallet: CreatorEarningWallet,
    onBuy: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Current Coin Balance
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("My Virtual Coin Balance", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("${wallet.coinBalance} Coins", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Purchase Coin Packages (Google Play Billing)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        sampleCoinPackages.forEach { pkg ->
            CoinPackageRow(pkg = pkg, onBuy = {
                Toast.makeText(context, "Google Play Billing: Purchased ${pkg.coins + pkg.bonusCoins} Coins!", Toast.LENGTH_SHORT).show()
                onBuy()
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Available Virtual Gifts to Send", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sampleVirtualGifts.take(3).forEach { gift ->
                GiftItemCard(gift = gift, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sampleVirtualGifts.drop(3).take(3).forEach { gift ->
                GiftItemCard(gift = gift, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CoinPackageRow(
    pkg: CoinPackage,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🪙", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${pkg.coins} Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (pkg.bonusCoins > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF22C55E).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("+${pkg.bonusCoins} Bonus", color = Color(0xFF22C55E), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("${pkg.priceLkr} ($${pkg.priceUsd})", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
        }

        Button(
            onClick = onBuy,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text("Buy", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun GiftItemCard(
    gift: VirtualGiftItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(gift.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(gift.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("🪙 ${gift.coinCost}", color = Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
