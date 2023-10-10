package com.sberg413.rickandmorty.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sberg413.rickandmorty.api.ApiService
import com.sberg413.rickandmorty.db.CharacterDatabase
import com.sberg413.rickandmorty.db.RemoteKey
import com.sberg413.rickandmorty.models.Character
import retrofit2.HttpException
import java.io.IOException
import java.util.regex.Pattern



@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val queryName: String?,
    private val queryStatus: String?,
    private val apiService: ApiService,
    private val characterDatabase: CharacterDatabase
) : RemoteMediator<Int, Character>() {

    private val pattern = Pattern.compile("https://.*page=([0-9]+).*")

    companion object {
        private const val TAG = "CharacterRemoteMediator"
        private const val CHARACTER_STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Character>): MediatorResult {

        Log.d(TAG, "load() LoadType = $loadType | state = $state")

        val loadKeys = when (loadType) {
            LoadType.REFRESH -> null
            // In this example, you never need to prepend, since REFRESH
            // will always load the first page in the list. Immediately
            // return, reporting end of pagination.
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            // Query remoteKeyDao for the next RemoteKey.
            LoadType.APPEND -> state.lastItemOrNull()?.let {
                    characterDatabase.remoteKeysDao().remoteKeysCharacterId(it.id)
                } ?: return MediatorResult.Success(endOfPaginationReached = true)
        }

        try {
            val page = loadKeys?.nextKey ?: CHARACTER_STARTING_PAGE_INDEX
            val apiResponse = apiService.getCharacterList(page, queryName, queryStatus)

            val characters = apiResponse.results
            val responseInfo = apiResponse.info
            val prevKey = responseInfo.prev?.let { page-1 }
            val nextKey = responseInfo.next?.let { page+1 }

            characterDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    characterDatabase.remoteKeysDao().clearRemoteKeys()
                    characterDatabase.charactersDao().clearCharacters()
                }

                val keys = characters.map {
                    RemoteKey(characterId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                characterDatabase.remoteKeysDao().insertAll(keys)
                characterDatabase.charactersDao().insertAll(characters)
            }
            return MediatorResult.Success(endOfPaginationReached = nextKey == null)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }

    private fun extractPageNumber(url: String): Int? {
        val matcher = pattern.matcher(url)
        return if (matcher.matches()) {
            matcher.group(1)?.toInt()
        } else null
    }
}