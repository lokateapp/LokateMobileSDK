package com.lokate.kmmsdk.data.repository

import com.lokate.kmmsdk.data.datasource.local.authentication.AuthenticationLocalDS
import com.lokate.kmmsdk.data.datasource.remote.authentication.AuthenticationRemoteDS
import com.lokate.kmmsdk.domain.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.repository.RepositoryResult
import com.lokate.kmmsdk.domain.repository.toRepositoryResult

class AuthenticationRepositoryImpl(
    private val remoteDS: AuthenticationRemoteDS,
    private val localDS: AuthenticationLocalDS,
) : AuthenticationRepository {
    override suspend fun getAppToken(): RepositoryResult<String> =
        localDS.getAppToken().toRepositoryResult()

    private suspend fun getLocalAuthentication(appToken: String): RepositoryResult<Boolean> =
        when (val result = localDS.getAppAuthentication(appToken).toRepositoryResult()) {
            is RepositoryResult.Success -> {
                if (result.body.valid) {
                    RepositoryResult.Success(true)
                } else {
                    RepositoryResult.Error("Authentication failed!", Any())
                }
            }

            is RepositoryResult.Error -> RepositoryResult.Error(result.message, result.errorType)
        }

    @Suppress("NestedBlockDepth")
    private suspend fun getRemoteAuthentication(appToken: String): RepositoryResult<Boolean> {
        return when (
            val remoteAuthentication =
                remoteDS.getAppAuthentication(appToken).toRepositoryResult()
        ) {
            is RepositoryResult.Error ->
                RepositoryResult.Error(
                    remoteAuthentication.message,
                    remoteAuthentication.errorType,
                )

            is RepositoryResult.Success -> {
                when (remoteAuthentication.body.valid) {
                    true -> setAppToken(appToken)
                    else -> RepositoryResult.Error("Authentication failed!", Any())
                }
            }
        }
    }

    override suspend fun getAppToken(appToken: String): RepositoryResult<Boolean> {
        return when {
            getLocalAuthentication(appToken) is RepositoryResult.Success ->
                getLocalAuthentication(appToken)

            getRemoteAuthentication(appToken) is RepositoryResult.Success ->
                getRemoteAuthentication(appToken)

            else -> RepositoryResult.Error("Authentication failed!", Any())
        }
    }

    override suspend fun setAppToken(appToken: String): RepositoryResult<Boolean> =
        localDS.setAuthentication(appToken).toRepositoryResult()

    override suspend fun setUserID(userId: String): RepositoryResult<Boolean> =
        localDS.setUserId(userId).toRepositoryResult()
}
