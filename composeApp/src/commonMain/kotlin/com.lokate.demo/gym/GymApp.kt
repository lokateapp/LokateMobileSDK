package com.lokate.demo.gym

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.ui.VideoPlayerView
import com.lokate.demo.common.CampaignExperience
import com.lokate.demo.common.NextCampaignUIState
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun GymApp(vm: GymViewModel) {
    val closestEquipmentUIState by vm.closestEquipmentUIState.collectAsStateWithLifecycle(null)
    val nextCampaignUIState by vm.nextCampaignUIState.collectAsStateWithLifecycle(null)

    GymScreen(closestEquipmentUIState, nextCampaignUIState)
}

@Composable
fun GymScreen(
    closestEquipmentUIState: EquipmentUIState?,
    nextCampaignUIState: NextCampaignUIState?,
) {
    CampaignExperience(nextCampaignUIState) {
        Equipment(closestEquipmentUIState)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Equipment(closestEquipmentUIState: EquipmentUIState?) {
    val rotated = remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (rotated.value) 180f else 0f,
        animationSpec = tween(500),
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!rotated.value) 1f else 0f,
        animationSpec = tween(500),
    )

    val animateBack by animateFloatAsState(
        targetValue = if (rotated.value) 1f else 0f,
        animationSpec = tween(500),
    )

    LaunchedEffect(closestEquipmentUIState) {
        rotated.value = false
    }

    if (closestEquipmentUIState != null) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 8 * density
                    }
                    .clickable {
                        rotated.value = !rotated.value
                    },
            shape = RoundedCornerShape(14.dp),
        ) {
            if (!rotated.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Image(
                        painter = painterResource(DrawableResource(closestEquipmentUIState.imagePath)),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().graphicsLayer { alpha = animateFront },
                        contentScale = ContentScale.FillBounds,
                    )

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = closestEquipmentUIState.title,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else {
                VideoPlayerView(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = animateBack
                                rotationY = rotation
                            }
                            .clickable {
                                rotated.value = !rotated.value
                            },
                    showSeekBar = true,
                    url = closestEquipmentUIState.videoPath,
                )
            }
        }
    } else {
        Text("No nearby gym equipment")
    }
}
