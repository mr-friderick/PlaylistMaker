package com.example.playlistmaker.medialibrary.ui.viewmodel

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksViewState {
    data object Empty: FavoriteTracksViewState

    data class Content(val favoriteTracks: List<Track>): FavoriteTracksViewState
}