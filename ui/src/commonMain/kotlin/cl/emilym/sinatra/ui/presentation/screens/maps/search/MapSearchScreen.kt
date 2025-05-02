package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.maps.MapCallbackItem
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.navigation.NativeMapScreen
import cl.emilym.sinatra.ui.placeCardDefaultNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.place.PointDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.browse.BrowseViewModel
import cl.emilym.sinatra.ui.presentation.screens.maps.search.browse.MapSearchScreenBrowseState
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreen
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MyLocationIcon
import cl.emilym.sinatra.ui.widgets.SearchIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportHeight
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.semantics_open_search_screen
import sinatra.ui.generated.resources.semantics_zoom_current_location

const val zoomThreshold = 14f
const val currentLocationZoom = zoomThreshold + 1f

class MapSearchScreen: MapScreen, NativeMapScreen {
    override val key: ScreenKey = this::class.qualifiedName!!

    override val bottomSheetHalfHeight: Float
        get() = 0.25f

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<MapSearchViewModel>()
        val mapControl = LocalMapControl.current

        val currentLocation = currentLocation()
        val state by viewModel.state.collectAsStateWithLifecycle()
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
                val showCurrentLocationButton by viewModel.showCurrentLocation.collectAsStateWithLifecycle()
                if (showCurrentLocationButton) {
                    currentLocation?.let {
                        val zoomContentDescription = stringResource(Res.string.semantics_zoom_current_location)
                        FloatingActionButton(
                            onClick = {
                                mapControl.moveToPoint(it, currentLocationZoom)
                            },
                            Modifier.then(
                                if (FeatureFlags.HIDE_MAPS_FROM_ACCESSIBILITY)
                                    Modifier.clearAndSetSemantics { invisibleToUser() }
                                else
                                    Modifier.semantics {
                                        contentDescription = zoomContentDescription
                                    }
                            )
                        ) { MyLocationIcon() }
                    }
                }
                if (state !is MapSearchState.Search) {
                    val openContentDescription = stringResource(Res.string.semantics_open_search_screen)
                    FloatingActionButton(
                        onClick = {
                            viewModel.openSearch()
                        },
                        Modifier.semantics {
                            contentDescription = openContentDescription
                        }
                    ) { SearchIcon() }
                }
            }
        }
    }

    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<MapSearchViewModel>()
        val browseViewModel = koinScreenModel<BrowseViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.heightIn(min = viewportHeight() * 0.5f)) {
            when (state) {
                is MapSearchState.Browse -> MapSearchScreenBrowseState(
                    browseViewModel,
                    viewModel
                )
                is MapSearchState.Search -> SearchScreen(
                    viewModel,
                    listOf(),
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
        val stopsRS by viewModel.stops.collectAsStateWithLifecycle()
        val stops = (stopsRS as? RequestState.Success)?.value ?: return

        DrawMapSearchScreenMapNative(stops)
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<MapSearchViewModel>()
        val stopsRS by viewModel.stops.collectAsStateWithLifecycle()
        val stops = (stopsRS as? RequestState.Success)?.value ?: return listOf()
        val navigator = LocalNavigator.currentOrThrow

        return mapSearchScreenMapItems(stops) + listOf(
            MapCallbackItem(
                onLongClick = { navigator.push(PointDetailScreen(it)) }
            )
        )
    }

}

@Composable
expect fun NativeMapScope.DrawMapSearchScreenMapNative(stops: List<Stop>)

@Composable
expect fun mapSearchScreenMapItems(stops: List<Stop>): List<MarkerItem>