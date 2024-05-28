package com.lokate.demo.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.lokate.demo.common.base.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LokateDemoStartScreen(
    demoType: Screen,
    onButtonClick: () -> Unit,
) {
    val backgroundPainter: Painter =
        when (demoType) {
            Screen.MarketScreen -> painterResource(DrawableResource("files/market/background.jpg"))
            Screen.MuseumScreen -> painterResource(DrawableResource("files/museum/background.jpg"))
            Screen.GymScreen -> painterResource(DrawableResource("files/gym/background.jpg"))
            // Screen.GameScreen -> painterResource(DrawableResource("files/game/background.jpg"))
        }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
