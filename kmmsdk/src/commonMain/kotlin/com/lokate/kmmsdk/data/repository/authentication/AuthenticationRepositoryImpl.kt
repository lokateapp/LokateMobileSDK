package com.lokate.kmmsdk.data.repository.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.local.authentication.AuthenticationLocalDS
import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import com.lokate.kmmsdk.data.datasource.remote.authentication.AuthenticationRemoteDS
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationResponse
import com.lokate.kmmsdk.domain.model.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.model.repository.RepositoryResult
import com.lokate.kmmsdk.utils.extension.emptyString

class AuthenticationRepositoryImpl(
    private val remoteDS: AuthenticationRemoteDS,
    private val localDS: AuthenticationLocalDS
) : AuthenticationRepository {
    override suspend fun getAppToken(): RepositoryResult<String> {
        return localDS.getAppToken().let {
            when (it) {
                is DSResult.Error<*> -> RepositoryResult.Error(it.message, it.errorType.toString())
                is DSResult.Success<*> -> if (it.data is String)
                    RepositoryResult.Success(it.data)
                else
                    RepositoryResult.Error(emptyString(), emptyString())
            }
        }
    }

    override suspend fun getAuthenticate(appToken: String): RepositoryResult<Boolean> {
        localDS.getAppAuthentication(appToken).let {
            when (it) {
                is DSResult.Success<*> -> {
                    if (it.data is AuthenticationResponse && it.data.valid)
                        return RepositoryResult.Success(true)
                }

                else -> {}
            }
        }
        remoteDS.getAppAuthentication(appToken).let {
            when (it) {
                is DSResult.Error<*> -> {
                    when (it.errorType) {
                        is ApiResponse.Error.HttpError<*> -> {
                            if (it.errorType.code == 401) {
                                return RepositoryResult.Error(
                                    "Authentication failed!",
                                    "Invalid app token"
                                )
                            }
                        }

                        else -> return RepositoryResult.Error(
                            "Authentication failed!",
                            "Connection Error"
                        )
                    }
                }

                is DSResult.Success<*> -> {
                    if (it.data is AuthenticationResponse) {
                        if (it.data.valid) {
                            setAuthentication(appToken)
                        }
                        return RepositoryResult.Success(true)
                    }
                }
            }
        }
        return RepositoryResult.Error("Authentication failed!", "Unknown Error")
    }

    private suspend fun setAuthentication(appToken: String): RepositoryResult<Boolean> {
        localDS.setAuthentication(appToken)
        return RepositoryResult.Success(true)
    }

    override suspend fun setUserID(userId: String): RepositoryResult<Boolean> {
        return localDS.setUserId(userId).let {
            when (it) {
                is DSResult.Error<*> -> RepositoryResult.Error(
                    "Set user failed!",
                    "This shouldn't be happening"
                )

                is DSResult.Success<*> -> RepositoryResult.Success(true)
            }
        }
    }
}