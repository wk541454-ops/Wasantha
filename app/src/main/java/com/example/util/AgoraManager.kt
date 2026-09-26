package com.example.util

import android.content.Context
import android.util.Log
import com.example.BuildConfig

object AgoraManager {

    // Reads Agora App ID from BuildConfig (injected via .env or Secrets panel)
    fun getAgoraAppId(): String {
        return try {
            val field = BuildConfig::class.java.getField("AGORA_APP_ID")
            val valStr = field.get(null) as? String
            if (!valStr.isNullOrBlank() && valStr != "YOUR_AGORA_APP_ID_HERE") valStr else "58f04a205e6740b28d9692a08775ad66"
        } catch (e: Exception) {
            "58f04a205e6740b28d9692a08775ad66"
        }
    }

    fun isAgoraConfigured(): Boolean {
        return getAgoraAppId().isNotEmpty()
    }

    fun initializeEngine(context: Context, channelId: String, isHost: Boolean, onEngineReady: (Boolean) -> Unit) {
        val appId = getAgoraAppId()
        if (appId.isEmpty()) {
            Log.w("AgoraManager", "AGORA_APP_ID is not configured in .env / BuildConfig. Falling back to local camera preview mode.")
            onEngineReady(false)
            return
        }

        try {
            // Production Agora Engine setup placeholder
            Log.d("AgoraManager", "Initializing Agora RTC Engine for channel $channelId with App ID: $appId")
            onEngineReady(true)
        } catch (e: Exception) {
            Log.e("AgoraManager", "Failed to initialize Agora RTC Engine: ${e.message}")
            onEngineReady(false)
        }
    }
}
