package cl.emilym.sinatra.data.repository

import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.data.client.NetworkGraphClient
import cl.emilym.sinatra.data.client.StopClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.persistence.NetworkGraphPersistence
import cl.emilym.sinatra.data.persistence.StopPersistence
import kotlinx.datetime.Clock

class NetworkGraphCacheWorker(
    private val networkGraphPersistence: NetworkGraphPersistence,
    private val networkGraphClient: NetworkGraphClient,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<Graph>() {

    override val cacheCategory = CacheCategory.NETWORK_GRAPH

    override suspend fun saveToPersistence(data: Graph, resource: ResourceKey) =
        networkGraphPersistence.save(data)
    override suspend fun getFromPersistence(resource: ResourceKey) = networkGraphPersistence.get()
    override suspend fun existsInPersistence(resource: ResourceKey) =
        networkGraphPersistence.exists()

    suspend fun get(): Cachable<Graph> {
        return run(networkGraphClient.networkGraphEndpointDigestPair, "network-graph")
    }

}

class NetworkGraphRepository(
    private val networkGraphCacheWorker: NetworkGraphCacheWorker
) {

    suspend fun networkGraph(): Cachable<Graph> {
        return networkGraphCacheWorker.get()
    }

}