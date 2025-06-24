package com.example.playlistmaker.medialibrary.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initVariables()

        setContentView(binding.root)
        setupWindowInsets()

        if (savedInstanceState == null) {
            addFragments()
        }

        setListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.screenMedia) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        binding = ActivityMediaBinding.inflate(layoutInflater)
    }

    private fun addFragments() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container_view, FavoritesTracksFragment())
            .commit()
    }

    private fun setListeners() {
        //TODO Добавить обработку слушателей
    }
}