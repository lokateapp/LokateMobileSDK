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
internal data class NextCampaignApiResult(val nextCampaignName: String)

// TODO: extract HTTP client to a separate object and do not create it on each function call
suspend fun getNextCampaign(customerId: String): String? {
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
            pathSegments = listOf("mobile", "demo", "next-campaign")
            parameters.append("customerId", customerId)
        }

    return try {
        val response: HttpResponse = client.get(url.buildString())
        val nextCampaignName = response.body<NextCampaignApiResult>().nextCampaignName
        log.d { "Next campaign based on the order of visits: $nextCampaignName" }
        nextCampaignName
    } catch (e: Exception) {
        log.e { "Getting next campaign failed: ${e.message}" }
        null
    } finally {
        client.close()
    }
}
