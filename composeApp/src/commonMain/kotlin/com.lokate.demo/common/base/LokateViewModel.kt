package com.lokate.demo.common.base

import com.lokate.kmmsdk.LokateSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.lighthousegames.logging.logging

open class LokateViewModel : ViewModel(), KoinComponent {
    protected val logger = logging(this::class.simpleName)

    private val lokateSDK: LokateSDK = get()
    protected val closestBeaconFlow = lokateSDK.getClosestBeaconFlow()

    protected val customerId: String
        get() = lokateSDK.getCustomerId()

    private val isLokateRunning: Boolean = lokateSDK.isRunning()
    private val _buttonClicked = MutableStateFlow(isLokateRunning)
    val buttonClicked: StateFlow<Boolean> = _buttonClicked.asStateFlow()

    fun toggleLokate() {
        if (!isLokateRunning) {
            lokateSDK.startScanning()
            _buttonClicked.value = true
        }
    }
}
