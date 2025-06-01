package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>, isError: Boolean)
    }
}