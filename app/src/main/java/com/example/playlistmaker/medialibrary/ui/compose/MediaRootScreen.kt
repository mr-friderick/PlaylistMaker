package com.example.playlistmaker.medialibrary.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MediaRootScreen() {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(
        containerColor = colorResource(R.color.bg_screen_default),
        topBar = { Toolbar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth(),
                containerColor = colorResource(R.color.bg_screen_default),
                indicator = { positions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(positions[selectedTabIndex.value])
                            .fillMaxWidth()
                            .height(2.dp)
                    ) {
                        Box(
                            Modifier
                                .align(Alignment.Center)
                                .width((ScreenWidthInDp() / 2).dp)
                                .fillMaxHeight()
                                .background(colorResource(R.color.tab_layout_color))
                        )
                    }
                }
            ) {
                MediaTab(
                    scope = scope,
                    pagerState = pagerState,
                    tabIndex = selectedTabIndex.value,
                    numberTab = 0,
                    title = stringResource(R.string.favorites_track_title),
                )

                MediaTab(
                    scope = scope,
                    pagerState = pagerState,
                    tabIndex = selectedTabIndex.value,
                    numberTab = 1,
                    title = stringResource(R.string.playlist_title),
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when(page) {
                    0 -> Text(text = "Первый экран")
                    1 -> Text(text = "Второй экран")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.media_toolbar),
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
private fun MediaTab(scope: CoroutineScope, pagerState: PagerState, tabIndex: Int, numberTab: Int, title: String) {
    Tab(
        selected = tabIndex == 0,
        selectedContentColor = colorResource(R.color.tab_layout_color),
        onClick = {
            scope.launch {
                pagerState.animateScrollToPage(numberTab)
            }
        },
        text = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontWeight = FontWeight.W500
                )
            )
        }
    )
}

@Composable
fun ScreenWidthInDp(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp
}