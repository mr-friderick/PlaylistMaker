package com.example.playlistmaker.medialibrary.ui.viewmodel

import com.example.playlistmaker.newplaylist.domain.models.Playlist

sealed interface ListPlaylistsViewState {
    data object Empty: ListPlaylistsViewState

    data class Content(val playlists: List<Playlist>): ListPlaylistsViewState
}