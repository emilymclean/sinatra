package cl.emilym.sinatra.data.repository

import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import cl.emilym.sinatra.e

@Factory
class RoutingPreferencesRepository(
    private val preferencesRepository: PreferencesRepository
) {

    suspend fun maximumWalkingTime(): Duration {
        return try {
            preferencesRepository.maximumWalkingTime.current().toDouble().minutes
        } catch(e: Exception) {
            Napier.e(e)
            30.minutes
        }
    }

    suspend fun requiresWheelchair(): Boolean {
        return preferencesRepository.requiresWheelchair.current()
    }

    suspend fun requiresBikes(): Boolean {
        return preferencesRepository.requiresBikes.current()
    }

}