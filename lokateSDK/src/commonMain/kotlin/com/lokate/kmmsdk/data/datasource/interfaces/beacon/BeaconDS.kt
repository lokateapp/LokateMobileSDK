package com.lokate.kmmsdk.data.datasource.interfaces.beacon

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.domain.model.beacon.EventRequest
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon

interface BeaconDS {
    suspend fun fetchBeacons(
        latitude: Double,
        longitude: Double,
    ): DSResult<List<LokateBeacon>>

    suspend fun sendBeaconEvent(beaconEventRequest: EventRequest): DSResult<Boolean>
}
