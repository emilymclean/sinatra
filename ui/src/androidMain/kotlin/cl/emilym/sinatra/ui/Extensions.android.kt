package cl.emilym.sinatra.ui

import cl.emilym.sinatra.data.models.Location
import com.google.android.gms.maps.model.LatLng

fun Location.toMaps(): LatLng {
    return LatLng(
        lat,
        lng
    )
}