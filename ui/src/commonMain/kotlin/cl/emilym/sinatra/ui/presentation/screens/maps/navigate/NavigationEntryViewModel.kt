package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.domain.CalculateJourneyUseCase
import cl.emilym.sinatra.ui.widgets.createRequestStateFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class NavigationEntryViewModel(
    private val calculateJourneyUseCase: CalculateJourneyUseCase,
    private val networkGraphRepository: NetworkGraphRepository
): ViewModel() {

    private var loadedGraph: Boolean = false
        set(value) {
            field = value
            if (value) calculate()
        }
    private var calculationJob: Job? = null

    private var currentLocation: MapLocation? = null

    private var destinationCurrentLocation = false
    private var destination: MapLocation? = null
        set(value) {
            field = value
            if (value != null) calculate()
        }
    private var originCurrentLocation = false
    private var origin: MapLocation? = null
        set(value) {
            field = value
            if (value != null) calculate()
        }

    val state = MutableStateFlow<NavigationState>(NavigationState.GraphLoading)

    fun init(destination: NavigationLocation, origin: NavigationLocation) {
        retryLoadingGraph()
        setDestination(destination)
        setOrigin(origin)
    }

    fun retryLoadingGraph() {
        viewModelScope.launch {
            state.value = NavigationState.GraphLoading
            try {
                withContext(Dispatchers.IO) {
                    networkGraphRepository.networkGraph()
                }
                state.value = NavigationState.GraphReady
                loadedGraph = true
            } catch (e: Exception) {
                state.value = NavigationState.GraphFailed(e)
            }
        }
    }

    fun setDestination(navigationLocation: NavigationLocation) {
        unpackLocation(
            navigationLocation
        ) { location, current ->
            destination = location
            destinationCurrentLocation = current
        }
    }

    fun setOrigin(navigationLocation: NavigationLocation) {
        unpackLocation(
            navigationLocation
        ) { location, current ->
            origin = location
            originCurrentLocation = current
        }
    }

    fun updateCurrentLocation(location: MapLocation) {
        val currentLocation = currentLocation
        if (currentLocation != null && distance(location, currentLocation) < 1.0) return
        this.currentLocation = location
        if (originCurrentLocation) origin = location
        if (destinationCurrentLocation) destination = location
    }

    private fun unpackLocation(
        navigationLocation: NavigationLocation,
        save: (MapLocation?, Boolean) -> Unit
    ) {
        when (navigationLocation) {
            is NavigationLocation.LocatableNavigationLocation -> {
                save(navigationLocation.location, false)
            }
            is NavigationLocation.CurrentLocation -> {
                save(currentLocation, true)
            }
        }
    }

    private fun calculate() {
        calculationJob?.cancel()
        if (!loadedGraph) return
        val destination = destination ?: return
        val origin = origin ?: return

        viewModelScope.launch {
            state.value = NavigationState.JourneyCalculating
            try {
                state.value = NavigationState.JourneyFound(
                    calculateJourneyUseCase(origin, destination).also {
                        Napier.d("Journey = $it")
                    }
                )
            } catch (e: Exception) {
                state.value = NavigationState.JourneyFailed(e)
            }
        }
    }

}