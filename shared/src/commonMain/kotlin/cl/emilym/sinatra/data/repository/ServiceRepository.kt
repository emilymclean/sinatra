package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.ServiceClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Service
import org.koin.core.annotation.Factory

@Factory
class ServiceRepository(
    private val serviceClient: ServiceClient
) {

    suspend fun services(): Cachable<List<Service>> {
        return Cachable.live(serviceClient.services())
    }

}