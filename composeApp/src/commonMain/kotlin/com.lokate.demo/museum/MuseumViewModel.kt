package com.lokate.demo.museum

import com.lokate.demo.common.NextCampaignUIState
import com.lokate.demo.common.getNextCampaign
import com.lokate.demo.utils.AudioPlayer
import com.lokate.kmmsdk.LokateSDK
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.lighthousegames.logging.logging

class MuseumViewModel : ViewModel(), KoinComponent {
    private val logger = logging("MuseumViewModel")

    private val lokateSDK: LokateSDK = get()
    private val player: AudioPlayer = get()

    private val closestBeaconFlow = lokateSDK.getClosestBeaconFlow()

    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked = _buttonClicked.asStateFlow()

    private val _closestExhibitionUIState = MutableStateFlow<ExhibitionUIState?>(null)
    val closestExhibitionUIState = _closestExhibitionUIState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    init {
        logger.d { "MuseumViewModel init" }
        collectClosestBeacon()
    }

    fun play() {
        if (player.isRunning) {
            player.pause()
            _isPlaying.value = false
            return
        }
        player.play()
        _isPlaying.value = true
    }

    private fun collectClosestBeacon() {
        viewModelScope.launch {
            closestBeaconFlow.collect {
                logger.d { "Closest beacon changed: $it" }
                if (it != null) {
                    val mapped = it.toExhibitionUIState()
                    if (mapped != null) {
                        setPlayer(mapped)
                        updateNextCampaign()
                    }
                    _closestExhibitionUIState.emit(mapped)
                }
            }
        }
    }

    private val customerId: String
        get() = lokateSDK.getCustomerId()

    private val isLokateRunning: Boolean = lokateSDK.isRunning()

    private val _nextCampaignUIState = MutableStateFlow<NextCampaignUIState?>(null)
    val nextCampaignUIState = _nextCampaignUIState.asStateFlow()

    private fun updateNextCampaign() {
        viewModelScope.launch {
            _nextCampaignUIState.value = getNextCampaign(customerId).toNextCampaignUIState()
        }
    }

    private fun setPlayer(mapped: ExhibitionUIState) {
        player.stop()
        _isPlaying.value = false
        player.setDataSource(mapped.audioUrl)
    }

    private fun LokateBeacon.toExhibitionUIState(): ExhibitionUIState? {
        return when (this.campaignName) {
            "pink" -> pieta
            "red" -> schoolOfAthens
            "white" -> venusDeMilo
            "yellow" -> monaLisa
            else -> null
        }
    }

    private fun String?.toNextCampaignUIState(): NextCampaignUIState? {
        return when (this) {
            "Pieta" -> pietaNext
            else -> null
        }
    }

    fun toggleLokate() {
        if (!isLokateRunning) {
            lokateSDK.startScanning()
            _buttonClicked.value = true
        }
    }
}
