package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.ui.presentation.screens.RootMapScreen
import cl.emilym.sinatra.ui.presentation.theme.SinatraTheme
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        // Configuration
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }

        SinatraTheme {
            Navigator(RootMapScreen())
        }
    }
}