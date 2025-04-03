package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson

class PlayerActivity:  AppCompatActivity() {

    private lateinit var trackModel: Track
    private lateinit var buttonBack: ImageButton
    private lateinit var trackPoster: ImageView
    private lateinit var trackName: MaterialTextView
    private lateinit var trackArtist: MaterialTextView
    private lateinit var trackTime: MaterialTextView
    private lateinit var trackCollection: MaterialTextView
    private lateinit var trackRelease: MaterialTextView
    private lateinit var trackGenre: MaterialTextView
    private lateinit var trackCountry: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        setupWindowInsets()

        initVariables()
        setListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screen_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        trackModel = Gson().fromJson(
            intent.getStringExtra(SearchActivity.INTENT_EXTRA_TRACK),
            Track::class.java
        )

        buttonBack = findViewById(R.id.player_back)
        trackPoster = findViewById(R.id.track_poster)
        trackName = findViewById(R.id.track_name)
        trackArtist = findViewById(R.id.track_artist)
        trackTime = findViewById(R.id.track_time)
        trackCollection = findViewById(R.id.track_collection)
        trackRelease = findViewById(R.id.track_release)
        trackGenre = findViewById(R.id.track_genre)
        trackCountry = findViewById(R.id.track_country)

        Glide.with(trackPoster)
            .load(trackModel.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .into(trackPoster)

        trackName.text = trackModel.trackName
        trackArtist.text = trackModel.artistName
        trackTime.text = trackModel.trackTimeMillis
        trackCollection.text = trackModel.collectionName
        trackRelease.text = trackModel.releaseDate
        trackGenre.text = trackModel.primaryGenreName
        trackCountry.text = trackModel.country
    }

    private fun setListeners() {
        buttonBack.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            finish()
        }
    }

}