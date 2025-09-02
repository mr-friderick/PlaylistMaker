package com.example.playlistmaker.db.converters

import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class PlaylistsDbConvertor {

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            picturePath = entity.picturePath,
            tracksId = mapTracksId(entity.tracksId),
            tracksCount = entity.tracksCount
        )
    }

    fun map(model: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = model.id ?: 0,
            title = model.title,
            description = model.description,
            picturePath = model.picturePath,
            tracksId = mapTracksId(model.tracksId),
            tracksCount = model.tracksId.size
        )
    }

    private fun mapTracksId(tracksId: String): List<Int> {
        return if (tracksId.isEmpty()) {
            emptyList()
        } else {
            tracksId
                .split(",")
                .map { it.toInt() }
        }
    }

    fun mapTracksId(tracksId: List<Int>): String {
        return tracksId.joinToString(separator = ",")
    }
}