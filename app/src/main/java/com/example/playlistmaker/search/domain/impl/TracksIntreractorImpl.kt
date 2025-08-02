package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksIntreractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>, Boolean>> {
        return repository.searchTracks(expression)
    }
}