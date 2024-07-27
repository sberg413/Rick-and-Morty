package com.sberg413.rickandmorty.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.api.CharacterService
import com.sberg413.rickandmorty.data.api.dto.CharacterDTO
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.remote.CharacterRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterService: CharacterService,
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CharacterRepository {

    override suspend fun getCharacterList(search: String?, status: String?) : Flow<PagingData<Character>> {
        Log.d(TAG,"getCharacterList() name= $search | status= $status ")
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CharacterPagingSource(characterService, search, status) }
        )
            .flow
            .map { pagingData ->
               pagingData.map {
                   it.toCharacter()
               }
            }
            .flowOn(dispatcher)
    }

    override suspend fun getCharacter(id: Int): ApiResult<Character> = withContext(dispatcher) {
        return@withContext when (val response =  characterRemoteDataSource.invoke(id)) {
            is ApiResult.Success -> ApiResult.Success(response.data.toCharacter())
            is ApiResult.Error -> ApiResult.Error(response.code, response.message)
            is ApiResult.Exception -> ApiResult.Exception(response.e)
        }
    }


    companion object {
        private const val TAG = "CharacterRepositoryImpl"
        const val NETWORK_PAGE_SIZE = 20

        private fun CharacterDTO.toCharacter(): Character {
            return Character(
                id,
                status,
                species,
                type,
                gender,
                origin.url.split("/").last(),
                location.url.split("/").last(),
                image,
                name)
        }
    }
}