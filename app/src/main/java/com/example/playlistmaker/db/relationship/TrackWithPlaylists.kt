package com.example.playlistmaker.db.relationship

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.db.entity.PlaylistTrackEntity

data class TrackWithPlaylists(
    @Embedded val track: PlaylistTrackEntity,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "id",
        entity = PlaylistEntity::class,
        associateBy = Junction(
            value = PlaylistTrackCrossRef::class,
            parentColumn = "trackId",
            entityColumn = "playlistId"
        )
    )
    val playlists: List<PlaylistEntity>
)