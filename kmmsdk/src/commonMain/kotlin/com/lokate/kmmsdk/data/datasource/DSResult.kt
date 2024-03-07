package com.lokate.kmmsdk.data.datasource

import com.lokate.kmmsdk.data.datasource.remote.ApiResponse

sealed class DSResult<out T> {
    data class Success<out T>(val data: T) : DSResult<T>()
    data class Error(val message: String?, val errorType: Any) : DSResult<Nothing>()
}

fun <T> ApiResponse<T, *>.toDSResult(): DSResult<T> {
    return when (this) {
        is ApiResponse.Success -> DSResult.Success(body)
        is ApiResponse.Error -> {
            // Extract relevant error information
            val errorMessage = when (this) {
                is ApiResponse.Error.HttpError<*> -> errorMessage ?: "HTTP Error"
                is ApiResponse.Error.SerializationError -> errorMessage ?: "Serialization Error"
                is ApiResponse.Error.GenericError -> errorMessage ?: "Unknown Error"
            }
            DSResult.Error(errorMessage, this)
        }
    }
}