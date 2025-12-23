package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.px
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.asInstants
import cl.emilym.sinatra.ui.widgets.SpecificRecomposeOnInstants
import cl.emilym.sinatra.ui.widgets.StopStationTime
import cl.emilym.sinatra.ui.widgets.UpcomingRouteCard
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.currentLocation
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.browse_option_upcoming_routes

@Composable
fun NearbyDepartureItemComponentContent(
    component: NearbyDepartureItemComponent
) {
    val currentLocation = currentLocation()
    LaunchedEffect(currentLocation) {
        component.updateLocation(currentLocation ?: return@LaunchedEffect)
    }
    // Bug in LazyColumn means must always have content :/
    Box(Modifier.height(1.px))

    val stop = component.departures.collectAsStateWithLifecycle().value ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.rdp)
            .clickable { component.onStopClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column {
            Text(
                stringResource(Res.string.browse_option_upcoming_routes, stop.stop.name),
                modifier = Modifier.padding(1.rdp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Column {
                    val triggers = stop.departures.map { it.stationTime }.asInstants()
                    SpecificRecomposeOnInstants(triggers) { trigger ->
                        for (upcoming in stop.departures.take(2)) {
                            UpcomingRouteCard(
                                upcoming,
                                StopStationTime.Departure(upcoming.stationTime.departure),
                                short = true,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    component.onDepartureClick(upcoming)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}