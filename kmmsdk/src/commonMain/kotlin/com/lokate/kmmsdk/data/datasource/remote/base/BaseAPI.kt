package com.lokate.kmmsdk.data.datasource.remote.base

import io.ktor.client.HttpClient

interface BaseAPI {
    companion object DEFAULTS {
        const val SERVICE = "http://172.20.10.4:5173/"
        const val MOBILE_PATH = "mobile"
    }

    val client: HttpClient
        get() = LokateHTTPClient.getInstance()

    val baseUrl: String
        get() = SERVICE

    val path: String
        get() = "$SERVICE/$MOBILE_PATH"
}
