package com.example.playlistmaker.main.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.example.playlistmaker.util.App

class RootActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariables()
        setContentView(binding.root)
        setListeners()
        setupStatusBar()
    }

    private fun initVariables() {
        binding = ActivityRootBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.root_fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setListeners() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.playerFragment -> {
                    binding.bottomNavigationView.isVisible = false
                }
                else -> {
                    binding.bottomNavigationView.isVisible = true
                }
            }
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.bg_screen_default)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !(applicationContext as App).isDarkThemeEnabled()
    }
}