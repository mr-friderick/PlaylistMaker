package com.example.playlistmaker.data.localstorage

import com.example.playlistmaker.data.SettingsStorage
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage): SettingsRepository {
    override fun save(state: Boolean) {
        settingsStorage.save(state)
    }

    override fun read(): Boolean {
        return settingsStorage.read()
    }

}