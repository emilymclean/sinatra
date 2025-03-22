package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.CalculateJourneyUseCase
import cl.emilym.sinatra.domain.JourneyCalculationTime
import cl.emilym.sinatra.domain.JourneyLocation
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.NEAREST_STOP_RADIUS
import cl.emilym.sinatra.ui.presentation.screens.maps.search.NEARBY_STOPS_LIMIT
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreenViewModel
import cl.emilym.sinatra.ui.presentation.screens.search.searchHandler
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory

private sealed interface State {
    data object JourneySelection: State
    data class JourneySelected(
        val journey: Journey
    ): State
    data class Search(
        val targetIsOrigin: Boolean
    ): State
}

sealed interface NavigationEntryState {
    data class JourneySelection(
        val state: NavigationState
    ): NavigationEntryState
    data class JourneySelected(
        val journey: Journey
    ): NavigationEntryState
    data class Search(
        val results: RequestState<List<SearchResult>>
    ): NavigationEntryState
}

sealed interface NavigationAnchorTime {
    data object Now: NavigationAnchorTime
    data class DepartureTime(
        val time: Instant
    ): NavigationAnchorTime
    data class ArrivalTime(
        val time: Instant
    ): NavigationAnchorTime
}

private data class NavigationParameters(
    val destinationLocation: JourneyLocation?,
    val originLocation: JourneyLocation?,
    val anchorTime: NavigationAnchorTime,
    val wheelchairAccessible: Boolean,
    val bikesAllowed: Boolean
)

@Factory
class NavigationEntryViewModel(
    private val calculateJourneyUseCase: CalculateJourneyUseCase,
    private val routeStopSearchUseCase: RouteStopSearchUseCase,
    private val networkGraphRepository: NetworkGraphRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val stopRepository: StopRepository,
    private val clock: Clock
): SinatraScreenModel, SearchScreenViewModel {

    val currentLocation = MutableStateFlow<MapLocation?>(null)
    val destination = MutableStateFlow<NavigationLocation?>(null)
    val origin = MutableStateFlow<NavigationLocation?>(null)
    val anchorTime = MutableStateFlow<NavigationAnchorTime>(NavigationAnchorTime.Now)
    val wheelchairAccessible = MutableStateFlow(false)
    val bikesAllowed = MutableStateFlow(false)

    val destinationLocation = destination.flatMapLatest {
        it.toJourneyLocation()
    }.state(null)
    val originLocation = origin.flatMapLatest {
        it.toJourneyLocation()
    }.state(null)
    private val _state = MutableStateFlow<State>(State.JourneySelection)
    private val retryGraphLoad = Channel<Unit>(Channel.CONFLATED)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val navigationState: StateFlow<NavigationState> = anchorTime.flatMapLatest {
        merge(
            flowOf(Unit),
            retryGraphLoad.consumeAsFlow()
        ).flatMapLatest { _ ->
            flow {
                emit(NavigationState.GraphLoading)
                try {
                    when (it) {
                        is NavigationAnchorTime.ArrivalTime -> networkGraphRepository.networkGraph(true)
                        else -> networkGraphRepository.networkGraph(false)
                    }
                    emit(NavigationState.GraphReady)
                    emitAll(
                        combine(
                            destinationLocation,
                            originLocation,
                            anchorTime,
                            wheelchairAccessible,
                            bikesAllowed
                        ) { destinationLocation, originLocation, anchorTime, wheelchairAccessible, bikesAllowed ->
                            NavigationParameters(
                                destinationLocation,
                                originLocation,
                                anchorTime,
                                wheelchairAccessible,
                                bikesAllowed
                            )
                        }.flatMapLatest {
                            flow {
                                if (it.destinationLocation == null || it.originLocation == null) return@flow
                                emit(NavigationState.JourneyCalculating)
                                try {
                                    emit(NavigationState.JourneysFound(
                                        calculateJourneyUseCase(
                                            it.originLocation,
                                            it.destinationLocation,
                                            when (it.anchorTime) {
                                                is NavigationAnchorTime.Now ->
                                                    JourneyCalculationTime.DepartureTime(clock.now())
                                                is NavigationAnchorTime.DepartureTime ->
                                                    JourneyCalculationTime.DepartureTime(it.anchorTime.time)
                                                is NavigationAnchorTime.ArrivalTime ->
                                                    JourneyCalculationTime.ArrivalTime(it.anchorTime.time)
                                            },
                                            it.wheelchairAccessible,
                                            it.bikesAllowed
                                        )
                                    ))
                                } catch(e: Exception) {
                                    emit(NavigationState.JourneyFailed(e))
                                }
                            }
                        }
                    )
                } catch(e: Exception) {
                    emit(NavigationState.GraphFailed(e))
                }
            }
        }
    }.stateIn(screenModelScope, SharingStarted.Lazily, NavigationState.GraphLoading)

    val state = _state.flatMapLatest {
        when (it) {
            is State.JourneySelection -> navigationState.map {
                if (it is NavigationState.JourneysFound && it.journeys.size == 1) {
                    NavigationEntryState.JourneySelected(it.journeys.first())
                } else {
                    NavigationEntryState.JourneySelection(it)
                }
            }
            is State.JourneySelected -> flowOf(NavigationEntryState.JourneySelected(it.journey))
            is State.Search -> searchHandler(routeStopSearchUseCase) { NavigationEntryState.Search(
                when (it) {
                    is RequestState.Success -> RequestState.Success(it.value.filter { it !is SearchResult.RouteResult })
                    else -> it
                }
            ) }
        }
    }.state(NavigationEntryState.JourneySelection(NavigationState.GraphLoading))

    val journeyCount = navigationState.map {
        when (it) {
            is NavigationState.JourneysFound -> it.journeys.size
            else -> 0
        }
    }.state(0)

    val backStackSize = MutableStateFlow(0)
    val showBackButton = combine(
        state,
        navigationState,
        backStackSize
    ) { state, navigationState, backStackSize ->
        when {
            backStackSize > 1 -> true
            navigationState is NavigationState.JourneysFound && state is NavigationEntryState.JourneySelected ->
                navigationState.journeys.size > 1
            else -> false
        }
    }.state(true)

    val timeDialogVisible = MutableStateFlow<Boolean>(false)

    // Search
    override val query = MutableStateFlow<String?>(null)

    private val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    private val _recentVisits = createRequestStateFlowFlow<List<RecentVisit>>()
    override val recentVisits = _recentVisits.presentable().mapLatest {
        when (it) {
            is RequestState.Success -> RequestState.Success(it.value.filter { it !is RecentVisit.Route })
            else -> it
        }
    }.state(RequestState.Initial())

    override val nearbyStops: StateFlow<List<StopWithDistance>?> = stops.combine(currentLocation) { stops, lastLocation ->
        if (stops !is RequestState.Success || lastLocation == null) return@combine null
        val stops = stops.value.nullIfEmpty() ?: return@combine null
        stops.map { StopWithDistance(it, distance(lastLocation, it.location)) }
            .filter { it.distance < NEAREST_STOP_RADIUS && it.stop.parentStation == null }
            .nullIfEmpty()
            ?.sortedBy { it.distance }
            ?.take(NEARBY_STOPS_LIMIT)
    }.state(null)

    override val results = state.mapLatest {
        when (it) {
            is NavigationEntryState.Search -> it.results
            else -> RequestState.Initial()
        }
    }.state(RequestState.Initial())

    fun init(destination: NavigationLocation, origin: NavigationLocation) {
        retryLoadingGraph()
        retryRecentVisits()
        retryStops()
        setDestination(destination)
        setOrigin(origin)
    }

    private fun NavigationLocation?.toJourneyLocation(): Flow<JourneyLocation?> {
        return when (this) {
            is NavigationLocation.Stop -> flowOf(JourneyLocation(location, true))
            is NavigationLocation.LocatableNavigationLocation -> flowOf(
                JourneyLocation(location, false)
            )
            is NavigationLocation.CurrentLocation -> currentLocation.mapLatest {
                it?.let { JourneyLocation(it, false) }
            }
            else -> flowOf(null)
        }
    }

    fun retryLoadingGraph() {
        screenModelScope.launch {
            retryGraphLoad.send(Unit)
        }
    }

    override fun retryRecentVisits() {
        screenModelScope.launch {
            _recentVisits.handleFlowProperly {
                recentVisitRepository.all()
            }
        }
    }

    fun retryStops() {
        screenModelScope.launch {
            stops.handle {
                stopRepository.stops().item
            }
        }
    }

    private fun setDestination(navigationLocation: NavigationLocation) {
        destination.value = navigationLocation
        navigationLocation.recentVisit?.let {
            screenModelScope.launch {
                recentVisitRepository.add(it)
            }
        }
    }

    private fun setOrigin(navigationLocation: NavigationLocation) {
        origin.value = navigationLocation
        navigationLocation.recentVisit?.let {
            screenModelScope.launch {
                recentVisitRepository.add(it)
            }
        }
    }

    fun swapOriginAndDestination() {
        val currentOrigin = origin.value
        origin.value = destination.value
        destination.value = currentOrigin
    }

    fun onOriginClick() {
        onOpenSearch()
        _state.value = State.Search(true)
    }

    fun onDestinationClick() {
        onOpenSearch()
        _state.value = State.Search(false)
    }

    private fun onOpenSearch() {
        query.value = null
    }

    fun onSearchItemClicked(item: NavigationLocation) {
        val s = _state.value as? State.Search ?: return
        when (s.targetIsOrigin) {
            true -> setOrigin(item)
            false -> setDestination(item)
        }
        _state.value = State.JourneySelection
    }

    fun openJourneyCalculation() {
        _state.value = State.JourneySelection
    }

    fun back(): Boolean {
        return when(_state.value) {
            is State.JourneySelected -> {
                when {
                    ((navigationState.value as? NavigationState.JourneysFound)?.journeys?.size ?: 0) > 1 -> {
                        _state.value = State.JourneySelection
                        false
                    }
                    else -> true
                }
            }
            else -> true
        }
    }

    fun selectJourney(journey: Journey) {
        _state.value = State.JourneySelected(journey)
    }

    fun updateCurrentLocation(location: MapLocation) {
        if (currentLocation.value != null) return
        this.currentLocation.value = location
    }

}