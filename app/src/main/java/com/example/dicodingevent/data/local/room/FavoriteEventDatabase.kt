package com.example.dicodingevent.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity

@Database(entities = [FavoriteEventEntity::class], version = 3, exportSchema = false)
abstract class FavoriteEventDatabase : RoomDatabase() {
    abstract fun eventDao(): FavoriteEventDao

    companion object {
        @Volatile
        private var instance: FavoriteEventDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): FavoriteEventDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteEventDatabase::class.java,
                    "favorite_event_database"
                ).build()
            }
    }
}