package cl.emilym.sinatra.data.client

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

    val networkGraphReverseEndpointDigestPair = object: EndpointDigestPair<ByteArray>() {
        override val endpoint = ::networkGraphReverse
        override val digest = ::networkGraphReverseDigest
    }

    val journeyConfigEndpointDigestPair = object: EndpointDigestPair<ByteArray>() {
        override val endpoint = ::journeyConfig
        override val digest = ::journeyConfigDigest
    }

    suspend fun networkGraph(): ByteArray {
        return gtfsApi.networkGraph()
    }

    suspend fun networkGraphDigest(): ShaDigest {
        return gtfsApi.networkGraphDigest()
    }

    suspend fun networkGraphReverse(): ByteArray {
        return gtfsApi.reverseNetworkGraph()
    }

    suspend fun networkGraphReverseDigest(): ShaDigest {
        return gtfsApi.reverseNetworkGraphDigest()
    }

    suspend fun journeyConfig(): ByteArray {
        return gtfsApi.journeyConfig()
    }

    suspend fun journeyConfigDigest(): ShaDigest {
        return gtfsApi.journeyConfigDigest()
    }

}