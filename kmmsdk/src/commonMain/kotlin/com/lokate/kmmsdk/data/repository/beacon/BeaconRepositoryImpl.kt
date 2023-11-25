package com.lokate.kmmsdk.data.repository.beacon

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.local.beacon.BeaconLocalDS
import com.lokate.kmmsdk.data.datasource.remote.beacon.BeaconRemoteDS
import com.lokate.kmmsdk.domain.model.beacon.Beacon
import com.lokate.kmmsdk.domain.model.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.model.repository.RepositoryResult
import com.lokate.kmmsdk.domain.repository.BeaconRepository
import com.lokate.kmmsdk.utils.extension.emptyString

class BeaconRepositoryImpl(
    private val authenticationRepository: AuthenticationRepository,
    private val remoteDS: BeaconRemoteDS,
    private val localDS: BeaconLocalDS
): BeaconRepository {
    override suspend fun fetchBeacons(): RepositoryResult<List<Beacon>> {
        val appToken = authenticationRepository.getAppToken()
        if(appToken !is RepositoryResult.Success)
            return RepositoryResult.Error("Couldn't fetch!", "No auth token!")
        return remoteDS.fetchBeacons(appToken.body).let {
            when(it){
                is DSResult.Error<*> -> RepositoryResult.Error(emptyString(), emptyString())
                is DSResult.Success<*> -> if(it.data is List<*>)
                    RepositoryResult.Success(it.data as List<Beacon>)
                else
                    RepositoryResult.Error(emptyString(), emptyString())
            }
        }
    }

    override suspend fun deleteBeacons(): RepositoryResult<Boolean> {
        return localDS.removeBeacons().let {
            RepositoryResult.Success(true)
        }
    }

    override suspend fun addBeacons(beacons: List<Beacon>): RepositoryResult<Boolean> {
        return localDS.addBeacon(beacons).let {
            RepositoryResult.Success(true)
        }
    }

    override suspend fun getBeaconsFromDb(): RepositoryResult<List<Beacon>> {
        return localDS.getBeacons().let {
            when(it){
                is DSResult.Error<*> -> RepositoryResult.Error(emptyString(),emptyString())
                is DSResult.Success<*> ->
                if(it.data is List<*>)
                    RepositoryResult.Success(it.data as List<Beacon>)
                else
                    RepositoryResult.Error(emptyString(), emptyString())
            }
        }
    }
}