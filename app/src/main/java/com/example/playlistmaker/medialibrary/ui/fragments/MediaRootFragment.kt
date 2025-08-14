package com.example.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaRootBinding
import com.example.playlistmaker.medialibrary.ui.adapter.MediaViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaRootFragment : Fragment() {

    private lateinit var binding: FragmentMediaRootBinding
    private lateinit var adapter: MediaViewPagerAdapter
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
    }

    private fun initVariables() {
        adapter = MediaViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorites_track_title)
                1 -> tab.text = getString(R.string.playlist_title)
            }
        }
        tabMediator.attach()
    }
}