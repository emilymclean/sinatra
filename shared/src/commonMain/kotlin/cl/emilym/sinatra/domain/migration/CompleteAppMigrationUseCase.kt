package cl.emilym.sinatra.domain.migration

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.repository.AppRepository
import org.koin.core.annotation.Factory

interface AppMigration {

    suspend fun apply(previous: Int, current: Int, name: String)

}

@Factory
class CompleteAppMigrationUseCase(
    private val appRepository: AppRepository,
    private val build: BuildInformation,
    forceRefreshRemoteConfigOnUpdateAppMigration: ForceRefreshRemoteConfigOnUpdateAppMigration,
    clearCacheOn017AppMigration: ClearCacheOn017AppMigration
) {

    private val migrations = listOf<AppMigration>(
        forceRefreshRemoteConfigOnUpdateAppMigration,
        clearCacheOn017AppMigration
    )

    suspend operator fun invoke() {
        val current = build.versionNumber.toInt()
        val previous = appRepository.lastAppCode()

        if (current == previous) return

        for (migration in migrations) {
            migration.apply(previous, current, build.versionName)
        }

        appRepository.setLastAppCode(current)
    }

}