package cl.emilym.sinatra.data.repository

import cl.emilym.gtfs.JourneySearchConfigEndpoint
import cl.emilym.sinatra.data.client.NetworkGraphClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.JourneySearchConfig
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.persistence.JourneyConfigPersistence
import cl.emilym.sinatra.data.persistence.NetworkGraphPersistence
import cl.emilym.sinatra.router.data.NetworkGraph
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import pbandk.decodeFromByteArray

@Factory
class NetworkGraphCacheWorker(
    private val networkGraphPersistence: NetworkGraphPersistence,
    private val networkGraphClient: NetworkGraphClient,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<ByteArray>() {

    override val cacheCategory = CacheCategory.NETWORK_GRAPH

    override suspend fun saveToPersistence(data: ByteArray, resource: ResourceKey) =
        networkGraphPersistence.save(data, reverse = false)
    override suspend fun getFromPersistence(resource: ResourceKey) = networkGraphPersistence.get(reverse = false)

    suspend fun get(): Cachable<NetworkGraph> {
        return run(networkGraphClient.networkGraphEndpointDigestPair, "network-graph-byte").map {
            NetworkGraph.byteFormatForByteArray(it)
        }
    }

}

@Factory
class NetworkGraphReverseCacheWorker(
    private val networkGraphPersistence: NetworkGraphPersistence,
    private val networkGraphClient: NetworkGraphClient,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<ByteArray>() {

    override val cacheCategory = CacheCategory.NETWORK_GRAPH

    override suspend fun saveToPersistence(data: ByteArray, resource: ResourceKey) =
        networkGraphPersistence.save(data, reverse = true)
    override suspend fun getFromPersistence(resource: ResourceKey) = networkGraphPersistence.get(reverse = true)

    suspend fun get(): Cachable<NetworkGraph> {
        return run(networkGraphClient.networkGraphReverseEndpointDigestPair, "network-graph-reverse-byte").map {
            NetworkGraph.byteFormatForByteArray(it)
        }
    }

}

@Factory
class JourneyConfigCacheWorker(
    private val journeyConfigPersistence: JourneyConfigPersistence,
    private val networkGraphClient: NetworkGraphClient,
    override val cacheWorkerDependencies: CacheWorkerDependencies,
    override val clock: Clock,
): CacheWorker<ByteArray>() {

    override val cacheCategory = CacheCategory.NETWORK_GRAPH

    override suspend fun saveToPersistence(data: ByteArray, resource: ResourceKey) =
        journeyConfigPersistence.save(data)
    override suspend fun getFromPersistence(resource: ResourceKey) = journeyConfigPersistence.get()

    suspend fun get(): Cachable<JourneySearchConfig> {
        return run(networkGraphClient.journeyConfigEndpointDigestPair, "journey-config-byte").map {
            JourneySearchConfig.fromPb(
                JourneySearchConfigEndpoint.decodeFromByteArray(it)
            )
        }
    }

}

@Factory
class NetworkGraphRepository(
    private val networkGraphCacheWorker: NetworkGraphCacheWorker,
    private val networkGraphReverseCacheWorker: NetworkGraphReverseCacheWorker,
    private val journeyConfigCacheWorker: JourneyConfigCacheWorker,
) {

    suspend fun networkGraph(reverse: Boolean = false): Cachable<NetworkGraph> {
        return when {
            reverse -> networkGraphReverseCacheWorker.get()
            else -> networkGraphCacheWorker.get()
        }
    }

    suspend fun config(): Cachable<JourneySearchConfig> {
        return try {
            journeyConfigCacheWorker.get()
        } catch (e: Exception) {
            Cachable.live(JourneyConfigPersistence.DEFAULT)
        }
    }

}