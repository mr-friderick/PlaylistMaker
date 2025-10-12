package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.services.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerClient {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun observePlayerState(): StateFlow<PlayerState>
}