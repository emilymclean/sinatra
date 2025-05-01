package cl.emilym.sinatra.ui.presentation.screens.maps.place

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Zoom
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.PlaceRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private data class MapLocationAndZoom(
    val mapLocation: MapLocation,
    val zoom: Zoom?
)

@Factory
class PointDetailViewModel(
    private val placeRepository: PlaceRepository,
    override val favouriteRepository: FavouriteRepository,
    override val recentVisitRepository: RecentVisitRepository,
    override val nearbyStopsUseCase: NearbyStopsUseCase
): AbstractPlaceViewModel() {

    private val point = MutableStateFlow<MapLocationAndZoom?>(null)

    override val _place = point.requestStateFlow {
        it?.let { placeRepository.reverse(it.mapLocation, it.zoom) }
    }
    override val placeId = _place.map { it.unwrap()?.id }.state(null)

    fun init(point: MapLocation, zoom: Zoom?) {
        this.point.value = MapLocationAndZoom(
            point, zoom
        )
//        screenModelScope.launch {
//            recentVisitRepository.addPlaceVisit(placeId)
//        }
    }

    override fun retryPlace() {
        screenModelScope.launch {
            _place.retry()
        }
    }
}

class PointDetailScreen(
    val point: MapLocation,
    val zoom: Zoom?
): AbstractPlaceScreen<PointDetailViewModel>() {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$point"

    @Composable
    override fun viewModel() = koinScreenModel<PointDetailViewModel>()

    override fun init(viewModel: PointDetailViewModel) {
        viewModel.init(point, zoom)
    }
}