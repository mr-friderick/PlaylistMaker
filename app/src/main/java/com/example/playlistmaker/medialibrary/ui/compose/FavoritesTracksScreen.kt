package com.example.playlistmaker.medialibrary.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun FavoritesTracksScreen(viewModel: FavoritesTracksViewModel) {

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
private fun Content(tracks: Track) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .padding(bottom = 20.dp)
    ) {

    }
}

@Composable
private fun ItemContent(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.ic_track_placeholder),
            contentDescription = null
        )

        GlideImage(
            imageModel = { track.artworkUrl100 },
            previewPlaceholder = painterResource(R.drawable.ic_track_placeholder)
        )
    }
}