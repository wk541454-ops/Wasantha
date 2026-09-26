package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.BusinessPage
import com.example.viewmodel.MainViewModel

@Composable
fun BusinessMenuScreen(viewModel: MainViewModel, businessContext: BusinessPage) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Slate 900
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "මෙනුව (Business)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC),
                fontFamily = com.example.ui.theme.PlusJakartaSansFamily
            )
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Slate 800
                modifier = Modifier.fillMaxWidth().clickable { viewModel.selectTab(4) },
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = businessContext.imageUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(40.dp).clip(CircleShape).border(1.dp, Color(0xFF10B981), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(businessContext.name, fontWeight = FontWeight.Bold, color = Color(0xFFF8FAFC), fontFamily = com.example.ui.theme.PlusJakartaSansFamily)
                        Text("පිටුව නරඹන්න", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    }
                }
            }
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth().clickable { viewModel.switchContextToPersonal() },
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.SwitchAccount, contentDescription = "Switch", tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Personal Account එකට මාරු වන්න", fontWeight = FontWeight.Bold, color = Color(0xFFF8FAFC), fontFamily = com.example.ui.theme.PlusJakartaSansFamily)
                }
            }
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth().clickable { viewModel.setAppSettingsOpen(true) },
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("පිටුවේ සැකසුම්", fontWeight = FontWeight.Bold, color = Color(0xFFF8FAFC), fontFamily = com.example.ui.theme.PlusJakartaSansFamily)
                }
            }
        }
        
        item {
            val isSettingsOpen by viewModel.isAppSettingsOpen.collectAsState()
            if (isSettingsOpen) {
                com.example.ui.components.AppSettingsModal(
                    viewModel = viewModel,
                    onDismiss = { viewModel.setAppSettingsOpen(false) }
                )
            }
        }
    }
}
