package com.lokate.kmmsdk.data.datasource.remote.beacon

import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import com.lokate.kmmsdk.data.datasource.remote.base.BaseAPI
import com.lokate.kmmsdk.data.datasource.remote.base.lokateRequest
import com.lokate.kmmsdk.domain.model.beacon.EventRequest
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.path

class BeaconAPI : BaseAPI {
    suspend fun getActiveBeacons(branchId: String): ApiResponse<List<LokateBeacon>, Any> =
        client.lokateRequest {
            url {
                method = HttpMethod.Get
                host = this@BeaconAPI.path
                path("activeBeacons")
                parameters.append("branchId", branchId)
            }
        }

    suspend fun postBeaconArea(request: EventRequest): ApiResponse<Any, Any> =
        client.lokateRequest {
            url {
                method = HttpMethod.Post
                host = this@BeaconAPI.path
                path("beaconArea")
                setBody(request)
            }
        }
}
