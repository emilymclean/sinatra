package cl.emilym.sinatra.ui.presentation.screens.maps.place

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.placeMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.zoomThreshold
import cl.emilym.sinatra.ui.stopCardDefaultNavigation
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.FavouriteButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.NoBusIcon
import cl.emilym.sinatra.ui.widgets.NoPlaceIcon
import cl.emilym.sinatra.ui.widgets.SheetIosBackButton
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.map_search_nearby_stops
import sinatra.ui.generated.resources.no_nearby_stops
import sinatra.ui.generated.resources.place_not_found
import sinatra.ui.generated.resources.point_outside_service_area
import sinatra.ui.generated.resources.semantics_favourite_place
import sinatra.ui.generated.resources.stop_detail_distance

abstract class AbstractPlaceScreen<T: AbstractPlaceViewModel>: MapScreen {

    @Composable
    abstract fun viewModel(): T
    abstract fun init(viewModel: T)

    abstract fun LazyListScope.CallToAction(
        place: Place,
        navigator: Navigator
    )

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = viewModel()
        val bottomSheetState = LocalBottomSheetState.current
        val navigator = LocalNavigator.currentOrThrow
        val mapControl = LocalMapControl.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState?.bottomSheetState?.halfExpand()
        }

        LifecycleEffectOnce {
            init(viewModel)
        }

        val place by viewModel.place.collectAsStateWithLifecycle()
        val outsideServiceArea by viewModel.outsideServiceArea.collectAsStateWithLifecycle()
        val nearbyStops by viewModel.nearbyStops.collectAsStateWithLifecycle()
        val noNearbyStops by viewModel.noNearbyStops.collectAsStateWithLifecycle()
        val location by viewModel.location.collectAsStateWithLifecycle()

        LaunchedEffect(location) {
            delay(100)
            location?.let { mapControl.moveToPoint(it, minZoom = zoomThreshold) }
        }

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            RequestStateWidget(place, { viewModel.retryPlace() }) { place ->
                when {
                    outsideServiceArea -> {
                        ListHint(stringResource(Res.string.point_outside_service_area)) {
                            NoPlaceIcon()
                        }
                    }

                    place == null -> {
                        ListHint(stringResource(Res.string.place_not_found)) {
                            NoPlaceIcon()
                        }
                    }

                    else -> {
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
                                                place.name ?: place.displayName,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            place.name?.let {
                                                Text(
                                                    place.displayName,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                        }
                                        val favourited by viewModel.favourited.collectAsStateWithLifecycle()
                                        val favouriteContentDescription =
                                            stringResource(Res.string.semantics_favourite_place)
                                        favourited?.let {
                                            FavouriteButton(
                                                it,
                                                { viewModel.favourite(it) },
                                                Modifier.semantics {
                                                    contentDescription = favouriteContentDescription
                                                    selected = it
                                                }
                                            )
                                        }
                                    }
                                }
                                item { Box(Modifier.height(1.rdp)) }
                                CallToAction(place, navigator)
                                item { Box(Modifier.height(1.rdp)) }
                                item { Subheading(stringResource(Res.string.map_search_nearby_stops)) }
                                item {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 1.rdp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        RequestStateWidget(
                                            nearbyStops,
                                            retry = { viewModel.retryNearby() }
                                        ) {}
                                    }
                                }
                                items(
                                    (nearbyStops as? RequestState.Success<List<StopWithDistance>?>)?.value
                                        ?: listOf()
                                ) {
                                    StopCard(
                                        it.stop,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { navigator.stopCardDefaultNavigation(it.stop) },
                                        subtitle = stringResource(
                                            Res.string.stop_detail_distance,
                                            it.distance.text
                                        ),
                                        showStopIcon = true
                                    )
                                }
                                if (noNearbyStops) {
                                    item {
                                        Box(Modifier.padding(horizontal = 1.rdp).padding(top = 1.rdp)) {
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
        val viewModel = viewModel()
        val location = viewModel.location.collectAsStateWithLifecycle().value ?: return emptyList()

        return listOf(
            MarkerItem(
                location,
                placeMarkerIcon(),
                id = "placeDetail-${location}"
            )
        )
    }
}