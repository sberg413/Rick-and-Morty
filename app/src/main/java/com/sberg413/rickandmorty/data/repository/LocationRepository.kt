package com.sberg413.rickandmorty.data.repository

import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.model.Location


interface LocationRepository {
    suspend fun getLocation(id: String) : ApiResult<Location>
}