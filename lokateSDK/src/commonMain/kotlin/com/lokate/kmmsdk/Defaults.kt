package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon

@Suppress("MagicNumber")
object Defaults {
    const val MAXIMUM_ELEMENTS_IN_SCAN_EVENT_PIPELINE = 100
    const val EVENT_REQUEST_TIMEOUT = 5000L
    const val DEFAULT_TIMEOUT_BEFORE_GONE = 10000L
    const val GONE_CHECK_INTERVAL = 5000L
    const val DEFAULT_SCAN_PERIOD = 1000L
    const val BEACON_LAYOUT_IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25,i:0-56"
    val DEFAULT_BEACONS =
        listOf(
            LokateBeacon(
                // pink
                proximityUUID = "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                major = 1,
                minor = 1,
                campaignName = "pink",
                radius = 1.5,
            ),
            LokateBeacon(
                // red
                proximityUUID = "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                major = 1,
                minor = 2,
                campaignName = "red",
                radius = 1.5,
            ),
            LokateBeacon(
                // white
                proximityUUID = "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                major = 1,
                minor = 3,
                campaignName = "white",
                radius = 1.5,
            ),
            LokateBeacon(
                // yellow
                proximityUUID = "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                major = 1,
                minor = 4,
                campaignName = "yellow",
                radius = 1.5,
            ),
        )
}
