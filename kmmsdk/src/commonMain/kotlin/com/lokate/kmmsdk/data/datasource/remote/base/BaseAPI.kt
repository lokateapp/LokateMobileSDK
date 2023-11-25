package com.lokate.kmmsdk.data.datasource.remote.base

import io.ktor.client.HttpClient

interface BaseAPI {
    val client: HttpClient
        get() = LokateHTTPClient.getInstance()
}