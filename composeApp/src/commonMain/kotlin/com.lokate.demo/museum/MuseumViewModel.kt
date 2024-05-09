package com.lokate.demo.museum

import com.lokate.demo.common.base.LokateViewModel
import com.lokate.demo.utils.AudioPlayer
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.component.get

class MuseumViewModel : LokateViewModel() {
    private val player: AudioPlayer = get()

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
                    if (mapped != null && mapped != closestExhibitionUIState.value) {
                        setPlayer(mapped)
                    }
                    _closestExhibitionUIState.emit(mapped)
                }
            }
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
}
