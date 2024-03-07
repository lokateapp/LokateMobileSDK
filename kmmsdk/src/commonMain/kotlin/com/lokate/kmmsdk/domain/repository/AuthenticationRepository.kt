package com.lokate.kmmsdk.domain.repository

interface AuthenticationRepository {
    suspend fun getAppToken(): RepositoryResult<String>

    suspend fun getAuthenticate(appToken: String): RepositoryResult<Boolean>

    suspend fun setUserID(userId: String): RepositoryResult<Boolean>
}
