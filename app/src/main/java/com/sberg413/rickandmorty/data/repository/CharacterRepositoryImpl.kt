package com.sberg413.rickandmorty.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.local.CharacterLocalDataSource
import com.sberg413.rickandmorty.data.local.dao.CharacterDao
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.remote.CharacterRemoteDataSource
import com.sberg413.rickandmorty.data.remote.CharacterRemoteMediator
import com.sberg413.rickandmorty.data.toCharacter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterRemoteMediator: CharacterRemoteMediator,
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterDao: CharacterDao,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getCharacterList(search: String?, status: String?) : Flow<PagingData<Character>> {
        Log.d(TAG,"getCharacterList() name= $search | status= $status ")
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE
            ),
            remoteMediator = characterRemoteMediator.apply {
                queryName = search
                queryStatus = status
            },
            pagingSourceFactory = {
                characterDao.getPagingSource(search ?: "", status ?: "")
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
        return@withContext characterLocalDataSource.invoke(id)?.let {
            ApiResult.Success(it.toCharacter())
        } ?: characterRemoteDataSource.invoke(id)
    }

    companion object {
        private const val TAG = "CharacterRepositoryImpl"
        const val NETWORK_PAGE_SIZE = 20


    }
}