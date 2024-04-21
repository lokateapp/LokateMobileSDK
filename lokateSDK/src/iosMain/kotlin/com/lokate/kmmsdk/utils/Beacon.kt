package com.lokate.kmmsdk.utils

import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconRegion
import platform.Foundation.NSUUID
import platform.Foundation.timeIntervalSince1970

fun LokateBeacon.toCLBeaconRegion(): CLBeaconRegion {
    // NSLog("Converting Beacon to CLBeaconRegion: UUID - ${this.uuid}, Major - ${this.major}, Minor - ${this.minor}")
    if (this.minor == null || this.major == null) {
        return CLBeaconRegion(
            uUID = NSUUID(this.proximityUUID),
            identifier = EMPTY_STRING,
        )
    }
    return CLBeaconRegion(
        uUID = NSUUID(this.proximityUUID),
        major = (this.major).toUShort(),
        minor = (this.minor).toUShort(),
        identifier = EMPTY_STRING,
    )
}

fun CLBeacon.toBeaconScanResult(): BeaconScanResult {
    // NSLog("Converting CLBeacon to BeaconScanResult: UUID - ${this.proximityUUID.UUIDString}, RSSI - ${this.rssi}")
    return BeaconScanResult(
        beaconUUID = proximityUUID.UUIDString,
        rssi = rssi.toDouble(),
        accuracy = accuracy,
        major = major.intValue,
        minor = minor.intValue,
        txPower = 0,
        seen = (timestamp.timeIntervalSince1970 * 1000L).toLong(),
    )
}
