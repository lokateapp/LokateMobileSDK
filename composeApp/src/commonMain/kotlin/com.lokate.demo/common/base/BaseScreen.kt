package com.lokate.demo.common.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.lokate.demo.common.LokateDemoStartScreen
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

@Composable
fun BaseScreen(vm: LokateViewModel, type:Screen, content: @Composable (vm: LokateViewModel) -> Unit) {
    val buttonClicked by vm.buttonClicked.collectAsStateWithLifecycle()
    if(!buttonClicked) {
        LokateDemoStartScreen(type, vm::toggleLokate)
    } else {
        content(vm)
    }
}