package com.sberg413.rickandmorty.data.api

import com.sberg413.rickandmorty.data.model.Location
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationService {
    @GET("location/{id}")
    suspend fun getLocation(@Path("id") id: String): Response<Location>
}