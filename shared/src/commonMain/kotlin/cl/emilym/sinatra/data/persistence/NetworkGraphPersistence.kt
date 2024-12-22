package cl.emilym.sinatra.data.persistence

import cl.emilym.gtfs.networkgraph.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import pbandk.decodeFromByteArray
import pbandk.encodeToByteArray

@Factory
class NetworkGraphPersistence(
    private val cacheFileWriter: CacheFileWriter
) {

    companion object {
        const val NETWORK_GRAPH_CACHE_FILENAME = "network-graph.pb"
    }

    suspend fun save(graph: Graph) {
        withContext(Dispatchers.IO) {
            cacheFileWriter.save(NETWORK_GRAPH_CACHE_FILENAME, graph.encodeToByteArray())
        }
    }

    suspend fun get(): Graph? {
        return withContext(Dispatchers.IO) {
            cacheFileWriter.retrieve(NETWORK_GRAPH_CACHE_FILENAME)?.let {
                Graph.decodeFromByteArray(it)
            }
        }
    }

    suspend fun exists(): Boolean {
        return withContext(Dispatchers.IO) {
            cacheFileWriter.exists(NETWORK_GRAPH_CACHE_FILENAME)
        }
    }

}