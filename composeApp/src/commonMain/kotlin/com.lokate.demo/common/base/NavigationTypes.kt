package com.lokate.demo.common.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.lokate.demo.NavigationItem
import com.lokate.demo.csfair.CSFairApp
import com.lokate.demo.csfair.CSFairViewModel
import com.lokate.demo.gym.GymApp
import com.lokate.demo.gym.GymViewModel
import com.lokate.demo.market.MarketApp
import com.lokate.demo.market.MarketViewModel
import com.lokate.demo.museum.MuseumApp
import com.lokate.demo.museum.MuseumViewModel
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

sealed class Screen(val title: String, val route: String, val navIcon: ImageVector) {
    data object MarketScreen :
        Screen(title = "Market", route = "/market", navIcon = Icons.Default.ShoppingCart)

    data object MuseumScreen :
        Screen(title = "Museum", route = "/museum", navIcon = Icons.Default.Museum)

    data object GymScreen :
        Screen(title = "Gym", route = "/gym", navIcon = Icons.Default.SportsGymnastics)

    data object CSFairScreen :
        Screen(title = "CSFair", route = "/csfair", navIcon = Icons.Default.Celebration)
}

val ScreenList = listOf(
    Screen.MarketScreen,
    Screen.MuseumScreen,
    Screen.GymScreen,
    Screen.CSFairScreen
)

@Composable
fun Screen.getVM() = when (this) {
    Screen.MarketScreen -> koinViewModel<MarketViewModel>(MarketViewModel::class)
    Screen.MuseumScreen -> koinViewModel<MuseumViewModel>(MuseumViewModel::class)
    Screen.GymScreen -> koinViewModel<GymViewModel>(GymViewModel::class)
    Screen.CSFairScreen -> koinViewModel<CSFairViewModel>(CSFairViewModel::class)
}

@Composable
fun Screen.getScreen() = when (this) {
    Screen.MarketScreen -> MarketApp(this.getVM() as MarketViewModel)
    Screen.MuseumScreen -> MuseumApp(this.getVM() as MuseumViewModel)
    Screen.GymScreen -> GymApp(this.getVM() as GymViewModel)
    Screen.CSFairScreen -> CSFairApp(this.getVM() as CSFairViewModel)
}

fun RouteBuilder.toScene(screen: Screen) = scene(
    screen.route, navTransition = NavTransition()
) {
    val vm = screen.getVM()
    BaseScreen(vm, screen) {
        screen.getScreen()
    }
}

fun Screen.getNavigationItem(navigator: Navigator) = NavigationItem(
    title = title,
    icon = navIcon,
    onClick = {
        navigator.navigate(route)
    }
)
