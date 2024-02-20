package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import kotlinx.coroutines.flow.Flow

class LokateSDK {
    private val scanner = getScanner()

    private val activeBeacons = mutableSetOf<BeaconScanResult>()
    private var results: Flow<List<BeaconScanResult>>? = null

    fun startScanning() {
        //scanner.start()
        //results = scanner.observeResults()
    }

    fun beaconCalc(){
        //three cases
        //1. beacon is already in the list and came again
        //2. beacon is not in the list and came for the first time
        //3. beacon is in the list and did not come again
        // in first case send stay event
        // in second case send enter event
        // in third case send exit event
    }

    fun stopScanning() {
        //scanner.stop()

    }


}