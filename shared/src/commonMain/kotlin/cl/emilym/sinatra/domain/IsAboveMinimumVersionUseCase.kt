package cl.emilym.sinatra.domain

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.models.VersionName
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class IsAboveMinimumVersionUseCase(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val buildInformation: BuildInformation
) {

    suspend operator fun invoke(): Boolean {
        val minimumVersion = try {
            remoteConfigRepository.minimumVersion()?.parse ?: return true
        } catch(e: Exception) {
            Napier.e(e)
            return true
        }
        val currentVersion = buildInformation.versionName.parse

        for (i in 0..2) {
            if (currentVersion[i] < minimumVersion[i]) return false
        }
        return true
    }

    val VersionName.parse: List<Int> get() =
        split(".")
            .take(3)
            .map { it.takeWhile { it.isDigit() }.toInt() }

}
