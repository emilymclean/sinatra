package cl.emilym.sinatra.domain.migration

import cl.emilym.sinatra.data.persistence.ContentPersistence
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class ForceRefreshRemoteConfigOnUpdateAppMigration(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val contentPersistence: ContentPersistence,
): AppMigration {

    override suspend fun apply(previous: Int, current: Int, name: String) {
        try {
            remoteConfigRepository.forceReload()
        } catch(e: Exception) {
            Napier.e(e)
            return
        }
        contentPersistence.clearCache()
    }
}