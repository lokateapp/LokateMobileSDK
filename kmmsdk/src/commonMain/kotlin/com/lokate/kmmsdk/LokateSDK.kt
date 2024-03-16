package com.lokate.kmmsdk

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
import com.lokate.kmmsdk.domain.model.beacon.EventStatus
import com.lokate.kmmsdk.domain.model.beacon.toEventRequest
import com.lokate.kmmsdk.domain.repository.RepositoryResult
import com.lokate.kmmsdk.utils.collection.ConcurrentSetWithSpecialEquals
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

    private var isActive = false

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

    private val activeBeacons = ConcurrentSetWithSpecialEquals(
        equals = { it1: BeaconScanResult, it2 ->
            it1.beaconUUID.lowercase() == it2.beaconUUID.lowercase() &&
                    it1.major == it2.major &&
                    it1.minor == it2.minor
        },
    )

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
        if (isActive) {
            log.e { "Already scanning, stop scanning and get beacons again" }
            return
        }
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
        isActive = true
        fetchActiveBeacons(EMPTY_STRING)

        if (branchBeacons.isEmpty()) {
            log.e { "No beacons to scan adding defaults" }
            beaconScanner.setRegions(Defaults.DEFAULT_BEACONS)
            return
        } else {
            log.d { "Beacons to scan: $branchBeacons" }
            beaconScanner.setRegions(branchBeacons.map {
                it.toLokateBeacon().copy(major = null, minor = null) // to scan all beacons. we can change this
            })
        }
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
            val checkInterval = GONE_CHECK_INTERVAL // Adjust based on your needs
            while (isActive) { // Ensures coroutine stops when scope is cancelled
                delay(checkInterval)
                try {
                    val currentTimeMillis = getTimeMillis()
                    val goneBeacons = activeBeacons.filter {
                        it.seen < currentTimeMillis - Defaults.DEFAULT_TIMEOUT_BEFORE_GONE
                    }
                    goneBeacons.forEach { beacon ->
                        gone.emit(beacon)
                        activeBeacons.remove(beacon)
                    }
                } catch (e: Exception) {
                    log.e { "Error checking for gone beacons: ${e.message}" }
                }
            }
        }
    }

    private fun sendEvents() {
        lokateScope.launch {
            newComer.collect {
                log.d { "Newcomer: $it" }
                beaconRepository.sendBeaconEvent(
                    it.toEventRequest("umut", EventStatus.ENTER),
                ).also { log.d { "Send newcomer in: $it" } }
            }
        }
        lokateScope.launch {
            alreadyIn.collect {
                log.d { "Already in: $it" }
                beaconRepository.sendBeaconEvent(
                    it.toEventRequest("umut", EventStatus.STAY)
                ).also { log.d { "Send already in: $it" } }
            }
        }
        lokateScope.launch {
            gone.collect {
                log.d { "Gone: $it" }
                beaconRepository.sendBeaconEvent(
                    it.toEventRequest("umut", EventStatus.EXIT)
                ).also { log.d { "Send gone: $it" } }
            }
        }
    }

    private fun scanResultHandler(beaconScannerFlow: Flow<BeaconScanResult>) {
        lokateScope.launch {
            beaconScannerFlow.collect { scan ->
                when (activeBeacons.contains(scan)) {
                    true -> alreadyIn.emit(scan)
                    false -> {
                        newComer.emit(scan)
                        activeBeacons.add(scan)
                    }
                }
            }
        }
    }

    fun stopScanning() {
        isActive = false
        beaconScanner.stopScanning()
        lokateScope.cancel()
    }
}
