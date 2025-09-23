package cl.emilym.sinatra.domain.migration

import cl.emilym.sinatra.data.repository.ShaRepository
import org.koin.core.annotation.Factory

@Factory
class ClearCacheOn017AppMigration(
    private val shaRepository: ShaRepository
): AppMigration {

    override suspend fun apply(previous: Int, current: Int, name: String) {
        if (previous <= 704) return // Version number for 0.16.1
        shaRepository.invalidateAll()
    }
}