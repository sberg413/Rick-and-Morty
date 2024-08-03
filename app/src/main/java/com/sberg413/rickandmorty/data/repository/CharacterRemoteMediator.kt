package com.sberg413.rickandmorty.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.local.db.AppDatabase
import com.sberg413.rickandmorty.data.local.entity.CharacterEntity
import com.sberg413.rickandmorty.data.local.entity.RemoteKey
import com.sberg413.rickandmorty.data.local.entity.toEntity
import retrofit2.HttpException
import java.io.IOException
import java.net.URL

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val queryName: String?,
    private val queryStatus: String?,
    private val apiService: CharacterService,
    private val database: AppDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    companion object {
        private const val TAG = "CharacterRemoteMediator"
        private const val STARTING_PAGE_INDEX = 1

        fun getPageNumber(url: String?): Int? {
            return if (url != null) {
                try {
                    val query = URL(url).query
                    val regex = Regex("""page=(\d+)""")
                    regex.find(query)?.groupValues?.get(1)?.toInt()
                } catch (e: Exception) {
                    null
                }
            } else null
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        val name = queryName ?: ""
        val status = queryStatus ?: ""

        val page = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> {
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKey = database.withTransaction {
                    database.remoteKeyDao().remoteKeyByQuery(name,status)
                }

                // You must explicitly check if the page key is null when
                // appending, since null is only valid for initial load.
                // If you receive null for APPEND, that means you have
                // reached the end of pagination and there are no more
                // items to load.
                if (remoteKey.nextKey == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
                remoteKey.nextKey
            }
        }

        Log.d(TAG, "loading page $page for loadType $loadType")

        try {
            val apiResponse = apiService.getCharacterList(page, queryName, queryStatus)
            val characters = apiResponse.results
            val endOfPaginationReached = apiResponse.info.next == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().deleteByQuery(name,status)
                    database.characterDao().deleteByQuery(name,status)
                }
                val next = getPageNumber(apiResponse.info.next)
                val prev = getPageNumber(apiResponse.info.prev)
                val key = RemoteKey(name, status, prev, next)

                database.remoteKeyDao().insertOrReplace(key)
                database.characterDao().insertAll(characters.map { it.toEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

//    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CharacterEntity>): RemoteKey? {
//        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { character ->
//            database.remoteKeyDao().remoteKeysCharacterId(character.id)
//        }
//    }
//
//    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CharacterEntity>): RemoteKey? {
//        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { character ->
//            database.remoteKeyDao().remoteKeysCharacterId(character.id)
//        }
//    }
//
//    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, CharacterEntity>): RemoteKey? {
//        return state.anchorPosition?.let { position ->
//            state.closestItemToPosition(position)?.id?.let { characterId ->
//                database.remoteKeyDao().remoteKeysCharacterId(characterId)
//            }
//        }
//    }
}