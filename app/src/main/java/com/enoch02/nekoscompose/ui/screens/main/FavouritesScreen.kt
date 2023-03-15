package com.enoch02.nekoscompose.ui.screens.main

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(application = LocalContext.current.applicationContext as Application))
) {
    val items: List<Neko> by mainViewModel.getFavourites().collectAsState(initial = emptyList())

    if (items.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.HeartBroken,
                contentDescription = null,
                modifier = Modifier.scale(2f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_favourites),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyRow(
            modifier = modifier,
            content = {
                items(
                    count = items.size,
                    itemContent = { index ->
                        var showPlaceHolder by remember { mutableStateOf(true) }
                        val neko = items[index]
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
                                .padding(8.dp)
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