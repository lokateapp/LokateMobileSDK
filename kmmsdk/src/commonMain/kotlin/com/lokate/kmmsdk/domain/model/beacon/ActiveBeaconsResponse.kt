package com.lokate.kmmsdk.domain.model.beacon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveBeacon(
    val uuid: String,
    val major: String,
    val minor: String,
    val radius: Double,
    val campaignName: String,
) {
    fun toLokateBeacon(): LokateBeacon =
        LokateBeacon(
            uuid = this.uuid,
            major = this.major.toIntOrNull() ?: 0,
            minor = minor.toIntOrNull() ?: 0,
            campaign = this.campaignName,
            radius = this.radius,
        )
}

fun LokateBeacon.toActiveBeacon(): ActiveBeacon {
    return ActiveBeacon(
        uuid = this.uuid,
        major = this.major.toString(),
        minor = this.minor.toString(),
        campaignName = this.campaign,
        radius = this.radius,
    )
}
