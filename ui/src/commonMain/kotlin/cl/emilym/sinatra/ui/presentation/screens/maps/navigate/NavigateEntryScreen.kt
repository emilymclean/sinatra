package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.compose.errorwidget.ErrorWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreen
import cl.emilym.sinatra.ui.presentation.theme.Container
import cl.emilym.sinatra.ui.widgets.GenericMarkerIcon
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigatorBackButton
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.WalkIcon
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.format
import cl.emilym.sinatra.ui.widgets.routeRandleSize
import com.mikepenz.markdown.m3.Markdown
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigate_calculating_journey
import sinatra.ui.generated.resources.navigate_calculating_journey_failed
import sinatra.ui.generated.resources.navigate_downloading_graph
import sinatra.ui.generated.resources.navigate_travel
import sinatra.ui.generated.resources.navigate_travel_arrive
import sinatra.ui.generated.resources.navigate_travel_depart
import sinatra.ui.generated.resources.navigate_walk


class NavigateEntryScreen(
    val destination: NavigationLocation,
    val origin: NavigationLocation = NavigationLocation.CurrentLocation
): Screen {

    private val journeyIconInset
        @Composable
        get() = 2.rdp + routeRandleSize

    private val iconInset
        @Composable
        get() = 2.rdp + 24.dp

    override val key: ScreenKey = "navigateEntryScreen-${destination.screenKey}-${origin.screenKey}"

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<NavigationEntryViewModel>()
        val state by viewModel.state.collectAsState(null)
        val currentLocation = currentLocation()

        LaunchedEffect(Unit) {
            viewModel.init(destination, origin)
        }

        LaunchedEffect(currentLocation) {
            if (currentLocation == null) return@LaunchedEffect
            viewModel.updateCurrentLocation(currentLocation)
        }

        Scaffold { innerPadding ->
            when (val state = state) {
                is NavigationEntryState.Journey -> JourneyState(innerPadding, viewModel, state.state)
                is NavigationEntryState.Search -> SearchState(innerPadding, viewModel)
                null -> {}
            }
        }
    }

    @Composable
    fun SearchState(
        innerPadding: PaddingValues,
        viewModel: NavigationEntryViewModel,
    ) {
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            SearchScreen(
                viewModel,
                false,
                { viewModel.openJourney() },
                { viewModel.onSearchItemClicked(NavigationLocation.Stop(it)) },
                {},
                { viewModel.onSearchItemClicked(NavigationLocation.Place(it)) }
            ) {}
        }
    }

    @Composable
    fun JourneyState(
        innerPadding: PaddingValues,
        viewModel: NavigationEntryViewModel,
        navigationState: NavigationState
    ) {
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                Modifier.padding(1.rdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigatorBackButton()
                Column(
                    Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, shape = MaterialTheme.shapes.large)
                        .clip(MaterialTheme.shapes.large)
                        .background(Container)
                ) {
                    val origin by viewModel.origin.collectAsState()
                    val destination by viewModel.destination.collectAsState()
                    origin?.let { origin ->
                        NavigationLocationDisplay(
                            origin,
                            false,
                            modifier = Modifier.clickable { viewModel.onOriginClick() }
                        )
                    }
                    HorizontalDivider(Modifier.padding(start = iconInset))
                    destination?.let { destination ->
                        NavigationLocationDisplay(
                            destination,
                            true,
                            modifier = Modifier.clickable { viewModel.onDestinationClick() }
                        )
                    }
                }
            }

            navigationState.let { state ->
                when (state) {
                    is NavigationState.GraphLoading, NavigationState.JourneyCalculating -> {
                        Box(
                            Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                Modifier.padding(1.rdp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(1.rdp)
                            ) {
                                CircularProgressIndicator()
                                Text(stringResource(
                                    if (state == NavigationState.GraphLoading) {
                                        Res.string.navigate_downloading_graph
                                    } else {
                                        Res.string.navigate_calculating_journey
                                    }
                                ))
                            }
                        }
                    }
                    is NavigationState.GraphFailed -> {
                        Box(
                            Modifier.padding(1.rdp).weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorWidget(
                                if (state is NavigationState.GraphFailed) state.exception else
                                    (state as? NavigationState.JourneyFailed)?.exception,
                                retry = { viewModel.retryLoadingGraph() }
                            )
                        }
                    }
                    is NavigationState.JourneyFailed -> {
                        Box(
                            Modifier.padding(1.rdp).weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ListHint(
                                stringResource(Res.string.navigate_calculating_journey_failed),
                                icon = { MapIcon(tint = MaterialTheme.colorScheme.primary) }
                            )
                        }
                    }
                    is NavigationState.GraphReady -> {}
                    is NavigationState.JourneyFound -> {
                        DisplayJourney(state.journey)
                    }
                    else -> {}
                }
            }
        }
    }

    @Composable
    fun DisplayJourney(journey: Journey) {
        if (journey.legs.isEmpty()) return
        val lastLeg = journey.legs.last()
        val viewModel = koinViewModel<NavigationEntryViewModel>()
        val destination by viewModel.destination.collectAsState()

        Column(Modifier.fillMaxWidth()) {
            for (legI in journey.legs.indices) {
                val leg = journey.legs[legI]
                when (leg) {
                    is JourneyLeg.Transfer -> TransferLeg(leg)
                    is JourneyLeg.Travel -> TravelLeg(leg)
                }
                if (legI < journey.legs.size - 1)
                    HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
            }

            if (lastLeg is JourneyLeg.Transfer || destination !is NavigationLocation.Stop) {
                HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                ArrivalStopLeg(lastLeg.stops.last(), lastLeg.arrivalTime)
            }
        }
    }

}

@Composable
fun LegScaffold(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        Modifier.padding(horizontal = 1.rdp, vertical = 0.75.rdp).fillMaxWidth().then(modifier),
        horizontalArrangement = Arrangement.spacedBy(1.rdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(routeRandleSize), contentAlignment = Alignment.Center) {
            icon()
        }
        content()
    }
}

@Composable
fun TransferLeg(leg: JourneyLeg.Transfer) {
    LegScaffold({ WalkIcon() }) {
        Markdown(stringResource(Res.string.navigate_walk, leg.travelTime.inWholeMinutes))
    }
}

@Composable
fun TravelLeg(leg: JourneyLeg.Travel) {
    LegScaffold({ RouteRandle(leg.route) }) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.75.rdp)
        ) {
            Markdown(stringResource(Res.string.navigate_travel_depart, leg.stops.first().name, leg.departureTime.format()))
            Markdown(stringResource(Res.string.navigate_travel, leg.travelTime.inWholeMinutes, leg.route.name, leg.heading))
            Markdown(stringResource(Res.string.navigate_travel_arrive, leg.stops.last().name, leg.arrivalTime.format()))
        }
    }
}

@Composable
fun ArrivalStopLeg(stop: Stop, arrivalTime: Time) {
    LegScaffold({ GenericMarkerIcon() }) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.75.rdp)
        ) {
            Markdown(stringResource(Res.string.navigate_travel_arrive, stop.name, arrivalTime.format()))
        }
    }
}

@Composable
fun NavigationLocationDisplay(
    location: NavigationLocation,
    isDestination: Boolean,
    modifier: Modifier = Modifier
) {
    LegScaffold(
        {
            when (isDestination) {
                true -> GenericMarkerIcon()
                false -> StarOutlineIcon() // TODO change
            }
        },
        modifier = modifier
    ) {
        Text(location.name)
    }
}
