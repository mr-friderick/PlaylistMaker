package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository

class TracksIntreractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        val t = Thread {
            val (tracks, isError) = repository.searchTracks(expression)
            consumer.consume(tracks, isError)
        }
        t.start()
    }
}