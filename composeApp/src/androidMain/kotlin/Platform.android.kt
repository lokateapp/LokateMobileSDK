import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.activity.ComponentActivity

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidPermissionHandler(private val activity: ComponentActivity) : PermissionHandler {
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getPermissionsS(onPermissionsGranted: () -> Unit) {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH
        )
        val permissions = mutableListOf(false, false, false, false)

        while (!permissions.all { it }) {
            requiredPermissions.forEachIndexed { index, permission ->
                permissions[index] =
                    activity.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }

            permissions.filter { !it }.forEachIndexed { index, b ->
                activity.requestPermissions(arrayOf(requiredPermissions[index]), 0)
            }
        }
        onPermissionsGranted()
    }
    override fun getPermissions(onPermissionsGranted: () -> Unit): Unit{
        //check API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getPermissionsS(onPermissionsGranted)
        }
        val requiredPermissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH
        )
        val permissions = mutableListOf(false, false)
        while (!permissions.all { it }) {
            requiredPermissions.forEachIndexed { index, permission ->
                permissions[index] =
                    activity.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }

            permissions.filter { !it }.forEachIndexed { index, b ->
                activity.requestPermissions(arrayOf(requiredPermissions[index]), 0)
            }
        }
        onPermissionsGranted()
    }
}
