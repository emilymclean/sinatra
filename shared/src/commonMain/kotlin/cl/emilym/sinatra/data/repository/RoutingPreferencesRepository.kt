package cl.emilym.sinatra.data.repository

import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Factory
class RoutingPreferencesRepository(
    private val preferencesRepository: PreferencesRepository
) {

    suspend fun maximumWalkingTime(): Duration {
        return preferencesRepository.preference(Preference.MaximumWalkingTime).current().toDouble().minutes
    }

    suspend fun requiresWheelchair(): Boolean {
        return preferencesRepository.preference(Preference.RequiresWheelchair).current()
    }

    suspend fun requiresBikes(): Boolean {
        return preferencesRepository.preference(Preference.RequiresBikes).current()
    }

    suspend fun schoolServiceAllowed(): Boolean {
        return preferencesRepository.preference(Preference.ShowSchoolServices).current()
    }

}