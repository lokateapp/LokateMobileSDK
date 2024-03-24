package com.lokate.kmmsdk.data.datasource.remote.base

import io.ktor.client.HttpClient
import com.lokate.kmmsdk.BuildKonfig

interface BaseAPI {
    companion object DEFAULTS {
        val SERVICE = BuildKonfig.MOBILE_API_IP_ADDRESS
        const val MOBILE_PATH = "mobile"
    }

    val client: HttpClient
        get() = LokateHTTPClient.getInstance()

    val baseUrl: String
        get() = SERVICE

    val defPort: Int
        get() = 5173

    fun getPath(path: String): String {
        return "$MOBILE_PATH/$path"
    }
}
