package com.lokate.kmmsdk

import android.util.Log
import com.lokate.kmmsdk.Defaults.MINIMUM_SECONDS_BEFORE_EXIT
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.utils.toBeaconScanResult
import com.lokate.kmmsdk.utils.toRegion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

class AndroidBeaconScanner2 : BeaconScanner2 {
    companion object {
        private val manager: BeaconManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BeaconManager.getInstanceForApplication(applicationContext).apply {
                beaconParsers.add(BeaconParser().setBeaconLayout(Defaults.BEACON_LAYOUT_IBEACON))
                setEnableScheduledScanJobs(false)
                isRegionStatePersistenceEnabled = false
            }
        }
        private var mainJob = SupervisorJob()
        private val coroutineScope = CoroutineScope(Dispatchers.Main + mainJob)
    }

    private var interval: Long = 150L
    private var beaconEmitterJob: Job? = null
    private var running: Boolean = false

    private var regions = mutableListOf<Region>()
    private val internalScanResults: MutableSet<BeaconScanResult> = mutableSetOf()
    private val beaconFlow: MutableSharedFlow<Set<BeaconScanResult>> = MutableSharedFlow()
    override fun setScanPeriod(interval: Long) {
        this.interval = interval
    }

    /*
    Temporary beacons for testing
     */
    private val testBeacons = listOf(
        LokateBeacon(
            "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            24719,
            65453,
            ""
        ),//white
        LokateBeacon(

            "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
            1,
            1,
            "2",
        )//pink
        ,
        LokateBeacon(
            "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            24719,
            28241,
            "3",
        ),//White
        LokateBeacon(
            "D5D885F1-D7DA-4F5A-AD51-487281B7F8B3",
            1,
            1,
            "3"
        )
    )//yellow)

    override fun startScanning() {
        if (beaconEmitterJob?.isActive == true) {
            Log.e("BeaconScanner2", "Already scanning")
            return
        }


        if (regions.isEmpty()) {
            //for debugging purposes
            setRegions(listOf())
            //Log.e("BeaconScanner2", "No regions to scan")
            //return
        }

        Log.e("BeaconScanner2", "Starting scanning")
        with(manager) {
            running = true
            removeAllRangeNotifiers()
            removeAllMonitorNotifiers()
            addRangeNotifier { beacons, region ->
                Log.e("BeaconScanner2", "Beacons found: $beacons")
                beacons.forEach { beacon ->
                    when (val current =
                        internalScanResults.firstOrNull { it.beaconUUID == beacon.id1.toString() && it.major == beacon.id2.toInt() && it.minor == beacon.id3.toInt() }) {
                        null -> {
                            internalScanResults.add(beacon.toBeaconScanResult())
                        }

                        else -> {
                            internalScanResults.remove(current)
                            internalScanResults.add(
                                beacon.toBeaconScanResult().copy(
                                    firstSeen = current.firstSeen,
                                    lastSeen = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
            }
            regions.forEach {

                startRangingBeacons(it)
            }
            Log.e("BeaconScanner2", "Starting ranging")
            beaconEmitterJob = coroutineScope.launch {
                try {
                    while (running) {
                        internalScanResults.removeIf {
                            it.lastSeen + MINIMUM_SECONDS_BEFORE_EXIT*1000L < System.currentTimeMillis()
                        }
                        Log.d("BeaconScanner2", "Emitting")
                        beaconFlow.emit(internalScanResults)
                        Log.d("BeaconScanner2", "Emitting ${internalScanResults.size} beacons")
                        Log.d("BeaconScanner2", "Emitting ${internalScanResults}")
                        delay(interval)
                    }
                } catch (e: Exception) {
                    Log.e("BeaconScanner2", "Error starting ranging", e)
                    beaconEmitterJob?.cancel()
                    running = false
                }
            }
        }
    }

    override fun complete() {
        stopScanning()
        mainJob.cancel()
        running = false
    }

    override fun stopScanning() {
        beaconEmitterJob?.cancel()
        running = false

    }

    override fun setRegions(regions: List<LokateBeacon>) {
        if (running){
            Log.d("BeaconScanner2", "Already running!")
            return
        }
        this.regions.clear()
        this.regions.addAll(
            // if regions is empty, use testBeacons
            when {
                regions.isEmpty() -> testBeacons
                else -> regions
            }.let { list ->
                list.map { it.toRegion() }
            }
        )
    }

    override fun scanResultFlow(): Flow<Set<BeaconScanResult>> {
        return beaconFlow
    }
}