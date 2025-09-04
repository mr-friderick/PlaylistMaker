package com.example.playlistmaker.search.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(
    private val tracks: ArrayList<Track>,
    private val clickItem: (Track) -> Unit,
    private val longClickItem: (Int) -> Unit = {}
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            clickItem(track)
        }
        holder.itemView.setOnLongClickListener {
            longClickItem(track.trackId)
            true
        }
    }

    override fun getItemCount(): Int = tracks.size
}