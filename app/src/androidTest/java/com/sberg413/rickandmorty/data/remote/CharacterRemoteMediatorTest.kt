package com.sberg413.rickandmorty.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sberg413.rickandmorty.TestDto
import com.sberg413.rickandmorty.data.local.db.AppDatabase
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.remote.dto.CharacterListApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediatorTest {

    @Mock
    private lateinit var apiService: CharacterService

    private lateinit var database: AppDatabase

    private lateinit var mediator: CharacterRemoteMediator

    private val pagerConfig = PagingConfig(20)

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        MockitoAnnotations.openMocks(this)

        mediator = CharacterRemoteMediator(null, null, apiService, database)
    }

    @After
    fun tearDown() = runTest {
        database.close()
    }

    @Test
    fun refreshLoadsDataFromNetworkAndInsertsIntoDatabase() = runTest {
        val apiResponse = createTestApiResponse(page = 1, nextPage = "https://example.com/?page=2")
        Mockito.`when`(apiService.getCharacterList(1, null, null)).thenReturn(apiResponse)

        val result = mediator.load(LoadType.REFRESH, PagingState(emptyList(), null, pagerConfig, 10))

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertEquals(apiResponse.results.size, database.characterDao().getAllCharacters().size)
    }

    @Test
    fun appendLoadsNextPageFromNetworkAndInsertsIntoDatabase() = runTest {
        val initialApiResponse = createTestApiResponse(page = 1, nextPage = "https://example.com/?page=2")
        Mockito.`when`(apiService.getCharacterList(1, null, null)).thenReturn(initialApiResponse)
        mediator.load(LoadType.REFRESH, PagingState(emptyList(), null, pagerConfig, 10))

        val nextApiResponse = createTestApiResponse(page = 2, nextPage = null)
        Mockito.`when`(apiService.getCharacterList(2, null, null)).thenReturn(nextApiResponse)

        val result = mediator.load(LoadType.APPEND, PagingState(emptyList(), null, pagerConfig, 10))

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertEquals(
            initialApiResponse.results.size + nextApiResponse.results.size,
            database.characterDao().getAllCharacters().size
        )
    }

    @Test
    fun prependReturnsSuccessWithEndOfPaginationReachedTrue() = runTest {
        val result = mediator.load(LoadType.PREPEND, PagingState(emptyList(), null, pagerConfig, 10))

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun loadReturnsErrorOnIOException() = runTest {
        val exception = IOException("Network error")
        Mockito.`when`(apiService.getCharacterList(1, null, null)).thenAnswer{
            throw exception
        }

        val result = mediator.load(LoadType.REFRESH, PagingState(emptyList(), null, pagerConfig, 10))

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
    }

    @Test
    fun loadReturnsErrorOnHttpException() = runTest {
        val exception = HttpException(Response.error<Any>(500, "".toResponseBody()))
        Mockito.`when`(apiService.getCharacterList(1, null, null)).thenThrow(exception)

        val result = mediator.load(LoadType.REFRESH, PagingState(emptyList(), null, pagerConfig, 10))

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
    }

    // Helper function to create test API response
    private fun createTestApiResponse(page: Int, nextPage: String?): CharacterListApi {

        val characters = if (page == 1)
            listOf(TestDto.testCharacterDTO1)
        else
            listOf(TestDto.testCharacterDTO2)

        return CharacterListApi(
            info = CharacterListApi.Info(
                count = characters.size,
                pages = if (nextPage == null) page else page + 1,
                next = nextPage,
                prev = if (page == 1) null else "https://example.com/?page=${page - 1}"
            ),
            results = characters
        )
    }
}