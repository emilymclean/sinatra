package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RetryToken
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.map
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.NavigationInstruction
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

interface RouteItemComponent: BrowseItemPlugin<List<Route>> {

    val requestState: StateFlow<RequestState<Unit>>

    fun onClick(route: Route)
    fun retry()

}

class DefaultRouteItemComponent(
    private val onNavigate: (NavigationInstruction) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): RouteItemComponent, SinatraComponentContext by sinatraComponentContext {

    private val displayRoutesUseCase: DisplayRoutesUseCase = koin.get()

    private val retryToken = RetryToken()
    private val _routes: StateFlow<RequestState<List<Route>>> = flatRequestStateFlow(retryToken) {
        displayRoutesUseCase().mapLatest { it.item }
    }.state()

    override val requestState: StateFlow<RequestState<Unit>> = _routes.mapLatest {
        it.map {  }
    }.state()
    override val state = _routes.unwrap().mapLatest {
        when (it) {
            null -> BrowseItemContent.None()
            else -> BrowseItemContent.Content(it)
        }
    }.state(BrowseItemContent.None())

    override fun onClick(route: Route) {
        onNavigate(
            NavigationInstruction.RouteDetail(
                route.id
            )
        )
    }

    override fun retry() {
        componentScope.launch {
            retryToken.retry()
        }
    }
}