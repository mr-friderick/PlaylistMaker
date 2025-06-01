package com.example.playlistmaker.settings.domain.interactors

interface SettingsInteractor {
    fun save(state: Boolean)

    fun read(): Boolean
}