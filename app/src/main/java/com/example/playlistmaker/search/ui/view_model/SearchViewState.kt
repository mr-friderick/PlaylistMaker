package com.example.playlistmaker.search.ui.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchViewState {
    data object Default: SearchViewState
    data class History(val historyTracks: ArrayList<Track>): SearchViewState
    data object Loading: SearchViewState
    data class Content(val contentTracks: ArrayList<Track>): SearchViewState
    data object NotFound: SearchViewState
    data object Error: SearchViewState
}