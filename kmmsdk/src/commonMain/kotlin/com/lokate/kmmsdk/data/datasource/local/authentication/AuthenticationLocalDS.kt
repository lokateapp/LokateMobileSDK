package com.lokate.kmmsdk.data.datasource.local.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.authentication.AuthenticationDS
import com.lokate.kmmsdk.domain.model.authentication.AuthenticationResponse
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import com.russhwolf.settings.Settings

class AuthenticationLocalDS(private val authenticationSettings: Settings) : AuthenticationDS {
    suspend fun getAppToken(): DSResult<String> {
        return authenticationSettings.getString("auth_token", EMPTY_STRING).let {
            when (it.isEmpty()) {
                true -> DSResult.Error("No auth token found", Error())
                else -> DSResult.Success(it)
            }
        }
    }

    override suspend fun getAppAuthentication(appToken: String): DSResult<AuthenticationResponse> {
        return authenticationSettings.getString("auth_token", EMPTY_STRING).let {
            when (it.isEmpty()) {
                true -> DSResult.Error("No auth token found", Error())
                else -> DSResult.Success(AuthenticationResponse(true))
            }
        }
    }

    override suspend fun setUserId(userId: String): DSResult<Boolean> {
        return try {
            authenticationSettings.putString("user_id", userId)
            DSResult.Success(true)
        } catch (e: Exception) {
            DSResult.Error(e.message, e)
        }
    }

    fun setAuthentication(authToken: String): DSResult<Boolean> {
        return try {
            authenticationSettings.putString("auth_token", authToken)
            DSResult.Success(true)
        } catch (e: Exception) {
            DSResult.Error(e.message, e)
        }
    }
}
