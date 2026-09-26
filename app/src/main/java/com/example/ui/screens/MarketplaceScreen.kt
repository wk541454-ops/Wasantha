package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.widget.Toast
import coil.compose.AsyncImage
import com.example.model.MarketplaceItem
import com.example.ui.components.MarketplaceLocationMapBanner
import com.example.ui.components.MarketplacePhotoGalleryModal
import com.example.ui.components.SriLankaMarketplaceMapModal
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import com.example.viewmodel.MainViewModel

@Composable
fun MarketplaceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.marketplaceItems.collectAsState()
    val category by viewModel.marketplaceCategory.collectAsState()
    val isCreateMarketplaceOpen by viewModel.isCreateMarketplaceOpen.collectAsState()

    var selectedItemForDetail by remember { mutableStateOf<MarketplaceItem?>(null) }
    var mapItemToShow by remember { mutableStateOf<MarketplaceItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("ALL", "Electronics", "Furniture", "Fashion", "Vehicles")

    val filteredItems = items.filter { item ->
        (category == "ALL" || item.category.equals(category, ignoreCase = true)) &&
        (searchQuery.isBlank() || item.title.contains(searchQuery, ignoreCase = true))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.selectTab(0) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Reverse to Home Feed",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Marketplace",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Storefront, contentDescription = "Store", tint = NeonPurple)
                }

                Button(
                    onClick = { viewModel.setCreateMarketplaceOpen(true) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2), contentColor = Color.White),
                    modifier = Modifier.testTag("sell_item_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Sell Item", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("විකුණන්න", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("භාණ්ඩ සොයන්න...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("marketplace_search_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonBlue,
                    unfocusedBorderColor = OledCardBorder,
                    focusedContainerColor = OledSurfaceVariant,
                    unfocusedContainerColor = OledSurfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = category == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) NeonBlue.copy(alpha = 0.2f) else OledSurfaceVariant)
                            .border(1.dp, if (isSelected) NeonBlue else OledCardBorder, RoundedCornerShape(16.dp))
                            .clickable { viewModel.setMarketplaceCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (cat == "ALL") "All Products" else cat,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) NeonBlue else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { item ->
                    MarketplaceItemCard(
                        item = item,
                        onClick = { selectedItemForDetail = item },
                        onViewMap = { mapItemToShow = item }
                    )
                }
            }
        }

        // Detail Modal
        selectedItemForDetail?.let { item ->
            MarketplaceDetailDialog(
                item = item,
                viewModel = viewModel,
                onDismiss = { selectedItemForDetail = null },
                onContactSeller = {
                    selectedItemForDetail = null
                    viewModel.selectTab(2) // Navigate to Messages (Chats)
                },
                onViewMap = { mapItemToShow = item }
            )
        }

        // Map View Modal for Item Location
        mapItemToShow?.let { item ->
            SriLankaMarketplaceMapModal(
                initialLocationName = item.location,
                isSelectMode = false,
                itemTitle = item.title,
                itemPrice = item.price,
                itemImageUrl = item.imageUrl,
                sellerName = item.sellerName,
                onDismiss = { mapItemToShow = null },
                onSelectLocation = {}
            )
        }

        // Create Item Dialog
        if (isCreateMarketplaceOpen) {
            CreateMarketplaceListingScreen(
                onDismiss = { viewModel.setCreateMarketplaceOpen(false) },
                onSubmit = { title, price, cat, desc, imgUrl ->
                    viewModel.createMarketplaceListing(title, price, cat, desc, imgUrl)
                }
            )
        }
    }
}

@Composable
fun MarketplaceItemCard(
    item: MarketplaceItem,
    onClick: () -> Unit,
    onViewMap: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp, brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(OledCardBorder, OledCardBorder)))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = NeonBlue
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = NeonPurple, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.location,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1877F2).copy(alpha = 0.2f))
                            .clickable { onViewMap() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "📍 සිතියම",
                            color = Color(0xFF60A5FA),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceDetailDialog(
    item: MarketplaceItem,
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onContactSeller: () -> Unit,
    onViewMap: () -> Unit = {}
) {
    val aiPriceExpertEnabled by viewModel.aiPriceExpertEnabled.collectAsState()
    val context = LocalContext.current
    var isFollowingSeller by remember { mutableStateOf(false) }
    var joinedGroup1 by remember { mutableStateOf(false) }
    var joinedGroup2 by remember { mutableStateOf(false) }
    var joinedGroup3 by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF18191A)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Top Bar matching Screenshot (< Title 🔍)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF242526))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = {
                            Toast.makeText(context, "සොයන්න...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF3A3B3C), thickness = 0.5.dp)

                // 2. Scrollable Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Item Main Image
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Price & Title Section
                    item {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                            Text(
                                text = "Rs. ${String.format("%,.0f", item.price * 300)} (USD $${String.format("%.2f", item.price)})",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 19.sp
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color(0xFFE4E6EB)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Contact info line from screenshot
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF242526))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📲 WhatsApp: 0729267161",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    ),
                                    color = Color(0xFF4ADE80)
                                )
                            }

                            if (aiPriceExpertEnabled) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonBlue.copy(alpha = 0.1f))
                                        .border(1.dp, NeonBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "🤖 AI Fair Price Evaluation",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = NeonBlue
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "This product is listed at a FAIR market value in ${item.location}.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                ),
                                color = Color(0xFFB0B3B8)
                            )
                        }
                    }

                    item {
                        HorizontalDivider(color = Color(0xFF3A3B3C), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                    }

                    // 3. විකුණුම්කරු තොරතුරු (Seller Information - Exactly like screenshot)
                    item {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "විකුණුම්කරු තොරතුරු",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color.White
                                )

                                Text(
                                    text = "විකුණුම්කරුගේ විස්තර",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF58A6FF),
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.clickable {
                                        Toast.makeText(context, "${item.sellerName} ගේ සම්පූර්ණ විස්තර", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Seller Profile Row with Follow Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.sellerAvatar,
                                    contentDescription = item.sellerName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, Color(0xFF3A3B3C), CircleShape)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.sellerName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "2022 දී FriendHub වෙත එක් වෙන ලදී",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp
                                        ),
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                // "+ හඹා යන්න" Button (Follow Seller)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isFollowingSeller) Color(0xFF2563EB) else Color(0xFF3A3B3C))
                                        .clickable {
                                            isFollowingSeller = !isFollowingSeller
                                            val msg = if (isFollowingSeller) "${item.sellerName} හඹා යාම ආරම්භ විය" else "හඹා යාම අවසන් විය"
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 7.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (!isFollowingSeller) {
                                            Text("➕", fontSize = 11.sp, color = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = if (isFollowingSeller) "✓ හඹා යමින්" else "හඹා යන්න",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Map Location Banner (EXACTLY AS IN SCREENSHOT)
                    item {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                            MarketplaceLocationMapBanner(
                                locationName = item.location,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onViewMap
                            )
                        }
                    }

                    item {
                        HorizontalDivider(color = Color(0xFF3A3B3C), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                    }

                    // 5. යෝජිත ගැනුම් සහ විකුණුම් සමූහ (Suggested Buy and Sell Groups - Exactly from screenshot)
                    item {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                            Text(
                                text = "යෝජිත ගැනුම් සහ විකුණුම් සමූහ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Group 1: ගාල්ල /ඉමදුව /හබරාදුව
                            SuggestedGroupRow(
                                title = "ගාල්ල /ඉමදුව /හබරාදුව",
                                members = "සාමාජිකයින් 71,575 දෙනෙකි",
                                imageUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=150&q=80",
                                isJoined = joinedGroup1,
                                onToggleJoin = {
                                    joinedGroup1 = !joinedGroup1
                                    Toast.makeText(context, if (joinedGroup1) "සමූහයට සම්බන්ධ විය" else "සමූහයෙන් ඉවත් විය", Toast.LENGTH_SHORT).show()
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Group 2: Hakmana Api | හක්මන අපි
                            SuggestedGroupRow(
                                title = "Hakmana Api | හක්මන අපි",
                                members = "සාමාජිකයින් 33,214 දෙනෙකි",
                                imageUrl = "https://images.unsplash.com/photo-1596178065887-1198b6148b2b?w=150&q=80",
                                isJoined = joinedGroup2,
                                onToggleJoin = {
                                    joinedGroup2 = !joinedGroup2
                                    Toast.makeText(context, if (joinedGroup2) "සමූහයට සම්බන්ධ විය" else "සමූහයෙන් ඉවත් විය", Toast.LENGTH_SHORT).show()
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Group 3: හතර වටේ බිස්නස් #අකුරැස්ස, #පිටබැද්දර
                            SuggestedGroupRow(
                                title = "හතර වටේ බිස්නස් #අකුරැස්ස, #පිටබැද්දර",
                                members = "සාමාජිකයින් 54,290 දෙනෙකි",
                                imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=150&q=80",
                                isJoined = joinedGroup3,
                                onToggleJoin = {
                                    joinedGroup3 = !joinedGroup3
                                    Toast.makeText(context, if (joinedGroup3) "සමූහයට සම්බන්ධ විය" else "සමූහයෙන් ඉවත් විය", Toast.LENGTH_SHORT).show()
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // 6. Bottom Sticky Contact / Message Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF242526))
                        .padding(12.dp)
                ) {
                    Button(
                        onClick = onContactSeller,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                    ) {
                        Text(
                            text = "විකුණුම්කරුට පණිවිඩයක් යවන්න (Message Seller)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestedGroupRow(
    title: String,
    members: String,
    imageUrl: String,
    isJoined: Boolean,
    onToggleJoin: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFF3A3B3C), RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = members,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedButton(
            onClick = onToggleJoin,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isJoined) Color(0xFF2563EB) else Color.Transparent,
                contentColor = if (isJoined) Color.White else Color(0xFF58A6FF)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isJoined) Color(0xFF2563EB) else Color(0xFF58A6FF)),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isJoined) "සාමාජිකයෙකි" else "එක් වන්න",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==================== FB LITE CREATE LISTING SCREEN & CATEGORY PICKER ====================

data class MarketplaceCategoryGroup(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val subcategories: List<String>
)

val FB_LITE_MARKETPLACE_CATEGORIES = listOf(
    MarketplaceCategoryGroup(
        title = "රථවාහන",
        icon = Icons.Default.DirectionsCar,
        iconColor = Color(0xFFAB47BC), // Purple
        subcategories = listOf("රථවාහන", "මෝටර් රථ අමතර කොටස්", "පා පැදි")
    ),
    MarketplaceCategoryGroup(
        title = "නිවාස",
        icon = Icons.Default.HomeWork,
        iconColor = Color(0xFFFF7043), // Orange
        subcategories = listOf("නිවාස විකුණුම්", "කුලියට දීමට ඇති දේපළ")
    ),
    MarketplaceCategoryGroup(
        title = "නිවාස සහ ගෙවත්ත",
        icon = Icons.Default.Home,
        iconColor = Color(0xFF26C6DA), // Cyan
        subcategories = listOf("ගෘහ භාණ්ඩ", "ගෘහස්ථ භාණ්ඩ", "උපකරණ", "මෙවලම්", "ගෙවතු")
    ),
    MarketplaceCategoryGroup(
        title = "ඉලෙක්ට්‍රොනික උපකරණ",
        icon = Icons.Default.Smartphone,
        iconColor = Color(0xFF26A69A), // Teal
        subcategories = listOf("ඉලෙක්ට්‍රොනික සහ පරිගණක", "ජංගම දුරකථන")
    ),
    MarketplaceCategoryGroup(
        title = "වර්ග කල",
        icon = Icons.Default.Style,
        iconColor = Color(0xFFEC407A), // Pink
        subcategories = listOf("ගරාජ් අලෙවිය", "විවිධ")
    ),
    MarketplaceCategoryGroup(
        title = "විනෝදාංශ",
        icon = Icons.Default.MusicNote,
        iconColor = Color(0xFFF06292), // Light Pink
        subcategories = listOf("ක්‍රීඩා සහ එළිමහන් ක්‍රියාකාරකම්", "පෞරාණික හා වටිනා භාණ්ඩ", "සංගීත භාණ්ඩ", "කලා ශිල්ප")
    ),
    MarketplaceCategoryGroup(
        title = "ඇඳුම් සහ ආයිත්තම්",
        icon = Icons.Default.ShoppingBag,
        iconColor = Color(0xFFFFCA28), // Yellow/Gold
        subcategories = listOf("කාන්තා ඇඳුම් පැළඳුම් සහ පාවහන්", "පිරිමි ඇඳුම් පැළඳුම් සහ පාවහන්", "ආභරණ සහ ආයිත්තම්", "බෑග් සහ ගමන් මල්ල")
    ),
    MarketplaceCategoryGroup(
        title = "පවුල",
        icon = Icons.Default.VolunteerActivism,
        iconColor = Color(0xFFFF5252), // Red/Coral
        subcategories = listOf("ළදරු හා ළමා", "සෞඛ්‍ය සහ රූපලාවන්‍යය", "සෙල්ලම් බඩු හා ක්‍රීඩා", "සුරතල් සතුන් සඳහා සැපයුම්")
    ),
    MarketplaceCategoryGroup(
        title = "විනෝදාස්වාද",
        icon = Icons.Default.SportsEsports,
        iconColor = Color(0xFF66BB6A), // Green
        subcategories = listOf("වීඩියෝ ක්‍රීඩා", "පොත්, චිත්‍රපට සහ සංගීතය")
    )
)

@Composable
fun CreateMarketplaceListingScreen(
    onDismiss: () -> Unit,
    onSubmit: (title: String, price: Double, category: String, desc: String, imgUrl: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("") }
    var isCategoryModalOpen by remember { mutableStateOf(false) }
    var isMapModalOpen by remember { mutableStateOf(false) }
    var isPhotoModalOpen by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("0") }
    var location by remember { mutableStateOf("Pelmadulla") }
    var description by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("තනි අයිතමයක් ලෙස ලැයිස්තුගත කරන්න") }

    var showHelpBanner by remember { mutableStateOf(true) }
    var offerDelivery by remember { mutableStateOf(false) }
    var hideFromFriends by remember { mutableStateOf(false) }
    var enableComments by remember { mutableStateOf(true) }

    var selectedImageUrl by remember {
        mutableStateOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80")
    }

    val darkBackground = Color(0xFF18191A)
    val cardBackground = Color(0xFF242526)
    val textPrimary = Color(0xFFE4E6EB)
    val textSecondary = Color(0xFFB0B3B8)
    val fbBlue = Color(0xFF1877F2)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = darkBackground
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Top Bar: [< නව ලැ...]  [ප්‍රකාශයට පත් කරන්න]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackground)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "නව ලැ...",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            ),
                            color = textPrimary
                        )
                    }

                    TextButton(
                        onClick = {
                            val p = priceStr.toDoubleOrNull() ?: 0.0
                            val finalCat = if (selectedCategory.isBlank()) "විවිධ" else selectedCategory
                            val finalTitle = if (title.isBlank()) "නව අයිතමය" else title
                            onSubmit(finalTitle, p, finalCat, description, selectedImageUrl)
                        }
                    ) {
                        Text(
                            text = "ප්‍රකාශයට පත් කරන්න",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = fbBlue
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.5.dp)

                // Main Form Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // 1. ප්‍රවර්ගය (Category Dropdown Selector)
                    item {
                        Text(
                            text = "ප්‍රවර්ගය",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(cardBackground)
                                .border(1.dp, Color(0xFF4E4F50), RoundedCornerShape(4.dp))
                                .clickable { isCategoryModalOpen = true }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (selectedCategory.isBlank()) "තෝරන්න" else selectedCategory,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                                color = if (selectedCategory.isBlank()) textSecondary else textPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select",
                                tint = textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 2. ඔබ විකුණන්නේ මොනවාද? (What are you selling?)
                    item {
                        Text(
                            text = "ඔබ විකුණන්නේ මොනවාද?",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("", color = textSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedContainerColor = cardBackground,
                                unfocusedContainerColor = cardBackground,
                                focusedBorderColor = fbBlue,
                                unfocusedBorderColor = Color(0xFF4E4F50)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 3. මිල ($) (Price)
                    item {
                        Text(
                            text = "මිල ($)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedContainerColor = cardBackground,
                                unfocusedContainerColor = cardBackground,
                                focusedBorderColor = fbBlue,
                                unfocusedBorderColor = Color(0xFF4E4F50)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 4. පිහිටීම (Location with Map Picker)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "පිහිටීම",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = textPrimary
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(fbBlue.copy(alpha = 0.15f))
                                    .clickable { isMapModalOpen = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Select Map",
                                    tint = fbBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "📍 සිතියමෙන් තෝරන්න",
                                    color = fbBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedContainerColor = cardBackground,
                                unfocusedContainerColor = cardBackground,
                                focusedBorderColor = fbBlue,
                                unfocusedBorderColor = Color(0xFF4E4F50)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketplaceLocationMapBanner(
                            locationName = location,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { isMapModalOpen = true }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 5. විස්තරය (Description)
                    item {
                        Text(
                            text = "විස්තරය",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedContainerColor = cardBackground,
                                unfocusedContainerColor = cardBackground,
                                focusedBorderColor = fbBlue,
                                unfocusedBorderColor = Color(0xFF4E4F50)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 6. සිටින බව (Condition / Availability)
                    item {
                        Text(
                            text = "සිටින බව",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(cardBackground)
                                .border(1.dp, Color(0xFF4E4F50), RoundedCornerShape(4.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = condition,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                                color = textPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 7. Blue Helper Banner
                    if (showHelpBanner) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(fbBlue)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ගැණුම්කරුවන්ට ඔබව සම්බන්ධ කර ගැනීමට පෙර ලබා ගැනීමේ හැකියාව අවබෝධ කර ගැනීමට උදවු කිරීම සඳහා 'තනි අයිතමයක් ලෙස ලැයිස්තුගත කරන්න' තෝරන්න.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { showHelpBanner = false }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // 8. ප්‍රවාහනය කිරීමේ දීමනාව (Delivery Offer Toggle)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ප්‍රවාහනය කිරීමේ දීමනාව",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                            )
                            Switch(
                                checked = offerDelivery,
                                onCheckedChange = { offerDelivery = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = fbBlue,
                                    uncheckedThumbColor = textSecondary,
                                    uncheckedTrackColor = cardBackground
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 9. මිතුරන්ගෙන් සඟවන්න (Hide from friends)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "මිතුරන්ගෙන් සඟවන්න",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 15.sp,
                                        color = textPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "මෙම ලැයිස්තුගත කිරීම තවමත් සියල්ලන්ට විවෘත වේ. ඔබ මෙම ලැයිස්තුගත කිරීම මිතුරන්ගෙන් සඟවන්නේ නම්, ඔවුන් එය බොහෝ අවස්ථාවල දී නොදකිනු ඇත.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = textSecondary
                                    )
                                )
                                Text(
                                    text = "වැඩිදුර දැන ගන්න",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = fbBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = hideFromFriends,
                                onCheckedChange = { hideFromFriends = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = fbBlue,
                                    uncheckedThumbColor = textSecondary,
                                    uncheckedTrackColor = cardBackground
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 10. ලැයිස්තුගත කිරීම පිළිබඳව අදහස් දැක්වීම ක්‍රියාත්මක කරන්න
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ලැයිස්තුගත කිරීම පිළිබඳව අදහස් දැක්වීම ක්‍රියාත්මක කරන්න",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    color = textPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = enableComments,
                                onCheckedChange = { enableComments = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = fbBlue,
                                    uncheckedThumbColor = textSecondary,
                                    uncheckedTrackColor = cardBackground
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 11. [ 📷 ඡායාරූප එක් කරන්න ] (Add Photos Button with Gallery Modal)
                    item {
                        OutlinedButton(
                            onClick = { isPhotoModalOpen = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = cardBackground,
                                contentColor = textPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4E4F50))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                tint = textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📷 ඡායාරූප එක් කරන්න / Gallery",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            )
                        }

                        // Selected photo preview
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF393A3B), RoundedCornerShape(8.dp))
                                .clickable { isPhotoModalOpen = true }
                        ) {
                            AsyncImage(
                                model = selectedImageUrl,
                                contentDescription = "Selected Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ඡායාරූපය තෝරාගෙන ඇත ✓ (වෙනස් කිරීමට තට්ටු කරන්න)",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 12. Policy Disclosure Note
                    item {
                        Text(
                            text = "අපගේ වාණිජ ප්‍රතිපත්ති අනුගමනය කරන බව සහතික කිරීම අන් අයට ඒවා දෘශ්‍යමාන වීමට පෙර සියලු ම ලැයිස්තුගත කිරීම් ප්‍රකාශයටපත් කරන විට ක්ෂණික ප්‍රමිති සමාලෝචනයකට භාජනය වේ. සතුන්, මත්ද්‍රව්‍ය, ආයුධ, ව්‍යාජ නෝට්ටු සහ තවත් දේ වැනි අයිතමවලට ඉඩ නොදේ.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = textSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Bottom Fixed Primary Button: [ප්‍රකාශයට පත් කරන්න]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackground)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val p = priceStr.toDoubleOrNull() ?: 0.0
                            val finalCat = if (selectedCategory.isBlank()) "විවිධ" else selectedCategory
                            val finalTitle = if (title.isBlank()) "නව අයිතමය" else title
                            onSubmit(finalTitle, p, finalCat, description, selectedImageUrl)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = fbBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "ප්‍රකාශයට පත් කරන්න",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            // Category Selection Modal
            if (isCategoryModalOpen) {
                CategorySelectionModal(
                    onDismiss = { isCategoryModalOpen = false },
                    onSelectCategory = { cat ->
                        selectedCategory = cat
                        isCategoryModalOpen = false
                    }
                )
            }

            // Map Location Picker Modal
            if (isMapModalOpen) {
                SriLankaMarketplaceMapModal(
                    initialLocationName = location,
                    isSelectMode = true,
                    onDismiss = { isMapModalOpen = false },
                    onSelectLocation = { selectedLoc ->
                        location = selectedLoc
                        isMapModalOpen = false
                    }
                )
            }

            // Photo Gallery Modal
            if (isPhotoModalOpen) {
                MarketplacePhotoGalleryModal(
                    currentImageUrl = selectedImageUrl,
                    onDismiss = { isPhotoModalOpen = false },
                    onSelectImage = { newImageUrl ->
                        selectedImageUrl = newImageUrl
                        isPhotoModalOpen = false
                    }
                )
            }
        }
    }
}

@Composable
fun CategorySelectionModal(
    onDismiss: () -> Unit,
    onSelectCategory: (String) -> Unit
) {
    val darkSurface = Color(0xFF242526)
    val textPrimary = Color(0xFFE4E6EB)
    val dividerColor = Color(0xFF393A3B)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            color = darkSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Header: [ප්‍රවර්ගය වෙනස් කරන්න]  [X]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ප්‍රවර්ගය වෙනස් කරන්න",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = textPrimary
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                HorizontalDivider(color = dividerColor, thickness = 0.5.dp)

                // Category list matching screenshots
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    FB_LITE_MARKETPLACE_CATEGORIES.forEach { group ->
                        // Category Header Item with Color Icon
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = group.icon,
                                    contentDescription = group.title,
                                    tint = group.iconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = group.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = textPrimary
                                )
                            }
                        }

                        // Subcategories
                        items(group.subcategories) { subcat ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectCategory(subcat) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 50.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = subcat,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                                        color = textPrimary
                                    )
                                }
                                HorizontalDivider(
                                    color = dividerColor,
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(start = 50.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

