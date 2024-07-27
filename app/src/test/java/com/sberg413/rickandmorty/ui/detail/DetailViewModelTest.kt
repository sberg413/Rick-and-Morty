package com.sberg413.rickandmorty.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.model.CharacterDetail
import com.sberg413.rickandmorty.data.repository.LocationRepository
import com.sberg413.rickandmorty.domain.GetCharacterDetailUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.internal.stubbing.answers.AnswersWithDelay
import org.mockito.internal.stubbing.answers.Returns

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val coroutineRule = MainCoroutineRule(testDispatcher)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private var characterDetailUseCase: GetCharacterDetailUseCase = mock()


    private val characterDetail = CharacterDetail(TestData.TEST_CHARACTER, TestData.TEST_LOCATION)
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailViewModel



    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        val character = TestData.TEST_CHARACTER
        savedStateHandle = SavedStateHandle(mapOf(DetailViewModel.KEY_CHARACTER_ID to character.id))
    }

    @Test
    fun testLoadingState() = runTest {
        // Given
        `when`(characterDetailUseCase.invoke(anyInt())).thenAnswer(
            AnswersWithDelay(1000,
                Returns(ApiResult.Success(characterDetail))))

        // When
        viewModel = DetailViewModel(characterDetailUseCase, savedStateHandle)

        // Then
        assertEquals(CharacterDetailUiState.Loading, viewModel.uiState.first())
    }

    @Test
    fun testSuccessState() = runTest {
        // Given
        val id = savedStateHandle.get<Int>(DetailViewModel.KEY_CHARACTER_ID)!!
        `when`(characterDetailUseCase.invoke(id))
            .thenReturn(ApiResult.Success(characterDetail))

        // When
        viewModel = DetailViewModel(characterDetailUseCase, savedStateHandle)
        advanceUntilIdle() // Wait for all coroutines to finish

        // Then
        assertEquals(
            CharacterDetailUiState.Success(characterDetail.character, characterDetail.location),
            viewModel.uiState.first()
        )
    }
}
