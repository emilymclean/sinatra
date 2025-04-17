package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.repository.CacheRepository
import cl.emilym.sinatra.data.repository.ShaRepository
import org.koin.core.annotation.Factory

@Factory
class CacheInvalidationUseCase(
    private val cacheRepository: CacheRepository,
    private val shaRepository: ShaRepository
) {

    suspend operator fun invoke() {
        if (cacheRepository.shouldInvalidate()) {
            shaRepository.invalidateAll()
        }
    }

}