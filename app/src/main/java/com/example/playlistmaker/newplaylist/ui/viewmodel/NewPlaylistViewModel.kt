package com.example.playlistmaker.newplaylist.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.newplaylist.domain.interactors.ImageStorageInteractor
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.google.gson.Gson

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val imageStorageInteractor: ImageStorageInteractor,
    gson: Gson,
    jsonModel: String = ""
) : ViewModel() {

    private val _state = MutableLiveData<NewPlaylistViewState>()
    val stateLiveData = _state
    private var playlistModel: Playlist? = if (jsonModel.isNotEmpty()) {
        gson.fromJson(
            jsonModel,
            Playlist::class.java
        )
    } else {
        null
    }

    init {
        if (playlistModel != null) {
            _state.value = NewPlaylistViewState.EditingPlaylist(playlistModel!!)
        } else {
            _state.value = NewPlaylistViewState.Default
        }
    }

    suspend fun createPlaylist(title: String, description: String, uri: Uri?): Result<Unit> =
        runCatching {
            var picturePath = ""
            if (uri != null) {
                picturePath = imageStorageInteractor.saveFromUri(uri)
            }

            val playlist = Playlist(
                id = 0,
                title = title,
                description = description,
                picturePath = picturePath
            )

            playlistInteractor.addPlaylist(playlist)
        }

    suspend fun savePlaylist(title: String, description: String, uri: Uri?, uriChange: Boolean = false) {
        runCatching {
            var picturePath = playlistModel!!.picturePath
            if (uriChange) {
                if (uri != null) {
                    picturePath = imageStorageInteractor.saveFromUri(uri)
                }
            }

            playlistModel = playlistModel!!.copy(
                title = title,
                description = description,
                picturePath = picturePath
            )

            playlistInteractor.updatePlaylist(playlistModel!!)
        }
    }
}