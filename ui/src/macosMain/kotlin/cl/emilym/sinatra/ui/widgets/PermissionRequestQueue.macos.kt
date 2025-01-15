package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

actual class PermissionRequestQueue actual constructor() {

    actual suspend fun request(permission: Permission): Boolean {
        return false
    }

    actual fun pop() {}

}

@Composable
actual fun PermissionRequestQueueHandler() {
}