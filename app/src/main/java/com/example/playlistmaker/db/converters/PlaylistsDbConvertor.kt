package com.example.playlistmaker.db.converters

import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class PlaylistsDbConvertor {

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            title = playlistEntity.title,
            description = playlistEntity.description,
            picturePath = playlistEntity.picturePath,
            tracksId = mapTracksId(playlistEntity.tracksId),
            tracksCount = playlistEntity.tracksCount
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            picturePath = playlist.picturePath,
            tracksId = mapTracksId(playlist.tracksId),
            tracksCount = playlist.tracksCount
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