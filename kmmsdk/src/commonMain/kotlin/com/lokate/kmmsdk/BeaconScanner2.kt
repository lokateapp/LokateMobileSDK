package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.Flow

interface BeaconScanner2 {

    fun setScanPeriod(interval: Long)
    fun startScanning()
    fun stopScanning()
    fun setRegions(regions: List<LokateBeacon>)
    fun complete()
    fun scanResultFlow(): Flow<Set<BeaconScanResult>>
}