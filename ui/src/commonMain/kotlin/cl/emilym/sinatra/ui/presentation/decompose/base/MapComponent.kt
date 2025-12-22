package cl.emilym.sinatra.ui.presentation.decompose.base

import cl.emilym.sinatra.ui.maps.MapItem
import kotlinx.coroutines.flow.StateFlow

interface MapComponent: SinatraComponent {

    val mapItems: StateFlow<List<MapItem>>

}