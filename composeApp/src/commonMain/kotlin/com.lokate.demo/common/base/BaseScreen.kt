package com.lokate.demo.common.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.lokate.demo.common.LokateDemoStartScreen
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun BaseScreen(type: Screen) {
    val vm = koinViewModel(type.getVM())
    val buttonClicked by vm.buttonClicked.collectAsStateWithLifecycle()
    if (!buttonClicked) {
        LokateDemoStartScreen(type, vm::toggleLokate)
    } else {
        type.getScreen(vm)
    }
}
