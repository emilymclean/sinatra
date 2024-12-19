package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.e
import cl.emilym.sinatra.lib.Tokenizer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import kotlin.math.max

sealed interface SearchResult {
    data class RouteResult(val route: Route): SearchResult
    data class StopResult(val stop: Stop): SearchResult
    data class PlaceResult(val place: Place): SearchResult
}

@Factory
class RouteStopSearchUseCase(
    routeTypeSearcher: RouteTypeSearcher,
    stopTypeSearcher: StopTypeSearcher,
    placeTypeSearcher: PlaceTypeSearcher,
    private val tokenizer: Tokenizer
) {

    private val searchers = listOf(routeTypeSearcher, stopTypeSearcher, placeTypeSearcher)

    suspend operator fun invoke(query: String): List<SearchResult> {
        val tokens = tokenizer.tokenize(query)
        val results = withContext(Dispatchers.IO) {
            val out = mutableListOf<RankableResult<*>>()
            for (searcher in searchers) {
                try {
                    out.addAll(searcher(tokens))
                } catch (e: Exception) {
                    Napier.e(e)
                }
            }
            out.toList()
        }
        return results.sortedByDescending { it.score }.mapNotNull {
            when (it.result) {
                is Route -> SearchResult.RouteResult(it.result)
                is Stop -> SearchResult.StopResult(it.result)
                is Place -> SearchResult.PlaceResult(it.result)
                else -> null
            }
        }
    }

}

data class RankableResult<T>(
    val result: T,
    val score: Double
)

interface TypeSearcher<T> {
    suspend operator fun invoke(tokens: List<String>): List<RankableResult<T>>
    fun wrap(item: T): SearchResult
}

abstract class AbstractTypeSearcher<T>: TypeSearcher<T> {

    protected abstract fun fields(t: T): List<String>

    open fun scoreMultiplier(item: T): Double { return 1.0 }

    protected fun search(tokens: List<String>, space: List<T>): List<RankableResult<T>> {
        return space.mapNotNull {
            match(tokens, it)
        }
    }

    private fun exclusiveMatch(tokens: List<String>, field: String): List<String> {
        val matches = mutableListOf<String>()
        val remainingField = StringBuilder(field)
        val sortedTokens = tokens.sortedByDescending { it.length }

        for (token in sortedTokens) {
            val index = remainingField.indexOf(token)
            if (index == -1) continue
            matches.add(token)
            for (i in index until (index + token.length)) {
                remainingField[i] = '\u0000'
            }
        }

        return matches
    }

    private fun matchScore(matchingTokens: List<String>, field: String): Double {
        return matchingTokens.sumOf { it.length }.toDouble() / field.length.toDouble()
    }

    private fun match(tokens: List<String>, item: T): RankableResult<T>? {
        val fields = fields(item).map { it.lowercase() }
        val matchedTokens = mutableSetOf<String>()
        var highestScore = 0.0
        for (field in fields) {
            val matches = exclusiveMatch(tokens, field)
            matchedTokens.addAll(matches)
            highestScore = max(highestScore, matchScore(matches, field))
        }

        return when {
            matchedTokens.size != tokens.size -> null
            highestScore == 0.0 -> null
            else -> RankableResult(item, scoreMultiplier(item) * highestScore)
        }
    }

}

abstract class LocalTypeSearcher<T>: AbstractTypeSearcher<T>() {

    private var cache: List<T>? = null
    private val lock = Mutex()

    protected abstract suspend fun load(): List<T>

    private suspend fun space(): List<T> {
        cache?.let { return it }
        return lock.withLock {
            cache?.let { return@withLock it }
            load().also {
                cache = it
            }
        }
    }

    override suspend operator fun invoke(tokens: List<String>): List<RankableResult<T>> {
        val space = space()
        return search(tokens, space)
    }

}