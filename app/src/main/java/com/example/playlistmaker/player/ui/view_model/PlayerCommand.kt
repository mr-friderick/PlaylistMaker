package com.example.playlistmaker.player.ui.view_model

sealed class PlayerCommand {
    data object StartTimer: PlayerCommand()
    data object StopTimer: PlayerCommand()
}