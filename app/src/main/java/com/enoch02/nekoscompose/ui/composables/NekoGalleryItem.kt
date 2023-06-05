package com.enoch02.nekoscompose.ui.composables

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.enoch02.nekoscompose.R
import com.enoch02.nekoscompose.data.db.Neko
import com.enoch02.nekoscompose.data.model.MainViewModel
import com.enoch02.nekoscompose.data.model.MainViewModelFactory
import com.enoch02.nekoscompose.util.FavouriteState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NekoGalleryItem(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(application = LocalContext.current.applicationContext as Application)),
    context: Context,
    scope: CoroutineScope,
    artistHref: String,
    artistName: String,
    sourceUrl: String,
    url: String,
    modifier: Modifier,
    onLoadingComplete: () -> Unit
) {
    var isFavourite by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        isFavourite = mainViewModel.checkFavourite(url)
    }

    ElevatedCard(shape = RoundedCornerShape(8.dp), modifier = modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (image, surface) = createRefs()


            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build(),
                contentDescription = "Neko image by $artistName",
                contentScale = if (mainViewModel.fullScreen) ContentScale.Fit else ContentScale.Crop,
                onSuccess = { onLoadingComplete() },
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(surface.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(surface) {
                        top.linkTo(image.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                        visibility =
                            if (mainViewModel.fullScreen) Visibility.Gone else Visibility.Visible
                    },
                /*tonalElevation = 8.dp,*/
                content = {
                    Column {
                        Text(
                            text = "Artist: $artistName",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 2.dp)
                        )

                        ActionsRow(
                            onProfileClicked = {
                                mainViewModel.viewProfile(
                                    context,
                                    artistHref
                                )
                            },
                            onDownloadClicked = {
                                mainViewModel.downloadImage(
                                    context = context,
                                    url = url
                                )
                                Toast.makeText(
                                    context,
                                    R.string.downloading,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onShareButtonClicked = {
                                mainViewModel.shareImageLink(
                                    context,
                                    sourceUrl
                                )
                            },
                            onFavouriteButtonClicked = {
                                val neko = Neko(
                                    artistHref = artistHref,
                                    artistName = artistName,
                                    sourceUrl = sourceUrl,
                                    url = url
                                )
                                scope.launch {
                                    when (mainViewModel.insertFavourite(neko)) {
                                        FavouriteState.ADDING -> {
                                            Toast.makeText(
                                                context,
                                                "Adding to favourites",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        FavouriteState.REMOVING -> {
                                            Toast.makeText(
                                                context,
                                                "Removed from favourites",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    isFavourite = mainViewModel.checkFavourite(neko.url)
                                }
                            },
                            onExpandButtonClicked = {
                                mainViewModel.fullScreen = true
                            },
                            favouriteButtonTint = if (isFavourite) Color.Red else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
            )
        }
    }
}

@Composable
fun ActionsRow(
    onProfileClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
    onShareButtonClicked: () -> Unit,
    onFavouriteButtonClicked: () -> Unit,
    onExpandButtonClicked: () -> Unit,
    favouriteButtonTint: Color,
    modifier: Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        IconButton(
            onClick = onProfileClicked,
            content = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
        )

        // TODO: Ask for custom file name from a dialog before downloading
        IconButton(
            onClick = onDownloadClicked,
            content = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(R.string.save)
                )
            },
        )

        IconButton(
            onClick = onShareButtonClicked,
            content = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
        )

        IconButton(
            onClick = onFavouriteButtonClicked,
            content = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    tint = favouriteButtonTint,
                    contentDescription = stringResource(R.string.add_to_fav)
                )
            }
        )

        IconButton(
            onClick = onExpandButtonClicked,
            content = {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = stringResource(R.string.expand)
                )
            }
        )
    }
}
