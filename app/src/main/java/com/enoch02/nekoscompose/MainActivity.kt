@file:OptIn(ExperimentalMaterial3Api::class)

package com.enoch02.nekoscompose

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enoch02.nekoscompose.data.model.MainViewModel
import com.enoch02.nekoscompose.data.model.MainViewModelFactory
import com.enoch02.nekoscompose.ui.composables.AppScreenTopBar
import com.enoch02.nekoscompose.ui.screens.main.FavouritesScreen
import com.enoch02.nekoscompose.ui.screens.main.HomeScreen
import com.enoch02.nekoscompose.ui.screens.main.SearchScreen
import com.enoch02.nekoscompose.ui.theme.NekosComposeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel =
                viewModel(factory = MainViewModelFactory(application = LocalContext.current.applicationContext as Application))
            val listState = rememberLazyListState()

            NekosComposeTheme {
                // TODO: change back to zero
                var selectedScreen by rememberSaveable { mutableStateOf(1) }

                Scaffold(
                    topBar = {
                        /*when (selectedScreen) {
                            0 -> {
                                AppScreenTopBar(currentScreen = selectedScreen)
                            }
                            1 -> {
                                SearchScreenTopBar()
                            }
                            2 -> {
                                AppScreenTopBar(currentScreen = selectedScreen)
                            }
                        }*/
                        AppScreenTopBar(currentScreen = selectedScreen)
                    },
                    bottomBar = {
                        val items = listOf(R.string.home, R.string.search, R.string.favorites)
                        val icons = listOf(
                            Icons.Default.Home,
                            Icons.Default.Search,
                            Icons.Default.Favorite
                        )

                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedScreen == index,
                                    onClick = { selectedScreen = index },
                                    label = { Text(text = stringResource(item)) },
                                    icon = {
                                        Icon(
                                            imageVector = icons[index],
                                            contentDescription = stringResource(item)
                                        )
                                    }
                                )
                            }
                        }
                    },
                    content = { innerPadding ->
                        Crossfade(targetState = selectedScreen) { selectedScreen ->
                            when (selectedScreen) {
                                0 -> {
                                    HomeScreen(
                                        modifier = Modifier.padding(innerPadding),
                                        listState = listState
                                    )
                                }
                                1 -> {
                                    SearchScreen(modifier = Modifier.padding(innerPadding))
                                }
                                2 -> {
                                    FavouritesScreen(modifier = Modifier.padding(innerPadding))
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
