package com.lokate.kmmsdk.domain.repository

import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon
import com.lokate.kmmsdk.domain.model.beacon.EventRequest

interface BeaconRepository {
    suspend fun fetchBeacons(branchId: String): RepositoryResult<List<ActiveBeacon>>

    suspend fun deleteBeacons(): RepositoryResult<Boolean>

    suspend fun addBeacons(beacons: List<ActiveBeacon>): RepositoryResult<Boolean>

    suspend fun sendBeaconEvent(beaconEventRequest: EventRequest): RepositoryResult<Boolean>
}
