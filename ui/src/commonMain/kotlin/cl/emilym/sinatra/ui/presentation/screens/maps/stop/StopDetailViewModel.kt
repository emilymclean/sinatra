package cl.emilym.sinatra.ui.presentation.screens.maps.stop

import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.child
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.map
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.repository.AlertDisplayContext
import cl.emilym.sinatra.data.repository.AlertRepository
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.LastDepartureForStopUseCase
import cl.emilym.sinatra.domain.RoutesVisitingStopUseCase
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import cl.emilym.sinatra.lib.naturalComparator
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.defaultConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory

private data class StopRoutes(
    val stopId: StopId,
    val routeIds: List<RouteId>
)

enum class StopDetailPage {
    UPCOMING, LAST_DEPARTURES, CHILDREN
}

data class StopDetailPageInformation(
    val page: StopDetailPage,
    val selected: Boolean
)

data class RouteInformation(
    val route: Route,
    val selected: Boolean
)

sealed interface StopDetailState {
    data class Upcoming(
        val upcoming: RequestState<List<IStopTimetableTime>>
    ): StopDetailState

    data class LastDepartures(
        val lastDepartures: RequestState<List<IStopTimetableTime>>
    ): StopDetailState

    data class Children(
        val children: RequestState<List<Stop>>
    ): StopDetailState
}

@Factory
class StopDetailViewModel(
    private val stopRepository: StopRepository,
    private val upcomingRoutesForStopUseCase: UpcomingRoutesForStopUseCase,
    private val lastDepartureForStopUseCase: LastDepartureForStopUseCase,
    private val routesForStopUseCase: RoutesVisitingStopUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val alertRepository: AlertRepository
): SinatraScreenModel {

    private val stopId = MutableStateFlow<StopId?>(null)
    private val routeId = MutableStateFlow<List<RouteId>>(emptyList())
    private val page = MutableStateFlow(StopDetailPage.UPCOMING)

    val favourited = stopId.filterNotNull().flatMapLatest { stopId ->
        favouriteRepository.stopIsFavourited(stopId)
    }.state(false)

    private val _alerts = stopId.filterNotNull().flatRequestStateFlow(defaultConfig) { stopId ->
        alertRepository.alerts(AlertDisplayContext.Stop(stopId))
    }
    val alerts = _alerts.state()

    private val stopWithChildren = stopId.filterNotNull().requestStateFlow(defaultConfig) { stopId ->
        stopRepository.stopWithChildren(stopId).item
    }
    val stop = stopWithChildren.child { it?.stop }.state()
    private val children: StateFlow<RequestState<List<Stop>>> = stopWithChildren
        .child {
            it?.children?.sortedWith(compareBy(naturalComparator()) { it.name }) ?: emptyList()
        }
        .state()

    private val _upcoming = stopId.filterNotNull().flatRequestStateFlow(defaultConfig) { stopId ->
        withContext(Dispatchers.IO) {
            upcomingRoutesForStopUseCase(
                stopId = stopId,
                number = null
            )
        }
    }
    private val upcoming: StateFlow<RequestState<List<IStopTimetableTime>>> = combine(
        _upcoming,
        routeId
    ) { upcoming, routeId ->
        upcoming.map { it.item.filter { routeId.isEmpty() || routeId.contains(it.routeId) }.take(10) }
    }.state()

    private val _lastDeparture = stopId.filterNotNull().flatRequestStateFlow(defaultConfig) { stopId ->
        lastDepartureForStopUseCase(stopId = stopId)
    }
    private val lastDeparture: StateFlow<RequestState<List<IStopTimetableTime>>> = combine(
        _lastDeparture,
        routeId
    ) { upcoming, routeId ->
        upcoming.map { it.filter { routeId.isEmpty() || routeId.contains(it.routeId) } }
    }.state()

    private val _routes = stopId.filterNotNull().requestStateFlow { stopId ->
        routesForStopUseCase(stopId).item
    }
    private val routesState = _routes.state()

    val routes = combine(
        routesState.unwrap().filterNotNull(),
        routeId
    ) { routes, routeId ->
        routes.map {
            RouteInformation(
                it,
                routeId.contains(it.id)
            )
        }
    }.state(emptyList())

    val pages = combine(
        stop,
        page
    ) { stop, page ->
        listOfNotNull(
            StopDetailPage.UPCOMING,
            StopDetailPage.LAST_DEPARTURES,
            if (stop.unwrap()?.visibility?.showChildren == true) StopDetailPage.CHILDREN else null
        ).map {
            StopDetailPageInformation(it, it == page)
        }
    }.state(emptyList())

    val state = page.flatMapLatest { page ->
        when (page) {
            StopDetailPage.UPCOMING -> upcoming.mapLatest { upcoming ->
                StopDetailState.Upcoming(upcoming)
            }
            StopDetailPage.LAST_DEPARTURES -> lastDeparture.mapLatest { lastDeparture ->
                StopDetailState.LastDepartures(lastDeparture)
            }
            StopDetailPage.CHILDREN -> children.mapLatest { children ->
                StopDetailState.Children(children)
            }
        }
    }.state(StopDetailState.Upcoming(RequestState.Initial()))

    fun init(
        stopId: StopId
    ) {
        this.stopId.value = stopId

        screenModelScope.launch {
            recentVisitRepository.addStopVisit(stopId)
        }
    }

    fun filter(routeId: RouteId) {
        val current = this.routeId.value
        this.routeId.value = when {
            current.contains(routeId) -> current - routeId
            else -> current + routeId
        }
    }

    fun option(page: StopDetailPage) {
        this.page.value = page
    }

    fun retry() {
        screenModelScope.launch { _alerts.retryIfNeeded(alerts.value) }
        screenModelScope.launch { stopWithChildren.retryIfNeeded(stop.value) }
        screenModelScope.launch { _upcoming.retryIfNeeded(upcoming.value) }
        screenModelScope.launch { _lastDeparture.retryIfNeeded(lastDeparture.value) }
        screenModelScope.launch { _routes.retryIfNeeded(routesState.value) }
    }

    fun favourite(stopId: StopId, favourited: Boolean) {
        screenModelScope.launch {
            favouriteRepository.setStopFavourite(stopId, favourited)
        }
    }

}