package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.network.GtfsApi
import org.koin.core.annotation.Factory

@Factory
class StopClient(
    private val gtfsApi: GtfsApi
) {

    val stopsEndpointPair by lazy {
        object : EndpointDigestPair<List<Stop>>() {
            override val endpoint = ::stops
            override val digest = ::stopsDigest
        }
    }

    fun timetableEndpointPair(stopId: StopId) = object : EndpointDigestPair<StopTimetable>() {
        override val endpoint = suspend { timetable(stopId) }
        override val digest = suspend { timetableDigest(stopId) }
    }

    suspend fun stops(): List<Stop> {
        val pbStops = gtfsApi.stops()
        return pbStops.stop.map { Stop.fromPB(it) }
    }

    suspend fun stopsDigest(): ShaDigest {
        return gtfsApi.stopsDigest()
    }

    suspend fun timetable(stopId: StopId): StopTimetable {
        val pbTimetable = gtfsApi.stopTimetable(stopId)
        return StopTimetable.fromPB(pbTimetable)
    }

    suspend fun timetableDigest(stopId: StopId): ShaDigest {
        return gtfsApi.stopTimetableDigest(stopId)
    }

}