package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository): HistoryInteractor {
    override fun add(track: Track) {
        val currentHistory = repository.read()
        currentHistory.removeIf { it.trackId == track.trackId }
        if (currentHistory.size == MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        currentHistory.add(0, track)

        repository.save(currentHistory)
    }

    override fun read(): ArrayList<Track> {
        return repository.read()
    }

    override fun clear() {
        repository.clear()
    }

    override fun isEmpty(): Boolean {
        return repository.read().isEmpty()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}