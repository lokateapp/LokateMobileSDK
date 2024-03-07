package com.lokate.kmmsdk.domain.repository

import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon

interface BeaconRepository {
    suspend fun fetchBeacons(branchId: String): RepositoryResult<List<ActiveBeacon>>

    suspend fun deleteBeacons(): RepositoryResult<Boolean>

    suspend fun addBeacons(beacons: List<ActiveBeacon>): RepositoryResult<Boolean>
}
