package com.lokate.kmmsdk.domain.model.beacon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveBeacon(
    val id: String,
    val userId: String,
    val branchId: String,
    val radius: Int,
    val name: String,
    val major: String,
    val minor: String,
    val campaign: Campaign,
    val range: BeaconProximity,
) {
    fun toLokateBeacon(): LokateBeacon =
        LokateBeacon(
            uuid = this.id,
            major = this.major.toIntOrNull() ?: 0,
            minor = minor.toIntOrNull() ?: 0,
            campaign = this.campaign.name,
            minProximity = this.range,
        )
}

@Serializable
data class Campaign(
    val id: String,
    val name: String,
    val userId: String,
    val status: String,
    @SerialName("createdAt") val createdAt: String,
)

@Serializable
enum class BeaconProximity {
    @SerialName("immediate")
    Immediate,

    @SerialName("near")
    Near,

    @SerialName("far")
    Far,

    @SerialName("unknown")
    Unknown,

    ;

    companion object {
        fun fromString(value: String): BeaconProximity =
            when (value.lowercase()) {
                "immediate" -> Immediate
                "near" -> Near
                "far" -> Far
                "unknown" -> Unknown
                else -> throw IllegalArgumentException("Unknown range: $value")
            }

        fun fromInt(value: Int): BeaconProximity =
            when (value) {
                0 -> Immediate
                1 -> Near
                2 -> Far
                else -> Unknown
            }
    }
}

fun LokateBeacon.toActiveBeacon(): ActiveBeacon {
    return ActiveBeacon(
        id = this.uuid,
        userId = "1",
        branchId = "1",
        radius = 1,
        name = "default",
        major = this.major.toString(),
        minor = this.minor.toString(),
        campaign = Campaign("1", "default", "1", "active", "2021-08-01T00:00:00.000Z"),
        range = this.minProximity,
    )
}
