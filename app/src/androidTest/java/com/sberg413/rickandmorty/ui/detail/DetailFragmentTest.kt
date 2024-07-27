package com.sberg413.rickandmorty.ui.detail


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @Before
    public override fun setUp() {
        hiltRule.inject()
    }

    @After
    public override fun tearDown() {
    }

    @Test
    fun testDetailDisplayed() = runTest {
        (repository as TestLocationRepositoryImpl).location = TEST_LOCATION

        val fragmentArgs = Bundle().apply {
            putParcelable(DetailViewModel.KEY_CHARACTER_ID, TEST_CHARACTER)
        }

        launchFragmentInHiltContainer<DetailFragment>(fragmentArgs) {
            // Check if the action bar title is set correctly
            val activity = requireActivity() as AppCompatActivity
            assertEquals(TEST_CHARACTER.name, activity.supportActionBar?.title)
        }

        // Compose assertions
        composeTestRule.onNodeWithText(TEST_CHARACTER.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_CHARACTER.species).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_LOCATION.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_LOCATION.dimension).assertIsDisplayed()
    }

}