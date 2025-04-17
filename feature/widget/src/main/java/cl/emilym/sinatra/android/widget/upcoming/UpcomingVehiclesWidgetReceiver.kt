package cl.emilym.sinatra.android.widget.upcoming

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import cl.emilym.sinatra.android.widget.base.KoinGlanceAppWidgetReceiver
import cl.emilym.sinatra.android.widget.base.KoinGlanceAppWidgetReceiverComponent
import cl.emilym.sinatra.android.widget.data.proto.UpcomingType
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleState
import cl.emilym.sinatra.android.widget.data.toProto
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
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

    private suspend fun upcoming(): UpcomingVehicleState {
        val upcoming = withContext(Dispatchers.IO) {
            upcomingRoutesForStopUseCase(
                "8126",
                "ACTO001",
                "Alinga St",
                number = 2,
                forceNotLive = true
            ).first()
        }

        return UpcomingVehicleState.newBuilder()
            .setHasUpcoming(upcoming.item.isNotEmpty())
            .setType(UpcomingType.UPCOMING_TYPE_STOP_ROUTE_HEADING)
            .addAllTimes(upcoming.item.map { it.toProto() })
            .build()
    }

    private fun update(context: Context, appWidgetId: Int) {
        coroutineScope.launch {
            val id = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            val upcoming = try {
                upcoming()
            } catch (e: Exception) {
                UpcomingVehicleState.newBuilder()
                    .setErrorMessage(e.message)
                    .build()
            }

            updateAppWidgetState(context, UpcomingVehicleWidgetState, id) { prefs ->
                upcoming
            }
            glanceAppWidget.update(context, id)

            if (upcoming.timesList.isNotEmpty()) {
                val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                val intent = PendingIntent.getBroadcast(
                    context,
                    UPDATE_REQUEST_CODE,
                    Intent(context, UpcomingVehiclesWidgetReceiver::class.java).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val time = upcoming.timesList.first().departureTime

                if (Build.VERSION.SDK_INT < 31) {
                    alarmManager.setExact(
                        AlarmManager.RTC,
                        time,
                        intent
                    )
                } else {
                    alarmManager.setWindow(
                        AlarmManager.RTC,
                        time,
                        60000L,
                        intent
                    )
                }
            }
        }
    }

}