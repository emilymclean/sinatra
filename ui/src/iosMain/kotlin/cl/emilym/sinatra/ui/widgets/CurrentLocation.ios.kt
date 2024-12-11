package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import cl.emilym.sinatra.data.models.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal actual fun platformCurrentLocation(): Flow<Location?> {
    return flowOf()
}