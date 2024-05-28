package com.lokate.demo.game

import com.lokate.demo.common.base.LokateViewModel
import com.lokate.kmmsdk.domain.model.beacon.LokateBeacon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class GameViewModel : LokateViewModel() {
    private val _closestColorUIState = MutableStateFlow<ColorUIState?>(null)
    val closestColorUIState = _closestColorUIState.asStateFlow()

    private var canStart: Boolean = false

    private val levels = listOf(
        listOf(Color.Red, Color.Yellow, Color.White),
        listOf(Color.Red, Color.Yellow, Color.White, Color.Red),
        listOf(Color.Red, Color.Yellow, Color.White, Color.Red, Color.Magenta),
        listOf(Color.Red, Color.Yellow, Color.White, Color.Red, Color.Magenta, Color.White),
    )

    private var currentLevel = 0
    private var currentStep = 0

    override fun toggleLokate(){
        super.toggleLokate()
        startNext()
    }

    init {
        collectClosestBeacon()
    }

    private fun startNext() {
        viewModelScope.launch {
            delay(1000)
            startNextLevel()
        }
    }

    private fun collectClosestBeacon() {
        viewModelScope.launch {
            closestBeaconFlow.collect {
                logger.d { "Closest beacon changed: $it" }
                if(canStart) {
                    if (it != null) {
                        val mapped = it.toColorUIState()
                        _closestColorUIState.emit(mapped)
                        if (mapped != null) {
                            checkColor(mapped.color)
                        }
                    }
                }
            }
        }
    }

    private suspend fun startNextLevel() {
        if (currentLevel < levels.size) {
            currentStep = 0
            for (color in levels[currentLevel]) {
                _closestColorUIState.emit(color.toColorUIState())
                delay(500)
            }
            canStart = true
        } else {
            // Game over or handle game completion
        }
    }

    private fun checkColor(color: Color) {
        if (currentLevel < levels.size && currentStep < levels[currentLevel].size) {
            if (color == levels[currentLevel][currentStep]) {
                currentStep++
                if (currentStep == levels[currentLevel].size) {
                    viewModelScope.launch {
                        currentLevel++
                        canStart = false
                        startNextLevel()
                    }
                }
            } else {
                // Wrong color selected, handle game over
                viewModelScope.launch {
                    logger.d {"current level: $currentLevel"}
                    _closestColorUIState.emit(Color.Black.toColorUIState())
                }
            }
        }
    }

    private fun LokateBeacon.toColorUIState(): ColorUIState? {
        return when (this.campaignName) {
            "pink" -> pinkColor
            "red" -> redColor
            "white" -> whiteColor
            "yellow" -> yellowColor
            else -> null
        }
    }

    private fun Color.toColorUIState(): ColorUIState? {
        return when (this) {
            Color.Magenta -> pinkColor
            Color.Red -> redColor
            Color.White -> whiteColor
            Color.Yellow -> yellowColor
            Color.Black -> blackColor
            else -> null
        }
    }
}