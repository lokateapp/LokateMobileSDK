package com.lokate.kmmsdk.utils

import com.lokate.kmmsdk.domain.model.beacon.BeaconProximity
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import kotlin.math.pow

fun org.altbeacon.beacon.Beacon.toBeaconScanResult(): BeaconScanResult {
    val beaconUUID = id1.toString()
    val accuracy = calculateAccuracy(txPower, rssi.toDouble())
    val proximity = calculateProximity(accuracy)
    return BeaconScanResult(
        beaconUUID = beaconUUID,
        rssi = rssi.toDouble(),
        txPower = txPower,
        accuracy = accuracy,
        proximity = proximity,
        major = id2.toInt(),
        minor = id3.toInt(),
        firstSeen = System.currentTimeMillis(),
        lastSeen = System.currentTimeMillis(),
    )
}

// taken from kmmbeacons
fun calculateProximity(accuracy: Double): BeaconProximity =
    when (accuracy) {
        in 0.0..0.5 -> BeaconProximity.Immediate
        in 0.5..3.0 -> BeaconProximity.Near
        in 3.0..Double.MAX_VALUE -> BeaconProximity.Far
        else -> BeaconProximity.Unknown
    }

fun calculateAccuracy(
    txPower: Int,
    rssi: Double,
): Double {
    if (rssi == 0.0) {
        return -1.0 // if we cannot determine accuracy, return -1.
    }

    val ratio = rssi * 1.0 / txPower
    return if (ratio < 1.0) {
        ratio.pow(10.0)
    } else {
        val accuracy = 0.89976 * ratio.pow(7.7095) + 0.111
        accuracy
    }
}

fun LokateBeacon.toRegion() =
    Region(
        this.uuid,
        Identifier.parse(this.uuid),
        null,
        null,
        // Identifier.fromInt(this.major ?: 0),
        // Identifier.fromInt(this.minor ?: 0)
    )
