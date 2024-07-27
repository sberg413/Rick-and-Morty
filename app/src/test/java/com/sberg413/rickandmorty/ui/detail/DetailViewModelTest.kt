package com.sberg413.rickandmorty.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.repository.LocationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
    private var locationRepository: LocationRepository = mock()


    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        val character = TestData.TEST_CHARACTER
        savedStateHandle = SavedStateHandle(mapOf(DetailViewModel.KEY_CHARACTER to character))
    }

    @Test
    fun testLoadingState() = runTest {
        // Given
        `when`(locationRepository.getLocation(anyString())).thenAnswer(
            AnswersWithDelay(1000,
                Returns(ApiResult.Success(TestData.TEST_LOCATION))))

        // When
        viewModel = DetailViewModel(locationRepository, savedStateHandle)

        // Then
        assertEquals(CharacterDetailUiState.Loading, viewModel.uiState.first())
    }

    @Test
    fun testSuccessState() = runTest {
        // Given
        val location = TestData.TEST_LOCATION
        val character = savedStateHandle.get<Character>(DetailViewModel.KEY_CHARACTER)!!
        `when`(locationRepository.getLocation(character.locationId!!))
            .thenReturn(ApiResult.Success(location))

        // When
        viewModel = DetailViewModel(locationRepository, savedStateHandle)
        advanceUntilIdle() // Wait for all coroutines to finish

        // Then
        assertEquals(CharacterDetailUiState.Success(character, location), viewModel.uiState.first())
    }
}
