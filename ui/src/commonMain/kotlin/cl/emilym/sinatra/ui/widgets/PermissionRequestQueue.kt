package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CompletableDeferred

enum class Permission {
    LOCATION
}

class PermissionRequestQueueItem(
    val permission: Permission,
    val suspended: CompletableDeferred<Boolean>
)

expect class PermissionRequestQueue() {
    suspend fun request(permission: Permission): Boolean
    fun pop()
}

val LocalPermissionRequestQueue = staticCompositionLocalOf<PermissionRequestQueue> { error("No provided permission request queue") }

@Composable
expect fun PermissionRequestQueueHandler()