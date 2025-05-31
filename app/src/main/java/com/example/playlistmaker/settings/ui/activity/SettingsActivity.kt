package com.example.playlistmaker.settings.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.main.ui.activity.MainActivity
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.example.playlistmaker.util.App
import com.example.playlistmaker.util.Creator

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private var dataForIntent: MutableMap<String, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initVariables()

        setContentView(binding.root)
        setupWindowInsets()

        observeLiveData()
        setupThemeSwitcher()
        setListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.screenSettings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        dataForIntent["shareLink"] = getString(R.string.settings_share_link)
        dataForIntent["shareTitle"] = getString(R.string.settings_share_title)
        dataForIntent["supportEmail"] = getString(R.string.settings_support_email)
        dataForIntent["supportSubject"] = getString(R.string.settings_support_subject)
        dataForIntent["supportMessage"] = getString(R.string.settings_support_message)
        dataForIntent["agreementLink"] = getString(R.string.settings_agreement_link)
    }

    private fun observeLiveData() {
        viewModel.themeLiveData.observe(this) { value ->
            binding.themeSwitcher.isChecked = value
            (applicationContext as App).switchTheme(value)
        }
    }

    private fun setupThemeSwitcher() {
       viewModel.setupThemeSwitcher()
    }

    private fun setListeners() {
        binding.settingsBack.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeSwitcherClicked(checked)
        }

        binding.share.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, dataForIntent["shareLink"])
            }
            startActivity(Intent.createChooser(intent, dataForIntent["shareTitle"]))
        }

        binding.support.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data =
                    ("mailto:${dataForIntent["supportEmail"]}?" +
                            "subject=${Uri.encode(dataForIntent["supportSubject"])}" +
                            "&body=${Uri.encode(dataForIntent["supportMessage"])}"
                    ).toUri()
            }
            startActivity(intent)
        }

        binding.agreement.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = dataForIntent["agreementLink"]?.toUri()
            }
            startActivity(intent)
        }
    }
}