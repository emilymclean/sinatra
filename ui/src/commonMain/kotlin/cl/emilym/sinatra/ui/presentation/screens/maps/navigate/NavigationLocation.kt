package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.compose.runtime.Composable
import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.ui.localization.format
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.bus
import sinatra.ui.generated.resources.journey_start
import sinatra.ui.generated.resources.marker_icon
import sinatra.ui.generated.resources.navigate_current_location
import sinatra.ui.generated.resources.navigate_lat_lng

sealed interface NavigationLocation: Serializable {
    interface LocatableNavigationLocation: NavigationLocation {
        val location: MapLocation
    }

    @get:Composable
    val name: String
    val icon: DrawableResource
        get() = Res.drawable.journey_start

    val screenKey: String

    val recentVisit: RecentVisit?

    data object None: NavigationLocation {
        override val name: String
            @Composable
            get() = ""
        override val screenKey: String = "none"
        override val recentVisit: RecentVisit? = null
    }

    data class Point(
        override val location: MapLocation
    ): LocatableNavigationLocation {
        override val name: String
            @Composable
            get() = stringResource(
                Res.string.navigate_lat_lng,
                location.lat.format(3),
                location.lng.format(3)
            )
        override val screenKey: String = "point-${location.lng}-${location.lng}"

        override val recentVisit: RecentVisit? get() = null
    }

    data class Place(
        val place: cl.emilym.sinatra.data.models.Place
    ): LocatableNavigationLocation {
        override val location: MapLocation
            get() = place.location
        override val name: String
            @Composable
            get() = place.name ?: place.displayName
        override val icon: DrawableResource
            get() = Res.drawable.marker_icon
        override val screenKey: String = "place-${place.id}"

        override val recentVisit: RecentVisit get() = RecentVisit.Place(place)
    }

    data class Stop(
        val stop: cl.emilym.sinatra.data.models.Stop
    ): LocatableNavigationLocation {
        override val location: MapLocation
            get() = stop.location
        override val name: String
            @Composable
            get() = stop.name
        override val icon: DrawableResource
            get() = Res.drawable.bus
        override val screenKey: String = "stop-${stop.id}"

        override val recentVisit: RecentVisit get() = RecentVisit.Stop(stop)
    }

    data object CurrentLocation: NavigationLocation {
        override val name: String
            @Composable
            get() = stringResource(Res.string.navigate_current_location)
        override val screenKey: String = "currentLocation"

        override val recentVisit: RecentVisit? = null
    }
}
