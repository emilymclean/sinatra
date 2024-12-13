package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.data.models.MapLocation
import dev.icerock.moko.permissions.Permission
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal expect fun platformCurrentLocation(): Flow<MapLocation?>

@Composable
fun currentLocation(): MapLocation? {
    var hasPermission by remember { mutableStateOf(false) }
    val permissionRequestQueue = LocalPermissionRequestQueue.current

    LaunchedEffect(permissionRequestQueue) {
        hasPermission = permissionRequestQueue.request(Permission.LOCATION).apply {
            Napier.d("Location permission request completed, value = $this")
        }
    }

    Napier.d("Has permission = ${hasPermission}")
    val platformLocation = when (hasPermission) {
        true -> platformCurrentLocation()
        false -> flowOf()
    }

    return platformLocation.collectAsState(null).value
}