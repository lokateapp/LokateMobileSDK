package com.lokate.demo.market

import com.lokate.demo.common.base.LokateViewModel
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

class MarketViewModel : LokateViewModel() {
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
                        updateLocationBasedRecommendations()
                    }
                    _closestDiscountUIState.emit(mapped)
                }
            }
        }
    }

    private val _nextCampaignUIState = MutableStateFlow<NextCampaignUIState?>(null)
    val nextCampaignUIState = _nextCampaignUIState.asStateFlow()

    private fun updateLocationBasedRecommendations() {
        viewModelScope.launch {
            val (affinedCampaigns, nextCampaign) = getLocationBasedRecommendations(customerId)
            _affinedCampaigns.value = affinedCampaigns
            _nextCampaignUIState.value = nextCampaign.toNextCampaignUIState()
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
}
