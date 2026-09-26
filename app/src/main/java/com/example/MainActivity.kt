package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppSettingsModal
import com.example.ui.components.FloatingDancingEmojisOverlay
import com.example.ui.components.OfflineNoticeBanner
import com.example.ui.components.QuickCreateMenuModal
import com.example.ui.components.SearchModal
import com.example.ui.components.TopNavBar
import com.example.ui.screens.AuthModal
import com.example.ui.screens.CallsScreen
import com.example.ui.screens.FacebookLiteLoginScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.MessagingScreen
import com.example.ui.screens.NetworkProblemScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OtherUserProfileScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VideosScreen
import com.example.ui.theme.FriendHubTheme
import com.example.util.NetworkMonitor
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (t: Throwable) {
            // EdgeToEdge fallback
        }

        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(this)
            }
        } catch (t: Throwable) {
            android.util.Log.w("MainActivity", "FirebaseApp init fallback: ${t.message}")
        }

        try {
            setContent {
                val isDarkMode by viewModel.isDarkMode.collectAsState()
                val selectedFont by viewModel.selectedFont.collectAsState()
                FriendHubTheme(darkTheme = isDarkMode, fontName = selectedFont) {
                    FriendHubApp(viewModel = viewModel)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "Failed to set content", t)
        }
    }
}

@Composable
fun FriendHubApp(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    var isSplashShowing by remember { mutableStateOf(true) }

    if (isSplashShowing) {
        SplashScreen(
            onSplashComplete = { isSplashShowing = false }
        )
        return
    }

    // Listen to real-time network connectivity
    LaunchedEffect(networkMonitor) {
        try {
            networkMonitor.isOnline.collect { online ->
                viewModel.setNetworkAvailable(online)
            }
        } catch (e: Exception) {
            // Safe fallback for network monitor listener
        }
    }

    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            try {
                viewModel.updateActivity(context)
            } catch (e: Exception) {
                // Safe activity update check
            }
        }
    }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isAuthModalOpen by viewModel.isAuthModalOpen.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()
    val isQuickCreateMenuOpen by viewModel.isQuickCreateMenuOpen.collectAsState()
    val isAppSettingsOpen by viewModel.isAppSettingsOpen.collectAsState()
    val selectedPostForDetail by viewModel.selectedPostForDetail.collectAsState()
    val selectedUserProfile by viewModel.selectedUserProfile.collectAsState()

    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val isCheckingConnection by viewModel.isCheckingConnection.collectAsState()
    val isOfflineBrowsingMode by viewModel.isOfflineBrowsingMode.collectAsState()
    val incomingCall by viewModel.incomingCall.collectAsState()
    val isCallActive by viewModel.isCallActive.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val activeChatMessages by viewModel.activeChatMessages.collectAsState()

    val friendRequests by viewModel.friendRequests.collectAsState()
    val userCoins by viewModel.userCoins.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val isDailyRewardClaimed by viewModel.isDailyRewardClaimed.collectAsState()
    val showDailyRewardsModal by viewModel.showDailyRewardsModal.collectAsState()
    val showCoinRechargeModal by viewModel.showCoinRechargeModal.collectAsState()

    // 1. Facebook Lite Signature "Network Problem" Screen
    // Loads within the app itself (never shows Chrome/browser crash page)
    if (!isNetworkAvailable && !isOfflineBrowsingMode) {
        NetworkProblemScreen(
            isCheckingConnection = isCheckingConnection,
            onRetry = {
                viewModel.retryConnection { networkMonitor.isConnected() }
            },
            onBrowseOffline = {
                viewModel.setOfflineBrowsingMode(true)
            }
        )
        return
    }

    if (!isLoggedIn) {
        FacebookLiteLoginScreen(
            currentUser = currentUser,
            onLoginSuccessWithUser = { newUser -> viewModel.loginWithUser(newUser) },
            onLoginSuccess = { viewModel.login() },
            onCreateAccount = { viewModel.setAuthModalOpen(true) }
        )

        if (isAuthModalOpen) {
            AuthModal(
                onDismiss = { viewModel.setAuthModalOpen(false) },
                onLoginSuccess = {
                    viewModel.setAuthModalOpen(false)
                    viewModel.login()
                }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            val activeBusinessContext by viewModel.activeBusinessContext.collectAsState()
            val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsState()
            val unreadMessagesCount by viewModel.unreadMessagesCount.collectAsState()
            
            if (activeBusinessContext != null) {
                com.example.ui.screens.BusinessMainScreen(
                    viewModel = viewModel,
                    businessContext = activeBusinessContext!!
                )
            } else {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Column {
                            if (selectedTab != 6) {
                                TopNavBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { viewModel.selectTab(it) },
                                    unreadNotificationsCount = unreadNotificationsCount,
                                    unreadMessagesCount = unreadMessagesCount,
                                    currentUserAvatar = currentUser?.avatarUrl ?: "",
                                    coinBalance = userCoins,
                                    streakDays = streakDays,
                                    onCoinWalletClick = { viewModel.setShowCoinRechargeModal(true) },
                                    onDailyStreakClick = { viewModel.setShowDailyRewardsModal(true) },
                                    onCreateClick = { viewModel.setQuickCreateMenuOpen(true) },
                                    onSearchClick = { viewModel.setSearchOpen(true) },
                                    onLiveClick = { viewModel.setLiveStreamingOpen(true) },
                                    onAppSettingsClick = { viewModel.setAppSettingsOpen(true) },
                                    onTriggerEmojiBurst = { origin -> viewModel.triggerEmojiBurst(origin) }
                                )

                                com.example.ui.components.BottomNavBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { viewModel.selectTab(it) },
                                    unreadMessagesCount = unreadMessagesCount,
                                    friendRequestsCount = friendRequests.size,
                                    onCreateClick = { viewModel.setQuickCreateMenuOpen(true) }
                                )
                            }

                            // Notice when browsing cached feed offline
                            OfflineNoticeBanner(
                                visible = !isNetworkAvailable && isOfflineBrowsingMode,
                                onRetry = {
                                    viewModel.retryConnection { networkMonitor.isConnected() }
                                }
                            )
                        }
                    },
                    bottomBar = { },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (selectedTab) {
                            0 -> FeedScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                            1 -> FriendsScreen(viewModel = viewModel)
                            2 -> MessagingScreen(viewModel = viewModel)
                            3 -> VideosScreen(viewModel = viewModel)
                            4 -> NotificationsScreen(viewModel = viewModel)
                            5 -> MarketplaceScreen(viewModel = viewModel)
                            6 -> MenuScreen(viewModel = viewModel)
                            else -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }

            // MODALS & OVERLAYS
            if (isAuthModalOpen) {
                AuthModal(onDismiss = { viewModel.setAuthModalOpen(false) })
            }

            if (isSearchOpen) {
                SearchModal(viewModel = viewModel, onDismiss = { viewModel.setSearchOpen(false) })
            }

            if (isQuickCreateMenuOpen) {
                QuickCreateMenuModal(viewModel = viewModel, onDismiss = { viewModel.setQuickCreateMenuOpen(false) })
            }

            if (isAppSettingsOpen) {
                AppSettingsModal(viewModel = viewModel, onDismiss = { viewModel.setAppSettingsOpen(false) })
            }

            if (showCoinRechargeModal) {
                com.example.ui.components.live.CoinWalletRechargeSheet(
                    userCoins = userCoins,
                    onDismiss = { viewModel.setShowCoinRechargeModal(false) },
                    onPurchaseSuccess = { addedCoins ->
                        viewModel.addCoins(addedCoins)
                    }
                )
            }

            if (showDailyRewardsModal) {
                com.example.ui.components.home.DailyRewardsDialog(
                    streakDays = streakDays,
                    isClaimed = isDailyRewardClaimed,
                    onClaim = { coins ->
                        viewModel.claimDailyReward(coins)
                    },
                    onDismiss = { viewModel.setShowDailyRewardsModal(false) }
                )
            }

            // FULL SCREEN OVERLAYS
            AnimatedVisibility(
                visible = selectedPostForDetail != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                selectedPostForDetail?.let { post ->
                    PostDetailScreen(viewModel = viewModel, post = post)
                }
            }

            AnimatedVisibility(
                visible = selectedUserProfile != null,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                selectedUserProfile?.let { userProfile ->
                    OtherUserProfileScreen(
                        user = userProfile,
                        viewModel = viewModel,
                        onBack = { viewModel.closeUserProfile() }
                    )
                }
            }

            incomingCall?.let { call ->
                com.example.ui.screens.IncomingCallMessengerOverlay(
                    call = call,
                    onAnswer = { viewModel.answerCall(call) },
                    onDecline = { viewModel.declineCall() }
                )
            }

            // Interactive High-End Call Overlay
            if (isCallActive && activeCall != null) {
                com.example.ui.screens.ActiveCallDialog(
                    call = activeCall!!,
                    onEndCall = { viewModel.endCall() },
                    messages = activeChatMessages,
                    onSendMessage = { viewModel.sendMessage(it) }
                )
            }

            // Live Streaming Full Screen Overlay
            val isLiveStreamingOpen by viewModel.isLiveStreamingOpen.collectAsState()
            AnimatedVisibility(
                visible = isLiveStreamingOpen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                com.example.ui.screens.LiveStreamingScreen(viewModel = viewModel)
            }

            // 20 Dancing Emojis floating upwards across the screen from inside the 3D logo
            val emojiBurstTrigger by viewModel.emojiBurstEvent.collectAsState()
            val emojiBurstOrigin by viewModel.emojiBurstOrigin.collectAsState()
            FloatingDancingEmojisOverlay(
                burstTrigger = emojiBurstTrigger,
                burstOrigin = emojiBurstOrigin
            )

            // Removed LivePreviewFloatingWindow PiP
        }
    }
}
