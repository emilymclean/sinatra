package cl.emilym.sinatra.ui.presentation.screens.maps.route

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.bounds
import cl.emilym.sinatra.data.models.IRouteTripInformation
import cl.emilym.sinatra.data.models.IRouteTripStop
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.nullIf
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.asInstants
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.current
import cl.emilym.sinatra.ui.localization.LocalClock
import cl.emilym.sinatra.ui.localization.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.highlightedRouteStopMarkerIcon
import cl.emilym.sinatra.ui.maps.routeStopMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.past
import cl.emilym.sinatra.ui.presentation.screens.maps.stop.StopDetailScreen
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.AccessibilityIconLockup
import cl.emilym.sinatra.ui.widgets.AlertScaffold
import cl.emilym.sinatra.ui.widgets.BikeIcon
import cl.emilym.sinatra.ui.widgets.ExternalLinkIcon
import cl.emilym.sinatra.ui.widgets.FavouriteButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.NoBusIcon
import cl.emilym.sinatra.ui.widgets.RouteLine
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.SegmentedButtonHeight
import cl.emilym.sinatra.ui.widgets.SheetIosBackButton
import cl.emilym.sinatra.ui.widgets.SpecificRecomposeOnInstants
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.WarningIcon
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.pick
import cl.emilym.sinatra.ui.widgets.value
import com.mikepenz.markdown.m3.Markdown
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.accessibility_title
import sinatra.ui.generated.resources.current_stops_title
import sinatra.ui.generated.resources.past_stops_title
import sinatra.ui.generated.resources.route_accessibility_bikes_allowed
import sinatra.ui.generated.resources.route_accessibility_no_bikes_allowed
import sinatra.ui.generated.resources.route_accessibility_not_wheelchair_accessible
import sinatra.ui.generated.resources.route_accessibility_wheelchair_accessible
import sinatra.ui.generated.resources.route_heading
import sinatra.ui.generated.resources.route_not_operating
import sinatra.ui.generated.resources.route_see_more
import sinatra.ui.generated.resources.semantics_favourite_route
import sinatra.ui.generated.resources.stop_detail_distance
import sinatra.ui.generated.resources.stop_detail_nearest_stop
import sinatra.ui.generated.resources.stops_timing_approximate
import sinatra.ui.generated.resources.stops_timing_approximate_title
import sinatra.ui.generated.resources.stops_title
import sinatra.ui.generated.resources.trip_not_found

val zoomPadding
    @Composable
    get() = 2.rdp

class RouteDetailScreen(
    private val routeId: RouteId,
    private val serviceId: ServiceId? = null,
    private val tripId: TripId? = null,
    private val stopId: StopId? = null,
    startOfDay: Instant? = null
): MapScreen {
    private val _startOfDay: Long? = startOfDay?.toEpochMilliseconds()
    private val startOfDay: Instant?
        get() = _startOfDay?.let { Instant.fromEpochMilliseconds(it) }

    override val key: ScreenKey = "${this::class.qualifiedName!!}/$routeId/$serviceId/$tripId/$stopId/$_startOfDay"

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalVoyagerApi::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<RouteDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState?.bottomSheetState?.halfExpand()
        }

        LifecycleEffectOnce {
            viewModel.init(routeId, serviceId, tripId, startOfDay)
        }

        if (FeatureFlag.ROUTE_DETAIL_NEAREST_STOP.value()) {
            val currentLocation = currentLocation()
            LaunchedEffect(currentLocation) {
                viewModel.updateLocation(currentLocation ?: return@LaunchedEffect)
            }
        }

        val route by viewModel.route.collectAsStateWithLifecycle()
        val tripInformation by viewModel.tripInformation.collectAsStateWithLifecycle()
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RequestStateWidget(route, { viewModel.retry() }) { route ->
                // Jank as hell but good enough
                RequestStateWidget(tripInformation, { viewModel.retry() }) { tripInformation ->
                    when {
                        route == null -> { Text(stringResource(Res.string.trip_not_found)) }
                        else -> {
                            val info = tripInformation
                            val triggers = info?.stationTimes?.asInstants()
                            when {
                                triggers != null -> {
                                    SpecificRecomposeOnInstants(triggers) { trigger ->
                                        TripDetails(route, info, trigger)
                                    }
                                }
                                else -> {
                                    TripDetails(route, info, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TripDetails(route: Route, info: IRouteTripInformation?, trigger: Int?) {
        val viewModel = koinScreenModel<RouteDetailViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val clock = LocalClock.current
        val timeZone = LocalScheduleTimeZone.current
        val mapControl = LocalMapControl.current

        val nearestStop by viewModel.nearestStop.collectAsStateWithLifecycle()
        val alerts by viewModel.alerts.collectAsStateWithLifecycle()
        val selectedHeading by viewModel.selectedHeading.collectAsStateWithLifecycle()
        val heading by viewModel.heading.collectAsStateWithLifecycle()
        val headings by viewModel.headings.collectAsStateWithLifecycle()
        val isToday by viewModel.isToday.collectAsStateWithLifecycle()
        val showAccessibility =
            viewModel.showAccessibility.collectAsStateWithLifecycle().value &&
            !FeatureFlag.GLOBAL_HIDE_TRANSPORT_ACCESSIBILITY.value()


        val current = if (trigger != null) {
            remember(trigger) {
                val now = clock.now()
                info?.stops?.current(now, startOfDay ?: now.startOfDay(timeZone)).nullIfEmpty()
            }
        } else {
            info?.stops
        }
        val past = if (trigger != null) {
            remember(trigger) {
                val now = clock.now()
                info?.stops?.past(now, startOfDay ?: now.startOfDay(timeZone))?.nullIfEmpty()?.reversed()
            }
        } else {
            null
        }

        val zoomPadding = zoomPadding
        LaunchedEffect(info?.stops) {
            if (FeatureFlag.ROUTE_DETAIL_PREVENT_ZOOM_WHEN_HAVE_SOURCE_STOP.immediate && stopId != null)
                return@LaunchedEffect
            info?.stops?.let {
                mapControl.zoomToArea(info.stops.mapNotNull { it.stop?.location }.bounds(), zoomPadding)
            }
        }

        Scaffold { innerPadding ->
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                item { Box(Modifier.height(1.rdp)) }
                item {
                    Row(
                        Modifier.padding(horizontal = 1.rdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(1.rdp)
                    ) {
                        SheetIosBackButton()
                        RouteRandle(route)
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Text(
                                route.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            heading?.let {
                                Text(
                                    stringResource(Res.string.route_heading, it),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        val favourited by viewModel.favourited.collectAsStateWithLifecycle()
                        val favouriteContentDescription = stringResource(Res.string.semantics_favourite_route)
                        FavouriteButton(
                            favourited,
                            { viewModel.favourite(routeId, it) },
                            Modifier.semantics {
                                contentDescription = favouriteContentDescription
                                selected = favourited
                            }
                        )
                    }
                }
                item { Box(Modifier.height(0.5.rdp)) }
                info?.stops?.nullIfEmpty()?.let {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            RouteLine(
                                route,
                                info.stops.mapNotNull { it.stop },
                                info.stops.mapNotNull { it.stationTime }.nullIfEmpty()
                            )
                        }
                    }
                    item { Box(Modifier.height(2.rdp)) }
                }
                item {
                    AlertScaffold((alerts as? RequestState.Success)?.value)
                }
                if (info == null) {
                    item { Box(Modifier.height(2.rdp)) }
                    item {
                        ListHint(
                            stringResource(Res.string.route_not_operating),
                            modifier = Modifier.padding(horizontal = 1.rdp)
                        ) {
                            NoBusIcon(
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    item { Box(Modifier.height(1.rdp)) }
                }
                if (!isToday) {
                    item {
                        WarningCard(
                            stringResource(Res.string.route_not_operating),
                            null,
                            Modifier.fillMaxWidth().padding(horizontal = 1.rdp)
                        )
                    }
                    item { Box(Modifier.height(1.rdp)) }
                }
                if (route.approximateTimings && isToday) {
                    item {
                        WarningCard(
                            stringResource(Res.string.stops_timing_approximate_title),
                            stringResource(Res.string.stops_timing_approximate),
                            Modifier.fillMaxWidth().padding(horizontal = 1.rdp)
                        )
                    }
                    item { Box(Modifier.height(1.rdp)) }
                }
                if (route.description != null && trigger == null) {
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(horizontal = 1.rdp),
                            verticalArrangement = Arrangement.spacedBy(1.rdp)
                        ) {
                            Markdown(route.description ?: "")
                        }
                    }
                    item { Box(Modifier.height(
                        if (route.moreLink == null) 2.rdp else 1.rdp
                    )) }
                }
                if (route.moreLink != null && trigger == null) {
                    item {
                        val uriHandler = LocalUriHandler.current
                        Button(
                            onClick = { uriHandler.openUri(route.moreLink ?: "") },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 1.rdp)
                        ) {
                            Text(stringResource(Res.string.route_see_more))
                            Box(Modifier.width(0.5.rdp))
                            ExternalLinkIcon()
                        }
                    }
                    item { Box(Modifier.height(2.rdp)) }
                }
                info?.let { info ->
                    if (showAccessibility) {
                        item {
                            Subheading(stringResource(Res.string.accessibility_title))
                        }
                        item { Box(Modifier.height(1.rdp)) }
                        item {
                            Column(Modifier.padding(horizontal = 1.rdp)) {
                                if (info.accessibility.wheelchairAccessible != ServiceWheelchairAccessible.UNKNOWN) {
                                    AccessibilityIconLockup(
                                        {
                                            WheelchairAccessibleIcon(info.accessibility.wheelchairAccessible == ServiceWheelchairAccessible.ACCESSIBLE)
                                        }
                                    ) {
                                        Text(when(info.accessibility.wheelchairAccessible == ServiceWheelchairAccessible.ACCESSIBLE) {
                                            true -> stringResource(Res.string.route_accessibility_wheelchair_accessible)
                                            false -> stringResource(Res.string.route_accessibility_not_wheelchair_accessible)
                                        })
                                    }
                                }
                                if (info.accessibility.bikesAllowed != ServiceBikesAllowed.UNKNOWN) {
                                    AccessibilityIconLockup(
                                        { BikeIcon() }
                                    ) {
                                        Text(when(info.accessibility.bikesAllowed == ServiceBikesAllowed.ALLOWED) {
                                            true -> stringResource(Res.string.route_accessibility_bikes_allowed)
                                            false -> stringResource(Res.string.route_accessibility_no_bikes_allowed)
                                        })
                                    }
                                }
                            }
                        }
                        item { Box(Modifier.height(2.rdp)) }
                    }
                }
                headings?.nullIf { it.size <= 1 }?.let { headings ->
                    item {
                        val textMeasurer = rememberTextMeasurer()
                        BoxWithConstraints(
                            Modifier.fillMaxWidth()
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 1.rdp),
                            ) {
                                SingleChoiceSegmentedButtonRow(
                                    Modifier.widthIn(min = this@BoxWithConstraints.maxWidth - 2.rdp)
                                ) {
                                    headings.forEachIndexed { i, heading ->
                                        val style = MaterialTheme.typography.labelLarge
                                        val widthMeasurement = remember(heading, style) {
                                            textMeasurer
                                                .measure(
                                                    heading,
                                                    style = style
                                                )
                                                .size.width
                                        }
                                        val width = with(LocalDensity.current) { widthMeasurement.toDp() }
                                        SegmentedButton(
                                            heading == selectedHeading || (i == 0 && selectedHeading == null),
                                            onClick = {
                                                viewModel.selectHeading(heading)
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = i,
                                                count = headings.size
                                            ),
                                            icon = {},
                                            label = {
                                                Text(heading, softWrap = false)
                                            },
                                            // They have to all be fixed to the same height otherwise one may be larger than the others
                                            modifier = Modifier
                                                .height(SegmentedButtonHeight)
                                                .widthIn(min = width + 2.rdp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item { Box(Modifier.height(2.rdp)) }
                }
                nearestStop?.let { nearestStop ->
                    item {
                        if (!FeatureFlag.ROUTE_DETAIL_NEAREST_STOP.value()) return@item
                        Column {
                            Subheading(stringResource(Res.string.stop_detail_nearest_stop))
                            StopCard(
                                nearestStop.stop,
                                Modifier.fillMaxWidth(),
                                onClick = {
                                    navigator.push(
                                        StopDetailScreen(
                                            nearestStop.stop.id
                                        )
                                    )
                                },
                                subtitle = stringResource(Res.string.stop_detail_distance, nearestStop.distance.text)
                            )
                            Box(Modifier.height(1.rdp))
                        }
                    }
                }
                info?.stops?.nullIfEmpty()?.let {
                    when {
                        trigger == null -> {
                            item { Subheading(stringResource(Res.string.stops_title)) }
                            Cards(navigator, current ?: listOf(), route)
                        }
                        else -> {
                            if (current != null) {
                                item {
                                    Subheading(stringResource(Res.string.current_stops_title))
                                }
                                Cards(navigator, current ?: listOf(), route)
                            }
                            if (past != null) {
                                item {
                                    Subheading(stringResource(Res.string.past_stops_title))
                                }
                                Cards(navigator, past ?: listOf(), route)
                            }
                        }
                    }
                }
                item { Box(Modifier.height(2.rdp)) }
            }
        }
    }

    private fun LazyListScope.Cards(
        navigator: Navigator,
        stops: List<IRouteTripStop>,
        route: Route
    ) {
        items(stops) {
            if (it.stop == null) return@items
            StopCard(
                it.stop!!,
                Modifier.fillMaxWidth(),
                it.stationTime?.pick(route, it.sequence <= 1),
                onClick = {
                    navigator.push(StopDetailScreen(it.stopId))
                }
            )
        }
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<RouteDetailViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val info = viewModel.tripInformation.collectAsStateWithLifecycle().value.unwrap() ?: return emptyList()
        val route = viewModel.route.collectAsStateWithLifecycle().value.unwrap() ?: return emptyList()

        val icon = routeStopMarkerIcon(route)
        val stops = info.stops
        if (stops.all { it.stop == null }) return listOf()

        return listOf(
            LineItem(
                stops.mapNotNull { it.stop?.location },
                route.color()
            )
        ) + stops.mapNotNull {
            MarkerItem(
                it.stop?.location ?: return@mapNotNull null,
                when (it.stopId == stopId && FeatureFlag.ROUTE_DETAIL_HIGHLIGHT_SOURCE_STOP.value()) {
                    true -> highlightedRouteStopMarkerIcon(route, it.stop)
                    false -> icon
                },
                id = "routeDetail-${it.stopId}",
                onClick = when (FeatureFlag.ROUTE_DETAIL_CLICKABLE_STOPS.value()) {
                    true -> { { navigator.push(StopDetailScreen(it.stopId)) } }
                    false -> null
                }
            )
        }
    }

}

@Composable
fun WarningCard(
    title: String,
    description: String?,
    modifier: Modifier = Modifier
) {
    Card(
        Modifier.then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(1.rdp),
            horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
        ) {
            WarningIcon()
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium
                )
                description?.let { Text(description) }
            }
        }
    }
}