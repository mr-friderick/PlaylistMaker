package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.util.Creator

class SettingsViewModel: ViewModel() {

    private val settingsInteractor = Creator.provideSettingInteractor()

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