package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.model.BusinessPage
import com.example.model.Post
import com.example.ui.components.PostCard
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.viewmodel.MainViewModel

// Emerald Dark Theme Enterprise Colors
private val Suite_Slate900 = Color(0xFF0F172A) // Main Background
private val Suite_Slate800 = Color(0xFF1E293B) // Card Surface
private val Suite_Slate700 = Color(0xFF334155) // Card Border / Separators
private val Suite_Slate400 = Color(0xFF94A3B8) // Muted Text
private val Suite_Slate50 = Color(0xFFF8FAFC)  // High Contrast Text
private val Suite_Emerald = Color(0xFF10B981)   // CTA Primary
private val Suite_Cyan = Color(0xFF06B6D4)      // Highlights & Accents
private val Suite_WhatsApp = Color(0xFF25D366)  // WhatsApp Brand Green
private val Suite_Amber = Color(0xFFF59E0B)     // Rating & Reviews

// Data model for Store Products supporting up to 10 photos
data class StoreProductItem(
    val id: String,
    val title: String,
    val price: String, // e.g. "Rs. 18,500"
    val originalPrice: String? = null, // e.g. "Rs. 24,000"
    val discountPercent: String? = null, // e.g. "23% OFF"
    val description: String,
    val images: List<String>, // Up to 10 images
    val inStock: Boolean = true,
    val stockQuantity: Int = 18,
    val category: String = "Electronics",
    val rating: Float = 4.9f,
    val reviewCount: Int = 38,
    val warranty: String = "1 Year Brand Warranty",
    val deliveryInfo: String = "Islandwide Express Delivery (24-48 Hours)"
)

@Composable
fun BusinessStoreScreen(
    page: BusinessPage,
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val posts by viewModel.posts.collectAsState()

    // Page profile local state (supports real-time customization)
    var currentPageState by remember(page) {
        mutableStateOf(
            if (page.name.contains("Store") || page.name.contains("Official")) {
                page
            } else {
                page.copy(
                    name = "Wasantha Kumara Official Store",
                    category = "E-Commerce & Retail • Official Store",
                    coverUrl = if (page.coverUrl.isNotBlank()) page.coverUrl else "https://images.unsplash.com/photo-1557821552-17105176674c?w=1200&auto=format&fit=crop&q=80",
                    phone = if (page.phone.isNotBlank()) page.phone else "0771234567",
                    address = if (page.address.isNotBlank()) page.address else "Pelmadulla, Rathnapura, Sri Lanka",
                    bio = if (page.bio.isNotBlank()) page.bio else "Official Store for premium gadgets, smart wearables & lifestyle accessories. Islandwide Cash on Delivery available!"
                )
            }
        )
    }

    // Default high-converting product catalog with rich multi-photo galleries
    var productsList by remember {
        mutableStateOf(
            listOf(
                StoreProductItem(
                    id = "prod_emerald_watch",
                    title = "Pro AMOLED Smart Watch Series 9",
                    price = "Rs. 18,500",
                    originalPrice = "Rs. 26,000",
                    discountPercent = "29% OFF",
                    description = "Ultra HD 2.02\" Curved AMOLED Always-On Display. Bluetooth HD Calling, Heart Rate, SpO2, Sleep Tracking, 100+ Sports Modes, and IP68 Waterproof. Includes dual magnetic straps and wireless magnetic fast charger.",
                    images = listOf(
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=1000&auto=format&fit=crop&q=80"
                    ),
                    inStock = true,
                    stockQuantity = 14,
                    category = "Wearables",
                    rating = 4.9f,
                    reviewCount = 52
                ),
                StoreProductItem(
                    id = "prod_anc_headphones",
                    title = "Hi-Res Active Noise Cancelling Headphones",
                    price = "Rs. 22,000",
                    originalPrice = "Rs. 30,000",
                    discountPercent = "26% OFF",
                    description = "Hybrid Active Noise Cancellation blocking 95% of ambient noise. Certified Hi-Res Audio with 40mm titanium drivers. 45-hour ultra battery life with quick charge (5 min charge gives 4 hours playback).",
                    images = listOf(
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=1000&auto=format&fit=crop&q=80"
                    ),
                    inStock = true,
                    stockQuantity = 9,
                    category = "Audio",
                    rating = 5.0f,
                    reviewCount = 34
                ),
                StoreProductItem(
                    id = "prod_studio_speaker",
                    title = "Studio 360° Surround Bluetooth Speaker",
                    price = "Rs. 16,900",
                    originalPrice = "Rs. 21,500",
                    discountPercent = "21% OFF",
                    description = "Dual passive radiators delivering room-filling bass and crystal clear acoustics. IPX7 waterproof rating for outdoor use. TWS pairing to connect 2 speakers simultaneously. 18-hour continuous battery life.",
                    images = listOf(
                        "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=1000&auto=format&fit=crop&q=80"
                    ),
                    inStock = true,
                    stockQuantity = 22,
                    category = "Audio",
                    rating = 4.8f,
                    reviewCount = 28
                ),
                StoreProductItem(
                    id = "prod_gan_charger",
                    title = "100W GaN Fast Charger & MagSafe Power Bank",
                    price = "Rs. 9,500",
                    originalPrice = "Rs. 13,000",
                    discountPercent = "27% OFF",
                    description = "Gallium Nitride (GaN) III technology charging laptops, tablets, and phones simultaneously. Includes 10,000mAh magnetic wireless power bank with folding stand and digital LED percentage display.",
                    images = listOf(
                        "https://images.unsplash.com/photo-1622445262464-84b14e4b7de7?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=1000&auto=format&fit=crop&q=80"
                    ),
                    inStock = true,
                    stockQuantity = 30,
                    category = "Accessories",
                    rating = 4.9f,
                    reviewCount = 47
                ),
                StoreProductItem(
                    id = "prod_tactical_bag",
                    title = "Cyber Pro Waterproof Laptop Backpack",
                    price = "Rs. 11,200",
                    originalPrice = "Rs. 15,500",
                    discountPercent = "28% OFF",
                    description = "Engineered with 1680D ballistic waterproof oxford fabric. Shockproof 16-inch laptop compartment, hidden anti-theft RFID passport pocket, external USB 3.0 pass-through charging port.",
                    images = listOf(
                        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=1000&auto=format&fit=crop&q=80"
                    ),
                    inStock = true,
                    stockQuantity = 11,
                    category = "Accessories",
                    rating = 4.8f,
                    reviewCount = 19
                ),
                StoreProductItem(
                    id = "prod_gaming_buds",
                    title = "Ultra Low Latency TWS Gaming Earbuds",
                    price = "Rs. 7,800",
                    originalPrice = "Rs. 10,500",
                    discountPercent = "25% OFF",
                    description = "40ms ultra-low gaming latency mode with RGB breathing light case. Dual ENC microphones for noise-free voice chat. 36 hours total battery with charging case.",
                    images = listOf(
                        "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=1000&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b?w=1000&auto=format&fit=crop&q=80"
                    ),
                    inStock = true,
                    stockQuantity = 25,
                    category = "Wearables",
                    rating = 4.7f,
                    reviewCount = 23
                )
            )
        )
    }

    // Interactive Modals State
    var selectedProductForDetail by remember { mutableStateOf<StoreProductItem?>(null) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showQuickBuyCheckout by remember { mutableStateOf<StoreProductItem?>(null) }

    // Tab state & category filter state
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Products", "News & Updates", "Reviews", "About")
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    // Filtered products and chunks computed in Composable scope (outside LazyColumn)
    val filteredProducts = remember(productsList, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") productsList
        else productsList.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }
    val chunkedProductPairs = remember(filteredProducts) {
        filteredProducts.chunked(2)
    }
    val businessPosts = remember(posts, currentPageState.name) {
        posts.filter { it.userName.equals(currentPageState.name, ignoreCase = true) }
    }

    // Cover Photo Picker Launcher
    val coverPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val updated = currentPageState.copy(coverUrl = uri.toString())
            currentPageState = updated
            viewModel.updateBusinessProfile(updated)
            Toast.makeText(context, "Cover photo updated successfully! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    // Avatar Logo Picker Launcher
    val avatarPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val updated = currentPageState.copy(imageUrl = uri.toString())
            currentPageState = updated
            viewModel.updateBusinessProfile(updated)
            Toast.makeText(context, "Business Profile picture updated! ✨", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper: Function to trigger WhatsApp ordering
    val orderViaWhatsApp: (StoreProductItem) -> Unit = { product ->
        val cleanPhone = currentPageState.phone.ifBlank { "0771234567" }.replace(Regex("[^0-9]"), "")
        val internationalPhone = when {
            cleanPhone.startsWith("94") -> cleanPhone
            cleanPhone.startsWith("0") -> "94" + cleanPhone.substring(1)
            else -> "94$cleanPhone"
        }
        val prefilledMsg = "Hi ${currentPageState.name}, I am interested in buying *${product.title}* priced at *${product.price}*. Please share stock availability and islandwide delivery details! 🛍️"
        val encodedMsg = Uri.encode(prefilledMsg)
        val whatsappUri = Uri.parse("https://wa.me/$internationalPhone?text=$encodedMsg")
        val intent = Intent(Intent.ACTION_VIEW, whatsappUri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Opening WhatsApp for $internationalPhone...", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Suite_Slate900
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // -----------------------------------------------------------------
            // SECTION 1: COVER PHOTO FRAME, AVATAR & BUSINESS PROFILE HEADER
            // -----------------------------------------------------------------
            item {
                BusinessCoverAndHeaderSection(
                    page = currentPageState,
                    productCount = productsList.size,
                    onChangeCoverClick = {
                        coverPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onChangeAvatarClick = {
                        avatarPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onEditProfileClick = { showEditProfileDialog = true },
                    onContactPageClick = {
                        viewModel.openChatForBusiness(
                            currentPageState.name,
                            currentPageState.imageUrl,
                            currentPageState.phone
                        )
                        Toast.makeText(context, "Opening chat with ${currentPageState.name}...", Toast.LENGTH_SHORT).show()
                    },
                    onPhoneClick = {
                        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${currentPageState.phone}"))
                        try {
                            context.startActivity(callIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Calling ${currentPageState.phone}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onWhatsAppClick = {
                        val cleanPhone = currentPageState.phone.ifBlank { "0771234567" }.replace(Regex("[^0-9]"), "")
                        val intPhone = if (cleanPhone.startsWith("0")) "94" + cleanPhone.substring(1) else "94$cleanPhone"
                        val waIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://wa.me/$intPhone?text=${Uri.encode("Hello ${currentPageState.name}, I have an inquiry regarding your store!")}")
                        )
                        try {
                            context.startActivity(waIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Opening WhatsApp for ${currentPageState.name}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDirectionsClick = {
                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(currentPageState.address)}"))
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Locating: ${currentPageState.address}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // -----------------------------------------------------------------
            // SECTION 2: TABS SECTION (Products, News, Reviews, About)
            // -----------------------------------------------------------------
            item {
                BusinessStoreTabsRow(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // -----------------------------------------------------------------
            // SECTION 3: TAB SPECIFIC CONTENT
            // -----------------------------------------------------------------
            when (selectedTab) {
                0 -> {
                    // TAB 0: PRODUCTS & E-COMMERCE GRID
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "STORE CATALOG (${productsList.size})",
                                    color = Suite_Slate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontFamily = PlusJakartaSansFamily
                                )

                                Button(
                                    onClick = { showAddProductDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Add Product",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = PlusJakartaSansFamily
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Filter Chips Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val filterCategories = listOf("All", "Wearables", "Audio", "Accessories")
                                filterCategories.forEach { category ->
                                    val isSelected = selectedCategoryFilter == category
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) Suite_Emerald else Suite_Slate800,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) Suite_Emerald else Suite_Slate700
                                        ),
                                        modifier = Modifier.clickable { selectedCategoryFilter = category }
                                    ) {
                                        Text(
                                            text = category,
                                            color = if (isSelected) Color.White else Suite_Slate400,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontFamily = PlusJakartaSansFamily,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (filteredProducts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No products found in '$selectedCategoryFilter'.",
                                    color = Suite_Slate400,
                                    fontFamily = PlusJakartaSansFamily
                                )
                            }
                        }
                    } else {
                        items(chunkedProductPairs, key = { it.first().id }) { pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StoreProductCard(
                                    product = pair[0],
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedProductForDetail = pair[0] },
                                    onWhatsAppClick = { orderViaWhatsApp(pair[0]) }
                                )

                                if (pair.size > 1) {
                                    StoreProductCard(
                                        product = pair[1],
                                        modifier = Modifier.weight(1f),
                                        onClick = { selectedProductForDetail = pair[1] },
                                        onWhatsAppClick = { orderViaWhatsApp(pair[1]) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: NEWS FEED / BUSINESS POSTS
                    if (businessPosts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Feed, contentDescription = null, tint = Suite_Slate700, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No business updates published yet.", color = Suite_Slate400, fontFamily = PlusJakartaSansFamily)
                                }
                            }
                        }
                    } else {
                        items(businessPosts, key = { it.id }) { post ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                PostCard(post = post, viewModel = viewModel)
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: REVIEWS & TESTIMONIALS
                    item {
                        BusinessReviewsView(page = currentPageState)
                    }
                }

                3 -> {
                    // TAB 3: ABOUT & CONTACT DETAILS
                    item {
                        BusinessAboutView(page = currentPageState, onDirectionsClick = {
                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(currentPageState.address)}"))
                            try { context.startActivity(mapIntent) } catch (e: Exception) {
                                Toast.makeText(context, "Locating: ${currentPageState.address}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // DIALOG 1: INTERACTIVE PRODUCT DETAIL MODAL & WHATSAPP ORDERING
    // -----------------------------------------------------------------
    selectedProductForDetail?.let { product ->
        ProductDetailModalSheet(
            product = product,
            page = currentPageState,
            onDismiss = { selectedProductForDetail = null },
            onOrderWhatsApp = {
                orderViaWhatsApp(product)
            },
            onQuickBuy = {
                selectedProductForDetail = null
                showQuickBuyCheckout = product
            },
            onMessageStore = {
                selectedProductForDetail = null
                viewModel.openChatForBusiness(
                    currentPageState.name,
                    currentPageState.imageUrl,
                    currentPageState.phone
                )
            }
        )
    }

    // -----------------------------------------------------------------
    // DIALOG 2: MULTI-PHOTO UPLOAD SUPPORT (UP TO 10 IMAGES)
    // -----------------------------------------------------------------
    if (showAddProductDialog) {
        AddProductMultiPhotoDialog(
            onDismiss = { showAddProductDialog = false },
            onProductCreated = { newProduct ->
                productsList = listOf(newProduct) + productsList
                showAddProductDialog = false
                Toast.makeText(context, "🎉 '${newProduct.title}' published to store with ${newProduct.images.size} photo(s)!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // -----------------------------------------------------------------
    // DIALOG 3: COMPLETE BUSINESS PROFILE CUSTOMIZATION
    // -----------------------------------------------------------------
    if (showEditProfileDialog) {
        EditBusinessProfileDialog(
            initialPage = currentPageState,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                currentPageState = updated
                viewModel.updateBusinessProfile(updated)
                showEditProfileDialog = false
                Toast.makeText(context, "Business Profile customized successfully! ✅", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // -----------------------------------------------------------------
    // DIALOG 4: INSTANT CASH ON DELIVERY / ORDER CHECKOUT
    // -----------------------------------------------------------------
    showQuickBuyCheckout?.let { product ->
        QuickOrderCheckoutDialog(
            product = product,
            page = currentPageState,
            onDismiss = { showQuickBuyCheckout = null },
            onConfirmed = { name, phone, address ->
                showQuickBuyCheckout = null
                Toast.makeText(
                    context,
                    "🎉 Order Confirmed!\nItem: ${product.title}\nTotal: ${product.price}\nRecipient: $name\nDispatched to: $address via Islandwide Express.",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}

// ---------------------------------------------------------------------
// COMPONENT: Cover Photo Frame, Profile Picture Selector & Header
// ---------------------------------------------------------------------
@Composable
private fun BusinessCoverAndHeaderSection(
    page: BusinessPage,
    productCount: Int,
    onChangeCoverClick: () -> Unit,
    onChangeAvatarClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onContactPageClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onDirectionsClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. Cover Photo Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Suite_Slate800)
        ) {
            AsyncImage(
                model = if (page.coverUrl.isNotBlank()) page.coverUrl else "https://images.unsplash.com/photo-1557821552-17105176674c?w=1200&auto=format&fit=crop&q=80",
                contentDescription = "Cover Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Tint
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Suite_Slate900)
                        )
                    )
            )

            // Camera Pill for Cover Photo Customization
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Suite_Slate900.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .clickable { onChangeCoverClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Change Cover",
                        tint = Suite_Emerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Edit Cover",
                        color = Suite_Slate50,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PlusJakartaSansFamily
                    )
                }
            }
        }

        // 2. Avatar & Info Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-36).dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Avatar Frame with Camera Selector
                Box(modifier = Modifier.size(88.dp)) {
                    if (page.imageUrl.isNotBlank() && !page.imageUrl.contains("default")) {
                        AsyncImage(
                            model = page.imageUrl,
                            contentDescription = "Business Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(3.dp, Suite_Emerald, CircleShape),
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
                                text = page.name.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Avatar Change Camera Badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Suite_Slate800)
                            .border(1.5.dp, Suite_Emerald, CircleShape)
                            .clickable { onChangeAvatarClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Avatar",
                            tint = Suite_Emerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Edit Profile Pill
                OutlinedButton(
                    onClick = onEditProfileClick,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Emerald),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Suite_Emerald),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Customize Profile",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                }
            }

            // Text Info
            Column(modifier = Modifier.offset(y = (-24).dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = page.name,
                        color = Suite_Slate50,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = PlusJakartaSansFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Business",
                        tint = Suite_Emerald,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = page.category,
                    color = Suite_Cyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSansFamily
                )

                if (page.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = page.bio,
                        color = Suite_Slate400,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        fontFamily = PlusJakartaSansFamily
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Bar (Followers, Rating, Total Products)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Suite_Slate800,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BusinessStatCol(label = "Followers", value = "${page.followers / 1000}K+")
                        VerticalDivider(modifier = Modifier.height(24.dp), color = Suite_Slate700)
                        BusinessStatCol(label = "Store Rating", value = "★ ${page.rating}", valueColor = Suite_Amber)
                        VerticalDivider(modifier = Modifier.height(24.dp), color = Suite_Slate700)
                        BusinessStatCol(label = "Products", value = "$productCount Items", valueColor = Suite_Emerald)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons Row (Contact Page, Call, WhatsApp, Directions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Primary Contact Page Button
                    Button(
                        onClick = onContactPageClick,
                        modifier = Modifier
                            .weight(2.2f)
                            .height(44.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = Suite_Emerald),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Suite_Slate900, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Contact Page",
                                color = Suite_Slate900,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = PlusJakartaSansFamily
                            )
                        }
                    }

                    // Direct Call Button
                    BusinessIconButton(
                        icon = Icons.Default.Phone,
                        contentDesc = "Call Business",
                        tintColor = Suite_Slate50,
                        modifier = Modifier.weight(1f),
                        onClick = onPhoneClick
                    )

                    // WhatsApp Button with Green Logo
                    BusinessIconButton(
                        drawableRes = com.example.R.drawable.ic_whatsapp_green,
                        contentDesc = "WhatsApp Order",
                        modifier = Modifier.weight(1f),
                        onClick = onWhatsAppClick
                    )

                    // Facebook Page Button with Blue Logo
                    BusinessIconButton(
                        drawableRes = com.example.R.drawable.ic_facebook_blue,
                        contentDesc = "Facebook Page",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            Toast.makeText(context, "Opening Official Facebook Page...", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Location / Map Button
                    BusinessIconButton(
                        icon = Icons.Default.LocationOn,
                        contentDesc = "Directions",
                        tintColor = Suite_Cyan,
                        modifier = Modifier.weight(1f),
                        onClick = onDirectionsClick
                    )
                }
            }
        }
    }
}

@Composable
private fun BusinessStatCol(
    label: String,
    value: String,
    valueColor: Color = Suite_Slate50
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = PlusJakartaSansFamily
        )
        Text(
            text = label,
            color = Suite_Slate400,
            fontSize = 11.sp,
            fontFamily = PlusJakartaSansFamily
        )
    }
}

@Composable
private fun BusinessIconButton(
    icon: ImageVector? = null,
    drawableRes: Int? = null,
    contentDesc: String,
    tintColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Suite_Slate800,
        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (drawableRes != null) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = drawableRes),
                    contentDescription = contentDesc,
                    tint = tintColor,
                    modifier = Modifier.size(26.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDesc,
                    tint = tintColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// COMPONENT: Business Store Tabs Row
// ---------------------------------------------------------------------
@Composable
private fun BusinessStoreTabsRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Suite_Slate800,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Suite_Slate700)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 12.dp, horizontal = 4.dp)
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Suite_Emerald else Suite_Slate400,
                        fontSize = 13.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = PlusJakartaSansFamily
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .height(2.5.dp)
                            .width(28.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Suite_Emerald else Color.Transparent)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// COMPONENT: Clean Store Product Card (Zero-Clipping Grid Item)
// ---------------------------------------------------------------------
@Composable
private fun StoreProductCard(
    product: StoreProductItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
        modifier = modifier.clickable { onClick() }
    ) {
        Column {
            // Product Main Image with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(Suite_Slate900)
            ) {
                AsyncImage(
                    model = product.images.firstOrNull() ?: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Stock status overlay pill
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = if (product.inStock) "In Stock" else "Out of Stock",
                        color = if (product.inStock) Suite_Emerald else Color(0xFFEF4444),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Multi-photo count pill (if > 1 image)
                if (product.images.size > 1) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${product.images.size}",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Details Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Title (Up to 2 lines, no clipping)
                Text(
                    text = product.title,
                    color = Suite_Slate50,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = product.price,
                        color = Suite_Cyan,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    if (!product.originalPrice.isNullOrBlank()) {
                        Text(
                            text = product.originalPrice,
                            color = Suite_Slate400,
                            fontSize = 10.sp,
                            textDecoration = TextDecoration.LineThrough,
                            fontFamily = PlusJakartaSansFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Direct WhatsApp Order CTA Button
                Button(
                    onClick = onWhatsAppClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Suite_WhatsApp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_whatsapp_white),
                            contentDescription = "WhatsApp Order",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "WhatsApp Order",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// MODAL: Interactive Product Detail & Direct WhatsApp Ordering
// ---------------------------------------------------------------------
@Composable
private fun ProductDetailModalSheet(
    product: StoreProductItem,
    page: BusinessPage,
    onDismiss: () -> Unit,
    onOrderWhatsApp: () -> Unit,
    onQuickBuy: () -> Unit,
    onMessageStore: () -> Unit
) {
    var activeImageIndex by remember { mutableStateOf(0) }
    val displayImages = remember(product) {
        if (product.images.isNotEmpty()) product.images
        else listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&auto=format&fit=crop&q=80")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Product Details",
                        color = Suite_Slate50,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                HorizontalDivider(color = Suite_Slate700, thickness = 0.5.dp)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Full Image Carousel Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Suite_Slate900)
                    ) {
                        AsyncImage(
                            model = displayImages.getOrElse(activeImageIndex) { displayImages[0] },
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Photo Index Indicator
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Photo ${activeImageIndex + 1} of ${displayImages.size}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // 2. Horizontal Thumbnail Preview Row (Up to 10 photos)
                    if (displayImages.size > 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            displayImages.forEachIndexed { index, imgUrl ->
                                val isSelected = activeImageIndex == index
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            2.dp,
                                            if (isSelected) Suite_Emerald else Suite_Slate700,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { activeImageIndex = index }
                                ) {
                                    AsyncImage(
                                        model = imgUrl,
                                        contentDescription = "Thumbnail $index",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Category & Stock Status Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Suite_Emerald.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Emerald)
                        ) {
                            Text(
                                text = if (product.inStock) "🟢 In Stock (${product.stockQuantity} available)" else "🔴 Out of Stock",
                                color = Suite_Emerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSansFamily,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("★ ${product.rating}", color = Suite_Amber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("(${product.reviewCount} reviews)", color = Suite_Slate400, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Product Title
                    Text(
                        text = product.title,
                        color = Suite_Slate50,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = PlusJakartaSansFamily
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 5. Price Display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = product.price,
                            color = Suite_Cyan,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = PlusJakartaSansFamily
                        )
                        if (!product.originalPrice.isNullOrBlank()) {
                            Text(
                                text = product.originalPrice,
                                color = Suite_Slate400,
                                fontSize = 14.sp,
                                textDecoration = TextDecoration.LineThrough,
                                fontFamily = PlusJakartaSansFamily
                            )
                        }
                        if (!product.discountPercent.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Suite_Emerald
                            ) {
                                Text(
                                    text = product.discountPercent,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Guarantee & Logistics Badges
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Suite_Slate900, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Suite_Emerald, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(product.warranty, color = Suite_Slate50, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Suite_Cyan, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(product.deliveryInfo, color = Suite_Slate50, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = Suite_Amber, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cash on Delivery & Bank Transfer Supported", color = Suite_Slate50, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7. Item Description
                    Text(
                        text = "Item Description",
                        color = Suite_Slate50,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.description,
                        color = Suite_Slate400,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontFamily = PlusJakartaSansFamily
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 8. Sticky Action Buttons at Bottom
                Surface(
                    color = Suite_Slate900,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Suite_Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Direct WhatsApp Ordering Button
                        Button(
                            onClick = onOrderWhatsApp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Suite_WhatsApp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_whatsapp_white),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Chat / Order on WhatsApp",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PlusJakartaSansFamily
                                )
                            }
                        }

                        // Secondary Row: Quick Buy (COD) & In-App Chat
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onQuickBuy,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald)
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Suite_Slate900, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Buy Now",
                                    color = Suite_Slate900,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = PlusJakartaSansFamily
                                )
                            }

                            OutlinedButton(
                                onClick = onMessageStore,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0084FF).copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0084FF))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_3d_chat_bubble_vector),
                                    contentDescription = "Messenger",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Messenger",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PlusJakartaSansFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// MODAL: Multi-Photo Upload Flow (Up to 10 Images Supported)
// ---------------------------------------------------------------------
@Composable
private fun AddProductMultiPhotoDialog(
    onDismiss: () -> Unit,
    onProductCreated: (StoreProductItem) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var originalPriceText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }
    var stockQuantityText by remember { mutableStateOf("15") }

    // Multi-photo list (up to 10 photos)
    var selectedPhotos by remember {
        mutableStateOf<List<String>>(
            listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&auto=format&fit=crop&q=80")
        )
    }

    // PickMultipleVisualMedia launcher (maxItems = 10)
    val multiImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val stringUris = uris.map { it.toString() }
            val combined = (selectedPhotos + stringUris).distinct().take(10)
            selectedPhotos = combined
            Toast.makeText(context, "${uris.size} photo(s) selected from gallery!", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Product (Up to 10 Photos)",
                        color = Suite_Slate50,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                HorizontalDivider(color = Suite_Slate700, thickness = 0.5.dp)

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Multi-Photo Upload Frame & Preview Carousel
                    Text(
                        text = "Product Photos (${selectedPhotos.size} / 10)",
                        color = Suite_Emerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display current selected photos with remove badges
                        selectedPhotos.forEachIndexed { index, photoUrl ->
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Suite_Emerald, RoundedCornerShape(10.dp))
                            ) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Photo $index",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Remove badge (X)
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.TopEnd)
                                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                        .clickable {
                                            selectedPhotos = selectedPhotos.filterIndexed { i, _ -> i != index }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }

                        // "Add More Photos" Box if < 10 photos
                        if (selectedPhotos.size < 10) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Suite_Slate900,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Cyan),
                                modifier = Modifier
                                    .size(76.dp)
                                    .clickable {
                                        multiImagePicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Suite_Cyan, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+ Add",
                                        color = Suite_Cyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 1: Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Product Title *") },
                        placeholder = { Text("e.g. Wireless Noise-Cancelling Earbuds") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Field 2: Price & Original Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price (Rs.) *") },
                            placeholder = { Text("18500") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = businessTextFieldColors()
                        )

                        OutlinedTextField(
                            value = originalPriceText,
                            onValueChange = { originalPriceText = it },
                            label = { Text("Original Price (Rs.)") },
                            placeholder = { Text("25000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = businessTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Field 3: Category Selector Chips
                    Text("Category", color = Suite_Slate400, fontSize = 12.sp, fontFamily = PlusJakartaSansFamily)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Electronics", "Wearables", "Audio", "Accessories", "Fashion").forEach { cat ->
                            val isSelected = category == cat
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) Suite_Emerald else Suite_Slate900,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Suite_Emerald else Suite_Slate700),
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.White else Suite_Slate400,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Field 4: Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Specifications *") },
                        placeholder = { Text("Key features, specifications, and warranty info...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4,
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Field 5: Stock Quantity
                    OutlinedTextField(
                        value = stockQuantityText,
                        onValueChange = { stockQuantityText = it },
                        label = { Text("Initial Stock Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Submit Button
                Surface(
                    color = Suite_Slate900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (title.isBlank() || priceText.isBlank()) {
                                Toast.makeText(context, "Please enter product title and price!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val cleanPrice = if (priceText.startsWith("Rs.")) priceText else "Rs. $priceText"
                            val cleanOrig = if (originalPriceText.isNotBlank()) {
                                if (originalPriceText.startsWith("Rs.")) originalPriceText else "Rs. $originalPriceText"
                            } else null

                            val newProd = StoreProductItem(
                                id = "prod_${System.currentTimeMillis()}",
                                title = title,
                                price = cleanPrice,
                                originalPrice = cleanOrig,
                                discountPercent = if (cleanOrig != null) "SPECIAL OFFER" else null,
                                description = description.ifBlank { "High quality verified store item backed by islandwide express delivery." },
                                images = if (selectedPhotos.isNotEmpty()) selectedPhotos else listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&auto=format&fit=crop&q=80"),
                                inStock = true,
                                stockQuantity = stockQuantityText.toIntOrNull() ?: 10,
                                category = category
                            )
                            onProductCreated(newProd)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald)
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, tint = Suite_Slate900, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Publish Product to Store",
                            color = Suite_Slate900,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFamily
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// MODAL: Complete Business Profile Customization
// ---------------------------------------------------------------------
@Composable
private fun EditBusinessProfileDialog(
    initialPage: BusinessPage,
    onDismiss: () -> Unit,
    onSave: (BusinessPage) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialPage.name) }
    var category by remember { mutableStateOf(initialPage.category) }
    var phone by remember { mutableStateOf(initialPage.phone) }
    var address by remember { mutableStateOf(initialPage.address) }
    var bio by remember { mutableStateOf(initialPage.bio) }
    var website by remember { mutableStateOf(initialPage.website) }
    var hours by remember { mutableStateOf(initialPage.hours) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customize Business Profile",
                        color = Suite_Slate50,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                HorizontalDivider(color = Suite_Slate700, thickness = 0.5.dp)

                // Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset Quick Rename Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Suite_Slate900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Cyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                name = "Wasantha Kumara Official Store"
                                category = "E-Commerce & Retail • Official Store"
                                Toast.makeText(context, "Set to 'Wasantha Kumara Official Store'", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Suite_Cyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Use: 'Wasantha Kumara Official Store'",
                                color = Suite_Cyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Business Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Business Page Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category *") },
                        placeholder = { Text("E-Commerce & Retail") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contact Phone / WhatsApp
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone / WhatsApp Contact *") },
                        placeholder = { Text("0771234567") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Address / Location
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Store Address / Location *") },
                        placeholder = { Text("Pelmadulla, Rathnapura, Sri Lanka") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bio / Tagline
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Business Bio / Tagline") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Website & Hours
                    OutlinedTextField(
                        value = website,
                        onValueChange = { website = it },
                        label = { Text("Website") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Business Hours") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = businessTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Save Button
                Surface(
                    color = Suite_Slate900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "Business name cannot be empty!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val updated = initialPage.copy(
                                name = name,
                                category = category,
                                phone = phone,
                                address = address,
                                bio = bio,
                                website = website,
                                hours = hours
                            )
                            onSave(updated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald)
                    ) {
                        Text(
                            text = "Save Profile Customization",
                            color = Suite_Slate900,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFamily
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// MODAL: Instant Cash on Delivery / Order Checkout
// ---------------------------------------------------------------------
@Composable
private fun QuickOrderCheckoutDialog(
    product: StoreProductItem,
    page: BusinessPage,
    onDismiss: () -> Unit,
    onConfirmed: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant Order Checkout",
                        color = Suite_Slate50,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Suite_Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Order summary pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Suite_Slate900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.images.firstOrNull() ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, color = Suite_Slate50, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(product.price, color = Suite_Cyan, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Your Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = businessTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Contact Phone *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = businessTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Islandwide Delivery Address *") },
                    placeholder = { Text("Street, City, Postal Code") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = businessTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Payment Method", color = Suite_Slate400, fontSize = 11.sp, fontFamily = PlusJakartaSansFamily)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Cash on Delivery", "Bank Transfer").forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Suite_Emerald else Suite_Slate900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Suite_Emerald else Suite_Slate700),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPaymentMethod = method }
                        ) {
                            Text(
                                text = method,
                                color = if (isSelected) Color.White else Suite_Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (customerName.isBlank() || customerPhone.isBlank() || deliveryAddress.isBlank()) {
                            Toast.makeText(context, "Please fill in all order details!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onConfirmed(customerName, customerPhone, deliveryAddress)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Suite_Emerald)
                ) {
                    Text(
                        text = "Confirm & Dispatch Order",
                        color = Suite_Slate900,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = PlusJakartaSansFamily
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// SECTION: Reviews View
// ---------------------------------------------------------------------
@Composable
private fun BusinessReviewsView(page: BusinessPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${page.rating}",
                color = Suite_Slate50,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = PlusJakartaSansFamily
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Row {
                    repeat(5) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Suite_Amber, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Based on ${page.reviewCount} customer reviews", color = Suite_Slate400, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val customerReviews = listOf(
            Triple("Niluka Perera", "5", "Ordered the AMOLED Smart Watch on Wednesday, received it by Thursday noon in Galle. 1-year warranty card included. Awesome service!"),
            Triple("Kamal Fernando", "5", "Genuine products and very fast WhatsApp support. 100% recommended for electronics in Sri Lanka."),
            Triple("Sarah Jayawardena", "5", "Best place for MagSafe chargers and gadgets. Islandwide delivery is super prompt.")
        )

        customerReviews.forEach { (reviewer, rating, comment) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Suite_Slate800),
                border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Suite_Emerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(reviewer.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(reviewer, color = Suite_Slate50, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row {
                                    repeat(rating.toInt()) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Suite_Amber, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Suite_Emerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Verified Buyer",
                                color = Suite_Emerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(comment, color = Suite_Slate400, fontSize = 12.5.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// SECTION: About & Location View
// ---------------------------------------------------------------------
@Composable
private fun BusinessAboutView(page: BusinessPage, onDirectionsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AboutDetailRow(icon = Icons.Default.Info, label = "About Business", value = page.bio)
        AboutDetailRow(icon = Icons.Default.LocationOn, label = "Official Address", value = page.address)
        AboutDetailRow(icon = Icons.Default.Phone, label = "Phone / WhatsApp", value = page.phone)
        AboutDetailRow(icon = Icons.Default.Schedule, label = "Business Hours", value = page.hours)
        AboutDetailRow(icon = Icons.Default.Language, label = "Website", value = page.website)

        Spacer(modifier = Modifier.height(4.dp))

        // Interactive Map Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Suite_Slate800,
            border = androidx.compose.foundation.BorderStroke(1.dp, Suite_Slate700),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clickable { onDirectionsClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = Suite_Cyan, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap to open Google Maps navigation",
                        color = Suite_Slate50,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = page.address,
                        color = Suite_Slate400,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutDetailRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = Suite_Emerald, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = Suite_Slate400, fontSize = 11.sp, fontFamily = PlusJakartaSansFamily)
            Text(value, color = Suite_Slate50, fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = PlusJakartaSansFamily)
        }
    }
}

// ---------------------------------------------------------------------
// HELPER: Text Field Colors for Dark Theme
// ---------------------------------------------------------------------
@Composable
private fun businessTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Suite_Slate50,
    unfocusedTextColor = Suite_Slate50,
    focusedContainerColor = Suite_Slate900,
    unfocusedContainerColor = Suite_Slate900,
    cursorColor = Suite_Emerald,
    focusedBorderColor = Suite_Emerald,
    unfocusedBorderColor = Suite_Slate700,
    focusedLabelColor = Suite_Emerald,
    unfocusedLabelColor = Suite_Slate400,
    focusedPlaceholderColor = Suite_Slate400,
    unfocusedPlaceholderColor = Suite_Slate400
)
