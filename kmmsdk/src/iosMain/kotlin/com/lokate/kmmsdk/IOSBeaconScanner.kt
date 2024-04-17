package com.lokate.kmmsdk

import com.lokate.kmmsdk.SharedCLLocationManager.manager
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.utils.AUTHORIZED_ALWAYS
import com.lokate.kmmsdk.utils.AUTHORIZED_WHEN_IN_USE
import com.lokate.kmmsdk.utils.DENIED
import com.lokate.kmmsdk.utils.NOT_DETERMINED
import com.lokate.kmmsdk.utils.RESTRICTED
import com.lokate.kmmsdk.utils.toBeaconScanResult
import com.lokate.kmmsdk.utils.toCLBeaconRegion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconRegion
import platform.Foundation.NSLog

class IOSBeaconScanner : BeaconScanner {
    internal class IOSBeaconScannerHelper {
        private val mainJob = SupervisorJob()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)

        private var regions: List<CLBeaconRegion> = listOf()
        private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()
        private var running: Boolean = false

        fun startScanning() {
            if (running) {
                NSLog("Already running")
                return
            }
            if (regions.isEmpty()) {
                NSLog("No regions to scan1")
                return
            }

            regions.forEach {
                NSLog("Starting ranging for region: $it")
                manager.startRangingBeaconsInRegion(it)
            }
            running = true
        }

        init {
            SharedCLLocationManager.setAuthorizationStatusListener(::authorizationListener)
            SharedCLLocationManager.setBeaconRangeListener(::beaconRegionListener)
        }

        private fun authorizationListener(
            didChangeAuthorizationStatus: Int,
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

        private fun beaconRegionListener(
            didRangeBeacons: List<*>,
        ) {
            didRangeBeacons.forEach {
                with((it as CLBeacon).toBeaconScanResult()) {
                    coroutineScope.launch {
                        // NSLog("Emitting beacon result eeeee ${this@with}")
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
                NSLog("No regions to scan")
                return
            } else {
                this.regions =
                    regions.map {
                        it.toCLBeaconRegion()
                    }
            }
        }

        fun scanResultFlow(): Flow<BeaconScanResult> {
            return beaconFlow
        }
    }

    companion object {
        private val helper: IOSBeaconScannerHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            IOSBeaconScannerHelper()
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

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return helper.scanResultFlow()
    }
}
