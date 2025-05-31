package com.example.playlistmaker.util

import android.content.Context
import com.example.playlistmaker.search.data.localstorage.HistoryRepositoryImpl
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.localstorage.SharedPrefHistoryStorage
import com.example.playlistmaker.settings.data.SharedPrefSettingsStorage
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.settings.domain.interactors.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksIntreractorImpl
import com.example.playlistmaker.player.data.MediaPlayerController
import com.example.playlistmaker.player.data.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.google.gson.Gson

object Creator {
    private const val FILE_HISTORY_PREFERENCES = "history_preferences"
    private const val FILE_SETTINGS_PREFERENCES = "settings_preferences"

    // Search tracks --------------------------------------------
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksIntreractorImpl(getTracksRepository())
    }

    // Tracks history -------------------------------------------
    private fun getHistoryRepository(context: Context): HistoryRepository {
        val sharedPrefs = context.getSharedPreferences(FILE_HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        return HistoryRepositoryImpl(SharedPrefHistoryStorage(sharedPrefs, Gson()))
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(context))
    }

    // Settings -------------------------------------------------
    private fun getSettingsRepository(context: Context): SettingsRepository {
        val sharedPrefs = context.getSharedPreferences(FILE_SETTINGS_PREFERENCES, Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(SharedPrefSettingsStorage(sharedPrefs))
    }

    fun provideSettingInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    // Player
    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(MediaPlayerController())
    }

    fun providePlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }

}