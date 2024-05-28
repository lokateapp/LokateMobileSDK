package com.lokate.kmmsdk

import android.util.Log
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials
import com.estimote.proximity_sdk.api.ProximityObserver
import com.estimote.proximity_sdk.api.ProximityObserverBuilder
import com.estimote.proximity_sdk.api.ProximityZone
import com.estimote.proximity_sdk.api.ProximityZoneBuilder
import com.estimote.proximity_sdk.api.ProximityZoneContext
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AndroidEstimoteBeaconScanner(appId: String, appToken: String) : BeaconScanner {
    private val cloudCredentials = EstimoteCloudCredentials(appId, appToken)
    private val proximityObserver =
        ProximityObserverBuilder(applicationContext, cloudCredentials)
            .withLowLatencyPowerMode()
            .withTelemetryReportingDisabled()
            .withEstimoteSecureMonitoringDisabled()
            .withAnalyticsReportingDisabled() // we will use our own analytics
            .onError { error ->
                Log.e("AndroidEstimoteBeaconScanner", "Error: ${error.message}")
                stopScanning()
            }
            .build()
    private var observationHandler: ProximityObserver.Handler? = null

    private var mainJob = SupervisorJob()
    private var coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)

    private var running: Boolean = false

    private val regions = mutableListOf<ProximityZone>()
    private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()

    private val scanResults = mutableSetOf<BeaconScanResult>()

    private fun initJobs() {
        if (!mainJob.isActive) {
            mainJob = SupervisorJob()
            coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)
        }
    }

    override fun startScanning() {
        if (running) {
            Log.e("AndroidEstimoteBeaconScanner", "Already scanning")
            return
        }
        if (regions.isEmpty()) {
            setRegions(listOf())
        }
        initJobs()
        Log.d("AndroidEstimoteBeaconScanner", "Starting scanning")

        observationHandler =
            with(proximityObserver) {
                running = true
                flowEmitter()
                startObserving(regions)
            }
    }

    override fun stopScanning() {
        if (!running) {
            Log.e("AndroidEstimoteBeaconScanner", "Not scanning anyways")
            return
        }
        observationHandler!!.stop()
        mainJob.cancel()
        running = false
    }

    override fun setRegions(regions: List<LokateBeacon>) {
        if (running) {
            Log.d("AndroidEstimoteBeaconScanner", "Already running!")
            return
        }
        if (regions.isEmpty()) {
            Log.d("AndroidEstimoteBeaconScanner", "No regions to scan")
            return
        }

        Log.d("AndroidEstimoteBeaconScanner", "Setting regions")

        this.regions.clear()
        this.regions.addAll(
            regions
                .map { beacon ->
                    ProximityZoneBuilder()
                        .forTag(beacon.proximityUUID.uppercase() + ':' + beacon.major + ':' + beacon.minor)
                        .inCustomRange(beacon.radius)
                        .onEnter { handleEnter(it) }
                        .onExit { handleExit(it) }
                        .build()
                },
        )
    }

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return beaconFlow
    }

    private fun flowEmitter() {
        coroutineScope.launch {
            while (true) {
                delay(Defaults.DEFAULT_SCAN_PERIOD)
                Log.i(
                    "AndroidEstimoteBeaconScanner",
                    "Beacons found: ${scanResults.map { it.minor }}",
                )
                scanResults.forEach {
                    beaconFlow.emit(it.copy(seen = System.currentTimeMillis()))
                }
            }
        }
    }

    private fun handleEnter(regionContext: ProximityZoneContext) {
        val (beaconUUID, major, minor) = regionContext.tag.split(":")

        scanResults.add(
            BeaconScanResult(
                beaconUUID = beaconUUID,
                major = major.toInt(),
                minor = minor.toInt(),
                rssi = 0.0, // not relevant (handled inside EstimoteSDK)
                txPower = 0, // not relevant (handled inside EstimoteSDK)
                accuracy = 0.0, // definitely inside proximity range, so passes the if check in LokateSDK
                seen = System.currentTimeMillis(),
            ),
        )
    }

    private fun handleExit(regionContext: ProximityZoneContext) {
        val (beaconUUID, major, minor) = regionContext.tag.split(":")

        scanResults.removeIf {
            it.beaconUUID.lowercase() == beaconUUID.lowercase() && it.major == major.toInt() && it.minor == minor.toInt()
        }
    }
}
