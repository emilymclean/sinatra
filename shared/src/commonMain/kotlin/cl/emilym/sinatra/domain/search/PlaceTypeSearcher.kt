package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.persistence.RecentVisitPersistence
import cl.emilym.sinatra.data.repository.PlaceRepository
import org.koin.core.annotation.Factory

@Factory
class PlaceTypeSearcher(
    private val placeRepository: PlaceRepository
): AbstractTypeSearcher<Place>() {

    companion object {

        const val MIN_QUERY_LENGTH = 4

    }

    override suspend fun invoke(tokens: List<String>): List<RankableResult<Place>> {
        if (tokens.sumOf { it.length } < MIN_QUERY_LENGTH) return listOf()
        if (!placeRepository.available()) return listOf()
        val space = placeRepository.search(tokens.joinToString(" "))
        return search(tokens, space)
    }

    override fun fields(t: Place): List<String> {
        return listOf(t.name, t.displayName)
    }

    override fun wrap(item: Place): SearchResult {
        return SearchResult.PlaceResult(item)
    }

}