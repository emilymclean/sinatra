package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.e
import cl.emilym.sinatra.ui.presentation.screens.RootMapScreen
import cl.emilym.sinatra.ui.presentation.theme.SinatraTheme
import cl.emilym.sinatra.ui.widgets.LocalPermissionRequestQueue
import cl.emilym.sinatra.ui.widgets.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.widgets.PermissionRequestQueue
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@KoinViewModel
class AppViewModel(
    private val transportMetadataRepository: TransportMetadataRepository
): ViewModel() {

    val scheduleTimeZone = MutableStateFlow(TimeZone.currentSystemDefault())

    init {
        viewModelScope.launch {
            scheduleTimeZone.value = transportMetadataRepository.timeZone()
        }
    }

}

@Composable
fun App() {
    KoinContext {
        val viewModel = koinViewModel<AppViewModel>()
        // Configuration
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }
        val permissionQueue = remember { PermissionRequestQueue() }
        val timeZone by viewModel.scheduleTimeZone.collectAsState(TimeZone.currentSystemDefault())

        SinatraTheme {
            CompositionLocalProvider(
                LocalScheduleTimeZone provides timeZone,
                LocalPermissionRequestQueue provides permissionQueue
            ) {
                PermissionRequestQueueHandler()
                Navigator(RootMapScreen())
            }
        }
    }
}

@Composable
fun PermissionRequestQueueHandler() {
    val queue = LocalPermissionRequestQueue.current
    val request by queue.request.collectAsState(null)

    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController: PermissionsController = remember(permissionsFactory) {
        permissionsFactory.createPermissionsController()
    }
    BindEffect(permissionsController)

    LaunchedEffect(request) {
        val request = request ?: return@LaunchedEffect

        if (permissionsController.isPermissionGranted(request.permission)) {
            request.suspended.complete(true)
            Napier.d("Already have permission ${request.permission}, continuing")
        } else {
            try {
                Napier.d("Requesting permission ${request.permission}")
                permissionsController.providePermission(request.permission)
                Napier.d("Got ${request.permission} permission, continuing")
                request.suspended.complete(true)
            } catch (e: Exception) {
                Napier.e(e)
                request.suspended.complete(false)
            }
        }
        queue.pop()
    }
}