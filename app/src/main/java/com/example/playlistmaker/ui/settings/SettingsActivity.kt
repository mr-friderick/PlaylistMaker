package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.ui.main.MainActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var buttonShare: MaterialTextView
    private lateinit var buttonSupport: MaterialTextView
    private lateinit var buttonAgreement: MaterialTextView

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        setupWindowInsets()

        initVariables()
        setupThemeSwitcher()
        setListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screen_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        toolbar = findViewById(R.id.settings_back)
        themeSwitcher = findViewById(R.id.theme_switcher)
        buttonShare = findViewById(R.id.share)
        buttonSupport = findViewById(R.id.support)
        buttonAgreement = findViewById(R.id.agreement)

        settingsInteractor = Creator.provideSettingInteractor(this)
    }

    private fun setupThemeSwitcher() {
        themeSwitcher.isChecked = settingsInteractor.read()
    }

    private fun setListeners() {
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.save(checked)
            (applicationContext as App).switchTheme(checked)
        }

        buttonShare.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_link))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.settings_share_title)))
        }

        buttonSupport.setOnClickListener {
            val recipient = getString(R.string.settings_support_email)
            val subject = getString(R.string.settings_support_subject)
            val body = getString(R.string.settings_support_message)

            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data =
                    "mailto:$recipient?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}".toUri()
            }
            startActivity(intent)
        }

        buttonAgreement.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = getString(R.string.settings_agreement_link).toUri()
            }
            startActivity(intent)
        }
    }
}