package com.lokate.kmmsdk.data.datasource

sealed class DSResult{
    data class Success<T>(val data: T): DSResult()
    data class Error<E>(val message: String, val errorType: E): DSResult()
}