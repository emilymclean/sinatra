package cl.emilym.sinatra.android.widget.upcoming

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.android.widget.R
import cl.emilym.sinatra.android.widget.base.SinatraGlanceAppWidget
import cl.emilym.sinatra.android.widget.base.widgetFormat
import cl.emilym.sinatra.android.widget.data.proto.UpcomingType
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleState
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleStopTime
import cl.emilym.sinatra.nullIfBlank
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class UpcomingVehiclesWidget: SinatraGlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = UpcomingVehicleWidgetState

    @Composable
    override fun Content() {
        val state = currentState<UpcomingVehicleState>()

        Scaffold(
            horizontalPadding = 0.dp
        ) {
            Box(
                GlanceModifier.padding(1.rdp).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !state.isConfigured -> {
                        SinatraText(
                            LocalContext.current.getString(
                                R.string.upcoming_vehicle_widget_needs_config,
                            )
                        )
                    }
                    state.errorMessage.isNotBlank() -> {
                        val context = LocalContext.current
                        val glanceId = LocalGlanceId.current
                        Column {
                            SinatraText(
                                LocalContext.current.getString(
                                    R.string.upcoming_vehicle_widget_something_went_wrong,
                                ),
                                bold = true
                            )
                            SinatraText(
                                state.errorMessage ?: "",
                                maxLines = 2
                            )
                            Spacer(GlanceModifier.height(8.dp))
                            Button(
                                LocalContext.current.getString(
                                    R.string.upcoming_vehicle_widget_retry,
                                ),
                                {
                                    MainScope().launch { update(context, glanceId) }
                                },
                                modifier = GlanceModifier.fillMaxWidth()
                            )
                        }
                    }
                    !state.hasUpcoming -> {
                        SinatraText(
                            LocalContext.current.getString(
                                R.string.upcoming_vehicle_widget_no_upcoming,
                            )
                        )
                    }
                    else -> state.timesList.firstOrNull()?.let {
                        UpcomingStopWidget(
                            it,
                            state.type
                        )
                    }
                }
            }
        }
    }
}

@Composable
@GlanceComposable
fun UpcomingStopWidget(
    upcoming: UpcomingVehicleStopTime,
    type: UpcomingType,
    modifier: GlanceModifier = GlanceModifier
) {
    Column(modifier) {
        SinatraText(
            when(type) {
                UpcomingType.UPCOMING_TYPE_STOP_ROUTE_HEADING -> LocalContext.current.getString(
                    R.string.upcoming_vehicle_widget_heading,
                    upcoming.heading
                )
                else -> LocalContext.current.getString(
                    R.string.upcoming_vehicle_widget_route_and_heading,
                    upcoming.routeCode,
                    upcoming.heading
                )
            }
        )
        val time = Instant.fromEpochMilliseconds(upcoming.departureTime).widgetFormat()
        SinatraText(
            time,
            big = true
        )
    }
}

@Composable
@GlanceComposable
fun SinatraText(
    content: String,
    big: Boolean = false,
    bold: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        content,
        style = TextStyle(
            color = GlanceTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = if (bold) FontWeight.Bold else null,
            fontSize = if (big) 24.sp else null
        ),
        modifier = GlanceModifier.fillMaxWidth(),
        maxLines = maxLines
    )
}