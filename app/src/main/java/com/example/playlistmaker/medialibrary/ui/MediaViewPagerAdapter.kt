package com.example.playlistmaker.medialibrary.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.medialibrary.ui.activity.FavoritesTracksFragment
import com.example.playlistmaker.medialibrary.ui.activity.PlaylistFragment

class MediaViewPagerAdapter(host: AppCompatActivity) : FragmentStateAdapter(host) {

    private val listFragments = listOf(
        FavoritesTracksFragment.newInstance(),
        PlaylistFragment.newInstance()
    )

    override fun getItemCount(): Int {
        return listFragments.count()
    }

    override fun createFragment(position: Int): Fragment {
        return listFragments[position]
    }

}