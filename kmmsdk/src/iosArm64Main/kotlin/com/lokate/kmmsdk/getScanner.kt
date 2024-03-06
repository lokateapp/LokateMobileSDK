package com.lokate.kmmsdk

/*
internal actual fun getScanner(): BeaconScanner {
    return IOSBeaconScanner()
}
 */

internal actual fun getScanner(): BeaconScanner2 {
    return IOSBeaconScanner2()
}