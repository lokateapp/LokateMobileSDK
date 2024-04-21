package com.lokate.kmmsdk

actual fun getBeaconScanner(scannerType: LokateSDK.BeaconScannerType): BeaconScanner {
    return when (scannerType) {
        is LokateSDK.BeaconScannerType.IBeacon -> AndroidBeaconScanner()
        is LokateSDK.BeaconScannerType.EstimoteMonitoring -> AndroidEstimoteBeaconScanner(scannerType.appId, scannerType.appToken)
    }
}
