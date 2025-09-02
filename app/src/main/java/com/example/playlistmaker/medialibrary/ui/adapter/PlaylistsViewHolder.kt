package com.example.playlistmaker.medialibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.google.android.material.textview.MaterialTextView
import java.io.File

class PlaylistsViewHolder(parent: ViewGroup, @LayoutRes layoutId: Int) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
) {
    private val playlistCover: ImageView = itemView.findViewById(R.id.playlistCover)
    private val playlistTitle: MaterialTextView = itemView.findViewById(R.id.playlistName)
    private val playlistTracksCount: MaterialTextView = itemView.findViewById(R.id.playlistTracksCount)

    fun bind(model: Playlist) {
        val context = itemView.context

        playlistTitle.text = model.title
        playlistTracksCount.text = context.resources.getQuantityString(
            R.plurals.tracks_count,
            model.tracksCount,
            model.tracksCount
        )

        val file = File(context.filesDir, model.picturePath)

        Glide.with(itemView)
            .load(file)
            .placeholder(R.drawable.ic_track_placeholder)
            .error(R.drawable.ic_track_placeholder)
            .centerCrop()
            .into(playlistCover)
    }
}