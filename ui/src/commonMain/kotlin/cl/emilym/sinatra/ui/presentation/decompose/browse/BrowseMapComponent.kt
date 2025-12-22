package cl.emilym.sinatra.ui.presentation.decompose.browse

import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.presentation.decompose.base.MapComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BrowseMapComponent: MapComponent

class DefaultBrowseMapComponent(
    sinatraComponentContext: SinatraComponentContext
): BrowseMapComponent, SinatraComponentContext by sinatraComponentContext {

    override val mapItems: StateFlow<List<MapItem>> = MutableStateFlow(emptyList())

}