package com.sberg413.rickandmorty.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sberg413.rickandmorty.data.api.CharacterService
import com.sberg413.rickandmorty.data.api.dto.CharacterListApi
import com.sberg413.rickandmorty.data.model.Character
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterService: CharacterService,
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

    private fun CharacterListApi.Result.toCharacter(): Character {
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

    companion object {
        private const val TAG = "CharacterRepositoryImpl"
        const val NETWORK_PAGE_SIZE = 20
    }
}