package com.lokate.demo.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun CampaignExperience(
    nextCampaignUIState: NextCampaignUIState?,
    currentCampaignExperience: @Composable () -> Unit,
) {
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
                currentCampaignExperience()
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
                if (nextCampaignUIState != null) {
                    // Icon()
                    Text(
                        text =
                            buildAnnotatedString {
                                append("İzlediğiniz rotaya göre sıradaki önerim:")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                                    append(nextCampaignUIState.information)
                                }
                            },
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                } else {
                    Text(
                        text = "Rotanıza uygun sıradaki ürünü belirleyemedim. Alışverişinize devam ettikçe size özel sıradaki ürünleri buradan paylaşacağım.",
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
    }
}
