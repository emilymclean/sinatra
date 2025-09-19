package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.network.GtfsApi
import cl.emilym.sinatra.network.validated
import org.koin.core.annotation.Factory

@Factory
class NetworkGraphClient(
    private val gtfsApi: GtfsApi
) {

    val networkGraphEndpointDigestPair = object: ValidatedEndpointDigestPair<ByteArray>() {
        override val endpoint = ::networkGraph
        override val digest = ::networkGraphDigest
    }

    val networkGraphReverseEndpointDigestPair = object: ValidatedEndpointDigestPair<ByteArray>() {
        override val endpoint = ::networkGraphReverse
        override val digest = ::networkGraphReverseDigest
    }

    val journeyConfigEndpointDigestPair = object: ValidatedEndpointDigestPair<ByteArray>() {
        override val endpoint = ::journeyConfig
        override val digest = ::journeyConfigDigest
    }

    suspend fun networkGraph(digest: ShaDigest): ByteArray {
        return gtfsApi.networkGraph().validated(digest)
    }

    suspend fun networkGraphDigest(): ShaDigest {
        return gtfsApi.networkGraphDigest()
    }

    suspend fun networkGraphReverse(digest: ShaDigest): ByteArray {
        return gtfsApi.reverseNetworkGraph().validated(digest)
    }

    suspend fun networkGraphReverseDigest(): ShaDigest {
        return gtfsApi.reverseNetworkGraphDigest()
    }

    suspend fun journeyConfig(digest: ShaDigest): ByteArray {
        return gtfsApi.journeyConfig().validated(digest)
    }

    suspend fun journeyConfigDigest(): ShaDigest {
        return gtfsApi.journeyConfigDigest()
    }

}