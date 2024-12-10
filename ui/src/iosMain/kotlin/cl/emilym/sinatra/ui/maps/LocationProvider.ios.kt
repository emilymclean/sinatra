package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.Location
import kotlinx.coroutines.flow.Flow

actual class LocationProvider {
    actual val currentLocation: Flow<Location?>
        get() = TODO("Not yet implemented")

}