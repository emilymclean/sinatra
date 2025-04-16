package cl.emilym.sinatra.android.widget.base

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

abstract class KoinGlanceAppWidgetReceiver: GlanceAppWidgetReceiver() {

    final override val glanceAppWidget get() = component.glanceAppWidget
    abstract val component: KoinGlanceAppWidgetReceiverComponent

    final override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        component.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    final override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        component.onDeleted(context, appWidgetIds)
    }

    final override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        component.onReceive(context, intent)
    }

    final override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        component.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}

abstract class KoinGlanceAppWidgetReceiverComponent: KoinComponent {

    abstract val glanceAppWidget: GlanceAppWidget
    open val coroutineScope = MainScope()

    @CallSuper
    open fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {}

    @CallSuper
    open fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {}

    @CallSuper
    open fun onDeleted(context: Context, appWidgetIds: IntArray) {}

    @CallSuper
    open fun onReceive(context: Context, intent: Intent) {}

}