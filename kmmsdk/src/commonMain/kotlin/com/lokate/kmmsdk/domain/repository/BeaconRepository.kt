package com.lokate.kmmsdk.domain.repository

import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.domain.model.beacon.EventRequest

interface BeaconRepository {
    suspend fun fetchBeacons(
        latitude: Double,
        longitude: Double,
    ): RepositoryResult<List<LokateBeacon>>

    suspend fun deleteBeacons(): RepositoryResult<Boolean>

    suspend fun addBeacons(beacons: List<LokateBeacon>): RepositoryResult<Boolean>

    suspend fun sendBeaconEvent(beaconEventRequest: EventRequest): RepositoryResult<Boolean>
}
