package com.sberg413.rickandmorty.data.repository

import com.sberg413.rickandmorty.MainCoroutineRule
import com.sberg413.rickandmorty.TestData.TEST_LOCATION
import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.remote.LocationRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LocationRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule(testDispatcher)

    private val locationRemoteDataSource: LocationRemoteDataSource = mock()

    private lateinit var locationRepository: LocationRepositoryImpl

    @Before
    fun setUp() {
        locationRepository = LocationRepositoryImpl(locationRemoteDataSource, testDispatcher)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getLocation() = runTest {
        val id = "20"
        `when`(locationRemoteDataSource.invoke(id)).thenReturn(ApiResult.Success(TEST_LOCATION))

        val result = locationRepository.getLocation(id)

        assertEquals(ApiResult.Success(TEST_LOCATION), result)
    }
}