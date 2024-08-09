package com.sberg413.rickandmorty.ui.detail

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.robolectric.shadows.ShadowToast

@RunWith(AndroidJUnit4::class)
class DetailRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Mock
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

    }

    @After
    fun tearDown() {
    }

    @Test
    fun testDetailDisplayed() {
        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(
                CharacterDetailUiState.Success(TestData.TEST_CHARACTER, TestData.TEST_LOCATION)
            )
        )

        composeTestRule.setContent {
            CharacterDetailDescription(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("CharacterName").assertExists()
        composeTestRule.onNodeWithTag("Species").assertExists()
        composeTestRule.onNodeWithTag("Location").assertExists()
        composeTestRule.onNodeWithTag("Dimension").assertExists()
    }

    @Test
    fun testLoadingState() {
        whenever(viewModel.uiState).thenReturn( MutableStateFlow(CharacterDetailUiState.Loading))

        composeTestRule.setContent {
            AppTheme {
                CharacterDetailDescription(viewModel = viewModel)
            }
        }

        // Verify LoadingScreen is displayed
        composeTestRule.onNodeWithTag("LoadingScreen").assertExists()
    }

    @Test
    fun testErrorState() {
        val errorMessage = "Test Error"
        whenever(viewModel.uiState).thenReturn(MutableStateFlow(CharacterDetailUiState.Error(errorMessage)))

        composeTestRule.setContent {
            AppTheme {
                CharacterDetailDescription(viewModel = viewModel)
            }
        }

        // Assert that error toast is displayed
        val latestToast = ShadowToast.getTextOfLatestToast()
        assertEquals("ERROR: $errorMessage", latestToast)
    }

    @Test
    @Ignore("Ignore until compose fully implemented")
    fun testActionBarTitle() {
        whenever(viewModel.uiState).thenReturn( MutableStateFlow(CharacterDetailUiState.Success(TestData.TEST_CHARACTER, TestData.TEST_LOCATION)))

        composeTestRule.setContent {
            AppTheme {
                CharacterDetailDescription(viewModel = viewModel)
            }
        }

        // Wait for LaunchedEffect to execute
        composeTestRule.waitForIdle()

        val activity = composeTestRule.activity
        assertEquals(TestData.TEST_CHARACTER.name, activity.title)
    }
}