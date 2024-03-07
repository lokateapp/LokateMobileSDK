package com.lokate.kmmsdk.data.datasource.interfaces.authentication

import com.lokate.kmmsdk.data.datasource.DSResult

interface AuthenticationDS {
    suspend fun getAppAuthentication(appToken: String): DSResult

    suspend fun setUserId(userId: String): DSResult
}
