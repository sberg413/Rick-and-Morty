package com.sberg413.rickandmorty.data.remote

import com.sberg413.rickandmorty.TestData
import com.sberg413.rickandmorty.data.remote.api.ApiResult
import com.sberg413.rickandmorty.data.remote.api.LocationService
import com.sberg413.rickandmorty.data.model.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class LocationRemoteDataSourceTest {

    @Mock
    private lateinit var locationService: LocationService

    private lateinit var dataSource: LocationRemoteDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dataSource = LocationRemoteDataSource(locationService)
    }

    @Test
    fun `invoke returns location on success`() = runTest {
        val locationId = "123"
        val location = TestData.TEST_LOCATION

        Mockito.`when`(locationService.getLocation(locationId))
            .thenReturn(Response.success(location))

        val result = dataSource.invoke(locationId)

        assert(result is ApiResult.Success)
        result as ApiResult.Success
        assertEquals(location, result.data)
    }

    @Test
    fun `invoke returns error when service returns error`() = runTest {
        val locationId = "123"
        val errorCode = 404
        val errorMessage = "Response.error()" // this is hardcoded in Retrofit for the message
        val response = Response.error<Location>(errorCode, errorMessage.toResponseBody())

        Mockito.`when`(locationService.getLocation(locationId))
            .thenAnswer { response }

        val result = dataSource.invoke(locationId)

        assert(result is ApiResult.Error)
        result as ApiResult.Error
        assertEquals(response.code(), result.code)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `invoke returns exception when service throws exception`() = runTest {
        val locationId = "123"
        val errorCode = 404
        val errorMessage = "Location not found"
        val response = Response.error<Location>(errorCode,  errorMessage.toResponseBody())
        val exception = HttpException(response)

        Mockito.`when`(locationService.getLocation(locationId))
            .thenThrow(exception)

        val result = dataSource.invoke(locationId)

        assert(result is ApiResult.Exception)
        result as ApiResult.Exception
        assertEquals(exception, result.e)
    }
}
