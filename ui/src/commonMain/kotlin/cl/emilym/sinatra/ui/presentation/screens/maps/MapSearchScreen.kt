package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.navigation.NativeMapScope
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MyLocationIcon
import cl.emilym.sinatra.ui.widgets.NoBusIcon
import cl.emilym.sinatra.ui.widgets.NoResultsIcon
import cl.emilym.sinatra.ui.widgets.PillShape
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.SearchIcon
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.handleFlow
import cl.emilym.sinatra.ui.widgets.screenHeight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.no_upcoming_vehicles
import sinatra.ui.generated.resources.search_hint
import sinatra.ui.generated.resources.no_search_results
import kotlin.time.Duration.Companion.seconds

sealed interface MapSearchState {
    data object Browse: MapSearchState
    data class Search(
        val results: RequestState<List<SearchResult>>
    ): MapSearchState
}

@KoinViewModel
class MapSearchViewModel(
    private val stopRepository: StopRepository,
    private val routeStopSearchUseCase: RouteStopSearchUseCase
): ViewModel() {

    private val _state = MutableStateFlow(State.BROWSE)
    val query = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state: Flow<MapSearchState> = _state.flatMapLatest {
        when (it) {
            State.BROWSE -> flowOf(MapSearchState.Browse)
            State.SEARCH -> flow<MapSearchState> {
                emit(MapSearchState.Search(RequestState.Initial()))
                emitAll(
                    query.flatMapLatest {
                        flow {
                            emit(MapSearchState.Search(RequestState.Loading()))
                            emitAll(
                                query.debounce(1.seconds).flatMapLatest { query ->
                                    when (query) {
                                        null, "" -> flowOf(MapSearchState.Search(RequestState.Initial()))
                                        else -> handleFlow {
                                            routeStopSearchUseCase(query)
                                        }.map { MapSearchState.Search(it) }
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
    val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    var hasZoomedToLocation = false

    init {
        retry()
    }

    fun retry() {
        viewModelScope.launch {
            stops.handle {
                stopRepository.stops().item
            }
        }
    }

    fun search(query: String) {
        this.query.value = query
    }

    fun openSearch() {
        query.value = null
        _state.value = State.SEARCH
    }

    fun openBrowse() {
        query.value = null
        _state.value = State.BROWSE
    }

    private enum class State {
        BROWSE, SEARCH
    }

}

class MapSearchScreen: MapScreen {
    override val key: ScreenKey = this::class.qualifiedName!!

    override val bottomSheetHalfHeight: Float
        get() = 0.25f

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val mapControl = LocalMapControl.current

        val currentLocation = currentLocation()
        val state by viewModel.state.collectAsState(MapSearchState.Browse)

        SinatraBackHandler(state is MapSearchState.Search) {
            viewModel.openBrowse()
        }

        LaunchedEffect(currentLocation) {
            currentLocation?.let { currentLocation ->
                if (!viewModel.hasZoomedToLocation) {
                    mapControl.zoomToPoint(currentLocation)
                    viewModel.hasZoomedToLocation = true
                }
            }
        }

        ConstraintLayout(Modifier.fillMaxSize()) {
            val padding = 1.rdp
            val halfScreen = screenHeight() * bottomSheetHalfHeight
            val (expandOffsetRef, searchButtonRef, locationButtonRef) = createRefs()
//            val actionBarrier = createTopBarrier(searchButtonRef, expandOffsetRef)

            if (state !is MapSearchState.Search) {
                FloatingActionButton(
                    onClick = {
                        viewModel.openSearch()
                    },
                    modifier = Modifier.constrainAs(searchButtonRef) {
                        end.linkTo(parent.end, padding)
                        bottom.linkTo(expandOffsetRef.top, padding)
                    }
                ) { SearchIcon() }
            }

            currentLocation?.let {
                FloatingActionButton(
                    onClick = {
                        mapControl.zoomToPoint(it)
                    },
                    modifier = Modifier.constrainAs(locationButtonRef) {
                        end.linkTo(parent.end, padding)
                        bottom.linkTo(if (state !is MapSearchState.Search) searchButtonRef.top else expandOffsetRef.top, padding)
                    }
                ) { MyLocationIcon() }
            }

            val sheetValue = LocalBottomSheetState.current.bottomSheetState.currentValue
            Box(Modifier.constrainAs(expandOffsetRef) {
                bottom.linkTo(parent.bottom, when(sheetValue) {
                    SinatraSheetValue.PartiallyExpanded -> 56.dp
                    else -> halfScreen
                })
            })
        }
    }

    @Composable
    override fun MapScope.MapContent() {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val stopsRS by viewModel.stops.collectAsState(RequestState.Initial())
        val stops = (stopsRS as? RequestState.Success)?.value ?: return

        Native {
            MapSearchScreenMapNative(stops.filter { it.parentStation == null })
        }
    }

    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val state by viewModel.state.collectAsState(MapSearchState.Browse)

        Box(modifier = Modifier.heightIn(min = screenHeight() * 0.5f)) {
            when (state) {
                is MapSearchState.Browse -> BrowseContent()
                is MapSearchState.Search -> SearchContent(
                    (state as? MapSearchState.Search)?.results ?: RequestState.Initial()
                )
            }
        }
    }

    @Composable
    fun BrowseContent() {
        val bottomSheetState = LocalBottomSheetState.current.bottomSheetState

        LaunchedEffect(Unit) {
            bottomSheetState.halfExpand()
        }

        val viewModel = koinViewModel<RouteListViewModel>()

        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val navigator = LocalNavigator.currentOrThrow
            val routes by viewModel.routes.collectAsState(RequestState.Initial())
            RequestStateWidget(routes, { viewModel.retry() }) { routes ->
                LazyColumn {
                    items(routes.size) {
                        RouteCard(
                            routes[it],
                            onClick = {
                                navigator.push(RouteDetailScreen(
                                    routes[it].id
                                ))
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun SearchContent(results: RequestState<List<SearchResult>>) {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetState = LocalBottomSheetState.current.bottomSheetState
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            bottomSheetState.expand()
        }

        val query by viewModel.query.collectAsState()
        LazyColumn(Modifier.fillMaxWidth().heightIn(min = screenHeight() * 1.2f)) {
            item {
                Box(Modifier.padding(1.rdp))
            }
            item {
                TextField(
                    query ?: "",
                    { viewModel.search(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 1.rdp).focusRequester(focusRequester),
                    maxLines = 1,
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    leadingIcon = {
                        SearchIcon(
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    placeholder = {
                        Text(stringResource(Res.string.search_hint))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
            when {
                results is RequestState.Success -> {
                    item {
                        Box(Modifier.padding(0.5.rdp))
                    }
                    if (results.value.isEmpty()) {
                        item {
                            Box(Modifier.padding(0.5.rdp))
                        }
                        item {
                            ListHint(
                                stringResource(Res.string.no_search_results, query ?: "")
                            ) {
                                NoResultsIcon(
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    items(results.value) { result ->
                        when (result) {
                            is SearchResult.RouteResult -> {
                                RouteCard(
                                    result.route,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { navigator.push(RouteDetailScreen(result.route.id)) }
                                )
                            }

                            is SearchResult.StopResult -> {
                                StopCard(
                                    result.stop,
                                    arrival = null,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { navigator.push(StopDetailScreen(result.stop.id)) }
                                )
                            }
                        }
                    }
                }
                else -> {
                    item {
                        Box(Modifier.padding(1.rdp))
                    }
                    item {
                        if (query.isNullOrBlank()) return@item
                        Box(
                            Modifier.fillMaxSize().padding(horizontal = 1.rdp),
                            contentAlignment = Alignment.Center
                        ) {
                            RequestStateWidget(results) {}
                        }
                    }
                }
            }
            item {
                Box(Modifier.padding(2.rdp))
            }
        }
    }

}

@Composable
expect fun NativeMapScope.MapSearchScreenMapNative(stops: List<Stop>)