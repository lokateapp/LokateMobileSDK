package com.lokate.demo.museum

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.lokate.demo.utils.TextFlow
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource

@Composable
fun MuseumApp(vm: MuseumViewModel) {
    val closestExhibitionUIState by vm.closestExhibition.collectAsStateWithLifecycle(null)
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle(false)
    val nextExhibitionUIState by vm.nextExhibition.collectAsStateWithLifecycle(null)
    MuseumScreen(closestExhibitionUIState, nextExhibitionUIState, vm::play, isPlaying)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MuseumScreen(
    closestExhibitionUIState: ExhibitionUIState?,
    nextExhibition: ExhibitionUIState?,
    play: () -> Unit,
    isPlaying: Boolean,
) {
    val scrollState = rememberScrollState()
    val fabVisibility = remember { mutableStateOf(true) }

    LaunchedEffect(scrollState.isScrollInProgress) {
        fabVisibility.value = !scrollState.isScrollInProgress
    }

    if (closestExhibitionUIState == null) {
        Text("No exhibition found")
    } else {
        Scaffold(
            floatingActionButton = {
                if (fabVisibility.value) {
                    FloatingActionButton(onClick = play) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Information",
                        )
                    }
                }
            },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Card(
                    modifier =
                        Modifier.padding(4.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(.75f)
                            .verticalScroll(scrollState),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, Color.Black),
                ) {
                    TextFlow(
                        text = closestExhibitionUIState.description,
                        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(4.dp),
                    ) {
                        closestExhibitionUIState.imagePath?.let {
                            Image(
                                painter = BitmapPainter(imageResource(DrawableResource(it))),
                                contentDescription = null,
                                modifier = Modifier.size(192.dp),
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, Color.Black),
                ) { // use the whole remaining size
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Next one is: ${nextExhibition?.description ?: "No next exhibition found"}")
                    }
                }
            }
        }
    }
}
