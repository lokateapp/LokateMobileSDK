package com.lokate.kmmsdk.data.datasource.remote.base

import io.ktor.client.HttpClient

interface BaseAPI {
    companion object DEFAULTS {
        const val SERVICE = "172.20.10.4"
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
