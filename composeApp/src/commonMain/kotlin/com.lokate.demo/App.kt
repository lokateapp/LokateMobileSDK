package com.lokate.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lokate.demo.csfair.CSFairApp
import com.lokate.demo.gym.GymApp
import com.lokate.demo.gym.GymViewModel
import com.lokate.demo.market.MarketApp
import com.lokate.demo.market.MarketViewModel
import com.lokate.demo.museum.MuseumApp
import com.lokate.demo.museum.MuseumViewModel
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
            permission,
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
        var isVisible by remember { mutableStateOf(false) }
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
                                    NavigationItem("Market", Icons.Default.ShoppingCart) {
                                        navigator.navigate(
                                            "/market",
                                            options = NavOptions(launchSingleTop = true),
                                        )
                                    },
                                    NavigationItem("CSFair", Icons.Default.Celebration) {
                                        navigator.navigate(
                                            "/csfair",
                                            options = NavOptions(launchSingleTop = true),
                                        )
                                    },
                                    NavigationItem("Gym", Icons.Default.SportsGymnastics) {
                                        navigator.navigate(
                                            "/gym",
                                            options = NavOptions(launchSingleTop = true),
                                        )
                                    },
                                    NavigationItem("Museum", Icons.Default.Museum) {
                                        navigator.navigate(
                                            "/museum",
                                            options = NavOptions(launchSingleTop = true),
                                        )
                                    },
                                ),
                                isVisible = isVisible,
                                isVisibleChanged = { isVisible = it },
                            )
                        },
                    ) {
                        Nav(navigator = navigator, if (isVisible) it else PaddingValues(0.dp))
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
fun Nav(
    navigator: Navigator = rememberNavigator(),
    paddingValues: PaddingValues,
) {
    NavHost(
        modifier = Modifier.padding(paddingValues),
        navigator = navigator,
        // Navigation transition for the scenes in this NavHost, this is optional
        navTransition = NavTransition(),
        // The start destination
        initialRoute = "/market",
    ) {
        scene(
            "/market",
            navTransition = NavTransition(),
        ) {
            val vm = koinViewModel(MarketViewModel::class)
            MarketApp(vm)
        }
        scene("/csfair", navTransition = NavTransition()) {
            CSFairApp()
        }
        scene("/gym", navTransition = NavTransition()) {
            val vm = koinViewModel(GymViewModel::class)
            GymApp(vm)
        }
        scene("/museum", navTransition = NavTransition()) {
            val vm = koinViewModel(MuseumViewModel::class)
            MuseumApp(vm)
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun NavigationBar(
    items: List<NavigationItem>,
    isVisible: Boolean,
    isVisibleChanged: (Boolean) -> Unit = {},
) {
    LaunchedEffect(isVisible) {
        delay(5000)
        isVisibleChanged(false)
    }
    Box(
        modifier =
            Modifier.fillMaxWidth(if (isVisible) 1f else 0.1f)
                .fillMaxHeight(0.1f)
                .clickable(enabled = !isVisible) {
                    isVisibleChanged(!isVisible)
                }
                .background(Color.Transparent),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (isVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                items.forEach {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(
                            modifier = Modifier.aspectRatio(1f),
                            onClick = it.onClick,
                            shape = RoundedCornerShape(0.dp),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(it.icon, null)
                                Text(it.title, fontSize = 8.sp)
                            }
                        }
                    }
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
                    permission,
                )
            }
            hasPermissions.value = checkPermissions(permissionsController)
        }
    }) {
        Text("Grant Permissions")
    }
}
