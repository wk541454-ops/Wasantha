package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlin.math.cos
import kotlin.math.sin

enum class GoogleMapType(val labelEn: String, val labelSi: String) {
    DEFAULT("Default", "සම්මත සිතියම"),
    SATELLITE("Satellite", "චන්ද්‍රිකා දසුන"),
    TERRAIN("Terrain", "භූලක්ෂණ දසුන")
}

data class SriLankaCity(
    val nameEn: String,
    val nameSi: String,
    val district: String,
    val lat: Double,
    val lon: Double,
    val xNorm: Float, // 0f..1f within map canvas
    val yNorm: Float, // 0f..1f within map canvas
    val distanceKm: Double = 0.0,
    val etaDriveMin: Int = 0,
    val isCapital: Boolean = false,
    val isMajor: Boolean = false
)

val SRI_LANKA_MAP_CITIES = listOf(
    SriLankaCity("Jaffna", "යාපනය", "යාපනය", 9.6615, 80.0255, 0.32f, 0.14f, isMajor = true),
    SriLankaCity("Rameswaram", "රාමේෂ්වරම්", "ඉන්දියාව", 9.2876, 79.3129, 0.08f, 0.17f),
    SriLankaCity("Vavuniya", "වවුනියාව", "වවුනියාව", 8.7542, 80.4982, 0.48f, 0.28f, isMajor = true),
    SriLankaCity("Trincomalee", "ත්‍රිකුණාමලය", "ත්‍රිකුණාමලය", 8.5874, 81.2152, 0.73f, 0.31f, isMajor = true),
    SriLankaCity("Anuradhapura", "අනුරාධපුරය", "අනුරාධපුරය", 8.3114, 80.4037, 0.45f, 0.35f, isMajor = true),
    SriLankaCity("Sigiriya", "සීගිරිය", "මාතලේ", 7.9570, 80.7603, 0.53f, 0.41f),
    SriLankaCity("Polonnaruwa", "පොළොන්නරුව", "පොළොන්නරුව", 7.9403, 81.0188, 0.64f, 0.42f),
    SriLankaCity("Batticaloa", "මඩකලපුව", "මඩකලපුව", 7.7310, 81.6747, 0.81f, 0.46f, isMajor = true),
    SriLankaCity("Kandy", "මහනුවර", "මහනුවර", 7.2906, 80.6337, 0.52f, 0.51f, isMajor = true),
    SriLankaCity("Negombo", "මීගමුව", "ගම්පහ", 7.2008, 79.8737, 0.26f, 0.52f),
    SriLankaCity("Colombo", "කොළඹ", "කොළඹ", 6.9271, 79.8612, 0.27f, 0.57f, isCapital = true, isMajor = true),
    SriLankaCity("Panadura", "පානදුර", "කළුතර", 6.7132, 79.9074, 0.28f, 0.61f),
    SriLankaCity("Pelmadulla", "පැල්මඩුල්ල", "රත්නපුර", 6.6214, 80.5447, 0.44f, 0.62f, isMajor = true),
    SriLankaCity("Ratnapura", "රත්නපුර", "රත්නපුර", 6.7056, 80.3847, 0.41f, 0.60f),
    SriLankaCity("Nuwara Eliya", "නුවරඑළිය", "නුවරඑළිය", 6.9497, 80.7891, 0.56f, 0.56f),
    SriLankaCity("Yala National Park", "යාල ජාතික වනෝද්‍යානය", "හම්බන්තොට", 6.3725, 81.5170, 0.72f, 0.63f),
    SriLankaCity("Galle", "ගාල්ල", "ගාල්ල", 6.0535, 80.2210, 0.35f, 0.70f, isMajor = true),
    SriLankaCity("Tangalle", "තංගල්ල", "හම්බන්තොට", 6.0242, 80.7941, 0.49f, 0.70f),
    SriLankaCity("Matara", "මාතර", "මාතර", 5.9549, 80.5550, 0.44f, 0.72f),
    SriLankaCity("Kurunegala", "කුරුණෑගල", "කුරුණෑගල", 7.4863, 80.3623, 0.42f, 0.46f)
)

data class MapFilterChip(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val GOOGLE_MAP_FILTER_CHIPS = listOf(
    MapFilterChip("Restaurants", Icons.Default.Restaurant),
    MapFilterChip("Hotels", Icons.Default.Work),
    MapFilterChip("Gas", Icons.Default.LocalGasStation),
    MapFilterChip("Groceries", Icons.Default.Explore),
    MapFilterChip("Coffee", Icons.Default.Restaurant),
    MapFilterChip("Pharmacies", Icons.Default.LocalGasStation)
)

fun launchExternalGoogleMaps(context: Context, lat: Double, lon: Double, label: String) {
    try {
        val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(${Uri.encode(label)})")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to web browser Google Maps
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        context.startActivity(webIntent)
    }
}

@Composable
fun SriLankaMarketplaceMapModal(
    initialLocationName: String = "Pelmadulla",
    isSelectMode: Boolean = true,
    itemTitle: String = "",
    itemPrice: Double = 0.0,
    itemImageUrl: String = "",
    sellerName: String = "",
    onDismiss: () -> Unit,
    onSelectLocation: (String) -> Unit
) {
    val context = LocalContext.current

    // Map View States
    var mapType by remember { mutableStateOf(GoogleMapType.DEFAULT) }
    var selectedFilterChip by remember { mutableStateOf<String?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Selected town / pinpoint
    var selectedCity by remember {
        val initialMatch = SRI_LANKA_MAP_CITIES.find {
            it.nameEn.equals(initialLocationName, ignoreCase = true) ||
            it.nameSi.contains(initialLocationName) ||
            initialLocationName.contains(it.nameEn, ignoreCase = true) ||
            initialLocationName.contains(it.nameSi)
        } ?: SRI_LANKA_MAP_CITIES.find { it.nameEn == "Pelmadulla" } ?: SRI_LANKA_MAP_CITIES.first()
        mutableStateOf(initialMatch)
    }

    // Direct center zoom on the selected item's location
    var zoomScale by remember { mutableFloatStateOf(1.25f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Red Location Marker Pulsing Beacon Animation
    val infiniteTransition = rememberInfiniteTransition(label = "RedPinGpsPulse")
    val gpsPulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gpsPulseRadius"
    )
    val gpsPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gpsPulseAlpha"
    )

    // Animated zoom
    val animatedScale by animateFloatAsState(
        targetValue = zoomScale,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "mapScaleAnim"
    )

    val filteredSearchCities = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else SRI_LANKA_MAP_CITIES.filter {
            it.nameEn.contains(searchQuery, ignoreCase = true) ||
            it.nameSi.contains(searchQuery) ||
            it.district.contains(searchQuery)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF88D9E8) // Ocean cyan matching Google Maps screenshot
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                // ==================== 1. FULL SCREEN GOOGLE MAP CANVAS ====================
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                zoomScale = (zoomScale * zoom).coerceIn(0.85f, 4.0f)
                                panOffsetX += pan.x
                                panOffsetY += pan.y
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures { tapOffset ->
                                val width = size.width.toFloat()
                                val height = size.height.toFloat()
                                val centerX = width / 2f + panOffsetX
                                val centerY = height / 2f + panOffsetY

                                var closestCity: SriLankaCity? = null
                                var minDistance = Float.MAX_VALUE

                                SRI_LANKA_MAP_CITIES.forEach { city ->
                                    val cityX = centerX + (city.xNorm - 0.5f) * width * 1.15f * zoomScale
                                    val cityY = centerY + (city.yNorm - 0.5f) * height * 1.15f * zoomScale
                                    val dist = kotlin.math.hypot(tapOffset.x - cityX, tapOffset.y - cityY)
                                    if (dist < 40.dp.toPx() && dist < minDistance) {
                                        minDistance = dist
                                        closestCity = city
                                    }
                                }

                                closestCity?.let {
                                    selectedCity = it
                                    Toast.makeText(context, "${it.nameEn} (${it.nameSi}) තෝරා ගන්නා ලදී", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                ) {
                    val canvasW = size.width
                    val canvasH = size.height

                    // 1. Draw Google Maps Ocean (Cyan matching screenshot)
                    val oceanColor = when (mapType) {
                        GoogleMapType.DEFAULT -> Color(0xFF8CD8E9)
                        GoogleMapType.SATELLITE -> Color(0xFF0F263B)
                        GoogleMapType.TERRAIN -> Color(0xFF89D1E3)
                    }
                    drawRect(color = oceanColor)

                    val mapCenterX = canvasW / 2f + panOffsetX
                    val mapCenterY = canvasH / 2f + panOffsetY
                    val mapWidth = canvasW * 1.15f * animatedScale
                    val mapHeight = canvasH * 1.15f * animatedScale

                    fun toScreenX(xNorm: Float): Float = mapCenterX + (xNorm - 0.5f) * mapWidth
                    fun toScreenY(yNorm: Float): Float = mapCenterY + (yNorm - 0.5f) * mapHeight

                    // 2. Draw North Indian Coastline / Rameswaram snippet (top left)
                    val indiaPath = Path().apply {
                        moveTo(toScreenX(-0.1f), toScreenY(0.05f))
                        cubicTo(
                            toScreenX(0.02f), toScreenY(0.12f),
                            toScreenX(0.12f), toScreenY(0.18f),
                            toScreenX(0.05f), toScreenY(0.24f)
                        )
                        lineTo(toScreenX(-0.1f), toScreenY(0.25f))
                        close()
                    }
                    drawPath(
                        path = indiaPath,
                        color = if (mapType == GoogleMapType.SATELLITE) Color(0xFF1E3A2B) else Color(0xFFD6F0C2)
                    )

                    // 3. Draw Sri Lanka Island Landmass
                    val landPath = Path().apply {
                        // Point Pedro / Jaffna top
                        moveTo(toScreenX(0.33f), toScreenY(0.10f))
                        // North East coast down to Trincomalee
                        cubicTo(
                            toScreenX(0.46f), toScreenY(0.18f),
                            toScreenX(0.68f), toScreenY(0.26f),
                            toScreenX(0.74f), toScreenY(0.32f)
                        )
                        // East coast to Batticaloa and Arugam Bay
                        cubicTo(
                            toScreenX(0.82f), toScreenY(0.42f),
                            toScreenX(0.85f), toScreenY(0.54f),
                            toScreenX(0.81f), toScreenY(0.62f)
                        )
                        // South coast: Yala, Hambantota, Tangalle, Matara, Dondra Head
                        cubicTo(
                            toScreenX(0.72f), toScreenY(0.68f),
                            toScreenX(0.56f), toScreenY(0.73f),
                            toScreenX(0.44f), toScreenY(0.74f)
                        )
                        // South West coast: Galle, Hikkaduwa, Bentota, Kalutara, Colombo
                        cubicTo(
                            toScreenX(0.34f), toScreenY(0.72f),
                            toScreenX(0.26f), toScreenY(0.65f),
                            toScreenX(0.25f), toScreenY(0.56f)
                        )
                        // West coast: Negombo, Chilaw, Puttalam, Mannar
                        cubicTo(
                            toScreenX(0.24f), toScreenY(0.48f),
                            toScreenX(0.23f), toScreenY(0.36f),
                            toScreenX(0.22f), toScreenY(0.25f)
                        )
                        // North West to Jaffna peninsula
                        cubicTo(
                            toScreenX(0.24f), toScreenY(0.18f),
                            toScreenX(0.29f), toScreenY(0.12f),
                            toScreenX(0.33f), toScreenY(0.10f)
                        )
                        close()
                    }

                    val landColor = when (mapType) {
                        GoogleMapType.DEFAULT -> Color(0xFFCBEBC1) // Google Maps light pastel green
                        GoogleMapType.SATELLITE -> Color(0xFF1B3B24)
                        GoogleMapType.TERRAIN -> Color(0xFFC2E8B8)
                    }

                    // Draw Main Land
                    drawPath(path = landPath, color = landColor)

                    // Land coastline subtle outline
                    drawPath(
                        path = landPath,
                        color = if (mapType == GoogleMapType.SATELLITE) Color(0xFF134E27) else Color(0xFFA6D69D),
                        style = Stroke(width = 1.8.dp.toPx())
                    )

                    // 4. Central Highlands / Hill Country Relief (Kandy, Nuwara Eliya, Horton Plains)
                    val highlandPath = Path().apply {
                        moveTo(toScreenX(0.48f), toScreenY(0.48f))
                        cubicTo(
                            toScreenX(0.60f), toScreenY(0.49f),
                            toScreenX(0.65f), toScreenY(0.57f),
                            toScreenX(0.58f), toScreenY(0.64f)
                        )
                        cubicTo(
                            toScreenX(0.48f), toScreenY(0.66f),
                            toScreenX(0.38f), toScreenY(0.60f),
                            toScreenX(0.40f), toScreenY(0.52f)
                        )
                        close()
                    }
                    val highlandColor = when (mapType) {
                        GoogleMapType.DEFAULT -> Color(0xFFB1DF9F)
                        GoogleMapType.SATELLITE -> Color(0xFF132B1A)
                        GoogleMapType.TERRAIN -> Color(0xFF9ECE89)
                    }
                    drawPath(path = highlandPath, color = highlandColor)

                    // 5. Road Networks (Google Maps White/Orange Highway Lines)
                    val roadPaint = Stroke(width = 2.0.dp.toPx(), cap = StrokeCap.Round)
                    val highwayPaint = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
                    val highwayColor = if (mapType == GoogleMapType.SATELLITE) Color(0xFFFFA726).copy(alpha = 0.8f) else Color(0xFFFFFFFF)
                    val expressColor = if (mapType == GoogleMapType.SATELLITE) Color(0xFFFF7043) else Color(0xFFFED7AA)

                    // A1 Highway (Colombo -> Kandy)
                    val a1Path = Path().apply {
                        moveTo(toScreenX(0.27f), toScreenY(0.57f))
                        cubicTo(toScreenX(0.35f), toScreenY(0.55f), toScreenX(0.42f), toScreenY(0.53f), toScreenX(0.52f), toScreenY(0.51f))
                    }
                    drawPath(path = a1Path, color = highwayColor, style = highwayPaint)

                    // A9 Highway (Kandy -> Anuradhapura -> Vavuniya -> Jaffna)
                    val a9Path = Path().apply {
                        moveTo(toScreenX(0.52f), toScreenY(0.51f))
                        lineTo(toScreenX(0.45f), toScreenY(0.35f))
                        lineTo(toScreenX(0.48f), toScreenY(0.28f))
                        lineTo(toScreenX(0.32f), toScreenY(0.14f))
                    }
                    drawPath(path = a9Path, color = highwayColor, style = roadPaint)

                    // A4 Highway (Colombo -> Ratnapura -> Pelmadulla -> Badulla -> Batticaloa)
                    val a4Path = Path().apply {
                        moveTo(toScreenX(0.27f), toScreenY(0.57f))
                        lineTo(toScreenX(0.41f), toScreenY(0.60f)) // Ratnapura
                        lineTo(toScreenX(0.44f), toScreenY(0.62f)) // Pelmadulla
                        lineTo(toScreenX(0.62f), toScreenY(0.58f))
                        lineTo(toScreenX(0.81f), toScreenY(0.46f)) // Batticaloa
                    }
                    drawPath(path = a4Path, color = highwayColor, style = roadPaint)

                    // Southern Expressway E01 (Colombo -> Galle -> Matara -> Hambantota)
                    val e01Path = Path().apply {
                        moveTo(toScreenX(0.27f), toScreenY(0.57f))
                        lineTo(toScreenX(0.28f), toScreenY(0.61f))
                        lineTo(toScreenX(0.35f), toScreenY(0.70f))
                        lineTo(toScreenX(0.44f), toScreenY(0.72f))
                        lineTo(toScreenX(0.56f), toScreenY(0.71f))
                    }
                    drawPath(path = e01Path, color = expressColor, style = highwayPaint)

                    // A6 (Kurunegala -> Dambulla -> Trincomalee)
                    val a6Path = Path().apply {
                        moveTo(toScreenX(0.42f), toScreenY(0.46f))
                        lineTo(toScreenX(0.53f), toScreenY(0.41f))
                        lineTo(toScreenX(0.73f), toScreenY(0.31f))
                    }
                    drawPath(path = a6Path, color = highwayColor, style = roadPaint)

                    // 6. Draw "Sri Lanka" text label in middle of Island
                    drawContext.canvas.nativeCanvas.apply {
                        val slTextPaint = android.graphics.Paint().apply {
                            color = if (mapType == GoogleMapType.SATELLITE) android.graphics.Color.WHITE else android.graphics.Color.rgb(30, 41, 59)
                            textSize = 24.sp.toPx() * animatedScale.coerceIn(0.9f, 1.4f)
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            textAlign = android.graphics.Paint.Align.CENTER
                            setShadowLayer(6f, 0f, 0f, android.graphics.Color.argb(160, 255, 255, 255))
                        }
                        drawText("Sri Lanka", toScreenX(0.50f), toScreenY(0.46f), slTextPaint)
                    }

                    // 7. Draw City Dots & Labels
                    SRI_LANKA_MAP_CITIES.forEach { city ->
                        val cityX = toScreenX(city.xNorm)
                        val cityY = toScreenY(city.yNorm)

                        val isCitySelected = city.nameEn.equals(selectedCity.nameEn, ignoreCase = true)

                        if (!isCitySelected) {
                            // City dot
                            drawCircle(
                                color = Color(0xFF263238),
                                radius = if (city.isMajor) 3.5.dp.toPx() else 2.5.dp.toPx(),
                                center = Offset(cityX, cityY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = if (city.isMajor) 2.0.dp.toPx() else 1.5.dp.toPx(),
                                center = Offset(cityX, cityY)
                            )

                            // City Label
                            drawContext.canvas.nativeCanvas.apply {
                                val labelPaint = android.graphics.Paint().apply {
                                    color = if (mapType == GoogleMapType.SATELLITE) android.graphics.Color.WHITE else android.graphics.Color.rgb(33, 33, 33)
                                    textSize = (if (city.isMajor) 12.sp.toPx() else 10.sp.toPx()) * animatedScale.coerceIn(0.85f, 1.25f)
                                    typeface = if (city.isMajor) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                                    textAlign = android.graphics.Paint.Align.LEFT
                                    setShadowLayer(4f, 0f, 0f, android.graphics.Color.WHITE)
                                }
                                drawText(city.nameEn, cityX + 6.dp.toPx(), cityY + 3.dp.toPx(), labelPaint)
                            }
                        }
                    }

                    // 8. Custom Google Maps Badges from Screenshot (Work 💼, Home 🏠, Saved Hearts ❤️)
                    val heart1X = toScreenX(0.57f)
                    val heart1Y = toScreenY(0.56f)
                    drawCircle(color = Color(0xFFF43F5E), radius = 10.dp.toPx(), center = Offset(heart1X, heart1Y))
                    drawCircle(color = Color.White, radius = 9.dp.toPx(), center = Offset(heart1X, heart1Y), style = Stroke(width = 1.5.dp.toPx()))

                    val heart2X = toScreenX(0.23f)
                    val heart2Y = toScreenY(0.61f)
                    drawCircle(color = Color(0xFFF43F5E), radius = 10.dp.toPx(), center = Offset(heart2X, heart2Y))
                    drawCircle(color = Color.White, radius = 9.dp.toPx(), center = Offset(heart2X, heart2Y), style = Stroke(width = 1.5.dp.toPx()))

                    val workX = toScreenX(0.41f)
                    val workY = toScreenY(0.60f)
                    drawCircle(color = Color(0xFF1A73E8), radius = 11.dp.toPx(), center = Offset(workX, workY))
                    drawCircle(color = Color.White, radius = 10.dp.toPx(), center = Offset(workX, workY), style = Stroke(width = 1.5.dp.toPx()))

                    drawContext.canvas.nativeCanvas.apply {
                        val homeTextPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.rgb(26, 115, 232)
                            textSize = 12.sp.toPx()
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                            textAlign = android.graphics.Paint.Align.LEFT
                            setShadowLayer(4f, 0f, 0f, android.graphics.Color.WHITE)
                        }
                        drawText("Home", workX + 14.dp.toPx(), workY + 4.dp.toPx(), homeTextPaint)
                    }

                    // ==================== 9. PROMINENT RED MARKER (📍) ON ITEM LOCATION ====================
                    val targetX = toScreenX(selectedCity.xNorm)
                    val targetY = toScreenY(selectedCity.yNorm)

                    // Glowing Red Pulse Wave
                    drawCircle(
                        color = Color(0xFFEA4335).copy(alpha = gpsPulseAlpha * 0.7f),
                        radius = gpsPulseRadius * 2.0f,
                        center = Offset(targetX, targetY)
                    )
                    drawCircle(
                        color = Color(0xFFEA4335).copy(alpha = 0.3f),
                        radius = 16.dp.toPx(),
                        center = Offset(targetX, targetY)
                    )

                    // High-contrast 3D Red Google Maps Pin Drop Marker
                    val pinPath = Path().apply {
                        moveTo(targetX, targetY)
                        cubicTo(
                            targetX - 12.dp.toPx(), targetY - 18.dp.toPx(),
                            targetX - 16.dp.toPx(), targetY - 34.dp.toPx(),
                            targetX, targetY - 34.dp.toPx()
                        )
                        cubicTo(
                            targetX + 16.dp.toPx(), targetY - 34.dp.toPx(),
                            targetX + 12.dp.toPx(), targetY - 18.dp.toPx(),
                            targetX, targetY
                        )
                        close()
                    }

                    // Shadow behind pin
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.35f),
                        radius = 6.dp.toPx(),
                        center = Offset(targetX, targetY + 2.dp.toPx())
                    )

                    // Draw Red Pin Body
                    drawPath(path = pinPath, color = Color(0xFFEA4335))
                    // Draw Dark Red inner shading
                    drawCircle(
                        color = Color(0xFFB71C1C),
                        radius = 6.dp.toPx(),
                        center = Offset(targetX, targetY - 24.dp.toPx())
                    )
                    // Draw White center dot
                    drawCircle(
                        color = Color.White,
                        radius = 4.5.dp.toPx(),
                        center = Offset(targetX, targetY - 24.dp.toPx())
                    )

                    // Prominent City & Item Pin Title Label
                    drawContext.canvas.nativeCanvas.apply {
                        val pinLabelPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.rgb(198, 40, 40)
                            textSize = 14.sp.toPx()
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                            textAlign = android.graphics.Paint.Align.CENTER
                            setShadowLayer(8f, 0f, 0f, android.graphics.Color.WHITE)
                        }
                        drawText("📍 " + selectedCity.nameEn + " (" + selectedCity.nameSi + ")", targetX, targetY - 40.dp.toPx(), pinLabelPaint)
                    }

                    // 10. Watermark "Google" in Bottom Left
                    drawContext.canvas.nativeCanvas.apply {
                        val gPaint = android.graphics.Paint().apply {
                            textSize = 18.sp.toPx()
                            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD)
                            setShadowLayer(4f, 0f, 0f, android.graphics.Color.argb(120, 255, 255, 255))
                        }
                        val gx = 16.dp.toPx()
                        val gy = canvasH - 180.dp.toPx()

                        gPaint.color = android.graphics.Color.rgb(66, 133, 244)
                        drawText("G", gx, gy, gPaint)
                        gPaint.color = android.graphics.Color.rgb(234, 67, 53)
                        drawText("o", gx + 14.dp.toPx(), gy, gPaint)
                        gPaint.color = android.graphics.Color.rgb(251, 188, 5)
                        drawText("o", gx + 25.dp.toPx(), gy, gPaint)
                        gPaint.color = android.graphics.Color.rgb(66, 133, 244)
                        drawText("g", gx + 36.dp.toPx(), gy, gPaint)
                        gPaint.color = android.graphics.Color.rgb(52, 168, 83)
                        drawText("l", gx + 47.dp.toPx(), gy, gPaint)
                        gPaint.color = android.graphics.Color.rgb(234, 67, 53)
                        drawText("e", gx + 52.dp.toPx(), gy, gPaint)
                    }
                }

                // ==================== 2. TOP FLOATING GOOGLE MAPS SEARCH BAR ====================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp)),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { isSearchExpanded = !isSearchExpanded },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Google Maps Pin",
                                    tint = Color(0xFFEA4335),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isSearchExpanded = true }
                            ) {
                                if (isSearchExpanded) {
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        placeholder = { Text("Search city (e.g. Pelmadulla, Colombo)", fontSize = 14.sp, color = Color.Gray) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = if (searchQuery.isNotBlank()) searchQuery else "Search here",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                                        color = if (searchQuery.isNotBlank()) Color.Black else Color(0xFF5F6368)
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Listening for location...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Search",
                                    tint = Color(0xFF5F6368),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1A73E8))
                                    .clickable {
                                        Toast.makeText(context, "Google Account Profile", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    if (isSearchExpanded && filteredSearchCities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                filteredSearchCities.take(4).forEach { city ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedCity = city
                                                searchQuery = city.nameEn
                                                isSearchExpanded = false
                                                panOffsetX = 0f
                                                panOffsetY = 0f
                                                zoomScale = 1.35f
                                                Toast.makeText(context, "${city.nameEn} තෝරා ගන්නා ලදී", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFFEA4335),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${city.nameEn} (${city.nameSi})",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "${city.district} දිස්ත්‍රික්කය, ශ්‍රී ලංකාව",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(GOOGLE_MAP_FILTER_CHIPS) { chip ->
                            val isSelected = selectedFilterChip == chip.title
                            Row(
                                modifier = Modifier
                                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isSelected) Color(0xFFE8F0FE) else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color(0xFF1A73E8) else Color(0xFFDADCE0),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .clickable {
                                        selectedFilterChip = if (isSelected) null else chip.title
                                        Toast.makeText(context, "Showing ${chip.title} near ${selectedCity.nameEn}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = chip.icon,
                                    contentDescription = chip.title,
                                    tint = if (isSelected) Color(0xFF1A73E8) else Color(0xFF5F6368),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = chip.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    ),
                                    color = if (isSelected) Color(0xFF1A73E8) else Color(0xFF3C4043)
                                )
                            }
                        }
                    }
                }

                // ==================== 3. FLOATING ACTION CONTROLS ON RIGHT ====================
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 14.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Layers Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                mapType = when (mapType) {
                                    GoogleMapType.DEFAULT -> GoogleMapType.SATELLITE
                                    GoogleMapType.SATELLITE -> GoogleMapType.TERRAIN
                                    GoogleMapType.TERRAIN -> GoogleMapType.DEFAULT
                                }
                                Toast.makeText(context, "දසුන: ${mapType.labelSi}", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Map Layers",
                            tint = Color(0xFF5F6368),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 2. Compass Needle
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                panOffsetX = 0f
                                panOffsetY = 0f
                                zoomScale = 1.25f
                                Toast.makeText(context, "Oriented North", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val nPath = Path().apply {
                                moveTo(size.width / 2f, 2f)
                                lineTo(size.width / 2f - 5.dp.toPx(), size.height / 2f)
                                lineTo(size.width / 2f + 5.dp.toPx(), size.height / 2f)
                                close()
                            }
                            drawPath(path = nPath, color = Color(0xFFEA4335))

                            val sPath = Path().apply {
                                moveTo(size.width / 2f, size.height - 2f)
                                lineTo(size.width / 2f - 5.dp.toPx(), size.height / 2f)
                                lineTo(size.width / 2f + 5.dp.toPx(), size.height / 2f)
                                close()
                            }
                            drawPath(path = sPath, color = Color(0xFFB0BEC5))
                        }
                    }

                    // 3. My Location GPS Target Locator (Centers directly on red pin location)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                panOffsetX = 0f
                                panOffsetY = 0f
                                zoomScale = 1.35f
                                Toast.makeText(context, "ස්ථානය: ${selectedCity.nameEn}", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "My Location",
                            tint = Color(0xFFEA4335),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // 4. Vibrant Blue Directions FAB
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1A73E8))
                            .clickable {
                                launchExternalGoogleMaps(context, selectedCity.lat, selectedCity.lon, itemTitle.ifBlank { selectedCity.nameEn })
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Directions",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // ==================== 4. BOTTOM SHEET: "Latest in the area" & DIRECT GOOGLE MAPS OPEN ====================
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width(36.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFDADCE0))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEA4335))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "භාණ්ඩය පිහිටි ස්ථානය (Item Location)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = Color(0xFF202124)
                                    )
                                }

                                Text(
                                    text = "📍 ${selectedCity.nameEn}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEA4335)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Item & Location Details Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8F9FA))
                                    .border(1.dp, Color(0xFFE8EAED), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (itemImageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = itemImageUrl,
                                        contentDescription = itemTitle,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEE2E2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFFEA4335),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (itemTitle.isNotBlank()) itemTitle else "Marketplace Item @ ${selectedCity.nameEn}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = Color(0xFF202124),
                                        maxLines = 1
                                    )
                                    if (itemPrice > 0.0) {
                                        Text(
                                            text = "Rs. ${String.format("%,.0f", itemPrice * 300)} (USD $${String.format("%.2f", itemPrice)})",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF16A34A)
                                            )
                                        )
                                    }
                                    Text(
                                        text = "${selectedCity.district} දිස්ත්‍රික්කය • Lat: ${selectedCity.lat}, Lon: ${selectedCity.lon}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFF5F6368)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                if (isSelectMode) {
                                    Button(
                                        onClick = {
                                            onSelectLocation(selectedCity.nameEn)
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("තෝරන්න", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Direct Open in Google Maps Native App button
                            Button(
                                onClick = {
                                    launchExternalGoogleMaps(
                                        context = context,
                                        lat = selectedCity.lat,
                                        lon = selectedCity.lon,
                                        label = if (itemTitle.isNotBlank()) "$itemTitle (${selectedCity.nameEn})" else selectedCity.nameEn
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open Google Maps App",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Google Maps App මඟින් විවෘත කරන්න",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // ==================== 5. GOOGLE MAPS 5-TAB BOTTOM NAVIGATION BAR ====================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Explore Mode", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFD3E3FD))
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Explore",
                                        tint = Color(0xFF041E49),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Explore",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF041E49)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    launchExternalGoogleMaps(context, selectedCity.lat, selectedCity.lon, selectedCity.nameEn)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = "Go",
                                    tint = Color(0xFF444746),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Go", fontSize = 11.sp, color = Color(0xFF444746))
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Saved Places (Home, Work, Favorites)", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Saved",
                                    tint = Color(0xFF444746),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Saved", fontSize = 11.sp, color = Color(0xFF444746))
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Contribute to Map", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Contribute",
                                    tint = Color(0xFF444746),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Contribute", fontSize = 11.sp, color = Color(0xFF444746))
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Updates & Notifications", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Updates",
                                    tint = Color(0xFF444746),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Updates", fontSize = 11.sp, color = Color(0xFF444746))
                            }
                        }
                    }
                }

                // ==================== 6. TOP LEFT CLOSE / BACK BUTTON ====================
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 12.dp)
                        .size(36.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF3C4043),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
