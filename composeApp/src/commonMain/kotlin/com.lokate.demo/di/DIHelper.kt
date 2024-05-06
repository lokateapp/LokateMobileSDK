package com.lokate.demo.di

import com.lokate.demo.market.MarketViewModel
import com.lokate.kmmsdk.LokateSDK
import com.lokate.kmmsdk.di.SDKSettings
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun lokateModule(scannerType: LokateSDK.BeaconScannerType) =
    module {
        single { LokateSDK.getInstance(scannerType) }
    }

fun viewModelModule() =
    module {
        single {
            MarketViewModel()
        }
    }

fun initKoin(scannerType: LokateSDK.BeaconScannerType) {
    SDKSettings.beaconScannerType = scannerType
    startKoin {
        modules(viewModelModule(), lokateModule(scannerType))
    }
}

fun startKoinIBeacon() {
    initKoin(LokateSDK.BeaconScannerType.IBeacon)
}

fun startKoinEstimote(
    appId: String,
    appToken: String,
) {
    initKoin(LokateSDK.BeaconScannerType.EstimoteMonitoring(appId, appToken))
}
