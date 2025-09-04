package com.example.playlistmaker.newplaylist.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.newplaylist.domain.interactors.ImageStorageInteractor
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val imageStorageInteractor: ImageStorageInteractor
) : ViewModel() {

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
}