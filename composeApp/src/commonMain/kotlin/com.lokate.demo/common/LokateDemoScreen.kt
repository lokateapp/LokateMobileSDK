package com.lokate.demo.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class DemoType {
    MARKET,
    MUSEUM,
    GYM,
}

@Composable
fun LokateDemoStartScreen(
    demoType: DemoType,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "LOKATE $demoType DEMO",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.primary),
            textAlign = TextAlign.Center,
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onButtonClick,
        ) {
            Text(text = "Start Scanning")
        }
    }
}
