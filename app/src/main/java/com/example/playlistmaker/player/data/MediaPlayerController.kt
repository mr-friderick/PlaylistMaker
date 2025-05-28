package com.example.playlistmaker.player.data

import android.media.MediaPlayer

class MediaPlayerController: AudioPlayerClient {
    private val mediaPlayer = MediaPlayer()

    override fun prepare(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            prepare()
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition() =  mediaPlayer.currentPosition
}