package com.lokate.kmmsdk.data.repository

import com.lokate.kmmsdk.data.datasource.local.beacon.BeaconLocalDS
import com.lokate.kmmsdk.data.datasource.remote.beacon.BeaconRemoteDS
import com.lokate.kmmsdk.domain.model.beacon.EventRequest
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.domain.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.repository.BeaconRepository
import com.lokate.kmmsdk.domain.repository.RepositoryResult
import com.lokate.kmmsdk.domain.repository.toRepositoryResult
import org.lighthousegames.logging.logging

class BeaconRepositoryImpl(
    private val authenticationRepository: AuthenticationRepository,
    private val remoteDS: BeaconRemoteDS,
    private val localDS: BeaconLocalDS,
) : BeaconRepository {
    override suspend fun fetchBeacons(
        latitude: Double,
        longitude: Double,
    ): RepositoryResult<List<LokateBeacon>> {
        val appToken = authenticationRepository.getAppToken()
                // if (appToken !is RepositoryResult.Success) {
            //return RepositoryResult.Error("Couldn't fetch!", "No auth token!")
        //}

        if (latitude == 0.0 && longitude == 0.0) {
            return RepositoryResult.Error("Couldn't fetch!", "Current geolocation is unknown!")
        }

        logging("LokateSDK").d { "Fetching beacons for branch close to $latitude, $longitude" }

        val remoteBeacons = remoteDS.fetchBeacons(latitude, longitude).toRepositoryResult()
        if (remoteBeacons is RepositoryResult.Success) {
            localDS.updateOrInsertBeacon(remoteBeacons.body)
            return remoteBeacons.also {
                logging("LokateSDK").d { "Fetched from remote" }
            }
        } else {
            logging("LokateSDK").e { "Fetching from remote failed: $remoteBeacons" }
        }

        return RepositoryResult.Error("Couldn't fetch!", "Default beacons!")
        val localBeacons = localDS.fetchBeacons(latitude, longitude).toRepositoryResult()
        return if (localBeacons is RepositoryResult.Success && localBeacons.body.isNotEmpty()) {
            localBeacons.also {
                logging("LokateSDK").d { "Fetched from DB" }
            }
        } else {
            RepositoryResult.Error("Couldn't fetch!", "No beacons!")
        }
    }

    override suspend fun deleteBeacons(): RepositoryResult<Boolean> = localDS.removeBeacons().toRepositoryResult()

    override suspend fun addBeacons(beacons: List<LokateBeacon>): RepositoryResult<Boolean> =
        localDS.updateOrInsertBeacon(beacons).toRepositoryResult()

    override suspend fun sendBeaconEvent(beaconEventRequest: EventRequest): RepositoryResult<Boolean> {
        return remoteDS.sendBeaconEvent(beaconEventRequest).toRepositoryResult()
    }
}
