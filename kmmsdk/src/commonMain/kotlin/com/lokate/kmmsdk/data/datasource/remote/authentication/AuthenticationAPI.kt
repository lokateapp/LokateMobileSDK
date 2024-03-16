package com.lokate.kmmsdk.data.datasource.remote.authentication

import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import com.lokate.kmmsdk.data.datasource.remote.base.BaseAPI
import com.lokate.kmmsdk.data.datasource.remote.base.lokateRequest
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationRequest
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationResponse
import io.ktor.http.HttpMethod
import io.ktor.http.path

class AuthenticationAPI : BaseAPI {
    suspend fun getAuthenticate(authenticationRequest: AuthenticationRequest):
            ApiResponse<AuthenticationResponse, Any> = client.lokateRequest {
            url {
                method = HttpMethod.Get
                port = defPort
                host = baseUrl
                path(getPath("authenticate/${authenticationRequest.appToken}"))
            }
        }
}
