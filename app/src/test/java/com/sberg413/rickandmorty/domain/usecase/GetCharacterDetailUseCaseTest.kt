package com.sberg413.rickandmorty.domain.usecase

import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.repository.CharacterRepository
import com.sberg413.rickandmorty.data.repository.LocationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GetCharacterDetailUseCaseTest {

    @Mock
    private lateinit var characterRepository: CharacterRepository

    @Mock
    private lateinit var locationRepository: LocationRepository

    private lateinit var useCase: GetCharacterDetailUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetCharacterDetailUseCase(characterRepository, locationRepository, testDispatcher)
    }

    @Test
    fun `invoke returns character detail with location on success`() = runTest {
        val characterId = 1
        val character = TestData.TEST_CHARACTER
        val location = TestData.TEST_LOCATION

        Mockito.`when`(characterRepository.getCharacter(characterId))
            .thenReturn(ApiResult.Success(character))
        Mockito.`when`(locationRepository.getLocation(character.locationId!!))
            .thenReturn(ApiResult.Success(location))

        val result = useCase(characterId)

        assert(result is ApiResult.Success)
        result as ApiResult.Success
        assertEquals(character, result.data.character)
        assertEquals(location, result.data.location)
    }

    @Test
    fun `invoke returns character detail without location on success`() = runTest {
        val characterId = 1
        val character = TestData.TEST_CHARACTER_NO_LOCATION

        Mockito.`when`(characterRepository.getCharacter(characterId))
            .thenReturn(ApiResult.Success(character))

        val result = useCase(characterId)

        assert(result is ApiResult.Success)
        result as ApiResult.Success
        assertEquals(character, result.data.character)
        assertEquals(null, result.data.location)
    }

    @Test
    fun `invoke returns error when character repository returns error`() = runTest {
        val characterId = 1
        val errorCode = 404
        val errorMessage = "Character not found"

        Mockito.`when`(characterRepository.getCharacter(characterId))
            .thenReturn(ApiResult.Error(errorCode, errorMessage))

        val result = useCase(characterId)

        assert(result is ApiResult.Error)
        result as ApiResult.Error
        assertEquals(errorCode, result.code)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `invoke returns exception when character repository throws exception`() = runTest {
        val characterId = 1
        val exception = Exception("Network error")

        Mockito.`when`(characterRepository.getCharacter(characterId))
            .thenReturn(ApiResult.Exception(exception))

        val result = useCase(characterId)

        assert(result is ApiResult.Exception)
        result as ApiResult.Exception
        assertEquals(exception, result.e)
    }

    @Test
    fun `invoke returns error when location repository returns error`() = runTest {
        val characterId = 1
        val character = TestData.TEST_CHARACTER
        val errorCode = 404
        val errorMessage = "Location not found"

        Mockito.`when`(characterRepository.getCharacter(characterId))
            .thenReturn(ApiResult.Success(character))
        Mockito.`when`(locationRepository.getLocation(character.locationId!!))
            .thenReturn(ApiResult.Error(errorCode, errorMessage))

        val result = useCase(characterId)

        assert(result is ApiResult.Error)
        result as ApiResult.Error
        assertEquals(errorCode, result.code)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `invoke returns exception when location repository throws exception`() = runTest {
        val characterId = 1
        val character = TestData.TEST_CHARACTER
        val exception = Exception("Network error")

        Mockito.`when`(characterRepository.getCharacter(characterId))
            .thenReturn(ApiResult.Success(character))
        Mockito.`when`(locationRepository.getLocation(character.locationId!!))
            .thenReturn(ApiResult.Exception(exception))

        val result = useCase(characterId)

        assert(result is ApiResult.Exception)
        result as ApiResult.Exception
        assertEquals(exception, result.e)
    }
}
