package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.search.domain.models.Track

interface HistoryInteractor {
    fun add(track: Track)

    fun read(): ArrayList<Track>

    fun clear()

    fun isEmpty(): Boolean
}