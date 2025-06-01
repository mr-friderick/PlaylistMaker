package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun save(tracks: ArrayList<Track>)

    fun read(): ArrayList<Track>

    fun clear()
}