package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
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
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cl.emilym.compose.errorwidget.ErrorWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.bounds
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.routeStopMarkerIcon
import cl.emilym.sinatra.ui.maps.walkingMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.zoomPadding
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreen
import cl.emilym.sinatra.ui.presentation.theme.Container
import cl.emilym.sinatra.ui.presentation.theme.walkingColor
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.localization.format
import cl.emilym.sinatra.ui.widgets.CurrentLocationCard
import cl.emilym.sinatra.ui.widgets.GenericMarkerIcon
import cl.emilym.sinatra.ui.widgets.JourneyStartIcon
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigatorBackButton
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.WalkIcon
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.hasLocationPermission
import cl.emilym.sinatra.ui.widgets.routeRandleSize
import com.mikepenz.markdown.m3.Markdown
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigate_calculating_journey
import sinatra.ui.generated.resources.navigate_calculating_journey_failed
import sinatra.ui.generated.resources.navigate_downloading_graph
import sinatra.ui.generated.resources.navigate_entry_select_destination
import sinatra.ui.generated.resources.navigate_entry_select_origin
import sinatra.ui.generated.resources.navigate_travel
import sinatra.ui.generated.resources.navigate_travel_arrive
import sinatra.ui.generated.resources.navigate_travel_depart
import sinatra.ui.generated.resources.navigate_travel_journey_arrive
import sinatra.ui.generated.resources.navigate_travel_journey_depart
import sinatra.ui.generated.resources.navigate_walk


class NavigateEntryScreen(
    val destination: NavigationLocation,
    val origin: NavigationLocation? = null
): MapScreen {

    private val journeyIconInset
        @Composable
        get() = 2.rdp + routeRandleSize

    private val iconInset
        @Composable
        get() = 2.rdp + 24.dp

    override val key: ScreenKey = "navigateEntryScreen-${destination.screenKey}-${origin?.screenKey}"

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<NavigationEntryViewModel>()
        val state by viewModel.state.collectAsState(null)
        val journey = ((state as? NavigationEntryState.Journey)?.state as? NavigationState.JourneyFound)?.journey ?: return emptyList()
        val originLocation by viewModel.originLocation.collectAsState()
        val destinationLocation by viewModel.destinationLocation.collectAsState()

        val items = mutableListOf<MapItem>()

        @Composable
        fun addWalking(points: List<MapLocation>) {
            items.add(
                LineItem(
                    points,
                    walkingColor
                )
            )
            items.addAll(points.drop(1).dropLast(1).map {
                MarkerItem(
                    it,
                    walkingMarkerIcon(),
                    id = "routeLeg-walking-${it}"
                )
            })
        }

        for (i in journey.legs.indices) {
            val leg = journey.legs[i]

            when (leg) {
                is JourneyLeg.Travel -> {
                    items.add(
                        LineItem(
                            leg.stops.map { it.location },
                            leg.route.color()
                        )
                    )
                    items.addAll(leg.stops.map {
                        MarkerItem(
                            it.location,
                            routeStopMarkerIcon(leg.route),
                            id = "routeLeg-stop-${it.id}"
                        )
                    })
                }
                is JourneyLeg.Transfer -> {
                    when (i) {
                        0 -> addWalking(listOf(leg.stops.first().location) + leg.stops.map { it.location })
                        journey.legs.lastIndex -> addWalking(leg.stops.map { it.location } + listOf(leg.stops.last().location))
                        else -> addWalking(leg.stops.map { it.location })
                    }
                }
                is JourneyLeg.TransferPoint -> {
                    when (i) {
                        0 -> originLocation?.let {
                            val next = (journey.legs.getOrNull(1) as? JourneyLeg.RouteJourneyLeg) ?: return@let
                            addWalking(listOf(it, it, next.stops.first().location))
                        }
                        journey.legs.lastIndex -> destinationLocation?.let {
                            val next = (journey.legs.getOrNull(journey.legs.lastIndex - 1) as? JourneyLeg.RouteJourneyLeg) ?: return@let
                            addWalking(listOf(next.stops.last().location, it, it))
                        }
                    }
                }
                else -> {}
            }
        }

        return items
    }

    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<NavigationEntryViewModel>()
        val state by viewModel.state.collectAsState(null)
        val currentLocation = currentLocation()

        val hasLocationPermission = hasLocationPermission()
        LaunchedEffect(destination, origin) {
            viewModel.init(
                destination,
                origin ?: (
                        if (hasLocationPermission)
                            NavigationLocation.CurrentLocation
                        else NavigationLocation.None
                )
            )
        }

        LaunchedEffect(currentLocation) {
            if (currentLocation == null) return@LaunchedEffect
            viewModel.updateCurrentLocation(currentLocation)
        }

        when (val state = state) {
            is NavigationEntryState.Journey -> JourneyState(viewModel, state.state)
            is NavigationEntryState.Search -> SearchState(viewModel)
            null -> {}
        }

        val bottomSheet = LocalBottomSheetState.current
        val mapControl = LocalMapControl.current
        val originLocation by viewModel.originLocation.collectAsState()
        val destinationLocation by viewModel.destinationLocation.collectAsState()
        val zoomPadding = zoomPadding

        LaunchedEffect(state) {
            when (val state = state) {
                is NavigationEntryState.Journey -> when (state.state) {
                    is NavigationState.JourneyFound -> {
                        bottomSheet?.bottomSheetState?.halfExpand()
                        mapControl.zoomToArea(
                            (state.state.journey.legs
                                .filterIsInstance<JourneyLeg.RouteJourneyLeg>()
                                .flatMap { it.stops.map { it.location } } +
                                    listOfNotNull(originLocation, destinationLocation)
                            ).bounds(),
                            zoomPadding
                        )
                    }
                    else -> bottomSheet?.bottomSheetState?.expand()
                }
                is NavigationEntryState.Search -> bottomSheet?.bottomSheetState?.expand()
                null -> {}
            }
        }
    }

    @Composable
    fun SearchState(
        viewModel: NavigationEntryViewModel,
    ) {
        val hasLocationPermission = hasLocationPermission()
        Box(Modifier.fillMaxSize()) {
            SearchScreen(
                viewModel,
                { viewModel.openJourney() },
                { viewModel.onSearchItemClicked(NavigationLocation.Stop(it)) },
                {},
                { viewModel.onSearchItemClicked(NavigationLocation.Place(it)) }
            ) {
                if (hasLocationPermission) {
                    item {
                        CurrentLocationCard(
                            onClick = { viewModel.onSearchItemClicked(NavigationLocation.CurrentLocation) },
                            showCurrentLocationIcon = true
                        )
                    }
                    item {
                        Box(Modifier.height(1.rdp))
                    }
                }
            }
        }
    }

    @Composable
    fun JourneyState(
        viewModel: NavigationEntryViewModel,
        navigationState: NavigationState
    ) {
        Scaffold { innerPadding ->
            LazyColumn(
                Modifier
                    .fillMaxSize(),
                contentPadding = innerPadding
            ) {
                item {
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
                }

                navigationState.let { state ->
                    when (state) {
                        is NavigationState.GraphLoading, NavigationState.JourneyCalculating -> {
                            item {
                                Box(
                                    Modifier.fillMaxWidth(),
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
                        }
                        is NavigationState.GraphFailed -> {
                            item {
                                Box(
                                    Modifier.padding(1.rdp).fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ErrorWidget(
                                        if (state is NavigationState.GraphFailed) state.exception else
                                            (state as? NavigationState.JourneyFailed)?.exception,
                                        retry = { viewModel.retryLoadingGraph() }
                                    )
                                }
                            }
                        }
                        is NavigationState.JourneyFailed -> {
                            item {
                                Box(
                                    Modifier.padding(1.rdp).fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ListHint(
                                        stringResource(Res.string.navigate_calculating_journey_failed),
                                        icon = { MapIcon(tint = MaterialTheme.colorScheme.primary) }
                                    )
                                }
                            }
                        }
                        is NavigationState.GraphReady -> {}
                        is NavigationState.JourneyFound -> {
                            item {
                                DisplayJourney(state.journey)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    @Composable
    fun DisplayJourney(journey: Journey) {
        if (journey.legs.isEmpty()) return
        val lastLeg = journey.legs.last()
        val firstLeg = journey.legs.first()
        val viewModel = koinScreenModel<NavigationEntryViewModel>()
        val destination by viewModel.destination.collectAsState()
        val origin by viewModel.origin.collectAsState()

        Column(Modifier.fillMaxWidth()) {
            when (firstLeg) {
                is JourneyLeg.Transfer -> {
                    DepartureLeg(firstLeg.stops.first().name)
                    HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                }
                is JourneyLeg.TransferPoint -> {
                    origin?.name?.let {
                        DepartureLeg(it)
                        HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                    }
                }
                else -> {}
            }

            for (legI in journey.legs.indices) {
                val leg = journey.legs[legI]
                when (leg) {
                    is JourneyLeg.Transfer -> TransferLeg(leg)
                    is JourneyLeg.Travel -> TravelLeg(leg)
                    else -> {
                        when (legI) {
                            0 -> TransferLeg(leg)
                            journey.legs.lastIndex -> TransferLeg(leg)
                            else -> {}
                        }
                    }
                }
                if (legI < journey.legs.size - 1)
                    HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
            }

            when (lastLeg) {
                is JourneyLeg.Transfer -> {
                    HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                    ArrivalLeg(lastLeg.stops.last().name, lastLeg.arrivalTime)
                }
                is JourneyLeg.TransferPoint -> {
                    destination?.name?.let {
                        HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                        ArrivalLeg(it, lastLeg.arrivalTime)
                    }
                }
                else -> {}
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
        Modifier.then(modifier).padding(horizontal = 1.rdp, vertical = 0.75.rdp).fillMaxWidth(),
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
fun TransferLeg(leg: JourneyLeg) {
    LegScaffold({ WalkIcon() }) {
        Markdown(stringResource(Res.string.navigate_walk, leg.travelTime.text))
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
            Markdown(stringResource(Res.string.navigate_travel, leg.travelTime.text, leg.route.name, leg.heading))
            Markdown(stringResource(Res.string.navigate_travel_arrive, leg.stops.last().name, leg.arrivalTime.format()))
        }
    }
}

@Composable
fun DepartureLeg(pointName: String) {
    LegScaffold({ GenericMarkerIcon() }) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.75.rdp)
        ) {
            Markdown(stringResource(Res.string.navigate_travel_journey_depart, pointName))
        }
    }
}

@Composable
fun ArrivalLeg(pointName: String, arrivalTime: Time) {
    LegScaffold({ GenericMarkerIcon() }) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.75.rdp)
        ) {
            Markdown(stringResource(Res.string.navigate_travel_journey_arrive, pointName, arrivalTime.format()))
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
                false -> JourneyStartIcon()
            }
        },
        modifier = modifier
    ) {
        Text(
            when {
                location !is NavigationLocation.None -> location.name
                isDestination -> stringResource(Res.string.navigate_entry_select_destination)
                else -> stringResource(Res.string.navigate_entry_select_origin)
            },
            color = when {
                location !is NavigationLocation.None -> LocalContentColor.current
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
