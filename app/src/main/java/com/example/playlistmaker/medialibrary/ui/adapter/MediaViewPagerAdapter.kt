package com.example.playlistmaker.medialibrary.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.medialibrary.ui.fragments.FavoritesTracksFragment
import com.example.playlistmaker.medialibrary.ui.fragments.PlaylistFragment

class MediaViewPagerAdapter(host: Fragment) : FragmentStateAdapter(host) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FavoritesTracksFragment.newInstance() else PlaylistFragment.newInstance()
    }
}