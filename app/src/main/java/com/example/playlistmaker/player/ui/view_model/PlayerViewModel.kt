package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.models.Track
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

    private val playerState = MutableLiveData(STATE_DEFAULT)
    val playerStateLiveData: LiveData<Int> = playerState

    private val track = MutableLiveData<Track>()
    val trackLiveData: LiveData<Track> = track

    private val trackTimeLeft = MutableLiveData<String>()
    val trackTimeLeftLiveData: LiveData<String> = trackTimeLeft

    private val timerCommand = MutableLiveData<PlayerCommand>()
    val timerCommandLiveData: LiveData<PlayerCommand> = timerCommand

    fun playerControl() {
        when(playerState.value) {
            STATE_PLAYING -> {
                pauseAudioPlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                playAudioPlayer()
            }
        }
    }

    fun updateTime() {
        val formattedTrackTimeLeft = SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())

        trackTimeLeft.value = formattedTrackTimeLeft
    }

    fun setTrack() {
        track.value = trackModel
    }

    fun setOnCompletionListenerForPlayer() {
        playerInteractor.setOnCompletionListener {
            playerState.value = STATE_DEFAULT
            timerCommand.value = PlayerCommand.StopTimer
            trackTimeLeft.value = "0:00"
        }
    }

    fun prepareAudioPlayer() {
        playerInteractor.prepare(trackModel.previewUrl)
        playerState.value = STATE_PREPARED
    }

    fun playAudioPlayer() {
        playerInteractor.play()
        playerState.value = STATE_PLAYING
        timerCommand.value = PlayerCommand.StartTimer
    }

    fun pauseAudioPlayer() {
        playerInteractor.pause()
        playerState.value = STATE_PAUSED
        timerCommand.value = PlayerCommand.StopTimer
    }

    fun releaseAudioPlayer() {
        playerInteractor.release()
        playerState.value = STATE_DEFAULT
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getViewModelFactory(jsonModel: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(jsonModel)
            }
        }
    }
}