package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.interactors.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun save(state: Boolean) {
        repository.save(state)
    }

    override fun read(): Boolean {
        return repository.read()
    }
}