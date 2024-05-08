package com.lokate.demo.gym

import com.lokate.demo.common.NextCampaignUIState
import com.lokate.demo.common.base.LokateViewModel
import com.lokate.demo.common.getNextCampaign
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
                        updateNextCampaign()
                    }
                    _closestEquipmentUIState.emit(mapped)
                }
            }
        }
    }

    private val _nextCampaignUIState = MutableStateFlow<NextCampaignUIState?>(null)
    val nextCampaignUIState = _nextCampaignUIState.asStateFlow()

    private fun updateNextCampaign() {
        viewModelScope.launch {
            _nextCampaignUIState.value = getNextCampaign(customerId).toNextCampaignUIState()
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

    private fun String?.toNextCampaignUIState(): NextCampaignUIState? {
        return when (this) {
            "Bench Press" -> benchPressNext
            "Triceps Extension" -> tricepsExtensionNext
            "Dumbbell Shoulder Press" -> dumbbellShoulderPressNext
            "Lat Pulldown" -> latPulldownNext
            "Deadlift" -> deadliftNext
            "Pull-up" -> pullUpNext
            "Squat" -> squatNext
            "Leg Press" -> legPressNext
            else -> null
        }
    }
}
