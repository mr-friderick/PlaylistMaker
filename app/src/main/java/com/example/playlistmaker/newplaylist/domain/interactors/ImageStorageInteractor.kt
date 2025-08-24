package com.example.playlistmaker.newplaylist.domain.interactors

import android.net.Uri

interface ImageStorageInteractor {
    suspend fun saveFromUri(uri: Uri): String
}