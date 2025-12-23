package cl.emilym.sinatra.ui.presentation.decompose.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.NearbyDepartureItemComponentContent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.QuickFavouriteItemComponentContent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.RouteItemComponentContent
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

    val routeComponent = items.filterIsInstance<BrowseBottomSheetComponent.Item.Routes>().firstOrNull()?.component
    val routes = routeComponent?.routes?.collectAsStateWithLifecycle()?.value ?: RequestState.Initial()

    Scaffold { innerPadding ->
        FullscreenRequestStateWidget(routes, { routeComponent?.retry() }) { routes ->
            LazyColumn(
                contentPadding = innerPadding
            ) {
                for (it in items) {
                    when (it) {
                        is BrowseBottomSheetComponent.Item.Routes -> RouteItemComponentContent(
                            routes,
                            it.component
                        )
                        else -> {
                            item {
                                Box(Modifier.animateItem()) {
                                    when (it) {
                                        is BrowseBottomSheetComponent.Item.QuickFavourite ->
                                            QuickFavouriteItemComponentContent(it.component)
                                        is BrowseBottomSheetComponent.Item.NearbyDeparture ->
                                            NearbyDepartureItemComponentContent(it.component)
                                        else -> {}
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