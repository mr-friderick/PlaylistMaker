package com.example.playlistmaker.playlist.ui.viewmodel

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistViewState {
    data class Default(val model: Playlist, val tracks: List<Track>, val playlistTime: String): PlaylistViewState
}