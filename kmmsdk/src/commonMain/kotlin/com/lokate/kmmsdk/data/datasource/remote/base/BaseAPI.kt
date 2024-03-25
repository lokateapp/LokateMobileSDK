package com.lokate.kmmsdk.data.datasource.remote.base

import com.lokate.kmmsdk.BuildKonfig
import io.ktor.client.HttpClient

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
