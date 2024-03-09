package com.lokate.kmmsdk

import com.lokate.kmmsdk.Defaults.EVENT_REQUEST_TIMEOUT
import com.lokate.kmmsdk.Defaults.GONE_CHECK_INTERVAL
import com.lokate.kmmsdk.Defaults.MAXIMUM_ELEMENTS_IN_SCAN_EVENT_PIPELINE
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
import com.lokate.kmmsdk.domain.model.beacon.toActiveBeacon
import com.lokate.kmmsdk.domain.model.beacon.toEventRequest
import com.lokate.kmmsdk.domain.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.repository.BeaconRepository
import com.lokate.kmmsdk.domain.repository.RepositoryResult
import com.lokate.kmmsdk.utils.collection.ConcurrentSetWithSpecialEquals
import com.lokate.kmmsdk.utils.extension.EMPTY_STRING
import com.russhwolf.settings.Settings
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.lighthousegames.logging.logging

@OptIn(ExperimentalCoroutinesApi::class)
class LokateSDK private constructor(scannerType: BeaconScannerType) {
    sealed class BeaconScannerType {
        data object IBeacon : BeaconScannerType()
        data class EstimoteMonitoring(val appId: String, val appToken: String) : BeaconScannerType()
    }

    companion object {
        val log = logging("LokateSDK")
        fun createForIBeacon(): LokateSDK {
            return LokateSDK(BeaconScannerType.IBeacon)
        }
        fun createForEstimoteMonitoring(appId: String, appToken: String): LokateSDK {
            return LokateSDK(BeaconScannerType.EstimoteMonitoring(appId, appToken))
        }
    }

    private var isActive = false

    // move to DI
    private val beaconScanner = getBeaconScanner(scannerType)
    private val authenticationRepository: AuthenticationRepository =
        AuthenticationRepositoryImpl(
            AuthenticationRemoteDS(AuthenticationAPI()),
            AuthenticationLocalDS(Settings()),
        )
    private val beaconRepository: BeaconRepository =
        BeaconRepositoryImpl(
            authenticationRepository = authenticationRepository,
            remoteDS = BeaconRemoteDS(BeaconAPI()),
            localDS = BeaconLocalDS(getDatabase()),
        )

    private val branchBeacons = mutableListOf<ActiveBeacon>()

    private var customerId = "umut"

    fun setCustomerId(customerId: String) {
        if (!isActive) {
            this.customerId = customerId
        } else {
            log.e { "Cannot set customer ID while scanning" }
        }
    }

    private val lokateJob = SupervisorJob()
    private val lokateScopeNetworkDB = CoroutineScope(Dispatchers.IO + lokateJob)
    private val lokateScopeComputation = CoroutineScope(Dispatchers.IO + lokateJob)

    private val activeBeacons =
        ConcurrentSetWithSpecialEquals(
            equals = { it1: BeaconScanResult, it2 ->
                it1.beaconUUID.lowercase() == it2.beaconUUID.lowercase() &&
                    it1.major == it2.major &&
                    it1.minor == it2.minor
            },
        )

    private val beaconScanResultChannel = Channel<BeaconScanResult>(MAXIMUM_ELEMENTS_IN_SCAN_EVENT_PIPELINE)
    private val eventChannel: Channel<EventRequest> = Channel(MAXIMUM_ELEMENTS_IN_SCAN_EVENT_PIPELINE)

    private var appTokenSet: Boolean = false

    /*
     * it is being used but I believe we should not expose as it comes from the scanner
     */
    fun getScanResultFlow(): Flow<BeaconScanResult> {
        return beaconScanner.scanResultFlow()
    }

    fun setAppToken(appToken: String) {
        lokateScopeNetworkDB.launch {
            authenticationRepository.setAppToken(appToken)
        }.invokeOnCompletion {
            log.d { "App token set" }
            appTokenSet = true
        }
    }

    private fun fetchBranchBeacons(branchId: String) {
        if (isActive) {
            log.e { "Already scanning, stop scanning and get beacons again" }
            return
        }
        val branch =
            branchId.ifEmpty {
                log.e { "Branch ID is empty" }
                "1b224840-de56-41a2-92e0-959193b0035e"
            }
        if (branchBeacons.isEmpty()) {
            log.e { "No beacons to scan" }
            // debug purposes
            addDefaultBeacons()
        }
        lokateScopeNetworkDB.launch {
            log.d { "Fetching beacons for branch: $branch" }
            beaconRepository.fetchBeacons(branch).let {
                log.e { "Beacon fetch result arrived!, $it" }
                when (it) {
                    is RepositoryResult.Success -> {
                        if (it.body.isNotEmpty() && it.body.any { it.uuid != EMPTY_STRING }) {
                            branchBeacons.clear()
                            branchBeacons.addAll(it.body)
                        }
                        log.d { "Branch beacons: $branchBeacons" }
                    }

                    is RepositoryResult.Error -> {
                        log.e { "Error fetching beacons: ${it.message}" }
                    }
                }
            }
        }
    }

    private fun addDefaultBeacons() {
        log.e { "Adding default branch beacons" }
        branchBeacons.addAll(Defaults.DEFAULT_BEACONS.map { it.toActiveBeacon() })
    }

    private fun checkAppToken() {
        lokateScopeNetworkDB.launch {
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
        if (isActive) {
            log.e { "Already scanning" }
            return
        }

        fetchBranchBeacons(EMPTY_STRING)

        if (branchBeacons.isEmpty()) {
            log.e { "No beacons to scan" }
            return
        } else {
            log.d { "Beacons to scan: $branchBeacons" }
            beaconScanner.setRegions(
                branchBeacons.map {
                    it.toLokateBeacon()
                        .copy(major = null, minor = null) // to scan all beacons. we can change this
                },
            )
        }
        beaconScanner.startScanning()
        scanProcessPipeline(beaconScanner.scanResultFlow())
        checkGone()
        isActive = true
    }

    private fun CoroutineScope.excludeMinimumProximityAndNonBranchBeacons(channel: ReceiveChannel<BeaconScanResult>) =
        produce {
            for (scan in channel) {
                val beacon =
                    branchBeacons.firstOrNull {
                        it.uuid.lowercase() == scan.beaconUUID.lowercase() &&
                            it.major == scan.major.toString() &&
                            it.minor == scan.minor.toString()
                    }
                when {
                    scan.accuracy < 0 -> log.d { "This shouldn't happen" }
                    beacon == null -> log.d { "Beacon not in branch: $scan, branch beacons: $branchBeacons" }
                    beacon.radius < scan.accuracy -> {
                        log.d { "Beacon proximity is not in range: $scan." +
                                " setted: ${beacon.radius}, current: ${scan.accuracy}" }
                    }

                    else -> {
                        send(scan)
                    }
                }
            }
        }

    private fun CoroutineScope.differentiateType(
        receiveChannel: ReceiveChannel<BeaconScanResult>,
        outputChannel: Channel<EventRequest>,
    ) {
        launch {
            for (scan in receiveChannel) {
                when (activeBeacons.contains(scan)) {
                    true -> outputChannel.send(scan.toEventRequest(customerId, EventStatus.STAY))
                    false -> outputChannel.send(scan.toEventRequest(customerId, EventStatus.ENTER))
                }
                activeBeacons.addOrUpdate(scan)
            }
        }
    }

    private fun scanProcessPipeline(beaconScannerFlow: Flow<BeaconScanResult>) {
        lokateScopeComputation.launch {
            beaconScannerFlow.collect {
                beaconScanResultChannel.send(it)
            }
        }
        lokateScopeComputation.launch {
            val excludeLowerProximityAndNonBranchBeacons =
                excludeMinimumProximityAndNonBranchBeacons(beaconScanResultChannel)
            differentiateType(excludeLowerProximityAndNonBranchBeacons, eventChannel)
            sendEvent(eventChannel)
        }
    }

    private suspend fun sendEvent(eventPipeline: ReceiveChannel<EventRequest>) {
        for (event in eventPipeline) {
            log.d { "Send event (${event.status}): ${event.beaconUID}" }
            lokateScopeNetworkDB.launch {
                withTimeoutOrNull(EVENT_REQUEST_TIMEOUT) {
                    return@withTimeoutOrNull beaconRepository.sendBeaconEvent(event).also {
                        log.d { "event sent: (${event.status}): ${event.beaconUID}" }
                    }
                } ?: log.e { "Timeout while sending event" }
            }
        }
    }

    private fun checkGone() {
        lokateScopeComputation.launch {
            while (isActive) {
                delay(GONE_CHECK_INTERVAL)
                try {
                    val currentTimeMillis = getTimeMillis()
                    val goneBeacons =
                        activeBeacons.filter {
                            it.seen < currentTimeMillis - Defaults.DEFAULT_TIMEOUT_BEFORE_GONE
                        }
                    goneBeacons.forEach { beacon ->
                        activeBeacons.remove(beacon)
                        eventChannel.send(beacon.toEventRequest(customerId, EventStatus.EXIT))
                    }
                } catch (e: Exception) {
                    log.e { "Error checking for gone beacons: ${e.message}" }
                }
            }
        }
    }

    fun stopScanning() {
        isActive = false
        beaconScanner.stopScanning()
        lokateJob.cancel()
        closeChannels()
    }

    private fun closeChannels() {
        beaconScanResultChannel.close()
        eventChannel.close()
    }
}
