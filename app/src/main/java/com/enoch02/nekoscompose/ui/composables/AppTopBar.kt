package com.enoch02.nekoscompose.ui.composables

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enoch02.nekoscompose.data.model.MainViewModel
import com.enoch02.nekoscompose.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreenTopBar(
    mainViewModel: MainViewModel = viewModel(),
    currentScreen: Int,
    resetScrollStates: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        actions = {
            // refresh button
            IconButton(
                onClick = {
                    resetScrollStates()
                    mainViewModel.refresh()
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh)
                    )
                }
            )

            // clear search button
            if (currentScreen == 1) {
                IconButton(
                    onClick = {
                        mainViewModel.showingResults = false
                        mainViewModel.clearResults()
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                )
            }
        }
    )
}