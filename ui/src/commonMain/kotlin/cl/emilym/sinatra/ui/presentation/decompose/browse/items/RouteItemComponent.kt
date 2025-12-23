package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RetryToken
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.NavigationInstruction
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

interface RouteItemComponent: SinatraComponent {

    val routes: StateFlow<RequestState<List<Route>>>

    fun onClick(route: Route)
    fun retry()

}

class DefaultRouteItemComponent(
    private val onNavigate: (NavigationInstruction) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): RouteItemComponent, SinatraComponentContext by sinatraComponentContext {

    private val displayRoutesUseCase: DisplayRoutesUseCase = koin.get()

    private val retryToken = RetryToken()
    override val routes: StateFlow<RequestState<List<Route>>> = flatRequestStateFlow(retryToken) {
        displayRoutesUseCase().mapLatest { it.item }
    }.state()

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