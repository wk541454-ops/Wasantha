package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendOptionsBottomSheet(
    user: User,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showUnfriendConfirmDialog by remember { mutableStateOf(false) }
    var showTakeBreakDialog by remember { mutableStateOf(false) }

    if (showUnfriendConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showUnfriendConfirmDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Text(
                    text = "${user.name} මිතුරන්ගෙන් ඉවත් කරන්නද?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "ඔබ ${user.name} ඔබේ මිතුරන් ලැයිස්තුවෙන් ඉවත් කිරීමට තහවුරු කරන්න. ඔබට නැවත මිතුරු ඉල්ලීමක් යැවිය හැක.",
                    color = Color(0xFFB0B3B8),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.unfriendUser(user.id)
                        showUnfriendConfirmDialog = false
                        Toast.makeText(context, "${user.name} මිතුරන්ගෙන් ඉවත් කරන ලදී.", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                ) {
                    Text("මිතුරන්ගෙන් ඉවත් කරන්න", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnfriendConfirmDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFF2E89FF))
                }
            }
        )
    }

    if (showTakeBreakDialog) {
        AlertDialog(
            onDismissRequest = { showTakeBreakDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Text(
                    text = "${user.name} ගෙන් විවේකයක් ගන්න",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "ඔබට ඔහුගේ පෝස්ට් අඩුවෙන් දැකීමට සහ ඔබේ පෝස්ට් ඔහුගෙන් සඟවා තැබීමට හැකිය. ඔහු මිතුරන්ගෙන් ඉවත් නොවේ.",
                    color = Color(0xFFB0B3B8),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.takeBreakFromUser(user.id)
                        showTakeBreakDialog = false
                        Toast.makeText(context, "විවේකයක් ගැනීමේ සැකසුම සක්‍රිය කරන ලදී.", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                ) {
                    Text("සක්‍රිය කරන්න", color = Color(0xFF2E89FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTakeBreakDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
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
                    .background(Color(0xFF555555))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            // Option 1: Take a break (Screenshot 6)
            FriendOptionItem(
                icon = Icons.Default.Group,
                title = "විවේකයක් ගන්න",
                onClick = {
                    showTakeBreakDialog = true
                }
            )

            // Option 2: Snooze for 30 days (Screenshot 6)
            FriendOptionItem(
                icon = Icons.Default.AccessTime,
                title = if (user.isSnoozed) "තාවකාලික නැවතීම අවලංගු කරන්න" else "දින 30කට දැකීම තාවකාලිකව නවත්වන්න",
                onClick = {
                    viewModel.snoozeUser(user.id, 30)
                    Toast.makeText(
                        context,
                        if (user.isSnoozed) "තාවකාලික නැවතීම අවලංගු කරන ලදී." else "දින 30කට දැකීම තාවකාලිකව නවත්වන ලදී.",
                        Toast.LENGTH_SHORT
                    ).show()
                    onDismiss()
                }
            )

            // Option 3: Unfollow (Screenshot 6)
            FriendOptionItem(
                icon = Icons.Default.Cancel,
                title = if (user.isFollowedByMe) "හඹා යාමෙන් ඉවත් වන්න" else "හඹා යන්න",
                onClick = {
                    val followed = viewModel.toggleFollowUser(user.id)
                    val msg = if (followed) "හඹා යමින් සිටී." else "හඹා යාමෙන් ඉවත් විය."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            // Option 4: Unfriend (Screenshot 6)
            FriendOptionItem(
                icon = Icons.Default.Person,
                title = "මිතුරන්ගෙන් ඉවත් කරන්න",
                titleColor = Color(0xFFFF6B6B),
                onClick = {
                    showUnfriendConfirmDialog = true
                }
            )
        }
    }
}

@Composable
private fun FriendOptionItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A3B3C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = titleColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = titleColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
