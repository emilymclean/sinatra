package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class RemoteConfigClient(
    private val wrapper: RemoteConfigWrapper
) {

    private suspend fun load(): Boolean {
        return try {
            wrapper.load()
            true
        } catch(e: Exception) {
            Napier.e(e)
            false
        }
    }

    private suspend fun <T> getIfExistsAndLoaded(key: String, operation: RemoteConfigGetter<T>): T? {
        if (!load()) return null
        if (!wrapper.exists(key)) return null
        return operation(key)
    }

    private val stringGet: RemoteConfigGetter<String> = { wrapper.string(it) }
    private val numberGet: RemoteConfigGetter<Double> = { wrapper.number(it) }
    private val booleanGet: RemoteConfigGetter<Boolean> = { wrapper.boolean(it) }

    suspend fun string(key: String) = getIfExistsAndLoaded(key, stringGet)
    suspend fun number(key: String) = getIfExistsAndLoaded(key, numberGet)
    suspend fun boolean(key: String) = getIfExistsAndLoaded(key, booleanGet)

}

interface RemoteConfigWrapper {
    suspend fun load()
    suspend fun exists(key: String): Boolean
    suspend fun string(key: String): String
    suspend fun number(key: String): Double
    suspend fun boolean(key: String): Boolean
}

typealias RemoteConfigGetter<T> = suspend (key: String) -> T