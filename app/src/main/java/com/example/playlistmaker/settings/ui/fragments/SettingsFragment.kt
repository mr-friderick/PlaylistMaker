package com.example.playlistmaker.settings.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()
    private var dataForIntent: MutableMap<String, String> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
    }

    private fun initVariables() {
        dataForIntent["shareLink"] = getString(R.string.settings_share_link)
        dataForIntent["shareTitle"] = getString(R.string.settings_share_title)
        dataForIntent["supportEmail"] = getString(R.string.settings_support_email)
        dataForIntent["supportSubject"] = getString(R.string.settings_support_subject)
        dataForIntent["supportMessage"] = getString(R.string.settings_support_message)
        dataForIntent["agreementLink"] = getString(R.string.settings_agreement_link)
    }

    private fun observeLiveData() {
        viewModel.themeLiveData.observe(viewLifecycleOwner) { value ->
            binding.themeSwitcher.isChecked = value
            (requireContext().applicationContext as App).switchTheme(value)
        }
    }

    private fun setListeners() {
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