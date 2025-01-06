package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.RouteClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteServiceCanonicalTimetable
import cl.emilym.sinatra.data.models.RouteServiceTimetable
import cl.emilym.sinatra.data.models.RouteTripTimetable
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.persistence.RoutePersistence
import cl.emilym.sinatra.data.persistence.RouteServiceCanonicalTimetablePersistence
import cl.emilym.sinatra.data.persistence.RouteServicePersistence
import cl.emilym.sinatra.data.persistence.RouteServiceTimetablePersistence
import cl.emilym.sinatra.data.persistence.RouteTripTimetablePersistence
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory

@Factory
class RoutesCacheWorker(
    private val routePersistence: RoutePersistence,
    private val routeClient: RouteClient,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
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
    override val cacheWorkerDependencies: CacheWorkerDependencies,
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
    override val cacheWorkerDependencies: CacheWorkerDependencies,
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
class RouteServiceCanonicalTimetableCacheWorker(
    private val routeClient: RouteClient,
    private val routeServiceCanonicalTimetablePersistence: RouteServiceCanonicalTimetablePersistence,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<RouteServiceCanonicalTimetable>() {

    override val cacheCategory = CacheCategory.ROUTE_SERVICE_CANONICAL_TIMETABLE

    override suspend fun saveToPersistence(data: RouteServiceCanonicalTimetable, resource: ResourceKey) =
        routeServiceCanonicalTimetablePersistence.save(data, resource)

    override suspend fun getFromPersistence(resource: ResourceKey) =
        routeServiceCanonicalTimetablePersistence.get(resource)!!

    suspend fun get(routeId: RouteId, serviceId: ServiceId): Cachable<RouteServiceCanonicalTimetable> {
        return run(
            routeClient.routeServiceCanonicalTimetableEndpointPair(routeId, serviceId),
            "route/${routeId}/service/${serviceId}/canonical"
        )
    }
}

@Factory
class RouteTripTimetableCacheWorker(
    private val routeClient: RouteClient,
    private val routeTripTimetablePersistence: RouteTripTimetablePersistence,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): TimeCacheWorker<RouteTripTimetable>() {

    override val cacheCategory = CacheCategory.ROUTE_TRIP_TIMETABLE

    override suspend fun saveToPersistence(data: RouteTripTimetable, resource: ResourceKey) =
        routeTripTimetablePersistence.save(data, resource)

    override suspend fun getFromPersistence(resource: ResourceKey, extras: Instant) =
        routeTripTimetablePersistence.get(resource, extras)!!

    suspend fun get(routeId: RouteId, serviceId: ServiceId, tripId: TripId, startOfDay: Instant): Cachable<RouteTripTimetable> {
        return run(
            routeClient.routeTripTimetableEndpointPair(routeId, serviceId, tripId),
            "route/${routeId}/service/${serviceId}/trip/${tripId}/timetable",
            startOfDay
        )
    }
}

@Factory
class RouteCleanupWorker(
    private val routePersistence: RoutePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock
): CleanupWorker() {

    override val cacheCategory: CacheCategory = CacheCategory.ROUTE
    override suspend fun delete(resource: ResourceKey) = routePersistence.clear()

}

@Factory
class RouteServicesCleanupWorker(
    private val routeServicePersistence: RouteServicePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock
): CleanupWorker() {

    override val cacheCategory: CacheCategory = CacheCategory.ROUTE_SERVICE
    override suspend fun delete(resource: ResourceKey) = routeServicePersistence.clear(resource)

}

@Factory
class RouteServiceTimetableCleanupWorker(
    private val routeServiceTimetablePersistence: RouteServiceTimetablePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock
): CleanupWorker() {

    override val cacheCategory: CacheCategory = CacheCategory.ROUTE_SERVICE_TIMETABLE
    override suspend fun delete(resource: ResourceKey) =
        routeServiceTimetablePersistence.clear(resource)

}

@Factory
class RouteServiceCanonicalTimetableCleanupWorker(
    private val routeServiceTimetablePersistence: RouteServiceCanonicalTimetablePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock
): CleanupWorker() {

    override val cacheCategory: CacheCategory = CacheCategory.ROUTE_SERVICE_CANONICAL_TIMETABLE
    override suspend fun delete(resource: ResourceKey) =
        routeServiceTimetablePersistence.clear(resource)

}

@Factory
class RouteTripTimetableCleanupWorker(
    private val routeServiceTimetablePersistence: RouteTripTimetablePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock
): CleanupWorker() {

    override val cacheCategory: CacheCategory = CacheCategory.ROUTE_TRIP_TIMETABLE
    override suspend fun delete(resource: ResourceKey) =
        routeServiceTimetablePersistence.clear(resource)

}

@Factory
class RouteRepository(
    private val routeCacheWorker: RoutesCacheWorker,
    private val routeServicesCacheWorker: RouteServicesCacheWorker,
    private val stopsCacheWorker: StopsCacheWorker,
    private val routeServiceTimetableCacheWorker: RouteServiceTimetableCacheWorker,
    private val routeServiceCanonicalTimetableCacheWorker: RouteServiceCanonicalTimetableCacheWorker,
    private val routeTripTimetableCacheWorker: RouteTripTimetableCacheWorker,
    private val routeCleanupWorker: RouteCleanupWorker,
    private val routeServicesCleanupWorker: RouteServicesCleanupWorker,
    private val routeServiceTimetableCleanupWorker: RouteServiceTimetableCleanupWorker,
    private val routeServiceCanonicalTimetableCleanupWorker: RouteServiceCanonicalTimetableCleanupWorker,
    private val routeTripTimetableCleanupWorker: RouteTripTimetableCleanupWorker,
    private val routePersistence: RoutePersistence,
    private val transportMetadataRepository: TransportMetadataRepository
) {

    suspend fun routes() = routeCacheWorker.get()
    suspend fun route(routeId: RouteId): Cachable<Route?> {
        val all = routeCacheWorker.get()
        return all.map { routePersistence.get(routeId) }
    }
    suspend fun routes(routeIds: List<RouteId>): Cachable<List<Route?>> {
        val all = routeCacheWorker.get()
        return all.map { routeIds.map { routePersistence.get(it) } }
    }

    suspend fun servicesForRoute(routeId: RouteId) = routeServicesCacheWorker.get(routeId)

    @Deprecated("Use tripTimetable")
    suspend fun serviceTimetable(routeId: RouteId, serviceId: ServiceId): Cachable<RouteServiceTimetable> {
        val stops = stopsCacheWorker.get()
        return stops.flatMap { routeServiceTimetableCacheWorker.get(routeId, serviceId) }
    }

    suspend fun canonicalServiceTimetable(routeId: RouteId, serviceId: ServiceId): Cachable<RouteServiceCanonicalTimetable> {
        val stops = stopsCacheWorker.get()
        return stops.flatMap { routeServiceCanonicalTimetableCacheWorker.get(routeId, serviceId) }
    }

    suspend fun tripTimetable(
        routeId: RouteId,
        serviceId: ServiceId,
        tripId: TripId,
        startOfDay: Instant? = null
    ): Cachable<RouteTripTimetable> {
        val stops = stopsCacheWorker.get()
        return stops.flatMap { routeTripTimetableCacheWorker.get(
            routeId,
            serviceId,
            tripId,
            startOfDay ?: transportMetadataRepository.scheduleStartOfDay()
        ) }
    }

    suspend fun ignoredRoutes(): List<RouteId> {
        return listOf("NIS", "X1", "X2", "X3", "X4")
    }

    suspend fun removedRoutes(): List<RouteId> {
        return listOf("NIS")
    }

    suspend fun cleanup() {
        routeCleanupWorker()
        routeServicesCleanupWorker()
        routeServiceTimetableCleanupWorker()
        routeServiceCanonicalTimetableCleanupWorker()
        routeTripTimetableCleanupWorker()
    }

}