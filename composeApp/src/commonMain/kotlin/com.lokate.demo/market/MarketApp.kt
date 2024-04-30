package com.lokate.demo.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lokate.kmmsdk.LokateSDK
import kotlinx.coroutines.flow.map

@Composable
fun MarketApp() {
    // val lokateSDK = LokateSDK.createForEstimoteMonitoring(BuildKonfig.ESTIMOTE_CLOUD_APP_ID, BuildKonfig.ESTIMOTE_CLOUD_APP_TOKEN)
    val lokateSDK = LokateSDK.getInstance(LokateSDK.BeaconScannerType.IBeacon)

    GreetingView(
        lokateSDK,
    )
}

@Composable
fun GreetingView(lokateSDK: LokateSDK) {
    // val scanResultState by lokateSDK.getClosestBeaconFlow().collectAsState(initial = "No scan result")
    val campaignState by lokateSDK.getClosestBeaconFlow().map {
        it?.campaignName ?: "No nearest campaign"
    }.collectAsState(initial = "No nearest campaign")
    var buttonClicked by remember { mutableStateOf(false) }

    if (!buttonClicked) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Header
            Text(
                text = "Lokate Demo",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                style = MaterialTheme.typography.h1.copy(color = MaterialTheme.colors.primary),
                textAlign = TextAlign.Center,
            )

            // Bottom section with button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    lokateSDK.startScanning()
                    buttonClicked = true
                },
            ) {
                Text(text = "Start Scanning")
            }
        }
    } else {
        if (campaignState == "No nearest campaign") {
            Text(
                text = "There are no campaigns nearby",
                modifier =
                    Modifier.width(100.dp)
                        .height(100.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .size(60.dp),
                textAlign = TextAlign.Center,
            )
        } else { // There is a nearby campaign
            CampaignExperience(campaignState, lokateSDK.getCustomerId())
        }
    }
}

@Composable
fun CampaignExperience(campaignName: String, customerId: String) {
    var affinedCampaigns by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(campaignName) {
        val campaigns = getAffinedCampaigns(customerId)
        affinedCampaigns = campaigns
    }

    val notification = notificationPool[campaignName]?.random() ?: "No notification available"

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Upper part, current campaign experience (3/4 of the screen)
        Surface(
            modifier = Modifier.weight(3f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, Color.Black),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (campaignName in affinedCampaigns) {
                    Notification(notification)
                } else {
                    Notification("No relevant campaign in the current location")
                }
            }
        }

        // Bottom part, next campaign experience (1/4 of the screen)
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, Color.Black),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Next campaign: to be implemented",
                    modifier = Modifier.wrapContentSize(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
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
