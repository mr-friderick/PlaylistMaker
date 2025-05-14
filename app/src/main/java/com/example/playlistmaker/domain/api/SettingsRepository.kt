package com.example.playlistmaker.domain.api

interface SettingsRepository {
    fun save(state: Boolean)

    fun read(): Boolean
}