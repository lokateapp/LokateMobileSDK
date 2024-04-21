package com.lokate.kmmsdk.data.datasource.interfaces.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationResponse

interface AuthenticationDS {
    suspend fun getAppAuthentication(appToken: String): DSResult<AuthenticationResponse>

    suspend fun setUserId(userId: String): DSResult<Boolean>
}
