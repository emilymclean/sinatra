package cl.emilym.sinatra.data.client

import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.network.GtfsApi
import org.koin.core.annotation.Factory

@Factory
class NetworkGraphClient(
    private val gtfsApi: GtfsApi
) {

    val networkGraphEndpointDigestPair = object: EndpointDigestPair<ByteArray>() {
        override val endpoint = ::networkGraph
        override val digest = ::networkGraphDigest
    }

    suspend fun networkGraph(): ByteArray {
        return gtfsApi.networkGraph()
    }

    suspend fun networkGraphDigest(): ShaDigest {
        return gtfsApi.networkGraphDigest()
    }

}