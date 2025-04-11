package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.RouteClient
import cl.emilym.sinatra.data.client.ServiceAlertClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteServiceCanonicalTimetable
import cl.emilym.sinatra.data.models.RouteServiceTimetable
import cl.emilym.sinatra.data.models.RouteTripTimetable
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.persistence.RoutePersistence
import cl.emilym.sinatra.data.persistence.RouteServiceCanonicalTimetablePersistence
import cl.emilym.sinatra.data.persistence.RouteServicePersistence
import cl.emilym.sinatra.data.persistence.RouteServiceTimetablePersistence
import cl.emilym.sinatra.data.persistence.RouteTripTimetablePersistence
import cl.emilym.sinatra.data.persistence.ServiceAlertPersistence
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Factory
class ServiceAlertCacheWorker(
    private val serviceAlertPersistence: ServiceAlertPersistence,
    private val serviceAlertClient: ServiceAlertClient,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<List<ServiceAlert>>() {

    override val cacheCategory: CacheCategory = CacheCategory.SERVICE_ALERT
    override val expireTime: Duration = 12.hours

    override suspend fun saveToPersistence(data: List<ServiceAlert>, resource: ResourceKey) =
        serviceAlertPersistence.save(data)
    override suspend fun getFromPersistence(resource: ResourceKey): List<ServiceAlert> =
        serviceAlertPersistence.get()

    suspend fun get(): Cachable<List<ServiceAlert>> {
        return run(serviceAlertClient.serviceAlertsPair, "alerts")
    }

}

@Factory
class ServiceAlertCleanupWorker(
    private val serviceAlertPersistence: ServiceAlertPersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock
): CleanupWorker() {

    override val cacheCategory: CacheCategory = CacheCategory.SERVICE_ALERT
    override suspend fun delete(resource: ResourceKey) = serviceAlertPersistence.clear()

}

@Factory
class ServiceAlertRepository(
    private val serviceAlertCacheWorker: ServiceAlertCacheWorker,
    private val serviceAlertCleanupWorker: ServiceAlertCleanupWorker
) {

    suspend fun alerts() = serviceAlertCacheWorker.get()

    suspend fun cleanup() {
        serviceAlertCleanupWorker()
    }

}