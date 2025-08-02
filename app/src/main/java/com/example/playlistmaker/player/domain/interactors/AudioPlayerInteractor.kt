package com.example.playlistmaker.player.domain.interactors

interface AudioPlayerInteractor {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
}