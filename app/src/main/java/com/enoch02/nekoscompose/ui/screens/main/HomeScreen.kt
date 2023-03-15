package com.enoch02.nekoscompose.ui.screens.main

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enoch02.nekoscompose.data.model.MainViewModel
import com.enoch02.nekoscompose.data.model.MainViewModelFactory
import com.enoch02.nekoscompose.data.model.NekoImage
import com.enoch02.nekoscompose.ui.composables.NekoGalleryItem
import com.enoch02.nekoscompose.ui.composables.TBD
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(application = LocalContext.current.applicationContext as Application)),
    listState: LazyListState,
    modifier: Modifier
) {
    var images: List<NekoImage> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(key1 = mainViewModel.isCacheEmpty()) {
        images = mainViewModel.getNeko()
    }

    if (mainViewModel.getNekoErrorState()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Could not load images")
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Error!")
        }
    } else {
        when (images.isEmpty()) {
            true -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            false -> {
                NekoGallery(
                    images = images,
                    listState = listState,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun NekoGallery(
    listState: LazyListState,
    images: List<NekoImage>,
    modifier: Modifier
) {
    LazyRow(
        state = listState,
        content = {
            items(
                count = images.size,
                itemContent = { index ->
                    var showPlaceHolder by remember { mutableStateOf(true) }
                    val image = images[index]
                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                    NekoGalleryItem(
                        context = LocalContext.current,
                        scope = rememberCoroutineScope(),
                        artistHref = image.artistHref,
                        artistName = image.artistName,
                        sourceUrl = image.sourceUrl,
                        url = image.url,
                        modifier = modifier
                            .fillMaxHeight()
                            .width(screenWidth)
                            .padding(8.dp)
                            .placeholder(
                                visible = showPlaceHolder,
                                color = Color.LightGray,
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
