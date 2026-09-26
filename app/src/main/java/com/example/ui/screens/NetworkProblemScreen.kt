package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FbLiteRunningDotsLoader

/**
 * Minimal Facebook Lite-Style Screen:
 * Runs the iconic 4-dots loading animation and displays simple "No internet".
 */
@Composable
fun NetworkProblemScreen(
    isCheckingConnection: Boolean,
    onRetry: () -> Unit,
    onBrowseOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF18191A))
            .testTag("network_problem_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // FriendHub App Logo
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1877F2), Color(0xFF0052CC))
                        )
                    )
                    .border(2.5.dp, Color(0xFF4599FF).copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "f",
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Authentic Facebook Lite 4-Dots Running Loader
            FbLiteRunningDotsLoader(
                dotSize = 10.dp,
                spacing = 9.dp,
                dotColor = Color(0xFF1877F2)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pure & Simple: "No internet"
            Text(
                text = "No internet",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isCheckingConnection) "ජාලය පරීක්ෂා කරමින්..." else "දත්ත (Data) සම්බන්ධතාවයක් නොමැත",
                fontSize = 13.sp,
                color = Color(0xFFB0B3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Clean Retry Button
            Button(
                onClick = onRetry,
                enabled = !isCheckingConnection,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(44.dp)
                    .testTag("retry_connection_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2),
                    disabledContainerColor = Color(0xFF1877F2).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isCheckingConnection) {
                    FbLiteRunningDotsLoader(
                        dotSize = 6.dp,
                        spacing = 5.dp,
                        dotColor = Color.White
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "නැවත උත්සාහ කරන්න (Retry)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // View previously visited/cached content
            TextButton(
                onClick = onBrowseOffline,
                modifier = Modifier.testTag("browse_offline_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Offline Cache",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF4599FF)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "කලින් බැලූ පුවත් බලන්න (View Cached Posts)",
                        fontSize = 12.sp,
                        color = Color(0xFF4599FF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
