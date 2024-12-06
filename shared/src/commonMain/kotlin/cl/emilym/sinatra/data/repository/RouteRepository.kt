package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.RouteClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteServiceTimetable
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.persistence.RoutePersistence
import cl.emilym.sinatra.data.persistence.RouteServicePersistence
import cl.emilym.sinatra.data.persistence.RouteServiceTimetablePersistence
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

@Factory
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
class RouteServicesCacheWorker(
    private val routeClient: RouteClient,
    private val routeServicePersistence: RouteServicePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock,
): CacheWorker<List<ServiceId>>() {

    override val cacheCategory = CacheCategory.ROUTE_SERVICE

    override suspend fun saveToPersistence(data: List<ServiceId>, resource: ResourceKey) =
        routeServicePersistence.save(data, resource)
    override suspend fun getFromPersistence(resource: ResourceKey) = routeServicePersistence.get(resource)

    suspend fun get(routeId: RouteId): Cachable<List<ServiceId>> {
        return run(routeClient.routeServicesEndpointPair(routeId), "route/${routeId}/services")
    }

}

@Factory
class RouteServiceTimetableCacheWorker(
    private val routeClient: RouteClient,
    private val routeServiceTimetablePersistence: RouteServiceTimetablePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock,
): CacheWorker<RouteServiceTimetable>() {

    override val cacheCategory = CacheCategory.ROUTE_SERVICE_TIMETABLE

    override suspend fun saveToPersistence(data: RouteServiceTimetable, resource: ResourceKey) =
        routeServiceTimetablePersistence.save(data, resource)

    override suspend fun getFromPersistence(resource: ResourceKey) =
        routeServiceTimetablePersistence.get(resource)

    suspend fun get(routeId: RouteId, serviceId: ServiceId): Cachable<RouteServiceTimetable> {
        return run(
            routeClient.routeServiceTimetableEndpointPair(routeId, serviceId),
            "route/${routeId}/service/${serviceId}/timetable"
        )
    }

}

@Factory
class RouteRepository(
    private val routeCacheWorker: RoutesCacheWorker,
    private val routeServicesCacheWorker: RouteServicesCacheWorker,
    private val routeServiceTimetableCacheWorker: RouteServiceTimetableCacheWorker,
) {

    suspend fun routes() = routeCacheWorker.get()

    suspend fun servicesForRoute(routeId: RouteId) = routeServicesCacheWorker.get(routeId)

    suspend fun serviceTimetable(routeId: RouteId, serviceId: ServiceId) =
        routeServiceTimetableCacheWorker.get(routeId, serviceId)

    suspend fun ignoredRoutes(): List<RouteId> {
        return listOf("NIS", "X1", "X2", "X3", "X4")
    }

}