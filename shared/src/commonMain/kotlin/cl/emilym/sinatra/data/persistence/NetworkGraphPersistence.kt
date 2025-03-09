package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.JourneySearchConfig
import cl.emilym.sinatra.data.models.JourneySearchOption
import cl.emilym.sinatra.data.persistence.NetworkGraphPersistence.Companion.NETWORK_GRAPH_CACHE_FILENAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Factory
class NetworkGraphPersistence(
    private val cacheFileWriter: CacheFileWriter
) {

    companion object {
        const val NETWORK_GRAPH_CACHE_FILENAME = "network-graph.pb"
    }

    suspend fun save(graph: ByteArray) {
        withContext(Dispatchers.IO) {
            cacheFileWriter.save(NETWORK_GRAPH_CACHE_FILENAME, graph)
        }
    }

    suspend fun get(): ByteArray? {
        return withContext(Dispatchers.IO) {
            cacheFileWriter.retrieve(NETWORK_GRAPH_CACHE_FILENAME)
        }
    }

    suspend fun exists(): Boolean {
        return withContext(Dispatchers.IO) {
            cacheFileWriter.exists(NETWORK_GRAPH_CACHE_FILENAME)
        }
    }

}

@Factory
class JourneyConfigPersistence(
    private val cacheFileWriter: CacheFileWriter
) {

    companion object {
        const val JOURNEY_CONFIG_CACHE_FILENAME = "journey-config.pb"

        val DEFAULT = JourneySearchConfig(
            maximumComputationTime = 30.seconds,
            options = listOf(
                JourneySearchOption(
                    10.minutes,
                    5.minutes,
                    5.minutes.inWholeSeconds.toInt(),
                    5.minutes,
                    5.minutes.inWholeSeconds.toInt()
                )
            )
        )
    }

    suspend fun save(graph: ByteArray) {
        withContext(Dispatchers.IO) {
            cacheFileWriter.save(JOURNEY_CONFIG_CACHE_FILENAME, graph)
        }
    }

    suspend fun get(): ByteArray? {
        return withContext(Dispatchers.IO) {
            cacheFileWriter.retrieve(JOURNEY_CONFIG_CACHE_FILENAME)
        }
    }

    suspend fun exists(): Boolean {
        return withContext(Dispatchers.IO) {
            cacheFileWriter.exists(JOURNEY_CONFIG_CACHE_FILENAME)
        }
    }

}