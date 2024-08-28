package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCharacterRepository(): CharacterRepository {

    var characterList: List<Character> = emptyList()

    override suspend fun getCharacterList(
        search: String?,
        status: String?
    ): Flow<PagingData<Character>> = flow {
        emit(PagingData.from(characterList))
    }

    override suspend fun getCharacter(id: Int): ApiResult<Character> {
        TODO("Not yet implemented")
    }
}