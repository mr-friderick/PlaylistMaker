package com.example.playlistmaker.newplaylist.data.impl

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.playlistmaker.newplaylist.domain.api.ImageStorageRepository
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageStorageRepositoryImpl(
    private val context: Context
): ImageStorageRepository {
    override suspend fun saveFromUri(uri: Uri): String {
        val dir = File(context.filesDir, "covers").apply { mkdirs() }
        val mime = context.contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "jpg"
        val name = "${UUID.randomUUID()}.$ext"
        context.contentResolver.openInputStream(uri).use { input ->
            FileOutputStream(File(dir, name)).use { output -> input!!.copyTo(output) }
        }
        return "covers/$name"
    }
}