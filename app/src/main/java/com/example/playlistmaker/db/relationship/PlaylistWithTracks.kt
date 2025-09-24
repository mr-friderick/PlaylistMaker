package com.example.playlistmaker.db.relationship

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.db.entity.PlaylistTrackEntity

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId",
        entity = PlaylistTrackEntity::class,
        associateBy = Junction(
            value = PlaylistTrackCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "trackId"
        )
    )
    val tracks: List<PlaylistTrackEntity>
)
