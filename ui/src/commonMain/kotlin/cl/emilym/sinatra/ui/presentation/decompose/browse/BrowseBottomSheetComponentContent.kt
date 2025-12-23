package cl.emilym.sinatra.ui.presentation.decompose.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.domain.prompt.StopDepartures
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.BrowseItemContent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.NearbyDepartureItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.NearbyDepartureItemComponentContent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.QuickFavouriteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.QuickFavouriteItemComponentContent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.RouteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.RouteItemComponentContent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.ServiceUpdateItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.ServiceUpdateItemComponentContent
import cl.emilym.sinatra.ui.presentation.screens.maps.search.DrawMapSearchScreenMapNative
import cl.emilym.sinatra.ui.presentation.screens.maps.search.browse.QuickNavigationItem
import cl.emilym.sinatra.ui.presentation.screens.maps.search.mapSearchScreenMapItems
import cl.emilym.sinatra.ui.widgets.FullscreenRequestStateWidget
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import com.arkivanov.decompose.ExperimentalDecomposeApi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun BrowseBottomSheetComponentContent(
    component: BrowseBottomSheetComponent
) {
    val items = component.items.collectAsStateWithLifecycle().value
        .activeItems
        .values
        .map { it.first }

    val routeComponent = items.filterIsInstance<RouteItemComponent>().firstOrNull()
    val requestState = routeComponent?.requestState?.collectAsStateWithLifecycle()?.value ?: RequestState.Initial()

    val content = items.map {
        it to it.state.collectAsStateWithLifecycle().value
    }

    Scaffold { innerPadding ->
        FullscreenRequestStateWidget(requestState, { routeComponent?.retry() }) { routes ->
            LazyColumn(
                contentPadding = innerPadding
            ) {
                for ((component, state) in content) {
                    if (state is BrowseItemContent.None) continue
                    when (component) {
                        is NearbyDepartureItemComponent -> NearbyDepartureItemComponentContent(
                            (state as BrowseItemContent.Content<StopDepartures>).content,
                            component
                        )
                        is QuickFavouriteItemComponent -> QuickFavouriteItemComponentContent(
                            (state as BrowseItemContent.Content<List<QuickNavigationItem>>).content,
                            component
                        )
                        is RouteItemComponent -> RouteItemComponentContent(
                            (state as BrowseItemContent.Content<List<Route>>).content,
                            component
                        )
                        is ServiceUpdateItemComponent -> ServiceUpdateItemComponentContent(
                            (state as BrowseItemContent.Content<ServiceAlert>).content,
                            component
                        )
                    }
                    item {
                        Spacer(Modifier.height(1.rdp))
                    }
                }
            }
        }
    }
}

@Composable
fun browseBottomSheetComponentMapItems(component: BrowseBottomSheetComponent): List<MapItem> {
    val stops = component.stops.collectAsStateWithLifecycle().value
    return mapSearchScreenMapItems(stops) {

    }
}

@Composable
fun NativeMapScope.BrowseBottomSheetComponentNativeMapItems(component: BrowseBottomSheetComponent) {
    val stops = component.stops.collectAsStateWithLifecycle().value

    DrawMapSearchScreenMapNative(stops) {

    }
}