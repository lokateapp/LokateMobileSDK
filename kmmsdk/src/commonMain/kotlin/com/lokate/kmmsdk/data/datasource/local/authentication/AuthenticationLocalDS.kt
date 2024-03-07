package com.lokate.kmmsdk.data.datasource.local.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.authentication.AuthenticationDS
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import com.russhwolf.settings.Settings

class AuthenticationLocalDS(private val authenticationSettings: Settings) : AuthenticationDS {
    suspend fun getAppToken(): DSResult {
        return authenticationSettings.getString("auth_token", EMPTY_STRING).let {
            when (it.isEmpty()) {
                true -> DSResult.Error("No auth token found", Error())
                else -> DSResult.Success(it)
            }
        }
    }

    override suspend fun getAppAuthentication(appToken: String): DSResult {
        return authenticationSettings.getString("auth_token", EMPTY_STRING).let {
            when (it.isEmpty()) {
                true -> DSResult.Error("No auth token found", Error())
                (it == appToken) -> DSResult.Success(it)
                else -> {
                    DSResult.Error("Invalid auth token", Error())
                }
            }
        }
    }

    override suspend fun setUserId(userId: String): DSResult {
        return authenticationSettings.putString("user_id", userId).let {
            DSResult.Success(userId)
        }
    }

    fun setAuthentication(authToken: String): DSResult {
        return authenticationSettings.putString("auth_token", authToken).let {
            DSResult.Success(authToken)
        }
    }
}
