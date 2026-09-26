package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePhotoBottomSheet(
    onDismiss: () -> Unit,
    onAddToStory: () -> Unit,
    onAddFrame: () -> Unit,
    onTakePhoto: () -> Unit,
    onUploadPhoto: () -> Unit,
    onSelectFbPhoto: () -> Unit,
    onViewProfilePicture: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF242526),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF5E6065))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 1. කතන්දරයට එක් කරන්න (Add to story)
            ProfilePhotoOptionRow(
                icon = Icons.Default.Add,
                title = "කතන්දරයට එක් කරන්න",
                subtitle = "Add to story",
                onClick = {
                    onDismiss()
                    onAddToStory()
                }
            )

            // 2. රාමුව එක් කරන්න (Add frame)
            ProfilePhotoOptionRow(
                icon = Icons.Default.CropSquare,
                title = "රාමුව එක් කරන්න",
                subtitle = "Add frame",
                onClick = {
                    onDismiss()
                    onAddFrame()
                }
            )

            // 3. ඡායාරූපය ගන්න (Take photo)
            ProfilePhotoOptionRow(
                icon = Icons.Default.CameraAlt,
                title = "ඡායාරූපය ගන්න",
                subtitle = "Take photo",
                onClick = {
                    onDismiss()
                    onTakePhoto()
                }
            )

            // 4. ඡායාරූපය උඩුගත කරන්න (Upload photo)
            ProfilePhotoOptionRow(
                icon = Icons.Default.Folder,
                title = "ඡායාරූපය උඩුගත කරන්න",
                subtitle = "Upload photo",
                onClick = {
                    onDismiss()
                    onUploadPhoto()
                }
            )

            // 5. ඇල්බම වලින් ඡායාරූපය තෝරන්න (Select photo from albums)
            ProfilePhotoOptionRow(
                icon = Icons.Default.Collections,
                title = "ඇල්බම වලින් ඡායාරූපය තෝරන්න",
                subtitle = "Select photo from albums",
                onClick = {
                    onDismiss()
                    onSelectFbPhoto()
                }
            )

            // 6. ප්‍රොෆයිල පින්තූරය බලන්න (View profile picture)
            ProfilePhotoOptionRow(
                icon = Icons.Default.AccountCircle,
                title = "ප්‍රොෆයිල පින්තූරය බලන්න",
                subtitle = "View profile picture",
                onClick = {
                    onDismiss()
                    onViewProfilePicture()
                }
            )
        }
    }
}

@Composable
private fun ProfilePhotoOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A3B3C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFE4E6EB),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE4E6EB)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFFB0B3B8)
            )
        }
    }
}
