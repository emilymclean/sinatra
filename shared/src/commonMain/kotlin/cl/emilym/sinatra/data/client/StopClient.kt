package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.network.GtfsApi
import cl.emilym.sinatra.network.validated
import org.koin.core.annotation.Factory

@Factory
class StopClient(
    private val gtfsApi: GtfsApi
) {

    val stopsEndpointPair by lazy {
        object : ValidatedEndpointDigestPair<List<Stop>>() {
            override val endpoint = ::stops
            override val digest = ::stopsDigest
        }
    }

    fun timetableEndpointPair(stopId: StopId) = object : ValidatedEndpointDigestPair<StopTimetable>() {
        override val endpoint: suspend (ShaDigest) -> StopTimetable = { digest -> timetable(digest, stopId) }
        override val digest = suspend { timetableDigest(stopId) }
    }

    suspend fun stops(digest: ShaDigest): List<Stop> {
        val pbStops = gtfsApi.stops().validated(digest)
        return pbStops.stop.map { Stop.fromPB(it) }
    }

    suspend fun stopsDigest(): ShaDigest {
        return gtfsApi.stopsDigest()
    }

    suspend fun timetable(digest: ShaDigest, stopId: StopId): StopTimetable {
        val pbTimetable = gtfsApi.stopTimetable(stopId).validated(digest)
        return StopTimetable.fromPB(pbTimetable)
    }

    suspend fun timetableDigest(stopId: StopId): ShaDigest {
        return gtfsApi.stopTimetableDigest(stopId)
    }

}