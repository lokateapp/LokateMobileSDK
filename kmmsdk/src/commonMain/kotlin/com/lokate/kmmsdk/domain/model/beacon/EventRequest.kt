package com.lokate.kmmsdk.domain.model.beacon

data class EventRequest(
    val customerId: String,
    val beaconUID: String,
    val status: String,
    val timestamp: Long
)
