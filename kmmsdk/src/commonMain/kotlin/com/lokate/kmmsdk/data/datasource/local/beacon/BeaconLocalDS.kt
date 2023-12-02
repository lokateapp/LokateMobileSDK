package com.lokate.kmmsdk.data.datasource.local.beacon

import com.lokate.kmmsdk.Database
import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.domain.model.beacon.Beacon

class BeaconLocalDS(
    private val database: Database
) {
    private val queries = database.beaconDatabaseQueries

    suspend fun addBeacon(beacons: List<Beacon>) {
        beacons.forEach {
            queries.insertBeacon(it.uuid, it.major.toLong(), it.minor.toLong())
        }
    }

    suspend fun getBeacons(): DSResult {
        val a = queries.selectAllBeacons()
        return DSResult.Success(a.executeAsList().map {
            Beacon(
                it.id.toString(),
                it.uuid,
                it.major.toInt(),
                it.minor.toInt()
            )
        })
    }

    suspend fun removeBeacons() {
        queries.removeAllBeacons()
    }
}