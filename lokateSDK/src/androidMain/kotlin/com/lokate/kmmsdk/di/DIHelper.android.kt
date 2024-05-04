package com.lokate.kmmsdk.di

import com.lokate.kmmsdk.AndroidBeaconScanner
import com.lokate.kmmsdk.AndroidEstimoteBeaconScanner
import com.lokate.kmmsdk.BeaconScanner
import com.lokate.kmmsdk.LokateSDK
import org.koin.core.KoinApplication
import org.koin.dsl.module

actual fun getKoinApp(beaconScannerType: LokateSDK.BeaconScannerType): KoinApplication {
    val koinApp = KoinApplication.init()
    val scannerModule =
        module {
            single<BeaconScanner> {
                when (beaconScannerType) {
                    is LokateSDK.BeaconScannerType.IBeacon -> AndroidBeaconScanner()
                    is LokateSDK.BeaconScannerType.EstimoteMonitoring ->
                        AndroidEstimoteBeaconScanner(
                            beaconScannerType.appId,
                            beaconScannerType.appToken,
                        )
                }
            }
        }

    return koinApp.modules(scannerModule, lokateModule, dbModule, dataSourceModule, repositoryModule)
}
