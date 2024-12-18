package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.ServiceAccessibility
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.estimated_arrival
import sinatra.ui.generated.resources.scheduled_arrival

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaceCard(
    place: Place,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ListCard(
        {},
        modifier,
        onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
        ) {
            Text(
                place.displayName,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}