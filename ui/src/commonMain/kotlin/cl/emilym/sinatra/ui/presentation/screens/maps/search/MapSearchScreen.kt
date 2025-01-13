package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.units.px
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.navigation.NativeMapScreen
import cl.emilym.sinatra.ui.placeCardDefaultNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreen
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MyLocationIcon
import cl.emilym.sinatra.ui.widgets.SearchIcon
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportHeight
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel

const val zoomThreshold = 14f
const val currentLocationZoom = zoomThreshold + 4f

class MapSearchScreen: MapScreen, NativeMapScreen {
    override val key: ScreenKey = this::class.qualifiedName!!

    override val bottomSheetHalfHeight: Float
        get() = 0.66f

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<MapSearchViewModel>()
        val mapControl = LocalMapControl.current

        val currentLocation = currentLocation()
        val state by viewModel.state.collectAsState(MapSearchState.Browse)
        val zoomToCurrentLocation by viewModel.zoomToLocation.collectAsState(null)

        LaunchedEffect(currentLocation) {
            if (currentLocation != null) {
                viewModel.updateLocation(currentLocation)
            }
        }

        LaunchedEffect(zoomToCurrentLocation) {
            if (zoomToCurrentLocation == null || currentLocation == null) return@LaunchedEffect
            mapControl.moveToPoint(currentLocation, currentLocationZoom)
        }

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                Modifier.padding(1.rdp),
                verticalArrangement = Arrangement.spacedBy(1.rdp)
            ) {
                val showCurrentLocationButton by viewModel.showCurrentLocation.collectAsState(false)
                if (showCurrentLocationButton) {
                    currentLocation?.let {
                        FloatingActionButton(
                            onClick = {
                                mapControl.moveToPoint(it, currentLocationZoom)
                            },
                            Modifier.semantics {
                                contentDescription = "Zoom to current location"
                            }
                        ) { MyLocationIcon() }
                    }
                }
                if (state !is MapSearchState.Search) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.openSearch()
                        },
                        Modifier.semantics {
                            contentDescription = "Open search screen"
                        }
                    ) { SearchIcon() }
                }
            }
        }
    }

    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<MapSearchViewModel>()
        val routeListViewModel = koinScreenModel<RouteListViewModel>()
        val state by viewModel.state.collectAsState(MapSearchState.Browse)
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.heightIn(min = viewportHeight() * 0.5f)) {
            when (state) {
                is MapSearchState.Browse -> MapSearchScreenBrowseState(
                    routeListViewModel,
                    viewModel
                )
                is MapSearchState.Search -> SearchScreen(
                    viewModel,
                    { viewModel.openBrowse() },
                    { navigator.push(StopDetailScreen(it.id)) },
                    { navigator.push(RouteDetailScreen(it.id)) },
                    { navigator.placeCardDefaultNavigation(it) }
                )
            }
        }
    }

    @Composable
    override fun NativeMapScope.DrawMapNative() {
        val viewModel = koinScreenModel<MapSearchViewModel>()
        val stopsRS by viewModel.stops.collectAsState(RequestState.Initial())
        val stops = (stopsRS as? RequestState.Success)?.value?.filter { it.parentStation == null } ?: return

        DrawMapSearchScreenMapNative(stops)
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<MapSearchViewModel>()
        val stopsRS by viewModel.stops.collectAsState(RequestState.Initial())
        val stops = (stopsRS as? RequestState.Success)?.value?.filter { it.parentStation == null } ?: return listOf()

        return mapSearchScreenMapItems(stops)
    }

}

@Composable
expect fun NativeMapScope.DrawMapSearchScreenMapNative(stops: List<Stop>)

@Composable
expect fun mapSearchScreenMapItems(stops: List<Stop>): List<MarkerItem>