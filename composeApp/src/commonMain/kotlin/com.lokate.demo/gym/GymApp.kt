package com.lokate.demo.gym

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.lokate.demo.common.CampaignExperience
import com.lokate.demo.common.DemoType
import com.lokate.demo.common.LokateDemoStartScreen
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

@Composable
fun GymApp(vm: GymViewModel) {
    val buttonClicked by vm.buttonClicked.collectAsStateWithLifecycle()
    val currentCampaignName by vm.currentCampaignName.collectAsStateWithLifecycle()
    val nextCampaignName by vm.nextCampaignName.collectAsStateWithLifecycle()

    GymScreen(buttonClicked, vm::toggleLokate, currentCampaignName, nextCampaignName)
}

@Composable
fun GymScreen(
    buttonClicked: Boolean,
    onButtonClick: () -> Unit,
    currentCampaignName: String?,
    nextCampaignName: String?,
) {
    if (!buttonClicked) {
        LokateDemoStartScreen(DemoType.GYM, onButtonClick)
    } else {
        CampaignExperience(nextCampaignName) {
            GymVideo(currentCampaignName)
        }
    }
}

@Composable
fun GymVideo(campaignName: String?) {
    if (campaignName != null) {
        Text(campaignName)
    } else {
        Text("No nearby gym equipment")
    }
}
