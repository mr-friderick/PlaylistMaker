package com.example.playlistmaker.search.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.settings.ui.view_model.SearchViewState
import com.example.playlistmaker.util.Creator

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
): ViewModel() {

    private val history = MutableLiveData(historyInteractor.read())
    val historyLiveData: LiveData<ArrayList<Track>> = history

    private val tracks = MutableLiveData<ArrayList<Track>>()
    val tracksLiveData: LiveData<ArrayList<Track>> = tracks

    private val state = MutableLiveData<SearchViewState>(SearchViewState.Default)
    val stateLiveData: LiveData<SearchViewState> = state

    fun clearHistory() {
        historyInteractor.clear()
        history.postValue(historyInteractor.read())
        setDefaultState()
    }

    fun addTrackInHistory(track: Track) {
        historyInteractor.add(track)
        history.postValue(historyInteractor.read())
    }

    fun historyIsEmpty(): Boolean {
        return historyInteractor.isEmpty()
    }

    fun setDefaultState() {
        state.postValue(SearchViewState.Default)
    }

    fun setHistoryState() {
        state.postValue(SearchViewState.History)
    }

    fun setLoadingState() {
        state.postValue(SearchViewState.Loading)
    }

    fun setContentState() {
        state.postValue(SearchViewState.Content)
    }

    fun setNotFoundState() {
        state.postValue(SearchViewState.NotFound)
    }

    fun setErrorState() {
        state.postValue(SearchViewState.Error)
    }

    fun searchTracks(expression: String) {
        if (expression.isEmpty()) return

        setLoadingState()

        tracksInteractor.searchTracks(
            expression,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: ArrayList<Track>, isError: Boolean) {
                    if (foundTracks.isEmpty()) {
                        if (isError) {
                            setErrorState()
                        } else {
                            setNotFoundState()
                        }
                    } else {
                        setContentState()
                        tracks.postValue(foundTracks)
                    }
                }
            }
        )
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksInteractor = Creator.provideTracksInteractor()
                val historyInteractor = Creator.provideHistoryInteractor(context)
                SearchViewModel(tracksInteractor, historyInteractor)
            }
        }
    }

}