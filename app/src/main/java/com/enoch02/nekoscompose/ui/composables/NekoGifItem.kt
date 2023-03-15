package com.enoch02.nekoscompose.ui.composables

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun NekoGifItem(
    anime_name: String,
    url: String,
    context: Context,
    modifier: Modifier,
    onLoadingComplete: () -> Unit
) {
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build(),
        contentDescription = anime_name,
        imageLoader = imageLoader,
        onSuccess = { onLoadingComplete.invoke() },
        modifier = modifier
    )
}