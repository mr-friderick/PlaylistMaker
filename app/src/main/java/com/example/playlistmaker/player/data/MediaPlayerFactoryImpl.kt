package com.example.playlistmaker.player.data

import android.media.MediaPlayer

class MediaPlayerFactoryImpl: MediaPlayerFactory {
    override fun create(): MediaPlayer = MediaPlayer()
}