package com.lokate.demo.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lokate.kmmsdk.LokateSDK

@Composable
fun MarketApp() {
    // val lokateSDK = LokateSDK.createForEstimoteMonitoring(BuildKonfig.ESTIMOTE_CLOUD_APP_ID, BuildKonfig.ESTIMOTE_CLOUD_APP_TOKEN)
    val lokateSDK = LokateSDK.createForIBeacon()

    GreetingView(
        lokateSDK,
    )
}

@Composable
fun GreetingView(lokateSDK: LokateSDK) {
    val scanResultState by lokateSDK.getScanResultFlow().collectAsState(initial = "No scan result")

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        // Add some padding to the entire content
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

        // Middle section with text field
        Text(
            text = scanResultState.toString(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(bottom = 32.dp, top = 16.dp)
                    .size(20.dp),
            textAlign = TextAlign.Center,
        )

        // Bottom section with button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { lokateSDK.startScanning() },
        ) {
            Text(text = "Start Scanning")
        }
    }
}
