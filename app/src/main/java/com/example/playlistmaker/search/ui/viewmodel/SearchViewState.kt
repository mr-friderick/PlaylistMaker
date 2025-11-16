package com.example.playlistmaker.search.ui.viewmodel

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchViewState {
    data object Default: SearchViewState
    data class History(val historyTracks: List<Track>): SearchViewState
    data object Loading: SearchViewState
    data class Content(val contentTracks: List<Track>): SearchViewState
    data object NotFound: SearchViewState
    data object Error: SearchViewState
}