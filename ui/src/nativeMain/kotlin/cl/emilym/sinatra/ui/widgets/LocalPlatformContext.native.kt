package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import cl.emilym.sinatra.data.repository.PlatformContext

@Composable
actual fun platformContext(): PlatformContext {
    return PlatformContext()
}