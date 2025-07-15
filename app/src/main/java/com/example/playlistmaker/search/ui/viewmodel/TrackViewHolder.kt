package com.example.playlistmaker.search.ui.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.textview.MaterialTextView

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.element_track, parent, false)
) {
    private val trackPoster: ImageView = itemView.findViewById(R.id.trackPoster)
    private val trackName: MaterialTextView = itemView.findViewById(R.id.trackName)
    private val trackArtist: MaterialTextView = itemView.findViewById(R.id.trackArtist)
    private val trackTime: MaterialTextView = itemView.findViewById(R.id.time)

    fun bind(model: Track) {
        trackName.text = model.trackName
        trackArtist.text = model.artistName
        trackTime.text = model.trackTimeMillis

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .into(trackPoster)
    }
}

