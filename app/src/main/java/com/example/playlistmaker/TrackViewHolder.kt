package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.element_track, parent, false)
) {

    private val trackPoster: ImageView = itemView.findViewById(R.id.track_poster)
    private val trackName: MaterialTextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: MaterialTextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: MaterialTextView = itemView.findViewById(R.id.track_time)

    fun bind(model: Track) {
        trackName.text = model.trackName
        trackArtist.text = model.artistName
        trackTime.text = model.trackTime

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .into(trackPoster)
    }
}