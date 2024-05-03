package com.lokate.demo.market

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

class MarketViewModel : ViewModel(), KoinComponent {

    private val logger = logging("MarketViewModel")

    private val lokateSDK: LokateSDK = get()

    private val _closestBeaconFlow = lokateSDK.getClosestBeaconFlow()
    val closestBeaconFlow: SharedFlow<LokateBeacon?> = _closestBeaconFlow

    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked: StateFlow<Boolean> = _buttonClicked.asStateFlow()
    init {
        listenClosestBeacon()
    }
    fun listenClosestBeacon() {
        viewModelScope.launch {
            closestBeaconFlow.collect {
                logger.e{"Closest beacon changed: $it" }
                if (it != null) {
                    updateCampaign(it.campaignName)
                }
            }
        }
    }

    val customerId: String
        get() = lokateSDK.getCustomerId()

    val isLokateRunning: Boolean = lokateSDK.isRunning()

    private val _campaignName = MutableStateFlow<String?>(null)
    private val _affinedCampaigns = MutableStateFlow<List<String>>(emptyList())
    private val _notification = MutableStateFlow("No notification available")

    val campaignName: StateFlow<String?> = _campaignName.asStateFlow()
    val affinedCampaigns: StateFlow<List<String>> = _affinedCampaigns.asStateFlow()
    val notification: StateFlow<String> = _notification.asStateFlow()

    private fun updateCampaign(newCampaignName: String?) {
        if (newCampaignName != null) {
            viewModelScope.launch {
                _affinedCampaigns.value = getAffinedCampaigns(lokateSDK.getCustomerId())
                _campaignName.value = newCampaignName
                _notification.value = notificationPool[newCampaignName]?.random() ?: "No notification available"
            }
        }
    }

    fun toggleLokate() {
        if (!isLokateRunning) {
            lokateSDK.startScanning()
            _buttonClicked.value = true
            updateCampaign(null)
        }
    }
}
