package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    gson: Gson,
    jsonModel: String
): ViewModel() {

    private val trackModel = gson.fromJson(
        jsonModel,
        Track::class.java
    )

    private var timerJob: Job? = null

    private val _playerStateLiveData = MutableLiveData<PlayerViewState>()
    val playerStateLiveData: LiveData<PlayerViewState> = _playerStateLiveData

    init {
        viewModelScope.launch {
            trackModel.isFavorite = favoriteTracksInteractor.isFavorite(trackModel.trackId)
            _playerStateLiveData.value = PlayerViewState.Default(trackModel)
        }
    }

    fun playerControl() {
        when(_playerStateLiveData.value) {
            is PlayerViewState.Default, null -> {
                prepareAudioPlayer()
            }

            is PlayerViewState.Playing -> {
                pauseAudioPlayer()
            }
            is PlayerViewState.Prepared, is PlayerViewState.Paused, is PlayerViewState.Completed -> {
                playAudioPlayer()
            }
        }
    }

    private fun setOnCompletionListenerForPlayer() {
        playerInteractor.setOnCompletionListener {
            timerJob?.cancel()
            _playerStateLiveData.value = PlayerViewState.Completed()
        }
    }

    fun prepareAudioPlayer() {
        viewModelScope.launch {
            delay(PREPARE_DELAY)
            playerInteractor.prepare(trackModel.previewUrl)
            _playerStateLiveData.value = PlayerViewState.Prepared(getFormattedTime())

            setOnCompletionListenerForPlayer()
        }
    }

    fun playAudioPlayer() {
        playerInteractor.play()
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(TIME_LEFT_DELAY)
                _playerStateLiveData.value = PlayerViewState.Playing(getFormattedTime())
            }
        }
    }

    fun pauseAudioPlayer() {
        playerInteractor.pause()
        timerJob?.cancel()
        _playerStateLiveData.value = PlayerViewState.Paused(getFormattedTime())
    }

    fun releaseAudioPlayer() {
        playerInteractor.release()
        timerJob?.cancel()
        _playerStateLiveData.value = PlayerViewState.Default(trackModel)
    }

    private fun getFormattedTime(): String {
        return SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    companion object {
        private const val PREPARE_DELAY = 200L
        private const val TIME_LEFT_DELAY = 300L
    }
}