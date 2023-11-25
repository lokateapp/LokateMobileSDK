package com.lokate.kmmsdk.data.datasource.local.beacon

import com.lokate.Database
import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.domain.model.beacon.Beacon
import com.lokate.kmmsdk.utils.extension.emptyString

class BeaconLocalDS(
    private val database: Database
) {
    private val queries = database.beaconDatabaseQueries

    suspend fun addBeacon(beacons: List<Beacon>) {
        beacons.forEach {
            queries.insertBeacon(it.uuid, it.major, it.minor)
        }
    }

    suspend fun getBeacons(): DSResult {
        val a = queries.selectAllBeacons()
        if (a is List<Beacon>)
            return DSResult.Success(a)
        else
            return DSResult.Error(emptyString(), emptyString())
    }

    suspend fun removeBeacons() {
        queries.removeAllBeacons()
    }
}