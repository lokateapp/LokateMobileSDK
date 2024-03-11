package com.lokate.kmmsdk.domain.model.beacon

import kotlinx.serialization.Serializable

@Serializable
data class ActiveBeacon(
    val proximityUUID: String,
    val major: Int,
    val minor: Int,
    val radius: Double,
    val campaignName: String,
) {
    fun toLokateBeacon(): LokateBeacon =
        LokateBeacon(
            uuid = this.proximityUUID,
            major = this.major,
            minor = this.minor,
            campaign = this.campaignName,
            radius = this.radius,
        )
}

fun LokateBeacon.toActiveBeacon(): ActiveBeacon {
    return ActiveBeacon(
        proximityUUID = this.uuid,
        major = this.major ?: 0,
        minor = this.minor ?: 0,
        campaignName = this.campaign,
        radius = this.radius,
    )
}
