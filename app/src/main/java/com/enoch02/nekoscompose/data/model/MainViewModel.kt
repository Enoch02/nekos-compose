package com.enoch02.nekoscompose.data.model

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enoch02.nekoscompose.data.ApiService
import com.enoch02.nekoscompose.data.db.FavouritesDatabase
import com.enoch02.nekoscompose.data.db.FavouritesRepository
import com.enoch02.nekoscompose.data.db.Neko
import com.enoch02.nekoscompose.util.FavouriteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val TAG = "MAIN_VIEW_MODEL"
const val NEKO_AMT = "20"

class MainViewModel(context: Context) : ViewModel() {
    private val apiService = ApiService.getInstance()
    private var categories: Map<String, Category> by mutableStateOf(mapOf())
    private var nekoCache: List<NekoImage> by mutableStateOf(emptyList())
    private var errorState by mutableStateOf(false)

    private var favouritesRepository = FavouritesRepository(
        dao = FavouritesDatabase.getDataBase(context.applicationContext).getDao()
    )

    private val latestCategory: Flow<Map<String, Category>> = flow {
        while (true) {
            val latestCategory = categories
            if (categories.isNotEmpty()) {
                emit(latestCategory)
            } else {
                //categories = apiService.getCategories()  //TODO: Remove?
                getCategories()
            }
            delay(5000)
        }
    }
    private val showCategoryPlaceHolders = flow {
        while (true) {
            latestCategory.collect { latestCategory ->
                if (latestCategory.isNotEmpty()) {
                    emit(false)
                } else emit(true)
            }
        }
    }
    var imgSearchResults: List<NekoImage> by mutableStateOf(emptyList())
    var gifSearchResults: List<NekoGif> by mutableStateOf(emptyList())
    var showingResults by mutableStateOf(false)
    var fullScreen by mutableStateOf(false)

    init {
        fillNekoCache()
        getCategories()
    }

    fun isCacheEmpty() = nekoCache.isEmpty()

    private fun fillNekoCache() = viewModelScope.launch {
        try {
            nekoCache = apiService.getRandomNeko(NEKO_AMT).results
        } catch (e: Exception) {
            Log.e(TAG, "fillNekoCache: ${e.message}")
            errorState = true
            nekoCache = emptyList()
        }
    }

    private fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                categories = apiService.getCategories()
            } catch (e: Exception) {
                Log.e(TAG, "init: ${e.message.toString()}")
            }
        }
    }

    fun getCategoriesV1() = categories.entries

    fun getImgCategories() = categories.entries.filter { it.value.format == "png" }

    fun getGifCategories() = categories.entries.filter { it.value.format == "gif" }

    fun getCatPlaceHolderState() = showCategoryPlaceHolders

    fun getNekoErrorState() = errorState

    fun refresh(amount: String = "20") {
        nekoCache = emptyList()
        errorState = false
        fillNekoCache()
        getCategories()
    }

    fun clearResults() {
        imgSearchResults = emptyList()
        gifSearchResults = emptyList()
    }

    fun getNeko(): List<NekoImage> {
        if (nekoCache.isEmpty()) {
            fillNekoCache()
        }
        return nekoCache
    }

    fun searchNeko(query: String, type: String, category: String, amount: String) {
        try {
            viewModelScope.launch {
                if (type == "1")
                    imgSearchResults = apiService.search(query, type, category, amount).results
                if (type == "2")
                    gifSearchResults = apiService.searchGif(query, type, category, amount).results
                showingResults = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "searchNeko: ${e.message}", e)
        }
    }

    fun getResults(type: String) = if (type == "1") imgSearchResults else gifSearchResults

    fun downloadImage(context: Context, url: String) {
        val uri = Uri.parse(url)
        val fileName = url.substring(url.lastIndexOf('/') + 1)
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )

        if (file.exists()) {
            //TODO: Test if this part of the code works...
            Log.d(TAG, "downloadImage: A file with the name '$fileName' exists!")
            return
        } else {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setMimeType("image/jpg")

            downloadManager.enqueue(request)
        }
    }

    fun shareImageLink(context: Context, url: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun viewProfile(context: Context, url: String) = context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
    )

    suspend fun insertFavourite(neko: Neko): FavouriteState {
        return withContext(viewModelScope.coroutineContext) {
            if (favouritesRepository.checkUrl(neko.url)) {
                favouritesRepository.removeFavourite(neko.url)
                return@withContext FavouriteState.REMOVING

            } else {
                favouritesRepository.insertFavourite(neko)
                return@withContext FavouriteState.ADDING
            }
        }
    }

    /** Is this url already in the favourites?*/
    suspend fun checkFavourite(url: String): Boolean {
        return withContext(viewModelScope.coroutineContext) {
            return@withContext favouritesRepository.checkUrl(url)
        }
    }

    fun getFavourites() = favouritesRepository.getAll()
}
