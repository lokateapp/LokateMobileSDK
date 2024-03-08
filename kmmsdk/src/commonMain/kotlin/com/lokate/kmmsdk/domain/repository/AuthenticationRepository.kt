package com.lokate.kmmsdk.domain.repository

interface AuthenticationRepository {
    suspend fun getAppToken(): RepositoryResult<String>

    suspend fun getAppToken(appToken: String): RepositoryResult<Boolean>

    suspend fun setAppToken(appToken: String): RepositoryResult<Boolean>

    suspend fun setUserID(userId: String): RepositoryResult<Boolean>
}
