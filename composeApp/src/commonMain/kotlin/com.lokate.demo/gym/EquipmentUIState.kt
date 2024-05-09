package com.lokate.demo.gym

data class EquipmentUIState(
    val title: String,
    val imagePath: String,
    val videoPath: String,
)

val benchPress =
    EquipmentUIState(
        title = "Bench Press",
        imagePath = "files/gym/bench_press.jpg",
        videoPath = "https://storage.googleapis.com/lokate-demo-gym/bench_press.webm",
    )
val cableRow =
    EquipmentUIState(
        title = "Cable Row",
        imagePath = "files/gym/cable_row.jpg",
        videoPath = "https://storage.googleapis.com/lokate-demo-gym/cable_row.mp4",
    )
val latPulldown =
    EquipmentUIState(
        title = "Lat Pulldown",
        imagePath = "files/gym/lat_pulldown.jpg",
        videoPath = "https://storage.googleapis.com/lokate-demo-gym/lat_pulldown.mp4",
    )
val squat =
    EquipmentUIState(
        title = "Squat",
        imagePath = "files/gym/squat.jpg",
        videoPath = "https://storage.googleapis.com/lokate-demo-gym/squat.webm",
    )
