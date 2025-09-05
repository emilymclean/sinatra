package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.StopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

@Factory
class GetFilteredStopsUseCase(
    private val stopRepository: StopRepository,
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(): Flow<Cachable<List<Stop>>> = flow {
        val stops = stopRepository.stops()
        emitAll(
            preferencesRepository.preference(Preference.ShowSchoolServices).flow.mapLatest {
                when (it) {
                    true -> stops
                    else -> stops.map { it.filter { !it.schoolServiceOnly } }
                }
            }
        )
    }

}