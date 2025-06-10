package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
): ViewModel() {

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

    fun searchTracks(expression: String) {
        if (expression.isEmpty()) return

        state.postValue(SearchViewState.Loading)

        tracksInteractor.searchTracks(
            expression,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: ArrayList<Track>, isError: Boolean) {
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
        )
    }
}