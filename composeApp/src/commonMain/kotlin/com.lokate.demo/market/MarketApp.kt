package com.lokate.demo.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarketApp(vm: MarketViewModel) {
    val buttonClicked by vm.buttonClicked.collectAsState()
    val campaignName by vm.campaignName.collectAsState()
    val affinedCampaigns by vm.affinedCampaigns.collectAsState()
    val notification by vm.notification.collectAsState()

    GreetingView(buttonClicked, vm::toggleLokate, campaignName, affinedCampaigns, notification)
}

@Composable
fun GreetingView(
    buttonClicked: Boolean,
    onButtonClick: () -> Unit,
    campaignName: String?,
    affinedCampaigns: List<String>,
    notification: String
){
    if (!buttonClicked) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Lokate Demo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                style = MaterialTheme.typography.h1.copy(color = MaterialTheme.colors.primary),
                textAlign = TextAlign.Center
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onButtonClick
            ) {
                Text(text = "Start Scanning")
            }
        }
    } else {
        CampaignExperience(campaignName, affinedCampaigns, notification)
    }
}

@Composable
fun CampaignExperience(
    campaignName: String?,
    affinedCampaigns: List<String>,
    notification: String
) {
    if (campaignName != null) {
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
                    modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    modifier = Modifier.fillMaxSize().padding(16.dp),
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
    else {
        Notification("No campaign available")
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
