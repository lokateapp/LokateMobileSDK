package com.lokate.kmmsdk

import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import io.github.aakira.napier.Napier
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class LokateSDK {
    private val scanner = getScanner()

    private val activeBeacons = mutableSetOf<BeaconScanResult>()
    private var results: Flow<List<BeaconScanResult>>? = null

    private val lokateJob = SupervisorJob()
    private val lokateScope = CoroutineScope(Dispatchers.IO + lokateJob)

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
    )

    fun startScanning() {
        scanner.setRegions(testBeacons)
        scanner.startScanning()

        val scannerFlow = scanner.scanResultFlow()
        val newComer = scannerFlow.filter { it !in activeBeacons }
        val alreadyIn = scannerFlow.filter { it in activeBeacons }.transform {
            emit(activeBeacons.find { it.beaconUUID == it.beaconUUID && it.major == it.major && it.minor == it.minor }?.copy(lastSeen = getTimeMillis()))
        }
        // in the last 3 seconds, if the beacon is not in the list, it is considered gone
        val gone = flow {
            activeBeacons.forEach {
                if (it.lastSeen < getTimeMillis() - 3000) {
                    emit(it)
                }
            }
        }

        lokateScope.launch {
            beaconFlowHandler(newComer, alreadyIn, gone)
        }
        //scanner.start()
        //results = scanner.observeResults()
    }

    private suspend fun beaconFlowHandler(
        scannerFlow: Flow<BeaconScanResult>,
        alreadyIn: Flow<BeaconScanResult?>,
        gone: Flow<BeaconScanResult>
    ){
        // first look for the beacons that are already in the list
        alreadyIn.collect {
            it?.let {
                activeBeacons.removeAll { it.beaconUUID == it.beaconUUID && it.major == it.major && it.minor == it.minor }
                activeBeacons.add(it)
                //send stay event
                Napier.d("Beacon is still here $it")
            }
        }
        // then look for the beacons that are new
        scannerFlow.collect {
            activeBeacons.add(it)
            //send enter event
            Napier.d("Beacon is new $it")
        }
        // finally look for the beacons that are gone
        gone.collect {
            activeBeacons.remove(it)
            //send exit event
            Napier.d("Beacon is gone $it")
        }
        /*val newComer = scannerFlow.filter { it !in activeBeacons }
        val alreadyIn = scannerFlow.filter { it in activeBeacons }.transform {
            emit(activeBeacons.find { it.beaconUUID == it.beaconUUID && it.major == it.major && it.minor == it.minor }?.copy(lastSeen = getTimeMillis()))
        }
        // in the last 3 seconds, if the beacon is not in the list, it is considered gone
        val gone = flow {
            activeBeacons.forEach {
                if (it.lastSeen < getTimeMillis() - 3000) {
                    emit(it)
                }
            }
        }
        alreadyIn.onEach {
            it?.let {
                activeBeacons.removeAll { it.beaconUUID == it.beaconUUID && it.major == it.major && it.minor == it.minor }
                activeBeacons.add(it)
            }
            //send stay event
            Napier.d("Beacon is still here $it")

        }
        newComer.onEach {
            activeBeacons.add(it)
            //send enter event
            Napier.d("Beacon is new $it")

        }
        gone.onEach {
            activeBeacons.remove(it)
            //send exit event
            Napier.d("Beacon is gone $it")
        }

         */

        //three cases
        //1. beacon is already in the list and came again
        //2. beacon is not in the list and came for the first time
        //3. beacon is in the list and did not come again
        // in first case send stay event
        // in second case send enter event
        // in third case send exit event
    }

    fun stopScanning() {
        //scanner.stop()
        scanner.stopScanning()

    }



}