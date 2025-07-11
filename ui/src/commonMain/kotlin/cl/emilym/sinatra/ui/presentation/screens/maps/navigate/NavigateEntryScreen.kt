package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.errorwidget.ErrorWidget
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.bounds
import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.localization.format
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.routeStopMarkerIcon
import cl.emilym.sinatra.ui.maps.walkingMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.placeCardDefaultNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.route.zoomPadding
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreen
import cl.emilym.sinatra.ui.presentation.theme.Container
import cl.emilym.sinatra.ui.presentation.theme.walkingColor
import cl.emilym.sinatra.ui.routeCardDefaultNavigation
import cl.emilym.sinatra.ui.stopCardDefaultNavigation
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.AccessibleIcon
import cl.emilym.sinatra.ui.widgets.BackButton
import cl.emilym.sinatra.ui.widgets.BikeIcon
import cl.emilym.sinatra.ui.widgets.Chip
import cl.emilym.sinatra.ui.widgets.ClockIcon
import cl.emilym.sinatra.ui.widgets.CurrentLocationCard
import cl.emilym.sinatra.ui.widgets.FavouriteCard
import cl.emilym.sinatra.ui.widgets.GenericMarkerIcon
import cl.emilym.sinatra.ui.widgets.JourneyIcon
import cl.emilym.sinatra.ui.widgets.JourneyOptionCard
import cl.emilym.sinatra.ui.widgets.JourneyStartIcon
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.SwapIcon
import cl.emilym.sinatra.ui.widgets.WalkIcon
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.hasLocationPermission
import cl.emilym.sinatra.ui.widgets.noRippleClickable
import cl.emilym.sinatra.ui.widgets.routeRandleSize
import cl.emilym.sinatra.ui.widgets.value
import com.mikepenz.markdown.m3.Markdown
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.map_search_favourites
import sinatra.ui.generated.resources.navigate_calculating_journey
import sinatra.ui.generated.resources.navigate_calculating_journey_failed
import sinatra.ui.generated.resources.navigate_chip_anchor_arrive
import sinatra.ui.generated.resources.navigate_chip_anchor_depart
import sinatra.ui.generated.resources.navigate_chip_anchor_now
import sinatra.ui.generated.resources.navigate_chip_bikes_allowed
import sinatra.ui.generated.resources.navigate_chip_wheelchair_accessible
import sinatra.ui.generated.resources.navigate_downloading_graph
import sinatra.ui.generated.resources.navigate_entry_no_destination
import sinatra.ui.generated.resources.navigate_entry_no_starting_point
import sinatra.ui.generated.resources.navigate_entry_no_starting_point_destination
import sinatra.ui.generated.resources.navigate_entry_select_destination
import sinatra.ui.generated.resources.navigate_entry_select_origin
import sinatra.ui.generated.resources.navigate_entry_swap_origin_destination
import sinatra.ui.generated.resources.navigate_start_stop_same
import sinatra.ui.generated.resources.navigate_travel
import sinatra.ui.generated.resources.navigate_travel_arrive
import sinatra.ui.generated.resources.navigate_travel_depart
import sinatra.ui.generated.resources.navigate_travel_journey_arrive
import sinatra.ui.generated.resources.navigate_travel_journey_depart
import sinatra.ui.generated.resources.navigate_walk
import sinatra.ui.generated.resources.route_accessibility_bikes_allowed
import sinatra.ui.generated.resources.route_accessibility_wheelchair_accessible


class NavigateEntryScreen(
    val destination: NavigationLocation? = null,
    val origin: NavigationLocation? = null
): MapScreen {

    private val journeyIconInset
        @Composable
        get() = 2.rdp + routeRandleSize

    private val iconInset
        @Composable
        get() = 2.rdp + 24.dp

    override val key: ScreenKey = "navigateEntryScreen-${destination?.screenKey}-${origin?.screenKey}"

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<NavigationEntryViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val state by viewModel.state.collectAsStateWithLifecycle()
        val journey = (state as? NavigationEntryState.JourneySelected)?.journey ?: return emptyList()
        val originLocation by viewModel.originLocation.collectAsStateWithLifecycle()
        val destinationLocation by viewModel.destinationLocation.collectAsStateWithLifecycle()

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
                            id = "routeLeg-stop-${it.id}",
                            onClick = {
                                navigator.stopCardDefaultNavigation(it)
                            }
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
                    when {
                        journey.legs.size == 1 -> {
                            originLocation?.let { o ->
                                destinationLocation?.let { d ->
                                    addWalking(listOf(o.location, o.location, d.location, d.location))
                                }
                            }
                        }
                        i == 0 -> originLocation?.let {
                            val next = (journey.legs.getOrNull(1) as? JourneyLeg.RouteJourneyLeg) ?: return@let
                            addWalking(listOf(it.location, it.location, next.stops.first().location))
                        }
                        i == journey.legs.lastIndex -> destinationLocation?.let {
                            val next = (journey.legs.getOrNull(journey.legs.lastIndex - 1) as? JourneyLeg.RouteJourneyLeg) ?: return@let
                            addWalking(listOf(next.stops.last().location, it.location, it.location))
                        }
                    }
                }
                else -> {}
            }
        }

        return items
    }

    @Composable
    override fun Content() {
        SelectTimeDialog()
    }

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<NavigationEntryViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val currentLocation = currentLocation()
        val navigator = LocalNavigator.currentOrThrow

        val hasLocationPermission = hasLocationPermission()
        LifecycleEffectOnce {
            viewModel.init(
                destination ?: NavigationLocation.None,
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

        LaunchedEffect(navigator.size) {
            viewModel.backStackSize.value = navigator.size
        }

        when (val state = state) {
            is NavigationEntryState.JourneySelected,
            is NavigationEntryState.JourneySelection,
            is NavigationEntryState.MissingWaypoints ->
                JourneyState(viewModel, state)
            is NavigationEntryState.Search -> SearchState(viewModel)
        }

        val showBackButton by viewModel.showBackButton.collectAsStateWithLifecycle()
        SinatraBackHandler(showBackButton) {
            if (viewModel.back()) {
                navigator.pop()
            }
        }

        val bottomSheet = LocalBottomSheetState.current
        val mapControl = LocalMapControl.current
        val originLocation by viewModel.originLocation.collectAsStateWithLifecycle()
        val destinationLocation by viewModel.destinationLocation.collectAsStateWithLifecycle()
        val zoomPadding = zoomPadding

        LaunchedEffect(state) {
            when (val state = state) {
                is NavigationEntryState.JourneySelected -> {
                    bottomSheet?.bottomSheetState?.halfExpand()
                    mapControl.zoomToArea(
                        (state.journey.legs
                            .filterIsInstance<JourneyLeg.RouteJourneyLeg>()
                            .flatMap { it.stops.map { it.location } } +
                                listOfNotNull(originLocation?.location, destinationLocation?.location)
                        ).bounds(),
                        zoomPadding
                    )
                }
                is NavigationEntryState.Search,
                is NavigationEntryState.JourneySelection,
                is NavigationEntryState.MissingWaypoints -> bottomSheet?.bottomSheetState?.expand()
            }
        }
    }

    @Composable
    fun SearchState(
        viewModel: NavigationEntryViewModel,
    ) {
        val hasLocationPermission = hasLocationPermission()
        val favourites by viewModel.favourites.collectAsStateWithLifecycle()
        Box(Modifier.fillMaxSize()) {
            SearchScreen(
                viewModel,
                listOf(),
                { viewModel.openJourneyCalculation() },
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
                favourites.unwrap()?.let { favourites ->
                    if (!FeatureFlag.NAVIGATE_ENTRY_SCREEN_FAVOURITE_SEARCH.immediate) return@let
                    item {
                        Subheading(stringResource(Res.string.map_search_favourites))
                    }
                    items(favourites) {
                        FavouriteCard(
                            it,
                            onClick = {
                                it.let {
                                    viewModel.onSearchItemClicked(
                                        when (it) {
                                            is Favourite.Stop -> NavigationLocation.Stop(it.stop)
                                            is Favourite.Place -> NavigationLocation.Place(it.place)
                                            is Favourite.StopOnRoute -> NavigationLocation.Stop(it.stop)
                                            else -> return@let
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
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
        state: NavigationEntryState
    ) {
        val journeyCount by viewModel.journeyCount.collectAsStateWithLifecycle()
        Scaffold { innerPadding ->
            LazyColumn(
                Modifier
                    .fillMaxSize(),
                contentPadding = innerPadding
            ) {
                item {
                    val navigator = LocalNavigator.currentOrThrow
                    Row(
                        Modifier.padding(1.rdp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val showBackButton by viewModel.showBackButton.collectAsStateWithLifecycle()
                        if (showBackButton) {
                            BackButton {
                                if (viewModel.back()) {
                                    navigator.pop()
                                }
                            }
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, shape = MaterialTheme.shapes.large)
                                .clip(MaterialTheme.shapes.large)
                                .background(Container),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                Modifier.weight(1f)
                            ) {
                                val origin by viewModel.origin.collectAsStateWithLifecycle()
                                val destination by viewModel.destination.collectAsStateWithLifecycle()
                                NavigationLocationDisplay(
                                    origin,
                                    false,
                                    modifier = Modifier.clickable { viewModel.onOriginClick() }
                                )
                                HorizontalDivider(Modifier.padding(start = iconInset))
                                NavigationLocationDisplay(
                                    destination,
                                    true,
                                    modifier = Modifier.clickable { viewModel.onDestinationClick() }
                                )
                            }
                            if (FeatureFlag.RAPTOR_SWAP_BUTTON.value()) {
                                IconButton(
                                    onClick = { viewModel.swapOriginAndDestination() }
                                ) {
                                    SwapIcon(contentDescription = stringResource(
                                        Res.string.navigate_entry_swap_origin_destination
                                    ))
                                }
                            }
                        }
                    }
                }

                if (state !is NavigationEntryState.JourneySelected || journeyCount == 1) {
                    item {
                        val showAccessibilityButtons = !FeatureFlag.GLOBAL_HIDE_TRANSPORT_ACCESSIBILITY.value()
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 1.rdp),
                            horizontalArrangement = Arrangement.spacedBy(0.5.rdp),
                            modifier = Modifier.padding(bottom = 0.5.rdp)
                        ) {
                            item {
                                val anchorTime by viewModel.anchorTime.collectAsStateWithLifecycle()
                                Chip(
                                    selected = false,
                                    onToggle = {
                                        viewModel.timeDialogVisible.value = true
                                    },
                                    contentDescription = null
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(0.25.rdp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ClockIcon()
                                        anchorTime.let { anchorTime ->
                                            Text(
                                                when (anchorTime) {
                                                    is NavigationAnchorTime.Now ->
                                                        stringResource(Res.string.navigate_chip_anchor_now)
                                                    is NavigationAnchorTime.DepartureTime ->
                                                        stringResource(
                                                            Res.string.navigate_chip_anchor_depart,
                                                            anchorTime.time.format()
                                                        )
                                                    is NavigationAnchorTime.ArrivalTime ->
                                                        stringResource(
                                                            Res.string.navigate_chip_anchor_arrive,
                                                            anchorTime.time.format()
                                                        )
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (showAccessibilityButtons) {
                                item {
                                    val bikesAllowed by viewModel.bikesAllowed.collectAsStateWithLifecycle()
                                    Chip(
                                        selected = bikesAllowed ?: false,
                                        onToggle = { viewModel.bikesAllowed.value = it },
                                        contentDescription = stringResource(Res.string.navigate_chip_bikes_allowed)
                                    ) {
                                        BikeIcon()
                                    }
                                }
                                item {
                                    val wheelchairAccessibility by viewModel.wheelchairAccessible.collectAsStateWithLifecycle()
                                    Chip(
                                        selected = wheelchairAccessibility ?: false,
                                        onToggle = { viewModel.wheelchairAccessible.value = it },
                                        contentDescription = stringResource(Res.string.navigate_chip_wheelchair_accessible)
                                    ) {
                                        AccessibleIcon()
                                    }
                                }
                            }
                        }
                    }
                }

                when (state) {
                    is NavigationEntryState.MissingWaypoints -> {
                        item {
                            Column {
                                Spacer(Modifier.height(2.rdp))
                                ListHint(
                                    when {
                                        state.origin && state.destination -> stringResource(Res.string.navigate_entry_no_starting_point_destination)
                                        state.origin -> stringResource(Res.string.navigate_entry_no_starting_point)
                                        else -> stringResource(Res.string.navigate_entry_no_destination)
                                    }
                                ) {
                                    JourneyIcon(tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                    is NavigationEntryState.JourneySelection -> JourneySelection(
                        viewModel,
                        state.state
                    )
                    is NavigationEntryState.JourneySelected -> item {
                        DisplayJourney(state.journey)
                    }
                    else -> {}
                }
            }
        }
    }

    fun LazyListScope.JourneySelection(
        viewModel: NavigationEntryViewModel,
        state: NavigationState
    ) {
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
                            state.exception,
                            retry = { viewModel.retryLoadingGraph() }
                        )
                    }
                }
            }
            is NavigationState.JourneyStartStopSame -> {
                item {
                    Box(
                        Modifier.padding(1.rdp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ListHint(
                            stringResource(Res.string.navigate_start_stop_same),
                            icon = { MapIcon(tint = MaterialTheme.colorScheme.primary) }
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
            is NavigationState.JourneysFound -> {
                items(state.journeys.size) {
                    val journey = state.journeys[it]
                    JourneyOptionCard(
                        journey,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.selectJourney(journey)
                        }
                    )
                    if (it != state.journeys.lastIndex) {
                        HorizontalDivider(Modifier.padding(start = 1.rdp, end = iconInset))
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
        val navigator = LocalNavigator.currentOrThrow
        val destination by viewModel.destination.collectAsStateWithLifecycle()
        val origin by viewModel.origin.collectAsStateWithLifecycle()
        val anchorTime by viewModel.anchorTime.collectAsStateWithLifecycle()

        val departureTime = when(anchorTime) {
            !is NavigationAnchorTime.Now -> firstLeg.departureTime
            else -> null
        }

        Column(Modifier.fillMaxWidth()) {
            when (firstLeg) {
                is JourneyLeg.Transfer -> {
                    DepartureLeg(
                        firstLeg.stops.first().name,
                        departureTime,
                        onClick = { navigator.stopCardDefaultNavigation(firstLeg.stops.first()) }
                    )
                    HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                }
                is JourneyLeg.TransferPoint -> {
                    DepartureLeg(
                        origin.name,
                        departureTime,
                        onClick = origin.let { when (it) {
                            is NavigationLocation.Stop -> { {
                                navigator.stopCardDefaultNavigation(it.stop)
                            } }
                            is NavigationLocation.Place -> { {
                                navigator.placeCardDefaultNavigation(it.place)
                            } }
                            else -> null
                        } }
                    )
                    HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                }
                else -> {}
            }

            val showAccessibilityIcons =
                viewModel.showAccessibilityIcons.collectAsStateWithLifecycle().value &&
                        !FeatureFlag.GLOBAL_HIDE_TRANSPORT_ACCESSIBILITY.value()
            for (legI in journey.legs.indices) {
                val leg = journey.legs[legI]
                when (leg) {
                    is JourneyLeg.Transfer -> TransferLeg(leg)
                    is JourneyLeg.Travel -> TravelLeg(
                        leg,
                        showAccessibilityIcons
                    )
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
                    ArrivalLeg(
                        lastLeg.stops.last().name,
                        lastLeg.arrivalTime,
                        onClick = { navigator.stopCardDefaultNavigation(lastLeg.stops.last()) }
                    )
                }
                is JourneyLeg.TransferPoint -> {
                    HorizontalDivider(Modifier.padding(start = journeyIconInset, end = 1.rdp))
                    ArrivalLeg(
                        destination.name,
                        lastLeg.arrivalTime,
                        onClick = origin.let { when (it) {
                            is NavigationLocation.Stop -> { {
                                navigator.stopCardDefaultNavigation(it.stop)
                            } }
                            is NavigationLocation.Place -> { {
                                navigator.placeCardDefaultNavigation(it.place)
                            } }
                            else -> null
                        } }
                    )
                }
                else -> {}
            }
        }
    }

    @Composable
    fun SelectTimeDialog() {
        val viewModel = koinScreenModel<NavigationEntryViewModel>()
        val timeDialogVisible by viewModel.timeDialogVisible.collectAsStateWithLifecycle()
        if (!timeDialogVisible) return

        val anchorTime by viewModel.anchorTime.collectAsStateWithLifecycle()
        TimeSelectionDialog(
            anchorTime,
            onTimeSelected = { viewModel.anchorTime.value = it },
            onDismissRequest = { viewModel.timeDialogVisible.value = false }
        )
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
fun TravelLeg(
    leg: JourneyLeg.Travel,
    showAccessibilityIcons: Boolean = true
) {
    val navigator = LocalNavigator.currentOrThrow
    LegScaffold({ RouteRandle(leg.route) }) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.75.rdp)
        ) {
            Markdown(
                stringResource(Res.string.navigate_travel_depart, leg.stops.first().name, leg.departureTime.format()),
                modifier = Modifier.noRippleClickable { navigator.stopCardDefaultNavigation(leg.stops.first()) }
            )
            Markdown(
                stringResource(Res.string.navigate_travel, leg.travelTime.text, leg.route.name, leg.heading),
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { navigator.routeCardDefaultNavigation(leg.route) }
            )
            if (
                (leg.routeAccessibility?.wheelchairAccessible == ServiceWheelchairAccessible.ACCESSIBLE ||
                leg.routeAccessibility?.bikesAllowed == ServiceBikesAllowed.ALLOWED) &&
                showAccessibilityIcons
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(0.5.rdp)) {
                    if (leg.routeAccessibility?.wheelchairAccessible == ServiceWheelchairAccessible.ACCESSIBLE) {
                        WheelchairAccessibleIcon(
                            true,
                            contentDescription = stringResource(Res.string.route_accessibility_wheelchair_accessible)
                        )
                    }
                    if (leg.routeAccessibility?.bikesAllowed == ServiceBikesAllowed.ALLOWED) {
                        BikeIcon(
                            contentDescription = stringResource(Res.string.route_accessibility_bikes_allowed)
                        )
                    }
                }
            }
            Markdown(
                stringResource(Res.string.navigate_travel_arrive, leg.stops.last().name, leg.arrivalTime.format()),
                modifier = Modifier.noRippleClickable { navigator.stopCardDefaultNavigation(leg.stops.last()) }
            )
        }
    }
}

@Composable
fun DepartureLeg(
    pointName: String,
    departureTime: Time?,
    onClick: (() -> Unit)? = null
) {
    LegScaffold({ JourneyStartIcon() }) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.75.rdp)
        ) {
            Markdown(
                when (departureTime) {
                    null -> stringResource(Res.string.navigate_travel_journey_depart, pointName)
                    else -> stringResource(Res.string.navigate_travel_depart, pointName, departureTime.format())
                },
                modifier = Modifier.then(
                    when (onClick) {
                        null -> Modifier
                        else -> Modifier.noRippleClickable { onClick() }
                    }
                )
            )
        }
    }
}

@Composable
fun ArrivalLeg(
    pointName: String,
    arrivalTime: Time,
    onClick: (() -> Unit)? = null
) {
    LegScaffold({ GenericMarkerIcon() }) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.75.rdp),
        ) {
            Markdown(
                stringResource(Res.string.navigate_travel_journey_arrive, pointName, arrivalTime.format()),
                modifier = Modifier.then(
                    when (onClick) {
                        null -> Modifier
                        else -> Modifier.noRippleClickable { onClick() }
                    }
                )
            )
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
