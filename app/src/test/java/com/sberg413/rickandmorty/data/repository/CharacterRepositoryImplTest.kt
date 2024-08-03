package com.sberg413.rickandmorty.data.repository

import androidx.paging.testing.asSnapshot
import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestData.readJsonFile
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.remote.dto.CharacterListApi
import com.sberg413.rickandmorty.data.remote.CharacterRemoteDataSource
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class CharacterRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule(testDispatcher)

    private val mockApiServices: CharacterService = mock()
    private val mockRemoteDataSource: CharacterRemoteDataSource = mock()

    private lateinit var characterRepositoryImpl: CharacterRepositoryImpl

    @Before
    fun setUp() {
        characterRepositoryImpl = CharacterRepositoryImpl(mockApiServices, mockRemoteDataSource, testDispatcher)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getCharacterList() = runTest {
        // When
        val jsonString = readJsonFile("characters_response.json")
        val moshiAdapter = Moshi.Builder().build().adapter(CharacterListApi::class.java)
        val characterListApi = moshiAdapter.fromJson(jsonString)
       `when`(mockApiServices.getCharacterList(anyInt(), any(), any())).thenReturn(characterListApi)

        // Then
        val list = characterRepositoryImpl.getCharacterList(null, null).asSnapshot()

        // Verify
        assertEquals(40, list.size)
        assertEquals("Rick Sanchez", list[0].name)
        assertEquals("Male", list[0].gender)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", list[0].image)
        assertEquals(1, list[0].id)
    }
}