package cl.emilym.sinatra.ui.presentation.screens.maps.place

import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class AbstractPlaceViewModel : SinatraScreenModel {
    protected abstract val favouriteRepository: FavouriteRepository
    protected abstract val recentVisitRepository: RecentVisitRepository
    protected abstract val nearbyStopsUseCase: NearbyStopsUseCase

    protected abstract val placeId: StateFlow<PlaceId?>

    protected abstract val _place: Flow<RequestState<Place?>>
    val place: StateFlow<RequestState<Place?>> by lazy { _place.stateIn(screenModelScope, SharingStarted.Lazily, RequestState.Initial()) }

    open val outsideServiceArea: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val favourited: StateFlow<Boolean?> by lazy { placeId.flatMapLatest {
        it?.let { favouriteRepository.placeIsFavourited(it) } ?: flowOf(false)
    }.state(null) }

    private val _nearbyStops by lazy { place.requestStateFlow {
        it.unwrap()?.let {
            nearbyStopsUseCase(it.location, limit = 25)
        }
    } }
    val nearbyStops: StateFlow<RequestState<List<StopWithDistance>?>> by lazy { _nearbyStops
        .state(RequestState.Initial()) }
    val noNearbyStops: StateFlow<Boolean> by lazy {
        nearbyStops.mapLatest {
            it.unwrap()?.size == 0
        }.state(false)
    }

    fun favourite(favourited: Boolean) {
        val placeId = this.placeId.value ?: return
        screenModelScope.launch {
            favouriteRepository.setPlaceFavourite(placeId, favourited)
        }
    }

    abstract fun retryPlace()

    fun retryNearby() {
        screenModelScope.launch {
            _nearbyStops.retry()
        }
    }
}