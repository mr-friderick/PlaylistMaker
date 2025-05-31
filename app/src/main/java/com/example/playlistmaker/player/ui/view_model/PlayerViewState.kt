package com.example.playlistmaker.player.ui.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed interface PlayerViewState {
    data class Default(val trackModel: Track): PlayerViewState
    data class Prepared(val trackTime: String = "0:00"): PlayerViewState
    data class Playing(val trackTime: String, val isPlayButtonEnabled: Boolean = true): PlayerViewState
    data class Paused(val trackTime: String): PlayerViewState
    data class Complite(val trackTime: String = "0:00"): PlayerViewState
}