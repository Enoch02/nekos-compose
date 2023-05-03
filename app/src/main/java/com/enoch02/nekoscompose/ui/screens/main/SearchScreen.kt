@file:OptIn(ExperimentalMaterial3Api::class)

package com.enoch02.nekoscompose.ui.screens.main

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enoch02.nekoscompose.R
import com.enoch02.nekoscompose.data.model.MainViewModel
import com.enoch02.nekoscompose.data.model.MainViewModelFactory
import com.enoch02.nekoscompose.ui.composables.NekoGalleryItem
import com.enoch02.nekoscompose.ui.composables.NekoGifItem
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.CoroutineScope

//TODO: Replace with M3 SearchBar composable
@Composable
fun SearchScreen(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(application = LocalContext.current.applicationContext as Application)),
    modifier: Modifier
) {
    val context = LocalContext.current
    //val categories by mainViewModel.getCategories().collectAsState(initial = emptyList())
    val categories = mainViewModel.getCategoriesV1()
    val showPlaceHolders by mainViewModel.getCatPlaceHolderState()
        .collectAsState(initial = true)
    var query by rememberSaveable { mutableStateOf("") }

    var type by rememberSaveable { mutableStateOf("1") }
    var selectedImgChip by rememberSaveable { mutableStateOf(-1) }
    var selectedGifChip by rememberSaveable { mutableStateOf(-1) }
    var selectedCategoryString by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var selectedCategoryMax by rememberSaveable { mutableStateOf("0") }
        val selectedCategoryMin by rememberSaveable { mutableStateOf("0") }
        val startSearch = {
            if (query.isEmpty() || query.isBlank() || selectedCategoryString.isBlank()) {
                Toast.makeText(
                    context,
                    R.string.type_something_and,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mainViewModel.searchNeko(
                    query = query,
                    category = selectedCategoryString,
                    type = if (selectedGifChip == -1) "1" else "2",
                    amount = selectedCategoryMax
                )
            }
        }

        if (!mainViewModel.showingResults) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = { startSearch() },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    )
                },
                placeholder = { Text(text = stringResource(R.string.type_something)) },
                keyboardActions = KeyboardActions(
                    onDone = { startSearch() },
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!mainViewModel.showingResults) {
            LazyRow(
                content = {
                    val imgCategory = mainViewModel.getImgCategories()

                    item {
                        InputChip(
                            selected = false,
                            onClick = { },
                            label = { Text(text = stringResource(R.string.images)) },
                            enabled = false
                        )
                    }

                    items(
                        count = imgCategory.size,
                        itemContent = { index ->
                            InputChip(
                                selected = selectedImgChip == index,
                                onClick = {
                                    if (selectedImgChip == index) {
                                        selectedImgChip = -1
                                    } else {
                                        selectedImgChip = index
                                        type = "1"
                                        selectedCategoryString = imgCategory[index].key
                                        selectedCategoryMax = imgCategory[index].value.max
                                    }
                                },
                                label = {
                                    Text(text = imgCategory[index].key)
                                },
                                enabled = selectedGifChip == -1,
                                modifier = Modifier.placeholder(
                                    visible = showPlaceHolders,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(8.dp),
                                    highlight = PlaceholderHighlight.fade()
                                )
                            )
                        }
                    )
                },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            LazyRow(
                content = {
                    val gifCategory = mainViewModel.getGifCategories()

                    item {
                        InputChip(
                            selected = false,
                            onClick = { },
                            label = { Text(text = "gifs") },
                            enabled = false
                        )
                    }

                    items(
                        count = gifCategory.size,
                        itemContent = { index ->
                            InputChip(
                                selected = selectedGifChip == index,
                                onClick = {
                                    if (selectedGifChip == index) {
                                        selectedGifChip = -1
                                    } else {
                                        selectedGifChip = index
                                        type = "2"
                                        selectedCategoryString = gifCategory[index].key
                                        selectedCategoryMax = gifCategory[index].value.max
                                    }
                                },
                                label = {
                                    Text(text = gifCategory[index].key)
                                },
                                enabled = selectedImgChip == -1,
                                modifier = Modifier.placeholder(
                                    visible = showPlaceHolders,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            )
                        }
                    )
                },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
            )

            //Divider()
        }

        if (mainViewModel.showingResults) {
            //Divider()
            ResultsLayout(
                context = context,
                scope = rememberCoroutineScope(),
                type = type
            )
        }
    }
}

@Composable
fun ResultsLayout(
    context: Context,
    scope: CoroutineScope,
    viewModel: MainViewModel = viewModel(),
    type: String
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LazyColumn(
        state = rememberLazyListState(),
        content = {
            val items = viewModel.imgSearchResults
            val gifItems = viewModel.gifSearchResults

            if (type == "1") {
                items(
                    count = items.size,
                    itemContent = { index ->
                        var showPlaceHolder by rememberSaveable { mutableStateOf(true) }

                        NekoGalleryItem(
                            context = context,
                            scope = rememberCoroutineScope(),
                            artistHref = items[index].artistHref,
                            artistName = items[index].artistName,
                            sourceUrl = items[index].sourceUrl,
                            url = items[index].url,
                            modifier = Modifier
                                .fillParentMaxHeight()
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
            } else {
                items(
                    count = gifItems.size,
                    itemContent = { index ->
                        var showPlaceHolder by rememberSaveable { mutableStateOf(true) }

                        NekoGifItem(
                            anime_name = gifItems[index].anime_name,
                            url = gifItems[index].url,
                            context = context,
                            modifier = Modifier
                                .fillParentMaxHeight()
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
        },
        modifier = Modifier.fillMaxSize()
    )
}
