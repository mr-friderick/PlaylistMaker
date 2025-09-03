package com.example.playlistmaker.playlist.ui.viewmodel

import com.example.playlistmaker.newplaylist.domain.models.Playlist

sealed interface PlaylistViewState {
    data class Default(val model: Playlist): PlaylistViewState
}