package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.ReferencedTime
import cl.emilym.sinatra.domain.prompt.FavouriteNearbyStopDeparturesUseCase
import cl.emilym.sinatra.domain.prompt.StopDepartures
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.NavigationInstruction
import cl.emilym.sinatra.ui.presentation.screens.maps.route.RouteDetailScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

interface NearbyDepartureItemComponent: SinatraComponent {

    val departures: StateFlow<StopDepartures?>

    fun onStopClick()
    fun onDepartureClick(time: IStopTimetableTime)

    fun updateLocation(location: MapLocation)

}

class DefaultNearbyDepartureItemComponent(
    private val onNavigate: (NavigationInstruction) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): NearbyDepartureItemComponent, SinatraComponentContext by sinatraComponentContext {

    private val favouriteNearbyStopDeparturesUseCase: FavouriteNearbyStopDeparturesUseCase = koin.get()

    private val currentLocation = MutableStateFlow<MapLocation?>(null)

    override val departures: StateFlow<StopDepartures?> = currentLocation.flatRequestStateFlow(showLoading = false) {
        it ?: return@flatRequestStateFlow flowOf(null)
        withContext(Dispatchers.IO) {
            favouriteNearbyStopDeparturesUseCase(it)
        }
    }.unwrap().state(null)

    override fun onStopClick() {
        val stopId = departures.value?.stop?.id ?: return
        onNavigate(
            NavigationInstruction.StopDetail(stopId)
        )
    }

    override fun onDepartureClick(time: IStopTimetableTime) {
        val stopId = departures.value?.stop?.id ?: return
        onNavigate(
            NavigationInstruction.RouteDetail(
                time.routeId,
                time.serviceId,
                time.tripId,
                stopId,
                (time.stationTime.arrival.time as? ReferencedTime)?.startOfDay
            )
        )
    }

    override fun updateLocation(location: MapLocation) {
         currentLocation.value = location
    }


}