package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingData
import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.data.remote.api.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestCharacterRepositoryImpl @Inject constructor(): CharacterRepository {

    var characterResponse: ApiResult<Character> = ApiResult.Success(TestData.TEST_CHARACTER)

    override suspend fun getCharacterList(search: String, status: String): Flow<PagingData<Character>> = flow {
        emit(PagingData.empty())
    }

    override suspend fun getCharacter(id: Int): ApiResult<Character> {
        return characterResponse
    }

}