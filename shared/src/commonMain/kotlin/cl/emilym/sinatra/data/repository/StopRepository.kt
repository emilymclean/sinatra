package cl.emilym.sinatra.data.repository

import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.data.client.EndpointDigestPair
import cl.emilym.sinatra.data.client.StopClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.room.entities.TYPE_STOP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.hours

@Factory
class StopRepository(
    private val stopClient: StopClient,
    private val shaRepository: ShaRepository
) {

    fun stops(): Cachable<Flow<List<Stop>>> {
        val pair = stopClient.stopsEndpointPair
        shaRepository.save(pair.digest(), TYPE_STOP, pair.resource)
        return Cachable.live(pair.endpoint())
    }

    suspend fun timetable(stopId: StopId): Cachable<StopTimetable> {
        return Cachable.live(stopClient.timetable(stopId))
    }

}