package com.lokate.demo.museum

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.lokate.demo.utils.TextFlow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource

@Composable
fun MuseumApp(vm: MuseumViewModel) {
    val closestExhibitionUIState by vm.closestExhibition.collectAsState(null)
    val isPlaying by vm.isPlaying.collectAsState(false)
    MuseumScreen(closestExhibitionUIState, vm::play, isPlaying)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MuseumScreen(
    closestExhibitionUIState: ExhibitionUIState?,
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
            Card(
                modifier = Modifier.padding(4.dp).verticalScroll(scrollState),
                elevation = 4.dp,
                shape = RoundedCornerShape(4.dp),
            ) {
                TextFlow(
                    text = closestExhibitionUIState.description,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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
        }
    }
}
