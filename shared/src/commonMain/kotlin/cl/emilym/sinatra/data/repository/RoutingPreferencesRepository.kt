package cl.emilym.sinatra.data.repository

import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Factory
class RoutingPreferencesRepository {

    suspend fun maximumWalkingTime(): Duration {
        return 30.minutes
    }

    suspend fun requiresWheelchair(): Boolean {
        return false
    }

    suspend fun requiresBikes(): Boolean {
        return false
    }

}