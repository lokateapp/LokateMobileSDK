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
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

@Composable
fun MarketApp(vm: MarketViewModel) {
    val closestDiscountUIState by vm.closestDiscountUIState.collectAsStateWithLifecycle()
    val nextCampaignUIState by vm.nextCampaignUIState.collectAsStateWithLifecycle()
    val affinedCampaigns by vm.affinedCampaigns.collectAsStateWithLifecycle()

    MarketScreen(
        closestDiscountUIState,
        nextCampaignUIState,
        affinedCampaigns,
    )
}

@Composable
fun MarketScreen(
    closestDiscountUIState: DiscountUIState?,
    nextCampaignUIState: NextCampaignUIState?,
    affinedCampaigns: List<String>,
) {
    CampaignExperience(nextCampaignUIState) {
        if (closestDiscountUIState != null) {
            if (closestDiscountUIState.category in affinedCampaigns) {
                Discount(closestDiscountUIState.pool.random())
            } else {
                Discount("No relevant discounts nearby")
            }
        } else {
            Discount("No nearby campaigns")
        }
    }
}

@Composable
fun Discount(message: String) {
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
