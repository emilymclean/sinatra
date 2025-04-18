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
import cl.emilym.sinatra.android.widget.data.repository.UpcomingVehiclesWidgetRepository
import cl.emilym.sinatra.android.widget.data.toProto
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
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
    private val upcomingVehiclesWidgetRepository by inject<UpcomingVehiclesWidgetRepository>()

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

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                for (appWidgetId in appWidgetIds) {
                    try {
                        upcomingVehiclesWidgetRepository.delete(appWidgetId)
                    } catch (e: Exception) {
                        Napier.e(e)
                    }
                }
            }
        }
    }

    private suspend fun upcoming(appWidgetId: Int): UpcomingVehicleState {
        val out = UpcomingVehicleState.newBuilder()

        Napier.d("Updating widget $appWidgetId")
        try {
            val configuration = withContext(Dispatchers.IO) {
                upcomingVehiclesWidgetRepository.get(appWidgetId)
            } ?: return out.setIsConfigured(false).build()

            out.setIsConfigured(true)

            Napier.d("Fetching updates for config $configuration")

            val upcoming = withContext(Dispatchers.IO) {
                upcomingRoutesForStopUseCase(
                    configuration.stopId,
                    configuration.routeId,
                    configuration.heading,
                    number = 2,
                    forceNotLive = true
                ).first()
            }

            out.setHasUpcoming(upcoming.item.isNotEmpty())
                .setType(when {
                    configuration.heading != null -> UpcomingType.UPCOMING_TYPE_STOP_ROUTE_HEADING
                    configuration.routeId != null -> UpcomingType.UPCOMING_TYPE_STOP_ROUTE
                    else -> UpcomingType.UPCOMING_TYPE_STOP
                })
                .addAllTimes(upcoming.item.map { it.toProto() })

            Napier.d("Widget $appWidgetId has content = ${out.hasUpcoming}")

            return out.build()
        } catch (e: Exception) {
            Napier.e(e)
            return out
                .setErrorMessage(e.message)
                .build()
        }
    }

    private fun update(context: Context, appWidgetId: Int) {
        coroutineScope.launch {
            val id = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            val upcoming = upcoming(appWidgetId)

            updateAppWidgetState(context, UpcomingVehicleWidgetState, id) { prefs ->
                upcoming
            }
            glanceAppWidget.update(context, id)

            if (upcoming.timesList.isNotEmpty()) {
                val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                val intent = PendingIntent.getBroadcast(
                    context,
                    UPDATE_REQUEST_CODE,
                    context.updateIntent(appWidgetId),
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

fun Context.updateIntent(appWidgetId: Int): Intent = Intent(this, UpcomingVehiclesWidgetReceiver::class.java).apply {
    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
}