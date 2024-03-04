import market.MarketApp
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val permissionHandler = iOSPermissionHandler();
    MarketApp(permissionHandler)
}