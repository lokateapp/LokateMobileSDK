package com.lokate.demo.common.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.lokate.demo.NavigationItem
import com.lokate.demo.gym.GymApp
import com.lokate.demo.gym.GymViewModel
import com.lokate.demo.market.MarketApp
import com.lokate.demo.market.MarketViewModel
import com.lokate.demo.museum.MuseumApp
import com.lokate.demo.museum.MuseumViewModel
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.ViewModel

sealed class Screen(val title: String, val route: String, val navIcon: ImageVector) {
    data object MarketScreen :
        Screen(title = "Market", route = "/market", navIcon = Icons.Default.ShoppingCart)

    data object MuseumScreen :
        Screen(title = "Museum", route = "/museum", navIcon = Icons.Default.Museum)

    data object GymScreen :
        Screen(title = "Gym", route = "/gym", navIcon = Icons.Default.SportsGymnastics)
}

val ScreenList =
    listOf(
        Screen.MarketScreen,
        Screen.MuseumScreen,
        Screen.GymScreen,
    )

fun Screen.getVM() =
    when (this) {
        Screen.MarketScreen -> MarketViewModel::class
        Screen.MuseumScreen -> MuseumViewModel::class
        Screen.GymScreen -> GymViewModel::class
    }

@Composable
fun Screen.getScreen(vm: ViewModel) =
    when (this) {
        Screen.MarketScreen -> MarketApp(vm as MarketViewModel)
        Screen.MuseumScreen -> MuseumApp(vm as MuseumViewModel)
        Screen.GymScreen -> GymApp(vm as GymViewModel)
    }

fun RouteBuilder.toScene(screen: Screen) =
    scene(
        screen.route,
        navTransition = NavTransition(),
    ) {
        BaseScreen(screen)
    }

fun Screen.getNavigationItem(navigator: Navigator) =
    NavigationItem(
        title = title,
        icon = navIcon,
        onClick = {
            navigator.navigate(route, options = NavOptions(launchSingleTop = true))
        },
    )
