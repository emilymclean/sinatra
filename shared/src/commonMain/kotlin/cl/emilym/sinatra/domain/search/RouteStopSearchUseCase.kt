package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.lib.Tokenizer
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
}

class SearchSpace(
    val routes: List<Route>,
    val stops: List<Stop>
)

@Factory
class RouteStopSearchUseCase(
    private val routeRepository: RouteRepository,
    private val stopRepository: StopRepository,
    private val routeTypeSearcher: RouteTypeSearcher,
    private val stopTypeSearcher: StopTypeSearcher,
    private val tokenizer: Tokenizer
) {

    private val searchLock = Mutex()
    private var searchSpace: SearchSpace? = null

    suspend operator fun invoke(query: String): List<SearchResult> {
        val tokens = tokenizer.tokenize(query)
        val space = get()
        val results = withContext(Dispatchers.IO) {
            listOf(
                routeTypeSearcher(tokens, space.routes),
                stopTypeSearcher(tokens, space.stops)
            ).flatten()
        }
        return results.sortedByDescending { it.score }.mapNotNull {
            when (it.result) {
                is Route -> SearchResult.RouteResult(it.result)
                is Stop -> SearchResult.StopResult(it.result)
                else -> null
            }
        }
    }

    private suspend fun get(): SearchSpace {
        searchLock.withLock {
            searchSpace.also { if (it != null) return it }
            val removedRoutes = routeRepository.removedRoutes()
            val routes = routeRepository.routes().map { it.filter { it.id !in removedRoutes } }
            val stops = stopRepository.stops()
            return SearchSpace(
                routes.item, stops.item
            ).also { searchSpace = it }
        }
    }

}

data class RankableResult<T>(
    val result: T,
    val score: Double
)

abstract class TypeSearcher<T> {

    protected abstract fun fields(t: T): List<String>

    open fun scoreMultiplier(item: T): Double { return 1.0 }

    operator fun invoke(tokens: List<String>, space: List<T>): List<RankableResult<T>> {
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