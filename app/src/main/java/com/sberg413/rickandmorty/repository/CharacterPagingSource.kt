package com.sberg413.rickandmorty.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sberg413.rickandmorty.api.ApiService
import com.sberg413.rickandmorty.api.dto.CharacterListApi
import com.sberg413.rickandmorty.models.Character
import retrofit2.HttpException
import java.io.IOException

class CharacterPagingSource(
    private val apiService: ApiService,
    private val queryName: String?,
    private val queryStatus: String?
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            // Start refresh at page 1 if undefined.
            val page = params.key ?: STARTING_PAGE_INDEX
            val characterList = apiService.getCharacterList(page, queryName, queryStatus)
            Log.d(TAG, "Characters = $characterList")
            LoadResult.Page( data = characterList.results,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page == characterList.info.pages) null else page + 1 )
        } catch (e: IOException) {
            // IOException for network failures.
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return if (e.code() == 404){
                // The API returns 404 for a query with no results ...
                LoadResult.Page(emptyList(), null, null)
            } else {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val TAG = "CharacterPagingSource"

        private const val STARTING_PAGE_INDEX = 1
    }
}