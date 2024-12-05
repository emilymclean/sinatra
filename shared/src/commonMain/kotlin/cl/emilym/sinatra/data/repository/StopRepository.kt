package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.StopClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetable
import org.koin.core.annotation.Factory

@Factory
class StopRepository(
    private val stopClient: StopClient
) {

    suspend fun stops(): Cachable<List<Stop>> {
        return Cachable.live(stopClient.stops())
    }

    suspend fun timetable(stopId: StopId): Cachable<StopTimetable> {
        return Cachable.live(stopClient.timetable(stopId))
    }

}