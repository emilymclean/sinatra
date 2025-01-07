package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.ui.toShared
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.CoreLocation.kCLLocationAccuracyKilometer
import platform.CoreLocation.kCLLocationAccuracyNearestTenMeters
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun platformCurrentLocation(accuracy: LocationAccuracy): Flow<MapLocation?> {
    val locationManager = remember { CLLocationManager() }

    return callbackFlow {
        val locationCallback = object : CLLocationManagerDelegateProtocol, NSObject() {
            override fun locationManager(
                manager: CLLocationManager,
                didUpdateToLocation: CLLocation,
                fromLocation: CLLocation
            ) {
                launch {
                    send(didUpdateToLocation.coordinate.toShared())
                    delay(10000)
                }
            }
        }

        locationManager.delegate = locationCallback
        locationManager.desiredAccuracy = when (accuracy) {
            LocationAccuracy.LOW -> kCLLocationAccuracyKilometer
            LocationAccuracy.MEDIUM -> kCLLocationAccuracyNearestTenMeters
            LocationAccuracy.HIGH -> kCLLocationAccuracyBest
        }
        locationManager.startUpdatingLocation()

        awaitClose {
            locationManager.stopUpdatingLocation()
            locationManager.delegate = null
        }
    }
}