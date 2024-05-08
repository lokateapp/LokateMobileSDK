package com.lokate.kmmsdk

import android.util.Log
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
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
    }

    private var mainJob = SupervisorJob()
    private var coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)

    private var running: Boolean = false

    private val regions = mutableListOf<Region>()
    private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()

    private fun initJobs() {
        if (!mainJob.isActive) {
            mainJob = SupervisorJob()
            coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)
        }
    }

    override fun startScanning() {
        if (running) {
            Log.e("AndroidBeaconScanner", "Already scanning")
            return
        }
        initJobs()
        if (regions.isEmpty()) {
            setRegions(listOf())
        }

        Log.d("AndroidBeaconScanner", "Starting scanning")

        with(manager) {
            running = true
            removeAllRangeNotifiers()
            removeAllMonitorNotifiers()
            addRangeNotifier { beacons, region ->
                Log.i(
                    "AndroidBeaconScanner",
                    "Beacons found: ${beacons.map { it.id3 }} in region $region"
                )
                beacons.forEach { beacon ->
                    coroutineScope.launch {
                        beaconFlow.emit(beacon.toBeaconScanResult())
                    }
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
        if (running) {
            Log.d("AndroidBeaconScanner", "Already running!")
            return
        }
        if (regions.isEmpty()) {
            Log.d("AndroidBeaconScanner", "No regions to scan")
            return
        }
        this.regions.clear()
        this.regions.addAll(regions.map { it.toRegion() })
    }

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return beaconFlow
    }
}
