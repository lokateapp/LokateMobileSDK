package com.lokate.kmmsdk.data.datasource.remote.beacon

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.beacon.BeaconDS
import com.lokate.kmmsdk.data.datasource.toDSResult
import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon

class BeaconRemoteDS(private val api: BeaconAPI) : BeaconDS {
    override suspend fun fetchBeacons(branchId: String): DSResult<List<ActiveBeacon>> =
        api.getActiveBeacons(branchId).toDSResult()
}
