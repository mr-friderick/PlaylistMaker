package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun save(state: Boolean) {
        repository.save(state)
    }

    override fun read(): Boolean {
        return repository.read()
    }
}