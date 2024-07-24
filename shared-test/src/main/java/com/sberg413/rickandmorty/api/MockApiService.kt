package com.sberg413.rickandmorty.api

import com.sberg413.rickandmorty.data.api.CharacterService
import com.sberg413.rickandmorty.data.api.LocationService
import com.sberg413.rickandmorty.data.api.dto.CharacterListApi
import com.sberg413.rickandmorty.data.model.Location
import retrofit2.Response
import kotlin.math.ceil

class MockApiService : CharacterService, LocationService {

    companion object {
        private const val ITEMS_PER_PAGE = 2
    }

    private val characters = mutableListOf<CharacterListApi.Result>()

//     private val info = CharacterListApi.Info(1,
//                    "https://rickandmortyapi.com/api/character?page=2",
//                    2,
//                    null
//                )

    override suspend fun getCharacterList(
        page: Int,
        name: String?,
        status: String?
    ): CharacterListApi {


        val offsetStart = (page - 1) * ITEMS_PER_PAGE
        val offsetEnd = page * ITEMS_PER_PAGE

        val subList = if (characters.size > ITEMS_PER_PAGE){
            characters.subList(offsetStart, offsetEnd)
        } else {
            characters
        }

        val pages = ceil(characters.size / ITEMS_PER_PAGE.toDouble()).toInt()

        val next = page.takeIf { page < pages}?.let {
            "https://rickandmortyapi.com/api/character?page=${it+1}"
        }

        val prev = page.takeIf { page > 0 }?.let {
            "https://rickandmortyapi.com/api/character?page=${it-1}"
        }

        val info = CharacterListApi.Info(
            count = subList.size,
            next = next,
            pages = pages,
            prev = prev
        )

        return CharacterListApi(info, subList)
    }

    override suspend fun getLocation(id: String): Response<Location> {
        TODO("Not yet implemented")
    }

    fun addCharacterResult(result: CharacterListApi.Result) {
        characters.add(result)
    }
}