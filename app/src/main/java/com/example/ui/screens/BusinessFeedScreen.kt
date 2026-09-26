package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.BusinessPage
import com.example.model.MediaType
import com.example.model.Post
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.viewmodel.MainViewModel

// Emerald Dark Theme Enterprise Colors
private val Suite_Slate900 = Color(0xFF0F172A) // Main Canvas
private val Suite_Slate800 = Color(0xFF1E293B) // Card Surface
private val Suite_Slate700 = Color(0xFF334155) // Card Border / Separators
private val Suite_Slate400 = Color(0xFF94A3B8) // Muted Text
private val Suite_Slate50 = Color(0xFFF8FAFC)  // High Contrast Text
private val Suite_Emerald = Color(0xFF10B981)   // Metrics & Success
private val Suite_EmeraldDark = Color(0xFF047857)
private val Suite_Cyan = Color(0xFF06B6D4)      // CTA Highlights & Actions
private val Suite_CyanDark = Color(0xFF0E7490)
private val Suite_Indigo = Color(0xFF6366F1)    // Announcements
private val Suite_Amber = Color(0xFFF59E0B)     // Reviews & Stars

// Data model for structured Business Feed items
private enum class BusinessFeedType {
    SPONSORED_PRODUCT,
    COMPANY_ANNOUNCEMENT,
    CUSTOMER_REVIEW,
    SHOP_UPDATE,
    USER_POST
}

private data class BusinessFeedCardItem(
    val id: String,
    val type: BusinessFeedType,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val content: String,
    val mediaUrl: String? = null,
    val price: String? = null,
    val originalPrice: String? = null,
    val discountPercent: String? = null,
    val rating: Float? = null,
    val reviewerName: String? = null,
    val verifiedBuyer: Boolean = false,
    val ctaLabel: String,
    val secondaryCtaLabel: String? = null,
    val reachCount: String = "4.2K",
    val ctr: String = "3.8%",
    val badgeLabel: String,
    val badgeColor: Color = Suite_Emerald
)

@Composable
fun BusinessFeedScreen(
    viewModel: MainViewModel,
    businessContext: BusinessPage
) {
    val context = LocalContext.current
    val businessPosts by viewModel.businessPosts.collectAsState()

    // Dialog Visibilities
    var showPromoteModal by remember { mutableStateOf(false) }
    var showAddProductModal by remember { mutableStateOf(false) }
    var showBusinessUpdateModal by remember { mutableStateOf(false) }
    var showCheckoutModal by remember { mutableStateOf<BusinessFeedCardItem?>(null) }
    var showInsightsSummaryModal by remember { mutableStateOf(false) }

    // Active Feed Tab Filter
    var selectedFeedCategory by remember { mutableStateOf("All") }
    var selectedTimeframe by remember { mutableStateOf("Last 28 Days") }

    // Custom Published Business Posts by user in this session
    var customUpdates by remember { mutableStateOf<List<BusinessFeedCardItem>>(emptyList()) }

    // Curated High-Fidelity Business Feed Items
    val defaultFeedItems = remember(businessContext) {
        listOf(
            BusinessFeedCardItem(
                id = "item_sponsored_anc",
                type = BusinessFeedType.SPONSORED_PRODUCT,
                title = businessContext.name,
                subtitle = "Sponsored • High Converting Ad ⚡",
                timestamp = "Active Campaign • 2h ago",
                content = "🎧 Pro Active Noise-Cancelling Wireless Headphones. Hi-Res Audio certification, 40-hour battery life, and ultra-comfortable memory foam cushions. Exclusive 30% discount for this week only!",
                mediaUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=1000&auto=format&fit=crop&q=80",
                price = "Rs. 18,500",
                originalPrice = "Rs. 26,000",
                discountPercent = "30% OFF",
                rating = 4.9f,
                ctaLabel = "Shop Now",
                secondaryCtaLabel = "Order via WhatsApp",
                reachCount = "12.4K",
                ctr = "5.2%",
                badgeLabel = "SPONSORED",
                badgeColor = Suite_Cyan
            ),
            BusinessFeedCardItem(
                id = "item_announcement_hub",
                type = BusinessFeedType.COMPANY_ANNOUNCEMENT,
                title = businessContext.name,
                subtitle = "Official Company Announcement 📢",
                timestamp = "Yesterday at 10:30 AM",
                content = "🚀 Exciting News: Our new Islandwide Express Delivery Logistics Hub in Colombo & Kandy is now officially operational! Enjoy guaranteed 24-hour delivery on all verified electronics and accessories with live tracking.",
                mediaUrl = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=1000&auto=format&fit=crop&q=80",
                ctaLabel = "Learn More",
                secondaryCtaLabel = "Share Update",
                reachCount = "8.9K",
                ctr = "4.1%",
                badgeLabel = "ANNOUNCEMENT",
                badgeColor = Suite_Indigo
            ),
            BusinessFeedCardItem(
                id = "item_review_verified",
                type = BusinessFeedType.CUSTOMER_REVIEW,
                title = businessContext.name,
                subtitle = "Verified Customer Review ⭐ 5.0",
                timestamp = "3 days ago",
                content = "\"Ordered the Smart Watch Series 9 on Wednesday, received it by Thursday noon in Galle. Flawless genuine packaging, 1-year brand warranty card included. Customer service on WhatsApp was super responsive!\"",
                reviewerName = "Niluka Perera",
                verifiedBuyer = true,
                rating = 5.0f,
                ctaLabel = "Send Message",
                secondaryCtaLabel = "View Product",
                reachCount = "6.1K",
                ctr = "3.4%",
                badgeLabel = "5-STAR REVIEW",
                badgeColor = Suite_Amber
            ),
            BusinessFeedCardItem(
                id = "item_shop_catalog_drop",
                type = BusinessFeedType.SHOP_UPDATE,
                title = businessContext.name,
                subtitle = "Catalog Restocked & Ready for Dispatch 🛒",
                timestamp = "4 days ago",
                content = "📦 Massive Catalog Restock! Smart wearables, fast GaN fast chargers, and MagSafe wireless battery packs are now back in stock at wholesale & retail prices. Limited units available.",
                mediaUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&auto=format&fit=crop&q=80",
                price = "Starting from Rs. 4,200",
                ctaLabel = "Browse Catalog",
                secondaryCtaLabel = "Inquire Stock",
                reachCount = "15.3K",
                ctr = "4.7%",
                badgeLabel = "IN STOCK",
                badgeColor = Suite_Emerald
            )
        )
    }

    val allBusinessCards = remember(customUpdates, defaultFeedItems, selectedFeedCategory) {
        val combined = customUpdates + defaultFeedItems
        when (selectedFeedCategory) {
            "Sponsored" -> combined.filter { it.type == BusinessFeedType.SPONSORED_PRODUCT }
            "Announcements" -> combined.filter { it.type == BusinessFeedType.COMPANY_ANNOUNCEMENT }
            "Reviews" -> combined.filter { it.type == BusinessFeedType.CUSTOMER_REVIEW }
            "Shop Updates" -> combined.filter { it.type == BusinessFeedType.SHOP_UPDATE }
            else -> combined
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Suite_Slate900)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // -------------------------------------------------------------
            // SECTION 1: BUSINESS HEADER & ANALYTICS DASHBOARD
            // -------------------------------------------------------------
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Suite_Slate800,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        // Business Identity & Status Pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.size(46.dp)) {
                                    if (businessContext.imageUrl.isNotBlank() && !businessContext.imageUrl.contains("default")) {
                                        AsyncImage(
                                            model = businessContext.imageUrl,
                                            contentDescription = "Business Logo",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .border(2.dp, Suite_Emerald, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(Suite_Emerald),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = businessContext.name.take(1).uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                        }
                                    }
                                    // Status Badge
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(Suite_Emerald)
                                            .border(2.dp, Suite_Slate800, CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = businessContext.name,
                                            color = Suite_Slate50,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = PlusJakartaSansFamily,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Business",
                                            tint = Suite_Cyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "${businessContext.category.ifBlank { "Retail & E-Commerce" }} • Enterprise Suite",
                                        color = Suite_Slate400,
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaSansFamily
                                    )
                                }
                            }

                            // Timeframe Filter Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Suite_Slate900,
                                modifier = Modifier.clickable {
                                    selectedTimeframe = if (selectedTimeframe == "Last 28 Days") "Last 7 Days" else "Last 28 Days"
                                    Toast.makeText(context, "Analytics set to $selectedTimeframe", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = selectedTimeframe,
                                        color = Suite_Emerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = PlusJakartaSansFamily
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Timeframe",
                                        tint = Suite_Emerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Metrics Row (Post Reach, Page Engagement, New Leads)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Metric 1: Post Reach
                            AnalyticsMetricCard(
                                title = "Post Reach",
                                value = if (selectedTimeframe == "Last 28 Days") "12.5K" else "4.8K",
                                growth = "↑ 32.4%",
                                growthPositive = true,
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                accentColor = Suite_Emerald,
                                modifier = Modifier.weight(1f),
                                onClick = { showInsightsSummaryModal = true }
                            )

                            // Metric 2: Page Engagement
                            AnalyticsMetricCard(
                                title = "Page Engagement",
                                value = if (selectedTimeframe == "Last 28 Days") "4.8K" else "1.9K",
                                growth = "↑ 18.2%",
                                growthPositive = true,
                                icon = Icons.Default.BarChart,
                                accentColor = Suite_Emerald,
                                modifier = Modifier.weight(1f),
                                onClick = { showInsightsSummaryModal = true }
                            )

                            // Metric 3: New Leads
                            AnalyticsMetricCard(
                                title = "New Leads",
                                value = if (selectedTimeframe == "Last 28 Days") "142" else "38",
                                growth = "+28 hot",
                                growthPositive = true,
                                icon = Icons.Default.GroupAdd,
                                accentColor = Suite_Cyan,
                                modifier = Modifier.weight(1f),
                                onClick = { showInsightsSummaryModal = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Action Buttons Row (Promote, Add Product, Post Update, Messages)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickActionSuiteButton(
                                label = "Promote / Ad",
                                icon = Icons.Default.Campaign,
                                primaryColor = Suite_Emerald,
                                modifier = Modifier.weight(1f),
                                onClick = { showPromoteModal = true }
                            )

                            QuickActionSuiteButton(
                                label = "Add Product",
                                icon = Icons.Default.ShoppingBag,
                                primaryColor = Suite_Cyan,
                                modifier = Modifier.weight(1f),
                                onClick = { showAddProductModal = true }
                            )

                            QuickActionSuiteButton(
                                label = "Post Update",
                                icon = Icons.Default.EditNote,
                                primaryColor = Suite_Indigo,
                                modifier = Modifier.weight(1f),
                                onClick = { showBusinessUpdateModal = true }
                            )

                            QuickActionSuiteButton(
                                label = "Messages",
                                icon = Icons.AutoMirrored.Filled.Chat,
                                badgeText = "3",
                                primaryColor = Color(0xFF0084FF),
                                iconPainter = painterResource(id = R.drawable.ic_3d_chat_bubble_vector),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    // Navigate to Business Messaging Tab (Tab 1)
                                    viewModel.selectTab(1)
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Suite_Slate700, thickness = 0.5.dp)
            }

            // -------------------------------------------------------------
            // SECTION 2: BUSINESS FEED FILTER CHIPS
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf("All", "Sponsored", "Announcements", "Reviews", "Shop Updates")
                    categories.forEach { cat ->
                        val isSelected = selectedFeedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Suite_Emerald else Suite_Slate800,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Suite_Emerald else Suite_Slate700
                            ),
                            modifier = Modifier.clickable { selectedFeedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else Suite_Slate400,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = PlusJakartaSansFamily,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 3: RESTRUCTURED BUSINESS FEED CARDS
            // -------------------------------------------------------------
            items(allBusinessCards, key = { it.id }) { item ->
                BusinessFeedCard(
                    item = item,
                    onPrimaryCtaClick = {
                        when (item.type) {
                            BusinessFeedType.SPONSORED_PRODUCT -> {
                                showCheckoutModal = item
                            }
                            BusinessFeedType.COMPANY_ANNOUNCEMENT -> {
                                Toast.makeText(context, "Opening logistics & expansion update ↗", Toast.LENGTH_SHORT).show()
                            }
                            BusinessFeedType.CUSTOMER_REVIEW -> {
                                viewModel.openChatForBusiness(item.title, businessContext.imageUrl, businessContext.phone)
                            }
                            BusinessFeedType.SHOP_UPDATE -> {
                                viewModel.selectTab(4) // Open Store Tab
                            }
                            BusinessFeedType.USER_POST -> {
                                Toast.makeText(context, "Action logged for ${item.title}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onSecondaryCtaClick = {
                        when (item.type) {
                            BusinessFeedType.SPONSORED_PRODUCT -> {
                                val cleanPhone = businessContext.phone.ifBlank { "0771234567" }.replace(Regex("[^0-9]"), "")
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://wa.me/94${cleanPhone.removePrefix("0")}?text=Hi%20${Uri.encode(businessContext.name)},%20I%20want%20to%20order%20the%20${Uri.encode(item.title)}%20(${item.price})")
                                )
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp order opened for ${item.title}", Toast.LENGTH_SHORT).show()
                                }
                            }
                            BusinessFeedType.COMPANY_ANNOUNCEMENT -> {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${item.title}: ${item.content}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Announcement"))
                            }
                            BusinessFeedType.CUSTOMER_REVIEW -> {
                                viewModel.selectTab(4) // View in Shop
                            }
                            BusinessFeedType.SHOP_UPDATE -> {
                                viewModel.openChatForBusiness(businessContext.name, businessContext.imageUrl, businessContext.phone)
                            }
                            BusinessFeedType.USER_POST -> {}
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // User's standard repository business posts (if any exist beyond template)
            if (businessPosts.isNotEmpty() && selectedFeedCategory == "All") {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ORGANIC BUSINESS POSTS",
                        color = Suite_Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(businessPosts) { post ->
                    LegacyBusinessPostWrapper(
                        post = post,
                        businessContext = businessContext,
                        onMessageClick = {
                            viewModel.openChatForBusiness(businessContext.name, businessContext.imageUrl, businessContext.phone)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // MODALS & DIALOGS (Promote / Ad, Add Product, Post Update, Insights)
    // -------------------------------------------------------------

    // 1. Promote / Create Ad Modal
    if (showPromoteModal) {
        BusinessPromoteAdDialog(
            pageName = businessContext.name,
            onDismiss = { showPromoteModal = false },
            onLaunch = { budget, duration, audience, goal ->
                showPromoteModal = false
                Toast.makeText(
                    context,
                    "🚀 Ad Campaign launched for ${businessContext.name}!\nBudget: Rs. $budget • Audience: $audience • Target Reach: 35,000+",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // 2. Add Product to Shop Modal
    if (showAddProductModal) {
        BusinessAddProductDialog(
            onDismiss = { showAddProductModal = false },
            onProductAdded = { title, price, desc, imageUrl ->
                showAddProductModal = false
                // Prepend new catalog item into the feed
                val newCard = BusinessFeedCardItem(
                    id = "custom_prod_${System.currentTimeMillis()}",
                    type = BusinessFeedType.SHOP_UPDATE,
                    title = businessContext.name,
                    subtitle = "New Catalog Item Added 🛍️",
                    timestamp = "Just now",
                    content = "✨ Just Added: $title\n$desc",
                    mediaUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&auto=format&fit=crop&q=80" },
                    price = price,
                    ctaLabel = "Shop Now",
                    secondaryCtaLabel = "Inquire on WhatsApp",
                    reachCount = "1.2K",
                    ctr = "4.0%",
                    badgeLabel = "NEW ARRIVAL",
                    badgeColor = Suite_Emerald
                )
                customUpdates = listOf(newCard) + customUpdates
                Toast.makeText(context, "Product '$title' published to Business Shop & Feed! 🎉", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 3. Post Business Update Modal
    if (showBusinessUpdateModal) {
        BusinessPostUpdateDialog(
            pageName = businessContext.name,
            onDismiss = { showBusinessUpdateModal = false },
            onPublish = { text, mediaUrl, category, ctaType ->
                showBusinessUpdateModal = false
                val badge = when (category) {
                    "Special Promotion" -> "PROMOTION"
                    "Product Drop" -> "PRODUCT DROP"
                    else -> "ANNOUNCEMENT"
                }
                val badgeColor = when (category) {
                    "Special Promotion" -> Suite_Cyan
                    "Product Drop" -> Suite_Emerald
                    else -> Suite_Indigo
                }
                val ctaText = when (ctaType) {
                    "Shop Now" -> "Shop Now"
                    "Send Message" -> "Send Message"
                    else -> "Learn More"
                }
                val newCard = BusinessFeedCardItem(
                    id = "custom_update_${System.currentTimeMillis()}",
                    type = if (category == "Special Promotion") BusinessFeedType.SPONSORED_PRODUCT else BusinessFeedType.COMPANY_ANNOUNCEMENT,
                    title = businessContext.name,
                    subtitle = "$category • Official Update",
                    timestamp = "Just now",
                    content = text,
                    mediaUrl = mediaUrl,
                    ctaLabel = ctaText,
                    secondaryCtaLabel = "Share Update",
                    reachCount = "2.4K",
                    ctr = "3.9%",
                    badgeLabel = badge,
                    badgeColor = badgeColor
                )
                customUpdates = listOf(newCard) + customUpdates
                viewModel.createBusinessPost(businessContext.name, businessContext.imageUrl, text, mediaUrl, if (mediaUrl != null) MediaType.IMAGE else MediaType.NONE)
                Toast.makeText(context, "Business update published successfully! 🚀", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 4. Quick Checkout / Shop Now Modal for Feed Products
    showCheckoutModal?.let { item ->
        BusinessQuickCheckoutDialog(
            item = item,
            businessName = businessContext.name,
            businessPhone = businessContext.phone,
            onDismiss = { showCheckoutModal = null },
            onConfirmed = { buyerName, buyerPhone, address, paymentMethod ->
                showCheckoutModal = null
                Toast.makeText(
                    context,
                    "🎉 Order Confirmed!\nItem: ${item.title}\nTotal: ${item.price}\nPayment: $paymentMethod\nDispatching within 24 hours.",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // 5. Full Insights Summary Dialog
    if (showInsightsSummaryModal) {
        BusinessInsightsSummaryDialog(
            businessContext = businessContext,
            onDismiss = { showInsightsSummaryModal = false },
            onViewFullAnalytics = {
                showInsightsSummaryModal = false
                viewModel.selectTab(4)
            }
        )
    }
}

// -------------------------------------------------------------
// COMPONENT: Analytics Metric Card
// -------------------------------------------------------------
@Composable
private fun AnalyticsMetricCard(
    title: String,
    value: String,
    growth: String,
    growthPositive: Boolean,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Suite_Slate900),
        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Suite_Slate400,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = PlusJakartaSansFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
            }

            Text(
                text = value,
                color = Suite_Slate50,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSansFamily
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = growth,
                    color = if (growthPositive) accentColor else Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFamily
                )
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: Quick Action Suite Button
// -------------------------------------------------------------
@Composable
private fun QuickActionSuiteButton(
    label: String,
    icon: ImageVector,
    primaryColor: Color,
    badgeText: String? = null,
    iconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Suite_Slate900,
        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (iconPainter != null) {
                            Image(
                                painter = iconPainter,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (badgeText != null) {
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = (-2).dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = label,
                    color = Suite_Slate50,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSansFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: Restructured Business Feed Card
// -------------------------------------------------------------
@Composable
private fun BusinessFeedCard(
    item: BusinessFeedCardItem,
    onPrimaryCtaClick: () -> Unit,
    onSecondaryCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Badge, Subtitle & Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = item.badgeColor.copy(alpha = 0.18f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, item.badgeColor)
                    ) {
                        Text(
                            text = item.badgeLabel,
                            color = item.badgeColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFamily,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = item.subtitle,
                        color = Suite_Slate400,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = PlusJakartaSansFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = item.timestamp,
                    color = Suite_Slate400,
                    fontSize = 10.5.sp,
                    fontFamily = PlusJakartaSansFamily
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Content
            Text(
                text = item.content,
                color = Suite_Slate50,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                fontFamily = PlusJakartaSansFamily
            )

            // Rating Stars (For Customer Reviews)
            if (item.rating != null && item.reviewerName != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Suite_Slate900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐️⭐️⭐️⭐️⭐️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${item.rating} Rating",
                                color = Suite_Amber,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (item.verifiedBuyer) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Verified Buyer",
                                    tint = Suite_Emerald,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified Buyer (${item.reviewerName})",
                                    color = Suite_Emerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Media Image (if available)
            if (!item.mediaUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Suite_Slate900)
                ) {
                    AsyncImage(
                        model = item.mediaUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Pricing Overlay Pill for Product Cards
                    if (!item.price.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Cyan.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.price,
                                    color = Suite_Cyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = PlusJakartaSansFamily
                                )
                                if (!item.originalPrice.isNullOrBlank()) {
                                    Text(
                                        text = item.originalPrice,
                                        color = Suite_Slate400,
                                        fontSize = 11.sp,
                                        textDecoration = TextDecoration.LineThrough,
                                        fontFamily = PlusJakartaSansFamily
                                    )
                                }
                                if (!item.discountPercent.isNullOrBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Suite_Emerald
                                    ) {
                                        Text(
                                            text = item.discountPercent,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Conversion / Performance Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Suite_Slate900, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Reach",
                        tint = Suite_Slate400,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${item.reachCount} Impressions",
                        color = Suite_Slate400,
                        fontSize = 11.sp,
                        fontFamily = PlusJakartaSansFamily
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "CTR",
                        tint = Suite_Emerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "CTR ${item.ctr}",
                        color = Suite_Emerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action CTAs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Secondary CTA (e.g. "Order via WhatsApp", "Share", "View Product")
                if (!item.secondaryCtaLabel.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = onSecondaryCtaClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Suite_Slate50
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = item.secondaryCtaLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = PlusJakartaSansFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Primary CTA (e.g. "Shop Now", "Learn More", "Send Message")
                Button(
                    onClick = onPrimaryCtaClick,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.type == BusinessFeedType.SPONSORED_PRODUCT) Suite_Cyan else Suite_Emerald,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.ctaLabel,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: Legacy Business Post Wrapper (with CTAs)
// -------------------------------------------------------------
@Composable
private fun LegacyBusinessPostWrapper(
    post: Post,
    businessContext: BusinessPage,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = post.userAvatar.ifBlank { businessContext.imageUrl },
                        contentDescription = post.userName,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Suite_Emerald, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.userName.ifBlank { businessContext.name },
                                color = Suite_Slate50,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Suite_Cyan, modifier = Modifier.size(14.dp))
                        }
                        Text(text = post.timestamp, color = Suite_Slate400, fontSize = 11.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Suite_Emerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "BUSINESS POST",
                        color = Suite_Emerald,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = post.content,
                color = Suite_Slate50,
                fontSize = 13.5.sp,
                lineHeight = 19.sp
            )

            if (!post.mediaUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.mediaUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMessageClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF)),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_3d_chat_bubble_vector),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Message", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: Promote & Create Ad Campaign
// -------------------------------------------------------------
@Composable
private fun BusinessPromoteAdDialog(
    pageName: String,
    onDismiss: () -> Unit,
    onLaunch: (budget: String, duration: String, audience: String, goal: String) -> Unit
) {
    var selectedBudget by remember { mutableStateOf("Rs. 3,500") }
    var selectedDuration by remember { mutableStateOf("7 Days") }
    var selectedAudience by remember { mutableStateOf("Islandwide Sri Lanka (All Cities)") }
    var selectedGoal by remember { mutableStateOf("Direct WhatsApp & Messenger Inquiries") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Suite_Emerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Suite_Emerald, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "Promote / Create Ad",
                            color = Suite_Slate50,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                Text(
                    text = "Launch a targeted paid ad campaign for $pageName across feeds and reels.",
                    color = Suite_Slate400,
                    fontSize = 12.sp
                )

                // Projected Metric Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Suite_Slate900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Emerald.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Estimated Reach", color = Suite_Slate400, fontSize = 11.sp)
                            Text("35K - 80K", color = Suite_Emerald, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Lead Conversion", color = Suite_Slate400, fontSize = 11.sp)
                            Text("180 - 420 leads", color = Suite_Cyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Budget Selector
                Column {
                    Text("Campaign Budget", color = Suite_Slate400, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val budgets = listOf("Rs. 1,500", "Rs. 3,500", "Rs. 7,500", "Rs. 15,000")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        budgets.forEach { b ->
                            val isSel = selectedBudget == b
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Suite_Emerald else Suite_Slate900,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedBudget = b }
                            ) {
                                Text(
                                    text = b,
                                    color = if (isSel) Color.White else Suite_Slate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Duration Selector
                Column {
                    Text("Duration", color = Suite_Slate400, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val durations = listOf("3 Days", "7 Days", "14 Days", "30 Days")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        durations.forEach { d ->
                            val isSel = selectedDuration == d
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Suite_Cyan else Suite_Slate900,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedDuration = d }
                            ) {
                                Text(
                                    text = d,
                                    color = if (isSel) Color.White else Suite_Slate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Launch Button
                Button(
                    onClick = { onLaunch(selectedBudget, selectedDuration, selectedAudience, selectedGoal) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Launch Ad Boost ($selectedBudget)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: Add Product to Shop
// -------------------------------------------------------------
@Composable
private fun BusinessAddProductDialog(
    onDismiss: () -> Unit,
    onProductAdded: (title: String, price: String, desc: String, imageUrl: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedPhotoUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Product to Shop",
                        color = Suite_Slate50,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title (භාණ්ඩයේ නම)", color = Suite_Slate400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Suite_Slate50,
                        unfocusedTextColor = Suite_Slate50,
                        focusedBorderColor = Suite_Cyan,
                        unfocusedBorderColor = Suite_Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (මිළ - උදා: Rs. 14,500)", color = Suite_Slate400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Suite_Slate50,
                        unfocusedTextColor = Suite_Slate50,
                        focusedBorderColor = Suite_Cyan,
                        unfocusedBorderColor = Suite_Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Specifications", color = Suite_Slate400) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Suite_Slate50,
                        unfocusedTextColor = Suite_Slate50,
                        focusedBorderColor = Suite_Cyan,
                        unfocusedBorderColor = Suite_Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Photo Selector
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Suite_Slate900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Suite_Cyan, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (selectedPhotoUri != null) "Photo Selected (Ready to publish)" else "Upload Product Photo from Gallery",
                            color = if (selectedPhotoUri != null) Suite_Emerald else Suite_Slate400,
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && price.isNotBlank()) {
                            onProductAdded(
                                title,
                                price,
                                description.ifBlank { "High quality product with official warranty." },
                                selectedPhotoUri?.toString() ?: ""
                            )
                        }
                    },
                    enabled = title.isNotBlank() && price.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Suite_Cyan),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Publish to Catalog & Feed", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: Post Business Update
// -------------------------------------------------------------
@Composable
private fun BusinessPostUpdateDialog(
    pageName: String,
    onDismiss: () -> Unit,
    onPublish: (text: String, mediaUrl: String?, category: String, ctaType: String) -> Unit
) {
    var updateText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Official Announcement") }
    var selectedCta by remember { mutableStateOf("Learn More") }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedMediaUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Post Business Update",
                        color = Suite_Slate50,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                // Category selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val categories = listOf("Announcement", "Special Promotion", "Product Drop")
                    categories.forEach { cat ->
                        val isSel = selectedCategory.contains(cat.take(5))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Suite_Indigo else Suite_Slate900,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.White else Suite_Slate400,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = updateText,
                    onValueChange = { updateText = it },
                    placeholder = { Text("Write corporate announcement, store hours, or promotional update...", color = Suite_Slate400) },
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Suite_Slate50,
                        unfocusedTextColor = Suite_Slate50,
                        focusedBorderColor = Suite_Indigo,
                        unfocusedBorderColor = Suite_Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // CTA Button Selector
                Column {
                    Text("Call-To-Action (CTA) Button", color = Suite_Slate400, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val ctas = listOf("Learn More", "Shop Now", "Send Message")
                        ctas.forEach { c ->
                            val isSel = selectedCta == c
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Suite_Cyan else Suite_Slate900,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedCta = c }
                            ) {
                                Text(
                                    text = c,
                                    color = if (isSel) Color.White else Suite_Slate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // Media Attachment
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Suite_Slate900,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Suite_Indigo, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (selectedMediaUri != null) "Media Attached ✅" else "Attach Photo / Banner",
                            color = if (selectedMediaUri != null) Suite_Emerald else Suite_Slate400,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        if (updateText.isNotBlank() || selectedMediaUri != null) {
                            onPublish(updateText, selectedMediaUri?.toString(), selectedCategory, selectedCta)
                        }
                    },
                    enabled = updateText.isNotBlank() || selectedMediaUri != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Suite_Indigo),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Publish to Enterprise Feed", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: Quick Checkout Preview for "Shop Now"
// -------------------------------------------------------------
@Composable
private fun BusinessQuickCheckoutDialog(
    item: BusinessFeedCardItem,
    businessName: String,
    businessPhone: String,
    onDismiss: () -> Unit,
    onConfirmed: (buyerName: String, buyerPhone: String, address: String, payment: String) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("Cash on Delivery (COD)") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant Order / Checkout",
                        color = Suite_Slate50,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                // Item Summary
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Suite_Slate900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!item.mediaUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.mediaUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, color = Suite_Slate50, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(item.price ?: "Rs. 18,500", color = Suite_Cyan, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Your Name (නම)", color = Suite_Slate400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Suite_Slate50,
                        unfocusedTextColor = Suite_Slate50,
                        focusedBorderColor = Suite_Cyan,
                        unfocusedBorderColor = Suite_Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Phone Number (දුරකථන අංකය)", color = Suite_Slate400) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Suite_Slate50,
                        unfocusedTextColor = Suite_Slate50,
                        focusedBorderColor = Suite_Cyan,
                        unfocusedBorderColor = Suite_Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Delivery Address (ලිපිනය)", color = Suite_Slate400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Suite_Slate50,
                        unfocusedTextColor = Suite_Slate50,
                        focusedBorderColor = Suite_Cyan,
                        unfocusedBorderColor = Suite_Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        onConfirmed(
                            customerName.ifBlank { "Valued Customer" },
                            customerPhone.ifBlank { "0771234567" },
                            deliveryAddress.ifBlank { "Colombo, Sri Lanka" },
                            selectedPayment
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Suite_Cyan),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Confirm Order (${item.price ?: "Rs. 18,500"})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: Business Insights Summary
// -------------------------------------------------------------
@Composable
private fun BusinessInsightsSummaryDialog(
    businessContext: BusinessPage,
    onDismiss: () -> Unit,
    onViewFullAnalytics: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Suite_Slate800,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = Suite_Emerald)
                Text("Business Suite Analytics", color = Suite_Slate50, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Performance Overview for ${businessContext.name} (Last 28 Days)",
                    color = Suite_Slate400,
                    fontSize = 12.sp
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Suite_Slate900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Post Impressions", color = Suite_Slate400, fontSize = 12.sp)
                            Text("12,480 (↑ 32%)", color = Suite_Emerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Customer Interactions", color = Suite_Slate400, fontSize = 12.sp)
                            Text("4,820 (↑ 18%)", color = Suite_Emerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Qualified Inquiries", color = Suite_Slate400, fontSize = 12.sp)
                            Text("142 New Leads", color = Suite_Cyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ad Conversion Rate", color = Suite_Slate400, fontSize = 12.sp)
                            Text("4.8% Average", color = Suite_Slate50, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onViewFullAnalytics,
                colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald)
            ) {
                Text("Open Store & Full Suite", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Suite_Slate400)
            }
        }
    )
}
