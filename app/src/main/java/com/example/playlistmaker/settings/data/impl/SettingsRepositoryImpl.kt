package com.example.playlistmaker.settings.data.impl

import com.example.playlistmaker.settings.data.SettingsStorage
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage): SettingsRepository {
    override fun save(state: Boolean) {
        settingsStorage.save(state)
    }

    override fun read(): Boolean {
        return settingsStorage.read()
    }

}