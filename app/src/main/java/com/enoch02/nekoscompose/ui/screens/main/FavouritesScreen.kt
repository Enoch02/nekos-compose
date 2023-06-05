package com.enoch02.nekoscompose.ui.screens.main

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enoch02.nekoscompose.R
import com.enoch02.nekoscompose.data.db.Neko
import com.enoch02.nekoscompose.data.model.MainViewModel
import com.enoch02.nekoscompose.data.model.MainViewModelFactory
import com.enoch02.nekoscompose.ui.composables.NekoGalleryItem
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder

@Composable
fun FavouritesScreen(
    modifier: Modifier,
    favouritesListState: LazyListState,
    mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(application = LocalContext.current.applicationContext as Application))
) {
    val images: List<Neko> by mainViewModel.getFavourites().collectAsState(initial = emptyList())

    if (mainViewModel.fullScreen) {
        BackHandler(
            onBack = {
                mainViewModel.fullScreen = false
            }
        )
    }

    AnimatedVisibility(visible = images.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.HeartBroken,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_favourites),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }

    AnimatedVisibility(visible = images.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            state = favouritesListState,
            content = {
                items(
                    count = images.size,
                    key = { index -> images[index].url },
                    itemContent = { index ->
                        var showPlaceHolder by rememberSaveable { mutableStateOf(true) }
                        val neko = images[index]
                        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                        NekoGalleryItem(
                            context = LocalContext.current,
                            scope = rememberCoroutineScope(),
                            artistHref = neko.artistHref,
                            artistName = neko.artistName,
                            sourceUrl = neko.sourceUrl,
                            url = neko.url,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(screenWidth)
                                .padding(if (mainViewModel.fullScreen) 0.dp else 8.dp)
                                .placeholder(
                                    visible = showPlaceHolder,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(8.dp),
                                    highlight = PlaceholderHighlight.fade(),
                                ),
                            onLoadingComplete = { showPlaceHolder = false },
                        )
                    }
                )
            }
        )
    }
}