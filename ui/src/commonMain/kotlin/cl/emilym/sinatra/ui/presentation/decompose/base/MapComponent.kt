package cl.emilym.sinatra.ui.presentation.decompose.base

import cl.emilym.sinatra.ui.maps.MapItem
import kotlinx.coroutines.flow.Flow

interface MapComponent: SinatraComponent {

    val mapItems: Flow<List<MapItem>>

}