package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun add(track: Track)

    fun read(): ArrayList<Track>

    fun clear()

    fun isEmpty(): Boolean
}