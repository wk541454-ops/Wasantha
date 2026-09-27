package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "friendhub_settings")

class PreferencesManager(private val context: Context) {
    companion object {
        val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val LOGGED_IN_USER_ID_KEY = stringPreferencesKey("logged_in_user_id")
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        val PUSH_NOTIF_KEY = booleanPreferencesKey("push_notif")
        val COMMENTS_NOTIF_KEY = booleanPreferencesKey("comments_notif")
        val TAGS_NOTIF_KEY = booleanPreferencesKey("tags_notif")
        val FRIEND_REQ_NOTIF_KEY = booleanPreferencesKey("friend_req_notif")
        val DO_NOT_DISTURB_KEY = booleanPreferencesKey("do_not_disturb")
        val AUTO_UPDATE_WIFI_KEY = booleanPreferencesKey("auto_update_wifi")
        val HD_VIDEO_UPLOAD_KEY = booleanPreferencesKey("hd_video_upload")
        val HD_PHOTO_UPLOAD_KEY = booleanPreferencesKey("hd_photo_upload")
        val SOUND_EFFECTS_KEY = booleanPreferencesKey("sound_effects")
        val READ_RECEIPTS_KEY = booleanPreferencesKey("read_receipts")
        val TYPING_INDICATOR_KEY = booleanPreferencesKey("typing_indicator")
        val DATA_SAVER_KEY = booleanPreferencesKey("data_saver")
        val ACTIVE_STATUS_KEY = booleanPreferencesKey("active_status")
        val DEFAULT_POST_AUDIENCE_KEY = stringPreferencesKey("default_post_audience")
        val STORY_PRIVACY_KEY = stringPreferencesKey("story_privacy")
        val SELECTED_FONT_KEY = stringPreferencesKey("selected_font")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[IS_DARK_MODE_KEY] ?: true }

    val appLanguage: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[LANGUAGE_KEY] ?: "SI" }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[IS_LOGGED_IN_KEY] ?: false }

    val loggedInUserId: Flow<String?> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[LOGGED_IN_USER_ID_KEY] }

    val pushNotif: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PUSH_NOTIF_KEY] ?: true }

    val commentsNotif: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[COMMENTS_NOTIF_KEY] ?: true }

    val tagsNotif: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[TAGS_NOTIF_KEY] ?: true }

    val friendReqNotif: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[FRIEND_REQ_NOTIF_KEY] ?: true }

    val doNotDisturb: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[DO_NOT_DISTURB_KEY] ?: false }

    val autoUpdateWifi: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[AUTO_UPDATE_WIFI_KEY] ?: true }

    val hdVideoUpload: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[HD_VIDEO_UPLOAD_KEY] ?: true }

    val hdPhotoUpload: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[HD_PHOTO_UPLOAD_KEY] ?: true }

    val soundEffects: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[SOUND_EFFECTS_KEY] ?: true }

    val readReceipts: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[READ_RECEIPTS_KEY] ?: true }

    val typingIndicator: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[TYPING_INDICATOR_KEY] ?: true }

    val dataSaver: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[DATA_SAVER_KEY] ?: false }

    val activeStatus: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[ACTIVE_STATUS_KEY] ?: true }

    val defaultPostAudience: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[DEFAULT_POST_AUDIENCE_KEY] ?: "Public" }

    val storyPrivacy: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[STORY_PRIVACY_KEY] ?: "Friends" }

    val selectedFont: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[SELECTED_FONT_KEY] ?: "Default" }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
    }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDarkMode
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean, userId: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
            if (userId != null) {
                preferences[LOGGED_IN_USER_ID_KEY] = userId
            } else if (!isLoggedIn) {
                preferences.remove(LOGGED_IN_USER_ID_KEY)
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences.remove(LOGGED_IN_USER_ID_KEY)
        }
    }

    suspend fun saveSettingBool(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun saveSettingString(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}
