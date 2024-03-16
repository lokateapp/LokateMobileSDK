package com.lokate.kmmsdk

import com.lokate.kmmsdk.Defaults.DEFAULT_BEACONS
import com.lokate.kmmsdk.Defaults.GONE_CHECK_INTERVAL
import com.lokate.kmmsdk.data.datasource.local.authentication.AuthenticationLocalDS
import com.lokate.kmmsdk.data.datasource.local.beacon.BeaconLocalDS
import com.lokate.kmmsdk.data.datasource.local.factory.getDatabase
import com.lokate.kmmsdk.data.datasource.remote.authentication.AuthenticationAPI
import com.lokate.kmmsdk.data.datasource.remote.authentication.AuthenticationRemoteDS
import com.lokate.kmmsdk.data.datasource.remote.beacon.BeaconAPI
import com.lokate.kmmsdk.data.datasource.remote.beacon.BeaconRemoteDS
import com.lokate.kmmsdk.data.repository.AuthenticationRepositoryImpl
import com.lokate.kmmsdk.data.repository.BeaconRepositoryImpl
import com.lokate.kmmsdk.domain.model.beacon.ActiveBeacon
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.EventRequest
import com.lokate.kmmsdk.domain.model.beacon.EventStatus
import com.lokate.kmmsdk.domain.repository.RepositoryResult
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import com.russhwolf.settings.Settings
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class LokateSDK {
    companion object {
        val log = logging("LokateSDK")
    }

    // move to DI
    private val beaconScanner = getBeaconScanner()
    private val authenticationRepository =
        AuthenticationRepositoryImpl(
            AuthenticationRemoteDS(AuthenticationAPI()),
            AuthenticationLocalDS(Settings()),
        )
    private val beaconRepository =
        BeaconRepositoryImpl(
            authenticationRepository = authenticationRepository,
            remoteDS = BeaconRemoteDS(BeaconAPI()),
            localDS = BeaconLocalDS(getDatabase()),
        )

    private val branchBeacons = mutableListOf<ActiveBeacon>()

    private val lokateJob = SupervisorJob()
    private val lokateScope = CoroutineScope(Dispatchers.IO + lokateJob)

    private val activeBeacons = mutableSetOf<BeaconScanResult>()

    private val newComer = MutableSharedFlow<BeaconScanResult>()
    private val alreadyIn = MutableSharedFlow<BeaconScanResult>()
    private val gone = MutableSharedFlow<BeaconScanResult>()
    private var appTokenSet: Boolean = false

    /*
    * it is being used but I believe we should not expose as it comes from the scanner
    */
    fun getScanResultFlow(): Flow<BeaconScanResult> {
        return beaconScanner.scanResultFlow()
    }

    fun setAppToken(appToken: String) {
        lokateScope.launch {
            authenticationRepository.setAppToken(appToken)
        }.invokeOnCompletion {
            log.d { "App token set" }
            appTokenSet = true
        }
    }

    private fun fetchActiveBeacons(branchId: String) {
        val branch =
            branchId.ifEmpty {
                log.e { "Branch ID is empty" }
                "1b224840-de56-41a2-92e0-959193b0035e"
            }
        lokateScope.launch {
            beaconRepository.fetchBeacons(branch).let {
                if (it is RepositoryResult.Success) {
                    branchBeacons.addAll(it.body)
                    log.d { "Branch beacons: $branchBeacons" }
                }
            }
        }
    }

    private fun checkAppToken() {
        lokateScope.launch {
            authenticationRepository.getAppToken().let {
                if (it is RepositoryResult.Success && it.body.isNotEmpty()) {
                    appTokenSet = true
                }
            }
        }
    }

    init {
        log.d { "LokateSDK initializing" }
        // check if app token is set
        checkAppToken()

        /*
        this is a temporary solution to set the app token for debugging purposes
        * */
        if (!appTokenSet) {
            log.e { "App token not set" }
            // use default token
            setAppToken("1")
        }
    }

    fun startScanning() {
        beaconScanner.setRegions(DEFAULT_BEACONS)
        //
        fetchActiveBeacons(EMPTY_STRING)
        //
        beaconScanner.startScanning()

        scanResultHandler(
            beaconScannerFlow =
                beaconScanner.scanResultFlow().transform { scan ->
                    val beacon =
                        branchBeacons.firstOrNull {
                            it.id.lowercase() == scan.beaconUUID.lowercase() &&
                                it.major == scan.major.toString() &&
                                it.minor == scan.minor.toString()
                        }
                    val comparison = (beacon == null || beacon.range.ordinal >= scan.proximity.ordinal)
                    if (scan.accuracy > -1.0 && comparison) {
                        emit(scan)
                    }
                },
        )
        checkGone()
        sendEvents()
        // beaconScanner.start()
        // results = beaconScanner.observeResults()
    }

    private fun checkGone() {
        lokateScope.launch {
            while (true) {
                delay(GONE_CHECK_INTERVAL)
                log.d { "Checking for gone beacons" }
                activeBeacons.forEach {
                    if (it.seen < getTimeMillis() - Defaults.DEFAULT_TIMEOUT_BEFORE_GONE) {
                        gone.emit(it)
                        activeBeacons.remove(it)
                    }
                }
            }
        }
    }

    private fun sendEvents() {
        lokateScope.launch {
            newComer.collect {
                log.d { "Newcomer: $it" }
                beaconRepository.sendBeaconEvent(
                    EventRequest(
                        "umut",
                        it.beaconUUID,
                        it.major.toString(),
                        it.minor.toString(),
                        EventStatus.ENTER,
                        it.seen,
                    ),
                ).also { log.d { "Send newcomer in: $it" } }
            }
        }
        lokateScope.launch {
            alreadyIn.collect {
                log.d { "Already in: $it" }
                beaconRepository.sendBeaconEvent(
                    EventRequest(
                        "umut",
                        it.beaconUUID,
                        it.major.toString(),
                        it.minor.toString(),
                        EventStatus.STAY,
                        it.seen,
                    ),
                ).also { log.d { "Send already in: $it" } }
            }
        }
        lokateScope.launch {
            gone.collect {
                log.d { "Gone: $it" }
                beaconRepository.sendBeaconEvent(
                    EventRequest(
                        "umut",
                        it.beaconUUID,
                        it.major.toString(),
                        it.minor.toString(),
                        EventStatus.EXIT,
                        it.seen,
                    ),
                ).also { log.d { "Send gone: $it" } }
            }
        }
    }

    private fun scanResultHandler(beaconScannerFlow: Flow<BeaconScanResult>) {
        lokateScope.launch {
            beaconScannerFlow.collect { scan ->
                activeBeacons.firstOrNull {
                    it.beaconUUID == scan.beaconUUID &&
                        it.major == scan.major &&
                        it.minor == scan.minor
                }
                    ?.let {
                        alreadyIn.emit(scan)
                    } ?: newComer.emit(scan).also { activeBeacons.add(scan) }
            }
        }
    }

    fun stopScanning() {
        // beaconScanner.stop()
        beaconScanner.stopScanning()
        lokateScope.cancel()
    }
}
