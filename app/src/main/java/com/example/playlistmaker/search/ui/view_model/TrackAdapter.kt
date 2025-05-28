package com.example.playlistmaker.search.ui.view_model

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(private val tracks: ArrayList<Track>, private val clickItem: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            clickItem(track)
        }
    }

    override fun getItemCount(): Int = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Track>) {
        tracks.clear()
        tracks.addAll(newData)
        notifyDataSetChanged()
    }
}