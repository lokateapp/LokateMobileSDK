package com.lokate.kmmsdk.data.datasource.remote.beacon

import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import com.lokate.kmmsdk.data.datasource.remote.base.BaseAPI
import com.lokate.kmmsdk.data.datasource.remote.base.lokateRequest
import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon
import com.lokate.kmmsdk.domain.model.beacon.EventRequest
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path

class BeaconAPI : BaseAPI {
    suspend fun getActiveBeacons(branchId: String): ApiResponse<List<ActiveBeacon>, Any> =
        client.lokateRequest {
            url {
                method = HttpMethod.Get
                host = baseUrl
                port = defPort
                path(getPath("activeBeacons"))
                parameters.append("branchId", branchId)
            }
        }

    suspend fun sendBeaconEvent(event: EventRequest): ApiResponse<String, Any> =
        client.lokateRequest {
            url {
                method = HttpMethod.Post
                host = baseUrl
                port = defPort
                path(getPath("beaconArea"))
                contentType(ContentType.Application.Json)
                setBody(event)
            }
        }
}
