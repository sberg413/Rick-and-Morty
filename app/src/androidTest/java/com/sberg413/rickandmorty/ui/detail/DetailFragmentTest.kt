package com.sberg413.rickandmorty.ui.detail


import android.os.Bundle
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sberg413.rickandmorty.R
import com.sberg413.rickandmorty.TestData.TEST_CHARACTER
import com.sberg413.rickandmorty.TestData.TEST_LOCATION
import com.sberg413.rickandmorty.data.repository.LocationRepository
import com.sberg413.rickandmorty.data.repository.TestLocationRepositoryImpl
import com.sberg413.rickandmorty.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DetailFragmentTest : TestCase() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var repository: LocationRepository

    private val testNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    public override fun setUp() {
        hiltRule.inject()
    }

    @After
    public override fun tearDown() {
    }

    @Test
    @Ignore("NavHost issues. But getting rid of frags anyway")
    fun testDetailDisplayed() = runTest {
        (repository as TestLocationRepositoryImpl).location = TEST_LOCATION

        val fragmentArgs = Bundle().apply {
            putInt(DetailViewModel.KEY_CHARACTER_ID, TEST_CHARACTER.id)
        }

        launchFragmentInHiltContainer<DetailFragment>(fragmentArgs) {
            // Set the graph on the TestNavHostController
            testNavController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(this.view!!, testNavController)
        }

        // Compose assertions
        composeTestRule.onNodeWithText(TEST_CHARACTER.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_CHARACTER.species).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_LOCATION.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_LOCATION.dimension).assertIsDisplayed()
    }

}