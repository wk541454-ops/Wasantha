package com.example.ui.components

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ==========================================
// 1. MESSENGER CHAT THEMES & COLOR GRADIENTS
// ==========================================

enum class MessengerChatTheme(
    val title: String,
    val icon: String,
    val bubbleGradient: List<Color>,
    val bgColors: List<Color>,
    val accentColor: Color
) {
    MESSENGER_BLUE(
        "Messenger Classic",
        "🔵",
        listOf(Color(0xFF0084FF), Color(0xFF00C6FF)),
        listOf(Color(0xFF101010), Color(0xFF141414)),
        Color(0xFF0084FF)
    ),
    OCEAN_WAVE(
        "Ocean Wave",
        "🌊",
        listOf(Color(0xFF0072FF), Color(0xFF00C6FF)),
        listOf(Color(0xFF06152B), Color(0xFF0C2444)),
        Color(0xFF00C6FF)
    ),
    CHERRY_BLOSSOM(
        "Cherry Blossom",
        "🌸",
        listOf(Color(0xFFFF416C), Color(0xFFFF4B2B)),
        listOf(Color(0xFF1F0916), Color(0xFF330E24)),
        Color(0xFFFF416C)
    ),
    CYBERPUNK_NEON(
        "Cyberpunk Neon",
        "⚡",
        listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121)),
        listOf(Color(0xFF180A26), Color(0xFF260F3D)),
        Color(0xFFE94057)
    ),
    EMERALD_MINT(
        "Emerald Mint",
        "🌿",
        listOf(Color(0xFF0BA360), Color(0xFF3CBA92)),
        listOf(Color(0xFF071912), Color(0xFF0E2D20)),
        Color(0xFF3CBA92)
    ),
    SUNSET_GLOW(
        "Sunset Glow",
        "🌅",
        listOf(Color(0xFFFF512F), Color(0xFFDD2476)),
        listOf(Color(0xFF220C12), Color(0xFF38121E)),
        Color(0xFFFF512F)
    ),
    MIDNIGHT_STEALTH(
        "Midnight Stealth",
        "🖤",
        listOf(Color(0xFF3F3F46), Color(0xFF27272A)),
        listOf(Color(0xFF09090B), Color(0xFF121215)),
        Color(0xFFA1A1AA)
    )
}

// ==========================================
// 2. DATA MODELS & RICH CONTENT LISTS
// ==========================================

data class MessengerGifItem(
    val id: String,
    val title: String,
    val gifUrl: String,
    val category: String
)

data class MessengerSoundmojiItem(
    val emoji: String,
    val title: String,
    val soundDescription: String,
    val soundTone: String
)

data class MessengerVirtualGift(
    val id: String,
    val name: String,
    val icon: String,
    val coins: Int,
    val color: Color
)

val MESSENGER_GIFS_LIST = listOf(
    MessengerGifItem("g1", "Happy Dance", "https://media.giphy.com/media/blSTtZehjAZ8I/giphy.gif", "🔥 Trending"),
    MessengerGifItem("g2", "Excited Cat", "https://media.giphy.com/media/jpbnoe3UIa8TU8LM13/giphy.gif", "🔥 Trending"),
    MessengerGifItem("g3", "Party Popper", "https://media.giphy.com/media/l4KhQo2MESJkc6BLq/giphy.gif", "🔥 Trending"),
    MessengerGifItem("g4", "Thumbs Up Win", "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif", "👍 Approved"),
    MessengerGifItem("g5", "Big Heart Love", "https://media.giphy.com/media/26FLdmIp6wJr91JAI/giphy.gif", "❤️ Love"),
    MessengerGifItem("g6", "Warm Hug", "https://media.giphy.com/media/l2QDM9Jnim1YVWL6M/giphy.gif", "❤️ Love"),
    MessengerGifItem("g7", "Laughing So Hard", "https://media.giphy.com/media/10JhviFuU2gWD6/giphy.gif", "😂 LOL"),
    MessengerGifItem("g8", "Rolling Floor Laugh", "https://media.giphy.com/media/lw75Al819OAvS/giphy.gif", "😂 LOL"),
    MessengerGifItem("g9", "Happy Birthday Cake", "https://media.giphy.com/media/feio2yIUMtdqWjRiaF/giphy.gif", "🎉 Birthday"),
    MessengerGifItem("g10", "Birthday Cheers", "https://media.giphy.com/media/yoJC2GnSClbPOkV0eA/giphy.gif", "🎉 Birthday"),
    MessengerGifItem("g11", "Standing Ovation", "https://media.giphy.com/media/l3q2XhfQ8oCkm1GhO/giphy.gif", "👏 Bravo"),
    MessengerGifItem("g12", "Sri Lankan Baila Fun", "https://media.giphy.com/media/3ohzdIuqJoo8QdKlnW/giphy.gif", "🇱🇰 Lanka Fun"),
    MessengerGifItem("g13", "Coffee Chill Morning", "https://media.giphy.com/media/3o7TKoWXm3okO1kgHC/giphy.gif", "☕ Morning")
)

val MESSENGER_SOUNDMOJIS_LIST = listOf(
    MessengerSoundmojiItem("👏", "Applause", "Round of cheerful clapping!", "clap"),
    MessengerSoundmojiItem("😂", "LOL Laugh", "Infectious joyous laugh!", "laugh"),
    MessengerSoundmojiItem("🥁", "Drum Roll", "Ba-dum tsss punchline!", "drum"),
    MessengerSoundmojiItem("💓", "Heartbeat", "Romantic loud heartbeat!", "heart"),
    MessengerSoundmojiItem("💋", "Sweet Kiss", "Passionate kiss mwah!", "kiss"),
    MessengerSoundmojiItem("🥂", "Cheers", "Celebratory glasses clinking!", "cheers"),
    MessengerSoundmojiItem("🦗", "Crickets", "Funny awkward crickets sound...", "crickets"),
    MessengerSoundmojiItem("👻", "Spooky Boo", "Mysterious ghost surprise!", "boo"),
    MessengerSoundmojiItem("🚀", "Rocket Blast", "3.. 2.. 1.. Blastoff roar!", "rocket"),
    MessengerSoundmojiItem("🎸", "Rock Guitar", "Electric guitar riff solo!", "guitar")
)

val MESSENGER_VIRTUAL_GIFTS = listOf(
    MessengerVirtualGift("rose", "Romantic Rose", "🌹", 1, Color(0xFFFF2C55)),
    MessengerVirtualGift("heart_me", "Heart Me", "💖", 10, Color(0xFFF43F5E)),
    MessengerVirtualGift("tea_kevum", "Ceylon Tea & Kevum", "☕", 25, Color(0xFFD97706)),
    MessengerVirtualGift("cake", "Birthday Cake", "🎂", 50, Color(0xFFEC4899)),
    MessengerVirtualGift("crown", "Royal Crown", "👑", 100, Color(0xFFFFD700)),
    MessengerVirtualGift("fireworks", "Grand Fireworks", "🎆", 250, Color(0xFFA855F7)),
    MessengerVirtualGift("diamond", "Sparkling Diamond", "💎", 500, Color(0xFF06B6D4)),
    MessengerVirtualGift("supercar", "Luxury Supercar", "🏎️", 1000, Color(0xFFEF4444))
)

val MESSENGER_STICKER_PACKS = listOf(
    Pair("🐰 Pusheen & Meep", listOf(
        Pair("🐰", "Super Happy"),
        Pair("🥺", "Please Machan"),
        Pair("💤", "Nighty Night"),
        Pair("🎉", "Party Time"),
        Pair("🍔", "Burger Craving"),
        Pair("🏃‍♂️", "On The Way"),
        Pair("🤩", "Star Eyes"),
        Pair("😇", "Good Boy")
    )),
    Pair("❤️ Couple & Love", listOf(
        Pair("💖", "Always You"),
        Pair("👩‍❤️‍👨", "Together Forever"),
        Pair("💌", "Love Note"),
        Pair("🌹", "For My Love"),
        Pair("🥰", "Blushing So Much"),
        Pair("💍", "Marry Me?"),
        Pair("🫶", "Heart Hands"),
        Pair("💋", "Sweet Kisses")
    )),
    Pair("🇱🇰 Sri Lankan Moods", listOf(
        Pair("🇱🇰", "Ado Machan!"),
        Pair("🔥", "Gindara Thamayi!"),
        Pair("☕", "Tea Ekak Bomu"),
        Pair("🙏", "Budu Ammo!"),
        Pair("💃", "Baila Dance"),
        Pair("🚀", "Enawa Denma"),
        Pair("💯", "Aniwa Defa!"),
        Pair("🥳", "Suba Aluth Avuruddak")
    )),
    Pair("🎭 Memes & Laughs", listOf(
        Pair("💀", "I am Dead Bro"),
        Pair("🍿", "Ready For Drama"),
        Pair("👀", "Eyeing You"),
        Pair("🤡", "Total Clown"),
        Pair("👑", "Pure Boss"),
        Pair("✨", "Main Character"),
        Pair("😎", "Too Cool"),
        Pair("🔥", "Lit Af")
    ))
)

val MESSENGER_EMOJIS_BY_CATEGORY = mapOf(
    "😊 Smileys" to listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🥹", "☺️", "😊", "😇", "🥲", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😮‍💨", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔", "🫢", "🫣", "🤫", "🫡", "🤥", "😶", "😐", "😑", "😬", "🫠", "🙄", "😯", "😦", "😧", "😮", "😲", "🥱", "😴", "🤤", "😪", "😵", "😵‍💫", "🤐", "🥴", "🤢", "🤮", "🤧", "😷", "🤒", "🤕"
    ),
    "❤️ Love & Hands" to listOf(
        "👍", "👎", "👏", "🙌", "🫶", "👐", "🤲", "🤝", "🤜", "🤛", "✊", "👊", "✌️", "🤞", "🫰", "🤟", "🤘", "👌", "🤌", "🤏", "👈", "👉", "👆", "👇", "☝️", "✋", "🤚", "🖐️", "🖖", "👋", "🤙", "💪", "🖕", "✍️", "🙏", "🫵", "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝"
    ),
    "🐱 Nature & Animals" to listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🪰", "🐢", "🐍", "🦎", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🦈", "🐊", "🐅", "🐆", "🦓", "🦍", "🦧", "🐘", "🦛", "🦏", "🐪", "🐫", "🦒", "🦘", "🌸", "🌺", "🌹", "🌷", "🌻", "🌼", "💐", "🌴", "🌲", "🍀", "🍁"
    ),
    "🍕 Food & Drinks" to listOf(
        "🍕", "🍔", "🍟", "🌭", "🍿", "🥓", "🍳", "🧇", "🥞", "🧈", "🍞", "🥐", "🥨", "🧀", "🥗", "🥪", "🌮", "🌯", "🍜", "🍝", "🍣", "🍱", "🍛", "🍚", "🍙", "🍦", "🍧", "🍨", "🍩", "🍪", "🎂", "🍰", "🧁", "🥧", "🍫", "🍬", "🍭", "🍮", "🍯", "☕", "🍵", "🧋", "🥤", "🧃", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹"
    ),
    "🎉 Celebrations" to listOf(
        "🎉", "🎊", "🎈", "🎂", "🎁", "🪄", "🪅", "✨", "🎇", "🎆", "🧨", "🎐", "🎋", "🏆", "🥇", "🥈", "🥉", "🏅", "🎖️", "⚽", "🏀", "🏈", "🎾", "🏐", "🏏", "🎯", "🎲", "🎮", "🎸", "🎷", "🥁", "🎻", "🎤", "🎧", "🎬", "🎨", "🚀", "🛸", "🔥", "💯", "👑", "💎"
    )
)

// Audio playback for soundmojis
fun playSoundTone(soundTone: String) {
    try {
        val toneType = when (soundTone) {
            "clap" -> ToneGenerator.TONE_PROP_ACK
            "laugh" -> ToneGenerator.TONE_PROP_BEEP2
            "drum" -> ToneGenerator.TONE_PROP_PROMPT
            "heart" -> ToneGenerator.TONE_PROP_BEEP
            "kiss" -> ToneGenerator.TONE_CDMA_PIP
            "cheers" -> ToneGenerator.TONE_PROP_ACK
            "crickets" -> ToneGenerator.TONE_PROP_NACK
            "boo" -> ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK
            "rocket" -> ToneGenerator.TONE_CDMA_HIGH_SS
            else -> ToneGenerator.TONE_PROP_BEEP
        }
        val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 95)
        toneGen.startTone(toneType, 300)
    } catch (_: Exception) {}
}

// ==========================================
// 3. MESSENGER RICH MEDIA BOTTOM SHEET (5 TABS)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerChatMediaSheet(
    initialTab: Int = 0,
    accentColor: Color = Color(0xFF0084FF),
    onSendEmoji: (String) -> Unit,
    onSendSticker: (String) -> Unit,
    onSendGif: (MessengerGifItem) -> Unit,
    onSendSoundmoji: (MessengerSoundmojiItem) -> Unit,
    onSendGift: (MessengerVirtualGift) -> Unit,
    onOpenSurpriseGiftComposer: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var selectedEmojiCategory by remember { mutableStateOf("😊 Smileys") }
    var selectedGifCategory by remember { mutableStateOf("All") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF161616),
        scrimColor = Color.Black.copy(alpha = 0.75f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF3F3F46))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Messenger Top Tabs Row: Emojis, Stickers, GIFs, Soundmojis, Gifts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    Pair("😊 Emojis", 0),
                    Pair("🎭 Stickers", 1),
                    Pair("🎬 GIFs", 2),
                    Pair("🔊 Sound", 3),
                    Pair("🎁 Gifts", 4)
                )

                tabs.forEach { (title, tabIndex) ->
                    val isSelected = selectedTab == tabIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = tabIndex }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color(0xFF888888),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(2.5.dp)
                                .background(
                                    if (isSelected) accentColor else Color.Transparent,
                                    RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF27272A), thickness = 0.5.dp)

            when (selectedTab) {
                // TAB 0: ORIGINAL FULL CATEGORIZED EMOJIS
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    ) {
                        // Emoji Category Chips
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(MESSENGER_EMOJIS_BY_CATEGORY.keys.toList()) { cat ->
                                val isSelected = selectedEmojiCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) accentColor.copy(alpha = 0.25f) else Color(0xFF242426),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, accentColor) else null,
                                    modifier = Modifier.clickable { selectedEmojiCategory = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.White else Color(0xFFA1A1AA),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Emoji Grid
                        val emojis = MESSENGER_EMOJIS_BY_CATEGORY[selectedEmojiCategory] ?: emptyList()
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(emojis.chunked(7)) { rowEmojis ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowEmojis.forEach { emoji ->
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onSendEmoji(emoji) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = emoji, fontSize = 26.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 1: ORIGINAL MESSENGER STICKER PACKS
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        MESSENGER_STICKER_PACKS.forEach { (packName, stickers) ->
                            item {
                                Text(
                                    text = packName,
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                            }
                            items(stickers.chunked(4)) { rowStickers ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowStickers.forEach { (icon, title) ->
                                        Card(
                                            modifier = Modifier
                                                .width(76.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { onSendSticker("Sticker: $icon $title") },
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF222224)),
                                            border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp, brush = Brush.horizontalGradient(listOf(Color(0xFF333336), Color(0xFF333336))))
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                            ) {
                                                Text(text = icon, fontSize = 34.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = title,
                                                    fontSize = 9.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: ORIGINAL MESSENGER GIFS (TENOR / GIPHY)
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    ) {
                        // GIF Category Chips
                        val gifCats = listOf("All", "🔥 Trending", "❤️ Love", "😂 LOL", "🎉 Birthday", "🇱🇰 Lanka Fun")
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(gifCats) { cat ->
                                val isSelected = selectedGifCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) accentColor.copy(alpha = 0.25f) else Color(0xFF242426),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, accentColor) else null,
                                    modifier = Modifier.clickable { selectedGifCategory = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.White else Color(0xFFA1A1AA),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        val filteredGifs = if (selectedGifCategory == "All") MESSENGER_GIFS_LIST else MESSENGER_GIFS_LIST.filter { it.category == selectedGifCategory }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredGifs.chunked(2)) { rowGifs ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowGifs.forEach { gif ->
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(115.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { onSendGif(gif) },
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF222225))
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                AsyncImage(
                                                    model = gif.gifUrl,
                                                    contentDescription = gif.title,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .align(Alignment.BottomCenter)
                                                        .background(Color.Black.copy(alpha = 0.65f))
                                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = gif.title,
                                                            color = Color.White,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = "GIF",
                                                            color = Color(0xFF00C6FF),
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.ExtraBold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: SOUNDMOJIS (Messenger Audio Reactions)
                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .padding(horizontal = 14.dp)
                    ) {
                        Text(
                            text = "🔊 Messenger Soundmojis (හඬ සහිත ප්‍රතිචාර)",
                            color = Color(0xFFA1A1AA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(MESSENGER_SOUNDMOJIS_LIST) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            playSoundTone(item.soundTone)
                                            onSendSoundmoji(item)
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222225))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF2F2F33)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = item.emoji, fontSize = 24.sp)
                                            }
                                            Column {
                                                Text(
                                                    text = item.title,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = item.soundDescription,
                                                    color = Color(0xFFA1A1AA),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { playSoundTone(item.soundTone) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(accentColor.copy(alpha = 0.2f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Preview Sound",
                                                tint = accentColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 4: ORIGINAL MESSENGER GIFTS & SURPRISE BOX
                4 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Secret Surprise Wrapped Gift Box Button (MESSENGER FEATURE)
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onOpenSurpriseGiftComposer() },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF416C)))
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF3B1E2B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🎁", fontSize = 28.sp)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Surprise Gift Box Message 🎁",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "රහස් පණිවිඩයක් තෑගි පෙට්ටියක ඔතා යවන්න! (Tap to Unwrap)",
                                            color = Color(0xFFA1A1AA),
                                            fontSize = 11.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700)
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Virtual Friendship & Live Gifts",
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "🪙 50,000 Coins Available",
                                    color = Color(0xFFFFD700),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        items(MESSENGER_VIRTUAL_GIFTS.chunked(4)) { rowGifts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowGifts.forEach { gift ->
                                    Card(
                                        modifier = Modifier
                                            .width(78.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { onSendGift(gift) },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF222226)),
                                        border = CardDefaults.outlinedCardBorder().copy(
                                            width = 1.dp,
                                            brush = Brush.linearGradient(listOf(gift.color.copy(alpha = 0.5f), Color(0xFF333338)))
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                        ) {
                                            Text(text = gift.icon, fontSize = 28.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = gift.name,
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "🪙 ${gift.coins}",
                                                fontSize = 9.sp,
                                                color = Color(0xFFFFD700),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================
// 4. MESSENGER FUN ACTION HUB (✨ Action Hub for Friends)
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerFunHubSheet(
    currentTheme: MessengerChatTheme,
    onSelectTheme: (MessengerChatTheme) -> Unit,
    onRollDice: () -> Unit,
    onFlipCoin: () -> Unit,
    onOpenPollComposer: () -> Unit,
    onOpenMagic8Ball: () -> Unit,
    onOpenNicknameEditor: () -> Unit,
    onOpenQuickEmojiPicker: () -> Unit,
    isVanishMode: Boolean,
    onToggleVanishMode: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141416),
        scrimColor = Color.Black.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "✨ Messenger Action Hub & Games",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Text(
                text = "මිතුරන් සමඟ විනෝදයෙන් අසීමිතව චැට් කිරීමට සුවිශේෂී පහසුකම්",
                color = Color(0xFFA1A1AA),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 1. Themes Selector Row
            Text(
                text = "🎨 Chat Theme & Bubble Colors",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MessengerChatTheme.values()) { theme ->
                    val isSelected = currentTheme == theme
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF222226),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, theme.accentColor) else null,
                        modifier = Modifier.clickable { onSelectTheme(theme) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(theme.bubbleGradient))
                            )
                            Text(
                                text = theme.title,
                                color = if (isSelected) Color.White else Color(0xFFA1A1AA),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Interactive Games Grid (Dice, Coin, Poll, 8-Ball)
            Text(
                text = "🎲 Interactive Mini Games & Decisions",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Roll Dice
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onRollDice() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF202024))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎲", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Roll Dice", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "දාදු කැටය පෙරළන්න", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                    }
                }

                // Flip Coin
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onFlipCoin() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF202024))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🪙", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Flip Coin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "කාසියක් උඩ දමන්න", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                    }
                }

                // Quick Poll
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenPollComposer() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF202024))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📊", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Quick Poll", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "ඡන්දයක් අසන්න", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                    }
                }

                // Magic 8-Ball
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenMagic8Ball() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF202024))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎱", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "8-Ball", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "අනාගත අනාවැකි", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Messenger Customization Shortcuts (Nicknames, Quick Emoji, Vanish Mode)
            Text(
                text = "⚡ Messenger Settings & Tools",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Change Nickname
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenNicknameEditor() },
                    color = Color(0xFF202024)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "✏️", fontSize = 18.sp)
                        Column {
                            Text(text = "Nicknames", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "ආදරණීය නම්", color = Color.Gray, fontSize = 9.sp)
                        }
                    }
                }

                // Change Quick Emoji (Like customizer)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenQuickEmojiPicker() },
                    color = Color(0xFF202024)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💖", fontSize = 18.sp)
                        Column {
                            Text(text = "Quick Emoji", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Like වෙනස් කරන්න", color = Color.Gray, fontSize = 9.sp)
                        }
                    }
                }

                // Vanish Mode
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleVanishMode() },
                    color = if (isVanishMode) Color(0xFF3F102F) else Color(0xFF202024)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = if (isVanishMode) "👻" else "👁️", fontSize = 18.sp)
                        Column {
                            Text(
                                text = if (isVanishMode) "Vanish: ON" else "Vanish Mode",
                                color = if (isVanishMode) Color(0xFFFF416C) else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "මැකෙන පණිවිඩ", color = Color.Gray, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================
// 5. SURPRISE GIFT COMPOSER DIALOG
// =========================================================

@Composable
fun SurpriseGiftDialog(
    onSendSurprise: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var secretText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🎁", fontSize = 26.sp)
                Text(text = "Surprise Gift Message", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column {
                Text(
                    text = "ඔබගේ මිතුරාට රහස් පණිවිඩයක් තෑගි පෙට්ටියක ඔතා යවන්න! ඔවුන් එය විවෘත කරන තෙක් පණිවිඩය රහසක්ව පවතී ✨",
                    color = Color(0xFFA1A1AA),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = secretText,
                    onValueChange = { secretText = it },
                    placeholder = { Text("රහස් පණිවිඩය මෙහි ලියන්න (e.g. I Love You ❤️)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF3F3F46)
                    ),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (secretText.isNotBlank()) {
                        onSendSurprise(secretText.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF416C)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("තෑග්ග ඔතා යවන්න 🎁", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("අවලංගු කරන්න", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E24)
    )
}

// =========================================================
// 6. QUICK POLL COMPOSER DIALOG
// =========================================================

@Composable
fun QuickPollDialog(
    onSendPoll: (question: String, opt1: String, opt2: String) -> Unit,
    onDismiss: () -> Unit
) {
    var question by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📊", fontSize = 24.sp)
                Text(text = "Create Quick Poll", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "මිතුරන් සමඟ ඉක්මන් තීරණ ගැනීමට සජීවී ඡන්ද විමසීමක් සාදන්න",
                    color = Color(0xFFA1A1AA),
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    placeholder = { Text("ප්‍රශ්නය (e.g. අද රෑට කන්නේ මොනවාද?)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = opt1,
                    onValueChange = { opt1 = it },
                    placeholder = { Text("විකල්පය 1 (e.g. Pizza 🍕)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = opt2,
                    onValueChange = { opt2 = it },
                    placeholder = { Text("විකල්පය 2 (e.g. Koththu 🥘)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (question.isNotBlank() && opt1.isNotBlank() && opt2.isNotBlank()) {
                        onSendPoll(question.trim(), opt1.trim(), opt2.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Poll එක යවන්න 📊", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("අවලංගු කරන්න", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E24)
    )
}

// =========================================================
// 7. QUICK EMOJI (LIKE BUTTON) PICKER SHEET
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEmojiPickerSheet(
    currentEmoji: String,
    onSelectEmoji: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val quickOptions = listOf("👍", "❤️", "🔥", "😂", "🥰", "🎉", "🚀", "🇱🇰", "💯", "✨", "👏", "👑")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF161616)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "💖 Choose Quick Reaction Emoji",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Messenger Like (👍) බොත්තම වෙනුවට ඔබේ කැමති Emoji එකක් සකසන්න",
                color = Color(0xFFA1A1AA),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                quickOptions.take(6).forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (emoji == currentEmoji) Color(0xFF0084FF).copy(alpha = 0.3f) else Color(0xFF242426))
                            .clickable { onSelectEmoji(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                quickOptions.drop(6).forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (emoji == currentEmoji) Color(0xFF0084FF).copy(alpha = 0.3f) else Color(0xFF242426))
                            .clickable { onSelectEmoji(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

// =========================================================
// 8. INTERACTIVE CHAT CARDS (UNWRAP SURPRISE, POLL, DICE, COIN)
// =========================================================

@Composable
fun InteractiveSurpriseGiftCard(
    messageContent: String,
    onTriggerCelebration: () -> Unit
) {
    var isUnwrapped by remember { mutableStateOf(false) }
    val secretText = remember(messageContent) {
        messageContent.removePrefix("🎁 [Surprise Gift]: ").trim()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF416C)))
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isUnwrapped) {
                Text(text = "🎁", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ඔබට රහස් තෑග්ගක් ලැබී ඇත!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        isUnwrapped = true
                        onTriggerCelebration()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF416C)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("විවෘත කරන්න (Tap to Unwrap) ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🎉", fontSize = 20.sp)
                    Text(
                        text = "Surprise Message Revealed!",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(text = "✨", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = secretText,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun InteractivePollCard(
    messageContent: String
) {
    // Format: 📊 [Poll]: Question | Opt1 | Opt2
    val clean = messageContent.removePrefix("📊 [Poll]: ").trim()
    val parts = clean.split(" | ")
    val question = parts.getOrNull(0) ?: "Poll"
    val opt1 = parts.getOrNull(1) ?: "Option A"
    val opt2 = parts.getOrNull(2) ?: "Option B"

    var selectedVote by remember { mutableIntStateOf(0) }
    var opt1Count by remember { mutableIntStateOf(2) }
    var opt2Count by remember { mutableIntStateOf(1) }

    val total = opt1Count + opt2Count
    val p1 = if (total > 0) (opt1Count.toFloat() / total) * 100f else 50f
    val p2 = if (total > 0) (opt2Count.toFloat() / total) * 100f else 50f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0084FF))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "📊", fontSize = 18.sp)
                Text(text = question, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option 1
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selectedVote == 1) Color(0xFF0084FF).copy(alpha = 0.35f) else Color(0xFF28282C),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (selectedVote == 0) {
                            selectedVote = 1
                            opt1Count++
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = opt1, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "${p1.toInt()}% ($opt1Count)", color = Color(0xFF00C6FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Option 2
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selectedVote == 2) Color(0xFF0084FF).copy(alpha = 0.35f) else Color(0xFF28282C),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (selectedVote == 0) {
                            selectedVote = 2
                            opt2Count++
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = opt2, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "${p2.toInt()}% ($opt2Count)", color = Color(0xFF00C6FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
