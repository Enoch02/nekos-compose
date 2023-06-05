package com.enoch02.nekoscompose.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun SettingsScreen(modifier: Modifier) {
    val cacheDir = LocalContext.current.cacheDir
    var cacheSize by rememberSaveable { mutableStateOf(getDirSize(cacheDir)) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        content = {
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = null
                        )
                    },
                    headlineContent = { Text(text = "Clear Cache") },
                    supportingContent = { Text(text = "Current Cache Size: ${cacheSize / 1_000_000} MB") },
                    modifier = Modifier.clickable {
                        if (cacheDir.exists())
                            cacheDir.deleteRecursively()
                        cacheSize = getDirSize(cacheDir)
                    }
                )

                Divider(modifier = Modifier.padding(horizontal = 8.dp))
            }

            item {
                //TODO: make functional
                var checked by rememberSaveable { mutableStateOf(true) }

                ListItem(
                    headlineContent = { Text(text = "Dynamic colors") },
                    supportingContent = { Text(text = "Toggle dynamic colors on supported devices") },
                    trailingContent = {
                        Switch(
                            checked = checked,
                            onCheckedChange = { checked = it }
                        )
                    },
                    modifier = Modifier.clickable {
                        checked = !checked
                    }.alpha(0f)
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(text = "About") },
                    modifier = Modifier.alpha(0f)
                )
            }
        }
    )

    /*Column(
        modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable {
                    if (cacheDir.exists())
                        cacheDir.deleteRecursively()
                    cacheSize = getDirSize(cacheDir)
                },
            content = {
                Text(text = "Clear Cache")
                Text(text = "Current Cache Size: ${cacheSize / 1_000_000} MB")
            }
        )
        Divider()
    }*/
}

//TODO: make asnyc?
fun getDirSize(dir: File): Long {
    var size: Long = 0
    val files = dir.listFiles()

    if (files != null) {
        for (file in files) {
            size += if (file.isFile) {
                file.length()
            } else {
                getDirSize(file)
            }
        }
    }

    return size
}