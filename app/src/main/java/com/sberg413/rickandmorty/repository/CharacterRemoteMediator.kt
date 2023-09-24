package com.sberg413.rickandmorty.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sberg413.rickandmorty.api.ApiService
import com.sberg413.rickandmorty.db.CharacterDatabase
import com.sberg413.rickandmorty.db.RemoteKeys
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

    // val regex = "https://.*page=".toRegex()

    private val pattern = Pattern.compile("https://.*page=([0-9]+).*")

    companion object {
        private const val CHARACTER_STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Character>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = state.getRemoteKeyClosestToCurrentPosition()
                remoteKeys?.nextKey?.minus(1) ?: CHARACTER_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = state.getRemoteKeyForFirstItem()
                remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = state.getRemoteKeyForLastItem()
                remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

//        val page = when (loadType) {
//            LoadType.REFRESH -> CHARACTER_STARTING_PAGE_INDEX
//            // In this example, you never need to prepend, since REFRESH
//            // will always load the first page in the list. Immediately
//            // return, reporting end of pagination.
//            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
//            // Query remoteKeyDao for the next RemoteKey.
//            LoadType.APPEND -> {
//                val remoteKeys = state.getRemoteKeyForLastItem()
//                remoteKeys?.nextKey
//                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
//            }
//        }

        try {
            val apiResponse = apiService.getCharacterList(page, queryName, queryStatus)

            val characters = apiResponse.results
            val responseInfo = apiResponse.info
            val endOfPaginationReached = characters.isEmpty()
            characterDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    characterDatabase.remoteKeysDao().clearRemoteKeys()
                    characterDatabase.charactersDao().clearCharacters()
                }
                val prevKey = responseInfo.prev?.let { extractPageNumber(it) }
                val nextKey = responseInfo.next?.let { extractPageNumber(it) }
                val keys = characters.map {
                    RemoteKeys(characterId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                characterDatabase.remoteKeysDao().insertAll(keys)
                characterDatabase.charactersDao().insertAll(characters)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
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

    private suspend fun PagingState<Int, Character>.getRemoteKeyForLastItem(): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character ->
                // Get the remote keys of the last item retrieved
                characterDatabase.remoteKeysDao().remoteKeysCharacterId(character.id)
            }
    }

    private suspend fun PagingState<Int, Character>.getRemoteKeyForFirstItem(): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { character ->
                // Get the remote keys of the first items retrieved
                characterDatabase.remoteKeysDao().remoteKeysCharacterId(character.id)
            }
    }

    private suspend fun PagingState<Int, Character>.getRemoteKeyClosestToCurrentPosition(): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return anchorPosition?.let { position ->
            closestItemToPosition(position)?.id?.let { characterId ->
                characterDatabase.remoteKeysDao().remoteKeysCharacterId(characterId)
            }
        }
    }
}