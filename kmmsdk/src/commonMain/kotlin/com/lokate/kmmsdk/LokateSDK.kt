package com.lokate.kmmsdk

import com.lokate.kmmsdk.Defaults.DEFAULT_BEACONS
import com.lokate.kmmsdk.Defaults.GONE_CHECK_INTERVAL
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class LokateSDK {
    companion object {
        val log = logging("LokateSDK")
    }

    private val beaconScanner = getBeaconScanner()

    private val lokateJob = SupervisorJob()
    private val lokateScope = CoroutineScope(Dispatchers.IO + lokateJob)

    private val activeBeacons = mutableSetOf<BeaconScanResult>()

    private val newComer = MutableSharedFlow<BeaconScanResult>()
    private val alreadyIn = MutableSharedFlow<BeaconScanResult>()
    private val gone = MutableSharedFlow<BeaconScanResult>()

    fun getScanResultFlow(): Flow<BeaconScanResult> {
        return beaconScanner.scanResultFlow()
    }

    fun startScanning() {
        log.d { "SA" }
        beaconScanner.setRegions(DEFAULT_BEACONS)
        beaconScanner.startScanning()

        scanResultHandler(
            beaconScannerFlow =
                beaconScanner.scanResultFlow().transform { scan ->
                    if (scan.accuracy != null && scan.accuracy > -1.0) {
                        emit(scan)
                    }
                },
        )
        checkGone()
        flowEmitter()
        // beaconScanner.start()
        // results = beaconScanner.observeResults()
    }

    private fun checkGone() {
        lokateScope.launch {
            while (true) {
                delay(GONE_CHECK_INTERVAL)
                log.d { "Checking for gone beacons" }
                activeBeacons.forEach {
                    if (it.seen < getTimeMillis() - Defaults.DEFAULT_TIMEOUT_BEFORE_GONE) {
                        gone.emit(it)
                        activeBeacons.remove(it)
                    }
                }
            }
        }
    }

    private fun flowEmitter() {
        lokateScope.launch {
            newComer.collect {
                log.d { "Newcomer: $it" }
            }
        }
        lokateScope.launch {
            alreadyIn.collect {
                log.d { "Already in: $it" }
            }
        }
        lokateScope.launch {
            gone.collect {
                log.d { "Gone: $it" }
            }
        }
    }

    private fun scanResultHandler(beaconScannerFlow: Flow<BeaconScanResult>) {
        lokateScope.launch {
            beaconScannerFlow.collect { scan ->
                activeBeacons.firstOrNull {
                    it.beaconUUID == it.beaconUUID &&
                        it.major == it.major &&
                        it.minor == it.minor
                }
                    ?.let {
                        alreadyIn.emit(it.copy(seen = getTimeMillis()))
                    } ?: newComer.emit(scan).also { activeBeacons.add(scan) }
            }
        }
    }

    fun stopScanning() {
        // beaconScanner.stop()
        beaconScanner.stopScanning()
        lokateScope.cancel()
    }
}
