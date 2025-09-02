package com.example.playlistmaker.medialibrary.ui.viewmodel

import com.example.playlistmaker.newplaylist.domain.models.Playlist

sealed interface PlaylistsViewState {
    data object Empty: PlaylistsViewState

    data class Content(val playlists: List<Playlist>): PlaylistsViewState
}