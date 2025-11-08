package com.example.playlistmaker.medialibrary.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoriteTracksViewState
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun FavoritesTracksScreen(viewModel: FavoritesTracksViewModel, openPlayer: (Track) -> Unit) {

    val tracksViewState by viewModel.stateLiveData.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.setContent()
    }

    when (tracksViewState) {
        is FavoriteTracksViewState.Content -> {
            Content(
                (tracksViewState as FavoriteTracksViewState.Content).favoriteTracks,
                openPlayer
            )
        }
        FavoriteTracksViewState.Empty, null -> {
            Placeholder()
        }
    }
}

@Composable
private fun Placeholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 106.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_search_not_found),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = stringResource(R.string.media_empty),
            style = TextStyle(
                fontSize = 19.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.txt_default)
            )
        )
    }
}

@Composable
private fun Content(tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .padding(bottom = 20.dp)
            .padding(start = 13.dp)
            .padding(end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks) { track ->
            ItemContent(track, { onTrackClick(track) })
        }
    }
}

@Composable
private fun ItemContent(track: Track, onClickListener: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClickListener,
                indication = null,
                interactionSource = null
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            imageModel = { track.artworkUrl100 },
            previewPlaceholder = painterResource(R.drawable.ic_track_placeholder),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    fontWeight = FontWeight.W400,
                    color = colorResource(R.color.txt_default)
                )
            )

            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    text = track.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                        fontWeight = FontWeight.W400,
                        color = colorResource(R.color.txt_track_author)
                    )
                )

                Icon(
                    painter = painterResource(R.drawable.ic_ellipse),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                Text(
                    modifier = Modifier
                        .padding(start = 5.dp),
                    text = track.trackTimeMillis,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                        fontWeight = FontWeight.W400,
                        color = colorResource(R.color.txt_track_author)
                    )
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.ic_arrow_track),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}