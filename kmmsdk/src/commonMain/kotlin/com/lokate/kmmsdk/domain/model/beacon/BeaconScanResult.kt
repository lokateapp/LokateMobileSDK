package com.lokate.kmmsdk.domain.model.beacon

data class BeaconScanResult(
    val beaconUUID: String,
    val major: Int,
    val minor: Int,
    val rssi: Double,
    val txPower: Int,
    val accuracy: Double?,
    val proximity: BeaconProximity,
    val seen: Long,
)
