package com.example.playlistmaker.medialibrary.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class PlaylistsAdapter(
    private val layoutId: Int,
    private val playlists: List<Playlist>,
    private val clickItem: (Playlist) -> Unit
): RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        return PlaylistsViewHolder(parent, layoutId)
    }

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
        val playlist = playlists[position]
        holder.bind(playlist)

        holder.itemView.setOnClickListener {
            clickItem(playlist)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}