package com.example.playlistmaker.player.services

import kotlinx.coroutines.flow.StateFlow

interface MediaServiceClient {
    fun prepare()
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun observePlayerState(): StateFlow<PlayerState>
    fun isPlaying(): Boolean
    fun showNotification()
    fun closeNotification()
}