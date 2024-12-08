package cl.emilym.sinatra.ui.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.scheduled_arrival
import sinatra.ui.generated.resources.estimated_arrival

@Composable
fun StopCard(
    stop: Stop,
    arrival: StationTime?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ListCard(
        {},
        modifier,
        onClick,
    ) {
        Text(stop.name)
        if (arrival != null) {
            val time = arrival.time.format()
            Text(
                when (arrival) {
                    is StationTime.Scheduled -> stringResource(Res.string.scheduled_arrival, time)
                    is StationTime.Live -> stringResource(Res.string.estimated_arrival, time)
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}