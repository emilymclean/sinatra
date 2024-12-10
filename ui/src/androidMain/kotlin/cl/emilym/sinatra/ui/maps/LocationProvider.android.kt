package cl.emilym.sinatra.ui.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import cl.emilym.sinatra.data.models.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.concurrent.TimeUnit

@Single
actual class LocationProvider(
    private val context: Context
) {

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(context) }

    private val _hasLocationPermission = MutableStateFlow(
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    )
    actual val hasLocationPermission: Flow<Boolean> = _hasLocationPermission
    actual val currentLocation: Flow<Location?>
        @SuppressLint("MissingPermission")
        get() {
            Napier.d("Listening to location")
            val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, TimeUnit.MINUTES.toMillis(10))
                .setMinUpdateDistanceMeters(1000F)
                .build()

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

    actual suspend fun setHasLocationPermission() {
        _hasLocationPermission.value = true
    }

}