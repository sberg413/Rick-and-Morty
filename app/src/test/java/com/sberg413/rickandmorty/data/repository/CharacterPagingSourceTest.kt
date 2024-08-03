package com.sberg413.rickandmorty.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Page
import com.sberg413.rickandmorty.api.FakeApiService
import com.sberg413.rickandmorty.data.remote.dto.CharacterDTO
import com.sberg413.rickandmorty.util.CharacterFactory
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class CharacterPagingSourceTest : TestCase() {

    private val characterFactory = CharacterFactory()

    private val mockCharacters = listOf(
        characterFactory.createMockCharacter("Rick"),
        characterFactory.createMockCharacter("Morty"),
        characterFactory.createMockCharacter("Summer")
    )

    private val fakeApiService = FakeApiService()

    private lateinit var pagingSource: CharacterRemoteMediator


    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        pagingSource = CharacterRemoteMediator( "", "")
    }


    @Test
    fun `load() returns Page with data when successful`() = runTest {
       fakeApiService.characters = mockCharacters

        val result = pagingSource.load( Refresh(null, 1, false))

        Assert.assertTrue(result is Page)
        result as Page
        Assert.assertEquals(mockCharacters.subList(0,2), result.data)
        Assert.assertEquals(null, result.prevKey)
        Assert.assertEquals(2, result.nextKey)
    }

    @Test
    fun `load() returns Error when IOException occurs`() = runTest {
        val exception = IOException("")
        fakeApiService.exception = exception

        val result = pagingSource.load(Refresh(key = 1, loadSize = 10, placeholdersEnabled = false))

        Assert.assertTrue(result is PagingSource.LoadResult.Error)
        Assert.assertEquals(exception, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `load() returns Error when HttpException occurs with code other than 404`() = runTest {
        val exception = HttpException(retrofit2.Response.error<Any>(500, "".toResponseBody()))
        fakeApiService.exception = exception

        val result = pagingSource.load(Refresh(key = 1, loadSize = 10, placeholdersEnabled = false))

        Assert.assertTrue(result is PagingSource.LoadResult.Error)
        Assert.assertEquals(exception, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `load() returns empty Page when HttpException occurs with code 404`() = runTest {
        val exception = HttpException(retrofit2.Response.error<Any>(404, "".toResponseBody()))
        fakeApiService.exception = exception

        val result = pagingSource.load(Refresh(key = 1, loadSize = 10, placeholdersEnabled = false))

        Assert.assertTrue(result is Page)
        Assert.assertEquals(emptyList<CharacterDTO>(), (result as Page).data)
        Assert.assertNull(result.prevKey)
        Assert.assertNull(result.nextKey)
    }
}