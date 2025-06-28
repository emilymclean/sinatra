package cl.emilym.sinatra.ui.presentation.screens.maps.place

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.PlaceRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.placeJourneyNavigation
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.widgets.NavigateIcon
import cl.emilym.sinatra.ui.widgets.defaultConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.place_detail_navigate

@Factory
class PlaceDetailViewModel(
    private val placeRepository: PlaceRepository,
    override val favouriteRepository: FavouriteRepository,
    override val recentVisitRepository: RecentVisitRepository,
    override val nearbyStopsUseCase: NearbyStopsUseCase
): AbstractPlaceViewModel() {

    override val placeId = MutableStateFlow<PlaceId?>(null)

    override val _place = placeId.requestStateFlow(defaultConfig) {
        it?.let { placeRepository.get(it).item }
    }

    override val location: StateFlow<MapLocation?> = place.mapLatest {
        it.unwrap()?.location
    }.state(null)

    fun init(placeId: PlaceId) {
        this.placeId.value = placeId
        screenModelScope.launch {
            recentVisitRepository.addPlaceVisit(placeId)
        }
    }

    override fun retryPlace() {
        screenModelScope.launch {
            _place.retryIfNeeded(place.value)
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

    override fun LazyListScope.CallToAction(place: Place, navigator: Navigator) {
        item {
            Button(
                onClick = { navigator.placeJourneyNavigation(place) },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 1.rdp)
            ) {
                NavigateIcon()
                Box(Modifier.width(0.5.rdp))
                Text(stringResource(Res.string.place_detail_navigate))
            }
        }
    }
}