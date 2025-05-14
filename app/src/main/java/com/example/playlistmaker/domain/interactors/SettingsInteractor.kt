package com.example.playlistmaker.domain.interactors

interface SettingsInteractor {
    fun save(state: Boolean)

    fun read(): Boolean
}