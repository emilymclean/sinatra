package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.Tokenizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory

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
        return routeTypeSearcher(tokens, space.routes).map {
            SearchResult.RouteResult(it)
        } + stopTypeSearcher(tokens, space.stops).map {
            SearchResult.StopResult(it)
        }
    }

    private suspend fun get(): SearchSpace {
        searchLock.withLock {
            searchSpace.also { if (it != null) return it }
            val routes = routeRepository.routes()
            val stops = stopRepository.stops()
            return SearchSpace(
                routes.item, stops.item
            ).also { searchSpace = it }
        }
    }

}

abstract class TypeSearcher<T> {

    protected abstract fun fields(t: T): List<String>
    operator fun invoke(tokens: List<String>, space: List<T>): List<T> {
        return space.filter {
            val match = fields(it).map { it.lowercase() }
            tokens.all { t ->
                match.any { m -> t in m  }
            }
        }
    }

}