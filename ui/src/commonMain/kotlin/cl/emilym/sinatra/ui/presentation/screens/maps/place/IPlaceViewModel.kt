package cl.emilym.sinatra.ui.presentation.screens.maps.place

import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import kotlinx.coroutines.flow.StateFlow

interface IPlaceViewModel : SinatraScreenModel {
    val favourited: StateFlow<Boolean>
    val place: StateFlow<RequestState<Place?>>
    val nearbyStops: StateFlow<RequestState<List<StopWithDistance>?>>

    fun favourite(favourited: Boolean)
    fun retryPlace()
    fun retryNearby()
}