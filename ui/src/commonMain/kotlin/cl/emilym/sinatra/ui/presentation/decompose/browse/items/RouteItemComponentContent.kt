package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.ui.widgets.RouteCard

fun LazyListScope.RouteItemComponentContent(
    routes: List<Route>,
    component: RouteItemComponent,
) {
    items(routes) { route ->
        RouteCard(
            route,
            onClick = {
                component.onClick(route)
            }
        )
    }
}