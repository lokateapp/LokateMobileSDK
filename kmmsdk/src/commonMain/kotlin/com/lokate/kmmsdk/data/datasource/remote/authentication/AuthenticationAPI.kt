package com.lokate.kmmsdk.data.datasource.remote.authentication

import com.lokate.kmmsdk.data.datasource.remote.base.BaseAPI
import com.lokate.kmmsdk.data.datasource.remote.base.lokateRequest
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationRequest
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationResponse
import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import io.ktor.http.HttpMethod
import io.ktor.http.path

class AuthenticationAPI: BaseAPI {

    companion object{
        const val SERVICE = "127.0.0.1:3000"
    }

    suspend fun getAuthenticate(authenticationRequest: AuthenticationRequest): ApiResponse<AuthenticationResponse, Any> = client.lokateRequest{
        url{
            method = HttpMethod.Get
            this.host = SERVICE
            path("authenticate/${authenticationRequest.appToken}")
        }
    }
}