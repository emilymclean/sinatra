package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.navigation.NativeMapScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteListViewModel
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.widgets.IosBackButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MyLocationIcon
import cl.emilym.sinatra.ui.widgets.NoResultsIcon
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.SearchIcon
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportHeight
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.search_hint
import sinatra.ui.generated.resources.no_search_results
import sinatra.ui.generated.resources.search_recently_viewed

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

        val halfScreen = viewportHeight() * bottomSheetHalfHeight
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
                            mapControl.zoomToPoint(it)
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
                val sheetValue = LocalBottomSheetState.current.bottomSheetState.currentValue
                Box(
                    Modifier.height(
                        when (sheetValue) {
                            SinatraSheetValue.PartiallyExpanded -> 56.dp
                            else -> halfScreen
                        }
                    )
                )
            }
        }
    }

    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val state by viewModel.state.collectAsState(MapSearchState.Browse)

        Box(modifier = Modifier.heightIn(min = viewportHeight() * 0.5f)) {
            when (state) {
                is MapSearchState.Browse -> MapSearchScreenBrowseState()
                is MapSearchState.Search -> MapSearchScreenSearchState()
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