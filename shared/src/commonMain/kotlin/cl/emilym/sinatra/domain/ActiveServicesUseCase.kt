package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory

@Factory
class ActiveServicesUseCase(
    private val serviceRepository: ServiceRepository,
    private val clock: Clock,
    private val metadataRepository: TransportMetadataRepository
) {

    suspend operator fun invoke(instant: Instant = clock.now()): Cachable<List<Service>> {
        return serviceRepository.services().map { it.filter { it.active(instant, metadataRepository.timeZone()) } }
    }

}