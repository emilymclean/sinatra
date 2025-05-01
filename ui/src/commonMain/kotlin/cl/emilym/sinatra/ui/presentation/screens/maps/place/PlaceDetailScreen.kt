package cl.emilym.sinatra.ui.presentation.screens.maps.place

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.PlaceRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.nullIfEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class PlaceDetailViewModel(
    private val placeRepository: PlaceRepository,
    override val favouriteRepository: FavouriteRepository,
    override val recentVisitRepository: RecentVisitRepository,
    override val nearbyStopsUseCase: NearbyStopsUseCase
): AbstractPlaceViewModel() {

    override val placeId = MutableStateFlow<PlaceId?>(null)

    override val _place = placeId.requestStateFlow {
        it?.let { placeRepository.get(it).item }
    }

    fun init(placeId: PlaceId) {
        this.placeId.value = placeId
        screenModelScope.launch {
            recentVisitRepository.addPlaceVisit(placeId)
        }
    }

    override fun retryPlace() {
        screenModelScope.launch {
            _place.retry()
        }
    }
}

class PlaceDetailScreen(
    val placeId: PlaceId
): AbstractPlaceScreen<PlaceDetailViewModel>() {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$placeId"

    @Composable
    override fun viewModel() = koinScreenModel<PlaceDetailViewModel>()
    
    override fun init(viewModel: PlaceDetailViewModel) {
        viewModel.init(placeId)
    }
}