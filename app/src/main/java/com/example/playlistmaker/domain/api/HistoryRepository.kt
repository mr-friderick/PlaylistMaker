package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun save(tracks: ArrayList<Track>)

    fun read(): ArrayList<Track>

    fun clear()
}