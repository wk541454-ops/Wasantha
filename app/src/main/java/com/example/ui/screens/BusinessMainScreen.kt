package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.BusinessPage
import com.example.ui.components.BusinessTopNavBar
import com.example.ui.components.QuickCreateMenuModal
import com.example.ui.components.SearchModal
import com.example.viewmodel.MainViewModel

@Composable
fun BusinessMainScreen(
    viewModel: MainViewModel,
    businessContext: BusinessPage
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()
    val isQuickCreateMenuOpen by viewModel.isQuickCreateMenuOpen.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                if (selectedTab != 5) {
                    BusinessTopNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        businessAvatar = businessContext.imageUrl,
                        onCreateClick = { viewModel.setQuickCreateMenuOpen(true) },
                        onSearchClick = { viewModel.setSearchOpen(true) },
                        onMenuClick = { viewModel.selectTab(5) },
                        onSwitchToPersonalClick = { viewModel.switchContextToPersonal() }
                    )
                }
            }
        },
        containerColor = Color(0xFF0F172A) // Slate 900
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> BusinessFeedScreen(viewModel = viewModel, businessContext = businessContext)
                1 -> BusinessMessagingScreen(viewModel = viewModel, businessContext = businessContext)
                2 -> VideosScreen(viewModel = viewModel)
                3 -> NotificationsScreen(viewModel = viewModel)
                4 -> BusinessStoreScreen(page = businessContext, viewModel = viewModel, onBackClick = { viewModel.selectTab(0) })
                5 -> BusinessMenuScreen(viewModel = viewModel, businessContext = businessContext)
                else -> BusinessFeedScreen(viewModel = viewModel, businessContext = businessContext)
            }

            if (isQuickCreateMenuOpen) {
                QuickCreateMenuModal(
                    viewModel = viewModel,
                    onDismiss = { viewModel.setQuickCreateMenuOpen(false) }
                )
            }
            if (isSearchOpen) {
                SearchModal(
                    viewModel = viewModel,
                    onDismiss = { viewModel.setSearchOpen(false) }
                )
            }
        }
    }
}

