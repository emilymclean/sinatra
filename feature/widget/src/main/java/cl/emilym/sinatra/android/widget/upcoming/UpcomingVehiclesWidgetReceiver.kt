package cl.emilym.sinatra.android.widget.upcoming

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.core.component.KoinComponent

class UpcomingVehiclesWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UpcomingVehiclesWidget()
    private val helper by lazy { KoinUpcomingVehiclesWidgetReceiverHelper() }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
    }
}

class KoinUpcomingVehiclesWidgetReceiverHelper: KoinComponent {

}