package com.lokate.demo

import com.lokate.kmmsdk.BeaconScanner
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SpecialBeaconScannerType: BeaconScanner {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val flow = MutableSharedFlow<BeaconScanResult>()

    override fun startScanning() {

    }

    override fun stopScanning() {

    }

    override fun setRegions(regions: List<LokateBeacon>) {

    }

    fun emitScanResult(result: BeaconScanResult) {
        scope.launch {
            flow.emit(result)
        }
    }

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return flow
    }

}