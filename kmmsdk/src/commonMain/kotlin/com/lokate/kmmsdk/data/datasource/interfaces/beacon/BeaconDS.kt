package com.lokate.kmmsdk.data.datasource.interfaces.beacon

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon

interface BeaconDS {
    suspend fun fetchBeacons(branchId: String): DSResult<List<ActiveBeacon>>
}
