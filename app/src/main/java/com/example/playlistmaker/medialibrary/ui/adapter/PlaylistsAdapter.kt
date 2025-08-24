package com.example.playlistmaker.medialibrary.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class PlaylistsAdapter(
    private val playlists: List<Playlist>
): RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        return PlaylistsViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}