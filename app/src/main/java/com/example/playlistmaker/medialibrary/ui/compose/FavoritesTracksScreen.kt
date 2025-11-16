package com.example.playlistmaker.medialibrary.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoriteTracksViewState
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.compose.ItemTrackContent
import com.example.playlistmaker.util.compose.Placeholder

@Composable
fun FavoritesTracksScreen(
    viewModel: FavoritesTracksViewModel,
    openPlayer: (Track) -> Unit
) {
    val tracksViewState by viewModel.stateLiveData.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.setContent()
    }

    when (val uiState = tracksViewState) {
        is FavoriteTracksViewState.Content -> {
            Content(
                tracks = uiState.favoriteTracks,
                onTrackClick = openPlayer
            )
        }
        FavoriteTracksViewState.Empty, null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.height(106.dp)
                )
                Placeholder(
                    text = stringResource(R.string.media_empty)
                )
            }
        }
    }
}

@Composable
private fun Content(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 24.dp,
                bottom = 20.dp,
                start = 13.dp,
                end = 20.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks) { track ->
            ItemTrackContent(
                track = track,
                onClickListener = { onTrackClick(track) }
            )
        }
    }
}
