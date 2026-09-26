package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val LOGGED_IN_USER_ID_KEY = stringPreferencesKey("logged_in_user_id")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch {
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: true
        }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch {
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }

    val loggedInUserId: Flow<String?> = context.dataStore.data
        .catch {
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { preferences ->
            preferences[LOGGED_IN_USER_ID_KEY]
        }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences.remove(LOGGED_IN_USER_ID_KEY)
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
}
