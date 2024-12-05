package cl.emilym.betterbuscanberra.data.client

import cl.emilym.betterbuscanberra.data.models.Stop
import cl.emilym.betterbuscanberra.network.GtfsApi
import cl.emilym.gtfs.StopEndpoint
import org.koin.core.annotation.Factory
import pbandk.decodeFromByteArray

@Factory
class StopClient(
    private val gtfsApi: GtfsApi
) {

    suspend fun stops(): List<Stop> {
        val pbStops = StopEndpoint.decodeFromByteArray(gtfsApi.stops())
        return pbStops.stop.map { Stop.fromPB(it) }
    }

}