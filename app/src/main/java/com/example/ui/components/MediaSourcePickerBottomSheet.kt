package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.MediaType
import java.io.File
import java.io.FileOutputStream

/**
 * Utility to save captured Bitmap from camera to a cache file URI.
 */
fun saveBitmapToCache(context: Context, bitmap: Bitmap): String {
    return try {
        val file = File(context.cacheDir, "friendhub_camera_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        Uri.fromFile(file).toString()
    } catch (e: Exception) {
        ""
    }
}

/**
 * Bottom Sheet that lets users choose between:
 * 1. 📷 Take Photo with Camera (කැමරාවෙන් ඡායාරූපයක් ගන්න)
 * 2. 🖼️ Pick from Gallery (ගැලරියෙන් තෝරන්න)
 * 3. 🎥 Choose Video (වීඩියෝවක් තෝරන්න)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSourcePickerBottomSheet(
    onDismiss: () -> Unit,
    onPhotoCaptured: (uri: String) -> Unit,
    onGalleryPhotoPicked: (uri: String) -> Unit,
    onGalleryVideoPicked: (uri: String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current

    // Camera Capture Launcher (Takes photo directly from hardware/software camera)
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uriStr = saveBitmapToCache(context, bitmap)
            if (uriStr.isNotEmpty()) {
                Toast.makeText(context, "📸 කැමරා ඡායාරූපය සාර්ථකව ලබා ගන්නා ලදී!", Toast.LENGTH_SHORT).show()
                onPhotoCaptured(uriStr)
                onDismiss()
            }
        }
    }

    // Permission launcher before opening camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePhotoLauncher.launch(null)
        } else {
            Toast.makeText(context, "කැමරා අවසරය ලබා දෙන්න (Please grant Camera permission)", Toast.LENGTH_LONG).show()
        }
    }

    // Gallery Photo Picker Launcher
    val galleryPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Toast.makeText(context, "🖼️ ගැලරියෙන් ඡායාරූපය තෝරා ගන්නා ලදී!", Toast.LENGTH_SHORT).show()
            onGalleryPhotoPicked(uri.toString())
            onDismiss()
        }
    }

    // Gallery Video Picker Launcher
    val galleryVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Toast.makeText(context, "🎥 වීඩියෝව සාර්ථකව තෝරා ගන්නා ලදී!", Toast.LENGTH_SHORT).show()
            onGalleryVideoPicked(uri.toString())
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF242526),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF65676B))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "මාධ්‍ය තෝරන්න (Select Media Source)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "කැමරාව හෝ ගැලරිය භාවිතයෙන් එක් කරන්න",
                        fontSize = 12.sp,
                        color = Color(0xFFB0B3B8)
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF3A3B3C), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFF3A3B3C), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Option 1: 📷 Take Photo with Camera
            MediaSourceItemRow(
                icon = Icons.Default.CameraAlt,
                iconBg = Color(0xFF1877F2),
                title = "කැමරාව (Open Camera 📸)",
                subtitle = "කැමරාවෙන් ක්ෂණිකව ඡායාරූපයක් ගන්න",
                onClick = {
                    if (com.example.util.CameraPermissionManager.shouldRequestPermission(context)) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        takePhotoLauncher.launch(null)
                    }
                }
            )

            // Option 2: 🖼️ Choose from Gallery
            MediaSourceItemRow(
                icon = Icons.Default.PhotoLibrary,
                iconBg = Color(0xFF31A24C),
                title = "ගැලරිය (Choose from Gallery 🖼️)",
                subtitle = "ඔබගේ දුරකථනයේ ඡායාරූප ගැලරියෙන් තෝරන්න",
                onClick = {
                    galleryPhotoLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            // Option 3: 🎥 Choose Video
            MediaSourceItemRow(
                icon = Icons.Default.VideoLibrary,
                customDrawableRes = R.drawable.ic_3d_video_play,
                iconBg = Color(0xFFE41E3F),
                title = "වීඩියෝ (Choose Video / Reel 🎬)",
                subtitle = "ගැලරියෙන් වීඩියෝවක් හෝ Reel එකක් තෝරන්න",
                onClick = {
                    galleryVideoLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                    )
                }
            )
        }
    }
}

@Composable
private fun MediaSourceItemRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    customDrawableRes: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(iconBg.copy(alpha = 0.2f))
                .border(1.dp, iconBg.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (customDrawableRes != null) {
                Image(
                    painter = painterResource(id = customDrawableRes),
                    contentDescription = title,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconBg,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFFB0B3B8)
            )
        }
    }
}
