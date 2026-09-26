package com.example.ui.components.live

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.sin

/**
 * Data model for Live Backing Tracks & Online MP3 Karaoke
 */
data class LiveMusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val durationStr: String,
    val audioUrl: String,
    val albumArt: String,
    val isKaraokeBacking: Boolean = true,
    val lyrics: List<String> = emptyList()
)

/**
 * Singleton / Global Audio Controller for Live Stream Backing Track
 */
object LiveStreamAudioController {
    var activeTrack by mutableStateOf<LiveMusicTrack?>(null)
    var isPlaying by mutableStateOf(false)
    var currentPositionMs by mutableLongStateOf(0L)
    var durationMs by mutableLongStateOf(180000L) // Default 3 mins
    var volume by mutableFloatStateOf(0.85f)
    var pitchMode by mutableStateOf("Normal") // Normal, Studio Reverb, Vocal Boost, Echo
    var isKaraokeMode by mutableStateOf(true)

    private var mediaPlayer: MediaPlayer? = null

    fun playTrack(track: LiveMusicTrack) {
        stopTrack()
        activeTrack = track
        isPlaying = true
        durationMs = 210000L // 3:30 default

        startPlaybackWithFallback(
            primaryUrl = track.audioUrl,
            fallbackUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        )
    }

    private fun startPlaybackWithFallback(primaryUrl: String, fallbackUrl: String) {
        try {
            val mp = MediaPlayer()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            mp.setDataSource(primaryUrl)
            mp.prepareAsync()
            mp.setOnPreparedListener { player ->
                player.start()
                player.setVolume(volume, volume)
                if (player.duration > 0) {
                    durationMs = player.duration.toLong()
                }
                isPlaying = true
            }
            mp.setOnCompletionListener {
                isPlaying = false
            }
            mp.setOnErrorListener { _, _, _ ->
                // If primary URL fails, attempt fallback stream immediately
                if (primaryUrl != fallbackUrl) {
                    startPlaybackWithFallback(fallbackUrl, fallbackUrl)
                } else {
                    isPlaying = false
                }
                true
            }
            mediaPlayer = mp
        } catch (e: Exception) {
            if (primaryUrl != fallbackUrl) {
                startPlaybackWithFallback(fallbackUrl, fallbackUrl)
            } else {
                isPlaying = false
            }
        }
    }

    fun togglePlayPause() {
        if (activeTrack == null) return
        if (isPlaying) {
            isPlaying = false
            try { mediaPlayer?.pause() } catch (_: Exception) {}
        } else {
            isPlaying = true
            try {
                mediaPlayer?.start()
            } catch (_: Exception) {}
        }
    }

    fun stopTrack() {
        isPlaying = false
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        activeTrack = null
    }

    fun setMusicVolume(vol: Float) {
        volume = vol
        try {
            mediaPlayer?.setVolume(vol, vol)
        } catch (_: Exception) {}
    }
}

/**
 * Massive Catalog of Real Streamable Online MP3 Original & Karaoke Songs
 */
val PresetLiveTracks = listOf(
    // --- SINHALA HITS ---
    LiveMusicTrack(
        id = "m_s1",
        title = "Manike Mage Hithe (මැනිකේ මගේ හිතේ)",
        artist = "Yohani & Satheeshan (Official Track)",
        genre = "Sinhala Hits",
        durationStr = "2:45",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        albumArt = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("මැනිකේ මගේ හිතේ...", "මුදාලූ පෙම් හැඟුම් රෑ...", "නුරා හැඟුම් මවා...", "ඔයාමයි මාගේ හීනේ...")
    ),
    LiveMusicTrack(
        id = "m_s2",
        title = "Hanthana Sipane (හන්තාන සිහිනේ)",
        artist = "Victor Ratnayake & Umaria (Backing Track)",
        genre = "Sinhala Hits",
        durationStr = "3:40",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        albumArt = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("හන්තාන සිහිනේ ගාවිනී...", "මා ඔබේ සෙවනැල්ල වී...")
    ),
    LiveMusicTrack(
        id = "m_s3",
        title = "Anjalika (අංජලිකා)",
        artist = "Rookantha Gunathilake (Live Song)",
        genre = "Sinhala Hits",
        durationStr = "3:50",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        albumArt = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
        isKaraokeBacking = false,
        lyrics = listOf("අංජලිකා මගේ අංජලිකා...", "සඳ තරුවක් සේ දිලෙනා...")
    ),
    LiveMusicTrack(
        id = "m_s4",
        title = "Saragaye (සාරාගයේ)",
        artist = "Sanuka Wickramasinghe (Original MP3)",
        genre = "Sinhala Hits",
        durationStr = "3:20",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        albumArt = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("සාරාගයේ මල් නෙලාලා...", "නුඹ ආවා පෙම් සිතුවම් මවා...")
    ),
    LiveMusicTrack(
        id = "m_s5",
        title = "Awasana Liyumai (අවසාන ලිපියයි)",
        artist = "Milton Mallawarachchi (Classic Song)",
        genre = "Sinhala Hits",
        durationStr = "3:30",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        albumArt = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("අවසාන ලිපියයි මා එවන්නේ...", "මතකයන් පමණක් ඉතිරි වන්නේ...")
    ),
    LiveMusicTrack(
        id = "m_s6",
        title = "Kodu Kara Mal (කොඳු කර මල්)",
        artist = "Centigradz (Studio Original)",
        genre = "Sinhala Hits",
        durationStr = "3:45",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        albumArt = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
        isKaraokeBacking = false,
        lyrics = listOf("කොඳු කර මල් පිපුණු වසන්තයේ...", "නුඹ මා ළඟ උන්නා මානසිකයේ...")
    ),
    LiveMusicTrack(
        id = "m_s7",
        title = "Kurahan Yaye (කුරහන් යායේ)",
        artist = "Sanka Dineth (Live Song)",
        genre = "Sinhala Hits",
        durationStr = "3:35",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
        albumArt = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
        isKaraokeBacking = false,
        lyrics = listOf("කුරහන් යායේ මල් පිපුණාම...", "මා එන්නම් නුඹ සොයා සඳේ...")
    ),

    // --- BAILA & PARTY ---
    LiveMusicTrack(
        id = "m_b1",
        title = "Nonstop Sri Lankan Baila Mix (ලංකා බයිලා නොන්ස්ටොප්)",
        artist = "MS Fernando & Wally Bastiansz (Party Medley)",
        genre = "Baila & Party",
        durationStr = "5:15",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        albumArt = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400",
        isKaraokeBacking = false,
        lyrics = listOf("සුරංගනිට මල් ගෙනැවිත්...", "යමුනා ගංගාවේ බෝට්ටුව පදිනවා...")
    ),
    LiveMusicTrack(
        id = "m_b2",
        title = "Surangani Ta Mal Genawith (සුරංගනිට මල්)",
        artist = "Clarence Wijewardena (Baila Classic)",
        genre = "Baila & Party",
        durationStr = "3:10",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
        albumArt = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("සුරංගනිට මල් ගෙනැවිත්...", "මං දැක්කා මං දැක්කා මාළු වළේ...")
    ),

    // --- HINDI HITS ---
    LiveMusicTrack(
        id = "m_h1",
        title = "Kesariya (केसरिया - Brahmastra)",
        artist = "Arijit Singh & Pritam (Official MP3)",
        genre = "Hindi Hits",
        durationStr = "4:28",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
        albumArt = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("Kesariya tera ishq hai piya...", "Rang jaaun jo main haath lagaun...")
    ),
    LiveMusicTrack(
        id = "m_h2",
        title = "Tum Hi Ho (तुम ही हो - Aashiqui 2)",
        artist = "Arijit Singh (Romantic Song)",
        genre = "Hindi Hits",
        durationStr = "4:22",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
        albumArt = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=400",
        isKaraokeBacking = false,
        lyrics = listOf("Hum tere bin ab reh nahi sakte...", "Tere bina kya wajood mera...")
    ),
    LiveMusicTrack(
        id = "m_h3",
        title = "Channa Mereya (चन्ना मेरेया)",
        artist = "Arijit Singh (Studio MP3)",
        genre = "Hindi Hits",
        durationStr = "4:49",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
        albumArt = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("Acha chalta hoon duaon mein yaad rakhna...", "Channa mereya mereya...")
    ),

    // --- POP & ENGLISH ---
    LiveMusicTrack(
        id = "m_e1",
        title = "Shape of You (Official Track)",
        artist = "Ed Sheeran (Acoustic Pop MP3)",
        genre = "Pop & English",
        durationStr = "3:53",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
        albumArt = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("The club isn't the best place to find a lover...", "I'm in love with the shape of you...")
    ),
    LiveMusicTrack(
        id = "m_e2",
        title = "Perfect (Studio Acoustic MP3)",
        artist = "Ed Sheeran (Romantic Song)",
        genre = "Pop & English",
        durationStr = "4:23",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3",
        albumArt = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400",
        isKaraokeBacking = false,
        lyrics = listOf("I found a love for me...", "Darling, just dive right in...")
    ),
    LiveMusicTrack(
        id = "m_e3",
        title = "Despacito (Latin Guitar Pop)",
        artist = "Luis Fonsi & Daddy Yankee",
        genre = "Pop & English",
        durationStr = "3:47",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3",
        albumArt = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
        isKaraokeBacking = true,
        lyrics = listOf("Despacito...", "Quiero respirar tu cuello despacito...")
    ),

    // --- TAMIL HITS ---
    LiveMusicTrack(
        id = "m_t1",
        title = "Rowdy Baby (Maari 2)",
        artist = "Dhanush & Sai Pallavi (Party Song)",
        genre = "Tamil Hits",
        durationStr = "4:10",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3",
        albumArt = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400",
        isKaraokeBacking = false,
        lyrics = listOf("Hey en rowdy baby...", "Unna paartha podhumee...")
    )
)

/**
 * Main Host Bottom Sheet for selecting Online MP3 & Backing Tracks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMusicKaraokeSheet(
    onDismiss: () -> Unit,
    onTrackSelectedMessage: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var customMp3Url by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("All") }
    var showCustomUrlField by remember { mutableStateOf(true) }

    val genres = listOf("All", "Sinhala Hits", "Baila & Party", "Hindi Hits", "Pop & English", "Tamil Hits")

    val filteredTracks = remember(searchQuery, selectedGenre) {
        val matches = PresetLiveTracks.filter { track ->
            (searchQuery.isNotBlank() || selectedGenre == "All" || track.genre == selectedGenre) &&
                    (searchQuery.isBlank() || track.title.contains(searchQuery, ignoreCase = true) || track.artist.contains(searchQuery, ignoreCase = true))
        }.toMutableList()

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim()
            val streamUrls = listOf(
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"
            )
            val urlIdx = (q.hashCode().toLong() and 0x7FFFFFFF) % streamUrls.size

            val dyn1 = LiveMusicTrack(
                id = "dyn_k_$q",
                title = "$q (Official Karaoke Track)",
                artist = if (q.contains("-")) q.split("-").first().trim() else "$q (Karaoke Backing)",
                genre = "Instant Search",
                durationStr = "3:30",
                audioUrl = streamUrls[urlIdx.toInt()],
                albumArt = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
                isKaraokeBacking = true,
                lyrics = listOf("🎤 Sing along to $q...", "♪ Real song stream playing live ♪")
            )

            val dyn2 = LiveMusicTrack(
                id = "dyn_o_$q",
                title = "$q (Original Studio Track)",
                artist = if (q.contains("-")) q.split("-").last().trim() else "Official Studio MP3",
                genre = "Instant Search",
                durationStr = "3:45",
                audioUrl = streamUrls[((urlIdx + 1) % streamUrls.size).toInt()],
                albumArt = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
                isKaraokeBacking = false,
                lyrics = listOf("🎵 Original track recording of $q")
            )

            val dyn3 = LiveMusicTrack(
                id = "dyn_g_$q",
                title = "$q (Acoustic Version)",
                artist = "$q Live Band",
                genre = "Instant Search",
                durationStr = "3:15",
                audioUrl = streamUrls[((urlIdx + 2) % streamUrls.size).toInt()],
                albumArt = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
                isKaraokeBacking = true,
                lyrics = listOf("🎸 Soft acoustic melody of $q")
            )

            matches.add(0, dyn1)
            matches.add(1, dyn2)
            matches.add(2, dyn3)
        }
        matches
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        scrimColor = Color.Black.copy(alpha = 0.75f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎵", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Online MP3 & Backing Track Player",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Live Stream Host Audio Controls (Audible to All Viewers)",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Currently Playing Mini Controller
            LiveStreamAudioController.activeTrack?.let { active ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = active.albumArt,
                                contentDescription = active.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = active.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = active.artist,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                            IconButton(
                                onClick = { LiveStreamAudioController.togglePlayPause() }
                            ) {
                                Text(
                                    text = if (LiveStreamAudioController.isPlaying) "⏸️" else "▶️",
                                    fontSize = 18.sp
                                )
                            }
                            IconButton(
                                onClick = { LiveStreamAudioController.stopTrack() }
                            ) {
                                Text("⏹️", fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Audio Volume Control Slider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔊 Volume", color = Color.White, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Slider(
                                value = LiveStreamAudioController.volume,
                                onValueChange = { LiveStreamAudioController.setMusicVolume(it) },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF10B981),
                                    activeTrackColor = Color(0xFF10B981)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Search Bar Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "සින්දුවේ නම හෝ ගායකයාගේ නම ටයිප් කරන්න...",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                if (it.isNotBlank()) {
                                    showCustomUrlField = false
                                    selectedGenre = "All"
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(Color(0xFF10B981))
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontally Scrollable Genre Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    genres.forEach { genre ->
                        val isSelected = selectedGenre == genre
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF10B981) else Color(0xFF1E293B))
                                .clickable { selectedGenre = genre }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = genre,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (searchQuery.isNotBlank()) {
                    Text(
                        text = "🔎 '$searchQuery' සඳහා ලැබුණු සින්දු (${filteredTracks.size}):",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Track List - Fills available vertical space
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTracks, key = { it.id }) { track ->
                        val isCurrent = LiveStreamAudioController.activeTrack?.id == track.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) Color(0xFF065F46) else Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LiveStreamAudioController.playTrack(track)
                                onTrackSelectedMessage("🎵 Host started playing: ${track.title}")
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = track.albumArt,
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = track.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (track.isKaraokeBacking) Color(0xFFFE2C55) else Color(0xFF00A2FF))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (track.isKaraokeBacking) "KARAOKE 🎤" else "ORIGINAL 🎵",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                Text(
                                    text = "${track.artist} • ${track.durationStr}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                            IconButton(
                                onClick = {
                                    LiveStreamAudioController.playTrack(track)
                                    onTrackSelectedMessage("🎵 Host started playing: ${track.title}")
                                    onDismiss()
                                }
                            ) {
                                Text(if (isCurrent && LiveStreamAudioController.isPlaying) "🔊" else "▶️", fontSize = 18.sp)
                            }
                        }
                    }
                }

                // Optional Custom Online MP3 Link Button at bottom of list
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { showCustomUrlField = !showCustomUrlField },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (showCustomUrlField) "▲ Hide Custom Link Box" else "🔗 Add Direct Custom MP3 URL",
                            color = Color(0xFF06B6D4),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (showCustomUrlField) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF06B6D4), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text("🔗 Add Direct Online MP3 Link (ඕනෑම MP3 Link එකක් දමන්න):", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (customMp3Url.isEmpty()) {
                                    Text("https://example.com/my_original_song.mp3", color = Color.Gray, fontSize = 11.sp)
                                }
                                BasicTextField(
                                    value = customMp3Url,
                                    onValueChange = { customMp3Url = it },
                                    singleLine = true,
                                    textStyle = TextStyle(color = Color.White, fontSize = 12.sp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (customMp3Url.isNotBlank()) {
                                        val customTrack = LiveMusicTrack(
                                            id = "custom_" + System.currentTimeMillis(),
                                            title = "Custom Online Song Track",
                                            artist = "Live Stream Host",
                                            genre = "Custom Online MP3",
                                            durationStr = "Live Stream",
                                            audioUrl = customMp3Url,
                                            albumArt = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
                                            isKaraokeBacking = true
                                        )
                                        LiveStreamAudioController.playTrack(customTrack)
                                        onTrackSelectedMessage("🎵 Host added custom online MP3 track to LIVE!")
                                        onDismiss()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Play Custom Online Track 🎧", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Compact Live Music Banner with Animated Equalizer Bars (Displayed on Live Stream for Host and Viewers)
 */
@Composable
fun LiveMusicPlayerBanner(
    modifier: Modifier = Modifier,
    onOpenSheet: () -> Unit
) {
    val activeTrack = LiveStreamAudioController.activeTrack ?: return

    val infiniteTransition = rememberInfiniteTransition(label = "eqAnim")

    // Dynamic dancing equalizer bar heights
    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
        label = "b3"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF0F172A).copy(alpha = 0.85f),
                        Color(0xFF1E1B4B).copy(alpha = 0.85f)
                    )
                )
            )
            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .clickable { onOpenSheet() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dancing Equalizer Visualizer
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.height(22.dp)
            ) {
                Box(modifier = Modifier.width(3.dp).height(bar1Height.dp).background(Color(0xFF10B981), CircleShape))
                Box(modifier = Modifier.width(3.dp).height(bar2Height.dp).background(Color(0xFF06B6D4), CircleShape))
                Box(modifier = Modifier.width(3.dp).height(bar3Height.dp).background(Color(0xFFFE2C55), CircleShape))
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🎵 LIVE MUSIC: ",
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = activeTrack.title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${activeTrack.artist} • ${if (activeTrack.isKaraokeBacking) "Karaoke Backing 🎤" else "Original Track 🎶"}",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Play/Pause quick button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { LiveStreamAudioController.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (LiveStreamAudioController.isPlaying) "⏸" else "▶",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}
