package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ui.components.MediaSourcePickerBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditorScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var coverPhotoUri by remember { mutableStateOf<String?>(null) }
    var profilePhotoUri by remember { mutableStateOf<String?>(null) }
    
    // Bottom Sheet State
    var showMediaPicker by remember { mutableStateOf(false) }
    var mediaPickerType by remember { mutableStateOf("profile") } // "profile" or "cover"

    // Aura Theme State
    var showAuraSelector by remember { mutableStateOf(false) }
    var selectedAura by remember { mutableStateOf<Brush?>(null) }
    
    // AI Bio State
    var aiBioText by remember { mutableStateOf("🛠️ MOBILE - PELMADULLA\n\n📌 Your Trusted Mobile Repair &\nSales Center in Pelmadulla") }
    var showAiBioDialog by remember { mutableStateOf(false) }

    val defaultCover = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=2070"
    val defaultProfile = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?q=80&w=200"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ප්‍රොෆයිලය සංස්කරණය කරන්න",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF242526)
                )
            )
        },
        containerColor = Color(0xFF18191A)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background Aura Effect
            if (selectedAura != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(selectedAura!!.apply { })
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- Top Header: Cover & Profile Photos ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        // Cover Photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                                .background(Color(0xFF3A3B3C))
                                .clickable {
                                    mediaPickerType = "cover"
                                    showMediaPicker = true
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(coverPhotoUri ?: defaultCover),
                                contentDescription = "Cover Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Camera Icon for Cover Photo
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .clickable {
                                        mediaPickerType = "cover"
                                        showMediaPicker = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Edit Cover", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }

                        // Profile Photo
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF18191A))
                                    .padding(4.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(profilePhotoUri ?: defaultProfile),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            // Camera Icon for Profile Photo
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-10).dp, y = (-10).dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .clickable {
                                        mediaPickerType = "profile"
                                        showMediaPicker = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Edit Profile", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // --- 🌟 Unique Cool Features Section (Beyond FB) ---
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF242526))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "✨ PRO විශේෂාංග (FriendHub Pro)",
                            color = Color(0xFFE91E63),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // 1. AI Bio Generator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAiBioDialog = true }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color(0xFF9C27B0))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("AI මඟින් හැඳින්වීම ලියන්න", color = Color.White, fontSize = 15.sp)
                                Text("වචන කිහිපයකින් ආකර්ෂණීය Bio එකක් හදන්න", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        // 2. Profile Theme / Aura
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAuraSelector = !showAuraSelector }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Theme", tint = Color(0xFF00BCD4))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ප්‍රොෆයිල් Aura (වර්ණ තේමාව)", color = Color.White, fontSize = 15.sp)
                                Text("ප්‍රොෆයිලයේ පසුබිමට ලස්සන වර්ණයක් දෙන්න", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        // 3. Profile Anthem
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { Toast.makeText(context, "ප්‍රොෆයිල් ගීතය සැකසීමට විවෘත වේ 🎶", Toast.LENGTH_SHORT).show() }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = Color(0xFFFF9800))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ප්‍රොෆයිල් ගීතය (Profile Anthem)", color = Color.White, fontSize = 15.sp)
                                Text("ඔබේ පිටුවට එන අයට ඇහෙන්න සින්දුවක් දාන්න", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }

                    // Aura Selector UI
                    if (showAuraSelector) {
                        val styles = listOf(
                            Brush.linearGradient(listOf(Color(0xFF18191A), Color(0xFF18191A))), // Default
                            Brush.linearGradient(listOf(Color(0xFF29323C), Color(0xFF485563))), // Dark Grey
                            Brush.linearGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))), // Midnight Blue
                            Brush.linearGradient(listOf(Color(0xFF4B1248), Color(0xFFF0C27B)))  // Sunset
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            styles.forEach { brush ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(brush)
                                        .border(2.dp, if (selectedAura == brush) Color.White else Color.Transparent, CircleShape)
                                        .clickable {
                                            selectedAura = if (brush == styles[0]) null else brush
                                        }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- Standard Facebook Sections ---

                // හැඳින්වීම (Bio)
                item {
                    SectionHeader("හැඳින්වීම")
                    SectionItem(
                        icon = Icons.Default.PanTool,
                        title = "ජීවදත්ත",
                        subtitle = aiBioText,
                        onEdit = { showAiBioDialog = true }
                    )
                    SectionItem(
                        icon = Icons.Default.PushPin,
                        title = "අමුණා ලද විස්තර",
                        subtitle = "Pelmadulla, Sri Lanka",
                        onEdit = { Toast.makeText(context, "අමුණා ලද විස්තර වෙනස් කරන්න", Toast.LENGTH_SHORT).show() }
                    )
                }

                // පුද්ගලික තොරතුරු (Personal Info)
                item {
                    SectionHeader("පුද්ගලික තොරතුරු")
                    SectionItem(
                        icon = Icons.Default.LocationOn,
                        title = "Pelmadulla, Sri Lanka",
                        subtitle = "ඔබේ මිතුරන්",
                        onEdit = {}
                    )
                    SectionItem(
                        icon = Icons.Default.Home,
                        title = "Pelmadulla, Sri Lanka",
                        subtitle = "සියල්ලන්ටම විවෘත",
                        onEdit = {}
                    )
                    SectionItem(
                        icon = Icons.Default.Cake,
                        title = "නොවැම්බර් 29 • 1993",
                        subtitle = "ඔබට පමණයි (Private)",
                        onEdit = {}
                    )
                    SectionItem(
                        icon = Icons.Default.FavoriteBorder,
                        title = "ආදර සබඳතා තත්ත්වය",
                        onEdit = null
                    )
                    SectionItem(
                        icon = Icons.Default.FamilyRestroom,
                        title = "පවුල",
                        onEdit = null
                    )
                    SectionItem(
                        icon = Icons.Default.Wc,
                        title = "පුරුෂ",
                        onEdit = {}
                    )
                    SectionItem(
                        icon = Icons.Default.Language,
                        title = "භාෂා",
                        onEdit = null
                    )
                }

                // රැකියාව (Work)
                item {
                    SectionHeader("රැකියාව")
                    SectionItem(
                        icon = Icons.Default.WorkOutline,
                        title = "රැකියා අත්දැකීම",
                        onEdit = null
                    )
                }

                // අධ්‍යාපනික (Education)
                item {
                    SectionHeader("අධ්‍යාපනික")
                    SectionItem(
                        icon = Icons.Default.School,
                        title = "උසස් පාසල හෝ විද්‍යාලය",
                        onEdit = null
                    )
                }

                // විනෝදාංශ (Hobbies)
                item {
                    SectionHeader("විනෝදාංශ")
                    SectionItem(
                        icon = Icons.Default.Category,
                        title = "විනෝදාංශ",
                        onEdit = null
                    )
                }

                // රුචිකත්වයන් (Interests)
                item {
                    SectionHeader("රුචිකත්වයන්")
                    SectionItem(icon = Icons.Default.MusicNote, title = "සංගීතය", onEdit = null)
                    SectionItem(icon = Icons.Default.Tv, title = "රූපවාහිනී වැඩසටහන්", onEdit = null)
                    SectionItem(icon = Icons.Default.Movie, title = "චිත්‍රපට", onEdit = null)
                    SectionItem(icon = Icons.Default.SportsEsports, title = "ක්‍රීඩා", onEdit = null)
                    SectionItem(icon = Icons.Default.SportsScore, title = "ක්‍රීඩා කණ්ඩායම් සහ මලල ක්‍රීඩකයන්", onEdit = null)
                }

                // සංචාරය (Travel)
                item {
                    SectionHeader("සංචාරය")
                    SectionItem(icon = Icons.Default.Place, title = "ස්ථාන", onEdit = null)
                }

                // සබැඳි (Links)
                item {
                    SectionHeader("සබැඳි")
                    SectionItem(
                        icon = Icons.Default.Link,
                        title = "සබැඳි",
                        subtitle = "wa.me/message/4zdafy5qmirco1\nඔබේ මිතුරන්",
                        onEdit = {}
                    )
                }

                // සබඳතා තතු (Contact Info)
                item {
                    SectionHeader("සබඳතා තතු")
                    SectionItem(
                        icon = Icons.Default.AlternateEmail,
                        title = "සමාජ මාධ්‍ය",
                        subtitle = "tiktok.com/@wasanthakumara991\nඔබේ මිතුරන්",
                        onEdit = {}
                    )
                    SectionItem(
                        icon = Icons.Default.Phone,
                        title = "071 911 7815",
                        subtitle = "මා පමණි (Private)",
                        onEdit = {}
                    )
                    SectionItem(
                        icon = Icons.Default.Email,
                        title = "ඊමේල් ලිපිනය එක් කරන්න",
                        onEdit = null
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        // Media Picker Bottom Sheet
        if (showMediaPicker) {
            MediaSourcePickerBottomSheet(
                onDismiss = { showMediaPicker = false },
                onPhotoCaptured = { uri -> 
                    if (mediaPickerType == "cover") coverPhotoUri = uri else profilePhotoUri = uri 
                },
                onGalleryPhotoPicked = { uri -> 
                    if (mediaPickerType == "cover") coverPhotoUri = uri else profilePhotoUri = uri 
                },
                onGalleryVideoPicked = { uri -> 
                    if (mediaPickerType == "cover") coverPhotoUri = uri else profilePhotoUri = uri 
                }
            )
        }

        // AI Bio Dialog
        if (showAiBioDialog) {
            AlertDialog(
                onDismissRequest = { showAiBioDialog = false },
                containerColor = Color(0xFF242526),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color(0xFF9C27B0))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Bio Generator", color = Color.White)
                    }
                },
                text = {
                    Column {
                        Text("මූල පද (Keywords) ඇතුලත් කරන්න:", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = "Mobile Repair, Pelmadulla",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "\"🚀 Expert Mobile Repair in Pelmadulla! | 🛠️ Quality Service | 📱 Your Trusted Tech Partner.\"",
                            color = Color(0xFF4CAF50),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            aiBioText = "🚀 Expert Mobile Repair in Pelmadulla!\n🛠️ Quality Service\n📱 Your Trusted Tech Partner."
                            showAiBioDialog = false
                            Toast.makeText(context, "AI මඟින් හැඳින්වීම සාර්ථකව වෙනස් විය! ✨", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                    ) {
                        Text("Apply AI Bio")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAiBioDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Expand",
            tint = Color.White
        )
    }
}

@Composable
fun SectionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onEdit: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFB0B3B8),
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFFB0B3B8),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
        if (onEdit != null) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFFB0B3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
