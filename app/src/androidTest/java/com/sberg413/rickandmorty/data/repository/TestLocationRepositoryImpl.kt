package com.sberg413.rickandmorty.data.repository

import com.sberg413.rickandmorty.data.remote.api.ApiResult
import com.sberg413.rickandmorty.data.model.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestLocationRepositoryImpl @Inject constructor(): LocationRepository {

    var location: Location? = null

    override suspend fun getLocation(id: String): ApiResult<Location> {
        return location?.let { ApiResult.Success(it) }
            ?: throw Exception("Location not found in test! Did you set it?")
    }

}