package cl.emilym.sinatra.ui.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.StopId
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.route_code_name
import sinatra.ui.generated.resources.route_with_heading
import sinatra.ui.generated.resources.stop_detail_from
import sinatra.ui.generated.resources.stop_detail_platform
import sinatra.ui.generated.resources.stop_detail_platform_lr
import kotlin.math.max

@Composable
fun UpcomingRouteCard(
    timetableTime: IStopTimetableTime,
    stopStationTime: StopStationTime?,
    modifier: Modifier = Modifier,
    short: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    ListCard(
        {
            timetableTime.route?.let { RouteRandle(it) }
        },
        modifier,
        onClick
    ) {
        Text(
            stringResource(
                Res.string.route_with_heading,
                when {
                    short -> stringResource(
                        Res.string.route_code_name,
                        timetableTime.routeCode
                    )
                    else -> timetableTime.route?.name ?: stringResource(
                        Res.string.route_code_name,
                        timetableTime.routeCode
                    )
                }, timetableTime.heading
            )
        )
        stopStationTime?.let {
            Text(
                when(timetableTime.childStop) {
                    null -> it.text
                    else -> stringResource(
                        Res.string.stop_detail_from,
                        it.text,
                            timetableTime.childStop?.name?.platformName(
                                timetableTime.childStopId ?: ""
                            ) ?: "null"
                        )
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun String.platformName(
    stopId: StopId
): String {
    val lc = toLowerCase(Locale("en-AU"))
    val idx = listOf(lc.indexOf("platform"), lc.indexOf("plt")).max()
    if (idx < 0) return this

    val platformNumber = drop(idx).dropWhile { !it.isDigit() }.takeWhile { it.isDigit() }

    return when {
        listOf("8120", "8121", "8129").contains(stopId) ->
            stringResource(
                Res.string.stop_detail_platform_lr,
                platformNumber
            )
        else -> stringResource(
                Res.string.stop_detail_platform,
                platformNumber
            )
    }
}