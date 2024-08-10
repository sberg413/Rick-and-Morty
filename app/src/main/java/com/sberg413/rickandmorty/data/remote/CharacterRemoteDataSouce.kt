package com.sberg413.rickandmorty.data.remote

import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.handleApiResponse
import com.sberg413.rickandmorty.data.mapCharacterDtoToCharacter
import com.sberg413.rickandmorty.data.model.Character
import javax.inject.Inject

class CharacterRemoteDataSource @Inject constructor(
    private val characterService: CharacterService
) {
    suspend operator fun invoke(id: Int): ApiResult<Character> {
        return handleApiResponse(
            ::mapCharacterDtoToCharacter,
        ) { characterService.getCharacter(id) }
    }
}