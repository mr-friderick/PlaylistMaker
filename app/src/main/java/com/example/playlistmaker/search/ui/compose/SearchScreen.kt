package com.example.playlistmaker.search.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewState
import com.example.playlistmaker.util.compose.ItemTrackContent
import com.example.playlistmaker.util.compose.Placeholder
import com.example.playlistmaker.util.compose.Toolbar

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
) {

    val searchViewState by viewModel.stateLiveData.observeAsState()
    var textSearch by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        defineCurrentView(textSearch, viewModel)
    }

    Scaffold(
        containerColor = colorResource(R.color.bg_screen_default),
        topBar = {
            Toolbar(
                title = stringResource(R.string.main_button_search)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchField(
                value = textSearch,
                onValueChange = {
                    textSearch = it
                    if (historyAllowed(textSearch, viewModel)) {
                        viewModel.setHistoryState()
                    }
                    else if (it.isNotEmpty()) {
                        viewModel.searchTracks(it, true)
                    } else {
                        viewModel.cancelSearchJob()
                        viewModel.setDefaultState()
                    }
                },
                onFocus = {
                    if (historyAllowed(textSearch, viewModel)) {
                        viewModel.setHistoryState()
                    }
                },
                onClear = {
                    viewModel.cancelSearchJob()
                    viewModel.setDefaultState()
                    textSearch = ""
                    focusManager.clearFocus()
                },
                keyboardActionDone = {
                    viewModel.searchTracks(textSearch, false)
                }
            )

            when (searchViewState) {
                SearchViewState.Default -> {}
                is SearchViewState.History -> {
                    History(
                        tracks = (searchViewState as SearchViewState.History).historyTracks,
                        onTrackClick = onTrackClick,
                        onClearHistoryClick = { viewModel.clearHistory() }
                    )
                }
                is SearchViewState.Loading -> {
                    ProgressBar()
                }
                is SearchViewState.Content -> {
                    Content(
                        tracks = (searchViewState as SearchViewState.Content).contentTracks,
                        onTrackClick = { track ->
                            onTrackClick(track)
                            viewModel.addTrackInHistory(track)
                        }
                    )
                }
                is SearchViewState.NotFound, null -> {
                    Spacer(
                        modifier = Modifier
                            .height(110.dp)
                    )
                    Placeholder(
                        text = stringResource(R.string.search_not_found_text)
                    )
                }
                is SearchViewState.Error -> {
                    Spacer(
                        modifier = Modifier
                            .height(110.dp)
                    )
                    FailurePlaceholder(
                        onRefreshSearchClick = { viewModel.searchTracks(textSearch) }
                    )
                }
            }
        }

    }
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onClear: () -> Unit,
    keyboardActionDone: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = colorResource(R.color.bg_layout_search),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_mini_search),
                contentDescription = null,
                tint = Color.Unspecified,
            )

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                onFocus()
                                keyboardController?.show()
                            } else {
                                keyboardController?.hide()
                            }
                        },
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = colorResource(R.color.bg_dark_mode)
                    ),
                    cursorBrush = SolidColor(colorResource(R.color.ic_search_blue)),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onDone =  { keyboardActionDone() }
                    )
                )

                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.main_button_search),
                        color = colorResource(R.color.txt_search),
                        maxLines = 1
                    )
                }
            }

            AnimatedVisibility(
                visible = value.isNotEmpty()
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp),
                    onClick = onClear,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_drop_input),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
private fun History(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .padding(top = 50.dp),
        text = stringResource(R.string.search_history_title),
        fontSize = 19.sp,
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontWeight = FontWeight.W400,
        color = colorResource(R.color.txt_default)
    )

    Content(
        tracks = tracks,
        onTrackClick = onTrackClick,
        needButtonClearHistory = true,
        onClearHistoryClick = onClearHistoryClick
    )
}

@Composable
private fun Content(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    needButtonClearHistory: Boolean = false,
    onClearHistoryClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(bottom = 24.dp)
            .padding(start = 13.dp)
            .padding(end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks) { track ->
            ItemTrackContent(
                track = track,
                onClickListener = { onTrackClick(track) }
            )
        }

        if (needButtonClearHistory) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier
                            .wrapContentSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        onClick = onClearHistoryClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.button_refresh),
                            contentColor = colorResource(R.color.txt_button_refresh)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.search_clear_history_text),
                            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FailurePlaceholder(
    onRefreshSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_search_failure),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = stringResource(R.string.search_failure_text),
            style = TextStyle(
                fontSize = 19.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.txt_default),
                textAlign = TextAlign.Center
            )
        )

        Button(
            modifier = Modifier
                .wrapContentSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
            onClick = onRefreshSearchClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.button_refresh),
                contentColor = colorResource(R.color.txt_button_refresh)
            )
        ) {
            Text(
                text = stringResource(R.string.search_button_refresh_text),
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            )
        }
    }
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier
            .padding(top = 148.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = colorResource(R.color.progress_bar_search),
            strokeWidth = 3.dp
        )
    }
}

private fun defineCurrentView(text: String, viewModel: SearchViewModel) {
    if (historyAllowed(text, viewModel)) {
        viewModel.setHistoryState()
    } else {
        viewModel.setDefaultState()
    }
}

private fun historyAllowed(text: String, viewModel: SearchViewModel): Boolean {
    return text.isEmpty()
            && !viewModel.historyIsEmpty()
}