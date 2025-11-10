package cl.emilym.sinatra.component.search

import cl.emilym.sinatra.component.base.SinatraComponent
import cl.emilym.sinatra.component.base.SinatraComponentContext

interface SearchResult {
    val score: Double
}
interface SearchPlaceholder

interface SearchResultProvider {
    suspend fun search(query: String): List<SearchResult>
}

interface PlaceholderProvider {
    suspend fun get(): List<SearchPlaceholder>
}

interface SearchComponent: SinatraComponent {

    fun clearSearchField()

}

internal class DefaultSearchComponent(
    componentContext: SinatraComponentContext
): SearchComponent, SinatraComponentContext by componentContext {

    override fun clearSearchField() {

    }

}
