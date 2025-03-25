package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val SETTINGS_PREFERENCES = "settings_preferences"
        const val DARK_THEME = "dark_theme"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var buttonShare: MaterialTextView
    private lateinit var buttonSupport: MaterialTextView
    private lateinit var buttonAgreement: MaterialTextView
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        setupWindowInsets()

        initScreenView()
        initSharedPreferences()
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

    private fun initScreenView() {
        toolbar = findViewById(R.id.settings_back)
        themeSwitcher = findViewById(R.id.theme_switcher)
        buttonShare = findViewById(R.id.share)
        buttonSupport = findViewById(R.id.support)
        buttonAgreement = findViewById(R.id.agreement)
    }

    private fun initSharedPreferences() {
        sharedPrefs = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)
    }

    private fun setupThemeSwitcher() {
        themeSwitcher.isChecked = sharedPrefs.getBoolean(DARK_THEME, false)
    }

    private fun setListeners() {
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            sharedPrefs.edit()
                .putBoolean(DARK_THEME, checked)
                .apply()
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
                data = Uri.parse(
                    "mailto:$recipient?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
                )
            }
            startActivity(intent)
        }

        buttonAgreement.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.settings_agreement_link))
            }
            startActivity(intent)
        }
    }
}