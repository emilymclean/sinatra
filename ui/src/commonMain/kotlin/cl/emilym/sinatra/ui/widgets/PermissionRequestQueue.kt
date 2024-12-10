package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.staticCompositionLocalOf
import dev.icerock.moko.permissions.Permission
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