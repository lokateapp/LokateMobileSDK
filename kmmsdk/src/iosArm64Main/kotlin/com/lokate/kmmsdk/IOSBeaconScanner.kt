package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.beacon.BeaconScanner
import com.lokate.kmmsdk.domain.beacon.CFlow
import com.lokate.kmmsdk.domain.beacon.wrap
import com.lokate.kmmsdk.domain.model.beacon.Beacon
import com.lokate.kmmsdk.domain.model.beacon.BeaconProximity
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.utils.extension.emptyString
import kotlinx.coroutines.flow.MutableSharedFlow
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconRegion
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.CLProximity
import platform.Foundation.NSError
import platform.Foundation.NSUUID
import platform.darwin.NSObject

class IOSBeaconScanner : BeaconScanner {

    enum class AuthorizationStatus(id: Int) {
        NOT_DETERMINED(0),
        RESTRICTED(1),
        DENIED(2),
        AUTHORIZED_ALWAYS(3),
        AUTHORIZED_WHEN_IN_USE(4)
    }

    object DefaultBeacons {
        val beacons = listOf(
            Beacon(
                "1",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                24719,
                65453
            ),//white
            Beacon(
                "2",
                "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
                1,
                1
            )//pink
            ,
            Beacon(
                "3",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
                24719,
                28241
            ),//red
            Beacon(
                "4",
                "25E296BD-A76C-4013-8E90-4898977A6E1B",
                24719,
                11975
            )
        )//yellow)
    }

    internal class IOSBeaconScannerHandler : NSObject(), CLLocationManagerDelegateProtocol{

        private val _errorObservable = MutableSharedFlow<Exception>()
        private val _beaconScanResultObservable = MutableSharedFlow<List<BeaconScanResult>>()
        private val regions = mutableListOf<Beacon>()

        private val manager: CLLocationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CLLocationManager()
        }

        init {
            manager.delegate = this
            manager.allowsBackgroundLocationUpdates = true
        }

        private fun startRangingBeacons() {
            if (regions.isEmpty())
                regions.addAll(DefaultBeacons.beacons)
            regions.forEach {
                manager.startRangingBeaconsInRegion(it.toCLBeaconRegion())
            }
        }

        private fun Beacon.toCLBeaconRegion(): CLBeaconRegion {
            return CLBeaconRegion(
                uUID = NSUUID(this.uuid),
                major = this.major.toUShort(),
                minor = this.minor.toUShort(),
                identifier = emptyString()
            )
        }

        private fun stopRangingBeacons(beacons: List<Beacon>) {
            beacons.forEach {
                manager.stopRangingBeaconsInRegion(it.toCLBeaconRegion())
            }
        }

        override fun locationManager(
            manager: CLLocationManager,
            rangingBeaconsDidFailForRegion: CLBeaconRegion,
            withError: NSError
        ) {
            println("rangingBeaconsDidFailForRegion")
        }

        private fun CLBeacon.toBeaconScanResult(): BeaconScanResult {
            return BeaconScanResult(
                Beacon(
                    emptyString(),
                    this.proximityUUID.UUIDString,
                    this.major.intValue,
                    this.minor.intValue
                ),
                this.rssi.toDouble(),
                0,
                0.0,
                this.proximity.toLokateProximity()
            )
        }

        private fun CLProximity.toLokateProximity(): BeaconProximity {
            return when (this) {
                CLProximity.CLProximityUnknown -> BeaconProximity.Unknown
                CLProximity.CLProximityImmediate -> BeaconProximity.Immediate
                CLProximity.CLProximityNear -> BeaconProximity.Near
                CLProximity.CLProximityFar -> BeaconProximity.Far
                else -> BeaconProximity.Unknown
            }
        }

        override fun locationManager(
            manager: CLLocationManager,
            didRangeBeacons: List<*>,
            inRegion: CLBeaconRegion
        ) {
            didRangeBeacons.forEach {
                with((it as CLBeacon).toBeaconScanResult()) {
                    println(this)
                }
            }
        }

        private fun startRangingForRegions() {
            startRangingBeacons()
        }

        private fun startRegionOrAskPermissions() {
            when (manager.authorizationStatus) {
                AuthorizationStatus.NOT_DETERMINED.ordinal -> manager.requestWhenInUseAuthorization()
                AuthorizationStatus.AUTHORIZED_WHEN_IN_USE.ordinal, AuthorizationStatus.AUTHORIZED_ALWAYS.ordinal -> startRangingForRegions()
                AuthorizationStatus.DENIED.ordinal, AuthorizationStatus.RESTRICTED.ordinal -> _errorObservable.tryEmit(
                    Exception("Localization permission denied")
                )

                else -> manager.requestLocation()
            }
        }

        fun setScanPeriod(scanPeriodMillis: Long) {
            throw UnsupportedOperationException("Cannot set scan period on iOS")
        }

        fun setBetweenScanPeriod(betweenScanPeriod: Long) {
            throw UnsupportedOperationException("Cannot set between scan period on iOS")
        }

        fun observeResuls(): CFlow<List<BeaconScanResult>> {
            return _beaconScanResultObservable.wrap()
        }

        fun observeNonBeaconResults(): CFlow<List<BeaconScanResult>> {
            throw UnsupportedOperationException("Not planning to implement this")
        }

        fun setIosRegions(regions: List<Beacon>) {
            this.regions.clear()
            this.regions.addAll(regions)
        }

        fun setAndroidRegions(region: List<Beacon>) {
            throw UnsupportedOperationException("set iOS Region on iOS")
        }

        fun setRssiThreshold(threshold: Int) {
            throw UnsupportedOperationException("Cannot set RSSI threshold on iOS")
        }

        fun observeErrors(): CFlow<Exception> {
            return _errorObservable.wrap()
        }

        fun start() {
            startRegionOrAskPermissions()
        }

        private fun stopRanging() {
            stopRangingBeacons(regions)
        }

        fun stop() {
            stopRanging()
        }

    }

    private val beaconRegions = mutableListOf<Beacon>()

    companion object {
        private val handler: IOSBeaconScannerHandler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            IOSBeaconScannerHandler()
        }
    }

    init {
        //handler.setBetweenScanPeriod(Defauls.DEFAULT_PERIOD_BETWEEEN_SCAN)
        //handler.setScanPeriod(Defauls.DEFAULT_PERIOD_SCAN)
    }

    override fun setScanPeriod(scanPeriodMillis: Long) {
        //handler.setScanPeriod(scanPeriodMillis)
    }

    override fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        //handler.setBetweenScanPeriod(betweenScanPeriod)
    }

    override fun observeResuls(): CFlow<List<BeaconScanResult>> {
        return handler.observeResuls()
    }

    override fun observeNonBeaconResults(): CFlow<List<BeaconScanResult>> {
        return handler.observeNonBeaconResults()
    }

    override fun setIosRegions(regions: List<Beacon>) {
        handler.setIosRegions(regions)
    }

    override fun setAndroidRegions(region: List<Beacon>) {
        handler.setAndroidRegions(region)
    }

    override fun setRssiThreshold(threshold: Int) {
        handler.setRssiThreshold(threshold)
    }

    override fun observeErrors(): CFlow<Exception> {
        return handler.observeErrors()
    }

    override fun start() {
        handler.start()
    }

    override fun stop() {
        handler.stop()
    }
}