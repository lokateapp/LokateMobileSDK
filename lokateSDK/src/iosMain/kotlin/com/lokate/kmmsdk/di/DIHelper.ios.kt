package com.lokate.kmmsdk.di

import com.lokate.kmmsdk.BeaconScanner
import com.lokate.kmmsdk.IOSBeaconScanner
import com.lokate.kmmsdk.IOSEstimoteBeaconScanner
import com.lokate.kmmsdk.LocationManagerDelegate
import com.lokate.kmmsdk.LokateSDK
import com.lokate.kmmsdk.SharedCLLocationManager
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSLog

actual fun getKoinApp(beaconScannerType: LokateSDK.BeaconScannerType): KoinApplication {
    val koinApp = KoinApplication.init()
    val sharedCLLocationManagerModule =
        module {
            single<SharedCLLocationManager> {
                val manager = CLLocationManager()
                val delegate = LocationManagerDelegate() // Strongly retain the delegate here
                with(manager) {
                    this.delegate = delegate
                    this.requestAlwaysAuthorization()
                    this.allowsBackgroundLocationUpdates = true
                    this.desiredAccuracy = kCLLocationAccuracyBest
                }
                NSLog(manager.toString())
                NSLog(manager.delegate.toString())
                SharedCLLocationManager(manager).apply {
                    this.storeDelegate(delegate)
                }
            }
        }
    val scannerModule =
        module {
            single<BeaconScanner> {
                when (beaconScannerType) {
                    is LokateSDK.BeaconScannerType.IBeacon -> IOSBeaconScanner()
                    is LokateSDK.BeaconScannerType.EstimoteMonitoring ->
                        IOSEstimoteBeaconScanner(
                            beaconScannerType.appId,
                            beaconScannerType.appToken,
                        )
                }
            }
        }

    return koinApp.modules(sharedCLLocationManagerModule, scannerModule, dbModule, dataSourceModule, repositoryModule)
}
