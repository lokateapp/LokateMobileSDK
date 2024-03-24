import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import market.MarketApp

internal val RequiredPermissions =
    arrayOf(
        Permission.LOCATION,
        Permission.COARSE_LOCATION,
        // Permission.BLUETOOTH_SCAN,
    )

suspend fun checkPermissions(permissionsController: PermissionsController): Boolean {
    return RequiredPermissions.all { permission -> permissionsController.isPermissionGranted(permission) }
}

// TODO: handle denied exceptions properly (redirect user to settings etc.)
// should also check if bluetooth is open
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
    val permissionsController = remember(permissionsControllerFactory) { permissionsControllerFactory.createPermissionsController() }
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

    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background,
        ) {
            if (hasPermissions.value) {
                MarketApp()
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

@Composable
fun PermissionScreen(
    permissionsController: PermissionsController,
    coroutineScope: CoroutineScope,
    hasPermissions: MutableState<Boolean>,
) {
    Button(onClick = {
        coroutineScope.launch {
            RequiredPermissions.forEach { permission -> askPermission(permissionsController, permission) }
            hasPermissions.value = checkPermissions(permissionsController)
        }
    }) {
        Text("Grant Permissions")
    }
}
