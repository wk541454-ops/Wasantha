package com.example.ui.components.live

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Share to", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            // Add share options...
            Button(onClick = { /* Handle copy link */ onDismiss() }) { Text("Copy Link") }
            Button(onClick = { /* Handle download */ onDismiss() }) { Text("Download Stream") }
        }
    }
}

@Composable
fun GiftSheet(onDismiss: () -> Unit) {
    // Grid of gifts...
}

@Composable
fun CoinRechargeModal(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recharge Coins") },
        text = { Text("Choose a coin package...") },
        confirmButton = { Button(onClick = onDismiss) { Text("Get Coins") } }
    )
}
