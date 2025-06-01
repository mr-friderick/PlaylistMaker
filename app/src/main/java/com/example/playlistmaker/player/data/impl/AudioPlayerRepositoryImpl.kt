package com.example.playlistmaker.player.data.impl

import com.example.playlistmaker.player.data.AudioPlayerClient
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository

class AudioPlayerRepositoryImpl(private val audioPlayer: AudioPlayerClient): AudioPlayerRepository {
    override fun prepare(url: String) {
        audioPlayer.prepare(url)
    }

    override fun play() {
        audioPlayer.play()
    }

    override fun pause() {
       audioPlayer.pause()
    }

    override fun release() {
        audioPlayer.release()
    }

    override fun getCurrentPosition() = audioPlayer.getCurrentPosition()

    override fun setOnCompletionListener(listener: () -> Unit) {
        audioPlayer.setOnCompletionListener { listener() }
    }


}