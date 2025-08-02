package com.example.playlistmaker.player.data

interface AudioPlayerClient {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
}