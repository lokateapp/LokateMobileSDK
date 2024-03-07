package com.lokate.kmmsdk.domain.repository

import com.lokate.kmmsdk.data.datasource.DSResult

sealed class RepositoryResult<out T> {
    data class Success<out T>(val body: T) : RepositoryResult<T>()

    data class Error(
        val message: String,
        val errorType: Any
    ) : RepositoryResult<Nothing>()
}

fun <T> DSResult<T>.toRepositoryResult(): RepositoryResult<T> {
    return when (this) {
        is DSResult.Success -> RepositoryResult.Success(data)
        is DSResult.Error -> RepositoryResult.Error(message.orEmpty(), errorType)
    }
}