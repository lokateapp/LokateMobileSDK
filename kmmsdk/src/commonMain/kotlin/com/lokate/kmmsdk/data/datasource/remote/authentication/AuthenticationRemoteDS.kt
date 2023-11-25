package com.lokate.kmmsdk.data.datasource.remote.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.authentication.AuthenticationDS
import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationRequest

class AuthenticationRemoteDS(private val api: AuthenticationAPI) : AuthenticationDS {
    override suspend fun getAppAuthentication(appToken: String): DSResult {
        return api.getAuthenticate(AuthenticationRequest(appToken)).let{
            when (it) {
                is ApiResponse.Error.GenericError -> DSResult.Error(
                    it.errorMessage.orEmpty(),
                    it
                )

                is ApiResponse.Error.HttpError -> DSResult.Error(
                    "${it.code}" + it.errorBody,
                    it
                )

                is ApiResponse.Error.SerializationError -> DSResult.Error(
                    it.errorMessage.orEmpty(),
                    it
                )

                is ApiResponse.Success -> DSResult.Success(it.body)
            }
        }

    }

    override suspend fun setUserId(userId: String): DSResult {
        throw UnsupportedOperationException("userId cannot be set through remote ds")
    }
}