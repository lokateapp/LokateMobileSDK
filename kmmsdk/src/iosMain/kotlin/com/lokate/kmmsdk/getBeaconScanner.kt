package com.lokate.kmmsdk

actual fun getBeaconScanner(scannerType: LokateSDK.BeaconScannerType): BeaconScanner {
    return when (scannerType) {
        is LokateSDK.BeaconScannerType.IBeacon -> IOSBeaconScanner()
        is LokateSDK.BeaconScannerType.EstimoteMonitoring -> IOSEstimoteBeaconScanner(scannerType.appId, scannerType.appToken)
    }
}
