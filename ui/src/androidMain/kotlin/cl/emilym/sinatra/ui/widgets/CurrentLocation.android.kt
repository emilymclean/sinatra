package cl.emilym.sinatra.ui.widgets

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import cl.emilym.sinatra.data.models.MapLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@SuppressLint("MissingPermission")
@Composable
internal actual fun platformCurrentLocation(accuracy: LocationAccuracy): Flow<MapLocation?> {
    val context = LocalContext.current
    val fusedLocationClient = remember(context) { LocationServices.getFusedLocationProviderClient(context) }

    val request = remember {
        LocationRequest.Builder(
            when (accuracy) {
                LocationAccuracy.HIGH -> Priority.PRIORITY_HIGH_ACCURACY
                else -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            },
            when (accuracy) {
                LocationAccuracy.LOW -> TimeUnit.MINUTES.toMillis(10)
                LocationAccuracy.MEDIUM -> TimeUnit.MINUTES.toMillis(1)
                LocationAccuracy.HIGH -> TimeUnit.SECONDS.toMillis(30)
            }
        )
            .setMinUpdateDistanceMeters(when (accuracy) {
                LocationAccuracy.LOW -> 1000F
                LocationAccuracy.MEDIUM -> 10F
                LocationAccuracy.HIGH -> 1F
            })
            .build()
    }

    return callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                Napier.d("Location result = ${result.lastLocation}")
                result.lastLocation?.let {
                    launch {
                        send(MapLocation(it.latitude, it.longitude))
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

@Composable
actual fun hasLocationPermission(): Boolean {
    val context = LocalContext.current
    return remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}