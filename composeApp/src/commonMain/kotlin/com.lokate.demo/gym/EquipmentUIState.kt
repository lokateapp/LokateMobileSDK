package com.lokate.demo.gym

data class EquipmentUIState(
    val title: String,
    val videoPath: String,
)

val benchPress =
    EquipmentUIState(
        title = "Bench Press",
        videoPath = "files/gym/",
    )
val deadlift =
    EquipmentUIState(
        title = "Deadlift",
        videoPath = "files/gym/",
    )
val pullUp =
    EquipmentUIState(
        title = "Pull-up",
        videoPath = "files/gym/",
    )
val squat =
    EquipmentUIState(
        title = "Squat",
        videoPath = "files/gym/",
    )
