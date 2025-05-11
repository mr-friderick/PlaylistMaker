package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.ui.settings.SettingsActivity

class App : Application() {

    private val settingsSharedPrefs: SharedPreferences by lazy {
        getSharedPreferences(SettingsActivity.SETTINGS_PREFERENCES, MODE_PRIVATE)
    }
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        setupThemeStatus()
        switchTheme(darkTheme)
    }

    private fun setupThemeStatus() {
        darkTheme = settingsSharedPrefs.getBoolean(SettingsActivity.DARK_THEME, darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        settingsSharedPrefs.edit()
            .putBoolean(SettingsActivity.DARK_THEME, darkThemeEnabled)
            .apply()
    }
}