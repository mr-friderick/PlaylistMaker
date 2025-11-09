package com.example.playlistmaker.util.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    color = colorResource(R.color.txt_default)
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.bg_screen_default)
        )
    )
}

@Composable
fun Placeholder(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_search_not_found),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = text,
            style = TextStyle(
                fontSize = 19.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.txt_default),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun PlaceholderForMissingImage() {
    Image(
        modifier = Modifier
            .fillMaxSize(),
        painter = painterResource(R.drawable.ic_track_placeholder),
        contentDescription = null
    )
}

@Composable
fun ItemTrackContent(
    track: Track,
    onClickListener: () -> Unit
) {
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

        CoilImagePoster(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            imageModel = track.artworkUrl100
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

@Composable
fun CoilImagePoster(imageModel: Any, modifier: Modifier = Modifier) {
    CoilImage(
        modifier = modifier,
        imageModel = { imageModel },
        previewPlaceholder = painterResource(R.drawable.ic_track_placeholder),
        loading = { PlaceholderForMissingImage() },
        failure = { PlaceholderForMissingImage() },
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    )
}

