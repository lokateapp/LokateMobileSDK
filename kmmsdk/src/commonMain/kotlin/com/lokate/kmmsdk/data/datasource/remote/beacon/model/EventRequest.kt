package com.lokate.kmmsdk.data.datasource.remote.beacon.model

data class EventRequest(
    val customerId: String,
    val beaconUID: String,
    val status: String,
    val timestamp: Long
)
