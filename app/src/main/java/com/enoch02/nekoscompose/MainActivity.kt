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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import com.enoch02.nekoscompose.ui.screens.main.SettingsScreen
import com.enoch02.nekoscompose.ui.theme.NekosComposeTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /** Don't delete: Initializes the app's ViewModel **/
            val mainViewModel: MainViewModel =
                viewModel(factory = MainViewModelFactory(application = LocalContext.current.applicationContext as Application))
            val homeListState = rememberLazyListState()
            val favouritesListState = rememberLazyListState()
            val scope = rememberCoroutineScope()

            NekosComposeTheme {
                var selectedScreen by rememberSaveable { mutableStateOf(0) }

                Scaffold(
                    topBar = {
                        AppScreenTopBar(
                            currentScreen = selectedScreen,
                            resetScrollStates = {
                                //TODO: does not work offline
                                scope.launch {
                                    homeListState.scrollToItem(0)
                                    favouritesListState.scrollToItem(0)
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            R.string.home,
                            R.string.search,
                            R.string.favorites,
                            R.string.settings
                        )
                        val icons = listOf(
                            Icons.Default.Home,
                            Icons.Default.Search,
                            Icons.Default.Favorite,
                            Icons.Default.Settings
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
                                        homeListState = homeListState
                                    )
                                }

                                1 -> {
                                    SearchScreen(modifier = Modifier.padding(innerPadding))
                                }

                                2 -> {
                                    FavouritesScreen(
                                        modifier = Modifier.padding(innerPadding),
                                        favouritesListState = favouritesListState
                                    )
                                }

                                3 -> {
                                    SettingsScreen(modifier = Modifier.padding(innerPadding))
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
