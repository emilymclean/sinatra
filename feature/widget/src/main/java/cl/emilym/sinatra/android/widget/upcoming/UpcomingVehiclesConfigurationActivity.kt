package cl.emilym.sinatra.android.widget.upcoming

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.android.base.ComposeActivity
import cl.emilym.sinatra.android.base.SinatraViewModel
import cl.emilym.sinatra.android.widget.R
import cl.emilym.sinatra.android.widget.data.repository.UpcomingVehiclesWidgetRepository
import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.e
import cl.emilym.sinatra.nullIf
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchState
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreen
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreenViewModel
import cl.emilym.sinatra.ui.presentation.screens.search.searchHandler
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed interface UpcomingVehiclesConfigurationState {
    data object InvalidAppWidget: UpcomingVehiclesConfigurationState
    data object Loading: UpcomingVehiclesConfigurationState
    data class ConfigurationEntry(
        val validConfiguration: Boolean
    ): UpcomingVehiclesConfigurationState
    data class Search(
        val results: RequestState<List<SearchResult>>
    ): UpcomingVehiclesConfigurationState
}

@KoinViewModel
class UpcomingVehiclesConfigurationViewModel(
    private val upcomingVehiclesWidgetRepository: UpcomingVehiclesWidgetRepository,
    private val nearbyStopsUseCase: NearbyStopsUseCase,
    private val stopRepository: StopRepository,
    private val routeRepository: RouteRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val routeStopSearchUseCase: RouteStopSearchUseCase
): SinatraViewModel(), SearchScreenViewModel {

    val stop = MutableStateFlow<Stop?>(null)
    val route = MutableStateFlow<Route?>(null)
    val heading = MutableStateFlow<Heading?>(null)

    private val _isValid = MutableStateFlow(true)
    private val _isLoading = MutableStateFlow(true)
    private val _isSearching = MutableStateFlow(false)

    override val query = MutableStateFlow<String?>(null)

    private val lastLocation = MutableStateFlow<MapLocation?>(null)
    private val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    override val nearbyStops = stops.combine(lastLocation) { stops, lastLocation ->
        if (stops !is RequestState.Success || lastLocation == null) return@combine null
        val stops = stops.value.nullIfEmpty() ?: return@combine null
        with(nearbyStopsUseCase) { stops.filter(lastLocation).nullIfEmpty() }
    }.state(null)

    private val _recentVisits = createRequestStateFlowFlow<List<RecentVisit>>()
    override val recentVisits = _recentVisits.presentable()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _isValid.flatMapLatest {
        when (it) {
            false -> flowOf(UpcomingVehiclesConfigurationState.InvalidAppWidget)
            true -> _isLoading.flatMapLatest {
                when (it) {
                    true -> flowOf(UpcomingVehiclesConfigurationState.Loading)
                    false -> _isSearching.flatMapLatest {
                        when (it) {
                            true -> searchHandler(
                                routeStopSearchUseCase,
                                placeEnabled = false,
                                routeEnabled = false
                            ) { UpcomingVehiclesConfigurationState.Search(it) }
                            false -> stop.mapLatest { stop ->
                                UpcomingVehiclesConfigurationState.ConfigurationEntry(
                                    stop != null
                                )
                            }
                        }
                    }
                }
            }
        }
    }.state(UpcomingVehiclesConfigurationState.Loading)

    override val results = state.mapLatest {
        when (it) {
            is UpcomingVehiclesConfigurationState.Search -> it.results
            else -> RequestState.Initial()
        }
    }.state(RequestState.Initial())

    fun init(appWidgetId: Int?) {
        if (appWidgetId == null) {
            _isValid.value = false
            return
        }

        retryStops()
        viewModelScope.launch {
            val existingConfig = upcomingVehiclesWidgetRepository.get(appWidgetId) ?: return@launch
            stop.value = try {
                stopRepository.stop(existingConfig.stopId).item
            } catch (e: Exception) {
                Napier.e(e)
                null
            }
            route.value = existingConfig.routeId?.let {
                try {
                    routeRepository.route(existingConfig.stopId).item
                } catch (e: Exception) {
                    Napier.e(e)
                    null
                }
            }
            heading.value = existingConfig.heading
        }

        _isLoading.value = false
    }

    fun retryStops() {
        viewModelScope.launch {
            stops.handle {
                stopRepository.stops().item
            }
        }
    }

    override fun retryRecentVisits() {
        viewModelScope.launch {
            _recentVisits.handleFlowProperly {
                recentVisitRepository.all().mapLatest {
                    it.filterIsInstance<RecentVisit.Stop>()
                }
            }
        }
    }

    fun closeSearch() {
        _isSearching.value = false
    }

    fun updateLocation(location: MapLocation) {
        lastLocation.value = location
    }

    fun selectStop(
        stop: Stop
    ) {
        this.stop.value = stop
        closeSearch()
    }

}

class UpcomingVehiclesConfigurationActivity: ComposeActivity() {

    val viewModel by viewModel<UpcomingVehiclesConfigurationViewModel>()

    private val appWidgetId get() = intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )?.nullIf { it == 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init(appWidgetId)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val state by viewModel.state.collectAsStateWithLifecycle()
        val currentLocation = currentLocation()

        LaunchedEffect(currentLocation) {
            currentLocation?.let {
                viewModel.updateLocation(it)
            }
        }

        Scaffold(
           topBar = {
               TopAppBar(
                   title = {
                       Text(stringResource(R.string.upcoming_vehicle_widget_label))
                   }
               )
           }
        ) {
            Box(Modifier.padding(it).fillMaxSize()) {
                when (state) {
                    is UpcomingVehiclesConfigurationState.Loading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    is UpcomingVehiclesConfigurationState.InvalidAppWidget -> InvalidWidget()
                    is UpcomingVehiclesConfigurationState.Search -> SearchScreen(
                        viewModel,
                        { viewModel.closeSearch() },
                        { viewModel.selectStop(it) },
                        {},
                        {}
                    )
                    is UpcomingVehiclesConfigurationState.ConfigurationEntry ->
                        ConfigurationEntryWidget()
                }
            }
        }
    }

    @Composable
    private fun InvalidWidget() {
        Box(
            Modifier.fillMaxSize().padding(1.rdp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.upcoming_vehicle_configuration_invalid_widget),
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    private fun ConfigurationEntryWidget() {
        val state = (viewModel.state.collectAsStateWithLifecycle().value as?
                UpcomingVehiclesConfigurationState.ConfigurationEntry) ?: return
        Text("Test")
    }
}