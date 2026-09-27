package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.ui.components.AccountHelpAndAppealDialog
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.Post
import com.example.model.User
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.OledSurfaceVariant
import com.example.viewmodel.MainViewModel

data class MenuItemData(
    val id: String,
    val title: String,
    val iconVector: ImageVector? = null,
    val iconColor: Color,
    val customIconType: String = "VECTOR", // VECTOR, GRADIENT_REEL, META_AI, INSTAGRAM
    val tag: String
)

@Composable
fun MenuScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val friends by viewModel.allFriends.collectAsState()

    // Expandable accordion states
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var isHelpExpanded by remember { mutableStateOf(false) }

    var isDarkModeState by remember { mutableStateOf(true) }
    var isDataSaverState by remember { mutableStateOf(false) }
    var isBetaAccessState by remember { mutableStateOf(false) }
    val currentLangCode by viewModel.currentLanguage.collectAsState()
    val selectedLanguage = when (currentLangCode) {
        "SI" -> "සිංහල"
        "TA" -> "தமிழ் (Tamil)"
        else -> "English (US)"
    }
    var isLanguageDialogOpen by remember { mutableStateOf(false) }

    // Dialog Visibilities for each menu item
    var showMetaAiDialog by remember { mutableStateOf(false) }
    var showPagesDialog by remember { mutableStateOf(false) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var showMemoriesDialog by remember { mutableStateOf(false) }
    var showBirthdaysDialog by remember { mutableStateOf(false) }
    var showEventsDialog by remember { mutableStateOf(false) }
    var showMetaVerifiedDialog by remember { mutableStateOf(false) }
    var showFeedsDialog by remember { mutableStateOf(false) }
    var showInstagramLiteDialog by remember { mutableStateOf(false) }
    var showAccountSwitchDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showHelpCenterDialog by remember { mutableStateOf(false) }

    // The 12 menu items matching the colorful options:
    val menuItems = listOf(
        MenuItemData("friends", "මිතුරන්", Icons.Filled.People, Color(0xFF2563EB), tag = "menu_friends"),
        MenuItemData("reels", "Reels", null, Color(0xFFEC4899), customIconType = "GRADIENT_REEL", tag = "menu_reels"),
        MenuItemData("marketplace", "වෙළඳපොළ", Icons.Filled.Storefront, Color(0xFF0284C7), tag = "menu_marketplace"),
        MenuItemData("meta_ai", "AI සහායක", null, Color(0xFF8B5CF6), customIconType = "META_AI", tag = "menu_meta_ai"),
        MenuItemData("pages", "පිටු", Icons.Filled.Flag, Color(0xFFEA580C), tag = "menu_pages"),
        MenuItemData("saved", "සුරකිණි", Icons.Filled.Bookmark, Color(0xFFD946EF), tag = "menu_saved"),
        MenuItemData("memories", "මතකයන්", Icons.Filled.History, Color(0xFF06B6D4), tag = "menu_memories"),
        MenuItemData("birthdays", "උපන්දින", Icons.Filled.CardGiftcard, Color(0xFFF43F5E), tag = "menu_birthdays"),
        MenuItemData("events", "විශේෂ අවස්ථා", Icons.Filled.CalendarMonth, Color(0xFFEF4444), tag = "menu_events"),
        MenuItemData("meta_verified", "සත්‍යාපිත ලාංඡනය", Icons.Filled.Verified, Color(0xFF10B981), tag = "menu_meta_verified"),
        MenuItemData("feeds", "සංග්‍රහ", Icons.Filled.DynamicFeed, Color(0xFFF59E0B), tag = "menu_feeds"),
        MenuItemData("instagram_lite", "මාධ්‍ය ගැලරිය", Icons.Filled.PhotoLibrary, Color(0xFF6366F1), customIconType = "INSTAGRAM", tag = "menu_instagram_lite")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF18191A)) // Dark surface
    ) {
        // Top Bar Matching Screenshot: < මෙනුව [Swap/Switch] [Search]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF242526))
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.selectTab(0) }, // Reverse back to Home Feed
                modifier = Modifier
                    .size(40.dp)
                    .testTag("menu_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "මෙනුව",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            // Switch / Swap Accounts Icon
            IconButton(
                onClick = { showAccountSwitchDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("menu_switch_account_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = "Switch Account",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Search Icon
            IconButton(
                onClick = { viewModel.setSearchOpen(true) },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("menu_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        HorizontalDivider(color = Color(0xFFE4E6EB), thickness = 1.dp)

        // Scrollable Body (Profile + Grid + Expandable Settings + Add Account + Log Out)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Profile Navigation Card matching FB Lite Menu
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable { viewModel.selectTab(7) }
                    .testTag("menu_profile_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFF393A3B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = currentUser.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ඔබේ ප්‍රොෆයිලය බලන්න",
                            color = Color(0xFFB0B3B8),
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFFB0B3B8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dedicated Business Pages Shortcut Card (completely separate from personal account)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable {
                        viewModel.switchContextToBusiness(
                            com.example.model.BusinessPage(
                                id = "business_page_1",
                                name = "FriendHub Official Store",
                                category = "Shopping & Retail • Electronics",
                                imageUrl = "https://images.unsplash.com/photo-1557821552-17105176674c?w=500&auto=format&fit=crop&q=80",
                                coverUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1200&auto=format&fit=crop&q=80",
                                followers = 12000,
                                unreadMessages = 3,
                                bio = "නවතම Electronics & Smart Gadgets අඩුම මිලට! Islandwide Cash on Delivery."
                            )
                        )
                    }
                    .testTag("menu_switch_to_business_page"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F3FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1877F2).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1877F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Business Page",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🏢 Business Page එකට මාරු වන්න",
                            color = Color(0xFF1877F2),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "FriendHub Official Store (වෙනම Home Screen)",
                            color = Color(0xFF65676B),
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF1877F2),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2-Column Grid of 12 Items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (rowIndex in 0 until 6) {
                    val firstIndex = rowIndex * 2
                    val secondIndex = firstIndex + 1

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Left Item
                        if (firstIndex < menuItems.size) {
                            val item = menuItems[firstIndex]
                            MenuCardItem(
                                item = item,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    handleMenuClick(
                                        item.id,
                                        viewModel,
                                        onMetaAi = { showMetaAiDialog = true },
                                        onPages = { showPagesDialog = true },
                                        onSaved = { showSavedDialog = true },
                                        onMemories = { showMemoriesDialog = true },
                                        onBirthdays = { showBirthdaysDialog = true },
                                        onEvents = { showEventsDialog = true },
                                        onMetaVerified = { showMetaVerifiedDialog = true },
                                        onFeeds = { showFeedsDialog = true },
                                        onInstagram = { showInstagramLiteDialog = true }
                                    )
                                }
                            )
                        }

                        // Right Item
                        if (secondIndex < menuItems.size) {
                            val item = menuItems[secondIndex]
                            MenuCardItem(
                                item = item,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    handleMenuClick(
                                        item.id,
                                        viewModel,
                                        onMetaAi = { showMetaAiDialog = true },
                                        onPages = { showPagesDialog = true },
                                        onSaved = { showSavedDialog = true },
                                        onMemories = { showMemoriesDialog = true },
                                        onBirthdays = { showBirthdaysDialog = true },
                                        onEvents = { showEventsDialog = true },
                                        onMetaVerified = { showMetaVerifiedDialog = true },
                                        onFeeds = { showFeedsDialog = true },
                                        onInstagram = { showInstagramLiteDialog = true }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2 Pill Buttons (Feeds & Media Gallery)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.selectTab(0) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DynamicFeed, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("සංග්‍රහ", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showInstagramLiteDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("මාධ්‍ය ගැලරිය", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFF393A3B), thickness = 1.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // Expandable 1: සැකසුම් සහ පෞද්ගලිකත්වය (Settings & Privacy) - Exactly Screenshot 6
            ExpandableMenuRow(
                icon = Icons.Default.Settings,
                title = "සැකසුම් සහ පෞද්ගලිකත්වය",
                isExpanded = isSettingsExpanded,
                onToggle = { isSettingsExpanded = !isSettingsExpanded },
                tag = "menu_settings_privacy_row"
            )

            AnimatedVisibility(
                visible = isSettingsExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    SubOptionRow(
                        icon = Icons.Default.Settings,
                        title = "සැකසුම් (Settings)",
                        onClick = { viewModel.setAppSettingsOpen(true) }
                    )
                    SubOptionRow(
                        icon = Icons.Default.AccountCircle,
                        title = "ගිණුම් සම්බන්ධ කිරීම (Account Linking - Phone & Email)",
                        onClick = { viewModel.setAppSettingsOpen(true) }
                    )
                    SubOptionRow(
                        icon = Icons.Default.CreditCard,
                        title = "ඇණවුම් සහ ගෙවීම් (Orders & Payments)",
                        onClick = { viewModel.setAppSettingsOpen(true) }
                    )
                    SubOptionSwitchRow(
                        icon = Icons.Default.DarkMode,
                        title = "අඳුරු ප්‍රකාරය (Dark Mode)",
                        checked = viewModel.isDarkMode.collectAsState().value,
                        onCheckedChange = {
                            viewModel.setDarkMode(it)
                            Toast.makeText(context, if (it) "අඳුරු ප්‍රකාරය සක්‍රීයයි 🌙" else "ලා පැහැති ප්‍රකාරය සක්‍රීයයි ☀️", Toast.LENGTH_SHORT).show()
                        }
                    )
                    SubOptionRow(
                        icon = Icons.Default.Language,
                        title = "භාෂාව (Language) - $selectedLanguage",
                        onClick = { isLanguageDialogOpen = true }
                    )
                    SubOptionSwitchRow(
                        icon = Icons.Default.NetworkCheck,
                        title = "ඩේටා සුරැකුම (Data Saver)",
                        checked = isDataSaverState,
                        onCheckedChange = {
                            isDataSaverState = it
                            Toast.makeText(context, if (it) "ඩේටා සුරැකුම සක්‍රීයයි 📶" else "ඩේටා සුරැකුම අක්‍රීයයි", Toast.LENGTH_SHORT).show()
                        }
                    )
                    SubOptionRow(
                        icon = Icons.Default.CleaningServices,
                        title = "ඉඩ හිස් කරන්න (Clear Space)",
                        onClick = {
                            Toast.makeText(context, "හැඹිලි මතකය 48.2 MB සාර්ථකව හිස් කරන ලදී 🧹", Toast.LENGTH_LONG).show()
                        }
                    )
                    SubOptionSwitchRow(
                        icon = Icons.Default.Science,
                        title = "පූර්ව ප්‍රවේශය (Beta / Early Access)",
                        checked = isBetaAccessState,
                        onCheckedChange = {
                            isBetaAccessState = it
                            Toast.makeText(context, if (it) "පූර්ව ප්‍රවේශ (Beta) විශේෂාංග සක්‍රීයයි 🧪" else "Beta විශේෂාංග අක්‍රීයයි", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE4E6EB), thickness = 0.8.dp)

            // Expandable 2: උපකාර සහ සහාය (Help & Support)
            ExpandableMenuRow(
                icon = Icons.Default.Help,
                title = "උපකාර සහ සහාය",
                isExpanded = isHelpExpanded,
                onToggle = { isHelpExpanded = !isHelpExpanded },
                tag = "menu_help_support_row"
            )

            AnimatedVisibility(
                visible = isHelpExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    SubOptionRow(
                        icon = Icons.Default.Help,
                        title = "උපකාරක මධ්‍යස්ථානය (Help Center)",
                        onClick = { showHelpCenterDialog = true }
                    )
                    SubOptionRow(
                        icon = Icons.Default.Security,
                        title = "ගිණුම් Ban Appeals සහ ආරක්ෂාව",
                        onClick = { showHelpCenterDialog = true }
                    )
                    SubOptionRow(
                        icon = Icons.Default.SupportAgent,
                        title = "තාක්ෂණික සහාය සේවාව (Contact Support)",
                        onClick = { showHelpCenterDialog = true }
                    )
                    SubOptionRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "ගැටලුවක් වාර්තා කරන්න (Report a Problem)",
                        onClick = { showHelpCenterDialog = true }
                    )
                    SubOptionRow(
                        icon = Icons.Default.History,
                        title = "සහාය ලැබුණු ලිපි (Support Inbox)",
                        onClick = {
                            Toast.makeText(context, "විවෘත ගැටලු කිසිවක් නැත (All clear).", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE4E6EB), thickness = 0.8.dp)

            // 3: ගිණුම එක් කරන්න (Add Account)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAccountSwitchDialog = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("menu_add_account_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "ගිණුම එක් කරන්න",
                    tint = Color(0xFF65676B),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "ගිණුම එක් කරන්න",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            HorizontalDivider(color = Color(0xFFE4E6EB), thickness = 0.8.dp)

            // 3.5: ගිණුම මකා දමන්න (Delete Account) - Explicitly requested
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDeleteConfirmDialog = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("menu_delete_account_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "ගිණුම මකා දමන්න",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "ගිණුම ස්ථිරවම මකා දමන්න (Delete Account)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
            }

            HorizontalDivider(color = Color(0xFFE4E6EB), thickness = 0.8.dp)

            // 4: පිටවන්න (Log Out)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutConfirmDialog = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("menu_logout_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "පිටවන්න",
                    tint = Color(0xFF65676B),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "පිටවන්න",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }

    // ================= Interactive Sub-Dialogs =================

    // Language Selection Dialog
    if (isLanguageDialogOpen) {
        AlertDialog(
            onDismissRequest = { isLanguageDialogOpen = false },
            containerColor = Color(0xFF242526),
            title = { Text("භාෂාව තෝරන්න (Select Language)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("සිංහල", "English (US)", "English (UK)", "தமிழ் (Tamil)").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    Toast.makeText(context, "භාෂාව මාරු කරන ලදී: $lang", Toast.LENGTH_SHORT).show()
                                    isLanguageDialogOpen = false
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    Toast.makeText(context, "භාෂාව මාරු කරන ලදී: $lang", Toast.LENGTH_SHORT).show()
                                    isLanguageDialogOpen = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF2563EB),
                                    unselectedColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(lang, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isLanguageDialogOpen = false }) {
                    Text("හරි (Done)", color = NeonBlue)
                }
            }
        )
    }

    // 0. Account Help & Ban Appeal Dialog
    if (showHelpCenterDialog) {
        AccountHelpAndAppealDialog(
            currentUser = currentUser,
            onDismiss = { showHelpCenterDialog = false }
        )
    }

    // 1. Meta AI Interactive Chat Modal
    if (showMetaAiDialog) {
        MetaAiDialog(onDismiss = { showMetaAiDialog = false })
    }

    // 2. Facebook Pages (පිටු) Modal
    if (showPagesDialog) {
        PagesDialog(viewModel = viewModel, onDismiss = { showPagesDialog = false })
    }

    // 3. Saved (සුරකිණි) Modal
    if (showSavedDialog) {
        SavedPostsDialog(posts = posts, onDismiss = { showSavedDialog = false })
    }

    // 4. Memories (මතකයන්) Throwback Modal
    if (showMemoriesDialog) {
        MemoriesDialog(onDismiss = { showMemoriesDialog = false })
    }

    // 5. Birthdays (උපන්දින) Modal
    if (showBirthdaysDialog) {
        BirthdaysDialog(friends = friends, onDismiss = { showBirthdaysDialog = false })
    }

    // 6. Events (විශේෂ අවස්ථා) Modal
    if (showEventsDialog) {
        EventsDialog(onDismiss = { showEventsDialog = false })
    }

    // 7. Meta Verified Modal
    if (showMetaVerifiedDialog) {
        MetaVerifiedDialog(currentUser = currentUser, onDismiss = { showMetaVerifiedDialog = false })
    }

    // 8. Feeds (සංග්‍රහ) Switcher Modal
    if (showFeedsDialog) {
        FeedsFilterDialog(viewModel = viewModel, onDismiss = { showFeedsDialog = false })
    }

    // 9. Instagram Lite Modal
    if (showInstagramLiteDialog) {
        InstagramLiteDialog(currentUser = currentUser, onDismiss = { showInstagramLiteDialog = false })
    }

    // 10. Switch / Add Account Dialog
    if (showAccountSwitchDialog) {
        AccountSwitchDialog(
            currentUser = currentUser,
            onDismiss = { showAccountSwitchDialog = false },
            onAddAccount = {
                showAccountSwitchDialog = false
                viewModel.setAuthModalOpen(true)
            }
        )
    }

    // 11. Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("පිටවීම තහවුරු කරන්න", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("ඔබට ඔබගේ FriendHub ගිණුමෙන් සැබවින්ම පිටවීමට (Log Out) අවශ්‍යද?", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        viewModel.logout()
                        Toast.makeText(context, "ගිණුමෙන් සාර්ථකව පිටවිය (Logged Out)", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE41E3F))
                ) {
                    Text("පිටවන්න", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF242526)
        )
    }

    // 12. Delete Account Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("ගිණුම මකා දැමීම තහවුරු කරන්න", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { 
                Column {
                    Text("ඔබට ඔබගේ FriendHub ගිණුම සැබවින්ම ස්ථිරවම මකා දැමීමට අවශ්‍යද?", color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⚠️ අවධානය: මෙම ක්‍රියාව ආපසු හැරවිය නොහැක. ඔබගේ සියලුම දත්ත (පෝස්ට්, පණිවුඩ, මිතුරන්) ස්ථිරවම ඉවත්වනු ඇත.", color = Color(0xFFEF4444), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteAccount(context)
                        Toast.makeText(context, "ගිණුම ස්ථිරවම මකා දමන ලදී", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("ස්ථිරවම මකා දමන්න", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF242526)
        )
    }
}

// Single Menu Grid Card Item
@Composable
fun MenuCardItem(
    item: MenuItemData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1.95f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(item.tag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF393A3B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon according to custom type
            when (item.customIconType) {
                "GRADIENT_REEL" -> {
                    // 3D Red Video Play Icon for Reels / Videos
                    Image(
                        painter = painterResource(id = R.drawable.ic_3d_video_play),
                        contentDescription = "Reels / Video",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                    )
                }
                "META_AI" -> {
                    // Meta AI Swirling Flower / Bloom Icon
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF8B5CF6),
                                        Color(0xFF3B82F6),
                                        Color(0xFF06B6D4),
                                        Color(0xFFD946EF),
                                        Color(0xFF8B5CF6)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Meta AI",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                "INSTAGRAM" -> {
                    // Instagram Lite Camera Gradient Icon
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF58529), Color(0xFFDD2A7B), Color(0xFF8134AF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📸",
                            fontSize = 15.sp
                        )
                    }
                }
                else -> {
                    item.iconVector?.let { vector ->
                        Icon(
                            imageVector = vector,
                            contentDescription = item.title,
                            tint = item.iconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

// Expandable Header Row
@Composable
fun ExpandableMenuRow(
    icon: ImageVector,
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    tag: String
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrowRotation")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFFB0B3B8),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Expand",
            tint = Color(0xFFB0B3B8),
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation)
        )
    }
}

// Sub-option Row
@Composable
fun SubOptionRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color(0xFFE4E6EB)
        )
    }
}

// Sub-option Switch Row
@Composable
fun SubOptionSwitchRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color(0xFFE4E6EB)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2563EB),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF334155)
            )
        )
    }
}

// Click Router for Menu Cards
private fun handleMenuClick(
    id: String,
    viewModel: MainViewModel,
    onMetaAi: () -> Unit,
    onPages: () -> Unit,
    onSaved: () -> Unit,
    onMemories: () -> Unit,
    onBirthdays: () -> Unit,
    onEvents: () -> Unit,
    onMetaVerified: () -> Unit,
    onFeeds: () -> Unit,
    onInstagram: () -> Unit
) {
    when (id) {
        "friends" -> viewModel.selectTab(1) // Friends Screen
        "reels" -> viewModel.selectTab(3) // Reels / Videos Screen
        "marketplace" -> viewModel.selectTab(5) // Marketplace Screen
        "notifications" -> viewModel.selectTab(4) // Notifications Screen
        "meta_ai" -> onMetaAi()
        "pages" -> onPages()
        "saved" -> onSaved()
        "memories" -> onMemories()
        "birthdays" -> onBirthdays()
        "events" -> onEvents()
        "meta_verified" -> onMetaVerified()
        "feeds" -> onFeeds()
        "instagram_lite" -> onInstagram()
    }
}

// ================= Dialog 1: FriendHub AI Chat Dialog =================
@Composable
fun MetaAiDialog(onDismiss: () -> Unit) {
    var prompt by remember { mutableStateOf("") }
    val chatHistory = remember {
        mutableStateListOf(
            Pair("FriendHub AI", "ආයුබෝවන්! මම FriendHub AI. ඔබට ඕනෑම ප්‍රශ්නයක් සිංහලෙන් හෝ ඉංග්‍රීසියෙන් මගෙන් අසන්න පුළුවන්."),
            Pair("User", "FriendHub එකේ Reels වැඩිපුර View කරගන්න උපදෙස් දෙන්න"),
            Pair("FriendHub AI", "Reels වැඩිපුර reach කරගැනීමට:\n1. පළමු තත්පර 3 ආකර්ෂණීය කරන්න.\n2. Trending Audio භාවිතා කරන්න.\n3. පැහැදිලි ආලෝකකරණය සහ #Sinhala #Trending ටැග් යොදන්න!")
        )
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF18191A))
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6), Color(0xFF06B6D4), Color(0xFF8B5CF6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("FriendHub AI", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Text("ස්මාර්ට් කෘත්‍රිම බුද්ධි සහායක", fontSize = 11.sp, color = Color(0xFFA855F7))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat Messages list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                chatHistory.forEach { (sender, message) ->
                    val isAi = sender == "FriendHub AI"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAi) Color(0xFF242526) else Color(0xFF0084FF)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (isAi) {
                                    Text("✨ FriendHub AI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA855F7))
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(message, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Prompt Suggestions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("කවියක් ලියන්න", "Tech News", "Caption එකක්").forEach { suggestion ->
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                prompt = suggestion
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2F30))
                    ) {
                        Text(suggestion, fontSize = 12.sp, color = Color.LightGray, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("FriendHub AI ගෙන් ඕනෑම දෙයක් අසන්න...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF242526),
                        unfocusedContainerColor = Color(0xFF242526),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFA855F7),
                        unfocusedBorderColor = Color(0xFF393A3B)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (prompt.isNotBlank()) {
                            chatHistory.add(Pair("User", prompt))
                            val answer = when {
                                prompt.contains("කවි", ignoreCase = true) -> "සඳ පහන් රැයේ, තරු දිලෙන පැයේ,\nනෙත් යුග සනහා, සිත ප්‍රබෝධ වේ! 🌟"
                                prompt.contains("News", ignoreCase = true) -> "අද දින තාක්ෂණික පුවත්: FriendHub නව AI විශේෂාංග ශ්‍රී ලංකාවට හඳුන්වා දී ඇත."
                                else -> "ඔබගේ ප්‍රශ්නය: '$prompt'\nFriendHub AI විසඳුම: ඉතා හොඳ ප්‍රශ්නයක්! මෙය පිළිබඳ වැඩිදුර කරුණු ඉක්මනින්ම ඔබට ඉදිරිපත් කරමි."
                            }
                            chatHistory.add(Pair("FriendHub AI", answer))
                            prompt = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0084FF))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

// ================= Dialog 2: Pages (පිටු) Modal =================
@Composable
fun PagesDialog(viewModel: MainViewModel? = null, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPageNameInput by remember { mutableStateOf("") }
    var newPageCategoryInput by remember { mutableStateOf("Shopping & Retail") }
    var newPagePhoneInput by remember { mutableStateOf("0771234567") }

    val pages = remember {
        mutableStateListOf(
            Triple("NewsFirst Sinhala", "1.8M Followers • News & Media", true),
            Triple("Derana eSports", "450K Followers • Gaming Community", false),
            Triple("Sri Lanka Cricket Fans", "980K Followers • Sports", true),
            Triple("Tech Katha Sinhala", "320K Followers • Science & Tech", false)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("පිටු (Pages)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Business Pages You Manage (Switch Context)
                Text("ඔබ කළමනාකරණය කරන පිටු (Your Business Pages)", fontSize = 13.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("FriendHub Official Store", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("12K Followers • Retail & Gadgets", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel?.switchContextToBusiness(
                                    com.example.model.BusinessPage(
                                        id = "business_page_1",
                                        name = "FriendHub Official Store",
                                        category = "Shopping & Retail • Electronics",
                                        imageUrl = "https://images.unsplash.com/photo-1557821552-17105176674c?w=500&auto=format&fit=crop&q=80",
                                        coverUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1200&auto=format&fit=crop&q=80",
                                        followers = 12000,
                                        unreadMessages = 3,
                                        bio = "නවතම Electronics & Smart Gadgets අඩුම මිලට! Islandwide Cash on Delivery."
                                    )
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("මාරු වන්න ➡️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        showCreateDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ නව පිටුවක් තනන්න (Create Page)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("ඔබ අනුගමනය කරන පිටු (Followed Pages)", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                pages.forEachIndexed { index, (name, followers, isFollowed) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(followers, color = Color.Gray, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                pages[index] = Triple(name, followers, !isFollowed)
                                Toast.makeText(context, if (!isFollowed) "$name අනුගමනය කළා" else "$name ඉවත් කළා", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowed) Color(0xFF3A3B3C) else Color(0xFF1877F2)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(if (isFollowed) "Following" else "Follow", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = Color(0xFF242526),
            title = { Text("🚩 Create New Business Page", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newPageNameInput,
                        onValueChange = { newPageNameInput = it },
                        label = { Text("Page Name (පිටුවේ නම)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPageCategoryInput,
                        onValueChange = { newPageCategoryInput = it },
                        label = { Text("Category (ප්‍රවර්ගය)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPagePhoneInput,
                        onValueChange = { newPagePhoneInput = it },
                        label = { Text("WhatsApp / Phone Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPageNameInput.isNotBlank()) {
                            pages.add(0, Triple(newPageNameInput, "1 Followers • $newPageCategoryInput", true))
                            showCreateDialog = false
                            onDismiss()
                            Toast.makeText(context, "🎉 Business Page '$newPageNameInput' created successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please enter a page name", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                ) {
                    Text("Create Page", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = Color(0xFFB0B3B8))
                }
            }
        )
    }
}

// ================= Dialog 3: Saved (සුරකිණි) Modal =================
@Composable
fun SavedPostsDialog(posts: List<Post>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val feedSavedPosts = remember(posts) { posts.filter { it.isSaved } }
    val defaultSavedPosts = remember {
        mutableStateListOf(
            "සුපිරි AI Trick එකක්: සිංහලෙන් Prompt එකක් ලියන හැටි 🚀",
            "ශ්‍රී ලංකා සංචාරක ස්ථාන 10ක් (Hidden Gems in Ella) 🇱🇰",
            "කොළඹ අලුත්ම Tech Meetup එකට ලියාපදිංචි වන්න"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFFD946EF), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("සුරකිණි (Saved)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Feed saved posts
                if (feedSavedPosts.isNotEmpty()) {
                    Text("සුරකින ලද Feed සටහන් (${feedSavedPosts.size})", color = Color(0xFFD946EF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    feedSavedPosts.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF18191A))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFFD946EF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.userName, color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    p.content.ifBlank { "[ඡායාරූප හෝ වීඩියෝ සටහනකි]" },
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (feedSavedPosts.isEmpty() && defaultSavedPosts.isEmpty()) {
                    Text("කිසිදු සටහනක් සුරැකී නැත", color = Color.Gray, modifier = Modifier.padding(vertical = 20.dp))
                } else {
                    Text("සුරකින ලද වෙනත් සටහන්", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    defaultSavedPosts.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF18191A))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    defaultSavedPosts.remove(item)
                                    Toast.makeText(context, "සුරැකුම් වලින් ඉවත් කෙරිණි", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= Dialog 4: Memories (මතකයන්) Modal =================
@Composable
fun MemoriesDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("මතකයන් (Memories)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("වසර 1කට පෙර අද දින (On this day 1 year ago) 📅", fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18191A))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("නුවරඑළිය සංචාරයේ සුන්දර මතකයන්! ගොඩක් කාලෙකට පස්සේ යාලුවොත් එක්ක ගිය ලස්සන දවසක් 🌸⛰️", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1544644181-1484b3fdfc62?w=800&auto=format&fit=crop&q=80",
                            contentDescription = "Memory Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "මතකය News Feed එකේ බෙදාගත්තා! (Shared to Feed) ✨", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("බෙදාගන්න (Share Memory)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ================= Dialog 5: Birthdays (උපන්දින) Modal =================
@Composable
fun BirthdaysDialog(friends: List<User>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val todayBirthdays = listOf(
        Pair("කසුන් පෙරේරා", "අද උපන්දිනය සමරයි! 🎂"),
        Pair("දිල්ෂාන් මධුශංක", "අද උපන්දිනය සමරයි! 🎈")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("උපන්දින (Birthdays)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("අද උපන්දින (Today's Birthdays)", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                todayBirthdays.forEach { (name, note) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF18191A))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(note, color = Color.Gray, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "$name වෙත 'සුබ උපන්දිනයක්!' සුබපැතුම යැව්වා! 🎉", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("සුබපතන්න 🎁", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ================= Dialog 6: Events (විශේෂ අවස්ථා) Modal =================
@Composable
fun EventsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val events = remember {
        mutableStateListOf(
            Triple("Colombo Tech & AI Summit 2026", "BMICH, Colombo • හෙට පෙ.ව. 9:00", false),
            Triple("Live Musical Night (සජීවී ප්‍රසංගය)", "Viharamahadevi Open Air • සිකුරාදා", true),
            Triple("Blood Donation Camp (ලේ දන්දීමේ කඳවුර)", "Town Hall Colombo • ලබන ඉරිදා", false)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("විශේෂ අවස්ථා (Events)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                events.forEachIndexed { index, (title, details, isGoing) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18191A))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(details, color = Color.Gray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        events[index] = Triple(title, details, !isGoing)
                                        Toast.makeText(context, if (!isGoing) "සහභාගී වෙනවා ලෙස ලකුණු කළා! ✔️" else "ඉවත් කළා", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isGoing) Color(0xFF2D88FF) else Color(0xFF3A3B3C)
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(if (isGoing) "Going (සහභාගී වෙනවා) ✔" else "Interested (කැමැතියි)", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= Dialog 7: Verified Badge Modal =================
@Composable
fun MetaVerifiedDialog(currentUser: User, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var isSubscribed by remember { mutableStateOf(currentUser.isVerified) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0284C7).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("සත්‍යාපිත ලාංඡනය (Verified)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                Text("Build trust and authenticity with a verified blue badge", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF18191A))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✓ නිල Blue Verified Badge එක ඔබගේ නම ඉදිරියෙන්", color = Color.LightGray, fontSize = 12.sp)
                    Text("✓ අනන්‍යතා සොරකම් වලින් ඉහළ ගිණුම් ආරක්ෂාව (Identity Protection)", color = Color.LightGray, fontSize = 12.sp)
                    Text("✓ ප්‍රමුඛතා පාරිභෝගික සහාය (Direct Account Support)", color = Color.LightGray, fontSize = 12.sp)
                    Text("✓ විශේෂිත Reels ස්ටිකර් සහ ප්‍රතිචාර", color = Color.LightGray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isSubscribed = !isSubscribed
                        Toast.makeText(context, if (isSubscribed) "සුබපැතුම්! Verified Blue Badge එක ලැබුණි 🎖️" else "දායකත්වය අවලංගු කළා", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) Color(0xFF10B981) else Color(0xFF0284C7)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isSubscribed) "Active Verified Subscriber ✔" else "ග්‍රාහක වන්න (Subscribe - LKR 2,400/mo)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))
                TextButton(onClick = onDismiss) {
                    Text("පසුව කරන්න (Maybe Later)", color = Color.Gray)
                }
            }
        }
    }
}

// ================= Dialog 8: Feeds (සංග්‍රහ) Switcher =================
@Composable
fun FeedsFilterDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val feedFilter by viewModel.feedFilter.collectAsState()
    val options = listOf(
        Pair("ALL", "සියල්ල (All Feed)"),
        Pair("TRENDING", "ප්‍රියතම (Favorites)"),
        Pair("FRIENDS", "මිතුරන් පමණක් (Friends Only)"),
        Pair("MEDIA", "ඡායාරූප සහ වීඩියෝ (Media Only)"),
        Pair("ENCRYPTED", "ආරක්ෂිත සටහන් (Encrypted)")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DynamicFeed, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("සංග්‍රහ (Feeds)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                options.forEach { (key, label) ->
                    val isSelected = feedFilter == key
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.setFeedFilter(key)
                                onDismiss()
                                viewModel.selectTab(0) // Return to Feed with filter
                            }
                            .background(if (isSelected) Color(0xFF1877F2).copy(alpha = 0.2f) else Color.Transparent)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = if (isSelected) Color(0xFF1877F2) else Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1877F2))
                        }
                    }
                }
            }
        }
    }
}

// ================= Dialog 9: Media Gallery Modal =================
@Composable
fun InstagramLiteDialog(currentUser: User, onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("මාධ්‍ය ගැලරිය (Media Gallery)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentUser.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(currentUser.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Text("@${currentUser.username} • FriendHub ගිණුම හා සම්බන්ධයි", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Recent Photos & Reels", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Photo Grid
            val sampleImages = listOf(
                "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sampleImages.forEach { img ->
                    AsyncImage(
                        model = img,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                Toast.makeText(context, "Instagram Post Viewer", Toast.LENGTH_SHORT).show()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Instagram Lite ඇප් එක විවෘත වේ...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Open Instagram App 📸", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ================= Dialog 10: Switch / Add Account Dialog =================
@Composable
fun AccountSwitchDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onAddAccount: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ගිණුම් මාරු කරන්න (Switch Account)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Active Account
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF18191A))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentUser.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("ක්‍රියාකාරී ගිණුම (Active)", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add Another Account Button
                Button(
                    onClick = onAddAccount,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("වෙනත් ගිණුමකට පිවිසෙන්න (+ Add Account)")
                }
            }
        }
    }
}
