package com.lokate.kmmsdk.di

import com.lokate.kmmsdk.Database
import com.lokate.kmmsdk.LokateSDK
import com.lokate.kmmsdk.data.datasource.local.Settings
import com.lokate.kmmsdk.data.datasource.local.authentication.AuthenticationLocalDS
import com.lokate.kmmsdk.data.datasource.local.beacon.BeaconLocalDS
import com.lokate.kmmsdk.data.datasource.local.factory.getDatabase
import com.lokate.kmmsdk.data.datasource.remote.authentication.AuthenticationAPI
import com.lokate.kmmsdk.data.datasource.remote.authentication.AuthenticationRemoteDS
import com.lokate.kmmsdk.data.datasource.remote.beacon.BeaconAPI
import com.lokate.kmmsdk.data.datasource.remote.beacon.BeaconRemoteDS
import com.lokate.kmmsdk.data.repository.AuthenticationRepositoryImpl
import com.lokate.kmmsdk.data.repository.BeaconRepositoryImpl
import com.lokate.kmmsdk.domain.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.repository.BeaconRepository
import org.koin.dsl.module

val dbModule = module {
    single<Database>{
        getDatabase()
    }
}

val dataSourceModule = module {
    single<AuthenticationLocalDS> {
        AuthenticationLocalDS(authenticationSettings = Settings.authenticationSettings)
    }
    single <BeaconLocalDS> {
        BeaconLocalDS(get())
    }
    single <AuthenticationLocalDS> {
        AuthenticationLocalDS(authenticationSettings = Settings.authenticationSettings)
    }
    single <AuthenticationAPI>{
        AuthenticationAPI()
    }
    single<AuthenticationRemoteDS> {
        AuthenticationRemoteDS(get())
    }
    single<BeaconAPI> {
        BeaconAPI()
    }
    single<BeaconRemoteDS> {
        BeaconRemoteDS(get())
    }
}

val repositoryModule = module {
    single <AuthenticationRepository>{
        AuthenticationRepositoryImpl(get(), get())
    }
    single<BeaconRepository> {
        BeaconRepositoryImpl(get(), get(), get())
    }
}

expect fun initKoin(beaconScannerType: LokateSDK.BeaconScannerType)