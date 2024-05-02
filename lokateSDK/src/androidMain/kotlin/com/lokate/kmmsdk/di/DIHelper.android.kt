package com.lokate.kmmsdk.di

import com.lokate.kmmsdk.AndroidBeaconScanner
import com.lokate.kmmsdk.AndroidEstimoteBeaconScanner
import com.lokate.kmmsdk.BeaconScanner
import com.lokate.kmmsdk.LokateSDK
import org.koin.core.context.startKoin
import org.koin.dsl.module

actual fun initKoin(beaconScannerType: LokateSDK.BeaconScannerType) {
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
    startKoin {
        modules(dbModule, dataSourceModule, repositoryModule, scannerModule)
    }
}
