package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.NetworkGraphClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.persistence.NetworkGraphPersistence
import cl.emilym.sinatra.router.data.NetworkGraph
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

@Factory
class NetworkGraphCacheWorker(
    private val networkGraphPersistence: NetworkGraphPersistence,
    private val networkGraphClient: NetworkGraphClient,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<ByteArray>() {

    override val cacheCategory = CacheCategory.NETWORK_GRAPH

    override suspend fun saveToPersistence(data: ByteArray, resource: ResourceKey) =
        networkGraphPersistence.save(data)
    override suspend fun getFromPersistence(resource: ResourceKey) = networkGraphPersistence.get()

    suspend fun get(): Cachable<NetworkGraph> {
        return run(networkGraphClient.networkGraphEndpointDigestPair, "network-graph-byte").map {
            NetworkGraph.byteFormatForByteArray(it)
        }
    }

}

@Factory
class NetworkGraphRepository(
    private val networkGraphCacheWorker: NetworkGraphCacheWorker
) {

    suspend fun networkGraph(): Cachable<NetworkGraph> {
        return networkGraphCacheWorker.get()
    }

}