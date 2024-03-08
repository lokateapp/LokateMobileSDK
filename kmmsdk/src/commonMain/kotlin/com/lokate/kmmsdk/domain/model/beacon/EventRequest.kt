package com.lokate.kmmsdk.domain.model.beacon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val customerId: String,
    val beaconUID: String,
    val major: String,
    val minor: String,
    val status: EventStatus,
    val timestamp: Long,
)

@Serializable
enum class EventStatus {
    @SerialName("ENTER")
    ENTER,

    @SerialName("EXIT")
    EXIT,

    @SerialName("STAY")
    STAY;
}
