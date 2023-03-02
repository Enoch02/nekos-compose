package com.enoch02.nekoscompose.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Insert
    suspend fun insertFavorite(neko: Neko)

    @Query("DELETE FROM favourites WHERE url = :url")
    suspend fun removeFavorite(url: String)

    @Query("SELECT EXISTS(SELECT url FROM favourites WHERE url = :url)")
    suspend fun checkUrl(url: String): Boolean

    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Flow<List<Neko>>
}