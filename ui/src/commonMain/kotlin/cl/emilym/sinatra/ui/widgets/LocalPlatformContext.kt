package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import cl.emilym.sinatra.data.repository.PlatformContext

@Composable
expect fun platformContext(): PlatformContext