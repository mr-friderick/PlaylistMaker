package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.localstorage.HistoryRepositoryImpl
import com.example.playlistmaker.data.localstorage.SharedPrefHistoryStorage
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksIntreractorImpl
import com.google.gson.Gson

object Creator {
    const val FILE_HISTORY_PREFERENCES = "history_preferences"

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
}