package com.example.playlistmaker.player.data

interface AudioPlayerClient {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
}