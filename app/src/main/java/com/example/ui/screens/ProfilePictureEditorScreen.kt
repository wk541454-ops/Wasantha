package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MediaType
import com.example.ui.components.ProfileFrameOverlay
import com.example.ui.components.availableProfileFrames
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePictureEditorScreen(
    initialPhotoUrl: String,
    initialFrameId: String? = null,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedPhotoUrl by remember {
        mutableStateOf(initialPhotoUrl.ifBlank { currentUser.avatarUrl })
    }
    var selectedFrameId by remember {
        mutableStateOf(initialFrameId ?: currentUser.profileFrame ?: "none")
    }
    var captionText by remember { mutableStateOf("") }
    var showPresetSelector by remember { mutableStateOf(false) }
    var showAiStudioSheet by remember { mutableStateOf(false) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUrl = uri.toString()
            Toast.makeText(context, "නව ඡායාරූපය තෝරා ගන්නා ලදී 📷", Toast.LENGTH_SHORT).show()
        }
    }

    val presetPhotos = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=600&auto=format&fit=crop&q=80"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "පැතිකඩ පින්තූරය පෙරදසුන් කරන්න",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF18191A)
                )
            )
        },
        containerColor = Color(0xFF18191A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FB Lite Circular Crop Mask Preview Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFF121212)),
                contentAlignment = Alignment.Center
            ) {
                // Background Full Image (dimmed)
                AsyncImage(
                    model = selectedPhotoUrl,
                    contentDescription = "Profile Crop Base",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Semi-transparent overlay with circular hole mask
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension * 0.42f
                    val centerOffset = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)

                    val maskPath = Path().apply {
                        fillType = PathFillType.EvenOdd
                        addRect(androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height))
                        addOval(
                            androidx.compose.ui.geometry.Rect(
                                center = centerOffset,
                                radius = radius
                            )
                        )
                    }
                    drawPath(maskPath, color = Color.Black.copy(alpha = 0.65f))
                }

                // Profile Circular Viewport with Frame Overlay
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = selectedPhotoUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Real-time Frame Overlay
                    ProfileFrameOverlay(
                        frameId = selectedFrameId,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Carousel of Frames (Matching FB Lite Screenshot 3)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableProfileFrames) { frame ->
                    val isSelected = selectedFrameId == frame.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedFrameId = frame.id }
                            .width(64.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF1877F2) else Color(0xFF3E4042),
                                    shape = CircleShape
                                )
                                .padding(if (isSelected) 2.dp else 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Mini avatar thumbnail
                            AsyncImage(
                                model = selectedPhotoUrl,
                                contentDescription = frame.nameSinhala,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                            // Mini frame overlay
                            ProfileFrameOverlay(
                                frameId = frame.id,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = frame.nameSinhala,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF1877F2) else Color(0xFFE4E6EB),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: "AI Magic 🪄", "ඡායාරූපය වෙනස් කරන්න", & "රාමුව එක් කරන්න"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AI Magic Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showAiStudioSheet = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Magic",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Magic 🪄",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                // Change Photo Button
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .clickable {
                            try {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } catch (e: Exception) {
                                showPresetSelector = !showPresetSelector
                            }
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Change Photo",
                            tint = Color(0xFFE4E6EB),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ඡායාරූපය",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE4E6EB),
                            maxLines = 1
                        )
                    }
                }

                // Add Frame Button
                Card(
                    modifier = Modifier
                        .weight(1.1f)
                        .clickable {
                            // Cycle or highlight frame
                            val currentIndex = availableProfileFrames.indexOfFirst { it.id == selectedFrameId }
                            val nextIndex = (currentIndex + 1) % availableProfileFrames.size
                            selectedFrameId = availableProfileFrames[nextIndex].id
                            Toast.makeText(
                                context,
                                "රාමුව: ${availableProfileFrames[nextIndex].nameSinhala} 🖼️",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropSquare,
                            contentDescription = "Add Frame",
                            tint = Color(0xFFE4E6EB),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "රාමුව",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE4E6EB),
                            maxLines = 1
                        )
                    }
                }
            }

            // Optional Preset Photos Quick Shelf (if user wants to pick quickly from presets)
            if (showPresetSelector) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "ඡායාරූප තෝරන්න (Select Photo):",
                        fontSize = 12.sp,
                        color = Color(0xFFB0B3B8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetPhotos) { pUrl ->
                            AsyncImage(
                                model = pUrl,
                                contentDescription = "Preset",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedPhotoUrl = pUrl
                                        showPresetSelector = false
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Caption Box: "මෙම ඡායාරූපය පිළිබඳව යම් දෙයක් පවසන්න"
            OutlinedTextField(
                value = captionText,
                onValueChange = { captionText = it },
                placeholder = {
                    Text(
                        text = "මෙම ඡායාරූපය පිළිබඳව යම් දෙයක් පවසන්න",
                        color = Color(0xFF8A8D91),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(90.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFF3E4042),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF242526),
                    unfocusedContainerColor = Color(0xFF242526)
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Big Blue Button: "යාවත්කාලීන කරන්න" (Update)
            Button(
                onClick = {
                    val updatedUser = currentUser.copy(
                        avatarUrl = selectedPhotoUrl,
                        profileFrame = if (selectedFrameId == "none") null else selectedFrameId
                    )
                    viewModel.updateUserProfile(updatedUser)

                    // If caption is provided or frame is selected, share update post to Feed
                    if (captionText.isNotBlank()) {
                        viewModel.createPost(
                            content = captionText,
                            mediaUrl = selectedPhotoUrl,
                            mediaType = MediaType.IMAGE
                        )
                    }

                    Toast.makeText(
                        context,
                        "ප්‍රොෆයිල් පින්තූරය සාර්ථකව යාවත්කාලීන කරන ලදී! 🌟",
                        Toast.LENGTH_LONG
                    ).show()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
            ) {
                Text(
                    text = "යාවත්කාලීන කරන්න",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer note: "ඔබේ ප්‍රොෆයිල පින්තූරය පොදු ය."
            Text(
                text = "ඔබේ ප්‍රොෆයිල පින්තූරය පොදු ය.",
                fontSize = 12.sp,
                color = Color(0xFF8A8D91),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        if (showAiStudioSheet) {
            com.example.ui.components.AiMagicPhotoStudioSheet(
                initialPhotoUrl = selectedPhotoUrl,
                onClose = { showAiStudioSheet = false },
                onApplyEditedPhoto = { editedUrl ->
                    selectedPhotoUrl = editedUrl
                    showAiStudioSheet = false
                }
            )
        }
    }
}
