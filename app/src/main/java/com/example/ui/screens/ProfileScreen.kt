package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.BusinessPage
import com.example.model.HighlightCategory
import com.example.model.Post
import com.example.model.ProfileVisitor
import com.example.model.User
import com.example.repository.DefaultSocialData
import com.example.ui.components.AppSettingsModal
import com.example.ui.components.ProfileFrameOverlay
import com.example.ui.components.ProfileLockDialog
import com.example.ui.components.ProfileNoteDialog
import com.example.ui.components.ProfilePhotoBottomSheet
import com.example.ui.components.ViewProfilePhotoDialog
import com.example.ui.components.CreatorDashboardModal
import com.example.ui.components.VipGoldCheckmark
import com.example.ui.components.AdMobBannerAd
import com.example.ui.components.AdMobRewardedAdDialog
import com.example.ui.components.AdMobInterstitialAdDialog
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.MonetizationOn
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurfaceVariant
import com.example.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val currentUser by viewModel.currentUser.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val reels by viewModel.reels.collectAsState()
    val profileVisitors by viewModel.profileVisitors.collectAsState()
    val e2eEncryptionEnabled by viewModel.e2eEncryptionEnabled.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isEditProfileOpen by viewModel.isEditProfileOpen.collectAsState()

    // Screen State
    var showPhotoOptionsSheet by remember { mutableStateOf(false) }
    var showEditorScreen by remember { mutableStateOf(false) }
    var editorInitialPhotoUrl by remember { mutableStateOf("") }
    var editorInitialFrameId by remember { mutableStateOf<String?>(null) }
    var showViewPhotoDialog by remember { mutableStateOf(false) }
    var showProfileVisitorsModal by remember { mutableStateOf(false) }
    var showLockDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showMoreOptionsMenu by remember { mutableStateOf(false) }
    var showAppSettingsModal by remember { mutableStateOf(false) }
    var showLinkedDevicesDialog by remember { mutableStateOf(false) }
    var selectedPillTab by remember { mutableIntStateOf(0) } // 0: Post, 1: Media, 2: Details

    val storyHighlights = remember { DefaultSocialData.storyHighlights }

    val myPosts = remember(posts, currentUser) {
        posts.filter { it.userId == currentUser.id || it.userName.equals(currentUser.name, ignoreCase = true) }
    }
    val myMediaPosts = remember(myPosts) {
        myPosts.filter { !it.mediaUrl.isNullOrBlank() }
    }

    // Photo Pickers
    val profilePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            editorInitialPhotoUrl = uri.toString()
            editorInitialFrameId = currentUser.profileFrame
            showEditorScreen = true
        }
    }

    val coverPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val updatedUser = currentUser.copy(coverUrl = uri.toString())
            viewModel.updateUserProfile(updatedUser)
            Toast.makeText(context, "Cover photo updated! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    if (showEditorScreen) {
        ProfilePictureEditorScreen(
            initialPhotoUrl = editorInitialPhotoUrl.ifBlank { currentUser.avatarUrl },
            initialFrameId = editorInitialFrameId ?: currentUser.profileFrame,
            viewModel = viewModel,
            onDismiss = { showEditorScreen = false }
        )
        return
    }

    // Main Profile Screen Layout
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120)) // Deep Ultra Slate / Obsidian
            .padding(bottom = 80.dp)
    ) {
        // 1. Cover Banner & Floating Action Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                // Cover Image
                AsyncImage(
                    model = currentUser.coverUrl.ifBlank { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80" },
                    contentDescription = "Cover Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                // Top Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.selectTab(0) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Visitors Eye Icon with Counter and glowing border
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                .clickable { showProfileVisitorsModal = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Profile Visitors",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "👀 ${profileVisitors.size}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF38BDF8))
                                )
                            }
                        }

                        // Search Icon
                        IconButton(
                            onClick = { viewModel.setSearchOpen(true) },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }

                        // More Settings & Options Icon
                        IconButton(
                            onClick = { showMoreOptionsMenu = true },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "Settings & Options",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Edit Cover Camera Button
                IconButton(
                    onClick = {
                        try {
                            coverPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } catch (e: Exception) {
                            viewModel.setEditProfileOpen(true)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Edit Cover",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 2. Profile Card Section (Avatar, Name, Bio, Edit Profile & Stats)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Dark Slate 800
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Centered Rounded Square Avatar with Badge and Floating Thought Bubble
                    Box(
                        modifier = Modifier
                            .offset(y = (-45).dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Centered Rounded Square Avatar
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clickable { showPhotoOptionsSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = currentUser.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80" },
                                contentDescription = currentUser.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(32.dp))
                                    .border(4.dp, Color(0xFF1E293B), RoundedCornerShape(32.dp))
                            )

                            ProfileFrameOverlay(
                                frameId = currentUser.profileFrame,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Plus Badge at Bottom-Right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB)) // Vibrant Blue
                                    .border(2.dp, Color(0xFF1E293B), CircleShape)
                                    .clickable { showPhotoOptionsSheet = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Floating Thought / Status Note Bubble over Avatar
                        Surface(
                            onClick = { showNoteDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                            shadowElevation = 6.dp,
                            modifier = Modifier.offset(y = (-18).dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💭", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (!currentUser.profileNote.isNullOrBlank()) currentUser.profileNote!! else "+ Note",
                                    color = if (!currentUser.profileNote.isNullOrBlank()) Color.White else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Name, Bio, and Link (Offset to balance avatar overlap)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = (-35).dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser.name.ifBlank { "Jane Cooper" },
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = Color.White
                                )
                            )
                            if (currentUser.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (currentUser.isProfileLocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    onClick = { showLockDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E3A8A).copy(alpha = 0.6f),
                                    border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF93C5FD), modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Locked", fontSize = 10.sp, color = Color(0xFF93C5FD), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bio subtitle / tags
                        Text(
                            text = currentUser.bio.ifBlank { "Apple CEO, Auburn buke, National parks" },
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Clickable bio link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Opening link: ${currentUser.website.ifBlank { "bio.link.io/j.copr" }}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentUser.website.ifBlank { "bio.link.io/j.copr" },
                                fontSize = 13.sp,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Edit Profile | Add Story | ··· More Options
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.setEditProfileOpen(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(21.dp),
                                border = BorderStroke(1.dp, Color(0xFF475569)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Edit Profile",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.createStory(
                                        mediaUrl = currentUser.avatarUrl,
                                        caption = "My profile story 🌟",
                                        backgroundColor = "#2563EB"
                                    )
                                    Toast.makeText(context, "Story added! 📖", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(21.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add Story",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }

                            // 3-dot More Options Button
                            IconButton(
                                onClick = { showMoreOptionsMenu = true },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF334155))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "Options",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Profile Lock Status Banner
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLockDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(
                                1.dp,
                                if (currentUser.isProfileLocked) Color(0xFF3B82F6).copy(alpha = 0.4f)
                                else Color(0xFF10B981).copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(
                                            if (currentUser.isProfileLocked) Color(0xFF2563EB).copy(alpha = 0.2f)
                                            else Color(0xFF10B981).copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (currentUser.isProfileLocked) Icons.Default.Shield else Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = if (currentUser.isProfileLocked) Color(0xFF60A5FA) else Color(0xFF34D399),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (currentUser.isProfileLocked) "Your profile is locked 🔒" else "Profile is public 🌐",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (currentUser.isProfileLocked) "Only friends can see your posts and photos." else "Anyone can see what you share on your timeline.",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = if (currentUser.isProfileLocked) "Manage ❯" else "Lock Now ❯",
                                    color = if (currentUser.isProfileLocked) Color(0xFF60A5FA) else Color(0xFF34D399),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dedicated Profile Viewers Card / Chip
                        Surface(
                            onClick = { showProfileVisitorsModal = true },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Profile Views",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${profileVisitors.size} Profile Viewers",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "See who viewed your profile recently",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "View ❯",
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Profile Stats Row (Posts | Following | Followers)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Posts
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${myPosts.size.coerceAtLeast(103)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Posts",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp),
                                color = Color(0xFF334155)
                            )

                            // Following
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${currentUser.followingCount.coerceAtLeast(870)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Following",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp),
                                color = Color(0xFF334155)
                            )

                            // Followers
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "120k",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Followers",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Stories Highlights Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-10).dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "View all",
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "All Stories Highlights", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add Story Highlight Button
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Color(0xFF475569), CircleShape)
                                    .clickable {
                                        viewModel.createStory(
                                            mediaUrl = currentUser.avatarUrl,
                                            caption = "New Highlight! ✨",
                                            backgroundColor = "#2563EB"
                                        )
                                        Toast.makeText(context, "Story Highlight Created!", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add story",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Add story",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Preset Highlights (Travel, Food, Friends, Hangout, Outing)
                    items(storyHighlights) { highlight ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Viewing highlight: ${highlight.title}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFF2563EB), CircleShape)
                            ) {
                                AsyncImage(
                                    model = highlight.coverUrl,
                                    contentDescription = highlight.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = highlight.title,
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 4. Segmented Pill Filter Control [ Post | Media ]
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // "Post" Segment Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (selectedPillTab == 0) Color(0xFF2563EB) else Color.Transparent
                                )
                                .clickable { selectedPillTab = 0 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Post",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selectedPillTab == 0) Color.White else Color(0xFF94A3B8)
                            )
                        }

                        // "Media" Segment Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (selectedPillTab == 1) Color(0xFF2563EB) else Color.Transparent
                                )
                                .clickable { selectedPillTab = 1 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Media",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selectedPillTab == 1) Color.White else Color(0xFF94A3B8)
                            )
                        }

                        // "Details" Segment Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (selectedPillTab == 2) Color(0xFF2563EB) else Color.Transparent
                                )
                                .clickable { selectedPillTab = 2 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selectedPillTab == 2) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Segment Content Area (Post Feed or Media Grid)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (selectedPillTab == 0) {
                    // Posts Feed View
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        if (myPosts.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No posts published yet 📝", color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Share your thoughts or photos with your followers!", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                }
                            }
                        } else {
                            myPosts.forEach { post ->
                                ProfilePostCleanCard(post = post, user = currentUser)
                            }
                        }
                    }
                } else if (selectedPillTab == 1) {
                    // Media Gallery Grid View
                    val mediaUrls = remember(myMediaPosts, currentUser) {
                        val urls = myMediaPosts.mapNotNull { it.mediaUrl }.toMutableList()
                        if (urls.isEmpty()) {
                            urls.addAll(
                                listOf(
                                    currentUser.coverUrl,
                                    currentUser.avatarUrl,
                                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
                                    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&auto=format&fit=crop&q=80",
                                    "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=800&auto=format&fit=crop&q=80",
                                    "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800&auto=format&fit=crop&q=80"
                                )
                            )
                        }
                        urls
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val rows = mediaUrls.chunked(2)
                        rows.forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                pair.forEach { url ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(160.dp)
                                            .clickable {
                                                editorInitialPhotoUrl = url
                                                showViewPhotoDialog = true
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                                    ) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "User Media",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    // Profile Details & Public Info View
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "About & Public Details",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Edit ❯",
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.clickable { viewModel.setEditProfileOpen(true) }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            ProfileDetailItem(
                                icon = Icons.Default.Work,
                                title = "Work",
                                value = currentUser.work.ifBlank { "Software Engineer" }
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.School,
                                title = "Education",
                                value = currentUser.education.ifBlank { "University Graduate" }
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.LocationOn,
                                title = "Lives in",
                                value = currentUser.location.ifBlank { "Colombo, Sri Lanka" }
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.Home,
                                title = "From",
                                value = currentUser.hometown.ifBlank { "Colombo, Sri Lanka" }
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.Favorite,
                                title = "Relationship",
                                value = currentUser.relationshipStatus.ifBlank { "Single" }
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.Cake,
                                title = "Birthday",
                                value = currentUser.birthday.ifBlank { "July 15" }
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.Phone,
                                title = "Phone",
                                value = currentUser.phone.ifBlank { "+94 77 123 4567" },
                                isPrivate = true
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.Email,
                                title = "Email",
                                value = currentUser.email.ifBlank { "user@friendhub.io" },
                                isPrivate = true
                            )

                            ProfileDetailItem(
                                icon = Icons.Default.Language,
                                title = "Website",
                                value = currentUser.website.ifBlank { "https://friendhub.io" },
                                isLink = true,
                                onClick = {
                                    Toast.makeText(context, "Opening ${currentUser.website.ifBlank { "https://friendhub.io" }}", Toast.LENGTH_SHORT).show()
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFF334155))
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Privacy & Security",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                if (currentUser.isProfileLocked) Color(0xFF2563EB).copy(alpha = 0.2f)
                                                else Color(0xFF22C55E).copy(alpha = 0.2f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (currentUser.isProfileLocked) Icons.Default.Shield else Icons.Default.LockOpen,
                                            contentDescription = null,
                                            tint = if (currentUser.isProfileLocked) Color(0xFF60A5FA) else Color(0xFF22C55E),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (currentUser.isProfileLocked) "Locked Profile" else "Public Profile",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = if (currentUser.isProfileLocked) "Visible to friends only" else "Visible to everyone",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                    TextButton(
                                        onClick = { showLockDialog = true },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (currentUser.isProfileLocked) "Unlock Now" else "Lock Profile",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.setEditProfileOpen(true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit Public Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // MODALS AND DIALOGS
    if (showProfileVisitorsModal) {
        ProfileVisitorsDialog(
            visitors = profileVisitors,
            onDismiss = { showProfileVisitorsModal = false },
            onViewUserProfile = { userId ->
                val visitorUser = viewModel.getUserById(userId)
                if (visitorUser != null) {
                    viewModel.openUserProfile(visitorUser)
                } else {
                    Toast.makeText(context, "Opening profile: $userId", Toast.LENGTH_SHORT).show()
                }
            },
            onSimulateVisit = {
                viewModel.simulateProfileVisitor()
                Toast.makeText(context, "New visit and notification added! 🔔", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showLockDialog) {
        ProfileLockDialog(
            isCurrentlyLocked = currentUser.isProfileLocked,
            onToggleLock = { locked ->
                viewModel.setProfileLock(locked)
                showLockDialog = false
                Toast.makeText(
                    context,
                    if (locked) "Profile locked 🔒 (Only friends can see posts)" else "Profile is now public 🔓",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { showLockDialog = false }
        )
    }

    if (showNoteDialog) {
        ProfileNoteDialog(
            initialNote = currentUser.profileNote,
            onSaveNote = { newNote ->
                viewModel.updateProfileNote(newNote)
                showNoteDialog = false
                Toast.makeText(context, "Note saved successfully! 💭", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showNoteDialog = false }
        )
    }

    if (showMoreOptionsMenu) {
        ProfileOptionsMenuDialog(
            currentUser = currentUser,
            isDarkMode = isDarkMode,
            e2eEncryptionEnabled = e2eEncryptionEnabled,
            onDismiss = { showMoreOptionsMenu = false },
            onToggleLock = {
                showMoreOptionsMenu = false
                showLockDialog = true
            },
            onOpenSettings = {
                showMoreOptionsMenu = false
                showAppSettingsModal = true
            },
            onOpenNoteDialog = {
                showMoreOptionsMenu = false
                showNoteDialog = true
            },
            onOpenFrameEditor = {
                showMoreOptionsMenu = false
                editorInitialPhotoUrl = currentUser.avatarUrl
                editorInitialFrameId = currentUser.profileFrame
                showEditorScreen = true
            },
            onChangeCoverPhoto = {
                showMoreOptionsMenu = false
                try {
                    coverPhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } catch (e: Exception) {
                    viewModel.setEditProfileOpen(true)
                }
            },
            onOpenLinkedDevices = {
                showMoreOptionsMenu = false
                showLinkedDevicesDialog = true
            },
            onToggleDarkMode = {
                viewModel.toggleDarkMode()
            },
            onToggleE2EE = {
                viewModel.toggleE2EEncryption()
            },
            onCopyProfileLink = {
                clipboardManager.setText(AnnotatedString("https://friendhub.io/user/${currentUser.username.ifBlank { currentUser.id }}"))
                Toast.makeText(context, "Profile link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                showMoreOptionsMenu = false
            },
            onEditProfile = {
                showMoreOptionsMenu = false
                viewModel.setEditProfileOpen(true)
            }
        )
    }

    if (showAppSettingsModal) {
        AppSettingsModal(
            viewModel = viewModel,
            onDismiss = { showAppSettingsModal = false }
        )
    }

    if (showLinkedDevicesDialog) {
        LinkedDevicesDialog(
            currentUser = currentUser,
            onDismiss = { showLinkedDevicesDialog = false }
        )
    }

    if (showPhotoOptionsSheet) {
        ProfilePhotoBottomSheet(
            onDismiss = { showPhotoOptionsSheet = false },
            onAddToStory = {
                viewModel.createStory(
                    mediaUrl = currentUser.avatarUrl,
                    caption = "My profile story 🌟",
                    backgroundColor = "#2563EB"
                )
                Toast.makeText(context, "Added to Story! 📖", Toast.LENGTH_SHORT).show()
            },
            onAddFrame = {
                editorInitialPhotoUrl = currentUser.avatarUrl
                editorInitialFrameId = currentUser.profileFrame
                showEditorScreen = true
            },
            onTakePhoto = {
                editorInitialPhotoUrl = currentUser.avatarUrl
                editorInitialFrameId = currentUser.profileFrame
                showEditorScreen = true
            },
            onUploadPhoto = {
                try {
                    profilePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } catch (e: Exception) {
                    editorInitialPhotoUrl = currentUser.avatarUrl
                    showEditorScreen = true
                }
            },
            onSelectFbPhoto = {
                editorInitialPhotoUrl = currentUser.avatarUrl
                showEditorScreen = true
            },
            onViewProfilePicture = {
                showViewPhotoDialog = true
            }
        )
    }

    if (showViewPhotoDialog) {
        ViewProfilePhotoDialog(
            photoUrl = editorInitialPhotoUrl.ifBlank { currentUser.avatarUrl },
            frameId = currentUser.profileFrame,
            userName = currentUser.name,
            onEditClick = {
                editorInitialPhotoUrl = currentUser.avatarUrl
                editorInitialFrameId = currentUser.profileFrame
                showEditorScreen = true
            },
            onDismiss = { showViewPhotoDialog = false }
        )
    }

    if (isEditProfileOpen) {
        ProfileEditorScreen(
            onBack = { viewModel.setEditProfileOpen(false) }
        )
    }
}

// Clean Profile Post Card Component
@Composable
fun ProfilePostCleanCard(post: Post, user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = user.avatarUrl.ifBlank { post.userAvatar },
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = user.name.ifBlank { post.userName },
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = post.timestamp.ifBlank { "Just now" },
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            if (post.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = post.content,
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            if (!post.mediaUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    AsyncImage(
                        model = post.mediaUrl,
                        contentDescription = "Post Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

// Profile Visitors Dialog
@Composable
fun ProfileVisitorsDialog(
    visitors: List<ProfileVisitor>,
    onDismiss: () -> Unit,
    onViewUserProfile: (String) -> Unit,
    onSimulateVisit: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👀", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Profile Viewers (${visitors.size})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "People who viewed your profile recently. Real-time alert notifications are generated whenever someone visits!",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Test Profile View Notification Button
                OutlinedButton(
                    onClick = onSimulateVisit,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Text("🔔 Test Profile View Notification", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visitors) { visitor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF334155), RoundedCornerShape(14.dp))
                                .clickable {
                                    onViewUserProfile(visitor.visitorId)
                                    onDismiss()
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = visitor.visitorAvatar,
                                contentDescription = visitor.visitorName,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = visitor.visitorName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${visitor.timestamp}",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = visitor.visitorBio,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    onViewUserProfile(visitor.visitorId)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("View Profile", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Profile Detail Row Item Component
@Composable
fun ProfileDetailItem(
    icon: ImageVector,
    title: String,
    value: String,
    isPrivate: Boolean = false,
    isLink: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFF334155), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(17.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
            Text(
                text = value,
                color = if (isLink) Color(0xFF38BDF8) else Color.White,
                fontSize = 13.sp,
                fontWeight = if (isLink) FontWeight.Bold else FontWeight.Medium
            )
        }
        if (isPrivate) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF22C55E).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Protected 🔒",
                    color = Color(0xFF22C55E),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Profile Option Row in More Options Menu
@Composable
fun ProfileOptionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconTint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text("❯", color = Color(0xFF64748B), fontSize = 13.sp)
    }
}

// Complete Profile Settings & Options Dialog
@Composable
fun ProfileOptionsMenuDialog(
    currentUser: User,
    isDarkMode: Boolean,
    e2eEncryptionEnabled: Boolean,
    onDismiss: () -> Unit,
    onToggleLock: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNoteDialog: () -> Unit,
    onOpenFrameEditor: () -> Unit,
    onChangeCoverPhoto: () -> Unit,
    onOpenLinkedDevices: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onToggleE2EE: () -> Unit,
    onCopyProfileLink: () -> Unit,
    onEditProfile: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = currentUser.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80" },
                            contentDescription = currentUser.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Profile Settings & Options",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = currentUser.name,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(6.dp))

                // 1. Profile Lock
                ProfileOptionRow(
                    icon = if (currentUser.isProfileLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    iconTint = if (currentUser.isProfileLocked) Color(0xFF60A5FA) else Color(0xFF38BDF8),
                    title = if (currentUser.isProfileLocked) "Unlock Profile" else "Lock Profile",
                    subtitle = if (currentUser.isProfileLocked) "Make your posts and photos public" else "Only friends can see your timeline",
                    onClick = onToggleLock
                )

                // 2. Settings & Privacy
                ProfileOptionRow(
                    icon = Icons.Default.Settings,
                    iconTint = Color(0xFF38BDF8),
                    title = "Settings & Privacy",
                    subtitle = "Account, Notifications, Privacy & Media",
                    onClick = onOpenSettings
                )

                // 3. Status Note
                ProfileOptionRow(
                    icon = Icons.Default.ChatBubbleOutline,
                    iconTint = Color(0xFFA78BFA),
                    title = "Add Status Note 💭",
                    subtitle = if (!currentUser.profileNote.isNullOrBlank()) "Current note: \"${currentUser.profileNote}\"" else "Add a short note to your avatar",
                    onClick = onOpenNoteDialog
                )

                // 4. Frames & Avatar Editor
                ProfileOptionRow(
                    icon = Icons.Default.AutoAwesome,
                    iconTint = Color(0xFFFBBF24),
                    title = "Profile Frames & Avatar",
                    subtitle = "VIP, Neon Cyber, Floral & Holographic",
                    onClick = onOpenFrameEditor
                )

                // 5. Change Cover Photo
                ProfileOptionRow(
                    icon = Icons.Default.CameraAlt,
                    iconTint = Color(0xFF34D399),
                    title = "Update Cover Photo",
                    subtitle = "Choose a new background cover",
                    onClick = onChangeCoverPhoto
                )

                // 6. Linked Devices & Security
                ProfileOptionRow(
                    icon = Icons.Default.PhoneAndroid,
                    iconTint = Color(0xFF38BDF8),
                    title = "Linked Devices & Security",
                    subtitle = "Manage sessions and E2EE security",
                    onClick = onOpenLinkedDevices
                )

                // 7. Dark Mode Toggle
                ProfileOptionRow(
                    icon = Icons.Default.DarkMode,
                    iconTint = Color(0xFFE2E8F0),
                    title = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                    subtitle = if (isDarkMode) "Dark theme enabled" else "Light theme enabled",
                    onClick = onToggleDarkMode
                )

                // 8. E2EE Encryption Toggle
                ProfileOptionRow(
                    icon = Icons.Default.Security,
                    iconTint = if (e2eEncryptionEnabled) Color(0xFF22C55E) else Color(0xFFEF4444),
                    title = "End-to-End Chat Encryption (E2EE)",
                    subtitle = if (e2eEncryptionEnabled) "AES-256-GCM hardware security enabled 🔒" else "Disabled ⚠️",
                    onClick = onToggleE2EE
                )

                // 9. Copy Profile Link
                ProfileOptionRow(
                    icon = Icons.Default.ContentCopy,
                    iconTint = Color(0xFF94A3B8),
                    title = "Copy Profile Link",
                    subtitle = "https://friendhub.io/user/${currentUser.username.ifBlank { currentUser.id }}",
                    onClick = onCopyProfileLink
                )

                // 10. Edit Public Details
                ProfileOptionRow(
                    icon = Icons.Default.Edit,
                    iconTint = Color(0xFF38BDF8),
                    title = "Edit Public Details",
                    subtitle = "Change name, bio, work, and location",
                    onClick = onEditProfile
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// Linked Devices & Active Sessions Dialog
@Composable
fun LinkedDevicesDialog(
    currentUser: User,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var sessionsTerminated by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Linked Devices", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Active Sessions & Security", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Device Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF22C55E).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Android Device (Current Device)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF22C55E).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("Active Now", color = Color(0xFF22C55E), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("FriendHub App • Colombo, Sri Lanka", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("AES-256-GCM Hardware Security Enabled", color = Color(0xFF38BDF8), fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Other Session (if not terminated)
                if (!sessionsTerminated) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0xFF475569))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF64748B).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Chrome Browser (Web Client)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Last active: 2 hours ago • IP: 112.134.x.x", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Text(
                            text = "All other sessions have been successfully terminated ✅",
                            color = Color(0xFF22C55E),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(14.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!sessionsTerminated) {
                    OutlinedButton(
                        onClick = {
                            sessionsTerminated = true
                            Toast.makeText(context, "All other devices logged out! 🔒", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout from all other devices", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
