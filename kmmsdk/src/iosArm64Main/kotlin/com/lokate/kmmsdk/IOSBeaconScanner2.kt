package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconRegion
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSLog
import platform.darwin.NSObject

class IOSBeaconScanner2 : BeaconScanner2 {

    internal class iOSBeaconScannerHelper : NSObject(), CLLocationManagerDelegateProtocol {

        private val manager: CLLocationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CLLocationManager()
        }
        private val mainJob = SupervisorJob()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)

        private var regions: List<CLBeaconRegion> = listOf()
        private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()
        private var running: Boolean = false

        init {
            manager.delegate = this
            manager.requestAlwaysAuthorization()
            manager.allowsBackgroundLocationUpdates = true
        }

        fun startScanning() {
            if (running) {
                NSLog("Already running")
                return
            }
            if (regions.isEmpty()) {
                NSLog("No regions to scan1")
                setRegions(listOf())
                NSLog("added default regions")
                //return
            }

            regions.forEach {
                NSLog("Starting ranging for region: $it")
                manager.startRangingBeaconsInRegion(it)
            }
            running = true
        }

        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            NSLog("locationManagerDidChangeAuthorization called")
            NSLog("locationManagerDidChangeAuthorization ${manager.authorizationStatus}")
            startScanning()
        }

        override fun locationManager(
            manager: CLLocationManager,
            didChangeAuthorizationStatus: Int
        ) {
            NSLog("locationManager didChangeAuthorizationStatus: Status - $didChangeAuthorizationStatus")
            when (didChangeAuthorizationStatus) {
                NOT_DETERMINED -> {
                    NSLog("Requesting location permissions")
                    manager.requestAlwaysAuthorization()
                }

                AUTHORIZED_WHEN_IN_USE, AUTHORIZED_ALWAYS -> {
                    NSLog("Permissions granted, starting ranging for regions")
                    startScanning()
                }

                DENIED, RESTRICTED -> {
                    NSLog("Localization permission denied")
                }

                else -> {
                    NSLog("Requesting location")
                    manager.requestLocation()
                }
            }
        }


        override fun locationManager(
            manager: CLLocationManager,
            didRangeBeacons: List<*>,
            inRegion: CLBeaconRegion
        ) {
            didRangeBeacons.forEach {
                with((it as CLBeacon).toBeaconScanResult()) {
                    coroutineScope.launch {
                        NSLog("Emitting beacon result eeeee ${this@with}")
                        beaconFlow.emit(this@with)
                    }
                }
            }
        }

        fun stopScanning() {
            if (!running) {
                NSLog("Not running")
                return
            }
            regions.forEach {
                manager.stopRangingBeaconsInRegion(it)
            }
            running = false
        }

        fun setRegions(regions: List<LokateBeacon>) {
            if (regions.isEmpty()) {
                this.regions = listOf(
                    LokateBeacon(
                        "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                        24719,
                        65453,
                        ""
                    ),//white
                    LokateBeacon(

                        "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                        1,
                        1,
                        "2",
                    )//pink
                    ,
                    LokateBeacon(
                        "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                        24719,
                        28241,
                        "3",
                    ),//White
                    LokateBeacon(
                        "D5D885F1-D7DA-4F5A-AD51-487281B7F8B3",
                        1,
                        1,
                        "3"
                    )
                ).map { it.toCLBeaconRegion() }//yellow)
                //NSLog("No regions to scan")
                //return
            } else this.regions = regions.map {
                it.toCLBeaconRegion()
            }
        }

        fun scanResultFlow(): Flow<BeaconScanResult> {
            return beaconFlow
        }
    }

    companion object {
        private val helper: iOSBeaconScannerHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            iOSBeaconScannerHelper()
        }
    }

    override fun startScanning() {
        helper.startScanning()
    }

    override fun stopScanning() {
        helper.stopScanning()
    }

    override fun setRegions(regions: List<LokateBeacon>) {
        helper.setRegions(regions)
    }

    @Suppress("unused")
    fun setRegions() {
        helper.setRegions(listOf())
    }

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return helper.scanResultFlow()
    }
}