package cl.emilym.sinatra.android.widget.upcoming

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text

class UpcomingVehiclesWidget: GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Scaffold {
                Text("Test")
            }
        }
    }
}