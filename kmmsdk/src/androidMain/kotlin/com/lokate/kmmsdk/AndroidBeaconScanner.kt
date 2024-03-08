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
        private val mainJob = SupervisorJob()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + mainJob)
    }

    private var running: Boolean = false

    private val regions = mutableListOf<Region>()
    private val beaconFlow: MutableSharedFlow<BeaconScanResult> = MutableSharedFlow()

    override fun startScanning() {
        if (running) {
            Log.e("BeaconScanner2", "Already scanning")
            return
        }

        if (regions.isEmpty()) {
            setRegions(listOf())
        }

        Log.e("BeaconScanner2", "Starting scanning")

        with(manager) {
            running = true
            removeAllRangeNotifiers()
            removeAllMonitorNotifiers()
            addRangeNotifier { beacons, _ ->
                Log.e("BeaconScanner2", "Beacons found: $beacons")
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
            Log.d("BeaconScanner2", "Already running!")
            return
        }
        if (regions.isEmpty()) {
            Log.d("BeaconScanner2", "No regions to scan")
            return
        }
        this.regions.clear()
        this.regions.addAll(regions.map { it.toRegion() })
    }

    override fun scanResultFlow(): Flow<BeaconScanResult> {
        return beaconFlow
    }
}

actual fun getBeaconScanner(): BeaconScanner {
    return AndroidEstimoteBeaconScanner()
}
