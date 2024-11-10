package com.sberg413.rickandmorty.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.data.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever


@OptIn(ExperimentalSharedTransitionApi::class)
@RunWith(AndroidJUnit4::class)
class MainRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Mock
    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var navController: NavController

    @Mock
    private lateinit var character: Character

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(viewModel.uiState).thenReturn(MutableStateFlow(MainUiState()).asStateFlow())
        whenever(viewModel.characterClicked).thenReturn( flowOf(character) )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testMainCharacterListScreen_showInitialLoadingState() {
        val combinedLoadStates = CombinedLoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false),
            source = LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.NotLoading(endOfPaginationReached = false)
            ),
            mediator = null
        )
        val pagingData = flowOf(
            PagingData.empty<Character>(
                sourceLoadStates = combinedLoadStates.source
            )
        )

        whenever(viewModel.listData).thenReturn( pagingData )

        // Set up the composable
        initializeMainListScreenContent()

        composeTestRule.onNodeWithTag("TopAppBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CharacterSearchInput").assertExists()
        composeTestRule.onNodeWithTag("LoadingScreen").assertIsDisplayed()

    }

    @Test
    fun testMainCharacterListScreen_showErrorSnackbarOnError() {
        val errorLoadState = LoadState.Error(RuntimeException("Test error"))
        val combinedLoadStates = CombinedLoadStates(
            refresh = errorLoadState,
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false),
            source = LoadStates(
                refresh = errorLoadState,
                prepend = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.NotLoading(endOfPaginationReached = false)
            ),
            mediator = null
        )

        val pagingData = flowOf(
            PagingData.empty<Character>(
                sourceLoadStates = combinedLoadStates.source
            )
        )

        whenever(viewModel.listData).thenReturn( pagingData )

        // Set up the composable
        initializeMainListScreenContent()

        // Verify that the Snackbar is shown with the correct message
        composeTestRule.onNodeWithText("ERROR: Test error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EmptyResultsView").assertIsDisplayed()
    }

    @Test
    fun testMainCharacterListScreen_showCharacterList() {
        val combinedLoadStates = CombinedLoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = true),
            source = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.NotLoading(endOfPaginationReached = false)
            ),
            mediator = null
        )

        val pagingData = flowOf(
            PagingData.from(
                data = listOf(TestData.TEST_CHARACTER, TestData.TEST_CHARACTER_2),
                sourceLoadStates = combinedLoadStates.source
            )
        )

        whenever(viewModel.listData).thenReturn( pagingData )

        // Set up the composable
        initializeMainListScreenContent()

        // Verify that the Snackbar is shown with the correct message
        composeTestRule.onNodeWithTag("LoadingScreen").assertDoesNotExist()
        composeTestRule.onNodeWithTag("EmptyResultsView").assertDoesNotExist()
        composeTestRule.onNodeWithTag("CharacterList").assertIsDisplayed()
    }

    private fun initializeMainListScreenContent() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedContent(true, label = "test") { targetState ->
                    if (targetState) {
                        MainCharacterListScreen(
                            viewModel = viewModel,
                            navController = navController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this
                        )
                    }
                }
            }
        }

    }

}