package com.example.playlistmaker.medialibrary.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.medialibrary.ui.activity.FavoritesTracksFragment
import com.example.playlistmaker.medialibrary.ui.activity.PlaylistFragment

class MediaViewPagerAdapter(host: AppCompatActivity) : FragmentStateAdapter(host) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FavoritesTracksFragment.newInstance() else PlaylistFragment.newInstance()
    }
}