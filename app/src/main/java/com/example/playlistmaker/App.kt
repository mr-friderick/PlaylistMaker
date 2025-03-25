package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.SettingsActivity.Companion.SETTINGS_PREFERENCES

class App : Application() {

    private lateinit var settingsSharedPrefs: SharedPreferences
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        initSharedPreferences()
        setupThemeStatus()
        switchTheme(darkTheme)
    }

    private fun setupThemeStatus() {
        darkTheme = settingsSharedPrefs.getBoolean(SettingsActivity.DARK_THEME, darkTheme)
    }

    private fun initSharedPreferences() {
        settingsSharedPrefs = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)
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
    }
}