package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashComplete: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(1200)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B132B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ආරක්ෂිතව ලෝගෝව පූරණය කිරීම (Safe Logo Loading)
            val logoPainter = runCatching { painterResource(id = R.drawable.app_logo_fnl) }
                .recoverCatching { painterResource(id = R.drawable.app_logo_fn) }
                .getOrNull()
            
            if (logoPainter != null) {
                Image(
                    painter = logoPainter,
                    contentDescription = "FriendHub 3D Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .border(1.5.dp, Color(0xFFF3BDF8).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                )
            } else {
                // ලෝගෝව පූරණය වීමේදී දෝෂයක් ආවොත් පෙන්වන සුන්දර Text Fallback එක
                Text(
                    text = "FH",
                    color = Color(0xFFF3BDF8),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
