package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.PlaceRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.placeMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.placeJourneyNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.search.zoomThreshold
import cl.emilym.sinatra.ui.stopCardDefaultNavigation
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.FavouriteButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.NavigateIcon
import cl.emilym.sinatra.ui.widgets.NoBusIcon
import cl.emilym.sinatra.ui.widgets.SheetIosBackButton
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.map_search_nearby_stops
import sinatra.ui.generated.resources.no_nearby_stops
import sinatra.ui.generated.resources.place_detail_navigate
import sinatra.ui.generated.resources.place_not_found
import sinatra.ui.generated.resources.semantics_favourite_place
import sinatra.ui.generated.resources.stop_detail_distance

@Factory
class PlaceDetailViewModel(
    private val placeRepository: PlaceRepository,
    private val favouriteRepository: FavouriteRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val nearbyStopsUseCase: NearbyStopsUseCase
): SinatraScreenModel {

    private val placeId = MutableStateFlow<PlaceId?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val favourited = placeId.flatMapLatest {
        it?.let { favouriteRepository.placeIsFavourited(it) } ?: flowOf(false)
    }.state(false)

    private val _place = placeId.requestStateFlow {
        it?.let { placeRepository.get(it).item }
    }
    val place = _place.state(RequestState.Initial())

    private val _nearbyStops = place.requestStateFlow {
        (it as? RequestState.Success)?.value?.let {
            nearbyStopsUseCase(it.location, limit = 25).nullIfEmpty()
        }
    }
    val nearbyStops = _nearbyStops.state(RequestState.Initial())

    fun init(placeId: PlaceId) {
        this.placeId.value = placeId
        screenModelScope.launch {
            recentVisitRepository.addPlaceVisit(placeId)
        }
    }

    fun favourite(favourited: Boolean) {
        val placeId = this.placeId.value ?: return
        screenModelScope.launch {
            favouriteRepository.setPlaceFavourite(placeId, favourited)
        }
    }

    fun retryPlace() {
        screenModelScope.launch {
            _place.retry()
        }
    }

    fun retryNearby() {
        screenModelScope.launch {
            _nearbyStops.retry()
        }
    }
}


class PlaceDetailScreen(
    val placeId: PlaceId
): MapScreen {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$placeId"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<PlaceDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current
        val navigator = LocalNavigator.currentOrThrow
        val mapControl = LocalMapControl.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState?.bottomSheetState?.halfExpand()
        }

        LifecycleEffectOnce {
            viewModel.init(placeId)
        }

        val place by viewModel.place.collectAsStateWithLifecycle()
        val nearbyStops by viewModel.nearbyStops.collectAsStateWithLifecycle()

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            RequestStateWidget(place, { viewModel.retryPlace() }) { place ->
                when {
                    place == null -> {
                        Text(stringResource(Res.string.place_not_found))
                    }
                    else -> {
                        LaunchedEffect(place.location) {
                            mapControl.moveToPoint(place.location, minZoom = zoomThreshold)
                        }

                        Scaffold { innerPadding ->
                            LazyColumn(
                                Modifier.fillMaxSize(),
                                contentPadding = innerPadding
                            ) {
                                item { Box(Modifier.height(1.rdp)) }
                                item {
                                    Row(
                                        Modifier.padding(horizontal = 1.rdp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(1.rdp)
                                    ) {
                                        SheetIosBackButton()
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                place.name,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Text(
                                                place.displayName,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        val favourited by viewModel.favourited.collectAsStateWithLifecycle()
                                        val favouriteContentDescription = stringResource(Res.string.semantics_favourite_place)
                                        FavouriteButton(
                                            favourited,
                                            { viewModel.favourite(it) },
                                            Modifier.semantics {
                                                contentDescription = favouriteContentDescription
                                                selected = favourited
                                            }
                                        )
                                    }
                                }
                                item { Box(Modifier.height(1.rdp)) }
                                item {
                                    Button(
                                        onClick = { navigator.placeJourneyNavigation(place) },
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 1.rdp)
                                    ) {
                                        NavigateIcon()
                                        Box(Modifier.width(0.5.rdp))
                                        Text(stringResource(Res.string.place_detail_navigate))
                                    }
                                }
                                item { Box(Modifier.height(1.rdp)) }
                                item { Subheading(stringResource(Res.string.map_search_nearby_stops)) }
                                item {
                                    Box(Modifier.padding(horizontal = 1.rdp)) {
                                        RequestStateWidget(
                                            nearbyStops,
                                            retry = { viewModel.retryNearby() }
                                        ) {}
                                    }
                                }
                                items((nearbyStops as? RequestState.Success)?.value ?: listOf()) {
                                    StopCard(
                                        it.stop,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { navigator.stopCardDefaultNavigation(it.stop) },
                                        subtitle = stringResource(Res.string.stop_detail_distance, it.distance.text),
                                        showStopIcon = true
                                    )
                                }
                                if ((nearbyStops as? RequestState.Success)?.value?.size == 0) {
                                    item {
                                        Box(Modifier.padding(horizontal = 1.rdp)) {
                                            ListHint(
                                                stringResource(Res.string.no_nearby_stops)
                                            ) {
                                                NoBusIcon(
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<PlaceDetailViewModel>()
        val placeRS by viewModel.place.collectAsStateWithLifecycle()
        val place = (placeRS as? RequestState.Success)?.value ?: return listOf()

        return listOf(
            MarkerItem(
                place.location,
                placeMarkerIcon(place),
                id = "placeDetail-${place.id}"
            )
        )
    }
}