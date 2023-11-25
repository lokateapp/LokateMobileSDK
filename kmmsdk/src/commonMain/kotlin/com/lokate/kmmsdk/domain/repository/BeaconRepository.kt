package com.lokate.kmmsdk.domain.repository

import com.lokate.kmmsdk.domain.model.beacon.Beacon
import com.lokate.kmmsdk.domain.model.repository.RepositoryResult

interface BeaconRepository {
    suspend fun fetchBeacons(): RepositoryResult<List<Beacon>>
    suspend fun deleteBeacons(): RepositoryResult<Boolean>
    suspend fun addBeacons(beacons:List<Beacon>): RepositoryResult<Boolean>
    suspend fun getBeaconsFromDb(): RepositoryResult<List<Beacon>>
}