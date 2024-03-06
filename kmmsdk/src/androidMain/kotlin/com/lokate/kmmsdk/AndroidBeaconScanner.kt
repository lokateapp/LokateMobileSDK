package com.lokate.kmmsdk

import android.util.Log
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.Defaults
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.utils.toBeaconScanResult
import com.lokate.kmmsdk.utils.toRegion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

class AndroidBeaconScanner : BeaconScanner {
    companion object {
        private val manager: BeaconManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BeaconManager.getInstanceForApplication(applicationContext).apply {
                beaconParsers.add(BeaconParser().setBeaconLayout(Defaults.BEACON_LAYOUT_IBEACON))
                setEnableScheduledScanJobs(false)
                isRegionStatePersistenceEnabled = false
            }
        }
        private val mainJob = SupervisorJob()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)
    }

    private var running: Boolean = false

    private var regions = mutableListOf<Region>()
    //private val internalScanResults: MutableSet<BeaconScanResult> = mutableSetOf()
    private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()

    /*
    Temporary beacons for testing
     */
    private val testBeacons = listOf(
        LokateBeacon(
            "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            24719,
            65453,
            "0"
        ), // white
        LokateBeacon(
            "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            24719,
            28241,
            "1",
        ), // White
        LokateBeacon(
            "D5D885F1-D7DA-4F5A-AD51-487281B7F8B3",
            1,
            1,
            "2"
        ), // yellow
        LokateBeacon(
            "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
            1,
            1,
            "3",
        ), // pink
        LokateBeacon(
            "5D72CC30-5C61-4C09-889F-9AE750FA84EC",
            1,
            2,
            "4"
        ) // red
    )

    override fun startScanning() {
        if (running) {
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
                    coroutineScope.launch {
                        beaconFlow.emit(beacon.toBeaconScanResult())
                    }
                    /*when (val current =
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

                     */
                }
            }
            regions.forEach {
                startRangingBeacons(it)
            }
        }
    }

    override fun stopScanning() {
        mainJob.cancel()
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

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return beaconFlow
    }
}

actual fun getBeaconScanner(): BeaconScanner {
    return AndroidBeaconScanner()
}
