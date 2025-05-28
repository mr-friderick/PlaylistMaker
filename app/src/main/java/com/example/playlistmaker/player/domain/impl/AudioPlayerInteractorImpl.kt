package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository): AudioPlayerInteractor {
    override fun prepare(url: String) {
        repository.prepare(url)
    }

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition() = repository.getCurrentPosition()
}