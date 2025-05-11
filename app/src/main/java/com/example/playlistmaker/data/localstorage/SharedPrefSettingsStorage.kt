package com.example.playlistmaker.data.localstorage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.data.SettingsStorage

class SharedPrefSettingsStorage(
    private val sharedPrefs: SharedPreferences
): SettingsStorage {
    companion object {
        const val KEY_SETTINGS = "dark_theme"
    }

    override fun save(state: Boolean) {
        sharedPrefs.edit {
            putBoolean(KEY_SETTINGS, state)
        }
    }

    override fun read(): Boolean {
        return sharedPrefs.getBoolean(KEY_SETTINGS, false)
    }
}