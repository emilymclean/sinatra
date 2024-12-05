package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.RouteClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.persistence.RoutePersistence
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

class RoutesCacheWorker(
    private val routePersistence: RoutePersistence,
    private val routeClient: RouteClient,
    override val shaRepository: ShaRepository,
    override val clock: Clock,
): CacheWorker<List<Route>>() {

    override val cacheCategory: CacheCategory = CacheCategory.ROUTE

    override suspend fun saveToPersistence(data: List<Route>, resource: ResourceKey) = routePersistence.save(data)
    override suspend fun getFromPersistence(resource: ResourceKey): List<Route> = routePersistence.get()

    suspend fun get(): Cachable<List<Route>> {
        return run(routeClient.routesEndpointPair, "routes")
    }

}

@Factory
class RouteRepository(
    private val routeCacheWorker: RoutesCacheWorker
) {

    suspend fun routes(): Cachable<List<Route>> {
        return routeCacheWorker.get()
    }

}