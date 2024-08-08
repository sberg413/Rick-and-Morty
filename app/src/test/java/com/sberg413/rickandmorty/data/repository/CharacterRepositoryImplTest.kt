package com.sberg413.rickandmorty.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator.MediatorResult
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestDto
import com.sberg413.rickandmorty.TestEntity
import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.local.dao.CharacterDao
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.remote.CharacterRemoteDataSource
import com.sberg413.rickandmorty.data.remote.CharacterRemoteMediator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CharacterRepositoryImplTest {

    @Mock
    private lateinit var characterRemoteMediator: CharacterRemoteMediator

    @Mock
    private lateinit var characterRemoteDataSource: CharacterRemoteDataSource

    @Mock
    private lateinit var characterDao: CharacterDao

    private lateinit var characterRepository: CharacterRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val coroutineRule = MainCoroutineRule(testDispatcher)

    @Before
    fun setUp() {
        characterRepository = CharacterRepositoryImpl(
            characterRemoteMediator,
            characterRemoteDataSource,
            characterDao,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun testGetCharacterList() = runTest {
        val characterEntities = listOf(
            TestEntity.testCharacterEntity
        )
        val pagingSourceFactory = characterEntities.asPagingSourceFactory()
        val pagingSource = pagingSourceFactory()
        whenever(characterDao.getPagingSource(anyString(), anyString())).thenReturn(pagingSource)

        val mockMediatorResult = MediatorResult.Success(endOfPaginationReached = true)
        whenever(characterRemoteMediator.load(any(), any())).thenReturn(mockMediatorResult)

        val characterList = mutableListOf<Character>()
        val job = launch(testDispatcher) {
            characterRepository.getCharacterList("Rick", "Alive")
                .asSnapshot().toCollection(characterList)
        }
        advanceUntilIdle()

        assertEquals(characterList[0].id, characterList[0].id)
        assertEquals(characterList[0].name, characterList[0].name)
        assertEquals(characterList[0].status, characterList[0].status)
        assertEquals(characterList[0].species, characterList[0].species)

        job.cancel()

    }

    @Test
    fun `test getCharacter success`() = runTest {
        val character = TestDto.testCharacterDTO1
        val apiResult = ApiResult.Success(character)

        whenever(characterRemoteDataSource.invoke(anyInt())).thenReturn(apiResult)

        val result = characterRepository.getCharacter(1)

        assert(result is ApiResult.Success)
        assert((result as ApiResult.Success).data.id == 1)
        assert(result.data.name == "Rick Sanchez")
    }

    @Test
    fun `test getCharacter error`() = runTest {
        val apiResult = ApiResult.Error(404, "Not Found")

        whenever(characterRemoteDataSource.invoke(anyInt())).thenReturn(apiResult)

        val result = characterRepository.getCharacter(1)

        assert(result is ApiResult.Error)
        assert((result as ApiResult.Error).code == 404)
        assert(result.message == "Not Found")
    }

    @Test
    fun `test getCharacter exception`() = runTest {
        val apiResult = ApiResult.Exception(Exception("Network Error"))

        whenever(characterRemoteDataSource.invoke(anyInt())).thenReturn(apiResult)

        val result = characterRepository.getCharacter(1)

        assert(result is ApiResult.Exception)
        assert((result as ApiResult.Exception).e.message == "Network Error")
    }
}
