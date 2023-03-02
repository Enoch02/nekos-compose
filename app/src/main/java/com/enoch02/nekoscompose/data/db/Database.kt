package com.enoch02.nekoscompose.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Neko::class], version = 1, exportSchema = true)
abstract class FavouritesDatabase : RoomDatabase() {
    abstract fun getDao(): FavouriteDao

    companion object {
        private var instance: FavouritesDatabase? = null

        @Synchronized
        fun getDataBase(context: Context): FavouritesDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavouritesDatabase::class.java,
                    "neko"
                ).build()
            }
            return instance!!
        }
    }
}