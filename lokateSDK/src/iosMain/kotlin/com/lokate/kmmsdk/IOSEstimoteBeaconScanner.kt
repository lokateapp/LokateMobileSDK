package com.lokate.kmmsdk

import cocoapods.EstimoteProximitySDK.EPXCloudCredentials
import cocoapods.EstimoteProximitySDK.EPXProximityObserver
import cocoapods.EstimoteProximitySDK.EPXProximityRange
import cocoapods.EstimoteProximitySDK.EPXProximityRange.Companion.farRange
import cocoapods.EstimoteProximitySDK.EPXProximityZone
import cocoapods.EstimoteProximitySDK.EPXProximityZoneContext
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import io.ktor.util.date.getTimeMillis
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import platform.Foundation.NSLog

@OptIn(ExperimentalForeignApi::class)
class IOSEstimoteBeaconScanner(appId: String, appToken: String) : BeaconScanner {
    private val cloudCredentials = EPXCloudCredentials(appId, appToken)
    private val proximityObserver =
        EPXProximityObserver(cloudCredentials) { error ->
            NSLog("Error: $error")
        }

    private var mainJob = SupervisorJob()
    private var coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)

    private var running: Boolean = false

    private val regions = mutableListOf<EPXProximityZone>()
    private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()

    private val scanResults = mutableSetOf<BeaconScanResult>()

    private fun flowEmitter() {
        coroutineScope.launch {
            while (running) {
                scanResults.forEach {
                    beaconFlow.emit(it)
                }
                delay(Defaults.DEFAULT_SCAN_PERIOD)
            }
        }
    }

    override fun startScanning() {
        if (running) {
            NSLog("Already scanning")
            return
        }
        if (regions.isEmpty()) {
            setRegions(listOf())
        }

        initJobs()

        NSLog("Starting scanning")

        running = true
        proximityObserver.startObservingZones(regions)
        flowEmitter()
    }

    private fun initJobs()  {
        if (!mainJob.isActive)
            {
                mainJob = SupervisorJob()
                coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)
            }
    }

    override fun stopScanning() {
        if (!running) {
            NSLog("Not scanning anyways")
            return
        }

        NSLog("Stopping scanning")

        running = false
        proximityObserver.stopObservingZones()
        mainJob.cancel()
    }

    override fun setRegions(regions: List<LokateBeacon>) {
        if (running) {
            NSLog("Already running")
            return
        }
        if (regions.isEmpty()) {
            NSLog("No regions to scan")
            return
        }
        NSLog("Setting regions")
        this.regions.clear()
        regions.forEach {
            this.regions.add(
                it.toProximityZone().also {
                    it.onExit = {
                        it?.tag?.let {
                            val (uuid, major, minor) = it.split(':')
                            scanResults.removeAll {
                                it.beaconUUID == uuid && it.major == major.toInt() && it.minor == minor.toInt()
                            }
                        }
                    }
                    it.onEnter = { zoneContext ->
                        zoneContext?.tag?.let {
                            scanResults.add(zoneContext.toBeaconScanResult())
                        }
                    }
                },
            )
        }
    }

    private fun LokateBeacon.toProximityZone() =
        EPXProximityZone(
            proximityUUID.uppercase() + ':' + major + ':' + minor,
            EPXProximityRange.customRangeWithDesiredMeanTriggerDistance(this.radius)
                ?: farRange,
        )

    private fun EPXProximityZoneContext.toBeaconScanResult(): BeaconScanResult {
        val (uuid, major, minor) = tag.split(':')
        return BeaconScanResult(
            beaconUUID = uuid,
            major = major.toInt(),
            minor = minor.toInt(),
            rssi = 0.0,
            txPower = 0,
            accuracy = 0.0,
            seen = getTimeMillis(),
        )
    }

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return beaconFlow
    }
}
