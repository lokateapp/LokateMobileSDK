package com.lokate.demo.csfair

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.lokate.demo.common.CommonSurface
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

@Composable
fun CSFairApp(vm: CSFairViewModel) {
    val closestStandUIState by vm.closestStandUIState.collectAsStateWithLifecycle(null)

    CSFairScreen(closestStandUIState)
}

@Composable
fun CSFairScreen(closestStandUIState: StandUIState?) {
    CommonSurface {
        Stand(closestStandUIState)
    }
}

@Composable
fun Stand(closestStandUIState: StandUIState?) {
    if (closestStandUIState != null) {
        Text("Closest stand: ${closestStandUIState.title}")
    } else {
        Text("No nearby stand")
    }
}
