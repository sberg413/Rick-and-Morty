package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingSource
import com.sberg413.rickandmorty.api.MockApiService
import com.sberg413.rickandmorty.util.CharacterFactory
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CharacterPagingSourceTest : TestCase() {

    private val characterFactory = CharacterFactory()

    private val mockCharacters = listOf(
        characterFactory.createMockCharacter("Rick"),
        characterFactory.createMockCharacter("Morty"),
        characterFactory.createMockCharacter("Summer")
    )

    private val mockApiService = MockApiService().apply {
        mockCharacters.forEach { addCharacterResult(it) }
    }


    @Before
    public override fun setUp() {
    }

    @After
    public override fun tearDown() {
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pageKeyedCharacterPagingSourceLoad()  = runTest {

        val pagingSource = CharacterPagingSource(mockApiService, "", "")

        assertEquals(
            PagingSource.LoadResult.Page(
                data = mockCharacters.subList(0,2),
                prevKey = null,
                nextKey = 2
            ),
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    null,
                    1,
                    false))
        )
    }

    @Test
    fun getRefreshKey() {
    }
}