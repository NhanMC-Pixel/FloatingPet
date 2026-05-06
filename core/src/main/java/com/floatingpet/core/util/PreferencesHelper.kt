package com.floatingpet.core.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    private const val PREFS_NAME = "floating_pet_prefs"
    private const val KEY_MUSIC_APP_PACKAGE = "music_app_package"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getMusicAppPackage(context: Context): String {
        return getPrefs(context).getString(KEY_MUSIC_APP_PACKAGE, "com.spotify.music") ?: "com.spotify.music"
    }

    fun setMusicAppPackage(context: Context, packageName: String) {
        getPrefs(context).edit().putString(KEY_MUSIC_APP_PACKAGE, packageName).apply()
    }
}