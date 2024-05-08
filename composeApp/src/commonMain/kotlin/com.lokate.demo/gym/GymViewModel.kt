package com.lokate.demo.gym

import com.lokate.demo.common.base.LokateViewModel
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

class GymViewModel : LokateViewModel() {
    private val _closestEquipmentUIState = MutableStateFlow<EquipmentUIState?>(null)
    val closestEquipmentUIState = _closestEquipmentUIState.asStateFlow()

    init {
        collectClosestBeacon()
    }

    private fun collectClosestBeacon() {
        viewModelScope.launch {
            closestBeaconFlow.collect {
                logger.d { "Closest beacon changed: $it" }
                if (it != null) {
                    val mapped = it.toEquipmentUIState()
                    if (mapped != null) {
                        _closestEquipmentUIState.emit(mapped)
                    }
                }
            }
        }
    }
    private fun LokateBeacon.toEquipmentUIState(): EquipmentUIState? {
        return when (this.campaignName) {
            "pink" -> benchPress
            "red" -> cableRow
            "white" -> latPulldown
            "yellow" -> squat
            else -> null
        }
    }
}
