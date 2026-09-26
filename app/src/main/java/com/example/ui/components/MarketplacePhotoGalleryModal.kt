package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

data class CatalogProductPhoto(
    val title: String,
    val category: String,
    val imageUrl: String
)

data class CatalogPhotoCategory(
    val title: String,
    val icon: ImageVector,
    val photos: List<CatalogProductPhoto>
)

val REAL_MARKETPLACE_CATALOG = listOf(
    CatalogPhotoCategory(
        title = "ජංගම දුරකථන සහ පරිගණක",
        icon = Icons.Default.Smartphone,
        photos = listOf(
            CatalogProductPhoto("iPhone 15 Pro Max 256GB", "Smartphones", "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Samsung Galaxy S24 Ultra", "Smartphones", "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("MacBook Pro M3 Max", "Laptops", "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Sony WH-1000XM5 Headset", "Audio", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("iPad Air 5th Gen M1", "Tablets", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Custom Gaming PC Desktop", "Computers", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=800&auto=format&fit=crop&q=80")
        )
    ),
    CatalogPhotoCategory(
        title = "රථවාහන සහ යතුරුපැදි",
        icon = Icons.Default.DirectionsCar,
        photos = listOf(
            CatalogProductPhoto("Toyota Prius Hybrid 2020", "Vehicles", "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Honda Vezel RS 2019", "Vehicles", "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Yamaha FZ Version 3", "Motorbikes", "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Vespa Classic Scooter 150", "Scooters", "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Mountain Trail Bicycle", "Bicycles", "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Alloy Wheels & Tires Set", "Auto Parts", "https://images.unsplash.com/photo-1578844251758-2f71da64c96f?w=800&auto=format&fit=crop&q=80")
        )
    ),
    CatalogPhotoCategory(
        title = "ඇඳුම් සහ විලාසිතා",
        icon = Icons.Default.ShoppingBag,
        photos = listOf(
            CatalogProductPhoto("Men's Premium Leather Jacket", "Fashion", "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Nike Air Jordan Retro Sneakers", "Footwear", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Traditional Silk Saree", "Women's Fashion", "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Designer Travel Duffel Bag", "Bags", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Ray-Ban Classic Aviator Sunglasses", "Accessories", "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800&auto=format&fit=crop&q=80")
        )
    ),
    CatalogPhotoCategory(
        title = "ගෘහ භාණ්ඩ සහ නිවස",
        icon = Icons.Default.Home,
        photos = listOf(
            CatalogProductPhoto("Modern 3-Piece Living Room Sofa", "Furniture", "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Teak Wood Dining Table 6 Chairs", "Furniture", "https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("King Size Bedroom Bed Set", "Furniture", "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("LG Double Door Inverter Refrigerator", "Home Appliances", "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Sony 55-inch 4K Smart OLED TV", "Electronics", "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&auto=format&fit=crop&q=80")
        )
    ),
    CatalogPhotoCategory(
        title = "ඔරලෝසු සහ ආභරණ",
        icon = Icons.Default.Watch,
        photos = listOf(
            CatalogProductPhoto("Rolex Submariner Style Watch", "Watches", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Apple Watch Ultra 2 GPS", "Smartwatches", "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("Casio G-Shock Rugged Edition", "Watches", "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80"),
            CatalogProductPhoto("22K Gold Plated Chain Necklace", "Jewelry", "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=800&auto=format&fit=crop&q=80")
        )
    )
)

@Composable
fun MarketplacePhotoGalleryModal(
    currentImageUrl: String,
    onDismiss: () -> Unit,
    onSelectImage: (String) -> Unit
) {
    val context = LocalContext.current
    val darkBackground = Color(0xFF18191A)
    val cardBackground = Color(0xFF242526)
    val textPrimary = Color(0xFFE4E6EB)
    val textSecondary = Color(0xFFB0B3B8)
    val fbBlue = Color(0xFF1877F2)

    var selectedUrl by remember { mutableStateOf(currentImageUrl) }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var customUrlInput by remember { mutableStateOf("") }

    // Real device gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUrl = uri.toString()
            Toast.makeText(context, "ගැලරියෙන් ඡායාරූපය තෝරා ගන්නා ලදී!", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = darkBackground
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Top Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackground)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "භාණ්ඩයේ ඡායාරූපය තෝරන්න",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = textPrimary
                            )
                            Text(
                                text = "Gallery & Authentic Product Catalog",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = textSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3A3B3C))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF393A3B), thickness = 0.5.dp)

                // Pick from Device Gallery Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Button(
                        onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📂 දුරකථනයේ Gallery එකෙන් ඡායාරූපයක් තෝරන්න",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }

                // Category Tabs
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(REAL_MARKETPLACE_CATALOG.indices.toList()) { index ->
                        val cat = REAL_MARKETPLACE_CATALOG[index]
                        val isSelected = selectedCategoryIndex == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) fbBlue else cardBackground)
                                .border(1.dp, if (isSelected) fbBlue else Color(0xFF4E4F50), RoundedCornerShape(16.dp))
                                .clickable { selectedCategoryIndex = index }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.title,
                                    color = if (isSelected) Color.White else textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Photos Grid
                val currentCategory = REAL_MARKETPLACE_CATALOG[selectedCategoryIndex]
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentCategory.photos) { photo ->
                        val isSelected = selectedUrl == photo.imageUrl
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedUrl = photo.imageUrl },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBackground),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, fbBlue) else null
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.2f)) {
                                    AsyncImage(
                                        model = photo.imageUrl,
                                        contentDescription = photo.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(fbBlue),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = photo.title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = textPrimary,
                                    maxLines = 1,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Custom Web URL Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customUrlInput,
                        onValueChange = { customUrlInput = it },
                        placeholder = { Text("හෝ Web Image URL එකක් ඇතුළත් කරන්න...", fontSize = 12.sp, color = textSecondary) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedContainerColor = cardBackground,
                            unfocusedContainerColor = cardBackground,
                            focusedBorderColor = fbBlue,
                            unfocusedBorderColor = Color(0xFF4E4F50)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (customUrlInput.isNotBlank()) {
                                selectedUrl = customUrlInput.trim()
                                Toast.makeText(context, "Image URL යොදන ලදී!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3B3C))
                    ) {
                        Text("යොදන්න", fontSize = 12.sp)
                    }
                }

                // Selected Preview & Confirmation Bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF4E4F50), RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = selectedUrl,
                                contentDescription = "Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "තෝරාගත් ඡායාරූපය ✓",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "භාණ්ඩය විකිණීම සඳහා සූදානම්",
                                color = Color(0xFF22C55E),
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                onSelectImage(selectedUrl)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = fbBlue)
                        ) {
                            Text("තහවුරු කරන්න", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
