package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun save(state: Boolean)

    fun read(): Boolean
}