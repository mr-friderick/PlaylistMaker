package com.example.playlistmaker.di

import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import com.example.playlistmaker.medialibrary.ui.viewmodel.ListPlaylistViewModel
import com.example.playlistmaker.newplaylist.ui.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SettingsViewModel(get())
    }

    viewModel { (trackModel: String) ->
        PlayerViewModel(get(), get(), get(), get(), trackModel)
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        FavoritesTracksViewModel(get())
    }

    viewModel {
        ListPlaylistViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }
}