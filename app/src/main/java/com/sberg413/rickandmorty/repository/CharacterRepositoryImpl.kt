package com.sberg413.rickandmorty.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sberg413.rickandmorty.api.ApiService
import com.sberg413.rickandmorty.db.CharacterDatabase
import com.sberg413.rickandmorty.models.Character
import com.sberg413.rickandmorty.models.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val characterDatabase: CharacterDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacterList(search: String?, status: String?) : Flow<PagingData<Character>> {
        Log.d(TAG,"getCharacterList() name= $search | status= $status ")
        // val regex = "https://.*/".toRegex()

        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = search?.let {  "%${search.replace(' ', '%')}%" }
        val dbPagingSourceFactory =  { characterDatabase.charactersDao().charactersByName(dbQuery, status)}

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(
                search,
                status,
                apiService,
                characterDatabase
            ),
            pagingSourceFactory = dbPagingSourceFactory // { CharacterPagingSource(apiService, search, status) }
        )
            .flow
//            .map { pagingData ->
//               pagingData.map {
//                   val originId = regex.replace(it.origin.url,"")
//                   val locationId = regex.replace(it.location.url, "")
//                   Character(
//                       it.id,
//                       it.status,
//                       it.species,
//                       it.type,
//                       it.gender,
//                       originId,
//                       locationId,
//                       it.image,
//                       it.name)
//               }
//            }
            .flowOn(dispatcher)
    }

    override suspend fun getLocation(id: String): Location {
        // emit(Resource.loading(data = null))
        try {
           return apiService.getLocation(id)
        } catch (exception: Exception){
            Log.e(TAG,"Error retrieving location! ", exception)
            // emit(Resource.error(data=null,message = exception.message?:"Error occured"))
            throw (exception)
        }
    }

    companion object {
        private const val TAG = "CharacterRepositoryImpl"
        const val NETWORK_PAGE_SIZE = 20
    }
}