package cl.emilym.sinatra.android.widget.presentation.upcoming

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.text.Text
import cl.emilym.sinatra.android.widget.base.SinatraGlanceAppWidget

class UpcomingVehiclesWidget: SinatraGlanceAppWidget() {

    @Composable
    override fun Content() {
        val prefs = currentState<Preferences>()

        Scaffold {
            Text("Test")
        }
    }
}