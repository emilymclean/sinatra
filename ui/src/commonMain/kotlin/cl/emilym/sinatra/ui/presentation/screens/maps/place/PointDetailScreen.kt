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
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Zoom
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.PlaceRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.canberraRegion
import cl.emilym.sinatra.ui.localization.format
import cl.emilym.sinatra.ui.placeJourneyNavigation
import cl.emilym.sinatra.ui.pointJourneyNavigation
import cl.emilym.sinatra.ui.widgets.NavigateIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.place_detail_navigate
import sinatra.ui.generated.resources.point_detail_navigate

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
        it?.let {
            placeRepository.reverse(it.mapLocation, it.zoom) ?: Place(
                "",
                null,
                "${it.mapLocation.lat.format(3)}, ${it.mapLocation.lng.format(3)}",
                it.mapLocation
            )
        }
    }.onEach {
        it.unwrap()?.let {
            recentVisitRepository.addPlaceVisit(it.id)
        }
    }

    override val placeId = place.map { it.unwrap()?.id?.nullIfEmpty() }.state(null)

    override val location: StateFlow<MapLocation?> = point.mapLatest { it?.mapLocation }.state(null)

    override val outsideServiceArea = MutableStateFlow(false)

    fun init(point: MapLocation, zoom: Zoom?) {
        val isOutsideServiceArea = !canberraRegion.contains(point)
        outsideServiceArea.value = isOutsideServiceArea
        if (isOutsideServiceArea) return

        this.point.value = MapLocationAndZoom(
            point, zoom
        )
    }

    override fun retryPlace() {
        screenModelScope.launch {
            _place.retry()
        }
    }
}

class PointDetailScreen(
    val point: MapLocation,
    val zoom: Zoom? = null
): AbstractPlaceScreen<PointDetailViewModel>() {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$point"

    @Composable
    override fun viewModel() = koinScreenModel<PointDetailViewModel>()

    override fun init(viewModel: PointDetailViewModel) {
        viewModel.init(point, zoom)
    }

    override fun LazyListScope.CallToAction(place: Place, navigator: Navigator) {
        item {
            Button(
                onClick = { when (place.id) {
                    "" -> navigator.pointJourneyNavigation(place.location)
                    else -> navigator.placeJourneyNavigation(place)
                } },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 1.rdp)
            ) {
                NavigateIcon()
                Box(Modifier.width(0.5.rdp))
                Text(
                    when (place.id) {
                        "" -> stringResource(Res.string.point_detail_navigate)
                        else -> stringResource(Res.string.place_detail_navigate)
                    }
                )
            }
        }
    }
}