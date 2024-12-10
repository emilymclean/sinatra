package cl.emilym.sinatra.ui.widgets

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import cl.emilym.sinatra.data.models.Location
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
internal actual fun platformCurrentLocation(): Flow<Location?> {
    val context = LocalContext.current
    val fusedLocationClient = remember(context) { LocationServices.getFusedLocationProviderClient(context) }

    val request = remember {
        LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            TimeUnit.MINUTES.toMillis(10)
        )
            .setMinUpdateDistanceMeters(1000F)
            .build()
    }

    return callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                Napier.d("Location result = ${result.lastLocation}")
                result.lastLocation?.let {
                    launch {
                        send(Location(it.latitude, it.longitude))
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