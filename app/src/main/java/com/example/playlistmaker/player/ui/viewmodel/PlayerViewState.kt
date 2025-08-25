package com.example.playlistmaker.player.ui.viewmodel

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlayerViewState {
    data class Default(val trackIsFavorite: Boolean, val trackModel: Track,  val trackTime: String = "0:00"): PlayerViewState
    data class Prepared(val trackIsFavorite: Boolean, val trackTime: String = "0:00"): PlayerViewState
    data class Playing(val trackIsFavorite: Boolean, val trackTime: String, val isPlayButtonEnabled: Boolean = true): PlayerViewState
    data class Paused(val trackIsFavorite: Boolean, val trackTime: String): PlayerViewState
    data class Completed(val trackIsFavorite: Boolean, val trackTime: String = "0:00"): PlayerViewState
    data class Playlists(val playlists: List<Playlist>): PlayerViewState
    data class ResultAddTrack(val success: Boolean, val messageId: Int, val playlistTitle: String): PlayerViewState
}