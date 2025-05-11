package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.SettingsInteractor

class App : Application() {
    private val settingsInteractor: SettingsInteractor by lazy {
        Creator.provideSettingInteractor(this)
    }
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        setupThemeStatus()
        switchTheme(darkTheme)
    }

    private fun setupThemeStatus() {
        darkTheme = settingsInteractor.read()
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
        settingsInteractor.save(darkThemeEnabled)
    }
}