package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.BeaconProximity
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconRegion
import platform.CoreLocation.CLProximity
import platform.Foundation.NSUUID

fun LokateBeacon.toCLBeaconRegion(): CLBeaconRegion {
    // NSLog("Converting Beacon to CLBeaconRegion: UUID - ${this.uuid}, Major - ${this.major}, Minor - ${this.minor}")
    return CLBeaconRegion(
        uUID = NSUUID(this.uuid),
        major = (this.major ?: 0).toUShort(),
        minor = (this.minor ?: 0).toUShort(),
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
        beaconUUID = this.proximityUUID.UUIDString,
        rssi = this.rssi.toDouble(),
        txPower = 0,
        accuracy = this.accuracy,
        proximity = this.proximity.toLokateProximity(),
        major = this.major.intValue,
        minor = this.minor.intValue,
    )
}

fun Beacon.toCLBeaconRegion(): CLBeaconRegion {
    // NSLog("Converting Beacon to CLBeaconRegion: UUID - ${this.uuid}, Major - ${this.major}, Minor - ${this.minor}")
    return CLBeaconRegion(
        uUID = NSUUID(this.uuid),
        major = this.major.toUShort(),
        minor = this.minor.toUShort(),
        identifier = EMPTY_STRING,
    )
}
