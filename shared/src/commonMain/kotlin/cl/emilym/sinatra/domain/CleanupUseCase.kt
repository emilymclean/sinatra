package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory

@Factory
class CleanupUseCase(
    private val stopRepository: StopRepository,
    private val serviceRepository: ServiceRepository,
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            stopRepository.cleanup()
            serviceRepository.cleanup()
            routeRepository.cleanup()
        }
    }

}