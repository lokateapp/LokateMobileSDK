package com.lokate.kmmsdk.domain.model.beacon

import kotlinx.serialization.Serializable

@Serializable
data class ActiveBeacon(
    val proximityUUID: String,
    val major: Int,
    val minor: Int,
    val radius: Double,
    val campaignName: String,
)

fun ActiveBeacon.toLokateBeacon(): LokateBeacon {
    return LokateBeacon(
        uuid = this.proximityUUID,
        major = this.major,
        minor = this.minor,
        radius = radius,
        campaign = this.campaignName,
    )
}
