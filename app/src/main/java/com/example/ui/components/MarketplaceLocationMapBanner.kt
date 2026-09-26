package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TownMapProfile(
    val primaryTown: String,
    val neighborTown: String,
    val distanceKm: String = "3.5 km",
    val district: String = "රත්නපුර"
)

fun getTownMapProfile(locationName: String): TownMapProfile {
    val loc = locationName.trim().lowercase()
    return when {
        loc.contains("pelmadulla") || loc.contains("පැල්මඩුල්ල") ->
            TownMapProfile("Pelmadulla", "Opanayake", "3.5 km", "රත්නපුර")
        loc.contains("colombo") || loc.contains("කොළඹ") ->
            TownMapProfile("Colombo", "Dehiwala", "8.2 km", "බස්නාහිර")
        loc.contains("kandy") || loc.contains("මහනුවර") ->
            TownMapProfile("Kandy", "Peradeniya", "5.4 km", "මධ්‍යම")
        loc.contains("galle") || loc.contains("ගාල්ල") ->
            TownMapProfile("Galle", "Unawatuna", "4.8 km", "දකුණ")
        loc.contains("ratnapura") || loc.contains("රත්නපුර") ->
            TownMapProfile("Ratnapura", "Pelmadulla", "16.0 km", "සබරගමුව")
        loc.contains("kurunegala") || loc.contains("කුරුණෑගල") ->
            TownMapProfile("Kurunegala", "Mawathagama", "11.2 km", "වයඹ")
        loc.contains("negombo") || loc.contains("මීගමුව") ->
            TownMapProfile("Negombo", "Katunayake", "7.0 km", "ගම්පහ")
        loc.contains("matara") || loc.contains("මාතර") ->
            TownMapProfile("Matara", "Hakmana", "14.5 km", "දකුණ")
        loc.contains("gampaha") || loc.contains("ගම්පහ") ->
            TownMapProfile("Gampaha", "Yakkala", "4.2 km", "බස්නාහිර")
        loc.contains("anuradhapura") || loc.contains("අනුරාධපුර") ->
            TownMapProfile("Anuradhapura", "Mihintale", "12.0 km", "උතුරු මැද")
        loc.contains("jaffna") || loc.contains("යාපනය") ->
            TownMapProfile("Jaffna", "Nallur", "3.1 km", "උතුර")
        loc.contains("badulla") || loc.contains("බදුල්ල") ->
            TownMapProfile("Badulla", "Hali Ela", "6.2 km", "ඌව")
        loc.contains("nuwara") || loc.contains("නුවරඑළිය") ->
            TownMapProfile("Nuwara Eliya", "Nanu Oya", "8.0 km", "මධ්‍යම")
        loc.contains("hambantota") || loc.contains("හම්බන්තොට") ->
            TownMapProfile("Hambantota", "Ambalantota", "13.5 km", "දකුණ")
        loc.contains("trincomalee") || loc.contains("ත්‍රිකුණාමලය") ->
            TownMapProfile("Trincomalee", "Kinniya", "15.0 km", "නැගෙනහිර")
        loc.contains("batticaloa") || loc.contains("මඩකලපුව") ->
            TownMapProfile("Batticaloa", "Kattankudy", "6.0 km", "නැගෙනහිර")
        loc.contains("kalutara") || loc.contains("කළුතර") ->
            TownMapProfile("Kalutara", "Beruwala", "10.5 km", "බස්නාහිර")
        else -> {
            val cleanName = if (locationName.isNotBlank()) locationName else "Pelmadulla"
            TownMapProfile(cleanName, "Surrounding Area", "5.0 km", "ශ්‍රී ලංකාව")
        }
    }
}

/**
 * Renders the realistic Facebook Lite-style Location Map Preview Banner
 * with terrain, road network, nearby town labels, and approximate circular location radius zone.
 */
@Composable
fun MarketplaceLocationMapBanner(
    locationName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val profile = getTownMapProfile(locationName)

    val infiniteTransition = rememberInfiniteTransition(label = "LocationPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD7EBD6)) // Google Maps terrain light green
            .border(1.dp, Color(0xFFBDD9BD), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        // Vector Roads, Rivers & Terrain Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Terrain Gradient base
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFD6EBD7), Color(0xFFCCE6CD), Color(0xFFD8EED9)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // 2. Light green forest patches
            val forestPath = Path().apply {
                moveTo(0f, h * 0.1f)
                cubicTo(w * 0.2f, h * 0.05f, w * 0.25f, h * 0.45f, 0f, h * 0.5f)
                close()
            }
            drawPath(forestPath, Color(0xFFC0E0C2), style = Fill)

            val forestPath2 = Path().apply {
                moveTo(w * 0.75f, 0f)
                cubicTo(w * 0.85f, h * 0.35f, w * 0.95f, h * 0.15f, w, h * 0.25f)
                lineTo(w, 0f)
                close()
            }
            drawPath(forestPath2, Color(0xFFC2E3C4), style = Fill)

            // 3. Subtle River / Stream Path (soft blue)
            val riverPath = Path().apply {
                moveTo(w * 0.12f, 0f)
                cubicTo(w * 0.15f, h * 0.45f, w * 0.08f, h * 0.65f, w * 0.18f, h)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFFA5C9EB),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 4. Secondary Roads (white thin paths)
            val minorRoad1 = Path().apply {
                moveTo(0f, h * 0.75f)
                lineTo(w * 0.35f, h * 0.58f)
                lineTo(w * 0.5f, h * 0.65f)
            }
            drawPath(
                path = minorRoad1,
                color = Color(0xFFFAFDF9),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            val minorRoad2 = Path().apply {
                moveTo(w * 0.45f, 0f)
                lineTo(w * 0.48f, h * 0.45f)
                lineTo(w * 0.62f, h * 0.85f)
                lineTo(w * 0.7f, h)
            }
            drawPath(
                path = minorRoad2,
                color = Color(0xFFFAFDF9),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // 5. Main Highway (A-road / Primary Road across map)
            val mainHighway = Path().apply {
                moveTo(w * 0.05f, h * 0.48f)
                cubicTo(w * 0.3f, h * 0.42f, w * 0.42f, h * 0.52f, w * 0.54f, h * 0.52f)
                cubicTo(w * 0.68f, h * 0.52f, w * 0.78f, h * 0.68f, w * 0.98f, h * 0.54f)
            }
            // Highway outline casing
            drawPath(
                path = mainHighway,
                color = Color(0xFFB5D3B5),
                style = Stroke(width = 6.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Highway inner white fill
            drawPath(
                path = mainHighway,
                color = Color.White,
                style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 6. Prominent Red Location Marker (📍) on Item Location
            val centerCircle = Offset(w * 0.48f, h * 0.48f)
            val circleRadius = 26.dp.toPx() * pulseScale

            // Glowing Red Translucent Pulse Fill
            drawCircle(
                color = Color(0xFFEA4335).copy(alpha = 0.22f),
                radius = circleRadius,
                center = centerCircle
            )
            drawCircle(
                color = Color(0xFFEA4335).copy(alpha = 0.55f),
                radius = circleRadius,
                center = centerCircle,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // 3D Red Location Pin Drop
            val pinTip = centerCircle
            val pinHeadY = pinTip.y - 14.dp.toPx()
            val bannerPinPath = Path().apply {
                moveTo(pinTip.x, pinTip.y)
                cubicTo(pinTip.x - 7.dp.toPx(), pinTip.y - 7.dp.toPx(), pinTip.x - 9.dp.toPx(), pinHeadY, pinTip.x, pinHeadY)
                cubicTo(pinTip.x + 9.dp.toPx(), pinHeadY, pinTip.x + 7.dp.toPx(), pinTip.y - 7.dp.toPx(), pinTip.x, pinTip.y)
                close()
            }
            drawPath(path = bannerPinPath, color = Color(0xFFEA4335))
            drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(pinTip.x, pinHeadY + 3.dp.toPx()))
        }

        // Town Labels and Distance Overlay (HTML/Text rendered crisp above canvas)
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {

            // Primary Location Name (Centered in radius circle)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(end = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = profile.primaryTown,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = Color(0xFF1E293B)
                )
            }

            // Neighboring Town Label (Placed eastward along the highway)
            if (profile.neighborTown.isNotBlank()) {
                Text(
                    text = profile.neighborTown,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = Color(0xFF334155),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 8.dp)
                )
            }

            // Interactive Tap Indicator Badge (Top Right)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.75f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "සිතියම බලන්න",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
