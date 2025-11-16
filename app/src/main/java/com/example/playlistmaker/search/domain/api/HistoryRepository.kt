package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun save(tracks: List<Track>)

    fun read(): List<Track>

    fun clear()
}