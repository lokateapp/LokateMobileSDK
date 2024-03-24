package com.lokate.kmmsdk.data.datasource.remote.beacon

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.beacon.BeaconDS
import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import com.lokate.kmmsdk.data.datasource.toDSResult
import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon
import com.lokate.kmmsdk.domain.model.beacon.EventRequest

class BeaconRemoteDS(private val api: BeaconAPI) : BeaconDS {
    override suspend fun fetchBeacons(branchId: String): DSResult<List<ActiveBeacon>> {
        println()
        val a = api.getActiveBeacons(branchId)
        return a.toDSResult()
    }

    override suspend fun sendBeaconEvent(beaconEventRequest: EventRequest): DSResult<Boolean> {
        api.sendBeaconEvent(beaconEventRequest).let {
            if (it is ApiResponse.Success) {
                return DSResult.Success(true)
            } else {
                return DSResult.Error("Couldn't send beacon event!", "No response!")
            }
        }
    }
}
