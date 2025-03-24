package com.example.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity

@Dao
interface FavoriteEventDao {
    @Query("SELECT * FROM favorite_events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Int): FavoriteEventEntity?

    @Query("SELECT * FROM favorite_events ORDER BY beginTime DESC")
    fun getEventList(): LiveData<List<FavoriteEventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: FavoriteEventEntity)

    @Query("DELETE FROM favorite_events WHERE id = :id")
    suspend fun deleteEvent(id: Int)
}