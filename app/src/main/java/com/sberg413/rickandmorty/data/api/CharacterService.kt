package com.sberg413.rickandmorty.data.api

import com.sberg413.rickandmorty.data.api.dto.CharacterListApi
import com.sberg413.rickandmorty.data.api.dto.CharacterDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharacterService {

    @GET("character")
    suspend fun getCharacterList(
        @Query("page") page: Int,
        @Query("name") name: String?,
        @Query("status") status: String?
    ) : CharacterListApi

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id")  id: Int
    ): Response<CharacterDTO>
}