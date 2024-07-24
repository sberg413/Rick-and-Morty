package com.sberg413.rickandmorty.data.remote

import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.api.LocationService
import com.sberg413.rickandmorty.data.api.handleApiResponse
import com.sberg413.rickandmorty.data.model.Location
import javax.inject.Inject

class LocationRemoteDataSource @Inject constructor(
    private val locationService: LocationService
) {
    suspend operator fun invoke(id: String): ApiResult<Location> =
        handleApiResponse{ locationService.getLocation(id) }
}