package com.sberg413.rickandmorty.data.api

import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    data class Error(val code: Int, val message: String): ApiResult<Nothing>()
    data class Exception(val e: Throwable): ApiResult<Nothing>()
}


suspend fun <T: Any> handleApiResponse( execute: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ApiResult.Success(body)
        } else {
            ApiResult.Error(response.code(), response.message())
        }
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}