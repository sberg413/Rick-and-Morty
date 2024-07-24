package com.sberg413.rickandmorty.ui.main

import androidx.paging.PagingData
import app.cash.turbine.test
import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.TestData.TEST_CHARACTER
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.repository.CharacterRepository
import com.sberg413.rickandmorty.ui.CharacterFilter
import com.sberg413.rickandmorty.ui.NoSearchFilter
import com.sberg413.rickandmorty.ui.NoStatusFilter
import com.sberg413.rickandmorty.ui.SearchFilter
import com.sberg413.rickandmorty.ui.StatusFilter
import com.sberg413.rickandmorty.util.collectDataForTest
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest : TestCase() {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val coroutineRule = MainCoroutineRule(testDispatcher)

    @Mock
    lateinit var characterRepository: CharacterRepository


    val characterList = listOf(TestData.TEST_CHARACTER, TestData.TEST_CHARACTER_2)
    val pagingData = PagingData.from(characterList)

    lateinit var viewModel: MainViewModel

    @Before
    fun before() = runTest {
        MockitoAnnotations.openMocks(this)

        val dataResponse = flow { emit(pagingData) }
        `when`(characterRepository.getCharacterList(any(), any())).thenReturn(
            dataResponse
        )

        viewModel = MainViewModel(characterRepository)
    }

    @Test
    fun `setStatusFilter updates filter correctly`() = runTest {
        val expectedFilter = CharacterFilter(StatusFilter("Alive"), NoSearchFilter)

        viewModel.setStatusFilter("Alive")
        advanceUntilIdle()

        Assert.assertEquals(expectedFilter, viewModel._characterFilterFlow.value)
    }

    @Test
    fun `setSearchFilter updates filter correctly`() = runTest {
        val expectedFilter = CharacterFilter(NoStatusFilter, SearchFilter("Rick"))

        viewModel.setSearchFilter("Rick")
        advanceUntilIdle()

        Assert.assertEquals(expectedFilter, viewModel._characterFilterFlow.value)
    }

    @Test
    fun listDataInitialResponse() = runTest {

        val values = mutableListOf<PagingData<Character>>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.listData.toList(values)
        }

        assertEquals(characterList, values[1].collectDataForTest(testDispatcher))
        verify(characterRepository, times(1)).getCharacterList(null,null)

        collectJob.cancel()
    }

    @Test
    fun listDataAfterSetSearchFilter() = runTest {

        val values = mutableListOf<PagingData<Character>>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.listData.toList(values)
        }
        viewModel.setSearchFilter(SEARCH_MORTY)

        assertEquals(characterList, values[2].collectDataForTest(testDispatcher))
        verify(characterRepository, times(1)).getCharacterList(SEARCH_MORTY,null)

        collectJob.cancel()
    }

    @Test
    fun listDataAfterSetStatusFilter() = runTest {

        val values = mutableListOf<PagingData<Character>>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.listData.toList(values)
        }
        viewModel.setStatusFilter(STATUS_ALIVE)

        assertEquals(characterList, values[2].collectDataForTest(testDispatcher))
        verify(characterRepository, times(1)).getCharacterList(null, STATUS_ALIVE)

        collectJob.cancel()
    }

    @Test
    fun testUpdateStateAfterUiClick() = runTest {

        viewModel.characterClicked.test {
            assertEquals(null, awaitItem()) // Initial value is null

            viewModel.updateStateWithCharacterClicked(TEST_CHARACTER)

            assertEquals(TEST_CHARACTER, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        const val SEARCH_MORTY = "morty"
        const val STATUS_ALIVE = "alive"
    }
}