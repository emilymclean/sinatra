package cl.emilym.betterbuscanberra.data.repository

import cl.emilym.betterbuscanberra.data.client.ServiceClient
import cl.emilym.betterbuscanberra.data.models.Cachable
import cl.emilym.betterbuscanberra.data.models.Service
import org.koin.core.annotation.Factory

@Factory
class ServiceRepository(
    private val serviceClient: ServiceClient
) {

    suspend fun services(): Cachable<List<Service>> {
        return Cachable.live(serviceClient.services())
    }

}