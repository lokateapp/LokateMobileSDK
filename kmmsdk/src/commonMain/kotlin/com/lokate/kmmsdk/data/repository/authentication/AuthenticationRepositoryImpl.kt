package com.lokate.kmmsdk.data.repository.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.local.authentication.AuthenticationLocalDS
import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import com.lokate.kmmsdk.data.datasource.remote.authentication.AuthenticationRemoteDS
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationResponse
import com.lokate.kmmsdk.domain.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.repository.RepositoryResult
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import io.ktor.http.HttpStatusCode

class AuthenticationRepositoryImpl(
    private val remoteDS: AuthenticationRemoteDS,
    private val localDS: AuthenticationLocalDS,
) : AuthenticationRepository {
    override suspend fun getAppToken(): RepositoryResult<String> {
        return localDS.getAppToken().let {
            when (it) {
                is DSResult.Error<*> -> RepositoryResult.Error(it.message, it.errorType.toString())
                is DSResult.Success<*> ->
                    if (it.data is String) {
                        RepositoryResult.Success(it.data)
                    } else {
                        RepositoryResult.Error(EMPTY_STRING, EMPTY_STRING)
                    }
            }
        }
    }

    private suspend fun getLocalAuthentication(appToken: String): RepositoryResult<Boolean> {
        return localDS.getAppAuthentication(appToken).let {
            when (it) {
                is DSResult.Success<*> -> {
                    if (it.data is AuthenticationResponse && it.data.valid) {
                        return RepositoryResult.Success(true)
                    }
                }
                else -> {}
            }
            RepositoryResult.Error("Authentication failed!", "Unknown Error")
        }
    }

    @Suppress("NestedBlockDepth")
    private suspend fun getRemoteAuthentication(appToken: String): RepositoryResult<Boolean> {
        return remoteDS.getAppAuthentication(appToken).let {
            when (it) {
                is DSResult.Error<*> -> {
                    when (it.errorType) {
                        is ApiResponse.Error.HttpError<*> -> {
                            if (it.errorType.code == HttpStatusCode.Unauthorized.value) {
                                RepositoryResult.Error(
                                    "Authentication failed!",
                                    "Invalid app token",
                                )
                            }
                        }

                        else ->
                            RepositoryResult.Error(
                                "Unknown Error!",
                                "Unknown Error",
                            )
                    }
                }

                is DSResult.Success<*> -> {
                    if (it.data is AuthenticationResponse) {
                        if (it.data.valid) {
                            setAuthentication(appToken)
                        }
                        RepositoryResult.Success(true)
                    }
                }
            }
            RepositoryResult.Error("Unknown Error", "Unknown Error")
        }
    }

    override suspend fun getAuthenticate(appToken: String): RepositoryResult<Boolean> {
        return when {
            getLocalAuthentication(appToken) is RepositoryResult.Success -> getLocalAuthentication(appToken)
            getRemoteAuthentication(appToken) is RepositoryResult.Success -> getRemoteAuthentication(appToken)
            else -> RepositoryResult.Error("Authentication failed!", "Unknown Error")
        }
    }

    private suspend fun setAuthentication(appToken: String): RepositoryResult<Boolean> {
        localDS.setAuthentication(appToken)
        return RepositoryResult.Success(true)
    }

    override suspend fun setUserID(userId: String): RepositoryResult<Boolean> {
        return localDS.setUserId(userId).let {
            when (it) {
                is DSResult.Error<*> ->
                    RepositoryResult.Error(
                        "Set user failed!",
                        "This shouldn't be happening",
                    )

                is DSResult.Success<*> -> RepositoryResult.Success(true)
            }
        }
    }
}
