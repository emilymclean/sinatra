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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import cafe.adriel.voyager.core.screen.ScreenKey
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
        get() = 0.25f

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val mapControl = LocalMapControl.current

        val currentLocation = currentLocation()
        val state by viewModel.state.collectAsState(MapSearchState.Browse)

        LaunchedEffect(currentLocation) {
            val currentLocation = currentLocation ?: return@LaunchedEffect
            if (!viewModel.hasZoomedToLocation) {
                mapControl.zoomToPoint(currentLocation, currentLocationZoom)
                viewModel.hasZoomedToLocation = true
            }
            if (FeatureFlags.MAP_SEARCH_SCREEN_NEARBY_STOPS_SEARCH) {
                viewModel.updateLocation(currentLocation)
            }
        }
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                Modifier.padding(1.rdp),
                verticalArrangement = Arrangement.spacedBy(1.rdp)
            ) {
                currentLocation?.let {
                    FloatingActionButton(
                        onClick = {
                            mapControl.zoomToPoint(it, currentLocationZoom)
                        }
                    ) { MyLocationIcon() }
                }
                if (state !is MapSearchState.Search) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.openSearch()
                        },
                    ) { SearchIcon() }
                }
                val sheetValue = LocalBottomSheetState.current.bottomSheetState.offset
                Box(
                    Modifier.height(min(
                        viewportHeight() - (sheetValue?.px ?: 0.dp),
                        viewportHeight() * bottomSheetHalfHeight
                    ))
                )
            }
        }
    }

    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val state by viewModel.state.collectAsState(MapSearchState.Browse)
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.heightIn(min = viewportHeight() * 0.5f)) {
            when (state) {
                is MapSearchState.Browse -> MapSearchScreenBrowseState()
                is MapSearchState.Search -> SearchScreen(
                    viewModel,
                    true,
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
        val viewModel = koinViewModel<MapSearchViewModel>()
        val stopsRS by viewModel.stops.collectAsState(RequestState.Initial())
        val stops = (stopsRS as? RequestState.Success)?.value?.filter { it.parentStation == null } ?: return

        DrawMapSearchScreenMapNative(stops)
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val stopsRS by viewModel.stops.collectAsState(RequestState.Initial())
        val stops = (stopsRS as? RequestState.Success)?.value?.filter { it.parentStation == null } ?: return listOf()

        return mapSearchScreenMapItems(stops)
    }

}

@Composable
expect fun NativeMapScope.DrawMapSearchScreenMapNative(stops: List<Stop>)

@Composable
expect fun mapSearchScreenMapItems(stops: List<Stop>): List<MarkerItem>