package com.sberg413.rickandmorty.domain.usecase

import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.domain.model.CharacterDetail
import com.sberg413.rickandmorty.data.model.Location
import com.sberg413.rickandmorty.data.repository.CharacterRepository
import com.sberg413.rickandmorty.data.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(characterId: Int): ApiResult<CharacterDetail> =
        withContext(dispatcher) {
            val charResult = characterRepository.getCharacter(characterId)
            return@withContext when (charResult) {
                is ApiResult.Success -> {
                    val locationId = charResult.data.locationId
                    if (!locationId.isNullOrBlank()) {
                        handleLocationResult(charResult.data, locationRepository.getLocation(locationId))
                    } else {
                        ApiResult.Success(CharacterDetail(charResult.data, null))
                    }
                }

                is ApiResult.Error -> {
                    ApiResult.Error(charResult.code, charResult.message)
                }

                is ApiResult.Exception -> {
                    ApiResult.Exception(charResult.e)
                }
            }
        }

    private fun handleLocationResult(character: Character, locationResult: ApiResult<Location>): ApiResult<CharacterDetail> {
        return when (locationResult) {
            is ApiResult.Success -> {
                ApiResult.Success(CharacterDetail(character, locationResult.data))
            }

            is ApiResult.Error -> {
                ApiResult.Error(locationResult.code, locationResult.message)
            }

            is ApiResult.Exception -> {
                ApiResult.Exception(locationResult.e)
            }
        }
    }

}