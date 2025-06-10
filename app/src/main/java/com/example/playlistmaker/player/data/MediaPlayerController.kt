package com.example.playlistmaker.player.data

import kotlin.math.max

class MediaPlayerController(
    private var mediaPlayerFactory: MediaPlayerFactory
): AudioPlayerClient {

    private var mediaPlayer = mediaPlayerFactory.create()
    private var isRelease = false
    private var currentPosition = 0

    override fun prepare(url: String) {
        if (isRelease) {
            mediaPlayer = mediaPlayerFactory.create()
            isRelease = false
        }
        mediaPlayer.apply {
            setDataSource(url)
            prepare()
        }
    }

    override fun play() {
        mediaPlayer.seekTo(currentPosition)
        mediaPlayer.start()
    }

    override fun pause() {
        currentPosition = getCurrentPosition()
        mediaPlayer.pause()
    }

    override fun release() {
        currentPosition = getCurrentPosition();
        mediaPlayer.release()
        mediaPlayer.setOnCompletionListener {  }

        isRelease = true
    }

    override fun getCurrentPosition() = max(currentPosition, mediaPlayer.currentPosition)

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            listener.invoke()
            currentPosition = 0;
        }
    }
}