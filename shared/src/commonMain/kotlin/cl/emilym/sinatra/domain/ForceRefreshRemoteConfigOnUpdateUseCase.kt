package cl.emilym.sinatra.domain

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.persistence.ContentPersistence
import cl.emilym.sinatra.data.repository.AppRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class ForceRefreshRemoteConfigOnUpdateUseCase(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val appRepository: AppRepository,
    private val contentPersistence: ContentPersistence,
    private val build: BuildInformation
) {

    suspend operator fun invoke() {
        val current = build.versionNumber.toInt()
        val previous = appRepository.lastAppCode()

        if (current == previous) return

        try {
            remoteConfigRepository.forceReload()
        } catch(e: Exception) {
            Napier.e(e)
            return
        }

        appRepository.setLastAppCode(current)
        contentPersistence.clearCache()
    }

}