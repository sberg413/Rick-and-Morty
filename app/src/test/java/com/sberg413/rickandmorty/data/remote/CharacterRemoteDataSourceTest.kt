package com.sberg413.rickandmorty.data.remote

import com.sberg413.rickandmorty.TestDto
import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.remote.dto.CharacterDTO
import com.sberg413.rickandmorty.data.model.Character
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class CharacterRemoteDataSourceTest {

    @Mock
    private lateinit var characterService: CharacterService

    private lateinit var dataSource: CharacterRemoteDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dataSource = CharacterRemoteDataSource(characterService)
    }

    @Test
    fun `invoke returns character on success`() = runTest {
        val characterId = 1
        val character = TestDto.testCharacterDTO1

        Mockito.`when`(characterService.getCharacter(characterId))
            .thenReturn(Response.success(character))

        val result = dataSource.invoke(characterId)

        assert(result is ApiResult.Success)
        result as ApiResult.Success
        assertEquals(character.id, result.data.id)
        assertEquals(character.name, result.data.name)
        assertEquals(character.status, result.data.status)
    }

    @Test
    fun `invoke returns error when service returns error`() = runTest {
        val characterId = 1
        val errorCode = 404
        val errorMessage = "Response.error()" // this is hardcoded in Retrofit for the message
        val response = Response.error<Character>(errorCode, errorMessage.toResponseBody())

        Mockito.`when`(characterService.getCharacter(characterId))
            .thenAnswer { response }

        val result = dataSource.invoke(characterId)

        assert(result is ApiResult.Error)
        result as ApiResult.Error
        assertEquals(response.code(), result.code)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `invoke returns exception when service throws exception`() = runTest {
        val characterId = 1
        val errorCode = 404
        val errorMessage = "Location not found"
        val response = Response.error<Character>(errorCode,  errorMessage.toResponseBody())
        val exception = HttpException(response)

        Mockito.`when`(characterService.getCharacter(characterId))
            .thenThrow(exception)

        val result = dataSource.invoke(characterId)

        assert(result is ApiResult.Exception)
        result as ApiResult.Exception
        assertEquals(exception, result.e)
    }
}
