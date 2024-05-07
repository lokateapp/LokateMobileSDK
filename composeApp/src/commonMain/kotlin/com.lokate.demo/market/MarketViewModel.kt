package com.lokate.demo.market

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

class MarketViewModel : ViewModel(), KoinComponent {
    private val logger = logging("MarketViewModel")

    private val lokateSDK: LokateSDK = get()

    private val closestBeaconFlow = lokateSDK.getClosestBeaconFlow()

    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked = _buttonClicked.asStateFlow()

    private val _closestDiscountUIState = MutableStateFlow<DiscountUIState?>(null)
    val closestDiscountUIState = _closestDiscountUIState.asStateFlow()

    private val _affinedCampaigns = MutableStateFlow<List<String>>(emptyList())
    val affinedCampaigns = _affinedCampaigns.asStateFlow()

    init {
        collectClosestBeacon()
    }

    private fun collectClosestBeacon() {
        viewModelScope.launch {
            closestBeaconFlow.collect {
                logger.d { "Closest beacon changed: $it" }
                if (it != null) {
                    val mapped = it.toDiscountUIState()
                    if (mapped != null) {
                        getAffinedCampaigns()
                        updateNextCampaign()
                    }
                    _closestDiscountUIState.emit(mapped)
                }
            }
        }
    }

    private val customerId: String
        get() = lokateSDK.getCustomerId()

    private val isLokateRunning: Boolean = lokateSDK.isRunning()

    private val _nextCampaignUIState = MutableStateFlow<NextCampaignUIState?>(null)
    val nextCampaignUIState = _nextCampaignUIState.asStateFlow()

    private fun getAffinedCampaigns() {
        viewModelScope.launch {
            _affinedCampaigns.value = getAffinedCampaigns(customerId)
        }
    }

    private fun updateNextCampaign() {
        viewModelScope.launch {
            _nextCampaignUIState.value = getNextCampaign(customerId).toNextCampaignUIState()
        }
    }

    private fun LokateBeacon.toDiscountUIState(): DiscountUIState? {
        return when (this.campaignName) {
            "pink" -> selfCare
            "red" -> electronics
            "white" -> cloth
            "yellow" -> homeAppliances
            else -> null
        }
    }

    private fun String?.toNextCampaignUIState(): NextCampaignUIState? {
        return when (this) {
            "Bira" -> bira
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
