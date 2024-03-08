package com.lokate.kmmsdk.data.datasource.local.beacon

import com.lokate.kmmsdk.Database
import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.interfaces.beacon.BeaconDS
import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon
import com.lokate.kmmsdk.domain.model.beacon.BeaconProximity
import com.lokate.kmmsdk.domain.model.beacon.Campaign
import com.lokate.kmmsdk.domain.model.beacon.EventRequest
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING

class BeaconLocalDS(
    database: Database,
) : BeaconDS {
    private val queries = database.beaconDatabaseQueries

    @Suppress("TooGenericExceptionCaught")
    override suspend fun fetchBeacons(branchId: String): DSResult<List<ActiveBeacon>> {
        return try {
            queries.selectAllBeacons().executeAsList().let {
                DSResult.Success(
                    it.map {
                        ActiveBeacon(
                            userId = it.uuid,
                            major = it.major.toString(),
                            minor = it.minor.toString(),
                            range = BeaconProximity.fromInt(it.range.toInt()),
                            branchId = EMPTY_STRING,
                            radius = 0,
                            name = EMPTY_STRING,
                            campaign =
                                Campaign(
                                    EMPTY_STRING,
                                    EMPTY_STRING,
                                    EMPTY_STRING,
                                    EMPTY_STRING,
                                    EMPTY_STRING,
                                ),
                            id = EMPTY_STRING,
                        )
                    },
                )
            }
        } catch (e: Exception) {
            DSResult.Error(e.message, e)
        }
    }

    override suspend fun sendBeaconEvent(beaconEventRequest: EventRequest): DSResult<Boolean> {
        throw NotImplementedError("LocalDS doesn't support this operation!")
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun updateOrInsertBeacon(beacons: List<ActiveBeacon>): DSResult<Boolean> {
        return try {
            queries.transaction {
                beacons.forEach {
                    queries.insertBeacon(
                        uuid = it.userId,
                        major = it.major.toLong(),
                        minor = it.minor.toLong(),
                        range = it.range.ordinal.toLong(),
                    )
                }
            }
            DSResult.Success(true)
        } catch (e: Exception) {
            DSResult.Error(e.message, e)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun removeBeacons(): DSResult<Boolean> {
        return try {
            queries.transaction {
                queries.removeAllBeacons()
            }
            DSResult.Success(true)
        } catch (e: Exception) {
            DSResult.Error(e.message, e)
        }
    }
}
