package cl.emilym.sinatra.ui.presentation.screens.maps.route

import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.map
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.repository.AlertDisplayContext
import cl.emilym.sinatra.data.repository.AlertRepository
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.CurrentTripForRouteUseCase
import cl.emilym.sinatra.domain.NEAREST_STOP_RADIUS
import cl.emilym.sinatra.lib.combineFlatMapLatest
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.defaultConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory

@Factory
class RouteDetailViewModel(
    private val currentTripForRouteUseCase: CurrentTripForRouteUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val alertRepository: AlertRepository,
    private val clock: Clock
): SinatraScreenModel {

    private data class Params(
        val routeId: RouteId,
        val serviceId: ServiceId? = null,
        val tripId: TripId? = null,
        val referenceTime: Instant? = null
    )

    private val params = MutableStateFlow<Params?>(null)
    private val _heading = MutableStateFlow<Heading?>(null)

    private var lastLocation = MutableStateFlow<MapLocation?>(null)

    val favourited = MutableStateFlow(false)

    private val _currentTripInformation = params.filterNotNull().flatRequestStateFlow(defaultConfig) { params ->
        currentTripForRouteUseCase(
            params.routeId,
            params.serviceId,
            params.tripId,
            params.referenceTime ?: clock.now()
        ).map { it.item }
    }
    private val currentTripInformation = _currentTripInformation.state()

    val headings: StateFlow<List<Heading>?> = currentTripInformation.mapLatest {
        it.unwrap()?.tripInformations?.mapNotNull { it.heading }
    }.state(null)

    val route = currentTripInformation.mapLatest { it.unwrap()?.route }.state(null)
    val tripInformation = combine(
        currentTripInformation,
        _heading
    ) { currentTripInformation, heading ->
        currentTripInformation.map {
            when (heading) {
                null -> it?.tripInformations?.firstOrNull()
                else -> it?.tripInformations?.firstOrNull { it.heading == heading }
            }
        }
    }.state()
    val heading = combine(
        params,
        headings
    ) { params, headings ->
        when {
            params?.tripId != null -> headings?.firstOrNull()
            else -> null
        }
    }.state(null)
    val selectedHeading = _heading.asStateFlow()

    val nearestStop = combine(
        tripInformation,
        lastLocation,
        params
    ) { tripInformation, lastLocation, params ->
        if (
            tripInformation !is RequestState.Success ||
            lastLocation == null ||
            params?.tripId != null
        ) return@combine null
        val stops = tripInformation.value?.stops?.mapNotNull { it.stop }?.nullIfEmpty() ?: return@combine null
        stops.map { StopWithDistance(it, distance(lastLocation, it.location)) }
            .filter { it.distance < NEAREST_STOP_RADIUS }
            .nullIfEmpty()
            ?.minBy { it.distance }
    }.state(null)

    private val _alerts = params.filterNotNull().flatRequestStateFlow(defaultConfig) { params ->
        alertRepository.alerts(
            AlertDisplayContext.Route(
            routeId = params.routeId,
            tripId = params.tripId
        ))
    }
    val alerts = _alerts.state()

    fun init(
        routeId: RouteId,
        serviceId: ServiceId?,
        tripId: TripId?,
        referenceTime: Instant?
    ) {
        params.value = Params(
            routeId,
            serviceId,
            tripId,
            referenceTime
        )
        screenModelScope.launch {
            favourited.emitAll(favouriteRepository.routeIsFavourited(routeId))
        }
        screenModelScope.launch {
            recentVisitRepository.addRouteVisit(routeId)
        }
    }

    fun selectHeading(heading: Heading) {
        _heading.value = heading
    }

    fun retry() {
        screenModelScope.launch { _currentTripInformation.retryIfNeeded(currentTripInformation.value) }
        screenModelScope.launch { _alerts.retryIfNeeded(alerts.value) }
    }

    fun updateLocation(location: MapLocation) {
        lastLocation.value = location
    }

    fun favourite(routeId: RouteId, favourited: Boolean) {
        this.favourited.value = favourited
        screenModelScope.launch {
            favouriteRepository.setRouteFavourite(routeId, favourited)
        }
    }

}