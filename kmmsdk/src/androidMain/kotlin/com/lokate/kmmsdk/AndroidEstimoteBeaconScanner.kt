package com.lokate.kmmsdk

import android.util.Log
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials
import com.estimote.proximity_sdk.api.ProximityObserver
import com.estimote.proximity_sdk.api.ProximityObserverBuilder
import com.estimote.proximity_sdk.api.ProximityZone
import com.estimote.proximity_sdk.api.ProximityZoneBuilder
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class AndroidEstimoteBeaconScanner(appId: String, appToken: String) : BeaconScanner {
    private val cloudCredentials = EstimoteCloudCredentials(appId, appToken)
    private val proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
        .withLowLatencyPowerMode()
        .withTelemetryReportingDisabled()
        .withEstimoteSecureMonitoringDisabled()
        .onError { error -> Log.e("AndroidEstimoteBeaconScanner", "Error: ${error.message}") }
        .build()

    private var running: Boolean = false
    private val regions = mutableListOf<ProximityZone>()    // TODO: decide common naming for different libraries: region or zone
    private var observationHandler: ProximityObserver.Handler? = null
    private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()
    override fun startScanning() {
        Log.d("AndroidEstimoteBeaconScanner", "starting scanning")
        if (running) {
            Log.e("AndroidEstimoteBeaconScanner", "Already scanning")
            return
        }

        if (regions.isEmpty()) {
            setRegions(listOf())
        }

        Log.e("AndroidEstimoteBeaconScanner", "Starting scanning")

        observationHandler = proximityObserver.startObserving(this.regions)
    }

    override fun stopScanning() {
        if (running) {
            observationHandler!!.stop()
        }
    }

    override fun setRegions(regions: List<LokateBeacon>) {
        Log.d("AndroidEstimoteBeaconScanner", "setting regions")
        if (running) {
            Log.d("AndroidEstimoteBeaconScanner", "Already running!")
            return
        }
        if (regions.isEmpty()) {
            Log.d("AndroidEstimoteBeaconScanner", "No regions to scan")
            return
        }
        this.regions.clear()
        // this.regions.addAll(regions.map {
        //     ProximityZoneBuilder()
        //         // .forTag(it.campaign!!)
        //         .forTag("test1")
        //         // .inCustomRange(it.proximityRange!!.toDouble())
        //         .inCustomRange(3.5)
        //         .onEnter { Log.d("AndroidEstimoteBeaconScanner" , "Enter test1") }
        //         .onExit { Log.d("AndroidEstimoteBeaconScanner", "Exit test1") }
        //         .build()
        // })

        this.regions.add(
            ProximityZoneBuilder()
                .forTag("test1").inCustomRange(1.0)
                .onEnter { Log.d("AndroidEstimoteBeaconScanner" , "Enter test1") }
                .onExit { Log.d("AndroidEstimoteBeaconScanner", "Exit test1") }
                .build())
        this.regions.add(
            ProximityZoneBuilder()
                .forTag("test2").inCustomRange(1.0)
                .onEnter { Log.d("AndroidEstimoteBeaconScanner" , "Enter test2") }
                .onExit {
                    Log.d("AndroidEstimoteBeaconScanner", "Exit test2") }
                .build())
    }

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return beaconFlow
    }
}