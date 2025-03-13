package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.element_track, parent, false)
) {
    companion object {
        const val DEFAULT_TIME = "00:00"
    }

    private val trackPoster: ImageView = itemView.findViewById(R.id.track_poster)
    private val trackName: MaterialTextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: MaterialTextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: MaterialTextView = itemView.findViewById(R.id.track_time)

    private fun formatTrackTime(trackTimeMillis: String): String {
        return try {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(trackTimeMillis.toLong()))
        } catch (e: NumberFormatException) {
            DEFAULT_TIME
        }
    }

    fun bind(model: Track) {
        trackName.text = model.trackName
        trackArtist.text = model.artistName
        trackTime.text = formatTrackTime(model.trackTimeMillis)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .into(trackPoster)
    }
}

