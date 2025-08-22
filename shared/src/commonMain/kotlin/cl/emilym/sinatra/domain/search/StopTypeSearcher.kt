package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.GetFilteredStopsUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class StopTypeSearcher(
    private val getFilteredStopsUseCase: GetFilteredStopsUseCase
): LocalTypeSearcher<Stop>() {

    override fun fields(t: Stop) = listOf(t.id, t.name)

    override fun scoreMultiplier(item: Stop): Double {
        return when {
            item.visibility.searchWeight != null -> item.visibility.searchWeight
            item.parentStation != null -> 0.75
            else -> 1.0
        }
    }

    override suspend fun load(): List<Stop> {
        return getFilteredStopsUseCase().first().item
    }

    override fun wrap(item: Stop): SearchResult {
        return SearchResult.StopResult(item)
    }

}