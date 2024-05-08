package com.lokate.demo.csfair

import com.lokate.demo.common.base.LokateViewModel
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

class CSFairViewModel : LokateViewModel() {
    private val _closestStandUIState = MutableStateFlow<StandUIState?>(null)
    val closestStandUIState = _closestStandUIState.asStateFlow()

    init {
        collectClosestBeacon()
    }

    private fun collectClosestBeacon() {
        viewModelScope.launch {
            closestBeaconFlow.collect {
                logger.d { "Closest beacon changed: $it" }
                if (it != null) {
                    val mapped = it.toStandUIState()
                    if (mapped != null) {
                        _closestStandUIState.emit(mapped)
                    }
                }
            }
        }
    }

    private fun LokateBeacon.toStandUIState(): StandUIState? {
        return when (this.campaignName) {
            "pink" -> lokate
            "red" -> lokate
            "white" -> lokate
            "yellow" -> lokate
            else -> null
        }
    }
}
