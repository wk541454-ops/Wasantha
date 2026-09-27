package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.FirebaseAuthManager
import com.example.auth.PhoneAuthHelper
import com.example.data.PreferencesManager
import com.example.BuildConfig
import com.example.model.CallLogItem
import com.example.model.ChatSummary
import com.example.model.LiveChatMessage
import com.example.model.LiveGift
import com.example.model.MarketplaceItem
import com.example.model.MediaType
import com.example.model.Message
import com.example.model.Post
import com.example.model.Story
import com.example.model.User
import com.example.repository.FriendHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import androidx.compose.ui.geometry.Offset
import java.util.concurrent.TimeUnit

// --- Gemini API Data Classes ---
@Serializable
data class Content(val parts: List<Part>)

@Serializable
data class Part(val text: String)

@Serializable
data class GenerateContentRequest(val contents: List<Content>)

@Serializable
data class Candidate(val content: Content)

@Serializable
data class GenerateContentResponse(val candidates: List<Candidate>)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    val geminiService: GeminiApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(GeminiApiService::class.java)
    }
}

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {
    val repository = FriendHubRepository()
    private val preferencesManager = PreferencesManager(application)


    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow("EN") // "SI", "TA", "EN"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _mediaAutoSave = MutableStateFlow(true)
    val mediaAutoSave: StateFlow<Boolean> = _mediaAutoSave.asStateFlow()

    // Filters & Security Toggles
    private val _feedFilter = MutableStateFlow("ALL") // ALL, TRENDING, MEDIA, ENCRYPTED
    val feedFilter: StateFlow<String> = _feedFilter.asStateFlow()

    private val _e2eEncryptionEnabled = MutableStateFlow(true)
    val e2eEncryptionEnabled: StateFlow<Boolean> = _e2eEncryptionEnabled.asStateFlow()

    private val _marketplaceCategory = MutableStateFlow("ALL")
    val marketplaceCategory: StateFlow<String> = _marketplaceCategory.asStateFlow()

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val _isCheckingConnection = MutableStateFlow(false)
    val isCheckingConnection: StateFlow<Boolean> = _isCheckingConnection.asStateFlow()

    private val _isOfflineBrowsingMode = MutableStateFlow(true)
    val isOfflineBrowsingMode: StateFlow<Boolean> = _isOfflineBrowsingMode.asStateFlow()

    private val _selectedFont = MutableStateFlow("Default") // Default, Poppins, Bubblegum, Playfair, Montserrat
    val selectedFont: StateFlow<String> = _selectedFont.asStateFlow()

    init {
        try {
            FirebaseAuthManager.init(application)
        } catch (t: Throwable) {
            android.util.Log.w("MainViewModel", "FirebaseAuthManager init error: ${t.message}")
        }
        viewModelScope.launch {
            try {
                preferencesManager.isDarkMode.collect { _isDarkMode.value = it }
            } catch (t: Throwable) {
                _isDarkMode.value = true
            }
        }
        viewModelScope.launch {
            try {
                preferencesManager.isLoggedIn.collect { savedLoggedIn ->
                    val fbLoggedIn = try { FirebaseAuthManager.isLoggedIn() } catch (t: Throwable) { false }
                    _isLoggedIn.value = savedLoggedIn || fbLoggedIn
                }
            } catch (t: Throwable) {
                _isLoggedIn.value = false
            }
        }
        viewModelScope.launch {
            try {
                FirebaseAuthManager.isUserLoggedIn.collect { fbLoggedIn ->
                    if (fbLoggedIn) {
                        _isLoggedIn.value = true
                        val fbUser = FirebaseAuthManager.currentUser.value
                        if (fbUser != null) {
                            val current = repository.currentUser.value
                            val updated = current.copy(
                                id = fbUser.uid,
                                email = fbUser.email ?: current.email,
                                phone = fbUser.phoneNumber ?: current.phone,
                                name = fbUser.displayName ?: current.name.ifBlank { "FriendHub Member" }
                            )
                            repository.updateUserProfile(updated)
                            preferencesManager.setLoggedIn(true, fbUser.uid)
                        }
                    }
                }
            } catch (t: Throwable) {
                android.util.Log.w("MainViewModel", "Auth collector error: ${t.message}")
            }
        }
        viewModelScope.launch {
            try {
                repository.currentUser.collect { user ->
                    if (user != null && user.id.isNotBlank()) {
                        repository.observeConversations()
                    }
                }
            } catch (t: Throwable) {
                android.util.Log.w("MainViewModel", "User collector error: ${t.message}")
            }
        }
    }

    fun toggleDarkMode() {
        setDarkMode(!_isDarkMode.value)
    }

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val currentUser = repository.currentUser
    val posts = repository.posts
    val businessPosts = repository.businessPosts
    val stories = repository.stories
    val reels = repository.reels
    val chats = repository.chats
    val marketplaceItems = repository.marketplaceItems
    val callLogs = repository.callLogs
    val friendRequests = repository.friendRequests
    val allFriends = repository.allFriends
    val notifications = repository.notifications
    val profileVisitors = repository.profileVisitors
    val isPeerTyping = repository.isPeerTyping

    fun recordProfileVisitor(user: User) {
        repository.recordProfileVisitor(user)
    }

    fun simulateProfileVisitor() {
        repository.simulateProfileVisitor()
    }

    fun getUserById(userId: String): User? {
        return repository.getUserById(userId)
    }

    fun openUserProfile(user: User) {
        viewUserProfile(user)
    }

    fun updateUserProfile(user: User) {
        repository.updateUserProfile(user)
    }

    fun setProfileLock(locked: Boolean) {
        val current = repository.currentUser.value
        repository.updateUserProfile(current.copy(isProfileLocked = locked))
    }

    fun toggleProfileLock() {
        val current = repository.currentUser.value
        repository.updateUserProfile(current.copy(isProfileLocked = !current.isProfileLocked))
    }

    fun updateProfileNote(note: String?) {
        val current = repository.currentUser.value
        repository.updateUserProfile(current.copy(profileNote = note))
    }
    val incomingCall = repository.incomingCall

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val unreadMessagesCount: StateFlow<Int> = repository.conversations.map { list ->
        list.sumOf { it.unreadCount }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val activeLiveSession = repository.activeLiveSession
    val liveMessagesList = repository.liveMessagesList

    // Active Chat
    private val _activeChat = MutableStateFlow<ChatSummary?>(null)
    val activeChat: StateFlow<ChatSummary?> = _activeChat.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<Message>>(emptyList())
    val activeChatMessages: StateFlow<List<Message>> = _activeChatMessages.asStateFlow()

    // Active Call Simulation
    private val _activeCall = MutableStateFlow<CallLogItem?>(null)
    val activeCall: StateFlow<CallLogItem?> = _activeCall.asStateFlow()

    private val _isCallActive = MutableStateFlow(false)
    val isCallActive: StateFlow<Boolean> = _isCallActive.asStateFlow()

    // Post Detail Screen
    private val _selectedPostForDetail = MutableStateFlow<Post?>(null)
    val selectedPostForDetail: StateFlow<Post?> = _selectedPostForDetail.asStateFlow()

    fun openPostDetail(post: Post) {
        _selectedPostForDetail.value = post
    }

    fun closePostDetail() {
        _selectedPostForDetail.value = null
    }

    // Story Viewing
    private val _activeStory = MutableStateFlow<Story?>(null)
    val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

    // Comments Modal
    private val _activePostForComments = MutableStateFlow<Post?>(null)
    val activePostForComments: StateFlow<Post?> = _activePostForComments.asStateFlow()

    // Dialog Visibilities
    private val _isCreatePostOpen = MutableStateFlow(false)
    val isCreatePostOpen: StateFlow<Boolean> = _isCreatePostOpen.asStateFlow()

    private val _activeBusinessContext = MutableStateFlow<com.example.model.BusinessPage?>(null)
    val activeBusinessContext: StateFlow<com.example.model.BusinessPage?> = _activeBusinessContext.asStateFlow()

    fun switchContextToBusiness(page: com.example.model.BusinessPage) {
        _activeBusinessContext.value = page
        selectTab(0)
    }

    fun updateBusinessProfile(page: com.example.model.BusinessPage) {
        _activeBusinessContext.value = page
    }

    fun switchContextToPersonal() {
        _activeBusinessContext.value = null
        setDarkMode(true) // Personal Account needs Dark theme
        selectTab(0)
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(isDark)
            _isDarkMode.value = isDark
        }
    }

    private val _isCreateStoryOpen = MutableStateFlow(false)
    val isCreateStoryOpen: StateFlow<Boolean> = _isCreateStoryOpen.asStateFlow()

    private val _isCreateMarketplaceOpen = MutableStateFlow(false)
    val isCreateMarketplaceOpen: StateFlow<Boolean> = _isCreateMarketplaceOpen.asStateFlow()

    private val _isAuthModalOpen = MutableStateFlow(false)
    val isAuthModalOpen: StateFlow<Boolean> = _isAuthModalOpen.asStateFlow()

    private val _isEditProfileOpen = MutableStateFlow(false)
    val isEditProfileOpen: StateFlow<Boolean> = _isEditProfileOpen.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    private val _isQuickCreateMenuOpen = MutableStateFlow(false)
    val isQuickCreateMenuOpen: StateFlow<Boolean> = _isQuickCreateMenuOpen.asStateFlow()

    private val _isAppSettingsOpen = MutableStateFlow(false)
    val isAppSettingsOpen: StateFlow<Boolean> = _isAppSettingsOpen.asStateFlow()

    // --- Live Streaming States ---
    private val _isLiveStreamingOpen = MutableStateFlow(false)
    val isLiveStreamingOpen: StateFlow<Boolean> = _isLiveStreamingOpen.asStateFlow()

    private val _liveStreamingState = MutableStateFlow("PRE_LIVE") // PRE_LIVE, LIVE, ENDED
    val liveStreamingState: StateFlow<String> = _liveStreamingState.asStateFlow()

    private val _liveStreamTitle = MutableStateFlow("")
    val liveStreamTitle: StateFlow<String> = _liveStreamTitle.asStateFlow()

    private val _liveStreamCategory = MutableStateFlow("Chatting")
    val liveStreamCategory: StateFlow<String> = _liveStreamCategory.asStateFlow()

    private val _liveViewerCount = MutableStateFlow(0)
    val liveViewerCount: StateFlow<Int> = _liveViewerCount.asStateFlow()

    private val _liveMessages = MutableStateFlow<List<LiveChatMessage>>(emptyList())
    val liveMessages: StateFlow<List<LiveChatMessage>> = _liveMessages.asStateFlow()

    private val _liveViewers = MutableStateFlow<List<User>>(emptyList())
    val liveViewers: StateFlow<List<User>> = _liveViewers.asStateFlow()

    private val _liveGifts = MutableStateFlow<List<LiveGift>>(emptyList())
    val liveGifts: StateFlow<List<LiveGift>> = _liveGifts.asStateFlow()

    private val _liveStreamDuration = MutableStateFlow(0L) // Seconds
    val liveStreamDuration: StateFlow<Long> = _liveStreamDuration.asStateFlow()
    
    // --- New Live Features & Gamification ---
    private val _userCoins = MutableStateFlow(1250)
    val userCoins: StateFlow<Int> = _userCoins.asStateFlow()

    private val _streakDays = MutableStateFlow(5)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _isDailyRewardClaimed = MutableStateFlow(false)
    val isDailyRewardClaimed: StateFlow<Boolean> = _isDailyRewardClaimed.asStateFlow()

    private val _showDailyRewardsModal = MutableStateFlow(false)
    val showDailyRewardsModal: StateFlow<Boolean> = _showDailyRewardsModal.asStateFlow()
    
    private val _showGiftSheet = MutableStateFlow(false)
    val showGiftSheet: StateFlow<Boolean> = _showGiftSheet.asStateFlow()
    
    private val _showCoinRechargeModal = MutableStateFlow(false)
    val showCoinRechargeModal: StateFlow<Boolean> = _showCoinRechargeModal.asStateFlow()

    private val _showShareSheet = MutableStateFlow(false)
    val showShareSheet: StateFlow<Boolean> = _showShareSheet.asStateFlow()

    private val _isLivePipActive = MutableStateFlow(false)
    val isLivePipActive: StateFlow<Boolean> = _isLivePipActive.asStateFlow()

    private val _emojiBurstEvent = MutableStateFlow<Long>(0L)
    val emojiBurstEvent: StateFlow<Long> = _emojiBurstEvent.asStateFlow()

    private val _emojiBurstOrigin = MutableStateFlow(Offset(80f, 120f))
    val emojiBurstOrigin: StateFlow<Offset> = _emojiBurstOrigin.asStateFlow()

    fun triggerEmojiBurst(origin: Offset? = null) {
        if (origin != null) {
            _emojiBurstOrigin.value = origin
        }
        _emojiBurstEvent.value = System.currentTimeMillis()
    }

    fun setLivePipActive(active: Boolean) {
        _isLivePipActive.value = active
    }

    fun spendCoins(amount: Int): Boolean {
        if (_userCoins.value >= amount) {
            _userCoins.value -= amount
            return true
        }
        return false
    }

    private var liveTimerJob: kotlinx.coroutines.Job? = null
    private var liveSimulationJob: kotlinx.coroutines.Job? = null

    fun setLiveStreamingOpen(open: Boolean) {
        _isLiveStreamingOpen.value = open
        if (!open) {
            _liveStreamingState.value = "PRE_LIVE"
            stopLiveTimer()
            liveSimulationJob?.cancel()
            liveSimulationJob = null
        }
    }

    fun setLiveStreamTitle(title: String) { _liveStreamTitle.value = title }
    fun setLiveStreamCategory(category: String) { _liveStreamCategory.value = category }

    fun startLiveStream() {
        repository.startLiveSession(_liveStreamTitle.value, _liveStreamCategory.value)
        _liveStreamingState.value = "LIVE"
        startLiveTimer()
    }

    fun endLiveStream() {
        activeLiveSession.value?.let { repository.endLiveSession(it.id) }
        _liveStreamingState.value = "ENDED"
        stopLiveTimer()
    }

    fun sendLiveChatMessage(text: String) {
        if (text.isBlank()) return
        activeLiveSession.value?.let { repository.sendLiveMessage(it.id, text) }
    }

    fun sendLiveGift(name: String, icon: String) {
        val newGift = LiveGift(name, icon, System.currentTimeMillis())
        _liveGifts.value = (_liveGifts.value + newGift).takeLast(5)
        
        // Add a system message to live chat
        sendLiveChatMessage("Sent $name $icon! 🎁")
        
        // Clear gift after animation
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _liveGifts.value = _liveGifts.value.filter { it.id != newGift.id }
        }
    }

    fun joinLiveSeat(seatId: Int, micOn: Boolean = true, camOn: Boolean = true) {
        activeLiveSession.value?.let { session ->
            val user = currentUser.value
            repository.joinLiveSeat(session.id, seatId, user.name, user.avatarUrl, micOn)
        }
    }
    
    fun setShowGiftSheet(show: Boolean) { _showGiftSheet.value = show }
    fun setShowCoinRechargeModal(show: Boolean) { _showCoinRechargeModal.value = show }
    fun setShowDailyRewardsModal(show: Boolean) { _showDailyRewardsModal.value = show }
    fun setShowShareSheet(show: Boolean) { _showShareSheet.value = show }
    fun addCoins(amount: Int) { _userCoins.value += amount }

    fun claimDailyReward(coins: Int) {
        if (!_isDailyRewardClaimed.value) {
            _userCoins.value += coins
            _isDailyRewardClaimed.value = true
            _streakDays.value += 1
        }
    }

    private fun startLiveTimer() {
        _liveStreamDuration.value = 0
        liveTimerJob?.cancel()
        liveTimerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _liveStreamDuration.value += 1
            }
        }
    }

    private fun stopLiveTimer() {
        liveTimerJob?.cancel()
        liveTimerJob = null
    }

    fun setFont(fontName: String) {
        _selectedFont.value = fontName
    }


    private val _aiModerationEnabled = MutableStateFlow(true)
    val aiModerationEnabled: StateFlow<Boolean> = _aiModerationEnabled.asStateFlow()

    private val _aiVerificationEnabled = MutableStateFlow(true)
    val aiVerificationEnabled: StateFlow<Boolean> = _aiVerificationEnabled.asStateFlow()

    private val _aiChatAssistantEnabled = MutableStateFlow(false)
    val aiChatAssistantEnabled: StateFlow<Boolean> = _aiChatAssistantEnabled.asStateFlow()

    private val _aiPriceExpertEnabled = MutableStateFlow(false)
    val aiPriceExpertEnabled: StateFlow<Boolean> = _aiPriceExpertEnabled.asStateFlow()

    private val _aiSmartShareEnabled = MutableStateFlow(true)
    val aiSmartShareEnabled: StateFlow<Boolean> = _aiSmartShareEnabled.asStateFlow()

    fun toggleAiModeration() {
        _aiModerationEnabled.value = !_aiModerationEnabled.value
        repository.isAiModerationEnabled = _aiModerationEnabled.value
    }

    fun toggleAiVerification() {
        _aiVerificationEnabled.value = !_aiVerificationEnabled.value
        repository.isAiVerificationEnabled = _aiVerificationEnabled.value
    }

    fun toggleAiChatAssistant() {
        _aiChatAssistantEnabled.value = !_aiChatAssistantEnabled.value
    }

    fun toggleAiPriceExpert() {
        _aiPriceExpertEnabled.value = !_aiPriceExpertEnabled.value
    }

    fun toggleAiSmartShare() {
        _aiSmartShareEnabled.value = !_aiSmartShareEnabled.value
    }

    fun deleteAccount(context: android.content.Context) {
        val currentUserId = currentUser.value.id
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseAuthManager.deleteAccount(
                onSuccess = {
                    com.example.repository.AccountManager.deleteAccount(currentUserId, context)
                    viewModelScope.launch {
                        preferencesManager.clearSession()
                        withContext(Dispatchers.Main) {
                            _isLoggedIn.value = false
                            _isAuthModalOpen.value = false
                            _selectedTab.value = 0
                        }
                    }
                },
                onError = {
                    com.example.repository.AccountManager.deleteAccount(currentUserId, context)
                    viewModelScope.launch {
                        preferencesManager.clearSession()
                        withContext(Dispatchers.Main) {
                            _isLoggedIn.value = false
                            _isAuthModalOpen.value = false
                            _selectedTab.value = 0
                        }
                    }
                }
            )
        }
    }

    fun updateActivity(context: android.content.Context) {
        if (isLoggedIn.value && isNetworkAvailable.value) {
            repository.updateActivity()
        }
    }

    fun onAppForeground() {
        if (isLoggedIn.value && isNetworkAvailable.value) {
            repository.updatePresence(true)
        }
    }

    fun onAppBackground() {
        if (isLoggedIn.value) {
            repository.updatePresence(false)
        }
    }

    // AI Assistant Search Result
    private val _aiAssistantResponse = MutableStateFlow<String?>(null)
    val aiAssistantResponse: StateFlow<String?> = _aiAssistantResponse.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    fun performAiSearch(query: String) {
        if (query.length < 3) {
            _aiAssistantResponse.value = null
            return
        }
        
        viewModelScope.launch {
            _isAiThinking.value = true
            _aiAssistantResponse.value = "Smart AI Assistant is thinking... 🤖✨"
            
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY" || apiKey == "MY_GEMINI_API_KEY") {
                    // Enhanced Simulation for Youth Feature Query
                    if (query.contains("youth", ignoreCase = true) || query.contains("community", ignoreCase = true)) {
                        _aiAssistantResponse.value = "FriendHub AI: To empower youth, I recommend adding 'AI-Led Peer Mentoring' and 'Digital Wellness Streaks' that reward screen-time breaks. This fosters a safer, more conscious community. 🌟🛡️"
                    } else {
                        _aiAssistantResponse.value = "AI Assistant: I'm in simulation mode! You asked about '$query'. In a connected app, I'd provide a deep, personalized response. 🚀"
                    }
                } else {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.geminiService.generateContent(
                            apiKey = apiKey,
                            request = GenerateContentRequest(
                                contents = listOf(Content(parts = listOf(Part(text = "You are the FriendHub Smart AI Assistant. A user is searching for: $query. Provide a very short, helpful, hyper-personalized response (max 2 sentences)."))))
                            )
                        )
                    }
                    _aiAssistantResponse.value = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                        ?: "I'm here to help, but I couldn't process that query."
                }
            } catch (e: Exception) {
                val errMsg = e.message ?: ""
                if (errMsg.contains("429") || errMsg.contains("resource_exhausted") || errMsg.contains("Quota exceeded")) {
                    _aiAssistantResponse.value = "FriendHub AI: ඔබගේ Gemini API Quota සීමාව ඉක්මවා ඇත (Rate limit reached). අපි දැන් ස්වයංක්‍රීයව Local Smart Simulation Mode එකට මාරු වී ඇත්තෙමු! 🚀✨"
                } else {
                    _aiAssistantResponse.value = "AI Assistant: FriendHub AI is currently processing millions of data points. Let me get back to you! (Error: ${errMsg})"
                }
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearAiSearch() {
        _aiAssistantResponse.value = null
    }

    // Tracks posts visited/viewed by user while they had data
    private val _visitedPostIds = MutableStateFlow<Set<String>>(setOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
    val visitedPostIds: StateFlow<Set<String>> = _visitedPostIds.asStateFlow()

    fun markPostVisited(postId: String) {
        if (!_visitedPostIds.value.contains(postId)) {
            _visitedPostIds.value = _visitedPostIds.value + postId
        }
    }

    fun setNetworkAvailable(available: Boolean) {
        val wasOffline = !_isNetworkAvailable.value
        _isNetworkAvailable.value = available
        if (available) {
            _isOfflineBrowsingMode.value = false
            if (wasOffline && isLoggedIn.value) {
                repository.updatePresence(true)
                // Refresh core data safely
                viewModelScope.launch {
                    repository.loadNextPageOfPosts(20)
                }
            }
        } else {
            if (isLoggedIn.value) {
                repository.updatePresence(false)
            }
        }
    }

    fun setOfflineBrowsingMode(enabled: Boolean) {
        _isOfflineBrowsingMode.value = enabled
    }

    fun retryConnection(checkStatus: () -> Boolean) {
        viewModelScope.launch {
            _isCheckingConnection.value = true
            kotlinx.coroutines.delay(1200) // FB Lite connection pulse animation delay
            val connected = checkStatus()
            _isNetworkAvailable.value = connected
            _isCheckingConnection.value = false
        }
    }

    fun logout() {
        FirebaseAuthManager.logout {
            viewModelScope.launch {
                preferencesManager.clearSession()
                withContext(Dispatchers.Main) {
                    _isLoggedIn.value = false
                    _isAuthModalOpen.value = false
                    _selectedTab.value = 0
                }
            }
        }
    }

    fun login() {
        val uid = FirebaseAuthManager.currentUser.value?.uid ?: currentUser.value.id
        viewModelScope.launch {
            preferencesManager.setLoggedIn(true, uid)
        }
        _isLoggedIn.value = true
        _selectedTab.value = 0
    }

    fun loginWithUser(user: User) {
        repository.updateUserProfile(user)
        viewModelScope.launch {
            preferencesManager.setLoggedIn(true, user.id)
        }
        _isLoggedIn.value = true
        _selectedTab.value = 0
    }

    fun updateUserSettings(
        hideReactionCounts: Boolean? = null,
        pushNotificationsEnabled: Boolean? = null,
        commentsNotificationsEnabled: Boolean? = null,
        tagsNotificationsEnabled: Boolean? = null,
        friendRequestsNotificationsEnabled: Boolean? = null,
        doNotDisturb: Boolean? = null,
        language: String? = null,
        autoUpdateOnWifi: Boolean? = null,
        hdVideoUpload: Boolean? = null,
        hdPhotoUpload: Boolean? = null,
        isProfileLocked: Boolean? = null,
        bio: String? = null,
        location: String? = null,
        work: String? = null,
        defaultPostAudience: String? = null,
        friendRequestAudience: String? = null,
        friendsListAudience: String? = null,
        storyPrivacy: String? = null,
        storyArchiveEnabled: Boolean? = null,
        whoCanPostOnProfile: String? = null,
        reviewTagsEnabled: Boolean? = null
    ) {
        val current = currentUser.value
        val updated = current.copy(
            hideReactionCounts = hideReactionCounts ?: current.hideReactionCounts,
            pushNotificationsEnabled = pushNotificationsEnabled ?: current.pushNotificationsEnabled,
            commentsNotificationsEnabled = commentsNotificationsEnabled ?: current.commentsNotificationsEnabled,
            tagsNotificationsEnabled = tagsNotificationsEnabled ?: current.tagsNotificationsEnabled,
            friendRequestsNotificationsEnabled = friendRequestsNotificationsEnabled ?: current.friendRequestsNotificationsEnabled,
            doNotDisturb = doNotDisturb ?: current.doNotDisturb,
            language = language ?: current.language,
            autoUpdateOnWifi = autoUpdateOnWifi ?: current.autoUpdateOnWifi,
            hdVideoUpload = hdVideoUpload ?: current.hdVideoUpload,
            hdPhotoUpload = hdPhotoUpload ?: current.hdPhotoUpload,
            isProfileLocked = isProfileLocked ?: current.isProfileLocked,
            bio = bio ?: current.bio,
            location = location ?: current.location,
            work = work ?: current.work,
            defaultPostAudience = defaultPostAudience ?: current.defaultPostAudience,
            friendRequestAudience = friendRequestAudience ?: current.friendRequestAudience,
            friendsListAudience = friendsListAudience ?: current.friendsListAudience,
            storyPrivacy = storyPrivacy ?: current.storyPrivacy,
            storyArchiveEnabled = storyArchiveEnabled ?: current.storyArchiveEnabled,
            whoCanPostOnProfile = whoCanPostOnProfile ?: current.whoCanPostOnProfile,
            reviewTagsEnabled = reviewTagsEnabled ?: current.reviewTagsEnabled
        )
        repository.updateUserProfile(updated)
    }

    fun editPost(postId: String, newContent: String) {
        repository.editPost(postId, newContent)
        val updated = repository.posts.value.find { it.id == postId }
        if (updated != null && _selectedPostForDetail.value?.id == postId) {
            _selectedPostForDetail.value = updated
        }
    }

    fun deletePost(postId: String) {
        repository.deletePost(postId)
        if (_selectedPostForDetail.value?.id == postId) {
            _selectedPostForDetail.value = null
        }
    }

    fun deleteStory(storyId: String) {
        repository.deleteStory(storyId)
        if (_activeStory.value?.id == storyId) {
            _activeStory.value = null
        }
    }

    fun deleteMessage(chatId: String, messageId: String) {
        repository.deleteMessage(chatId, messageId)
        if (_activeChat.value?.id == chatId) {
            _activeChatMessages.value = repository.getMessagesForChat(chatId)
        }
    }

    fun deleteChat(chatId: String) {
        repository.deleteChat(chatId)
        if (_activeChat.value?.id == chatId) {
            _activeChat.value = null
            _activeChatMessages.value = emptyList()
        }
    }

    fun blockUser(userId: String) {
        repository.blockUser(userId)
        val currentProfile = _selectedUserProfile.value
        if (currentProfile?.id == userId) {
            _selectedUserProfile.value = currentProfile.copy(isFriend = false, isFollowedByMe = false, isBlockedByMe = true)
        }
    }

    fun unblockUser(userId: String) {
        repository.unblockUser(userId)
    }

    fun replyToComment(postId: String, commentId: String, replyText: String) {
        repository.replyToComment(postId, commentId, replyText)
        val updated = repository.posts.value.find { it.id == postId }
        if (updated != null) {
            _selectedPostForDetail.value = updated
            _activePostForComments.value = updated
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    private var messageCollectionJob: kotlinx.coroutines.Job? = null

    fun openChat(chat: ChatSummary) {
        val peerUser = if (chat.peerUserId != null) {
            repository.getUserById(chat.peerUserId)
        } else {
            repository.getUserByName(chat.peerName)
        }
        val effectiveConnected = if (peerUser != null) {
            peerUser.isFriend || peerUser.isFollowedByMe || chat.isConnected
        } else {
            chat.isConnected
        }
        val finalChat = chat.copy(isConnected = effectiveConnected)
        _activeChat.value = finalChat
        
        messageCollectionJob?.cancel()
        messageCollectionJob = viewModelScope.launch {
            try {
                repository.observeMessages(chat.id).collect { msgs ->
                    _activeChatMessages.value = if (msgs.isNotEmpty()) msgs else repository.getMessagesForChat(chat.id)
                }
            } catch (t: Throwable) {
                android.util.Log.w("MainViewModel", "Error in observeMessages: ${t.message}")
                _activeChatMessages.value = repository.getMessagesForChat(chat.id)
            }
        }
    }

    fun closeChat() {
        _activeChat.value = null
        messageCollectionJob?.cancel()
        messageCollectionJob = null
    }

    fun sendMessage(content: String, mediaUrl: String? = null) {
        val chat = _activeChat.value ?: return
        if (content.isBlank() && mediaUrl == null) return
        repository.sendMessage(chat.id, content, mediaUrl)
    }

    fun addMessageReaction(messageId: String, reactionEmoji: String) {
        val chat = _activeChat.value ?: return
        repository.addMessageReaction(chat.id, messageId, reactionEmoji)
        _activeChatMessages.value = repository.getMessagesForChat(chat.id)
    }

    fun createGroup(groupName: String, members: List<User>) {
        repository.createGroup(groupName, members)
    }

    fun startCall(peerName: String, peerAvatar: String, type: String = "VIDEO", isGroup: Boolean = false, memberAvatars: List<String> = emptyList()) {
        val call = CallLogItem(
            peerName = peerName,
            peerAvatar = peerAvatar,
            type = type,
            direction = "OUTGOING",
            timestamp = "Just now",
            duration = "00:00",
            isGroup = isGroup,
            memberAvatars = memberAvatars
        )
        // Store group info in active call
        _activeCall.value = call
        _isCallActive.value = true
    }

    fun endCall() {
        _isCallActive.value = false
        _activeCall.value = null
        repository.dismissIncomingCall()
    }

    fun answerCall(call: CallLogItem) {
        _activeCall.value = call
        _isCallActive.value = true
        repository.dismissIncomingCall()
    }

    fun declineCall() {
        repository.dismissIncomingCall()
    }

    fun simulateIncomingCall() {
        repository.simulateIncomingCall()
    }

    fun openStory(story: Story) {
        _activeStory.value = story
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun openCommentsForPost(post: Post) {
        _activePostForComments.value = post
    }

    fun closeComments() {
        _activePostForComments.value = null
    }

    fun addCommentToActivePost(content: String) {
        val post = _activePostForComments.value ?: return
        if (content.isBlank()) return
        repository.addComment(post.id, content)
        // refresh active post ref
        val updatedPost = repository.posts.value.find { it.id == post.id }
        _activePostForComments.value = updatedPost
    }

    fun addCommentToPostInDetail(postId: String, content: String) {
        if (content.isBlank()) return
        repository.addComment(postId, content)
        // refresh both screen refs
        val updatedPost = repository.posts.value.find { it.id == postId }
        _selectedPostForDetail.value = updatedPost
        _activePostForComments.value = updatedPost
    }

    fun setPostReaction(postId: String, reaction: com.example.model.ReactionType) {
        repository.setReaction(postId, reaction)
    }

    fun togglePostLike(postId: String) {
        repository.toggleLike(postId)
    }

    fun incrementPostLikes(postId: String, count: Int = 1) {
        repository.incrementLikeCount(postId, count)
    }

    fun sharePost(postId: String) {
        repository.sharePost(postId)
    }

    fun togglePostSave(postId: String): Boolean {
        return repository.toggleSavePost(postId)
    }

    fun hidePost(postId: String) {
        repository.hidePost(postId)
    }

    val userReports = repository.userReports

    fun reportPost(postId: String, reason: String) {
        repository.reportPost(postId, reason)
    }

    fun submitReport(targetType: String, targetId: String, reason: String, details: String = "") {
        repository.submitReport(targetType, targetId, reason, details)
    }

    fun createBusinessPost(pageName: String, pageAvatar: String, content: String, mediaUrl: String? = null, mediaType: MediaType = MediaType.NONE) {
        if (content.isBlank() && mediaUrl == null) return
        repository.addBusinessPost(pageName, pageAvatar, content, mediaUrl, mediaType)
    }

    fun createPost(content: String, mediaUrl: String?, mediaType: MediaType, audience: String = "Public") {
        if (content.isBlank() && mediaUrl == null) return
        repository.addPost(content, mediaUrl, mediaType, audience)
        _isCreatePostOpen.value = false
    }

    fun createReel(videoUrl: String, caption: String) {
        if (videoUrl.isBlank()) return
        repository.addReel(videoUrl, caption)
    }

    fun createStory(mediaUrl: String?, caption: String, backgroundColor: String? = null) {
        repository.addStory(mediaUrl, caption, backgroundColor)
        _isCreateStoryOpen.value = false
    }

    fun createMarketplaceListing(title: String, price: Double, category: String, description: String, imageUrl: String) {
        repository.addMarketplaceItem(title, price, category, description, imageUrl)
        _isCreateMarketplaceOpen.value = false
    }

    fun setCreatePostOpen(open: Boolean) { _isCreatePostOpen.value = open }
    fun setCreateStoryOpen(open: Boolean) { _isCreateStoryOpen.value = open }
    fun setCreateMarketplaceOpen(open: Boolean) { _isCreateMarketplaceOpen.value = open }
    fun setAuthModalOpen(open: Boolean) { _isAuthModalOpen.value = open }
    fun setEditProfileOpen(open: Boolean) { _isEditProfileOpen.value = open }
    fun setSearchOpen(open: Boolean) { _isSearchOpen.value = open }
    fun setQuickCreateMenuOpen(open: Boolean) { _isQuickCreateMenuOpen.value = open }
    fun setAppSettingsOpen(open: Boolean) { _isAppSettingsOpen.value = open }
    fun sharePostToFriend(friendName: String, friendAvatar: String, content: String, postUrl: String?) {
        val existingChat = chats.value.find { it.peerName.equals(friendName, ignoreCase = true) }
        val chatId = existingChat?.id ?: "chat_share_${System.currentTimeMillis()}"
        if (existingChat == null) {
            repository.ensureChatSummary(ChatSummary(
                id = chatId,
                peerName = friendName,
                peerAvatar = friendAvatar,
                lastMessage = content,
                lastTimestamp = "Just now",
                isOnline = true,
                isConnected = true
            ))
        }
        val fullMsg = if (!postUrl.isNullOrBlank()) "$content\n$postUrl" else content
        repository.sendMessage(chatId, fullMsg)
    }
    fun setFeedFilter(filter: String) { _feedFilter.value = filter }
    fun setMarketplaceCategory(category: String) { _marketplaceCategory.value = category }
    fun setLanguage(langNameOrCode: String) {
        val code = when (langNameOrCode) {
            "සිංහල", "SI" -> "SI"
            "தமிழ் (Tamil)", "TA" -> "TA"
            else -> "EN"
        }
        _currentLanguage.value = code
        // Persist to user profile
        repository.updateUserProfile(currentUser.value.copy(language = code))
        try {
            val localeTag = when (code) {
                "SI" -> "si"
                "TA" -> "ta"
                else -> "en"
            }
            val locale = java.util.Locale(localeTag)
            java.util.Locale.setDefault(locale)
            val context = getApplication<Application>().applicationContext
            val config = android.content.res.Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun toggleE2EEncryption() { _e2eEncryptionEnabled.value = !_e2eEncryptionEnabled.value }
    fun toggleMediaAutoSave() { _mediaAutoSave.value = !_mediaAutoSave.value }

    fun downloadMedia(messageId: String) {
        val chat = _activeChat.value ?: return
        val currentMessages = _activeChatMessages.value
        val updatedMessages = currentMessages.map { msg ->
            if (msg.id == messageId) msg.copy(isMediaDownloaded = true) else msg
        }
        _activeChatMessages.value = updatedMessages
        // In a real app, this would also update the repository/database
    }

    fun acceptFriendRequest(requestId: String) {
        repository.acceptFriendRequest(requestId)
        val current = _selectedUserProfile.value
        if (current != null) {
            val updated = repository.getUserById(current.id)
            if (updated != null) _selectedUserProfile.value = updated
        }
    }

    fun deleteFriendRequest(requestId: String) {
        repository.deleteFriendRequest(requestId)
        val current = _selectedUserProfile.value
        if (current != null) {
            val updated = repository.getUserById(current.id)
            if (updated != null) _selectedUserProfile.value = updated
        }
    }

    fun toggleFollowFriendRequest(requestId: String) {
        repository.toggleFollowFriendRequest(requestId)
        val current = _selectedUserProfile.value
        if (current != null) {
            val updated = repository.getUserById(current.id)
            if (updated != null) _selectedUserProfile.value = updated
        }
    }

    val knownUsers = repository.knownUsers

    private val _selectedUserProfile = MutableStateFlow<User?>(null)
    val selectedUserProfile: StateFlow<User?> = _selectedUserProfile.asStateFlow()

    fun viewUserProfile(user: User) {
        val latest = repository.getUserById(user.id)
            ?: repository.getUserByName(user.name)
            ?: user
        _selectedUserProfile.value = latest
    }

    fun closeUserProfile() {
        _selectedUserProfile.value = null
    }

    fun unfriendUser(userId: String) {
        repository.unfriendUser(userId)
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isFriend = false, isFollowedByMe = false)
        }
        val currentChat = _activeChat.value
        if (currentChat != null && (currentChat.peerUserId == userId || currentChat.peerName.equals(current?.name, ignoreCase = true))) {
            _activeChat.value = currentChat.copy(isConnected = false)
        }
    }

    fun sendFriendRequest(userId: String) {
        repository.sendFriendRequest(userId)
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isRequestSent = true)
        }
    }

    fun cancelFriendRequest(userId: String) {
        repository.cancelFriendRequest(userId)
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isRequestSent = false)
        }
    }

    fun toggleFollowUser(userId: String): Boolean {
        val res = repository.toggleFollowUser(userId)
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isFollowedByMe = res)
        }
        val currentChat = _activeChat.value
        if (currentChat != null && (currentChat.peerUserId == userId || currentChat.peerName.equals(current?.name, ignoreCase = true))) {
            val user = repository.getUserById(userId)
            val isConn = res || (user?.isFriend == true)
            _activeChat.value = currentChat.copy(isConnected = isConn)
        }
        return res
    }

    fun snoozeUser(userId: String, days: Int = 30) {
        repository.snoozeUser(userId, days)
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isSnoozed = true)
        }
    }

    fun takeBreakFromUser(userId: String) {
        repository.takeBreakFromUser(userId)
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isBreakTaken = true)
        }
    }

    fun confirmFriendRequestFromProfile(userId: String) {
        val req = repository.friendRequests.value.find { it.userId == userId }
        if (req != null) {
            repository.acceptFriendRequest(req.id)
        } else {
            val user = repository.getUserById(userId)
            if (user != null) {
                repository.acceptFriendRequest(userId)
            }
        }
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isFriend = true, isRequestReceived = false)
        }
        val currentChat = _activeChat.value
        if (currentChat != null && (currentChat.peerUserId == userId || currentChat.peerName.equals(current?.name, ignoreCase = true))) {
            _activeChat.value = currentChat.copy(isConnected = true)
        }
    }

    fun rejectFriendRequestFromProfile(userId: String) {
        val req = repository.friendRequests.value.find { it.userId == userId }
        if (req != null) {
            repository.rejectFriendRequest(req.id)
        }
        val current = _selectedUserProfile.value
        if (current?.id == userId) {
            _selectedUserProfile.value = repository.getUserById(userId) ?: current.copy(isRequestReceived = false)
        }
    }

    fun openChatWithUser(user: User) {
        _selectedUserProfile.value = null // Close the profile overlay so chat is directly visible
        val latestUser = repository.getUserById(user.id) ?: repository.getUserByName(user.name) ?: user
        val isFriendOrFollowed = latestUser.isFriend || latestUser.isFollowedByMe
        val existingChat = chats.value.find { 
            (it.peerUserId != null && it.peerUserId == latestUser.id) || it.peerName.equals(latestUser.name, ignoreCase = true) 
        }
        val chat = (existingChat ?: ChatSummary(
            id = "chat_${latestUser.id}",
            peerUserId = latestUser.id,
            peerName = latestUser.name,
            peerAvatar = latestUser.avatarUrl,
            lastMessage = "Say hi to ${latestUser.name}!",
            lastTimestamp = "Just now",
            isOnline = latestUser.isOnline
        )).copy(
            isConnected = isFriendOrFollowed
        )
        repository.ensureChatSummary(chat)
        openChat(chat)
        selectTab(2)
    }

    fun openChatForBusiness(pageName: String, pageAvatar: String, pagePhone: String) {
        val existingChat = chats.value.find { it.peerName.equals(pageName, ignoreCase = true) }
        val chat = existingChat ?: ChatSummary(
            id = "chat_biz_${System.currentTimeMillis()}",
            peerName = pageName,
            peerAvatar = pageAvatar,
            lastMessage = "Hello! Welcome to $pageName. How can we help you today? (Messenger Business Chat)",
            lastTimestamp = "Just now",
            isOnline = true,
            isConnected = true
        )
        repository.ensureChatSummary(chat)
        openChat(chat)
        selectTab(2) // Switch to Messaging tab (Messenger)
    }

    fun connectOrFollowInChat(chatId: String, peerUserId: String?) {
        if (peerUserId != null) {
            val user = repository.getUserById(peerUserId)
            if (user != null && !user.isFollowedByMe) {
                repository.toggleFollowUser(peerUserId)
                val currentProfile = _selectedUserProfile.value
                if (currentProfile?.id == peerUserId) {
                    _selectedUserProfile.value = repository.getUserById(peerUserId)
                }
            }
        }
        repository.setChatConnected(chatId, true)
        val current = _activeChat.value
        if (current != null && current.id == chatId) {
            _activeChat.value = current.copy(isConnected = true)
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        repository.markNotificationAsRead(notificationId)
    }

    fun markAllNotificationsAsRead() {
        repository.markAllNotificationsAsRead()
    }

    fun sendLiveStreamInvite(friendName: String, friendAvatar: String) {
        val user = currentUser.value
        val notification = com.example.model.NotificationItem(
            id = "notif_live_invite_${System.currentTimeMillis()}",
            type = com.example.model.NotificationType.LIVE_STREAM,
            senderName = user.name,
            senderAvatar = user.avatarUrl,
            message = "invited $friendName to join their Live Stream! 🔴 Click to watch & join co-host seat.",
            timestamp = "Just now",
            isRead = false,
            targetId = "live_stream_123"
        )
        repository.addNotification(notification)
    }

    fun reactToStory(storyId: String, emoji: String) {
        repository.reactToStory(storyId, emoji)
    }

    fun sendStoryCommentNotification(story: Story, comment: String) {
        val me = currentUser.value
        reactToStory(story.id, "💬")
        val notification = com.example.model.NotificationItem(
            type = com.example.model.NotificationType.COMMENT,
            senderName = me.name,
            senderAvatar = me.avatarUrl,
            message = "commented on your story: \"$comment\"",
            timestamp = "Just now",
            targetId = story.id
        )
        repository.addNotification(notification)
    }

    fun toggleStoryLike(story: Story) {
        val me = currentUser.value
        reactToStory(story.id, "❤️")
        val notification = com.example.model.NotificationItem(
            type = com.example.model.NotificationType.LIKE,
            senderName = me.name,
            senderAvatar = me.avatarUrl,
            message = "liked your story",
            timestamp = "Just now",
            targetId = story.id
        )
        repository.addNotification(notification)
    }

    fun onNotificationClick(notification: com.example.model.NotificationItem) {
        markNotificationAsRead(notification.id)
        
        if (notification.type == com.example.model.NotificationType.LIVE_STREAM) {
            setLiveStreamingOpen(true)
            return
        }
        
        val targetId = notification.targetId ?: return
        
        when (notification.type) {
            com.example.model.NotificationType.LIKE, 
            com.example.model.NotificationType.COMMENT,
            com.example.model.NotificationType.REACTION,
            com.example.model.NotificationType.FRIEND_NEW_POST -> {
                // Navigate to post detail view
                val post = repository.posts.value.find { it.id == targetId }
                if (post != null) {
                    openPostDetail(post)
                }
            }
            com.example.model.NotificationType.FRIEND_REQUEST,
            com.example.model.NotificationType.ACCEPT_REQUEST -> {
                // Navigate to Friends tab
                selectTab(1)
            }
            else -> {
                // Default action: Notification is marked as read
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        repository.deleteNotification(notificationId)
    }

    fun refreshFeed() {
        repository.refreshPosts()
    }

    fun loadNextPageOfPosts() {
        repository.loadNextPageOfPosts()
    }

    // --- Creator Unlock & AdMob Monetization ---
    private val _isCreatorDashboardOpen = MutableStateFlow(false)
    val isCreatorDashboardOpen: StateFlow<Boolean> = _isCreatorDashboardOpen.asStateFlow()

    private val _isRewardedAdOpen = MutableStateFlow(false)
    val isRewardedAdOpen: StateFlow<Boolean> = _isRewardedAdOpen.asStateFlow()

    private val _isInterstitialAdOpen = MutableStateFlow(false)
    val isInterstitialAdOpen: StateFlow<Boolean> = _isInterstitialAdOpen.asStateFlow()

    fun setCreatorDashboardOpen(isOpen: Boolean) {
        _isCreatorDashboardOpen.value = isOpen
    }

    fun setRewardedAdOpen(isOpen: Boolean) {
        _isRewardedAdOpen.value = isOpen
    }

    fun setInterstitialAdOpen(isOpen: Boolean) {
        _isInterstitialAdOpen.value = isOpen
    }

    fun followUserWithGuardrail(targetUserId: String): Boolean {
        return repository.followUserWithGuardrail(targetUserId)
    }

    fun addVirtualCoins(coins: Int) {
        repository.addVirtualCoins(coins)
    }

    fun auditAccountForMonetization() {
        repository.auditAccountForMonetization()
    }

    fun subscribeBusinessAccount() {
        repository.subscribeBusinessAccount()
    }
}

