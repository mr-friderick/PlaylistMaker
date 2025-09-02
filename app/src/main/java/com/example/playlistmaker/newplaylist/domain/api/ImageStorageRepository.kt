package com.example.playlistmaker.newplaylist.domain.api

import android.net.Uri

interface ImageStorageRepository {
    suspend fun saveFromUri(uri: Uri): String
}