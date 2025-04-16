package cl.emilym.sinatra.android.widget.upcoming

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import cl.emilym.sinatra.android.widget.base.KoinGlanceAppWidgetReceiver
import cl.emilym.sinatra.android.widget.base.KoinGlanceAppWidgetReceiverComponent
import cl.emilym.sinatra.android.widget.data.proto.UpcomingType
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleData
import cl.emilym.sinatra.android.widget.data.toProto
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.inject

class UpcomingVehiclesWidgetReceiver: KoinGlanceAppWidgetReceiver() {
    override val component by lazy { KoinUpcomingVehiclesWidgetReceiverHelper() }
}

class KoinUpcomingVehiclesWidgetReceiverHelper: KoinGlanceAppWidgetReceiverComponent() {

    companion object {
        const val UPDATE_REQUEST_CODE = 789
    }

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
        coroutineScope.launch {
            val id = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            val upcoming = withContext(Dispatchers.IO) {
                upcomingRoutesForStopUseCase(
                    "8126",
                    "ACTO001",
                    "Alinga St",
                    forceNotLive = true
                ).first()
            }

            updateAppWidgetState(context, UpcomingVehicleWidgetState, id) { pref ->
                UpcomingVehicleData.newBuilder()
                    .setHasUpcoming(upcoming.item.isNotEmpty())
                    .setType(UpcomingType.UPCOMING_TYPE_STOP_ROUTE_HEADING)
                    .addAllTimes(upcoming.item.map { it.toProto() })
                    .build()
            }
            glanceAppWidget.update(context, id)

            if (upcoming.item.isNotEmpty()) {
                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                    .setWindow(
                        AlarmManager.RTC,
                        upcoming.item.first().departureTime
                            .instant
                            .toEpochMilliseconds(),
                        60000L,
                        PendingIntent.getBroadcast(
                            context,
                            UPDATE_REQUEST_CODE,
                            Intent(context, KoinGlanceAppWidgetReceiver::class.java).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                            },
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
            }
        }
    }

}