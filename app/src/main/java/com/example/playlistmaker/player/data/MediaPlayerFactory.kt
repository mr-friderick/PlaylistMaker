package com.example.playlistmaker.player.data

import android.media.MediaPlayer

interface MediaPlayerFactory {
    fun create(): MediaPlayer
}