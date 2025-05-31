package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Creator
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(jsonModel: String): ViewModel() {
    private val playerInteractor = Creator.providePlayerInteractor()
    private val trackModel = Gson().fromJson(
        jsonModel,
        Track::class.java
    )

    private val playerState = MutableLiveData<PlayerViewState>(PlayerViewState.Default(trackModel))
    val playerStateLiveData: LiveData<PlayerViewState> = playerState

    fun playerControl() {
        when(playerState.value) {
            is PlayerViewState.Default, null -> {
                prepareAudioPlayer()
            }
            is PlayerViewState.Playing -> {
                pauseAudioPlayer()
            }
            is PlayerViewState.Prepared, is PlayerViewState.Paused, is PlayerViewState.Complite -> {
                playAudioPlayer()
            }
        }
    }

    fun updateTime() {
        val formattedTrackTimeLeft = getFormattedTime()
        val currentState = playerState.value
        playerState.value = when(currentState) {
            is PlayerViewState.Default, is PlayerViewState.Complite, is PlayerViewState.Prepared, null -> currentState
            is PlayerViewState.Playing -> currentState.copy(trackTime = formattedTrackTimeLeft)
            is PlayerViewState.Paused ->  currentState.copy(trackTime = formattedTrackTimeLeft)
        }
    }

    fun setOnCompletionListenerForPlayer() {
        playerInteractor.setOnCompletionListener {
            playerState.value = PlayerViewState.Complite()
        }
    }

    fun prepareAudioPlayer() {
        playerInteractor.prepare(trackModel.previewUrl)
        playerState.value = PlayerViewState.Prepared()
    }

    fun playAudioPlayer() {
        playerInteractor.play()
        playerState.value = PlayerViewState.Playing(getFormattedTime())
    }

    fun pauseAudioPlayer() {
        playerInteractor.pause()
        playerState.value = PlayerViewState.Paused(getFormattedTime())
    }

    fun releaseAudioPlayer() {
        playerInteractor.release()
        playerState.value = PlayerViewState.Default(trackModel)
    }

    private fun getFormattedTime(): String {
        return SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    companion object {
        fun getViewModelFactory(jsonModel: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(jsonModel)
            }
        }
    }
}