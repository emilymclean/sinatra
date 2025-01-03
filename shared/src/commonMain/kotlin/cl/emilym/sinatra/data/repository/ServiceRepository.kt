package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.ServiceClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.persistence.ServicePersistence
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

@Factory
class ServiceCacheWorker(
    private val serviceClient: ServiceClient,
    private val servicePersistence: ServicePersistence,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<List<Service>>() {
    override val cacheCategory: CacheCategory = CacheCategory.SERVICE

    override suspend fun saveToPersistence(data: List<Service>, resource: ResourceKey) =
        servicePersistence.save(data)

    override suspend fun getFromPersistence(resource: ResourceKey) =
        servicePersistence.get()

    suspend fun get(): Cachable<List<Service>> {
        return run(serviceClient.servicesEndpointPair, "services")
    }

}

@Factory
class ServiceCleanupWorker(
    private val servicePersistence: ServicePersistence,
    override val shaRepository: ShaRepository,
    override val clock: Clock
): CleanupWorker() {

    override val cacheCategory: CacheCategory = CacheCategory.SERVICE
    override suspend fun delete(resource: ResourceKey) =
        servicePersistence.clear()

}

@Factory
class ServiceRepository(
    private val serviceCacheWorker: ServiceCacheWorker,
    private val serviceCleanupWorker: ServiceCleanupWorker,
    private val servicePersistence: ServicePersistence
) {

    suspend fun services() = serviceCacheWorker.get()
    suspend fun services(ids: List<ServiceId>): Cachable<List<Service>> {
        val s = serviceCacheWorker.get()
        return Cachable(servicePersistence.get(ids), s.state)
    }

    suspend fun cleanup() {
        serviceCleanupWorker()
    }

}