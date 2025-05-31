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
import com.example.playlistmaker.util.Creator

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
): ViewModel() {

    private val history = MutableLiveData(historyInteractor.read())
    val historyLiveData: LiveData<ArrayList<Track>> = history

    fun clearHistory() {
        historyInteractor.clear()
        history.value = historyInteractor.read()
    }

    fun addTrackInHistory(track: Track) {
        historyInteractor.add(track)
        history.value = historyInteractor.read()
    }

    fun historyIsEmpty(): Boolean {
        return historyInteractor.isEmpty()
    }

    fun searchTracks() {

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