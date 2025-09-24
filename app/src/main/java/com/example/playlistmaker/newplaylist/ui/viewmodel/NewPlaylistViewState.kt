package com.example.playlistmaker.newplaylist.ui.viewmodel

import com.example.playlistmaker.newplaylist.domain.models.Playlist

sealed interface NewPlaylistViewState {
    data object Default: NewPlaylistViewState
    data class EditingPlaylist(val model: Playlist): NewPlaylistViewState
}