package com.sberg413.rickandmorty.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.remote.dto.CharacterDTO
import com.sberg413.rickandmorty.data.local.db.AppDatabase
import com.sberg413.rickandmorty.data.local.entity.CharacterEntity
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.remote.CharacterRemoteDataSource
import com.sberg413.rickandmorty.data.remote.CharacterRemoteMediator
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
    private val appDatabase: AppDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getCharacterList(search: String?, status: String?) : Flow<PagingData<Character>> {
        Log.d(TAG,"getCharacterList() name= $search | status= $status ")
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE
            ),
            remoteMediator = CharacterRemoteMediator(
                search,
                status,
                characterService,
                appDatabase
            ),
            pagingSourceFactory = {
                appDatabase.characterDao().getPagingSource(search ?: "", status ?: "")
            }
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

        private fun CharacterEntity.toCharacter(): Character {
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