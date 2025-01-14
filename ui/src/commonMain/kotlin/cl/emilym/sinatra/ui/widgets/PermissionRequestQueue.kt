package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.e
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class PermissionRequestQueueItem(
    val permission: Permission,
    val suspended: CompletableDeferred<Boolean>
)

class PermissionRequestQueue {
    private val requests = MutableStateFlow<List<PermissionRequestQueueItem>>(listOf())
    val request = requests.map { it.firstOrNull() }

    suspend fun request(permission: Permission): Boolean {
        val deferred = CompletableDeferred<Boolean>()
        requests.update {
            it + listOf(
                PermissionRequestQueueItem(permission, deferred)
            )
        }
        return deferred.await()
    }

    fun pop() {
        requests.update {
            if (it.isEmpty())
                listOf()
            else it.subList(1, it.size)
        }
    }

}

val LocalPermissionRequestQueue = staticCompositionLocalOf<PermissionRequestQueue> { error("No provided permission request queue") }

@Composable
fun PermissionRequestQueueHandler() {
    val queue = LocalPermissionRequestQueue.current
    val request by queue.request.collectAsState(null)
    val rejectedPermissions = rememberSaveable { mutableListOf<Permission>() }

    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController: PermissionsController = remember(permissionsFactory) {
        permissionsFactory.createPermissionsController()
    }
    BindEffect(permissionsController)

    LaunchedEffect(request) {
        val request = request ?: return@LaunchedEffect

        when {
            permissionsController.isPermissionGranted(request.permission) -> {
                request.suspended.complete(true)
                Napier.d("Already have permission ${request.permission}, continuing")
            }
            rejectedPermissions.contains(request.permission) -> {
                request.suspended.complete(false)
                Napier.d("Permission ${request.permission} already rejected this session")
            }
            else -> {
                try {
                    Napier.d("Requesting permission ${request.permission}")
                    permissionsController.providePermission(request.permission)
                    Napier.d("Got ${request.permission} permission, continuing")
                    request.suspended.complete(true)
                } catch (e: Exception) {
                    Napier.e(e)
                    rejectedPermissions.add(request.permission)
                    request.suspended.complete(false)
                }
            }
        }
        queue.pop()
    }
}