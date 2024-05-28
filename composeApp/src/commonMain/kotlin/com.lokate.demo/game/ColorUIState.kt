package com.lokate.demo.game

import androidx.compose.ui.graphics.Color

data class ColorUIState(
    val color: Color,
)

val pinkColor =
    ColorUIState(
        color = Color.Magenta
    )
val redColor =
    ColorUIState(
        color = Color.Red
    )
val whiteColor =
    ColorUIState(
        color = Color.LightGray
    )
val yellowColor =
    ColorUIState(
        color = Color.Yellow
    )
val blackColor =
    ColorUIState(
        color = Color.Black
    )
