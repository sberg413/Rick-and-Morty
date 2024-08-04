package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sberg413.rickandmorty.data.local.entity.CharacterEntity

class FakePagingSource: PagingSource<Int,CharacterEntity>() {

    lateinit var loadResult: LoadResult<Int,CharacterEntity>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
        return loadResult
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterEntity>): Int? {
        return null
    }
}