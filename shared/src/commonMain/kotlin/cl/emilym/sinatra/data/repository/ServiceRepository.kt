package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.ServiceClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.persistence.ServicePersistence
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

class ServiceCacheWorker(
    private val serviceClient: ServiceClient,
    private val servicePersistence: ServicePersistence,
    override val shaRepository: ShaRepository,
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
class ServiceRepository(
    private val serviceCacheWorker: ServiceCacheWorker
) {

    suspend fun services() = serviceCacheWorker.get()

}