package com.lokate.kmmsdk.domain.model.repository

sealed class RepositoryResult<out T> {
    data class Success<T>(val body: T) : RepositoryResult<T>()

    data class Error(
        val message: String?,
        val errorMessage: String?
    ) : RepositoryResult<Nothing>()
}