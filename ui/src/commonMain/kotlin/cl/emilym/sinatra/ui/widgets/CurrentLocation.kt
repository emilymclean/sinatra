package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.data.models.MapLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

enum class LocationAccuracy {
    LOW, MEDIUM, HIGH
}

@Composable
internal expect fun platformCurrentLocation(accuracy: LocationAccuracy): Flow<MapLocation?>

@Composable
fun currentLocation(accuracy: LocationAccuracy = LocationAccuracy.MEDIUM): MapLocation? {
    var hasPermission by remember { mutableStateOf(false) }
    val permissionRequestQueue = LocalPermissionRequestQueue.current

    LaunchedEffect(permissionRequestQueue) {
        hasPermission = permissionRequestQueue.request(Permission.LOCATION)
    }

    val platformLocation = when (hasPermission) {
        true -> platformCurrentLocation(accuracy)
        false -> flowOf()
    }

    return platformLocation.collectAsState(null).value
}

@Composable
expect fun hasLocationPermission(): Boolean