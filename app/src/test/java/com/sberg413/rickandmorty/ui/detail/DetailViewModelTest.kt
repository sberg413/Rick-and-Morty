package com.sberg413.rickandmorty.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.domain.model.CharacterDetail
import com.sberg413.rickandmorty.domain.usecase.GetCharacterDetailUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @Mock
    private lateinit var getCharacterDetailUseCase: GetCharacterDetailUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: DetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val coroutineRule = MainCoroutineRule(testDispatcher)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle()
    }


    @Test
    fun `refresh() updates uiState to Loading and then Success on successful API call`() = runTest {
        val characterId = 1
        savedStateHandle[DetailViewModel.KEY_CHARACTER_ID] = characterId
        val expectedCharacter = TestData.TEST_CHARACTER
        val expectedLocation = TestData.TEST_LOCATION
        Mockito.`when`(getCharacterDetailUseCase.invoke(characterId)).thenReturn(
            ApiResult.Success(
                CharacterDetail(
                    expectedCharacter,
                    expectedLocation
                )
            )
        )

        viewModel = DetailViewModel(getCharacterDetailUseCase, savedStateHandle)
        assertEquals(CharacterDetailUiState.Loading, viewModel.uiState.value) // Initially Loading

        // Wait for the coroutine to finish and update uiState
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(
            CharacterDetailUiState.Success(expectedCharacter, expectedLocation),
            viewModel.uiState.value
        )
    }

    @Test
    fun `refresh() updates uiState to Loading and then Error on API error`() = runTest {
        val characterId = 1
        savedStateHandle[DetailViewModel.KEY_CHARACTER_ID] = characterId
        val errorMessage = "Error fetching character details"
        val errorCode = 404
        Mockito.`when`(getCharacterDetailUseCase.invoke(characterId)).thenReturn(
            ApiResult.Error(errorCode, errorMessage)
        )

        viewModel = DetailViewModel(getCharacterDetailUseCase, savedStateHandle)
        assertEquals(CharacterDetailUiState.Loading, viewModel.uiState.value) // Initially Loading

        // Wait for the coroutine to finish and update uiState
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            CharacterDetailUiState.Error(errorMessage),
            viewModel.uiState.value
        )
    }

    @Test
    fun `refresh() updates uiState to Loading and then Error on API exception`() = runTest {
        val characterId = 1
        savedStateHandle[DetailViewModel.KEY_CHARACTER_ID] = characterId
        val exception = Exception("Network error")
        Mockito.`when`(getCharacterDetailUseCase.invoke(characterId)).thenReturn(
            ApiResult.Exception(exception)
        )

        viewModel = DetailViewModel(getCharacterDetailUseCase, savedStateHandle)
        assertEquals(CharacterDetailUiState.Loading, viewModel.uiState.value) // Initially Loading

        // Wait for the coroutine to finish and update uiState
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(
            CharacterDetailUiState.Error(exception.message!!),
            viewModel.uiState.value
        )
    }
}