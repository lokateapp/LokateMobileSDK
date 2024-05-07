package com.lokate.demo.gym

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.lokate.demo.common.CampaignExperience
import com.lokate.demo.common.DemoType
import com.lokate.demo.common.LokateDemoStartScreen
import com.lokate.demo.common.NextCampaignUIState
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

@Composable
fun GymApp(vm: GymViewModel) {
    val buttonClicked by vm.buttonClicked.collectAsStateWithLifecycle()
    val closestEquipmentUIState by vm.closestEquipmentUIState.collectAsStateWithLifecycle(null)
    val nextCampaignUIState by vm.nextCampaignUIState.collectAsStateWithLifecycle(null)

    GymScreen(buttonClicked, vm::toggleLokate, closestEquipmentUIState, nextCampaignUIState)
}

@Composable
fun GymScreen(
    buttonClicked: Boolean,
    onButtonClick: () -> Unit,
    closestEquipmentUIState: EquipmentUIState?,
    nextCampaignUIState: NextCampaignUIState?,
) {
    if (!buttonClicked) {
        LokateDemoStartScreen(DemoType.GYM, onButtonClick)
    } else {
        CampaignExperience(nextCampaignUIState) {
            Equipment(closestEquipmentUIState)
        }
    }
}

@Composable
fun Equipment(closestEquipmentUIState: EquipmentUIState?) {
    if (closestEquipmentUIState != null) {
        Text(closestEquipmentUIState.toString())
    } else {
        Text("No nearby gym equipment")
    }
}
