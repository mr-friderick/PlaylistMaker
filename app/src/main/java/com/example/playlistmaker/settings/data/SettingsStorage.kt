package com.example.playlistmaker.settings.data

interface SettingsStorage {
    fun save(state: Boolean)

    fun read(): Boolean
}