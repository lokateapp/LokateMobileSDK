package market

import Greeting
import MyApplicationTheme
import PermissionHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import lokatecmp.composeapp.generated.resources.Res
import lokatecmp.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun MarketApp(permissionHandler: PermissionHandler, /* beaconScanner: BeaconScanner */) {
    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            GreetingView(
                Greeting().greet(),
                permissionHandler::getPermissions,
                {
                    // beaconScanner.setRegions(listOf())
                }
            )
        }
    }
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}

@Composable
fun GreetingView(
    greetingText: String,
    permissionCheck: (() -> Unit) -> Unit,
    startScan: () -> Unit,
    textState: State<String> = mutableStateOf("") // TODO: use kmm.emptyString()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Add some padding to the entire content
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Text(
            text = "Lokate Demo for $greetingText",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            style = MaterialTheme.typography.h1.copy(color = MaterialTheme.colors.primary),
            textAlign = TextAlign.Center
        )

        // Middle section with text field
        Text(
            text = textState.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(bottom = 32.dp, top = 16.dp)
                .size(20.dp),
            textAlign = TextAlign.Center
        )

        // Bottom section with button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { permissionCheck(startScan) }
        ) {
            Text(text = "Start Scanning")
        }
    }
}
