package com.example.playlistmaker.data

interface SettingsStorage {
    fun save(state: Boolean)

    fun read(): Boolean
}