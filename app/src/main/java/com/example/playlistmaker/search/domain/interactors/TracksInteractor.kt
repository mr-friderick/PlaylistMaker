package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String) : Flow<Pair<ArrayList<Track>, Boolean>>
}