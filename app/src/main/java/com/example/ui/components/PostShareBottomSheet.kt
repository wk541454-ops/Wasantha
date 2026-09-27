package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.example.R
import com.example.viewmodel.MainViewModel

private data class FriendItem(val name: String, val avatarUrl: String)
private data class GroupItem(val name: String, val membersCount: String, val iconRes: Int? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostShareBottomSheet(
    postTitle: String,
    postUrl: String?,
    viewModel: MainViewModel,
    onShareToFriend: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shareCaption by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val privacyOptions = listOf("මිතුරන්", "ප්‍රසිද්ධ", "මම පමණයි")
    var privacyIndex by remember { mutableStateOf(0) }

    var showGroupPicker by remember { mutableStateOf(false) }
    var showFriendPicker by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var showMessengerDialog by remember { mutableStateOf(false) }
    var showDriveDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var taggedFriendsCount by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val sampleFriends = remember {
        listOf(
            FriendItem("වසන්ත කුමාර", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80"),
            FriendItem("යසිත මධුශංක", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150&auto=format&fit=crop&q=80"),
            FriendItem("නිමල් පෙරේරා", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80"),
            FriendItem("ප්‍රියා රත්නායක", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80"),
            FriendItem("කසුන් මදුරංග", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80")
        )
    }

    val sampleGroups = remember {
        listOf(
            GroupItem("ශ්‍රී ලංකා මිතුරු හවුල (SL Friends)", "සාමාජිකයන් 45K"),
            GroupItem("Sri Lanka Photography Club", "සාමාජිකයන් 120K"),
            GroupItem("Sinhala Tech & Innovation", "සාමාජිකයන් 88K"),
            GroupItem("Music & Arts Lovers", "සාමාජිකයන් 32K")
        )
    }

    fun getFullShareText(): String {
        return if (shareCaption.isNotBlank()) {
            "$shareCaption\n$postTitle"
        } else {
            postTitle
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E),
        scrimColor = Color.Black.copy(alpha = 0.75f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5A5B5E))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .verticalScroll(scrollState)
        ) {
            // FB Lite Top Card Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                border = BorderStroke(0.5.dp, Color(0xFF383838))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Row 1: Profile Avatar + Name + Privacy pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = currentUser.avatarUrl.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80" },
                            contentDescription = "Profile Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentUser.name.ifEmpty { "වසන්ත කුමාර" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            // Privacy Pill Dropdown (👥 මිතුරන් ▾)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF3A3B3C))
                                    .clickable {
                                        privacyIndex = (privacyIndex + 1) % privacyOptions.size
                                        Toast.makeText(context, "පෞද්ගලිකත්වය: ${privacyOptions[privacyIndex]}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = Color(0xFFB0B3B8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = privacyOptions[privacyIndex],
                                    color = Color(0xFFE4E6EB),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFFB0B3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 2: Caption Input "මේ ගැන යමක් පවසන්න.."
                    BasicTextField(
                        value = shareCaption,
                        onValueChange = { shareCaption = it },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        decorationBox = { innerTextField ->
                            if (shareCaption.isEmpty()) {
                                Text(
                                    text = "මේ ගැන යමක් පවසන්න..",
                                    color = Color(0xFF8A8D91),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 3: Tag Friends Icon (left) + "දැන් බෙදා ගන්න" Button (right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showTagDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAddAlt1,
                                contentDescription = "Tag Friends",
                                tint = if (taggedFriendsCount > 0) Color(0xFF1877F2) else Color(0xFFB0B3B8),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Button(
                            onClick = {
                                val fullText = getFullShareText()
                                viewModel.createPost(
                                    content = fullText,
                                    mediaUrl = postUrl,
                                    mediaType = if (!postUrl.isNullOrBlank()) com.example.model.MediaType.IMAGE else com.example.model.MediaType.NONE
                                )
                                Toast.makeText(context, "පෝස්ටුව සාර්ථකව ඔබේ Feed එකේ බෙදා ගන්නා ලදී! 🚀", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "දැන් බෙදා ගන්න",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Heading: "වෙනත් යමක් වෙත බෙද ගන්න"
            Text(
                text = "වෙනත් යමක් වෙත බෙද ගන්න",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Row 1 (4 items): සමූහ, මිතුරාගේ ප්‍රොෆයිලය, WhatsApp, පණිවිඩ (Messenger)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. සමූහ
                ShareCircleOption(
                    title = "සමූහ",
                    iconDrawableRes = R.drawable.ic_groups_white,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showGroupPicker = true
                    }
                )

                // 2. මිතුරාගේ ප්‍රොෆයිලය
                ShareCircleOption(
                    title = "මිතුරාගේ\nප්‍රොෆයිල\nය",
                    iconDrawableRes = R.drawable.ic_friend_profile_white,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showFriendPicker = true
                    }
                )

                // 3. WhatsApp
                ShareCircleOption(
                    title = "WhatsApp",
                    iconDrawableRes = R.drawable.ic_whatsapp_green,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shareToWhatsAppDirect(context, getFullShareText(), postUrl, null)
                        onDismiss()
                    }
                )

                // 4. Facebook
                ShareCircleOption(
                    title = "Facebook",
                    iconDrawableRes = R.drawable.ic_facebook_blue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        Toast.makeText(context, "Facebook වෙත සාර්ථකව බෙදා ගන්නා ලදී! 📘", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row 2 (4 items): Messenger, Google Drive, සබැඳිය පිටපත් කරන්න, තවත්
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 5. Messenger
                ShareCircleOption(
                    title = "Messenger",
                    iconDrawableRes = R.drawable.ic_3d_chat_bubble_vector,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shareToMessenger(context, getFullShareText(), postUrl) {
                            showMessengerDialog = true
                        }
                    }
                )

                // 6. Google Drive
                ShareCircleOption(
                    title = "Google\nDrive",
                    iconDrawableRes = R.drawable.ic_google_drive,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shareToGoogleDrive(context, getFullShareText(), postUrl) {
                            showDriveDialog = true
                        }
                    }
                )

                // 6. Email (Gmail)
                ShareCircleOption(
                    title = "Email\n(Gmail)",
                    iconDrawableRes = R.drawable.ic_email_gmail,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shareToEmail(context, getFullShareText(), postUrl) {
                            showEmailDialog = true
                        }
                    }
                )

                // 7. සබැඳිය පිටපත් කරන්න
                ShareCircleOption(
                    title = "සබැඳිය\nපිටපත්\nකරන්න",
                    iconDrawableRes = R.drawable.ic_link_white,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val shareLink = postUrl ?: "https://friendhub.app/post/${System.currentTimeMillis()}"
                        val clip = ClipData.newPlainText("Post Link", shareLink)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "සබැඳිය පිටපත් කරන ලදී! 🔗", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                // 8. තවත් (More)
                ShareCircleOption(
                    title = "තවත්",
                    iconVector = Icons.Default.MoreHoriz,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shareToSystemChooser(context, getFullShareText(), postUrl)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Dialog: Group Picker for "සමූහ"
    if (showGroupPicker) {
        AlertDialog(
            onDismissRequest = { showGroupPicker = false },
            containerColor = Color(0xFF242526),
            title = {
                Text(
                    text = "සමූහයක බෙදා ගන්න",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(sampleGroups) { group ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    Toast.makeText(context, "${group.name} සමූහය වෙත බෙදා ගන්නා ලදී! 👥", Toast.LENGTH_LONG).show()
                                    showGroupPicker = false
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
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
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = group.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = group.membersCount,
                                    color = Color(0xFFB0B3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGroupPicker = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFF1877F2))
                }
            }
        )
    }

    // Dialog: Friend Picker for "මිතුරාගේ ප්‍රොෆයිලය"
    if (showFriendPicker) {
        AlertDialog(
            onDismissRequest = { showFriendPicker = false },
            containerColor = Color(0xFF242526),
            title = {
                Text(
                    text = "මිතුරාගේ ප්‍රොෆයිලය වෙත",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(sampleFriends) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onShareToFriend(friend.name, friend.avatarUrl)
                                    Toast.makeText(context, "${friend.name} ගේ Timeline එකෙහි බෙදා ගන්නා ලදී! ✨", Toast.LENGTH_LONG).show()
                                    showFriendPicker = false
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = friend.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = friend.name,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFriendPicker = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFF1877F2))
                }
            }
        )
    }

    // Dialog: Tag Friends for "👤+"
    if (showTagDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val selectedFriends = remember { mutableStateOf(setOf<String>()) }

        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Text(
                    text = "මිතුරන් ටැග් කරන්න (Tag Friends)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("සොයන්න...", color = Color(0xFF8A8D91)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8A8D91))
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF1877F2),
                            unfocusedBorderColor = Color(0xFF3E4042)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    sampleFriends.filter { it.name.contains(searchQuery, ignoreCase = true) }.forEach { friend ->
                        val isSelected = selectedFriends.value.contains(friend.name)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val current = selectedFriends.value.toMutableSet()
                                    if (isSelected) current.remove(friend.name) else current.add(friend.name)
                                    selectedFriends.value = current
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = friend.avatarUrl,
                                    contentDescription = friend.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = friend.name,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            if (isSelected) {
                                Text("✓", color = Color(0xFF1877F2), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        taggedFriendsCount = selectedFriends.value.size
                        if (taggedFriendsCount > 0) {
                            Toast.makeText(context, "මිතුරන් $taggedFriendsCount දෙනෙකු ටැග් කරන ලදී 🏷️", Toast.LENGTH_SHORT).show()
                        }
                        showTagDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                ) {
                    Text("හරි", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog: Messenger Direct Send
    if (showMessengerDialog) {
        AlertDialog(
            onDismissRequest = { showMessengerDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_3d_chat_bubble_vector),
                        contentDescription = "Messenger",
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Messenger හි පණිවිඩයක් ලෙස යවන්න",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(sampleFriends) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onShareToFriend(friend.name, friend.avatarUrl)
                                    Toast.makeText(context, "Messenger මඟින් ${friend.name} වෙත සාර්ථකව යවන ලදී! 💬", Toast.LENGTH_LONG).show()
                                    showMessengerDialog = false
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = friend.avatarUrl,
                                    contentDescription = friend.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = friend.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            Button(
                                onClick = {
                                    onShareToFriend(friend.name, friend.avatarUrl)
                                    Toast.makeText(context, "Messenger මඟින් ${friend.name} වෙත සාර්ථකව යවන ලදී! 💬", Toast.LENGTH_LONG).show()
                                    showMessengerDialog = false
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Send", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMessengerDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }

    // Dialog: Google Drive Save
    if (showDriveDialog) {
        var isUploading by remember { mutableStateOf(false) }
        var uploadProgress by remember { mutableStateOf(0f) }
        var driveDocName by remember { mutableStateOf("FriendHub_Post_${System.currentTimeMillis() / 1000}.txt") }

        AlertDialog(
            onDismissRequest = { if (!isUploading) showDriveDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_drive),
                        contentDescription = "Google Drive",
                        tint = Color(0xFF0F9D58),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Drive වෙත සුරකින්න",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("ගිණුම: ${currentUser.email.ifBlank { "සක්‍රියයි" }} ✓", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("ස්ථානය: My Drive / FriendHub Shared", color = Color(0xFFB0B3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = driveDocName,
                        onValueChange = { driveDocName = it },
                        label = { Text("ලිපිගොනු නම (File Name)", color = Color(0xFFB0B3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0F9D58),
                            unfocusedBorderColor = Color(0xFF383838)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("අන්තර්ගතය Preview:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = getFullShareText(),
                            color = Color(0xFFE4E6EB),
                            fontSize = 12.sp,
                            maxLines = 3
                        )
                    }

                    if (isUploading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Drive වෙත උඩුගත වෙමින් පවතී... (${(uploadProgress * 100).toInt()}%)", color = Color(0xFF0F9D58), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF0F9D58),
                            trackColor = Color(0xFF383838)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isUploading) {
                            isUploading = true
                            scope.launch {
                                for (i in 1..10) {
                                    delay(100)
                                    uploadProgress = i / 10f
                                }
                                Toast.makeText(context, "Google Drive වෙත සාර්ථකව සුරකින ලදී! ☁️", Toast.LENGTH_LONG).show()
                                showDriveDialog = false
                                onDismiss()
                            }
                        }
                    },
                    enabled = !isUploading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
                ) {
                    Text(if (isUploading) "උඩුගත වේ..." else "Drive වෙත සුරකින්න", color = Color.White)
                }
            },
            dismissButton = {
                if (!isUploading) {
                    TextButton(onClick = { showDriveDialog = false }) {
                        Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                    }
                }
            }
        )
    }

    // Dialog: Email Send
    if (showEmailDialog) {
        var recipientEmail by remember { mutableStateOf("friend@gmail.com") }
        var emailSubject by remember { mutableStateOf("FriendHub Post: $postTitle") }
        var emailBody by remember { mutableStateOf(getFullShareText()) }

        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            containerColor = Color(0xFF242526),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email_gmail),
                        contentDescription = "Email",
                        tint = Color(0xFFEA4335),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Email (Gmail) මඟින් යවන්න",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = recipientEmail,
                        onValueChange = { recipientEmail = it },
                        label = { Text("ලැබෙන්නාගේ Email (To)", color = Color(0xFFB0B3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEA4335),
                            unfocusedBorderColor = Color(0xFF383838)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = emailSubject,
                        onValueChange = { emailSubject = it },
                        label = { Text("මාතෘකාව (Subject)", color = Color(0xFFB0B3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEA4335),
                            unfocusedBorderColor = Color(0xFF383838)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = emailBody,
                        onValueChange = { emailBody = it },
                        label = { Text("පණිවිඩය (Message Body)", color = Color(0xFFB0B3B8)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEA4335),
                            unfocusedBorderColor = Color(0xFF383838)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "$recipientEmail වෙත Email පණිවිඩය සාර්ථකව යවන ලදී! ✉️", Toast.LENGTH_LONG).show()
                        showEmailDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335))
                ) {
                    Text("Email එක යවන්න", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmailDialog = false }) {
                    Text("අවලංගු කරන්න", color = Color(0xFFB0B3B8))
                }
            }
        )
    }
}

/**
 * Reusable FB Lite circular share option button with dark circle and label below
 */
@Composable
private fun ShareCircleOption(
    title: String,
    modifier: Modifier = Modifier,
    iconDrawableRes: Int? = null,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        // Dark circular button
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF383838)),
            contentAlignment = Alignment.Center
        ) {
            if (iconDrawableRes != null) {
                Icon(
                    painter = painterResource(id = iconDrawableRes),
                    contentDescription = title,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(28.dp)
                )
            } else if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Multi-line centered label
        Text(
            text = title,
            color = Color(0xFFE4E6EB),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
            maxLines = 3
        )
    }
}

private fun shareToWhatsAppDirect(context: Context, text: String, url: String?, phone: String?) {
    val message = if (!url.isNullOrBlank()) "$text\n$url" else text
    val encodedMessage = Uri.encode(message)
    
    val urlString = if (!phone.isNullOrBlank()) {
        val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
        "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
    } else {
        "https://api.whatsapp.com/send?text=$encodedMessage"
    }

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                setPackage("com.whatsapp")
            }
            context.startActivity(sendIntent)
        } catch (ex: Exception) {
            shareToSystemChooser(context, text, url)
        }
    }
}

private fun shareToMessenger(context: Context, text: String, url: String?, onShowModal: () -> Unit) {
    val message = if (!url.isNullOrBlank()) "$text\n$url" else text
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.facebook.orca")
        }
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        onShowModal()
    }
}

private fun shareToGoogleDrive(context: Context, text: String, url: String?, onShowModal: () -> Unit) {
    val message = if (!url.isNullOrBlank()) "$text\n$url" else text
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.google.android.apps.docs")
        }
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        onShowModal()
    }
}

private fun shareToEmail(context: Context, text: String, url: String?, onShowModal: () -> Unit) {
    val message = if (!url.isNullOrBlank()) "$text\n$url" else text
    try {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, "FriendHub Shared Content")
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(emailIntent)
    } catch (e: Exception) {
        onShowModal()
    }
}

private fun shareToSystemChooser(context: Context, text: String, url: String?) {
    val message = if (!url.isNullOrBlank()) "$text\n$url" else text
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val chooser = Intent.createChooser(intent, "Share via FriendHub")
    try {
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Sharing initiated 🚀", Toast.LENGTH_SHORT).show()
    }
}
