package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestData.readJsonFile
import com.sberg413.rickandmorty.data.local.dao.CharacterDao
import com.sberg413.rickandmorty.data.local.db.AppDatabase
import com.sberg413.rickandmorty.data.local.entity.CharacterEntity
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.remote.CharacterRemoteDataSource
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.remote.dto.CharacterListApi
import com.sberg413.rickandmorty.util.collectDataForTest
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CharacterRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule(testDispatcher)

    @Mock
    private lateinit var mockApiServices: CharacterService
    @Mock
    private lateinit var mockRemoteDataSource: CharacterRemoteDataSource
    @Mock
    private lateinit var mockAppDatabase: AppDatabase
    @Mock
    private lateinit var mockCharacterDao: CharacterDao

    private lateinit var characterRepositoryImpl: CharacterRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(mockAppDatabase.characterDao()).thenReturn(mockCharacterDao)
        characterRepositoryImpl = CharacterRepositoryImpl(mockApiServices, mockRemoteDataSource, mockAppDatabase, testDispatcher)
    }

    @After
    fun tearDown() {
    }

    @Test
    @Ignore("May need to pass in the RemoteDataSource with DI!")
    fun getCharacterList() = runTest {
        val testEntity = createTestCharacterEntity()
        val pagingSourceLoadResult = PagingSource.LoadResult.Page<Int,CharacterEntity>(
            data = listOf(testEntity),
            prevKey = null,
            nextKey = null
        )
        val fakePagingSource = FakePagingSource().apply {
            this.loadResult = pagingSourceLoadResult
        }

        `when`(
            mockAppDatabase.characterDao().getPagingSource("", "")
        ).thenReturn(fakePagingSource)


        val values = mutableListOf<PagingData<Character>>()
        val collectJob = launch(testScheduler) {
            characterRepositoryImpl.getCharacterList(null, null).toList(values)
        }
        testDispatcher.scheduler.advanceUntilIdle()
        val list = values[0].collectDataForTest(testDispatcher)

        // Verify
        assertEquals(1, list.size)
        assertEquals(testEntity.id, list[0].id)
        assertEquals(testEntity.name, list[0].name)
        assertEquals(testEntity.gender, list[0].gender)
        assertEquals(testEntity.image, list[0].image)

        collectJob.cancel()
    }

    private fun createTestCharacterEntity(): CharacterEntity {
        return CharacterEntity(
            id = 1,
            created = "2022-01-01T00:00:00.000Z",
            gender = "Male",
            image = "https://example.com/image.jpg",
            name = "Test Character",
            species = "Human",
            status = "Alive",
            type = "",
            url = "https://example.com/character/1",
            origin = CharacterEntity.Origin(
                name = "Earth",
                url = "https://example.com/location/earth"
            ),
            location = CharacterEntity.Location(
                name = "Earth",
                url = "https://example.com/location/earth"
            ),
            episode = listOf("https://example.com/episode/1", "https://example.com/episode/2")
        )
    }

}