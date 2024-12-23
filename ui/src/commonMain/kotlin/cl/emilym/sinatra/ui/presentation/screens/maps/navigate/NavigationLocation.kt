package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.compose.runtime.Composable
import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Stop
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigate_lat_lng
import sinatra.ui.generated.resources.navigate_current_location

sealed interface NavigationLocation: Serializable {
    interface LocatableNavigationLocation: NavigationLocation {
        val location: MapLocation
    }

    @get:Composable
    val name: String
    val screenKey: String

    data class Point(
        override val location: MapLocation
    ): LocatableNavigationLocation {
        override val name: String
            @Composable
            get() = stringResource(Res.string.navigate_lat_lng)
        override val screenKey: String = "point-${location.lng}-${location.lng}"
    }

    data class Place(
        val place: cl.emilym.sinatra.data.models.Place
    ): LocatableNavigationLocation {
        override val location: MapLocation
            get() = place.location
        override val name: String
            @Composable
            get() = place.name
        override val screenKey: String = "place-${place.id}"
    }

    data class Stop(
        val stop: cl.emilym.sinatra.data.models.Stop
    ): LocatableNavigationLocation {
        override val location: MapLocation
            get() = stop.location
        override val name: String
            @Composable
            get() = stop.name
        override val screenKey: String = "stop-${stop.id}"
    }

    data object CurrentLocation: NavigationLocation {
        override val name: String
            @Composable
            get() = stringResource(Res.string.navigate_current_location)
        override val screenKey: String = "currentLocation"
    }
}