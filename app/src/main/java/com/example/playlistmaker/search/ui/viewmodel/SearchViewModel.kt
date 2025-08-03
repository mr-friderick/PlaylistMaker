package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
): ViewModel() {

    private var searchJob: Job? = null

    private val state = MutableLiveData<SearchViewState>(SearchViewState.Default)
    val stateLiveData: LiveData<SearchViewState> = state

    fun clearHistory() {
        historyInteractor.clear()
        state.postValue(SearchViewState.Default)
    }

    fun addTrackInHistory(track: Track) {
        historyInteractor.add(track)
    }

    fun historyIsEmpty(): Boolean {
        return historyInteractor.isEmpty()
    }

    fun setDefaultState() {
        state.postValue(SearchViewState.Default)
    }

    fun setHistoryState() {
        state.postValue(SearchViewState.History(historyInteractor.read()))
    }

    fun searchTracks(expression: String, needDelay:Boolean = true) {
        if (expression.isEmpty()) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (needDelay) {
                delay(SEARCH_DEBOUNCE_DELAY)
            }

            state.postValue(SearchViewState.Loading)

            tracksInteractor.searchTracks(expression)
                .collect { pair ->
                    val foundTracks = pair.first
                    val isError = pair.second
                    if (foundTracks.isEmpty()) {
                        if (isError) {
                            state.postValue(SearchViewState.Error)
                        } else {
                            state.postValue(SearchViewState.NotFound)
                        }
                    } else {
                        state.postValue(SearchViewState.Content(foundTracks))
                    }
                }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}