package com.lokate.demo.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lokate.demo.common.CampaignExperience
import com.lokate.demo.common.DemoType
import com.lokate.demo.common.LokateDemoStartScreen
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

@Composable
fun MarketApp(vm: MarketViewModel) {
    val buttonClicked by vm.buttonClicked.collectAsStateWithLifecycle()
    val currentCampaignName by vm.currentCampaignName.collectAsStateWithLifecycle()
    val nextCampaignName by vm.nextCampaignName.collectAsStateWithLifecycle()
    val affinedCampaigns by vm.affinedCampaigns.collectAsStateWithLifecycle()

    MarketScreen(buttonClicked, vm::toggleLokate, currentCampaignName, nextCampaignName, affinedCampaigns)
}

@Composable
fun MarketScreen(
    buttonClicked: Boolean,
    onButtonClick: () -> Unit,
    currentCampaignName: String?,
    nextCampaignName: String?,
    affinedCampaigns: List<String>,
) {
    if (!buttonClicked) {
        LokateDemoStartScreen(DemoType.MARKET, onButtonClick)
    } else {
        CampaignExperience(nextCampaignName) {
            if (currentCampaignName in affinedCampaigns) {
                val notification = notificationPool[currentCampaignName]?.random() ?: "No notification available for the current campaign"
                Notification(notification)
            } else {
                Notification("No relevant campaign in the current location")
            }
        }
    }
}

@Composable
fun Notification(message: String) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray,
        border = BorderStroke(2.dp, Color.Gray),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = message,
                modifier = Modifier.wrapContentSize(),
                textAlign = TextAlign.Center,
                style =
                    MaterialTheme.typography.subtitle1
                        .copy(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black),
            )
        }
    }
}
