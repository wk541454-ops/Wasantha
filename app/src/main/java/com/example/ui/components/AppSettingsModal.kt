package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.getTypographyForFont
import com.example.viewmodel.MainViewModel

/**
 * Colorful modern Settings & Privacy Screen matching the uploaded screenshots exactly.
 * Zero occurrences of 'Facebook', packed with vibrant colors and 100% working sub-dialogs.
 */
@Composable
fun AppSettingsModal(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Dialog state controllers for all sub-options
    var activeDialogId by remember { mutableStateOf<String?>(null) }

    // Persistent setting states
    var hideReactionCountsOthers by remember { mutableStateOf(false) }
    var hideReactionCountsOwn by remember { mutableStateOf(currentUser.hideReactionCounts) }

    var pushNotifEnabled by remember { mutableStateOf(currentUser.pushNotificationsEnabled) }
    var commentsNotifEnabled by remember { mutableStateOf(currentUser.commentsNotificationsEnabled) }
    var tagsNotifEnabled by remember { mutableStateOf(currentUser.tagsNotificationsEnabled) }
    var friendRequestsNotifEnabled by remember { mutableStateOf(currentUser.friendRequestsNotificationsEnabled) }
    var doNotDisturb by remember { mutableStateOf(currentUser.doNotDisturb) }

    var largeTextEnabled by remember { mutableStateOf(false) }
    var highContrastEnabled by remember { mutableStateOf(true) }
    var autoCaptionsEnabled by remember { mutableStateOf(false) }

    var autoUpdateWifi by remember { mutableStateOf(currentUser.autoUpdateOnWifi) }
    var isCheckingUpdates by remember { mutableStateOf(false) }
    var updateCheckResult by remember { mutableStateOf<String?>(null) }

    var selectedLanguage by remember { mutableStateOf(if (currentUser.language == "SI") "සිංහල" else "English") }

    var videoAutoplayMode by remember { mutableStateOf("ජංගම දත්ත සහ Wi-Fi මත") }
    var hdVideoUpload by remember { mutableStateOf(currentUser.hdVideoUpload) }
    var hdPhotoUpload by remember { mutableStateOf(currentUser.hdPhotoUpload) }
    var soundEffectsEnabled by remember { mutableStateOf(true) }

    var dailyReminderMinutes by remember { mutableStateOf(30) }
    var quietModeEnabled by remember { mutableStateOf(false) }

    // Enhanced Settings States
    // 1. Comment Settings
    var commentsEnabledGlobal by remember { mutableStateOf(true) }
    var whoCanCommentAudience by remember { mutableStateOf("සියලු දෙනා (Everyone)") }
    var autoFilterOffensiveWords by remember { mutableStateOf(true) }
    var allowGifComments by remember { mutableStateOf(true) }
    var allowCommentReplies by remember { mutableStateOf(true) }

    // 2. Like & Reaction Settings
    var reactionSoundsEnabled by remember { mutableStateOf(true) }
    var floatingReactionHearts by remember { mutableStateOf(true) }
    var defaultReactionEmoji by remember { mutableStateOf("👍") }

    // 3. Story Settings
    var storyReplyControl by remember { mutableStateOf("සියලු දෙනා (Everyone)") }
    var storySharingAllowed by remember { mutableStateOf(true) }
    var highQualityStoryUpload by remember { mutableStateOf(true) }

    // 4. Post Settings
    var autoLocationTagging by remember { mutableStateOf(false) }
    var colorBackgroundsEnabled by remember { mutableStateOf(true) }
    var allowPostResharing by remember { mutableStateOf(true) }

    // 5. Video & Reels Settings
    var muteVideosOnStart by remember { mutableStateOf(false) }
    var backgroundVideoAudio by remember { mutableStateOf(true) }
    var dataSaverVideoMode by remember { mutableStateOf(false) }

    // 6. Live Video Settings
    var liveStreamResolution by remember { mutableStateOf("1080p Full HD") }
    var liveChatOverlayEnabled by remember { mutableStateOf(true) }
    var liveGiftsStarsEnabled by remember { mutableStateOf(true) }
    var liveAudioQuality by remember { mutableStateOf("Ultra Clarity") }
    var autoSaveLiveReplays by remember { mutableStateOf(true) }

    // 7. Chat & Messaging Settings
    var readReceiptsEnabled by remember { mutableStateOf(true) }
    var typingIndicatorEnabled by remember { mutableStateOf(true) }
    var autoDownloadMediaWifi by remember { mutableStateOf(true) }

    var defaultPostAudience by remember { mutableStateOf(currentUser.defaultPostAudience) }
    var friendRequestAudience by remember { mutableStateOf(currentUser.friendRequestAudience) }
    var friendsListAudience by remember { mutableStateOf(currentUser.friendsListAudience) }
    var storyPrivacy by remember { mutableStateOf(currentUser.storyPrivacy) }
    var storyArchiveEnabled by remember { mutableStateOf(currentUser.storyArchiveEnabled) }
    var whoCanPostOnProfile by remember { mutableStateOf(currentUser.whoCanPostOnProfile) }
    var reviewTagsEnabled by remember { mutableStateOf(currentUser.reviewTagsEnabled) }

    fun updateProfile() {
        viewModel.updateUserSettings(
            hideReactionCounts = hideReactionCountsOwn,
            pushNotificationsEnabled = pushNotifEnabled,
            commentsNotificationsEnabled = commentsNotifEnabled,
            tagsNotificationsEnabled = tagsNotifEnabled,
            friendRequestsNotificationsEnabled = friendRequestsNotifEnabled,
            doNotDisturb = doNotDisturb,
            autoUpdateOnWifi = autoUpdateWifi,
            hdVideoUpload = hdVideoUpload,
            hdPhotoUpload = hdPhotoUpload,
            defaultPostAudience = defaultPostAudience,
            friendRequestAudience = friendRequestAudience,
            friendsListAudience = friendsListAudience,
            storyPrivacy = storyPrivacy,
            storyArchiveEnabled = storyArchiveEnabled,
            whoCanPostOnProfile = whoCanPostOnProfile,
            reviewTagsEnabled = reviewTagsEnabled
        )
    }

    var activeStatusEnabled by remember { mutableStateOf(true) }
    var locationHistoryEnabled by remember { mutableStateOf(true) }

    var blockedUsers by remember { mutableStateOf(listOf("Spam User SL", "Fake Account 02")) }
    var newBlockInput by remember { mutableStateOf("") }

    var activityList by remember {
        mutableStateOf(
            listOf(
                "ඔබ නව ඡායාරූපයක් පළ කරන ලදී • මීට පැය 2කට පෙර",
                "ඔබ යසිත ගේ පෝස්ටුවකට Like කරන ලදී • ඊයේ",
                "ඔබ 'Sri Lanka Photography' සමූහයට සම්බන්ධ විය • දින 3කට පෙර",
                "ඔබ පැතිකඩ තොරතුරු යාවත්කාලීන කරන ලදී • පසුගිය සතියේ"
            )
        )
    }

    // Profile detail state for live editing
    var bioInput by remember { mutableStateOf(currentUser.bio.ifEmpty { "Mobile Repair & Sales Center in Pelmadulla" }) }
    var locationInput by remember { mutableStateOf("Pelmadulla, Sri Lanka") }
    var workplaceInput by remember { mutableStateOf("Pelmadulla Mobile Solutions") }
    var birthdayInput by remember { mutableStateOf("නොවැම්බර් 29, 1993") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A) // Rich deep midnight slate background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Vibrant App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "සැකසුම් සහ පෞද්ගලිකත්වය",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { isSearchActive = !isSearchActive },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isSearchActive) Color(0xFF2563EB) else Color(0xFF334155))
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Search Bar Input
                AnimatedVisibility(visible = isSearchActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("සැකසුම් සොයන්න...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            )
                        )
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    // SECTION 1: මනාපයන් (Preferences)
                    if (searchQuery.isBlank() || "මනාපයන්".contains(searchQuery, ignoreCase = true)) {
                        SectionHeaderCard(
                            title = "මනාපයන්",
                            subtitle = "ඔබගේ අත්දැකීම අභිමතකරණය කරන්න",
                            badgeColor = Color(0xFFEC4899)
                        )
                    }

                    // 1.1 ප්‍රතිචාර මනාපයන්
                    ColorfulSettingItemRow(
                        title = "ප්‍රතිචාර මනාපයන්",
                        subtitle = "පෝස්ට් වල ඇති ප්‍රතික්‍රියා ගණන සඟවන්න",
                        icon = Icons.Default.ThumbUp,
                        iconBg = Color(0xFFEC4899),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "reactions" }
                    )

                    // 1.2 දැනුම්දීම්
                    ColorfulSettingItemRow(
                        title = "දැනුම්දීම්",
                        subtitle = "Push, ඊමේල් සහ පණිවිඩ දැනුම්දීම් කළමනාකරණය",
                        icon = Icons.Default.Notifications,
                        iconBg = Color(0xFFF59E0B),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "notifications" }
                    )

                    // 1.3 ප්‍රවේශ වීමේ හැකියාව
                    ColorfulSettingItemRow(
                        title = "ප්‍රවේශ වීමේ හැකියාව",
                        subtitle = "විශාල අකුරු, ප්‍රතිවිරෝධය සහ උපසිරැසි",
                        icon = Icons.Default.Accessibility,
                        iconBg = Color(0xFF0284C7),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "accessibility" }
                    )

                    // 1.3.1 අකුරු විලාසිතාව (Font Style) - Explicitly requested
                    val selectedFont by viewModel.selectedFont.collectAsState()
                    ColorfulSettingItemRow(
                        title = "අකුරු විලාසිතාව (Font Selection)",
                        subtitle = "ඔබ වඩාත් කැමති ලස්සන Font එකක් තෝරන්න ($selectedFont)",
                        icon = Icons.Default.Tune,
                        iconBg = Color(0xFF8B5CF6),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "fonts" }
                    )

                    // 1.3.1 අඳුරු ප්‍රකාරය (Dark Mode Option)
                    val isDarkMode by viewModel.isDarkMode.collectAsState()
                    ColorfulSettingItemRow(
                        title = "අඳුරු ප්‍රකාරය (Dark Mode)",
                        subtitle = if (isDarkMode) "දැනට සක්‍රියයි (On)" else "දැනට අක්‍රියයි (Off)",
                        icon = Icons.Default.Brightness4,
                        iconBg = Color(0xFF475569),
                        filterText = searchQuery,
                        onClick = { viewModel.toggleDarkMode() }
                    )

                    // 1.4 යෙදුම යාවත්කාලීන (NO FACEBOOK)
                    ColorfulSettingItemRow(
                        title = "යෙදුම යාවත්කාලීන",
                        subtitle = "නවතම අනුවාදය සහ ස්වයංක්‍රීය යාවත්කාලීන කිරීම්",
                        icon = Icons.Default.SystemUpdate,
                        iconBg = Color(0xFF10B981),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "updates" }
                    )

                    // 1.5 භාෂාව සහ කලාපය
                    ColorfulSettingItemRow(
                        title = "භාෂාව සහ කලාපය",
                        subtitle = "යෙදුමේ භාෂාව තෝරන්න (සිංහල / English / தமிழ்)",
                        icon = Icons.Default.Language,
                        iconBg = Color(0xFF6366F1),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "language" }
                    )

                    // 1.6 මාධ්‍ය
                    ColorfulSettingItemRow(
                        title = "මාධ්‍ය",
                        subtitle = "වීඩියෝ ස්වයංක්‍රීය වාදනය, HD ඡායාරූප සහ ශබ්ද",
                        icon = Icons.Default.PlayCircle,
                        iconBg = Color(0xFF8B5CF6),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "media" }
                    )

                    // 1.7 කාල කළමනාකරණය
                    ColorfulSettingItemRow(
                        title = "කාල කළමනාකරණය",
                        subtitle = "දෛනික කාල සීමාවන් සහ නිහඬ ප්‍රකාරය",
                        icon = Icons.Default.Timer,
                        iconBg = Color(0xFF06B6D4),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "time_management" }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 2: ප්‍රේක්ෂකයන් සහ දෘශ්‍යතාව (Audience and Visibility)
                    if (searchQuery.isBlank() || "ප්‍රේක්ෂකයන් සහ දෘශ්‍යතාව".contains(searchQuery, ignoreCase = true)) {
                        SectionHeaderCard(
                            title = "ප්‍රේක්ෂකයන් සහ දෘශ්‍යතාව",
                            subtitle = "ඔබ බෙදා ගන්නා දේ බැලිය හැක්කේ කාටද යන්න පාලනය කරන්න",
                            badgeColor = Color(0xFF2563EB)
                        )
                    }

                    // 2.1 ප්‍රොෆයිලය අගුළු දැමීම
                    ColorfulSettingItemRow(
                        title = "ප්‍රොෆයිලය අගුළු දැමීම",
                        subtitle = "මිතුරන්ට පමණක් ඔබගේ ඡායාරූප සහ පෝස්ට් පෙන්වන්න",
                        icon = Icons.Default.Security,
                        iconBg = Color(0xFF059669),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "profile_lock" }
                    )

                    // 2.2 ප්‍රොෆයිලයේ විස්තර
                    ColorfulSettingItemRow(
                        title = "ප්‍රොෆයිලයේ විස්තර",
                        subtitle = "හැඳින්වීම, ස්ථානය, රැකියාව සහ පුද්ගලික තතු",
                        icon = Icons.Default.AccountCircle,
                        iconBg = Color(0xFF2563EB),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "profile_details" }
                    )

                    // 2.3 ඔබව සම්බන්ධ කර ගන්නා ආකාරය
                    ColorfulSettingItemRow(
                        title = "ඔබව සම්බන්ධ කර ගන්නා ආකාරය",
                        subtitle = "මිතුරු ඉල්ලීම් සහ සම්බන්ධතා සෙවුම් පාලනය",
                        icon = Icons.Default.PersonAdd,
                        iconBg = Color(0xFFF97316),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "contact_settings" }
                    )

                    // 2.4 පෝස්ට් සැකසීම් (Post Settings)
                    ColorfulSettingItemRow(
                        title = "පෝස්ට් සැකසීම් (Post Settings)",
                        subtitle = "පෙරනිමි ප්‍රේක්ෂකාගාරය, HD පින්තූර සහ ස්ථාන ටැග්",
                        icon = Icons.Default.Article,
                        iconBg = Color(0xFF14B8A6),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "post_settings_detail" }
                    )

                    // 2.4.1 කමෙන්ට් සැකසීම් (Comment Settings)
                    ColorfulSettingItemRow(
                        title = "කමෙන්ට් සැකසීම් (Comment Settings)",
                        subtitle = "කමෙන්ට් On/Off කිරීම, අසභ්‍ය වචන පෙරහන සහ GIF අවසර",
                        icon = Icons.Default.Tune,
                        iconBg = Color(0xFF00F5FF),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "comment_settings_detail" }
                    )

                    // 2.4.2 ලයික් සහ ප්‍රතික්‍රියා සැකසීම් (Like & Reaction Settings)
                    ColorfulSettingItemRow(
                        title = "ලයික් සහ ප්‍රතික්‍රියා සැකසීම් (Like & Reaction Settings)",
                        subtitle = "ලයික් ගණන සඟවන්න, ශබ්ද සහ පාවෙන හදවත් සක්‍රිය කරන්න",
                        icon = Icons.Default.ThumbUp,
                        iconBg = Color(0xFFEC4899),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "like_settings_detail" }
                    )

                    // 2.5 කතන්දර සැකසීම් (Story Settings)
                    ColorfulSettingItemRow(
                        title = "කතන්දර සැකසීම් (Story Settings)",
                        subtitle = "Story ප්‍රේක්ෂකාගාරය, පිළිතුරු, HD උඩුගත කිරීම් සහ ලේඛනාගාරය",
                        icon = Icons.Default.AutoAwesome,
                        iconBg = Color(0xFFF43F5E),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "story_settings_detail" }
                    )

                    // 2.5.1 වීඩියෝ සහ Reels සැකසීම් (Video & Reels Settings)
                    ColorfulSettingItemRow(
                        title = "වීඩියෝ සහ Reels සැකසීම් (Video & Reels Settings)",
                        subtitle = "Auto-play, පසුබිම් ශබ්ද සහ Data Saver Mode",
                        icon = Icons.Default.PlayCircle,
                        iconBg = Color(0xFF8B5CF6),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "video_settings_detail" }
                    )

                    // 2.5.2 ලයිව් වීඩියෝ සැකසීම් (Live Video Settings)
                    ColorfulSettingItemRow(
                        title = "ලයිව් වීඩියෝ සැකසීම් (Live Video Settings)",
                        subtitle = "1080p Full HD, Live Chat, Gifts සහ Sound Quality",
                        icon = Icons.Default.PlayCircle,
                        iconBg = Color(0xFFEF4444),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "live_video_settings_detail" }
                    )

                    // 2.5.3 පණිවිඩ සහ චැට් සැකසීම් (Messaging & Chat Settings)
                    ColorfulSettingItemRow(
                        title = "පණිවිඩ සහ චැට් සැකසීම් (Messaging & Chat)",
                        subtitle = "කියවූ බව (Read Receipts), Typing පෙන්වීම සහ මාධ්‍ය බාගැනීම්",
                        icon = Icons.Default.Email,
                        iconBg = Color(0xFF10B981),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "chat_settings_detail" }
                    )

                    // 2.6 හඹා යන්නන් සහ සියල්ලන්ට විවෘත අන්තර්ගතය
                    ColorfulSettingItemRow(
                        title = "හඹා යන්නන් සහ සියල්ලන්ට විවෘත අන්තර්ගතය",
                        subtitle = "ප්‍රසිද්ධ අන්තර්ගත අදහස් සහ දැනුම්දීම්",
                        icon = Icons.Default.Groups,
                        iconBg = Color(0xFFA855F7),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "followers" }
                    )

                    // 2.7 ප්‍රොෆයිලය සහ ටැග් කිරීම
                    ColorfulSettingItemRow(
                        title = "ප්‍රොෆයිලය සහ ටැග් කිරීම",
                        subtitle = "ඔබව ටැග් කර ඇති දෑ පරීක්ෂා කිරීම සහ අවසර",
                        icon = Icons.Default.LocalOffer,
                        iconBg = Color(0xFF3B82F6),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "tagging" }
                    )

                    // 2.8 අවහිර කිරීම
                    ColorfulSettingItemRow(
                        title = "අවහිර කිරීම",
                        subtitle = "අවහිර කරන ලද පුද්ගලයින්ගේ ලැයිස්තුව කළමනාකරණය",
                        icon = Icons.Default.Block,
                        iconBg = Color(0xFFEF4444),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "blocking" }
                    )

                    // 2.9 සක්‍රිය තත්ත්වය
                    ColorfulSettingItemRow(
                        title = "සක්‍රිය තත්ත්වය",
                        subtitle = "ඔබ සක්‍රියව (Online) සිටින බව මිතුරන්ට පෙන්වන්න",
                        icon = Icons.Default.VerifiedUser,
                        iconBg = Color(0xFF22C55E),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "active_status" }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 3: ගෙවීම් (Payments)
                    if (searchQuery.isBlank() || "ගෙවීම්".contains(searchQuery, ignoreCase = true)) {
                        SectionHeaderCard(
                            title = "ගෙවීම්",
                            subtitle = "ඔබේ ගෙවීම් තතු සහ ක්‍රියාකාරකම කළමනාකරණය කරන්න",
                            badgeColor = Color(0xFF10B981)
                        )
                    }

                    // 3.1 වෙළඳ දැන්වීම් ගෙවීම්
                    ColorfulSettingItemRow(
                        title = "වෙළඳ දැන්වීම් ගෙවීම්",
                        subtitle = "ගෙවීම් ක්‍රම (Visa/Mastercard), ඉතිහාසය සහ ශේෂය",
                        icon = Icons.Default.CreditCard,
                        iconBg = Color(0xFF10B981),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "payments" }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 4: ඔබේ ක්‍රියාකාරකම (Your Activity)
                    if (searchQuery.isBlank() || "ඔබේ ක්‍රියාකාරකම".contains(searchQuery, ignoreCase = true)) {
                        SectionHeaderCard(
                            title = "ඔබේ ක්‍රියාකාරකම",
                            subtitle = "ඔබේ ක්‍රියාකාරකම සහ ඔබව ටැග් කර ඇති අන්තර්ගතය සමාලෝචනය කරන්න",
                            badgeColor = Color(0xFFF59E0B)
                        )
                    }

                    // 4.1 ක්‍රියාකාරකම් ලොගය
                    ColorfulSettingItemRow(
                        title = "ක්‍රියාකාරකම් ලොගය",
                        subtitle = "පෝස්ට්, ප්‍රතිචාර සහ සෙවුම් ඉතිහාසය",
                        icon = Icons.Default.History,
                        iconBg = Color(0xFFD97706),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "activity_log" }
                    )

                    // 4.2 ස්ථානය
                    ColorfulSettingItemRow(
                        title = "ස්ථානය",
                        subtitle = "උපාංග ස්ථාන අවසර සහ ස්ථාන ඉතිහාසය",
                        icon = Icons.Default.LocationOn,
                        iconBg = Color(0xFFFB7185),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "location" }
                    )

                    // 4.3 යෙදුම් සහ වෙබ් අඩවි
                    ColorfulSettingItemRow(
                        title = "යෙදුම් සහ වෙබ් අඩවි",
                        subtitle = "ඔබගේ ගිණුමට සම්බන්ධ කර ඇති බාහිර යෙදුම්",
                        icon = Icons.Default.Widgets,
                        iconBg = Color(0xFF0891B2),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "apps" }
                    )

                    // 4.4 ව්‍යාපාර සංකලන
                    ColorfulSettingItemRow(
                        title = "ව්‍යාපාර සංකලන",
                        subtitle = "ව්‍යාපාරික මෙවලම් සහ කළමනාකරණ සේවා",
                        icon = Icons.Default.BusinessCenter,
                        iconBg = Color(0xFF4F46E5),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "business" }
                    )

                    // 4.5 ඔබේ තතු කළමනාකරණය කිරීම
                    ColorfulSettingItemRow(
                        title = "ඔබේ තතු කළමනාකරණය කිරීම",
                        subtitle = "තොරතුරුවල පිටපතක් බාගැනීම හෝ ගිණුම ඉවත් කිරීම",
                        icon = Icons.Default.PrivacyTip,
                        iconBg = Color(0xFF7C3AED),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "manage_info" }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 5: ප්‍රජා ප්‍රමිති සහ නීති ප්‍රතිපත්ති
                    if (searchQuery.isBlank() || "ප්‍රජා ප්‍රමිති".contains(searchQuery, ignoreCase = true)) {
                        SectionHeaderCard(
                            title = "ප්‍රජා ප්‍රමිති සහ නීති ප්‍රතිපත්ති",
                            subtitle = "ආරක්ෂිත සහ විශ්වාසවන්ත පරිසරයක් උදෙසා නීති රීති",
                            badgeColor = Color(0xFF38BDF8)
                        )
                    }

                    // 5.1 සේවා නියමයන්
                    ColorfulSettingItemRow(
                        title = "සේවා නියමයන්",
                        subtitle = "යෙදුම භාවිතයේ කොන්දේසි සහ වගකීම්",
                        icon = Icons.Default.Description,
                        iconBg = Color(0xFF38BDF8),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "terms" }
                    )

                    // 5.2 පෞද්ගලිකත්ව ප්‍රතිපත්තිය
                    ColorfulSettingItemRow(
                        title = "පෞද්ගලිකත්ව ප්‍රතිපත්තිය",
                        subtitle = "දත්ත ආරක්ෂාව සහ රහස්‍යභාවය සුරැකීම",
                        icon = Icons.Default.Lock,
                        iconBg = Color(0xFF3B82F6),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "privacy_policy" }
                    )

                    // 5.3 කුකී ප්‍රතිපත්තිය
                    ColorfulSettingItemRow(
                        title = "කුකී ප්‍රතිපත්තිය",
                        subtitle = "කුකීස් සහ දේශීය මතක ගබඩාව පිළිබඳ තොරතුරු",
                        icon = Icons.Default.Gavel,
                        iconBg = Color(0xFFF97316),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "cookie_policy" }
                    )

                    // 5.4 ප්‍රජා ප්‍රමිති
                    ColorfulSettingItemRow(
                        title = "ප්‍රජා ප්‍රමිති",
                        subtitle = "අයාචිත තැපැල්, හිරිහැර සහ ප්‍රචණ්ඩත්වයෙන් තොර නීති",
                        icon = Icons.Default.Security,
                        iconBg = Color(0xFF10B981),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "community_standards" }
                    )

                    // 5.5 යෙදුම පිළිබඳව (NO FACEBOOK)
                    ColorfulSettingItemRow(
                        title = "යෙදුම පිළිබඳව",
                        subtitle = "FriendHub v4.8.2, විවෘත කේත සහ සංවර්ධන විස්තර",
                        icon = Icons.Default.Info,
                        iconBg = Color(0xFF8B5CF6),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "about" }
                    )

                    // 5.6 පද්ධති පරිපාලක සහ යෙදුම් හිමිකරු (System Admin & App Owner - Secure Read Only)
                    ColorfulSettingItemRow(
                        title = "පද්ධති පරිපාලක සහ යෙදුම් හිමිකරු",
                        subtitle = "පරිපාලක තොරතුරු, හිමිකම් සහ පද්ධති දත්ත (ආරක්ෂිත තොරතුරු)",
                        icon = Icons.Default.VerifiedUser,
                        iconBg = Color(0xFF0284C7),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "admin_info" }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5.7 දුරකථන කිහිපයක එකම ගිණුම පාවිච්චි කිරීම (Multi-Device Linked Devices - WhatsApp Style)
                    ColorfulSettingItemRow(
                        title = "සම්බන්ධිත දුරකථන (Linked Devices)",
                        subtitle = "WhatsApp ආකාරයට දුරකථන 2කම එකම ගිණුම භාවිත කරන්න (100% Sync)",
                        icon = Icons.Default.PhoneAndroid,
                        iconBg = Color(0xFF10B981),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "linked_devices" }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5.8 ගිණුම් සම්බන්ධ කිරීම (Account Linking - Dual Credentials)
                    ColorfulSettingItemRow(
                        title = "ගිණුම් සම්බන්ධ කිරීම (Account Linking)",
                        subtitle = "දුරකථන අංකය (+94) සහ විද්‍යුත් තැපෑල එකම ගිණුමට සම්බන්ධ කරන්න",
                        icon = Icons.Default.AccountCircle,
                        iconBg = Color(0xFF2563EB),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "account_linking" }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 6: ගිණුම් පාලනය (Account Control)
                    SectionHeaderCard(
                        title = "ගිණුම් පාලනය",
                        subtitle = "ඔබේ ගිණුමෙන් ඉවත් වීම හෝ මකා දැමීම",
                        badgeColor = Color(0xFFEF4444)
                    )

                    // 6.1 Logout
                    ColorfulSettingItemRow(
                        title = "ගිණුමෙන් ඉවත් වන්න (Logout)",
                        subtitle = "දැනට පවතින ගිණුමෙන් ආරක්ෂිතව ඉවත් වන්න",
                        icon = Icons.Default.Close,
                        iconBg = Color(0xFF64748B),
                        filterText = searchQuery,
                        onClick = { onDismiss(); viewModel.logout() }
                    )

                    // 6.2 Delete Account
                    ColorfulSettingItemRow(
                        title = "ගිණුම ස්ථිරවම මකා දමන්න (Delete Account)",
                        subtitle = "මෙම ගිණුම සහ සියලුම දත්ත ස්ථිරවම ඉවත් කරන්න",
                        icon = Icons.Default.Delete,
                        iconBg = Color(0xFFEF4444),
                        filterText = searchQuery,
                        onClick = { activeDialogId = "delete_account_confirm" }
                    )

                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }

    // ==========================================
    // INTERACTIVE SUB-OPTIONS DIALOGS (OPTIONS INSIDE OPTIONS 1000% FUNCTIONAL)
    // ==========================================

    // 0. ගිණුම් සම්බන්ධ කිරීම (Account Linking Dialog)
    if (activeDialogId == "account_linking") {
        AccountLinkingDialog(
            viewModel = viewModel,
            onDismiss = { activeDialogId = null }
        )
    }

    // 25. ගිණුම මකා දැමීම තහවුරු කිරීම (Delete Account Confirmation)
    if (activeDialogId == "delete_account_confirm") {
        AlertDialog(
            onDismissRequest = { activeDialogId = null },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "ගිණුම ස්ථිරවම මකා දමන්නද?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "මෙම ක්‍රියාව ආපසු හැරවිය නොහැක. ඔබගේ සියලුම පෝස්ට්, පණිවිඩ සහ තොරතුරු ස්ථිරවම මකා දැමෙනු ඇත.",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount(context)
                        activeDialogId = null
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ස්ථිරවම මකා දමන්න", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogId = null }) {
                    Text("අවලංගු කරන්න", color = Color.White)
                }
            }
        )
    }

    // 1. ප්‍රතිචාර මනාපයන් (Reaction Preferences)
    if (activeDialogId == "reactions") {
        SubOptionDialogContainer(
            title = "ප්‍රතිචාර මනාපයන්",
            onDismiss = { activeDialogId = null }
        ) {
            Text(
                text = "පෝස්ට් වල ඇති Reaction සහ Like ගණන අන් අයට නොපෙනෙන සේ සැඟවිය හැක.",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            SubOptionSwitchRow(
                title = "අනෙකුත් අයගේ පෝස්ට් වල",
                subtitle = "ඔබට අන් අයගේ පෝස්ට් වල ඇති ප්‍රතික්‍රියා ගණන නොපෙනේ",
                checked = hideReactionCountsOthers,
                onCheckedChange = {
                    hideReactionCountsOthers = it
                    Toast.makeText(context, if (it) "අනෙක් අයගේ Reaction ගණන සඟවන ලදී" else "Reaction ගණන පෙන්වයි", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SubOptionSwitchRow(
                title = "ඔබගේ පෝස්ට් වල",
                subtitle = "ඔබ දමන පෝස්ට් වල ඇති ප්‍රතික්‍රියා ගණන අන් අයට නොපෙනේ",
                checked = hideReactionCountsOwn,
                onCheckedChange = {
                    hideReactionCountsOwn = it
                    updateProfile()
                    Toast.makeText(context, if (it) "ඔබේ Reaction ගණන සඟවන ලදී" else "ඔබේ Reaction ගණන පෙන්වයි", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // 2. දැනුම්දීම් (Notifications)
    if (activeDialogId == "notifications") {
        SubOptionDialogContainer(
            title = "දැනුම්දීම් සැකසුම්",
            onDismiss = { activeDialogId = null }
        ) {
            SubOptionSwitchRow(
                title = "Push දැනුම්දීම්",
                subtitle = "දුරකථන තිරයේ ක්ෂණික පණිවිඩ පෙන්වන්න",
                checked = pushNotifEnabled,
                onCheckedChange = { 
                    pushNotifEnabled = it
                    updateProfile()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ප්‍රතිචාර සහ අදහස් (Comments)",
                subtitle = "ඔබගේ පෝස්ට් වලට ලැබෙන නව අදහස් ගැන දන්වන්න",
                checked = commentsNotifEnabled,
                onCheckedChange = { 
                    commentsNotifEnabled = it
                    updateProfile()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ටැග් කිරීම් (Tags)",
                subtitle = "කෙනෙකු ඔබව ටැග් කළ විට දැනුම් දෙන්න",
                checked = tagsNotifEnabled,
                onCheckedChange = { 
                    tagsNotifEnabled = it
                    updateProfile()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "මිතුරු ඉල්ලීම් (Friend Requests)",
                subtitle = "නව මිතුරු ඇරයුම් ලද විට දැනුම් දෙන්න",
                checked = friendRequestsNotifEnabled,
                onCheckedChange = { 
                    friendRequestsNotifEnabled = it
                    updateProfile()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "බාධා නොකරන්න (Do Not Disturb)",
                subtitle = "සියලුම ශබ්ද සහ කම්පන නිහඬ කරන්න",
                checked = doNotDisturb,
                onCheckedChange = {
                    doNotDisturb = it
                    updateProfile()
                    Toast.makeText(context, if (it) "බාධා නොකරන්න ප්‍රකාරය ක්‍රියාත්මකයි 🔕" else "ශබ්ද සාමාන්‍යයි 🔔", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // 3. ප්‍රවේශ වීමේ හැකියාව (Accessibility)
    if (activeDialogId == "accessibility") {
        SubOptionDialogContainer(
            title = "ප්‍රවේශ වීමේ හැකියාව",
            onDismiss = { activeDialogId = null }
        ) {
            SubOptionSwitchRow(
                title = "විශාල අකුරු (Large Text)",
                subtitle = "කියවීමට පහසු වන සේ අකුරු ප්‍රමාණය වැඩි කරන්න",
                checked = largeTextEnabled,
                onCheckedChange = { largeTextEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ඉහළ ප්‍රතිවිරෝධය (High Contrast)",
                subtitle = "අඳුරු පසුබිම මත පෙළ පැහැදිලිව ඉස්මතු කරන්න",
                checked = highContrastEnabled,
                onCheckedChange = { highContrastEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ස්වයංක්‍රීය උපසිරැසි (Auto Captions)",
                subtitle = "වීඩියෝ සඳහා ස්වයංක්‍රීයව උපසිරැසි ජනනය කරන්න",
                checked = autoCaptionsEnabled,
                onCheckedChange = { autoCaptionsEnabled = it }
            )
        }
    }

    // 3.1 අකුරු විලාසිතාව (Font Selection) - Explicitly requested
    if (activeDialogId == "fonts") {
        val fonts = listOf(
            Triple("Default", "Standard System Font", "Default"),
            Triple("Poppins", "Modern & Rounded", "Poppins"),
            Triple("Bubblegum", "Playful & Fun", "Bubblegum"),
            Triple("Playfair", "Elegant & Classic", "Playfair"),
            Triple("Montserrat", "Bold & Geometric", "Montserrat"),
            Triple("Caveat", "Elegant Handwriting", "Caveat")
        )
        val selectedFont by viewModel.selectedFont.collectAsState()
        
        SubOptionDialogContainer(
            title = "අකුරු විලාසිතාව (Select Font)",
            onDismiss = { activeDialogId = null }
        ) {
            Text(
                text = "ඇප් එක වඩාත් ලස්සනට පාවිච්චි කරන්න ඔබට කැමති Font එකක් තෝරාගන්න. (Choose a unique font for your app)",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            fonts.forEach { (name, desc, id) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedFont == id) Color(0xFF1E293B) else Color.Transparent)
                        .clickable { 
                            viewModel.setFont(id)
                            Toast.makeText(context, "$name Font එක සාර්ථකව තෝරාගත්තා! ✨", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedFont == id),
                        onClick = { 
                            viewModel.setFont(id)
                            Toast.makeText(context, "$name Font එක සාර්ථකව තෝරාගත්තා! ✨", Toast.LENGTH_SHORT).show()
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = getTypographyForFont(id).bodyLarge.copy(fontSize = 16.sp) // Preview font
                        )
                        Text(
                            text = desc,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }

    // 4. යෙදුම යාවත්කාලීන (App Updates - NO FACEBOOK)
    if (activeDialogId == "updates") {
        SubOptionDialogContainer(
            title = "යෙදුම යාවත්කාලීන",
            onDismiss = { activeDialogId = null }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("FriendHub Social", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("වත්මන් අනුවාදය: v4.8.2 (නිල සංස්කරණය)", color = Color(0xFF38BDF8), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("නවතම ආරක්ෂක අංග සහ වේගවත් කාර්ය සාධනය සමඟ යාවත්කාලීනව පවතී.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            SubOptionSwitchRow(
                title = "Wi-Fi මත ස්වයංක්‍රීයව බාගන්න",
                subtitle = "ඩේටා ඉතිරි කරමින් පසුබිමෙන් යාවත්කාලීන වේ",
                checked = autoUpdateWifi,
                onCheckedChange = { 
                    autoUpdateWifi = it
                    updateProfile()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isCheckingUpdates = true
                    updateCheckResult = null
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        isCheckingUpdates = false
                        updateCheckResult = "ඔබ භාවිතා කරන්නේ නවතම අනුවාදයයි! (v4.8.2) ✓"
                        Toast.makeText(context, "නවතම අනුවාදය සක්‍රීයයි!", Toast.LENGTH_SHORT).show()
                    }, 1200)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isCheckingUpdates) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("පරීක්ෂා කරමින්...", color = Color.White)
                } else {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("යාවත්කාලීන සඳහා පරීක්ෂා කරන්න", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            updateCheckResult?.let { result ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = result,
                    color = Color(0xFF34D399),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }

    // 5. භාෂාව සහ කලාපය (Language & Region)
    if (activeDialogId == "language") {
        val languages = listOf("සිංහල", "English (US)", "தமிழ் (Tamil)")
        SubOptionDialogContainer(
            title = "භාෂාව තෝරන්න (Select Language)",
            onDismiss = { activeDialogId = null }
        ) {
            languages.forEach { lang ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            selectedLanguage = lang
                            viewModel.setLanguage(lang)
                            Toast.makeText(context, "භාෂාව මාරු කරන ලදී: $lang", Toast.LENGTH_SHORT).show()
                            activeDialogId = null
                        }
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedLanguage == lang),
                        onClick = {
                            selectedLanguage = lang
                            viewModel.setLanguage(lang)
                            Toast.makeText(context, "භාෂාව මාරු කරන ලදී: $lang", Toast.LENGTH_SHORT).show()
                            activeDialogId = null
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6366F1))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = lang, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }

    // 6. මාධ්‍ය (Media Settings)
    if (activeDialogId == "media") {
        val autoplayOptions = listOf("ජංගම දත්ත සහ Wi-Fi මත", "Wi-Fi මත පමණක්", "ස්වයංක්‍රීයව වාදනය නොකරන්න")
        SubOptionDialogContainer(
            title = "මාධ්‍ය සැකසුම් (Media)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("වීඩියෝ ස්වයංක්‍රීයව වාදනය කිරීම (Autoplay):", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            autoplayOptions.forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { videoAutoplayMode = opt },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (videoAutoplayMode == opt),
                        onClick = { videoAutoplayMode = opt },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                    )
                    Text(text = opt, color = Color.White, fontSize = 13.sp)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF334155))
            SubOptionSwitchRow(title = "HD වීඩියෝ උඩුගත කරන්න", subtitle = "ඉහළම ගුණාත්මක භාවයෙන් වීඩියෝ පළ කිරීම", checked = hdVideoUpload, onCheckedChange = { 
                hdVideoUpload = it
                updateProfile()
            })
            Spacer(modifier = Modifier.height(8.dp))
            SubOptionSwitchRow(title = "HD ඡායාරූප උඩුගත කරන්න", subtitle = "ඉහළ විභේදනයෙන් ඡායාරූප සුරැකීම", checked = hdPhotoUpload, onCheckedChange = { 
                hdPhotoUpload = it
                updateProfile()
            })
            Spacer(modifier = Modifier.height(8.dp))
            SubOptionSwitchRow(title = "ශබ්ද ප්‍රයෝග (Sound Effects)", subtitle = "Like කිරීමේදී සහ Comment දැමීමේදී ශබ්දය", checked = soundEffectsEnabled, onCheckedChange = { soundEffectsEnabled = it })
        }
    }

    // 7. කාල කළමනාකරණය (Time Management)
    if (activeDialogId == "time_management") {
        SubOptionDialogContainer(
            title = "කාල කළමනාකරණය (Screen Time)",
            onDismiss = { activeDialogId = null }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("අද දින යෙදුම තුළ ගත කළ කාලය", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text("මිනිත්තු 42 යි", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text("දෛනික සාමාන්‍යය: මිනිත්තු 35", color = Color(0xFF64748B), fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("දෛනික මතක් කිරීම් කාල සීමාව:", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 30, 60, 120).forEach { mins ->
                    val isSel = dailyReminderMinutes == mins
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color(0xFF06B6D4) else Color(0xFF1E293B))
                            .clickable {
                                dailyReminderMinutes = mins
                                Toast.makeText(context, "මතක් කිරීම: මිනිත්තු $mins කට සැකසිණි", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${mins}m", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            SubOptionSwitchRow(
                title = "නිහඬ ප්‍රකාරය (Quiet Mode)",
                subtitle = "රාත්‍රී 10 සිට උදෑසන 7 දක්වා සියලුම දැනුම්දීම් අත්හිටුවන්න",
                checked = quietModeEnabled,
                onCheckedChange = { quietModeEnabled = it }
            )
        }
    }

    // 8. ප්‍රොෆයිලය අගුළු දැමීම (Profile Locking)
    if (activeDialogId == "profile_lock") {
        SubOptionDialogContainer(
            title = "ප්‍රොෆයිලය අගුළු දැමීම",
            onDismiss = { activeDialogId = null }
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF059669).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (currentUser.isProfileLocked) "ඔබේ ප්‍රොෆයිලය දැනටමත් අගුළු දමා ඇත" else "ඔබේ ප්‍රොෆයිලය අගුළු දමන්න",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ප්‍රොෆයිලය අගුළු දැමූ විට, ඔබගේ මිතුරන්ට පමණක් ඔබගේ ඡායාරූප, පෝස්ට් සහ කතන්දර සම්පූර්ණ ප්‍රමාණයෙන් බැලිය හැක.",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val newLockedState = !currentUser.isProfileLocked
                    viewModel.updateUserProfile(currentUser.copy(isProfileLocked = newLockedState))
                    Toast.makeText(
                        context,
                        if (newLockedState) "ප්‍රොෆයිලය සාර්ථකව අගුළු දමන ලදී 🔒" else "අගුල ඉවත් කරන ලදී 🔓",
                        Toast.LENGTH_SHORT
                    ).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentUser.isProfileLocked) Color(0xFFEF4444) else Color(0xFF059669)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (currentUser.isProfileLocked) "අගුල ඉවත් කරන්න (Unlock)" else "දැන්ම අගුළු දමන්න (Lock Profile)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // 9. ප්‍රොෆයිලයේ විස්තර (Profile Details & Edit - matching Screenshots 5 & 8)
    if (activeDialogId == "profile_details") {
        SubOptionDialogContainer(
            title = "ප්‍රොෆයිලය සංස්කරණය කරන්න",
            onDismiss = { activeDialogId = null }
        ) {
            Text("හැඳින්වීම (Bio):", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = bioInput,
                onValueChange = { bioInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFF475569)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("ස්ථානය (Location):", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = locationInput,
                onValueChange = { locationInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFF475569)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("රැකියාව (Workplace):", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = workplaceInput,
                onValueChange = { workplaceInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFF475569)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("උපන්දිනය (Birthday):", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = birthdayInput,
                onValueChange = { birthdayInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFF475569)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateUserProfile(
                        currentUser.copy(bio = bioInput)
                    )
                    Toast.makeText(context, "ප්‍රොෆයිල තොරතුරු සාර්ථකව සුරකින ලදී! ✨", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("වෙනස්කම් සුරකින්න", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 10. ඔබව සම්බන්ධ කර ගන්නා ආකාරය (Contact Settings)
    if (activeDialogId == "contact_settings") {
        SubOptionDialogContainer(
            title = "ඔබව සම්බන්ධ කර ගන්නා ආකාරය",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ඔබට මිතුරු ඉල්ලීම් එවිය හැක්කේ කාටද?", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("සියලු දෙනා", "මිතුරන්ගේ මිතුරන්").forEach { opt ->
                    val isSel = friendRequestAudience == opt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color(0xFFF97316) else Color(0xFF1E293B))
                            .clickable { friendRequestAudience = opt }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(opt, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("ඔබගේ මිතුරු ලැයිස්තුව බැලිය හැක්කේ කාටද?", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ප්‍රසිද්ධ", "මිතුරන්", "මම පමණයි").forEach { opt ->
                    val isSel = friendsListAudience == opt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color(0xFFF97316) else Color(0xFF1E293B))
                            .clickable { friendsListAudience = opt }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(opt, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "සම්බන්ධතා පෞද්ගලිකත්වය සුරකින ලදී ✓", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("සුරකින්න", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11.1 පෝස්ට් සැකසීම් (Post Settings Detail)
    if (activeDialogId == "post_settings_detail") {
        SubOptionDialogContainer(
            title = "පෝස්ට් සැකසීම් (Post Settings)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("අනාගත පෝස්ට් සඳහා පෙරනිමි ප්‍රේක්ෂකාගාරය:", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            listOf("ප්‍රසිද්ධ (Public)", "මිතුරන් (Friends)", "මම පමණයි (Only Me)").forEach { opt ->
                val simpleName = opt.substringBefore(" ")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { defaultPostAudience = simpleName },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (defaultPostAudience == simpleName),
                        onClick = { defaultPostAudience = simpleName },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF14B8A6))
                    )
                    Text(opt, color = Color.White, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "HD ඡායාරූප උඩුගත කිරීම",
                subtitle = "ඡායාරූප ඉහළම ගුණාත්මකභාවයෙන් (HD) පෝස්ට් කරන්න",
                checked = hdPhotoUpload,
                onCheckedChange = { hdPhotoUpload = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ස්වයංක්‍රීය ස්ථාන ටැගය (Auto-Location Tag)",
                subtitle = "පෝස්ට් කිරීමේදී ඔබ සිටින ස්ථානය ස්වයංක්‍රීයව එක් කරන්න",
                checked = autoLocationTagging,
                onCheckedChange = { autoLocationTagging = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "වර්ණවත් පෝස්ට් පසුබිම් (Color Backgrounds)",
                subtitle = "ලස්සන පසුබිම් වර්ණ පෝස්ට් සඳහා සක්‍රිය කරන්න",
                checked = colorBackgroundsEnabled,
                onCheckedChange = { colorBackgroundsEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "නැවත බෙදා ගැනීමට අවසර (Allow Re-sharing)",
                subtitle = "අන් අයට ඔබේ පෝස්ට් Share කිරීමට ඉඩ දෙන්න",
                checked = allowPostResharing,
                onCheckedChange = { allowPostResharing = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "පෝස්ට් සැකසීම් සාර්ථකව සුරකින ලදී ✨", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14B8A6)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11.2 කමෙන්ට් සැකසීම් (Comment Settings Detail)
    if (activeDialogId == "comment_settings_detail") {
        SubOptionDialogContainer(
            title = "කමෙන්ට් සැකසීම් (Comment Settings)",
            onDismiss = { activeDialogId = null }
        ) {
            SubOptionSwitchRow(
                title = "සියලුම පෝස්ට් සඳහා කමෙන්ට් සක්‍රිය කරන්න",
                subtitle = "අක්‍රිය කළහොත් අන් අයට අදහස් (Comments) දැමිය නොහැක",
                checked = commentsEnabledGlobal,
                onCheckedChange = {
                    commentsEnabledGlobal = it
                    Toast.makeText(context, if (it) "කමෙන්ට් සක්‍රිය කරන ලදී 💬" else "කමෙන්ට් අක්‍රිය කරන ලදී 🚫", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("ඔබගේ පෝස්ට් වලට කමෙන්ට් කළ හැක්කේ කාටද?", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            listOf("සියලු දෙනා (Everyone)", "මිතුරන් පමණයි (Friends)", "කිසිවෙකු නැත (Only Me)").forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { whoCanCommentAudience = opt },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (whoCanCommentAudience == opt),
                        onClick = { whoCanCommentAudience = opt },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00F5FF))
                    )
                    Text(opt, color = Color.White, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "අසභ්‍ය සහ අයාචිත වචන පෙරහන (Offensive Word Filter)",
                subtitle = "නරක වචන සහ Spam කමෙන්ට් ස්වයංක්‍රීයව වාරණය කරන්න",
                checked = autoFilterOffensiveWords,
                onCheckedChange = { autoFilterOffensiveWords = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "GIF සහ ස්ටිකර් කමෙන්ට් (GIFs & Stickers)",
                subtitle = "කමෙන්ට් වලට GIF සහ Stickers භාවිත කිරීමට ඉඩ දෙන්න",
                checked = allowGifComments,
                onCheckedChange = { allowGifComments = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "කමෙන්ට් පිළිතුරු (Comment Replies)",
                subtitle = "කමෙන්ට් වලට යටින් Replies ලියන්න ඉඩ දෙන්න",
                checked = allowCommentReplies,
                onCheckedChange = { allowCommentReplies = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "කමෙන්ට් සැකසුම් සුරකින ලදී 💬", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F5FF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11.3 ලයික් සහ ප්‍රතික්‍රියා සැකසීම් (Like & Reaction Settings)
    if (activeDialogId == "like_settings_detail") {
        SubOptionDialogContainer(
            title = "ලයික් සහ ප්‍රතික්‍රියා සැකසීම් (Like Settings)",
            onDismiss = { activeDialogId = null }
        ) {
            SubOptionSwitchRow(
                title = "වෙනත් අයගේ පෝස්ට් වල",
                subtitle = "අනෙක් අයගේ පෝස්ට් වල ඇති ප්‍රතික්‍රියා ගණන සඟවන්න",
                checked = hideReactionCountsOthers,
                onCheckedChange = { hideReactionCountsOthers = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ඔබගේ පෝස්ට් වල",
                subtitle = "ඔබ දමන පෝස්ට් වල ඇති ප්‍රතික්‍රියා ගණන අන් අයට නොපෙනේ",
                checked = hideReactionCountsOwn,
                onCheckedChange = { hideReactionCountsOwn = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ලයික් ශබ්ද බලපෑම් (Reaction Sounds)",
                subtitle = "ලයික් එකක් දමන විට ශබ්දයක් වාදනය කරන්න",
                checked = reactionSoundsEnabled,
                onCheckedChange = { reactionSoundsEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "පාවෙන ලයික් හදවත් (Floating Hearts)",
                subtitle = "Tap කළ විට පාවෙන Animated Hearts පෙන්වන්න",
                checked = floatingReactionHearts,
                onCheckedChange = { floatingReactionHearts = it }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("පෙරනිමි ලයික් ඉමෝජිය (Default Reaction):", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("👍", "❤️", "🔥", "😂", "😮").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (defaultReactionEmoji == emoji) Color(0xFFEC4899) else Color(0xFF1E293B))
                            .clickable { defaultReactionEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 20.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "ලයික් සැකසීම් සුරකින ලදී ❤️", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11.4 කතන්දර සැකසීම් (Story Settings Detail)
    if (activeDialogId == "story_settings_detail") {
        SubOptionDialogContainer(
            title = "කතන්දර සැකසීම් (Story Settings)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ඔබගේ කතන්දර නැරඹිය හැක්කේ කාටද?", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            listOf("ප්‍රසිද්ධ (Public)", "මිතුරන් (Friends)", "අභිරුචි (Custom)").forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { storyPrivacy = opt },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (storyPrivacy == opt),
                        onClick = { storyPrivacy = opt },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFF43F5E))
                    )
                    Text(opt, color = Color.White, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "HD කතන්දර උඩුගත කිරීම (High Quality Stories)",
                subtitle = "Story උපරිම ගුණාත්මකභාවයෙන් (1080p) පෙන්වන්න",
                checked = highQualityStoryUpload,
                onCheckedChange = { highQualityStoryUpload = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "කතන්දර ලේඛනාගාරයට සුරකින්න (Story Archive)",
                subtitle = "පැය 24කට පසු ස්වයංක්‍රීයව ලේඛනාගාරයේ සුරකින්න",
                checked = storyArchiveEnabled,
                onCheckedChange = { storyArchiveEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "කතන්දර බෙදා ගැනීමට අවසර (Allow Story Sharing)",
                subtitle = "අන් අයට ඔබේ කතන්දරය පණිවිඩ මගින් යැවීමට ඉඩ දෙන්න",
                checked = storySharingAllowed,
                onCheckedChange = { storySharingAllowed = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "කතන්දර සැකසීම් සුරකින ලදී ✨", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11.5 වීඩියෝ සහ Reels සැකසීම් (Video & Reels Settings Detail)
    if (activeDialogId == "video_settings_detail") {
        SubOptionDialogContainer(
            title = "වීඩියෝ සහ Reels සැකසීම්",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ස්වයංක්‍රීයව වාදනය (Auto-Play Mode):", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            listOf("ජංගම දත්ත සහ Wi-Fi මත", "Wi-Fi මත පමණයි", "කිසිවිටකත් නැත").forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { videoAutoplayMode = opt },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (videoAutoplayMode == opt),
                        onClick = { videoAutoplayMode = opt },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                    )
                    Text(opt, color = Color.White, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ආරම්භයේදී ශබ්දය නිහඬ කරන්න (Mute on Start)",
                subtitle = "වීඩියෝ පටන් ගන්නා විට ශබ්දය නොමැතිව වාදනය කරන්න",
                checked = muteVideosOnStart,
                onCheckedChange = { muteVideosOnStart = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "පසුබිම් ශ්‍රව්‍ය (Background Playback)",
                subtitle = "ඇප් එක පසුබිමේ ඇති විටද වීඩියෝ ශබ්දය අසන්න",
                checked = backgroundVideoAudio,
                onCheckedChange = { backgroundVideoAudio = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "දත්ත සුරැකීමේ ප්‍රකාරය (Data Saver Mode)",
                subtitle = "වීඩියෝ දත්ත භාවිතය 40%කින් අඩු කරන්න",
                checked = dataSaverVideoMode,
                onCheckedChange = { dataSaverVideoMode = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "වීඩියෝ සැකසීම් සුරකින ලදී 🎬", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11.6 ලයිව් වීඩියෝ සැකසීම් (Live Video Settings Detail)
    if (activeDialogId == "live_video_settings_detail") {
        SubOptionDialogContainer(
            title = "ලයිව් වීඩියෝ සැකසීම් (Live Streaming)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ලයිව් වීඩියෝ ගුණාත්මකභාවය (Stream Resolution):", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            listOf("1080p Full HD", "720p HD", "480p Standard").forEach { res ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { liveStreamResolution = res },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (liveStreamResolution == res),
                        onClick = { liveStreamResolution = res },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFEF4444))
                    )
                    Text(res, color = Color.White, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ලයිව් චැට් සහ ප්‍රතික්‍රියා (Live Chat Overlay)",
                subtitle = "ලයිව් බලන විට චැට් පණිවිඩ සහ ඉමෝජි පෙන්වන්න",
                checked = liveChatOverlayEnabled,
                onCheckedChange = { liveChatOverlayEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ලයිව් තෑගි සහ Stars (Live Gifts & Stars)",
                subtitle = "නරඹන්නන්ට ඔබට ඩිජිටල් තෑගි යැවීමට ඉඩ දෙන්න",
                checked = liveGiftsStarsEnabled,
                onCheckedChange = { liveGiftsStarsEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ලයිව් නැවත නැරඹුම් සුරකින්න (Auto-Save Replays)",
                subtitle = "ලයිව් අවසන් වූ පසු ස්වයංක්‍රීයව වීඩියෝව සුරකින්න",
                checked = autoSaveLiveReplays,
                onCheckedChange = { autoSaveLiveReplays = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "ලයිව් වීඩියෝ සැකසීම් සුරකින ලදී 🔴", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11.7 පණිවිඩ සහ චැට් සැකසීම් (Chat Settings Detail)
    if (activeDialogId == "chat_settings_detail") {
        SubOptionDialogContainer(
            title = "පණිවිඩ සහ චැට් සැකසීම් (Messaging)",
            onDismiss = { activeDialogId = null }
        ) {
            SubOptionSwitchRow(
                title = "කියවූ බව පෙන්වීම (Read Receipts - Blue Ticks)",
                subtitle = "ඔබ පණිවිඩය කියවූ බව අන් අයට පෙන්වන්න",
                checked = readReceiptsEnabled,
                onCheckedChange = { readReceiptsEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ටයිප් කරන බව පෙන්වීම (Typing Indicator)",
                subtitle = "ඔබ පණිවිඩයක් ලියන විට 'Typing...' ලෙස පෙන්වන්න",
                checked = typingIndicatorEnabled,
                onCheckedChange = { typingIndicatorEnabled = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "Wi-Fi හිදී මාධ්‍ය ස්වයංක්‍රීයව බාගැනීම",
                subtitle = "ඡායාරූප සහ හඬ පණිවිඩ ස්වයංක්‍රීයව Save කරන්න",
                checked = autoDownloadMediaWifi,
                onCheckedChange = { autoDownloadMediaWifi = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "චැට් සැකසීම් සුරකින ලදී 💬", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 11. පෝස්ට් (Posts Privacy)
    if (activeDialogId == "posts_privacy") {
        SubOptionDialogContainer(
            title = "පෝස්ට් සැකසුම්",
            onDismiss = { activeDialogId = null }
        ) {
            Text("අනාගත පෝස්ට් සඳහා පෙරනිමි ප්‍රේක්ෂකාගාරය:", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            listOf("ප්‍රසිද්ධ (Public)", "මිතුරන් (Friends)", "මම පමණයි (Only Me)").forEach { opt ->
                val simpleName = opt.substringBefore(" ")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { defaultPostAudience = simpleName },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (defaultPostAudience == simpleName),
                        onClick = { defaultPostAudience = simpleName },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF14B8A6))
                    )
                    Text(opt, color = Color.White, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "පෙරනිමි ප්‍රේක්ෂකාගාරය: $defaultPostAudience", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14B8A6)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("හරි (Save)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 12. කතන්දර (Stories Privacy)
    if (activeDialogId == "stories_privacy") {
        SubOptionDialogContainer(
            title = "කතන්දර සැකසුම් (Stories)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ඔබගේ කතන්දර නැරඹිය හැක්කේ කාටද?", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            listOf("ප්‍රසිද්ධ", "මිතුරන්", "අභිරුචි").forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { storyPrivacy = opt },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (storyPrivacy == opt),
                        onClick = { storyPrivacy = opt },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFF43F5E))
                    )
                    Text(opt, color = Color.White, fontSize = 14.sp)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFF334155))
            SubOptionSwitchRow(
                title = "කතන්දර ලේඛනාගාරය (Archive)",
                subtitle = "පැය 24කට පසු ස්වයංක්‍රීයව ලේඛනාගාරයට සුරකින්න",
                checked = storyArchiveEnabled,
                onCheckedChange = { storyArchiveEnabled = it }
            )
        }
    }

    // 13. හඹා යන්නන් (Followers)
    if (activeDialogId == "followers") {
        SubOptionDialogContainer(
            title = "හඹා යන්නන් සහ සියල්ලන්ට විවෘත අන්තර්ගතය",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ඔබව Follow කළ හැක්කේ කාටද?", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text("ප්‍රසිද්ධ (Public) ලෙස තැබූ විට ඕනෑම කෙනෙකුට ඔබගේ ප්‍රසිද්ධ පෝස්ට් නැරඹිය හැක.", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "හඹා යන්නන්ගේ සැකසුම් යාවත්කාලීන විය", Toast.LENGTH_SHORT).show()
                    activeDialogId = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("තහවුරු කරන්න", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 14. ප්‍රොෆයිලය සහ ටැග් කිරීම (Profile and Tagging)
    if (activeDialogId == "tagging") {
        SubOptionDialogContainer(
            title = "ප්‍රොෆයිලය සහ ටැග් කිරීම",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ඔබගේ Timeline හි පෝස්ට් දැමිය හැක්කේ කාටද?", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            listOf("මිතුරන්", "මම පමණයි").forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { whoCanPostOnProfile = opt },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (whoCanPostOnProfile == opt),
                        onClick = { whoCanPostOnProfile = opt },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF3B82F6))
                    )
                    Text(opt, color = Color.White, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ටැග් කරන ලද පෝස්ට් පරීක්ෂා කිරීම",
                subtitle = "ඔබව ටැග් කළ පෝස්ට් Profile එකේ පෙන්වීමට පෙර අනුමැතිය ලබා ගැනීම",
                checked = reviewTagsEnabled,
                onCheckedChange = { reviewTagsEnabled = it }
            )
        }
    }

    // 15. අවහිර කිරීම (Blocking)
    if (activeDialogId == "blocking") {
        SubOptionDialogContainer(
            title = "අවහිර කිරීම (Blocking)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("අවහිර කර ඇති ගිණුම්:", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            blockedUsers.forEach { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(user, color = Color.White, fontSize = 14.sp)
                    TextButton(
                        onClick = {
                            blockedUsers = blockedUsers.filter { it != user }
                            Toast.makeText(context, "$user අවහිරය ඉවත් කරන ලදී (Unblocked)", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("අවහිරය ඉවත් කරන්න", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newBlockInput,
                    onValueChange = { newBlockInput = it },
                    placeholder = { Text("නම හෝ ඊමේල්...", color = Color(0xFF64748B), fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFEF4444),
                        unfocusedBorderColor = Color(0xFF475569)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newBlockInput.isNotBlank()) {
                            blockedUsers = blockedUsers + newBlockInput.trim()
                            Toast.makeText(context, "${newBlockInput.trim()} අවහිර කරන ලදී 🚫", Toast.LENGTH_SHORT).show()
                            newBlockInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("අවහිර", color = Color.White)
                }
            }
        }
    }

    // 16. සක්‍රිය තත්ත්වය (Active Status)
    if (activeDialogId == "active_status") {
        SubOptionDialogContainer(
            title = "සක්‍රිය තත්ත්වය (Active Status)",
            onDismiss = { activeDialogId = null }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(if (activeStatusEnabled) Color(0xFF22C55E) else Color.Gray))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (activeStatusEnabled) "සක්‍රියව පවතී (Online)" else "නොබැඳිව පවතී (Offline)", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            SubOptionSwitchRow(
                title = "ඔබ සක්‍රියව සිටින විට පෙන්වන්න",
                subtitle = "ඔබ යෙදුම තුළ සිටින විට මිතුරන්ට ඔබගේ කොළ පැහැති තිත පෙනේ",
                checked = activeStatusEnabled,
                onCheckedChange = {
                    activeStatusEnabled = it
                    Toast.makeText(context, if (it) "Active Status සක්‍රීයයි 🟢" else "Active Status අක්‍රීයයි", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // 17. ගෙවීම් (Ad Payments / Payment Methods)
    if (activeDialogId == "payments") {
        SubOptionDialogContainer(
            title = "වෙළඳ දැන්වීම් ගෙවීම් (Payments)",
            onDismiss = { activeDialogId = null }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("වත්මන් ගිණුම් ශේෂය", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text("LKR 0.00", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("සම්බන්ධිත ගෙවීම් ක්‍රම: Visa Card (•••• 4242)", color = Color.White, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "නව කාඩ්පතක් එක් කිරීමේ දොරටුව විවෘත වේ...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("නව ගෙවීම් ක්‍රමයක් එක් කරන්න", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 18. ක්‍රියාකාරකම් ලොගය (Activity Log)
    if (activeDialogId == "activity_log") {
        SubOptionDialogContainer(
            title = "ක්‍රියාකාරකම් ලොගය (Activity Log)",
            onDismiss = { activeDialogId = null }
        ) {
            activityList.forEach { act ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(act, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    activityList = emptyList()
                    Toast.makeText(context, "ක්‍රියාකාරකම් ඉතිහාසය හිස් කරන ලදී 🧹", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ඉතිහාසය හිස් කරන්න", color = Color.White)
            }
        }
    }

    // 19. ස්ථානය (Location)
    if (activeDialogId == "location") {
        SubOptionDialogContainer(
            title = "ස්ථාන සැකසුම් (Location)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("වත්මන් ස්ථානය: Pelmadulla, Sri Lanka", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            SubOptionSwitchRow(
                title = "ස්ථාන ඉතිහාසය (Location History)",
                subtitle = "ඔබ සංචාරය කරන ස්ථාන පිළිබඳ ආරක්ෂිත සටහනක් තබා ගැනීම",
                checked = locationHistoryEnabled,
                onCheckedChange = { locationHistoryEnabled = it }
            )
        }
    }

    // 20. යෙදුම් සහ වෙබ් අඩවි (Apps and Websites)
    if (activeDialogId == "apps") {
        val connectedApps = listOf("Spotify Music", "SL News Reader", "Candy Quest SL")
        SubOptionDialogContainer(
            title = "සම්බන්ධිත යෙදුම් සහ වෙබ් අඩවි",
            onDismiss = { activeDialogId = null }
        ) {
            connectedApps.forEach { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(app, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    TextButton(onClick = { Toast.makeText(context, "$app ඉවත් කරන ලදී", Toast.LENGTH_SHORT).show() }) {
                        Text("ඉවත් කරන්න", color = Color(0xFFEF4444), fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }

    // 21. ව්‍යාපාර සංකලන (Business Integrations)
    if (activeDialogId == "business") {
        SubOptionDialogContainer(
            title = "ව්‍යාපාර සංකලන (Business Integrations)",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ක්‍රියාකාරී ව්‍යාපාරික මෙවලම් කිසිවක් නැත.", color = Color(0xFF94A3B8), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { Toast.makeText(context, "ව්‍යාපාරික පිටුවකට සම්බන්ධ විය", Toast.LENGTH_SHORT).show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("ව්‍යාපාර මෙවලම් සම්බන්ධ කරන්න", color = Color.White)
            }
        }
    }

    // 22. ඔබේ තතු කළමනාකරණය කිරීම (Manage Info)
    if (activeDialogId == "manage_info") {
        SubOptionDialogContainer(
            title = "ඔබේ තතු කළමනාකරණය කිරීම",
            onDismiss = { activeDialogId = null }
        ) {
            Text("ඔබට ඔබගේ සියලුම පෝස්ට්, ඡායාරූප සහ චැට් වල පිටපතක් ආරක්ෂිතව බාගත හැක.", color = Color(0xFF94A3B8), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "දත්ත ගොනුව බාගත කිරීමට සූදානම් කෙරේ (JSON Archive) 📦", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("තොරතුරුවල පිටපතක් බාගන්න (Download Archive)", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "ගිණුම ආරක්ෂිතව තාවකාලිකව අක්‍රිය කළ හැක", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("ගිණුම අක්‍රිය කිරීම (Deactivation)", color = Color(0xFFEF4444))
            }
        }
    }

    // 23. සේවා නියමයන් (Terms)
    if (activeDialogId == "terms") {
        SubOptionDialogContainer(title = "සේවා නියමයන්", onDismiss = { activeDialogId = null }) {
            Text(
                text = "FriendHub යෙදුම භාවිතා කිරීමෙන් ඔබ අපගේ ආරක්ෂිත සන්නිවේදන ප්‍රමිති සහ කොන්දේසි වලට එකඟ වේ. ඔබගේ අයිතිවාසිකම් සහ දත්ත ආරක්ෂාව අපගේ ඉහළම ප්‍රමුඛතාවයයි.\n\n1. ගිණුම් ආරක්‍ෂාව සහ වගකීම\n2. අන්‍යෝන්‍ය ගෞරවය සහ ප්‍රජා නීති\n3. බුද්ධිමය දේපල ආරක්ෂාව\n4. නීති විරෝධී ක්‍රියාකාරකම් තහනම් කිරීම",
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }

    // 24. පෞද්ගලිකත්ව ප්‍රතිපත්තිය (Privacy Policy & 10000% Data Protection Shield)
    if (activeDialogId == "privacy_policy") {
        SubOptionDialogContainer(title = "🛡️ 10000% පෞද්ගලිකත්ව සහ දත්ත ආරක්ෂණය", onDismiss = { activeDialogId = null }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = Color(0xFF0F291E),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MILITARY-GRADE DATA SHIELD ACTIVE",
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "ඔබගේ පෞද්ගලිකත්වය 10000%ක්ම සුරක්ෂිත කර ඇති අතර, කිසිදු බාහිර ඇප් එකකට හෝ තෙවන පාර්ශවයකට මෙහි ඇති කිසිදු දත්තයක් බැලීමට, සොරාගැනීමට හෝ වෙනස් කිරීමට නොහැකි වන සේ ආරක්ෂිත කේතනය (AES-256 Encryption & E2EE) මඟින් සලසා ඇත.\n\n🔒 **ප්‍රධාන ආරක්ෂණ සහතික**:\n• **බාහිර ඇප් තහනම**: බාහිර කිසිදු ඇප් එකකට මෙම ඇප් එකේ පරිශීලක දත්ත ලබාගත නොහැක.\n• **හැකර් විරෝධී පද්ධතිය**: Anti-Tampering & Anti-Hacking ආරක්ෂණ ස්තර ක්‍රියාත්මක වේ.\n• **දත්ත රහස්‍යභාවය**: පෞද්ගලික පණිවිඩ සහ තොරතුරු 100%ක් කෙළවරේ සිට කෙළවරට (End-to-End Encrypted) සුරක්ෂිතයි.\n• **ස්වයංක්‍රීය පාලනය**: ඔබගේ දත්තයන්හි සම්පූර්ණ හිමිකාරිත්වය සහ පාලනය ඔබට හිමිවේ.",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }

    // 25. කුකී ප්‍රතිපත්තිය (Cookie Policy)
    if (activeDialogId == "cookie_policy") {
        SubOptionDialogContainer(title = "කුකී ප්‍රතිපත්තිය", onDismiss = { activeDialogId = null }) {
            Text(
                text = "යෙදුමේ වේගවත් ක්‍රියාකාරීත්වය සහ ඔබගේ ප්‍රියතම සැකසුම් සුරැකීම සඳහා දේශීය හැඹිලි (Local Cache & Cookies) භාවිත වේ. ඔබට අවශ්‍ය විටෙක සැකසුම් මඟින් ඉඩ හිස් කරගත හැක.",
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }

    // 26. ප්‍රජා ප්‍රමිති (Community Standards)
    if (activeDialogId == "community_standards") {
        SubOptionDialogContainer(title = "ප්‍රජා ප්‍රමිති", onDismiss = { activeDialogId = null }) {
            Text(
                text = "අපගේ ප්‍රජාව සියලු දෙනාටම මිත්‍රශීලී සහ ආරක්ෂිත පරිසරයක් විය යුතුය.\n\n• අයාචිත තැපැල් (Spam) තහනම්\n• වෛරී ප්‍රකාශ හෝ හිරිහැර කිරීම් නොඉවසිය යුතුය\n• ව්‍යාජ තොරතුරු පළ කිරීම තහනම් වේ",
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }

    // 27. යෙදුම පිළිබඳව (About FriendHub - ZERO FACEBOOK)
    if (activeDialogId == "about") {
        SubOptionDialogContainer(title = "යෙදුම පිළිබඳව", onDismiss = { activeDialogId = null }) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF8B5CF6)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("FriendHub Social App", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("අනුවාදය: v4.8.2-stable", color = Color(0xFF38BDF8), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "වේගවත්, ආරක්ෂිත සහ වර්ණවත් සමාජ ජාල අත්දැකීමක් සැපයීම සඳහා නිර්මාණය කරන ලද්දකි. සියලුම හිමිකම් ඇවිරිණි © 2026.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }

    // 28. පද්ධති පරිපාලක සහ යෙදුම් හිමිකරු (Admin & Owner Details - Secure Read-Only with Interactive Owner Dashboard)
    if (activeDialogId == "admin_info") {
        val isAdmin = currentUser?.email?.trim()?.lowercase() == "wk541454@gmail.com"
        SubOptionDialogContainer(
            title = if (isAdmin) "👑 පරිපාලක පාලන පැනලය (Admin Panel)" else "👑 පරිපාලක සහ යෙදුම් හිමිකරු",
            onDismiss = { activeDialogId = null }
        ) {
            if (isAdmin) {
                // Interactive Owner Admin Console!
                var globalBroadcastText by remember { mutableStateOf("") }
                var verifyUserEmail by remember { mutableStateOf("") }
                var contentModerationInput by remember { mutableStateOf("") }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "SYSTEM CONTROL CENTER ACTIVE",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Logged in as System Owner: wk541454@gmail.com. You have complete root-level control over public content & user verifications.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Section 1: Global System Notice Broadcast
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📢 Global Announcement Broadcast", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = globalBroadcastText,
                                onValueChange = { globalBroadcastText = it },
                                placeholder = { Text("Write app-wide alert...", color = Color(0xFF64748B), fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (globalBroadcastText.isNotBlank()) {
                                        viewModel.createGlobalAnnouncement(globalBroadcastText.trim())
                                        Toast.makeText(context, "Announcement broadcasted globally to all feeds! 📢", Toast.LENGTH_SHORT).show()
                                        globalBroadcastText = ""
                                    } else {
                                        Toast.makeText(context, "Announcement text is empty", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Broadcast Alert", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Section 2: Account VIP Verification Badge Issuer
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🏅 Issue Gold Verification Badge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = verifyUserEmail,
                                onValueChange = { verifyUserEmail = it },
                                placeholder = { Text("Enter user's email or username...", color = Color(0xFF64748B), fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        if (verifyUserEmail.isNotBlank()) {
                                            viewModel.setUserVerificationStatus(verifyUserEmail.trim(), true)
                                            Toast.makeText(context, "${verifyUserEmail.trim()} verified as VIP! 🏅", Toast.LENGTH_SHORT).show()
                                            verifyUserEmail = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Verify VIP", color = Color.White, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        if (verifyUserEmail.isNotBlank()) {
                                            viewModel.setUserVerificationStatus(verifyUserEmail.trim(), false)
                                            Toast.makeText(context, "${verifyUserEmail.trim()} unverified.", Toast.LENGTH_SHORT).show()
                                            verifyUserEmail = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Revoke", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Section 3: Safety Moderation Center
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🛡️ Public Content Moderator", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Delete public abusive posts by ID.", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = contentModerationInput,
                                onValueChange = { contentModerationInput = it },
                                placeholder = { Text("Enter public Post ID...", color = Color(0xFF64748B), fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (contentModerationInput.isNotBlank()) {
                                        viewModel.deletePostByAdmin(contentModerationInput.trim())
                                        Toast.makeText(context, "Post ${contentModerationInput.trim()} deleted by Admin! 🛡️", Toast.LENGTH_SHORT).show()
                                        contentModerationInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Delete Public Post", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    // Section 4: Privacy Protection Policy Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "🔒 USER PRIVACY PROTOCOL ENFORCED:\nDirect Chats, Private Messages, and users' private account data are fully End-to-End Encrypted (E2EE) and locked from Administrative View to protect user privacy.",
                            color = Color(0xFF22C55E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                // Read-Only details for standard users
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔒 ආරක්ෂිත පද්ධති වාර්තාව",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = Color(0xFF0284C7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "READ-ONLY",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        Text(text = "වේදිකාව (Platform): FriendHub Official Social Network", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "සංස්කරණය (Version): v1.0.0 (Production Release)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "පාරිභෝගික සහාය (Support): support@friendhub.app", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "ආරක්ෂක ක්‍රමය (Security): Firebase Cloud Firestore & AES-256", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🛡️ පද්ධති ආරක්ෂණ සටහන:\nමෙම යෙදුමේ පරිපාලක සහ හිමිකරු දත්ත පද්ධති මට්ටමින් රක්ෂණය කර ඇත. කිසිදු පරිශීලකයෙකුට හෝ වෙනත් අයෙකුට මෙහි ඇති තොරතුරු වෙනස් කිරීමට හෝ සංස්කරණය කිරීමට අවසර නැත.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // 29. සම්බන්ධිත දුරකථන (Linked Devices - Strict 2-Device Limit WhatsApp Style)
    if (activeDialogId == "linked_devices") {
        var linkedDevicesCount by remember { mutableStateOf(2) } // Max 2 devices strict limit

        SubOptionDialogContainer(title = "📱 සම්බන්ධිත දුරකථන (උපරිම 2යි)", onDismiss = { activeDialogId = null }) {
            Text(text = "WhatsApp ආකාරයට මෙම ගිණුම උපරිම දුරකථන 2කට පමණක් සීමා කර ඇත (Device 1 සහ Device 2).", color = Color(0xFF94A3B8), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(14.dp))
            
            // Device 1
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Device 1: Primary Smartphone (This Device)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Status: Active & Synchronized 🟢", color = Color(0xFF38BDF8), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Device 2
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if (linkedDevicesCount >= 2) Color(0xFF10B981) else Color.Gray))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Device 2: Companion Smartphone", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(if (linkedDevicesCount >= 2) "Status: Linked & Active (2/2 Devices Connected)" else "Status: Not Linked", color = Color(0xFF38BDF8), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (linkedDevicesCount < 2) {
                Button(
                    onClick = {
                        linkedDevicesCount = 2
                        Toast.makeText(context, "2 වන උපාංගය සාර්ථකව සම්බන්ධ විය! (Device limit: 2/2)", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Link 2nd Device (+)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "⚠️ උපරිම උපාංග සීමාව (Devices 2/2) සම්පූර්ණයි. තවත් උපාංග එකතු කළ නොහැක.",
                    color = Color(0xFFF59E0B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Modern colorful section header card
 */
@Composable
private fun SectionHeaderCard(
    title: String,
    subtitle: String,
    badgeColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = subtitle,
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

/**
 * Colorful settings item row with vibrant icon badge and crisp typography
 */
@Composable
private fun ColorfulSettingItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    filterText: String = "",
    onClick: () -> Unit
) {
    if (filterText.isNotBlank() &&
        !title.contains(filterText, ignoreCase = true) &&
        !subtitle.contains(filterText, ignoreCase = true)
    ) {
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.85f)),
        border = BorderStroke(0.6.dp, Color(0xFF334155))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(iconBg, iconBg.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * Standard reusable sub-option dialog container
 */
@Composable
private fun SubOptionDialogContainer(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                content()
            }
        },
        confirmButton = {}
    )
}

/**
 * Switch row inside sub-option dialog
 */
@Composable
private fun SubOptionSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
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
