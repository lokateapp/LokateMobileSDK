package com.lokate.demo.museum

import com.lokate.demo.utils.AudioPlayer
import com.lokate.kmmsdk.LokateSDK
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.lighthousegames.logging.logging

class MuseumViewModel : ViewModel(), KoinComponent {
    val logger = logging("MuseumViewModel")

    private val lokateSDK: LokateSDK = get()
    private val player: AudioPlayer = get()

    private val _closestBeaconFlow = lokateSDK.getClosestBeaconFlow()
    val closestBeaconFlow: SharedFlow<LokateBeacon?> = _closestBeaconFlow

    private val _closestExhibition: MutableStateFlow<ExhibitionUIState?> = MutableStateFlow(null)
    val closestExhibition: StateFlow<ExhibitionUIState?> = _closestExhibition

    val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    init {
        logger.d { "MuseumViewModel init" }
        collectClosestBeacon()
    }

    fun play() {
        if(player.isRunning) {
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
                logger.d { "Closest beacon: $it" }
                if (it != null) {
                    val mapped = it.mapToExhibitionUIState()
                    if (mapped != null && mapped != closestExhibition.value) {
                        setPlayer(mapped)
                    }

                    _closestExhibition.emit(it.mapToExhibitionUIState())
                }
            }
        }
    }

    private fun setPlayer(mapped: ExhibitionUIState) {
        player.stop()
        _isPlaying.value = false
        if (mapped.audioUrl != null) {
            player.setDataSource(mapped.audioUrl)
        }
    }

    private fun LokateBeacon.mapToExhibitionUIState(): ExhibitionUIState? {
        return when (this.campaignName) {
            "yellow" -> {
                monaLisa
            }

            "white" -> {
                venusDeMilo
            }

            else -> {
                null
            }
        }
    }
}