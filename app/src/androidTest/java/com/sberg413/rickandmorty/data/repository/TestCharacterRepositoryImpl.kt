package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingData
import com.sberg413.rickandmorty.data.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestCharacterRepositoryImpl @Inject constructor(): CharacterRepository {

    override suspend fun getCharacterList(search: String?, status: String?): Flow<PagingData<Character>> = flow {
        emit(PagingData.empty())
    }

}