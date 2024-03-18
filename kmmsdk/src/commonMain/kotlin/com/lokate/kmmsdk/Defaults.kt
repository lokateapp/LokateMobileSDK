package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.BeaconProximity
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon

@Suppress("MagicNumber")
object Defaults {
    const val DEFAULT_TIMEOUT_BEFORE_GONE = 10000L
    const val GONE_CHECK_INTERVAL = 5000L
    const val BEACON_LAYOUT_IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25,i:0-56"
    val DEFAULT_BEACONS =
        listOf(
            LokateBeacon(
                // white
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                24719,
                28241,
                "1",
                BeaconProximity.Far.ordinal,
            ),
            LokateBeacon(
                // yellow
                "D5D885F1-D7DA-4F5A-AD51-487281B7F8B3",
                1,
                1,
                "2",
                BeaconProximity.Far.ordinal,
            ),
            LokateBeacon(
                // pink
                "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                1,
                1,
                "3",
                BeaconProximity.Far.ordinal,
            ),
            LokateBeacon(
                // red
                "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                1,
                2,
                "4",
                BeaconProximity.Far.ordinal,
            ),
        )
}
