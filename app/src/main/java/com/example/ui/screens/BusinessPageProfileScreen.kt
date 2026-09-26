package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.model.BusinessPage
import com.example.viewmodel.MainViewModel

/**
 * BusinessPageProfileScreen delegates directly to the comprehensive,
 * interactive BusinessStoreScreen (with full e-commerce, WhatsApp ordering,
 * multi-photo uploading up to 10 images, and complete profile customization).
 */
@Composable
fun BusinessPageProfileScreen(
    page: BusinessPage,
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    BusinessStoreScreen(
        page = page,
        viewModel = viewModel,
        onBackClick = onBackClick
    )
}
