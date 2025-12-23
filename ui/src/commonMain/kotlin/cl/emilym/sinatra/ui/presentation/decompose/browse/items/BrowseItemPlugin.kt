package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import kotlinx.coroutines.flow.StateFlow

sealed interface BrowseItemContent<T: Any> {
    class None<T: Any>: BrowseItemContent<T>
    data class Content<T: Any>(
        val content: T
    ): BrowseItemContent<T>
}

interface BrowseItemPlugin<T: Any>: SinatraComponent {

    val state: StateFlow<BrowseItemContent<T>>

}