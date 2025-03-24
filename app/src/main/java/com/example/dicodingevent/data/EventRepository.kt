package com.example.dicodingevent.data

import androidx.lifecycle.LiveData
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity
import com.example.dicodingevent.data.local.room.FavoriteEventDao
import com.example.dicodingevent.data.remote.api.ApiService
import com.example.dicodingevent.data.remote.response.EventItem
import okio.IOException

class EventRepository private constructor(private val apiService: ApiService, private val favEventDao: FavoriteEventDao){

    suspend fun getUpcomingEventList(limitNumber: Int? = null): Result<List<EventItem>> {
        return try {
            val response = apiService.getEventList(1, limit = limitNumber)
            if (response.isSuccessful) {
                Result.Success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.Error("Failed to load data from API, Status code: ${response.code()}")
            }
        } catch (e: Exception) {
            if (e is IOException) {
                Result.Error("No connection")
            } else {
                Result.Error(e.message.toString())
            }
        }
    }

    suspend fun getFinishedEventList(word: String? = null, limitNumber: Int? = null): Result<List<EventItem>> {
        return try {
            val response = apiService.getEventList(0, limit = limitNumber, keyword = word)
            if (response.isSuccessful) {
                Result.Success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.Error("Failed to load data from API, Status code: ${response.code()}")
            }
        } catch (e: Exception) {
            if (e is IOException) {
                Result.Error("No connection")
            } else {
                Result.Error(e.message.toString())
            }
        }
    }

    suspend fun getAllEventList(word: String? = null): Result<List<EventItem>> {
        return try {
            val response = apiService.getEventList(-1, keyword = word)
            if (response.isSuccessful) {
                Result.Success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.Error("Failed to load data from API, Status code: ${response.code()}")
            }
        } catch (e: Exception) {
            if (e is IOException) {
                Result.Error("No connection")
            } else {
                Result.Error(e.message.toString())
            }
        }
    }

    suspend fun getDetailEvent(id: Int): Result<EventItem> {
        return try {
            val response = apiService.getEvent(id)
            if (response.isSuccessful) {
                Result.Success(response.body()?.event ?: EventItem())
            } else {
                Result.Error("Failed to load data from API, Status code: ${response.code()}")
            }
        } catch (e: Exception) {
            if (e is IOException) {
                Result.Error("No connection")
            } else {
                Result.Error(e.message.toString())
            }
        }
    }

    suspend fun insertFavoriteEvent(event: EventItem) {
        val eventEntity = FavoriteEventEntity(
            event.id ?: 0,
            name = event.name,
            mediaCover = event.mediaCover,
            imageLogo = event.imageLogo,
            beginTime = event.beginTime
        )
        favEventDao.insertEvent(eventEntity)
    }

    suspend fun deleteFavoriteEvent(id: Int) {
        favEventDao.deleteEvent(id)
    }

    suspend fun getFavoriteEvent(id: Int): FavoriteEventEntity? {
        return favEventDao.getEventById(id)
    }

    fun getFavoriteEventList(): LiveData<List<FavoriteEventEntity>> {
        return favEventDao.getEventList()
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            favEventDao: FavoriteEventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, favEventDao)
            }.also { instance = it }
    }
}