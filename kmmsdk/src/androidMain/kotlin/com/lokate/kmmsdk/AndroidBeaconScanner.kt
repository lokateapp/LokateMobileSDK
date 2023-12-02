package com.lokate.kmmsdk

import android.annotation.SuppressLint
import android.util.Log
import com.lokate.kmmsdk.domain.beacon.BeaconScanner
import com.lokate.kmmsdk.domain.beacon.CFlow
import com.lokate.kmmsdk.domain.beacon.Defauls.BEACON_LAYOUT_IBEACON
import com.lokate.kmmsdk.domain.beacon.Defauls.DEFAULT_PERIOD_BETWEEEN_SCAN
import com.lokate.kmmsdk.domain.beacon.Defauls.DEFAULT_PERIOD_SCAN
import com.lokate.kmmsdk.domain.beacon.wrap
import com.lokate.kmmsdk.domain.model.beacon.Beacon
import com.lokate.kmmsdk.domain.model.beacon.BeaconProximity
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.utils.extension.emptyString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region
import kotlin.math.pow

class AndroidBeaconScanner : BeaconScanner {

    private val scanBeaconFlow: MutableSharedFlow<List<BeaconScanResult>> =
        MutableSharedFlow()
    private val scanNonBeaconFlow: MutableSharedFlow<List<BeaconScanResult>> =
        MutableSharedFlow()

    private var isScanning = false
    private var scanPeriodMillis: Long = DEFAULT_PERIOD_SCAN
    private var betweenScanPeriod: Long = DEFAULT_PERIOD_BETWEEEN_SCAN

    private val lastScannedBeacons = mutableListOf<BeaconScanResult>()
    private val lastScannedNonBeacons = mutableListOf<BeaconScanResult>()

    private val beaconRegions = mutableListOf<Region>()

    companion object {
        private val manager: BeaconManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BeaconManager.getInstanceForApplication(applicationContext).apply {
                beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
                setEnableScheduledScanJobs(false)
                isRegionStatePersistenceEnabled = false
            }
        }
    }

    init {
        setScanPeriod(scanPeriodMillis)
        setBetweenScanPeriod(betweenScanPeriod)
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var beaconEmitJob: Job? = null
    private var nonBeaconEmitJob: Job? = null

    private var rssiThreshold: Int? = null


    override fun setScanPeriod(scanPeriodMillis: Long) {
        this.scanPeriodMillis = scanPeriodMillis
    }

    override fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        this.betweenScanPeriod = betweenScanPeriod
    }

    override fun observeResuls(): CFlow<List<BeaconScanResult>> {
        return scanBeaconFlow.wrap()
    }

    override fun observeNonBeaconResults(): CFlow<List<BeaconScanResult>> {
        TODO("Not planning to implement this")
    }

    override fun setIosRegions(regions: List<Beacon>) {
        throw UnsupportedOperationException("set Android Region on Android")
    }

    override fun setAndroidRegions(region: List<Beacon>) {
        beaconRegions.clear()
        beaconRegions.addAll(region.map {
            Region(
                it.uuid,
                Identifier.parse(it.uuid),
                Identifier.parse(it.major.toString()),
                Identifier.parse(it.minor.toString())
            )
        })
    }

    override fun setRssiThreshold(threshold: Int) {
        rssiThreshold = threshold
    }

    override fun observeErrors(): CFlow<Exception> {
        TODO("Not planning to implement this")
    }

    private fun startEmittingBeaconsJob() {
        beaconEmitJob?.cancel()
        beaconEmitJob = scope.launch {
            var lastRegionEnteredBeacon: BeaconScanResult? = null
            var currentRegionEnteredBeacon: BeaconScanResult? = null
            while (isScanning) {

                scanBeaconFlow.emit(lastScannedBeacons.toList())
                currentRegionEnteredBeacon = lastScannedBeacons.maxByOrNull { it.rssi }
                if (lastRegionEnteredBeacon != null) {
                    if (currentRegionEnteredBeacon != lastRegionEnteredBeacon) {
                        Log.d("BeaconScanner", "Region changed: $currentRegionEnteredBeacon")
                        //fire region exit event
                        //fire region enter event
                    }
                }
                lastRegionEnteredBeacon = currentRegionEnteredBeacon
                lastScannedBeacons.clear()
                kotlinx.coroutines.delay(scanPeriodMillis)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun start() {
        if (isScanning)
            stop()

        isScanning = true
        Log.d("BeaconScanner", "start scanning")
        startEmittingBeaconsJob()
        //startEmittingNonBeaconsJob()

        provideDefaultRegionIfNecessary()
        with(manager) {
            removeAllRangeNotifiers()

            beaconRegions.forEach { region ->

                addMonitorNotifier(object : MonitorNotifier {
                    override fun didEnterRegion(region: Region) {
                        Log.d("BeaconScanner", "didEnterRegion: $region")
                    }

                    override fun didExitRegion(region: Region) {
                        Log.d("BeaconScanner", "didExitRegion: $region")
                    }

                    override fun didDetermineStateForRegion(state: Int, region: Region) {
                        Log.d("BeaconScanner", "didDetermineStateForRegion: $region")
                    }
                })

                addRangeNotifier { beacons, a ->
                    beacons.forEach {
                        it.distance
                    }
                    if (rssiThreshold == null) {
                        lastScannedBeacons.addAll(beacons.map {
                            it.toBeaconScanResult()
                        })
                    } else {
                        lastScannedBeacons.addAll(beacons.filter { it.rssi >= rssiThreshold!! }
                            .map { it.toBeaconScanResult() })
                    }
                }

                //startRangingBeacons(region)
                startMonitoring(region)
                startRangingBeacons(region)
            }
        }
    }

    private fun provideDefaultRegionIfNecessary() {
        if (beaconRegions.isEmpty()) {
            beaconRegions.addAll(
                listOf(Region("all-beacons-region", null, null, null))
            )
            setAndroidRegions(
                listOf(
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
                    )//yellow
                )
            )
        }
    }

    private fun org.altbeacon.beacon.Beacon.toBeaconScanResult(): BeaconScanResult {
        val beacon = Beacon(
            emptyString(),
            id1.toString(),
            id2.toInt(),
            id3.toInt()
        )
        val accuracy = calculateAccuracy(txPower, rssi.toDouble())
        val proximity = calculateProximity(accuracy)
        return BeaconScanResult(
            beacon = beacon,
            rssi = rssi.toDouble(),
            txPower = txPower,
            accuracy = accuracy,
            proximity = proximity
        )
    }


    //taken from kmmbeacons
    private fun calculateProximity(accuracy: Double): BeaconProximity = when (accuracy) {
        in 0.0..0.5 -> BeaconProximity.Immediate
        in 0.5..3.0 -> BeaconProximity.Near
        in 3.0..Double.MAX_VALUE -> BeaconProximity.Far
        else -> BeaconProximity.Unknown

    }


    private fun startEmittingNonBeaconsJob() {
        nonBeaconEmitJob?.cancel()
        nonBeaconEmitJob = scope.launch {
            while (isScanning) {
                scanNonBeaconFlow.emit(lastScannedNonBeacons.toList())
                lastScannedNonBeacons.clear()
                kotlinx.coroutines.delay(scanPeriodMillis)
            }
        }
    }

    override fun stop() {
        isScanning = false
        beaconEmitJob?.cancel()
        nonBeaconEmitJob?.cancel()
        manager.removeAllRangeNotifiers()
        manager.startRangingBeacons(Region("all-beacons-region", null, null, null))
    }

    //taken from kmmbeacons
    private fun calculateAccuracy(txPower: Int, rssi: Double): Double {
        if (rssi == 0.0) {
            return -1.0 // if we cannot determine accuracy, return -1.
        }

        val ratio = rssi * 1.0 / txPower
        return if (ratio < 1.0) {
            ratio.pow(10.0)
        } else {
            val accuracy = 0.89976 * ratio.pow(7.7095) + 0.111
            accuracy
        }
    }

}