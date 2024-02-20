package com.lokate.kmmsdk

/* internal actual fun getScanner(): BeaconScanner {
    return AndroidBeaconScanner()
}

 */

internal actual fun getScanner(): BeaconScanner2 {
    return AndroidBeaconScanner2()
}