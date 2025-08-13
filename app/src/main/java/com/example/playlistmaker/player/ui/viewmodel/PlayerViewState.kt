package com.example.playlistmaker.player.ui.viewmodel

import com.example.playlistmaker.search.domain.models.Track

sealed interface PlayerViewState {
    data class Default(val trackModel: Track, val trackTime: String = "0:00"): PlayerViewState
    data class Prepared(val trackModel: Track, val trackTime: String = "0:00"): PlayerViewState
    data class Playing(val trackModel: Track, val trackTime: String, val isPlayButtonEnabled: Boolean = true): PlayerViewState
    data class Paused(val trackModel: Track, val trackTime: String): PlayerViewState
    data class Completed(val trackModel: Track, val trackTime: String = "0:00"): PlayerViewState
}