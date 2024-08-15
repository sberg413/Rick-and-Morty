package com.sberg413.rickandmorty.data

import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    data class Error(val code: Int, val message: String): ApiResult<Nothing>()
    data class Exception(val e: Throwable): ApiResult<Nothing>()
}

/**
 * Handles the API response by executing the provided suspend function and applying a mapper function
 * to transform the response body. The function wraps the result in an {@link ApiResult} object, which
 * can represent a successful result, an error response, or an exception.
 *
 * @param <T> The type of the response body returned by the API call.
 * @param <R> The type of the data after applying the mapper function.
 * @param mapper A function that transforms the API response body from type {@code T} to type {@code R}.
 *               The default is a no-op mapper that simply casts the response body to type {@code R}.
 * @param execute A suspend function that makes the API call and returns a {@link Response} containing
 *                a body of type {@code T}.
 * @return An {@link ApiResult} object containing the transformed data if the API call is successful,
 *         or an error/exception result if the call fails.
 * @throws ClassCastException If the default no-op mapper is used and the types {@code T} and {@code R}
 *                            are not compatible.
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T: Any, R: Any> handleApiResponse(
    mapper: (T) -> R = { it as R }, // Default no mapping
    execute: suspend () -> Response<T>
): ApiResult<R> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val mappedData = mapper(body)
            ApiResult.Success(mappedData)
        } else {
            ApiResult.Error(response.code(), response.message())
        }
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}
