package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingData
import com.sberg413.rickandmorty.data.remote.api.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun getCharacterList( search: String, status: String): Flow<PagingData<Character>>
    suspend fun getCharacter( id: Int): ApiResult<Character>
}