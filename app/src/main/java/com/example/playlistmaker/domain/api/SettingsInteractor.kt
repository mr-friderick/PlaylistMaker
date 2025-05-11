package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun save(state: Boolean)

    fun read(): Boolean
}