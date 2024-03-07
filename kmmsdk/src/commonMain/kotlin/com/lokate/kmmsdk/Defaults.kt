package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon

@Suppress("MagicNumber")
object Defaults {
    const val DEFAULT_TIMEOUT_BEFORE_GONE = 3000L
    const val BEACON_LAYOUT_IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25,i:0-56"
    val DEFAULT_BEACONS =
        listOf(
            LokateBeacon(
                // ?
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                24719,
                65453,
                "1",
            ),
            LokateBeacon(
                // ?
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                24719,
                28241,
                "2",
            ),
            LokateBeacon(
                // pink
                "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                1,
                1,
                "3",
            ),
            LokateBeacon(
                // red
                "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                1,
                2,
                "4",
            ),
        )
}
