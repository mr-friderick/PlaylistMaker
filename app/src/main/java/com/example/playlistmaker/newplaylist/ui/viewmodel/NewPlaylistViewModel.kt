package com.example.playlistmaker.newplaylist.ui.viewmodel

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    fun createPlaylist() {
//        viewModelScope.launch {
//            requester.request(
//                Manifest.permission.READ_MEDIA_IMAGES
//            ).collect { result ->
//                when (result) {
//                    PermissionResult.Cancelled -> {}
//                    is PermissionResult.Denied.DeniedPermanently -> {}
//                    is PermissionResult.Denied.NeedsRationale -> {}
//                    is PermissionResult.Granted -> {}
//                }
//            }
//        }
    }
}