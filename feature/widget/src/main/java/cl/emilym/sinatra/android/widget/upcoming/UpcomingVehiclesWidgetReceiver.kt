package cl.emilym.sinatra.android.widget.upcoming

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import cl.emilym.sinatra.android.widget.base.KoinGlanceAppWidgetReceiver
import cl.emilym.sinatra.android.widget.base.KoinGlanceAppWidgetReceiverComponent
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleData
import cl.emilym.sinatra.android.widget.data.toProto
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class UpcomingVehiclesWidgetReceiver: KoinGlanceAppWidgetReceiver() {
    override val component by lazy { KoinUpcomingVehiclesWidgetReceiverHelper() }
}

class KoinUpcomingVehiclesWidgetReceiverHelper: KoinGlanceAppWidgetReceiverComponent() {

    override val glanceAppWidget: GlanceAppWidget = UpcomingVehiclesWidget()
    private val upcomingRoutesForStopUseCase by inject<UpcomingRoutesForStopUseCase>()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (id in appWidgetIds) {
            update(context, id)
        }
    }

    private fun update(context: Context, appWidgetId: Int) {
        MainScope().launch {
            val id = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            val upcoming = upcomingRoutesForStopUseCase(
                "8126",
                "ACTO001",
                "Alinga St"
            ).first()

            updateAppWidgetState(context, UpcomingVehicleWidgetState, id) { pref ->
                UpcomingVehicleData.newBuilder()
                    .setHasUpcoming(upcoming.item.isNotEmpty())
                    .addAllTimes(upcoming.item.map { it.toProto() })
                    .build()
            }
            glanceAppWidget.update(context, id)
        }
    }

}