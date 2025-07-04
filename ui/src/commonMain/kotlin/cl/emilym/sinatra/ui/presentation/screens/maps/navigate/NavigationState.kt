package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import cl.emilym.sinatra.data.models.Journey

sealed interface NavigationState {

    data object GraphLoading: NavigationState
    data object GraphReady: NavigationState
    data class GraphFailed(
        val exception: Exception
    ): NavigationState

    data object JourneyCalculating: NavigationState
    data object JourneyStartStopSame: NavigationState
    data class JourneysFound(
        val journeys: List<Journey>
    ): NavigationState
    data class JourneyFailed(
        val exception: Exception
    ): NavigationState

}