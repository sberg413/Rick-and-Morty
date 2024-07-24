package com.sberg413.rickandmorty.data.api

import com.sberg413.rickandmorty.data.api.dto.CharacterListApi
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterService {

    @GET("character")
    suspend fun getCharacterList(
        @Query("page") page: Int,
        @Query("name") name: String?,
        @Query("status") status: String?
    ) : CharacterListApi

//    @GET("character/{id}")
//    fun getCharacterDetail(@Path("id")  id: Int): Call<Character>
}