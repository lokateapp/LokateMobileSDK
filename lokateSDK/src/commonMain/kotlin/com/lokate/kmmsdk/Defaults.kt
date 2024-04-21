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
                // white
                proximityUUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                major = 24719,
                minor = 65453,
                campaign = "white",
                radius = 100.0,
            ),
            LokateBeacon(
                // yellow
                proximityUUID = "D5D885F1-D7DA-4F5A-AD51-487281B7F8B3",
                major = 1,
                minor = 1,
                campaign = "yellow",
                radius = 100.0,
            ),
            LokateBeacon(
                // pink
                proximityUUID = "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                major = 1,
                minor = 1,
                campaign = "pink",
                radius = 0.5,
            ),
            LokateBeacon(
                // red
                proximityUUID = "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                major = 1,
                minor = 2,
                campaign = "red",
                radius = 100.0,
            ),
        )
}
