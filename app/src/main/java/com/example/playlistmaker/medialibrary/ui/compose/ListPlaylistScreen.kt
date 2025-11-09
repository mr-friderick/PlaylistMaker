package com.example.playlistmaker.medialibrary.ui.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.ui.viewmodel.ListPlaylistViewModel
import com.example.playlistmaker.medialibrary.ui.viewmodel.ListPlaylistsViewState
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.util.compose.Placeholder
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import java.io.File

@Composable
fun ListPlaylistScreen(
    viewModel: ListPlaylistViewModel,
    openPlaylist: (Playlist) -> Unit,
    createPlaylist: () -> Unit
) {
    val playlistsViewState by viewModel.stateLiveData.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.setContent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NewPlaylistButton(
            onClick = createPlaylist
        )

        when (playlistsViewState) {
            is ListPlaylistsViewState.Content -> {
                Content(
                    playlists = (playlistsViewState as ListPlaylistsViewState.Content).playlists,
                    onPlaylistClick = openPlaylist
                )
            }
            ListPlaylistsViewState.Empty, null -> {
                Spacer(
                    modifier = Modifier.height(46.dp)
                )
                Placeholder(
                    text = stringResource(R.string.playlist_empty)
                )
            }
        }
    }
}

@Composable
private fun NewPlaylistButton(
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 24.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.button_refresh),
            contentColor = colorResource(R.color.txt_button_refresh)
        )
    ) {
        Text(
            text = stringResource(R.string.button_new_playlist),
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontSize = 14.sp,
            fontWeight = FontWeight.W400
        )
    }
}

@Composable
private fun Content(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit
) {

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp)
            .padding(start = 13.dp)
            .padding(end = 20.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(playlists) { playlist ->
            ItemContent(
                playlist = playlist,
                onClickListener = { onPlaylistClick(playlist) }
            )
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
private fun ItemContent(
    playlist: Playlist,
    onClickListener: () -> Unit
) {

    val context = LocalContext.current
    val posterFile = File(context.filesDir, playlist.picturePath)
    val tracksCount = context.resources.getQuantityString(
        R.plurals.tracks_count,
        playlist.tracksCount,
        playlist.tracksCount
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClickListener,
                indication = null,
                interactionSource = null
            )
    ) {
        GlideImage(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(8.dp)),
            imageModel = { posterFile },
            previewPlaceholder = painterResource(R.drawable.ic_track_placeholder),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = playlist.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.txt_default)
            )
        )

        Text(
            text = tracksCount,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.txt_default)
            )
        )
    }
}