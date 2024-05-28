package com.lokate.demo.di

import com.lokate.demo.BuildKonfig
import com.lokate.demo.game.GameViewModel
import com.lokate.demo.gym.GymViewModel
import com.lokate.demo.market.MarketViewModel
import com.lokate.demo.museum.MuseumViewModel
import com.lokate.demo.utils.getAudioPlayer
import com.lokate.kmmsdk.LokateSDK
import com.lokate.kmmsdk.di.SDKSettings
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun lokateModule(scannerType: LokateSDK.BeaconScannerType) =
    module {
        single {
            LokateSDK.getInstance(scannerType)
        }
    }

fun audioPlayerModule() =
    module {
        single {
            getAudioPlayer()
        }
    }

fun viewModelModule() =
    module {
        factory {
            MarketViewModel()
        }
        factory {
            MuseumViewModel()
        }
        factory {
            GymViewModel()
        }
        factory {
            GameViewModel()
        }
    }

fun initKoin(scannerType: LokateSDK.BeaconScannerType) {
    SDKSettings.beaconScannerType = scannerType
    startKoin {
        modules(audioPlayerModule(), viewModelModule(), lokateModule(scannerType))
    }
}

fun startKoinIBeacon() {
    initKoin(LokateSDK.BeaconScannerType.IBeacon)
}

fun startKoinEstimote() {
    val appId = BuildKonfig.ESTIMOTE_CLOUD_APP_ID
    val appToken = BuildKonfig.ESTIMOTE_CLOUD_APP_TOKEN
    initKoin(LokateSDK.BeaconScannerType.EstimoteMonitoring(appId, appToken))
}
