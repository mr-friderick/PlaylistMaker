package com.example.playlistmaker.newplaylist.domain.impl

import android.net.Uri
import com.example.playlistmaker.newplaylist.domain.api.ImageStorageRepository
import com.example.playlistmaker.newplaylist.domain.interactors.ImageStorageInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageStorageInteractorImpl(
    private val repository: ImageStorageRepository
): ImageStorageInteractor {
    override suspend fun saveFromUri(uri: Uri): String = withContext(Dispatchers.IO) {
        repository.saveFromUri(uri)
    }
}