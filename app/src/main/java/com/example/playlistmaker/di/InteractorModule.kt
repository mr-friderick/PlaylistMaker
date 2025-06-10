package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksIntreractorImpl
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.interactors.SettingsInteractor
import org.koin.dsl.module

val interactorModule = module {

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }

    factory<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    factory<TracksInteractor> {
        TracksIntreractorImpl(get())
    }

}