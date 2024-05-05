package com.lokate.kmmsdk

import com.lokate.kmmsdk.Defaults.DEFAULT_BEACONS
import com.lokate.kmmsdk.Defaults.EVENT_REQUEST_TIMEOUT
import com.lokate.kmmsdk.Defaults.GONE_CHECK_INTERVAL
import com.lokate.kmmsdk.Defaults.MAXIMUM_ELEMENTS_IN_SCAN_EVENT_PIPELINE
import com.lokate.kmmsdk.di.SDKKoinComponent
import com.lokate.kmmsdk.di.SDKKoinContext
import com.lokate.kmmsdk.domain.model.beacon.BeaconScanResult
import com.lokate.kmmsdk.domain.model.beacon.EventRequest
import com.lokate.kmmsdk.domain.model.beacon.EventStatus
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import com.lokate.kmmsdk.domain.model.beacon.toEventRequest
import com.lokate.kmmsdk.domain.repository.AuthenticationRepository
import com.lokate.kmmsdk.domain.repository.BeaconRepository
import com.lokate.kmmsdk.domain.repository.RepositoryResult
import com.lokate.kmmsdk.utils.collection.ConcurrentSetWithSpecialEquals
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.get
import org.lighthousegames.logging.logging

@OptIn(ExperimentalCoroutinesApi::class)
class LokateSDK(
    private val authenticationRepository: AuthenticationRepository,
    private val beaconRepository: BeaconRepository,
    private val beaconScanner: BeaconScanner,
) : SDKKoinComponent() {
    sealed class BeaconScannerType {
        data object IBeacon : BeaconScannerType()

        data class EstimoteMonitoring(val appId: String, val appToken: String) : BeaconScannerType()
    }

    companion object {
        val log = logging("LokateSDK")
        private var _instance: LokateSDK? = null

        fun getInstance(scannerType: BeaconScannerType): LokateSDK {
            if (_instance == null) {
                configure(scannerType)
            }
            return _instance!!
        }

        private fun configure(scannerType: BeaconScannerType) {
            SDKKoinContext.beaconScannerType = scannerType
            initSDK()
        }

        private fun initSDK() {
            _instance = SDKKoinContext.koin.get()
        }
    }

    fun isRunning(): Boolean {
        return isActive
    }

    private var isActive = false

    private val branchBeacons = mutableListOf<LokateBeacon>()

    private var customerId = "umut"

    fun setCustomerId(customerId: String) {
        if (!isActive) {
            this.customerId = customerId
        } else {
            log.e { "Cannot set customer ID while scanning" }
        }
    }

    fun getCustomerId(): String {
        return customerId
    }

    private val lokateJob = SupervisorJob()
    private val lokateScopeNetworkDB = CoroutineScope(Dispatchers.IO + lokateJob)
    private val lokateScopeComputation = CoroutineScope(Dispatchers.IO + lokateJob)

    private val lokateBeacons =
        ConcurrentSetWithSpecialEquals(
            equals = { it1: BeaconScanResult, it2 ->
                it1.beaconUUID.lowercase() == it2.beaconUUID.lowercase() &&
                    it1.major == it2.major &&
                    it1.minor == it2.minor
            },
        )

    private val beaconScanResultChannel =
        Channel<BeaconScanResult>(MAXIMUM_ELEMENTS_IN_SCAN_EVENT_PIPELINE)
    private val eventChannel: Channel<EventRequest> =
        Channel(MAXIMUM_ELEMENTS_IN_SCAN_EVENT_PIPELINE)

    private val closestBeaconFlow: MutableSharedFlow<LokateBeacon?> = MutableSharedFlow()

    private var appTokenSet: Boolean = false

    fun getClosestBeaconFlow(): SharedFlow<LokateBeacon?> {
        return closestBeaconFlow
    }

    fun setAppToken(appToken: String) {
        lokateScopeNetworkDB.launch {
            authenticationRepository.setAppToken(appToken)
        }.invokeOnCompletion {
            log.d { "App token set" }
            appTokenSet = true
        }
    }

    private fun fetchBranchBeacons(afterFetch: () -> Unit) {
        if (isActive) {
            log.e { "Already scanning, stop scanning and get beacons again" }
            return
        }
        lokateScopeNetworkDB.launch {
            val (latitude, longitude) =
                withTimeoutOrNull(10000) {
                    try {
                        getCurrentGeolocation()
                    } catch (e: Exception) {
                        log.e { "Failed to get current location: ${e.message}" }
                        Pair(0.0, 0.0)
                    }
                } ?: Pair(0.0, 0.0)

            log.d { "latitude: $latitude, longitude: $longitude" }

            val beacons =
                withTimeoutOrNull(5000) {
                    beaconRepository.fetchBeacons(latitude, longitude).let {
                        when (it) {
                            is RepositoryResult.Success -> {
                                log.d { "Successfully fetched beacons" }
                                it.body
                            }

                            is RepositoryResult.Error -> {
                                log.e { "Failed to fetch beacons: ${it.message}, ${it.errorType}" }
                                log.d { "Default beacons will be scanned" }
                                DEFAULT_BEACONS
                            }
                        }
                    }
                }?.plus(DEFAULT_BEACONS) ?: DEFAULT_BEACONS

            branchBeacons.addAll(beacons)

            log.d { "Branch beacons:" }.also {
                for (beacon in branchBeacons) {
                    log.d { "$beacon" }
                }
            }

            afterFetch()
        }
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

        fetchBranchBeacons {
            beaconScanner.setRegions(branchBeacons)
            beaconScanner.startScanning()
            scanProcessPipeline(beaconScanner.scanResultFlow())
            checkGone()
            isActive = true
        }
    }

    private fun CoroutineScope.excludeMinimumProximityAndNonBranchBeacons(channel: ReceiveChannel<BeaconScanResult>) =
        produce {
            for (scan in channel) {
                val beacon =
                    branchBeacons.firstOrNull {
                        it.proximityUUID.lowercase() == scan.beaconUUID.lowercase() &&
                            it.major == scan.major &&
                            it.minor == scan.minor
                    }
                when {
                    scan.accuracy < 0 -> log.d { "This shouldn't happen" }
                    beacon == null -> log.d { "Beacon not in branch: $scan, branch beacons: $branchBeacons" }
                    beacon.radius < scan.accuracy -> {
                        log.d {
                            "Beacon proximity is not in range: $scan." +
                                " setted: ${beacon.radius}, current: ${scan.accuracy}"
                        }
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
            var closestBeacon: LokateBeacon? = null

            for (scan in receiveChannel) {
                when (lokateBeacons.contains(scan)) {
                    true -> outputChannel.send(scan.toEventRequest(customerId, EventStatus.STAY))
                    false -> outputChannel.send(scan.toEventRequest(customerId, EventStatus.ENTER))
                }
                lokateBeacons.addOrUpdate(scan)

                // emit closest beacon only if there is a change in the closest beacon (prevent unnecessary emits)
                val closestScan = lokateBeacons.minBy { it.accuracy }
                if (closestBeacon == null ||
                    closestBeacon.proximityUUID.lowercase() != closestScan.beaconUUID.lowercase() ||
                    closestBeacon.major != closestScan.major ||
                    closestBeacon.minor != closestScan.minor
                ) {
                    closestBeacon =
                        branchBeacons.firstOrNull {
                            closestScan.beaconUUID.lowercase() == it.proximityUUID.lowercase() &&
                                closestScan.major == it.major &&
                                closestScan.minor == it.minor
                        }
                    closestBeaconFlow.emit(closestBeacon)
                    log.d { "closest beacon changed: $closestBeacon" }
                }
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
            log.d { "Sending event (${event.status}): ${event.beaconUUID}, ${event.major}, ${event.minor}" }
            lokateScopeNetworkDB.launch {
                withTimeoutOrNull(EVENT_REQUEST_TIMEOUT) {
                    return@withTimeoutOrNull beaconRepository.sendBeaconEvent(event).also {
                        log.d { "event sent: (${event.status}): ${event.beaconUUID}, ${event.major}, ${event.minor}" }
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
                        lokateBeacons.filter {
                            it.seen < currentTimeMillis - Defaults.DEFAULT_TIMEOUT_BEFORE_GONE
                        }
                    goneBeacons.forEach { beacon ->
                        lokateBeacons.remove(beacon)
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

expect suspend fun getCurrentGeolocation(): Pair<Double, Double>
