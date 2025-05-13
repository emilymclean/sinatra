package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.router.ArrivalBasedRouter
import cl.emilym.sinatra.router.DepartureBasedRouter
import cl.emilym.sinatra.router.RaptorConfig
import cl.emilym.sinatra.router.Router
import cl.emilym.sinatra.router.RouterPrefs
import cl.emilym.sinatra.router.data.NetworkGraph
import org.koin.core.annotation.Factory

@Factory
class RouterFactory {

    operator fun invoke(
        anchorTime: JourneyCalculationTime,
        graph: NetworkGraph,
        services: List<Cachable<List<ServiceId>>>,
        config: RaptorConfig,
        prefs: RouterPrefs
    ): Router {
        return when (anchorTime) {
            is JourneyCalculationTime.ArrivalTime -> ArrivalBasedRouter(
                graph,
                services.map { it.item },
                config,
                prefs
            )
            is JourneyCalculationTime.DepartureTime -> DepartureBasedRouter(
                graph,
                services.map { it.item },
                config,
                prefs
            )
        }
    }

}