package com.lokate.kmmsdk.utils

import com.lokate.kmmsdk.domain.model.beacon.BeaconProximity
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconRegion
import platform.CoreLocation.CLProximity
import platform.Foundation.NSUUID
import platform.Foundation.timeIntervalSince1970

fun LokateBeacon.toCLBeaconRegion(): CLBeaconRegion {
    // NSLog("Converting Beacon to CLBeaconRegion: UUID - ${this.uuid}, Major - ${this.major}, Minor - ${this.minor}")
    if (this.minor == null || this.major == null)
        return CLBeaconRegion(
            uUID = NSUUID(this.uuid),
            identifier = EMPTY_STRING,
        )
    return CLBeaconRegion(
        uUID = NSUUID(this.uuid),
        major = (this.major).toUShort(),
        minor = (this.minor).toUShort(),
        identifier = EMPTY_STRING,
    )
}

fun CLProximity.toLokateProximity(): BeaconProximity {
    // NSLog("Converting CLProximity to LokateProximity: Proximity - $this")
    return when (this) {
        CLProximity.CLProximityUnknown -> BeaconProximity.Unknown
        CLProximity.CLProximityImmediate -> BeaconProximity.Immediate
        CLProximity.CLProximityNear -> BeaconProximity.Near
        CLProximity.CLProximityFar -> BeaconProximity.Far
        else -> BeaconProximity.Unknown
    }
}

fun CLBeacon.toBeaconScanResult(): BeaconScanResult {
    // NSLog("Converting CLBeacon to BeaconScanResult: UUID - ${this.proximityUUID.UUIDString}, RSSI - ${this.rssi}")
    return BeaconScanResult(
        beaconUUID = proximityUUID.UUIDString,
        rssi = rssi.toDouble(),
        accuracy = accuracy,
        proximity = proximity.toLokateProximity(),
        major = major.intValue,
        minor = minor.intValue,
        seen = (timestamp.timeIntervalSince1970 * 1000L).toLong(),
    )
}
