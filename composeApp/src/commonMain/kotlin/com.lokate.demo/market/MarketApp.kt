package com.lokate.demo.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

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
        if (closestDiscountUIState == null) {
            Discount("Kapsama alanı dışı", null)
        } else {
                Discount(closestDiscountUIState.pool[0], closestDiscountUIState.imagePath)
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Discount(
    message: String,
    imagePath: String?,
) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        border = BorderStroke(2.dp, Color.Gray),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = message,
                    modifier = Modifier.wrapContentSize(),
                    textAlign = TextAlign.Center,
                    style =
                        MaterialTheme.typography.subtitle1
                            .copy(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (imagePath != null) {
                    Image(
                        painter = painterResource(DrawableResource(imagePath)),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
