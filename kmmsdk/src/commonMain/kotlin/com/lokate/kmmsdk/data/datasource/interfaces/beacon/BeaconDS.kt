package com.lokate.kmmsdk.data.datasource.interfaces.beacon

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon
import com.lokate.kmmsdk.domain.model.beacon.EventRequest

interface BeaconDS {
    suspend fun fetchBeacons(branchId: String): DSResult<List<ActiveBeacon>>

    suspend fun sendBeaconEvent(beaconEventRequest: EventRequest): DSResult<Boolean>
}
