package com.lokate.demo.gym

import com.lokate.demo.common.NextCampaignUIState
import com.lokate.demo.common.getNextCampaign
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

class GymViewModel : ViewModel(), KoinComponent {
    private val logger = logging("GymViewModel")

    private val lokateSDK: LokateSDK = get()

    private val closestBeaconFlow = lokateSDK.getClosestBeaconFlow()

    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked = _buttonClicked.asStateFlow()

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

    fun toggleLokate() {
        if (!isLokateRunning) {
            lokateSDK.startScanning()
            _buttonClicked.value = true
        }
    }
}
