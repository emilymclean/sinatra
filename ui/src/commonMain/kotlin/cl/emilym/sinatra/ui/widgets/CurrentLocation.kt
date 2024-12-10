package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.e
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Composable
internal expect fun platformCurrentLocation(): Flow<Location?>

@Composable
fun currentLocation(): Location? {
    var hasPermission by remember { mutableStateOf(false) }

    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController: PermissionsController = remember(permissionsFactory) {
        permissionsFactory.createPermissionsController()
    }
    BindEffect(permissionsController)

    LaunchedEffect(permissionsController) {
        if (permissionsController.isPermissionGranted(Permission.LOCATION)) {
            hasPermission = true
            Napier.d("Already have permission, continuing")
        } else {
            try {
                Napier.d("Requesting permission for location")
                permissionsController.providePermission(Permission.LOCATION)
                Napier.d("Got location permission, continuing")
                hasPermission = true
            } catch (e: Exception) {
                Napier.e(e)
            }
        }
    }

    Napier.d("Has permission = ${hasPermission}")
    val platformLocation = when (hasPermission) {
        true -> platformCurrentLocation()
        false -> flowOf()
    }

    return platformLocation.collectAsState(null).value
}