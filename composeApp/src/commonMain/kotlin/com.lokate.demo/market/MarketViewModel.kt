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
                    if (mapped != null && mapped != closestDiscountUIState.value) {
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
            _nextCampaignUIState.value = nextCampaign.toNextCampaignUIState()
            _affinedCampaigns.value = affinedCampaigns
        }
    }

    private fun LokateBeacon.toDiscountUIState(): DiscountUIState? {
        return when (this.campaignName) {
            "giris" -> giris
            "bebek bezi" -> bebekBezi
            "kuruyemis" -> kuruyemis
            "bira" -> bira
            else -> null
        }
    }

    private fun String?.toNextCampaignUIState(): NextCampaignUIState? {
        return when (this) {
            "bebek bezi" -> bebekBeziNext
            "kuruyemis" -> kuruyemisNext
            "bira" -> biraNext
            "ekmek" -> ekmekNext
            "bakliyat" -> bakliyatNext
            "konserve" -> konserveNext
            "kasap" -> kasapNext
            "icecek" -> icecekNext
            "bulasik" -> bulasikNext
            "deterjan" -> deterjanNext
            "sut" -> sutNext
            "cay" -> cayNext
            "dondurma" -> dondurmaNext
            "cips" -> cipsNext
            "kahve" -> kahveNext
            "cikolata" -> cikolataNext
            "dondurulmus hazir gida" -> dondurulmusHazirGidaNext
            "kisisel bakim" -> kisiselBakimNext
            "sarap" -> sarapNext
            "kasa" -> kasaNext
            "sandvic" -> sandvicNext
            "kagit" -> kagitNext
            "kalem" -> kalemNext
            "defter" -> defterNext
            else -> null
        }
    }
}
