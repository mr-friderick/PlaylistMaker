package com.example.playlistmaker.player.services

sealed interface PlayerState {
    data class Default(val trackTime: String = "0:00"): PlayerState
    data class Prepared(val trackTime: String = "0:00"): PlayerState
    data class Playing(val trackTime: String): PlayerState
    data class Paused(val trackTime: String): PlayerState
    data class Complete(val trackTime: String = "0:00"): PlayerState
}