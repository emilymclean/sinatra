package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import cl.emilym.sinatra.e
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

val Permission.moko: dev.icerock.moko.permissions.Permission get() = when (this) {
    Permission.LOCATION -> dev.icerock.moko.permissions.Permission.LOCATION
}

actual class PermissionRequestQueue actual constructor() {
    private val requests = MutableStateFlow<List<PermissionRequestQueueItem>>(listOf())
    val request = requests.map { it.firstOrNull() }

    actual suspend fun request(permission: Permission): Boolean {
        val deferred = CompletableDeferred<Boolean>()
        requests.update {
            it + listOf(
                PermissionRequestQueueItem(permission, deferred)
            )
        }
        return deferred.await()
    }

    actual fun pop() {
        requests.update {
            if (it.isEmpty())
                listOf()
            else it.subList(1, it.size)
        }
    }

}

@Composable
actual fun PermissionRequestQueueHandler() {
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
            permissionsController.isPermissionGranted(request.permission.moko) -> {
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
                    permissionsController.providePermission(request.permission.moko)
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