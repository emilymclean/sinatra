package cl.emilym.sinatra.android.widget.base

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.material3.ColorProviders
import cl.emilym.sinatra.ui.presentation.theme.DarkColorScheme
import cl.emilym.sinatra.ui.presentation.theme.LightColorScheme

abstract class SinatraGlanceAppWidget: GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme(
                colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    GlanceTheme.colors
                } else {
                    ColorProviders(
                        light = LightColorScheme,
                        dark = DarkColorScheme
                    )
                }
            ) {
                Content()
            }
        }
    }

    @Composable
    protected abstract fun Content()

}