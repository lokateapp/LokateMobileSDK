package com.lokate.kmmsdk.data.datasource.local.authentication

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.authentication.AuthenticationDS
import com.lokate.kmmsdk.utils.extension.emptyString
import com.russhwolf.settings.Settings

class AuthenticationLocalDS(private val authenticationSettings: Settings):AuthenticationDS {
    override suspend fun getAppAuthentication(appToken: String): DSResult {
        return authenticationSettings.getString("auth_token", defaultValue = "").let {
            when(it){
                "" -> DSResult.Error("No auth token found", Error())
                else -> DSResult.Success(it)
            }
        }
    }

    internal suspend fun getAppToken(): DSResult{
        return authenticationSettings.getString("auth_token", emptyString()).let {
            when(it){
                emptyString() -> DSResult.Error("No auth token found", Error())
                else -> DSResult.Success(it)
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