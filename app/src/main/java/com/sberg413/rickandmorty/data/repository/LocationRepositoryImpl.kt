package com.sberg413.rickandmorty.data.repository

import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.model.Location
import com.sberg413.rickandmorty.data.remote.LocationRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationRemoteDataSource: LocationRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocationRepository {
    override suspend fun getLocation(id: String): ApiResult<Location> =
        withContext(dispatcher){ locationRemoteDataSource.invoke(id) }

}