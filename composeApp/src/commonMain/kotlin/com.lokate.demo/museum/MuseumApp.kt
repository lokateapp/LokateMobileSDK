package com.lokate.demo.museum

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.lokate.demo.common.CampaignExperience
import com.lokate.demo.common.NextCampaignUIState
import com.lokate.demo.utils.TextFlow
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun MuseumApp(vm: MuseumViewModel) {
    val buttonClicked by vm.buttonClicked.collectAsStateWithLifecycle()
    val closestExhibitionUIState by vm.closestExhibitionUIState.collectAsStateWithLifecycle(null)
    val nextCampaignUIState by vm.nextCampaignUIState.collectAsStateWithLifecycle(null)
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle(false)

    MuseumScreen(
        closestExhibitionUIState,
        nextCampaignUIState,
        vm::play,
        isPlaying,
    )
}

@Composable
fun MuseumScreen(
    closestExhibitionUIState: ExhibitionUIState?,
    nextCampaignUIState: NextCampaignUIState?,
    play: () -> Unit,
    isPlaying: Boolean,
) {
    CampaignExperience(nextCampaignUIState) {
        Exhibition(closestExhibitionUIState, play, isPlaying)
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun Exhibition(
    closestExhibitionUIState: ExhibitionUIState?,
    play: () -> Unit,
    isPlaying: Boolean,
) {
    val scrollState = rememberScrollState()
    val fabVisibility = remember { mutableStateOf(true) }
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

    LaunchedEffect(scrollState.isScrollInProgress) {
        fabVisibility.value = !scrollState.isScrollInProgress
    }

    LaunchedEffect(closestExhibitionUIState) {
        rotated.value = false
    }

    if (closestExhibitionUIState == null) {
        Text("No nearby exhibitions")
    } else {
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
                Image(
                    painter = painterResource(DrawableResource(closestExhibitionUIState.imagePath)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().graphicsLayer { alpha = animateFront },
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                Scaffold(
                    floatingActionButton = {
                        if (fabVisibility.value) {
                            FloatingActionButton(onClick = play) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    modifier = Modifier.graphicsLayer { rotationY = rotation },
                                    contentDescription = "Information",
                                )
                            }
                        }
                    },
                ) {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState).graphicsLayer {
                                    alpha = animateBack
                                    rotationY = rotation
                                },
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(2.dp, Color.Black),
                    ) {
                        TextFlow(
                            text = closestExhibitionUIState.description,
                            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
                        )
                    }
                }
            }
        }
    }
}
