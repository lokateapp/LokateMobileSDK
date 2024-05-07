package com.lokate.demo.gym

import com.lokate.demo.common.getNextCampaign
import com.lokate.kmmsdk.LokateSDK
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _closestBeaconFlow = lokateSDK.getClosestBeaconFlow()
    private val closestBeaconFlow: SharedFlow<LokateBeacon?> = _closestBeaconFlow

    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked: StateFlow<Boolean> = _buttonClicked.asStateFlow()

    init {
        collectClosestBeacon()
    }

    private fun collectClosestBeacon() {
        viewModelScope.launch {
            closestBeaconFlow.collect {
                logger.i { "Closest beacon changed: $it" }
                if (it != null) {
                    updateCampaign(it.campaignName)
                }
            }
        }
    }

    val customerId: String
        get() = lokateSDK.getCustomerId()

    val isLokateRunning: Boolean = lokateSDK.isRunning()

    private val _currentCampaignName = MutableStateFlow<String?>(null)
    private val _nextCampaignName = MutableStateFlow<String?>(null)

    val currentCampaignName: StateFlow<String?> = _currentCampaignName.asStateFlow()
    val nextCampaignName: StateFlow<String?> = _nextCampaignName.asStateFlow()

    private fun updateCampaign(newCampaignName: String?) {
        if (newCampaignName != null) {
            viewModelScope.launch {
                _currentCampaignName.value = newCampaignName
                _nextCampaignName.value = getNextCampaign(customerId)
            }
        }
    }

    fun toggleLokate() {
        if (!isLokateRunning) {
            lokateSDK.startScanning()
            _buttonClicked.value = true
        }
    }
}
