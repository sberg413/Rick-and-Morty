package com.sberg413.rickandmorty.data.remote

import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.api.CharacterService
import com.sberg413.rickandmorty.data.api.dto.CharacterDTO
import com.sberg413.rickandmorty.data.api.handleApiResponse
import javax.inject.Inject

class CharacterRemoteDataSource @Inject constructor(
    private val characterService: CharacterService
) {
    suspend operator fun invoke(id: Int): ApiResult<CharacterDTO> {
        return handleApiResponse { characterService.getCharacter(id) }
    }
}