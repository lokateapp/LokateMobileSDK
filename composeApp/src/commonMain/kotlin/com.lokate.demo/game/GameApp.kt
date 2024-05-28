package com.lokate.demo.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.lokate.demo.common.CommonSurface
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun GameApp(vm: GameViewModel) {
    val closestColorUIState by vm.closestColorUIState.collectAsStateWithLifecycle(null)
    GameScreen(closestColorUIState)
}

@Composable
fun GameScreen(closestColorUIState: ColorUIState?) {
    CommonSurface {
        Color(closestColorUIState)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Color(closestColorUIState: ColorUIState?) {
    if (closestColorUIState != null) {
        Surface(modifier = Modifier.fillMaxSize(), color = closestColorUIState.color) {
        }
    } else {
        Text("No nearby beacons!")
    }
}
