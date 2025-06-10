package com.example.playlistmaker.di

import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.interactors.SettingsInteractor
import org.koin.dsl.module

val interactorModule = module {

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

}