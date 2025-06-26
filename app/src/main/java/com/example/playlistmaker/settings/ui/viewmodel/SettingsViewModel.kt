package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactors.SettingsInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
): ViewModel() {

    private val theme = MutableLiveData(false)
    val themeLiveData: LiveData<Boolean> = theme

    fun setupThemeSwitcher() {
        theme.value = settingsInteractor.read()
    }

    fun onThemeSwitcherClicked(currentValue: Boolean) {
        settingsInteractor.save(currentValue)
        theme.value = currentValue
    }

}