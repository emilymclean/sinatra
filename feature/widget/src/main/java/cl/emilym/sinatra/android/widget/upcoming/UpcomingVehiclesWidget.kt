package cl.emilym.sinatra.android.widget.upcoming

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.android.widget.base.SinatraGlanceAppWidget
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleData

class UpcomingVehiclesWidget: SinatraGlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = UpcomingVehicleWidgetState

    @Composable
    override fun Content() {
        val state = currentState<UpcomingVehicleData>()

        val hasUpcoming = state.hasUpcoming
        Scaffold(horizontalPadding = 0.dp) {
            Box(GlanceModifier.padding(1.rdp)) {
                Text(
                    when(hasUpcoming) {
                        true -> "Has upcoming vehicles"
                        false -> "No upcoming vehicles"
                        else -> "Loading..."
                    },
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }
        }
    }
}