package com.sberg413.rickandmorty.ui.main

import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sberg413.rickandmorty.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import junit.framework.TestCase

import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainFragmentTest : TestCase() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    public override fun setUp() {
    }

    @After
    public override fun tearDown() {
    }

    @Test
    @Ignore("NavHost issues. But getting rid of frags anyway")
    fun testMainFragment() {
        // The "fragmentArgs" argument is optional.
        val fragmentArgs = bundleOf()
        val scenario =
            launchFragmentInHiltContainer<MainFragment>(fragmentArgs)
    }

}