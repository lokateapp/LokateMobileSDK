package com.lokate.kmmsdk.domain.model.beacon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveBeacon(
    val id: String,
    val userId: String,
    val branchId: String,
    val radius:  Int,
    val name: String,
    val major: String,
    val minor: String,
    val campaign: Campaign,
    val range: BeaconRange
)

@Serializable
data class Campaign(
    val id: String,
    val name: String,
    val userId: String,
    val status: String,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
enum class BeaconRange {
    @SerialName("immediate") IMMEDIATE,
    @SerialName("near") NEAR,
    @SerialName("far") FAR,
    @SerialName("unknown") UNKNOWN;

    companion object {
        fun fromString(value: String): BeaconRange = when(value.lowercase()) {
            "immediate" -> IMMEDIATE
            "near" -> NEAR
            "far" -> FAR
            "unknown" -> UNKNOWN
            else -> throw IllegalArgumentException("Unknown range: $value")
        }

        fun fromInt(value: Int): BeaconRange = when(value) {
            0 -> IMMEDIATE
            1 -> NEAR
            2 -> FAR
            else -> UNKNOWN
        }
    }
}