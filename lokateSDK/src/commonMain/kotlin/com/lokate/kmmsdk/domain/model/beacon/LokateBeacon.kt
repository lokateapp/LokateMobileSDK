package com.lokate.kmmsdk.domain.model.beacon

import kotlinx.serialization.Serializable

@Serializable
data class LokateBeacon(
    val proximityUUID: String,
    val major: Int?,
    val minor: Int?,
    val campaignName: String,
    val radius: Double,
)
