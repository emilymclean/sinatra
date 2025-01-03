package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import org.koin.core.annotation.Factory

@Factory
class StopTypeSearcher(
    private val stopRepository: StopRepository
): LocalTypeSearcher<Stop>() {

    override fun fields(t: Stop) = listOf(t.id, t.name)

    override fun scoreMultiplier(item: Stop): Double {
        return when {
            item.parentStation != null -> 0.75
            else -> 1.0
        }
    }

    override suspend fun load(): List<Stop> {
        return stopRepository.stops().item
    }

    override fun wrap(item: Stop): SearchResult {
        return SearchResult.StopResult(item)
    }

}