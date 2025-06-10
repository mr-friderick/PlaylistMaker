package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefSettingsStorage(
    private val sharedPrefs: SharedPreferences
): SettingsStorage {

    override fun save(state: Boolean) {
        sharedPrefs.edit {
            putBoolean(KEY_SETTINGS, state)
        }
    }

    override fun read(): Boolean {
        return sharedPrefs.getBoolean(KEY_SETTINGS, false)
    }

    companion object {
        const val KEY_SETTINGS = "dark_theme"
    }
}