package com.lokate.demo.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

enum class DemoType {
    MARKET,
    MUSEUM,
    GYM,
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LokateDemoStartScreen(
    demoType: DemoType,
    onButtonClick: () -> Unit,
) {
    val backgroundPainter: Painter = when (demoType) {
        DemoType.MARKET -> painterResource(DrawableResource("files/market/background.jpg"))
        DemoType.MUSEUM -> painterResource(DrawableResource("files/museum/background.jpg"))
        DemoType.GYM -> painterResource(DrawableResource("files/gym/background.jpg"))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onButtonClick,
            ) {
                Text(text = "Start Scanning")
            }
        }
    }
}
