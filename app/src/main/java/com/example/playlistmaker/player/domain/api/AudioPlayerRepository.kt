package com.example.playlistmaker.player.domain.api

interface AudioPlayerRepository {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}