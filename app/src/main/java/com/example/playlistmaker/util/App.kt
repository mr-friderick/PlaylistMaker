package com.example.playlistmaker.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.interactors.SettingsInteractor

class App : Application() {
    private val settingsInteractor: SettingsInteractor by lazy {
        Creator.provideSettingInteractor()
    }
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

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