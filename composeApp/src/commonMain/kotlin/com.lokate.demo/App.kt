package com.lokate.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lokate.demo.csfair.CSFairApp
import com.lokate.demo.gym.GymApp
import com.lokate.demo.market.MarketApp
import com.lokate.demo.market.MarketViewModel
import com.lokate.demo.museum.MuseumApp
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

internal val RequiredPermissions =
    arrayOf(
        Permission.LOCATION,
        Permission.COARSE_LOCATION,
        // Permission.BLUETOOTH_SCAN,
    )

suspend fun checkPermissions(permissionsController: PermissionsController): Boolean {
    return RequiredPermissions.all { permission ->
        permissionsController.isPermissionGranted(
            permission
        )
    }
}

// TODO: handle denied exceptions properly (redirect user to settings etc.)
// should also check if bluetooth and internet are open
suspend fun askPermission(
    permissionsController: PermissionsController,
    permission: Permission,
) {
    try {
        permissionsController.providePermission(permission)
    } catch (deniedAlways: DeniedAlwaysException) {
        println("$permission is always denied")
    } catch (denied: DeniedException) {
        println("$permission is denied")
    }
}

@Composable
fun App() {
    val permissionsControllerFactory = rememberPermissionsControllerFactory()
    val permissionsController =
        remember(permissionsControllerFactory) { permissionsControllerFactory.createPermissionsController() }
    val coroutineScope = rememberCoroutineScope()
    val hasPermissions = remember { mutableStateOf(false) }

    BindEffect(permissionsController)
    val text = mutableStateOf("")
    coroutineScope.launch {
        hasPermissions.value = checkPermissions(permissionsController)
    }
    LaunchedEffect(hasPermissions) {
        hasPermissions.value = checkPermissions(permissionsController)
        text.value = ""
        RequiredPermissions.forEach {
            text.value += "${it.name}: ${permissionsController.isPermissionGranted(it)}\n"
        }
    }
    PreComposeApp {
        val navigator = rememberNavigator()
        MyApplicationTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background,
            ) {
                if (hasPermissions.value) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                listOf(
                                    NavigationItem("Market") {
                                        navigator.navigate(
                                            "/market",
                                            options = NavOptions(launchSingleTop = true)
                                        )
                                    },
                                    NavigationItem("CSFair") {
                                        navigator.navigate(
                                            "/csfair",
                                            options = NavOptions(launchSingleTop = true)
                                        )

                                    },
                                    NavigationItem("Gym") {
                                        navigator.navigate(
                                            "/gym",
                                            options = NavOptions(launchSingleTop = true)
                                        )
                                    },
                                    NavigationItem("Museum") {
                                        navigator.navigate(
                                            "/museum",
                                            options = NavOptions(launchSingleTop = true)
                                        )
                                    },
                                )
                            )
                        }
                    ) {
                        Nav(navigator = navigator, it)
                    }
                } else {
                    Column(Modifier.fillMaxHeight()) {
                        Box(Modifier.fillMaxHeight(0.3f)) {
                            PermissionScreen(permissionsController, coroutineScope, hasPermissions)
                        }
                        Text(text.value)
                    }
                }
            }
        }
    }
}

@Composable
fun Nav(navigator: Navigator = rememberNavigator(), paddingValues: PaddingValues) {

    NavHost(
        modifier = Modifier.padding(paddingValues),
        navigator = navigator,
        // Navigation transition for the scenes in this NavHost, this is optional
        navTransition = NavTransition(),
        // The start destination
        initialRoute = "/market"
    )
    {
        scene(
            "/market", navTransition = NavTransition()
        ) {
            val vm = koinViewModel(MarketViewModel::class)
            MarketApp(vm)
        }
        scene("/csfair", navTransition = NavTransition()) {
            CSFairApp()
        }
        scene("/gym", navTransition = NavTransition()) {
            GymApp()
        }
        scene("/museum", navTransition = NavTransition()) {
            MuseumApp()
        }
    }
}

data class NavigationItem(val title: String, val onClick: () -> Unit)

@Composable
fun NavigationBar(items: List<NavigationItem>) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) {
        delay(5000)
        isVisible = false
    }
    Box(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f)
            .clickable(enabled = !isVisible) {
                isVisible = true
            }
            .background(Color.Transparent),
        contentAlignment = androidx.compose.ui.Alignment.CenterStart
    ) {
        if (isVisible)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEach {
                    Button(onClick = it.onClick) {
                        Text(it.title)
                    }
                }
            }
    }
}

@Composable
fun PermissionScreen(
    permissionsController: PermissionsController,
    coroutineScope: CoroutineScope,
    hasPermissions: MutableState<Boolean>,
) {
    Button(onClick = {
        coroutineScope.launch {
            RequiredPermissions.forEach { permission ->
                askPermission(
                    permissionsController,
                    permission
                )
            }
            hasPermissions.value = checkPermissions(permissionsController)
        }
    }) {
        Text("Grant Permissions")
    }
}
