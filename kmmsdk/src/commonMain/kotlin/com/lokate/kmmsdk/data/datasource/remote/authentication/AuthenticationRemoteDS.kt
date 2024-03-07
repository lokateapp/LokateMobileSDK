package com.lokate.kmmsdk.data.datasource.remote.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.authentication.AuthenticationDS
import com.lokate.kmmsdk.data.datasource.toDSResult
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationRequest
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationResponse

class AuthenticationRemoteDS(private val api: AuthenticationAPI) : AuthenticationDS {
    override suspend fun getAppAuthentication(appToken: String): DSResult<AuthenticationResponse> =
        api.getAuthenticate(AuthenticationRequest(appToken)).toDSResult()

    override suspend fun setUserId(userId: String): DSResult<Boolean> {
        throw UnsupportedOperationException("userId cannot be set through remote ds")
    }
}
