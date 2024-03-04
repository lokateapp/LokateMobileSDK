interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// TODO: merge common logic in getting permissions here
interface PermissionHandler {
    fun getPermissions(onPermissionsGranted: () -> Unit)
}