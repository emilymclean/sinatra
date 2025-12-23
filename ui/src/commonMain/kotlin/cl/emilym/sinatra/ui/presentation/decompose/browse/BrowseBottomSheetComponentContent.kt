package cl.emilym.sinatra.ui.presentation.decompose.browse

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.RouteItemComponentContent
import cl.emilym.sinatra.ui.widgets.FullscreenRequestStateWidget
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import com.arkivanov.decompose.ExperimentalDecomposeApi
import io.github.aakira.napier.Napier

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun BrowseBottomSheetComponentContent(
    component: BrowseBottomSheetComponent
) {
    val itemsItems by component.items.collectAsStateWithLifecycle()
    LaunchedEffect(itemsItems) {
        
    }
    val items = itemsItems.activeItems.values.map { it.first }

    val routeComponent = items.filterIsInstance<BrowseBottomSheetComponent.Item.Routes>().firstOrNull()?.component
    val routes = routeComponent?.routes?.collectAsStateWithLifecycle()?.value ?: RequestState.Initial()

    Scaffold { innerPadding ->
        FullscreenRequestStateWidget(routes, { routeComponent?.retry() }) { routes ->
            LazyColumn(
                contentPadding = innerPadding
            ) {
                for (item in items) {
                    when (item) {
                        is BrowseBottomSheetComponent.Item.Routes -> RouteItemComponentContent(
                            routes,
                            item.component
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}