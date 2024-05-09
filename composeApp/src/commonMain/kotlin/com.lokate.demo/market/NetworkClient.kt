package com.lokate.demo.market

import com.lokate.demo.BuildKonfig
import com.lokate.kmmsdk.LokateSDK.Companion.log
import com.lokate.kmmsdk.data.datasource.remote.base.HttpExceptions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class ApiResult(val affinedCampaigns: List<String>, val nextCampaign: String)

// TODO: extract HTTP client to a separate object and do not create it on each function call
suspend fun getLocationBasedRecommendations(customerId: String): Pair<List<String>, String?> {
    val client =
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

    val url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTP
            host = BuildKonfig.MOBILE_API_IP_ADDRESS
            port = 5173
            pathSegments = listOf("mobile", "demo", "market")
            parameters.append("customerId", customerId)
        }

    return try {
        val response: HttpResponse = client.get(url.buildString())
        val affinedCampaigns = response.body<ApiResult>().affinedCampaigns
        log.d { "Affined campaigns based on purchase and visit history: $affinedCampaigns" }
        val nextCampaign = response.body<ApiResult>().nextCampaign
        log.d { "Next campaign based on the order of visits: $nextCampaign" }
        Pair(affinedCampaigns, nextCampaign)
    } catch (e: Exception) {
        log.e { "Getting relevant location information failed: ${e.message}" }
        Pair(emptyList(), null)
    } finally {
        client.close()
    }
}
