package com.lokate.kmmsdk.data.datasource.remote.base

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object LokateHTTPClient {
    private val _instance by lazy {
        HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }
            HttpResponseValidator {
                validateResponse {
                    if (!it.status.isSuccess()) {
                        val failReason = it.status.description
                        throw HttpExceptions(
                            response = it,
                            failureReason = failReason,
                            cachedResponseText = it.bodyAsText(),
                        )
                    }
                }
            }
        }
    }

    fun getInstance() = _instance
}
