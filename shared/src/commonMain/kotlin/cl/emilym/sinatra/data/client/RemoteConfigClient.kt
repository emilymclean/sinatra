package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.RemoteConfigNotLoadedException
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class RemoteConfigClient(
    private val wrapper: RemoteConfigWrapper
) {

    suspend fun load(): Boolean {
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
        return try {
             operation(key)
        } catch(e: Exception) {
            Napier.e(e)
            null
        }
    }

    private fun <T> getIfExists(key: String, operation: RemoteConfigGetter<T>): T? {
        if (!wrapper.loaded) throw RemoteConfigNotLoadedException()
        if (!wrapper.exists(key)) return null
        return try {
            operation(key)
        } catch(e: Exception) {
            Napier.e(e)
            null
        }
    }

    private val stringGet: RemoteConfigGetter<String> = { wrapper.string(it) }
    private val numberGet: RemoteConfigGetter<Double> = { wrapper.number(it) }
    private val booleanGet: RemoteConfigGetter<Boolean> = { wrapper.boolean(it) }

    suspend fun string(key: String) = getIfExistsAndLoaded(key, stringGet)
    suspend fun number(key: String) = getIfExistsAndLoaded(key, numberGet)
    suspend fun boolean(key: String) = getIfExistsAndLoaded(key, booleanGet)

    fun stringImmediate(key: String) = getIfExists(key, stringGet)
    fun numberImmediate(key: String) = getIfExists(key, numberGet)
    fun booleanImmediate(key: String) = getIfExists(key, booleanGet)

}

interface RemoteConfigWrapper {
    val loaded: Boolean
    suspend fun load()
    fun exists(key: String): Boolean
    fun string(key: String): String
    fun number(key: String): Double
    fun boolean(key: String): Boolean
}

typealias RemoteConfigGetter<T> = (key: String) -> T