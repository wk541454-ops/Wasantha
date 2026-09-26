package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Ensures camera permission is requested AT MOST ONCE per user/install.
 * "Camarawa open karaddi nitharama pramison illanna epa eka wathawak illanna"
 */
object CameraPermissionManager {
    private const val PREFS_NAME = "friendhub_camera_prefs"
    private const val KEY_PERMISSION_ASKED = "camera_perm_asked_once"

    fun isPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAskedPermission(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_PERMISSION_ASKED, false)
    }

    fun markPermissionAsked(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_PERMISSION_ASKED, true).apply()
    }

    /**
     * Returns true if and only if permission has NEVER been requested before and is not yet granted.
     * Subsequent attempts will return false, preventing repeated permission prompts.
     */
    fun shouldRequestPermission(context: Context): Boolean {
        if (isPermissionGranted(context)) return false
        val alreadyAsked = hasAskedPermission(context)
        if (!alreadyAsked) {
            markPermissionAsked(context)
            return true
        }
        return false
    }
}
