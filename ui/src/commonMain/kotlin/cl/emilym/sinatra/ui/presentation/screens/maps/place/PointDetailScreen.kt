package cl.emilym.sinatra.ui.presentation.screens.maps.place

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.PlaceRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class PointDetailViewModel(
    private val placeRepository: PlaceRepository,
    override val favouriteRepository: FavouriteRepository,
    override val recentVisitRepository: RecentVisitRepository,
    override val nearbyStopsUseCase: NearbyStopsUseCase
): AbstractPlaceViewModel() {

    private val point = MutableStateFlow<MapLocation?>(null)

    override val _place = point.requestStateFlow {
        it?.let { placeRepository.reverse(it) }
    }
    override val placeId = _place.map { it.unwrap()?.id }.state(null)

    fun init(point: MapLocation) {
        this.point.value = point
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
    val point: MapLocation
): AbstractPlaceScreen<PointDetailViewModel>() {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$point"

    @Composable
    override fun viewModel() = koinScreenModel<PointDetailViewModel>()

    override fun init(viewModel: PointDetailViewModel) {
        viewModel.init(point)
    }
}