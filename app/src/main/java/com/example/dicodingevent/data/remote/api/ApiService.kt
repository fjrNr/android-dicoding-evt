package com.example.dicodingevent.data.remote.api

import com.example.dicodingevent.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events/{id}")
    suspend fun getEvent(
        @Path("id") id: Int
    ): Response<EventResponse>

    @GET("events")
    suspend fun getEventList(
        @Query("active") active: Int,
        @Query("q") keyword: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<EventResponse>

    @GET("events")
    fun getUpcomingEvent(
        @Query("active") active: Int = 1,
    ): Call<EventResponse>
}