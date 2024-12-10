package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf

expect class LocationProvider {

    val hasLocationPermission: Flow<Boolean>
    val currentLocation: Flow<Location?>

    suspend fun setHasLocationPermission()

}

val LocationProvider.protectedCurrentLocation: Flow<Location?> get() = hasLocationPermission.flatMapConcat {
    when (it) {
        true -> currentLocation
        else -> flowOf(null)
    }
}