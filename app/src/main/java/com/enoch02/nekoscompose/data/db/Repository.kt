package com.enoch02.nekoscompose.data.db

import kotlinx.coroutines.flow.Flow

class FavouritesRepository(private val dao: FavouriteDao) {

    suspend fun insertFavourite(neko: Neko) {
        dao.insertFavorite(neko)
    }

    suspend fun removeFavourite(url: String) {
        dao.removeFavorite(url)
    }

    suspend fun checkUrl(url: String): Boolean = dao.checkUrl(url)

    fun getAll(): Flow<List<Neko>> = dao.getAllFavourites()
}