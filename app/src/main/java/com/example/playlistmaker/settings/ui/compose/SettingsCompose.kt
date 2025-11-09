package com.example.playlistmaker.settings.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.util.App
import com.example.playlistmaker.util.compose.Toolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val context = LocalContext.current
    val isChecked by viewModel.themeLiveData.observeAsState(false)

    val dataForIntent: MutableMap<String, String> = mutableMapOf()
    dataForIntent["shareLink"] = stringResource(R.string.settings_share_link)
    dataForIntent["shareTitle"] = stringResource(R.string.settings_share_title)
    dataForIntent["supportEmail"] = stringResource(R.string.settings_support_email)
    dataForIntent["supportSubject"] = stringResource(R.string.settings_support_subject)
    dataForIntent["supportMessage"] = stringResource(R.string.settings_support_message)
    dataForIntent["agreementLink"] = stringResource(R.string.settings_agreement_link)

    LaunchedEffect(Unit) {
        viewModel.setupThemeSwitcher()
    }

    Scaffold(
        containerColor = colorResource(R.color.bg_screen_default),
        topBar = {
            Toolbar(
                title = stringResource(R.string.main_button_settings)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ThemeSwitcher(
                isChecked,
                {
                    viewModel.onThemeSwitcherClicked(it)
                    (context.applicationContext as App).switchTheme(it)
                }
            )

            TextAndImageRow(
                R.string.settings_option_share,
                R.drawable.ic_share,
                {
                    context.startActivity(Intent.createChooser(
                        intentForShare(dataForIntent["shareLink"]),
                        dataForIntent["shareTitle"]
                        )
                    )
                }
            )

            TextAndImageRow(
                R.string.settings_option_support,
                R.drawable.ic_support,
                {
                    context.startActivity(
                        intentForSupp(
                            dataForIntent["supportEmail"],
                            dataForIntent["supportSubject"],
                            dataForIntent["supportMessage"]
                        )
                    )
                }
            )

            TextAndImageRow(
                R.string.settings_option_agreement,
                R.drawable.ic_arrow_forward,
                {
                    context.startActivity(
                        intentForAgreement(dataForIntent["agreementLink"])
                    )
                }
            )
        }
    }
}

@Composable
private fun ThemeSwitcher(
    isChecked: Boolean,
    onCheckListener: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(min = 61.dp)
            .toggleable(
                value = isChecked,
                onValueChange = onCheckListener,
                role = Role.Switch,
                indication = null,
                interactionSource = null,
                enabled = true
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_option_dark_mode),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                color = colorResource(R.color.txt_default),
                fontFamily = FontFamily(Font(R.font.ys_display_regular))
            ),
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.ic_switch),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun TextAndImageRow(textRes: Int, iconRes: Int, onClickListener: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(min = 61.dp)
            .clickable(
                onClick = onClickListener,
                indication = null,
                interactionSource = null
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(textRes),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                color = colorResource(R.color.txt_default),
                fontFamily = FontFamily(Font(R.font.ys_display_regular))
            ),
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

private fun intentForShare(value: String?): Intent {
    return Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, value)
    }
}

private fun intentForSupp(
    supportEmail: String?,
    supportSubject: String?,
    supportMessage: String?
): Intent {
    return Intent().apply {
        action = Intent.ACTION_SENDTO
        data =
            ("mailto:${supportEmail}?" +
                    "subject=${Uri.encode(supportSubject)}" +
                    "&body=${Uri.encode(supportMessage)}"
                    ).toUri()
    }
}

private fun intentForAgreement(link: String?): Intent {
    return Intent().apply {
        action = Intent.ACTION_VIEW
        data = link?.toUri()
    }
}