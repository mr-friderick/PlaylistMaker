package com.example.playlistmaker.search.ui.view_model

sealed class SearchViewState {
    object Default: SearchViewState()
    object History: SearchViewState()
    object Loading: SearchViewState()
    object Content: SearchViewState()
    object NotFound: SearchViewState()
    object Error: SearchViewState()
}