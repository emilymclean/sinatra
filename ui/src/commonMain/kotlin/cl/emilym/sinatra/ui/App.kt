package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import cl.emilym.sinatra.ui.presentation.decompose.root.RootComponent
import cl.emilym.sinatra.ui.presentation.decompose.root.RootComponentContent
import cl.emilym.sinatra.ui.presentation.theme.SinatraTheme
import cl.emilym.sinatra.ui.widgets.LocalPermissionRequestQueue
import cl.emilym.sinatra.ui.widgets.PermissionRequestQueue
import cl.emilym.sinatra.ui.widgets.PermissionRequestQueueHandler
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import org.koin.compose.KoinContext

@Composable
fun App(
    rootComponent: RootComponent
) {
    KoinContext(
        rootComponent.koin
    ) {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }

        val permissionQueue = remember { PermissionRequestQueue() }

        SinatraTheme {
            CompositionLocalProvider(
                LocalPermissionRequestQueue provides permissionQueue
            ) {
                PermissionRequestQueueHandler()
                RootComponentContent(rootComponent)
            }
        }
    }
}