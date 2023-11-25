package com.lokate.kmmsdk.data.datasource.interfaces.beacon

import com.lokate.kmmsdk.data.datasource.DSResult

interface BeaconDS {
    suspend fun getBeacons(): DSResult
}